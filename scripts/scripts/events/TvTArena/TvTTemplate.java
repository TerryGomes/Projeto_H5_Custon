package events.TvTArena;

import java.util.ArrayList;
import java.util.List;

import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.Announcements;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.Summon;
import l2mv.gameserver.model.Zone;
import l2mv.gameserver.model.base.TeamType;
import l2mv.gameserver.model.entity.Hero;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.Revive;
import l2mv.gameserver.network.serverpackets.SkillList;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.PositionUtils;

public abstract class TvTTemplate extends Functions
{
	private static int ITEM_ID = 4357;
	private static String ITEM_NAME = "Silver Shilen";
	private static int LENGTH_TEAM = 12;
	private static boolean ALLOW_BUFFS = false;
	private static boolean ALLOW_CLAN_SKILL = true;
	private static boolean ALLOW_HERO_SKILL = false;

	/*
	 * Config.EVENT_TVT_ARENA_ITEM_ID;
	 * Config.EVENT_TVT_ARENA_ITEM_NAME;
	 * Config.EVENT_TVT_ARENA_MAX_LENGTH_TEAM;
	 * Config.EVENT_TVT_ARENA_ALLOW_BUFFS;
	 * Config.EVENT_TVT_ARENA_ALLOW_CLAN_SKILL;
	 * Config.EVENT_TVT_ARENA_ALLOW_HERO_SKILL;
	 */

	protected int _managerId;
	protected String _className;

	protected Long _creatorId;
	protected NpcInstance _manager;
	protected int _status = 0;
	protected int _CharacterFound = 0;
	protected int _price = 10000;
	protected int _team1count = 1;
	protected int _team2count = 1;
	protected int _team1min = 1;
	protected int _team1max = 85;
	protected int _team2min = 1;
	protected int _team2max = 85;
	protected int _timeToStart = 10;
	protected boolean _timeOutTask;

	protected List<Location> _team1points;
	protected List<Location> _team2points;

	protected List<Long> _team1list;
	protected List<Long> _team2list;
	protected List<Long> _team1live;
	protected List<Long> _team2live;

	protected Zone _zone;
	protected ZoneListener _zoneListener;

	protected abstract void onLoad();

	protected abstract void onReload();

	public void template_stop()
	{
		if (_status <= 0)
		{
			return;
		}

		sayToAll("Fight interrupted for technical reasons, the rates returned");

		unParalyzeTeams();
		ressurectPlayers();
		returnItemToTeams();
		healPlayers();
		removeBuff();
		teleportPlayersToSavedCoords();
		clearTeams();
		_status = 0;
		_timeOutTask = false;
	}

	public void template_create1(Player player)
	{
		if (_status > 0)
		{
			show("Wait for the fight", player);
			return;
		}

		if (player.getTeam() != TeamType.NONE)
		{
			show("You are already registered", player);
			return;
		}
		show("scripts/events/TvTArena/" + _managerId + "-1.htm", player);
	}

	public void template_register(Player player)
	{
		if (_status == 0)
		{
			show("Battle at the moment is not created", player);
			return;
		}

		if (_status > 1)
		{
			show("Wait for the fight", player);
			return;
		}

		if (player.getTeam() != TeamType.NONE)
		{
			show("You are already registered", player);
			return;
		}
		show("scripts/events/TvTArena/" + _managerId + "-3.htm", player);
	}

	public void template_check1(Player player, NpcInstance manager, String[] var)
	{
		if (var.length != 8)
		{
			show("Incorrect data", player);
			return;
		}

		if (_status > 0)
		{
			show("Wait for the fight", player);
			return;
		}
		if (manager == null || !manager.isNpc())
		{
			show("Hacker? :) " + manager, player);
			return;
		}
		_manager = manager;
		try
		{
			_price = Integer.valueOf(var[0]);
			_team1count = Integer.valueOf(var[1]);
			_team2count = Integer.valueOf(var[2]);
			_team1min = Integer.valueOf(var[3]);
			_team1max = Integer.valueOf(var[4]);
			_team2min = Integer.valueOf(var[5]);
			_team2max = Integer.valueOf(var[6]);
			_timeToStart = Integer.valueOf(var[7]);
		}
		catch (Exception e)
		{
			show("Incorrect data", player);
			return;
		}
		if (_price < 1 || _price > 500)
		{
			show("Incorrect rate", player);
			return;
		}
		if (_team1count < 1 || _team1count > LENGTH_TEAM || _team2count < 1 || _team2count > LENGTH_TEAM)
		{
			show("Wrong size of the team", player);
			return;
		}
		if (_team1min < 1 || _team1min > 86 || _team2min < 1 || _team2min > 86 || _team1max < 1 || _team1max > 86 || _team2max < 1 || _team2max > 86 || _team1min > _team1max || _team2min > _team2max)
		{
			show("Wrong level", player);
			return;
		}
		if (player.getLevel() < _team1min || player.getLevel() > _team1max)
		{
			show("Wrong level", player);
			return;
		}
		if (_timeToStart < 1 || _timeToStart > 10)
		{
			show("Wrong time", player);
			return;
		}
		if (getItemCount(player, ITEM_ID) < _price)
		{
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			return;
		}
		_creatorId = player.getStoredId();
		removeItem(player, ITEM_ID, _price, "TvTTemplate");
		player.setTeam(TeamType.BLUE);
		_status = 1;
		_team1list.clear();
		_team2list.clear();
		_team1live.clear();
		_team2live.clear();
		_team1list.add(player.getStoredId());
		sayToAll(player.getName() + " created a battle " + _team1count + "х" + _team2count + ", " + _team1min + "-" + _team1max + "lv vs " + _team2min + "-" + _team2max + "lv, rate " + _price + " " + ITEM_NAME + ", Start over " + _timeToStart + " min");
		executeTask("events.TvTArena." + _className, "announce", new Object[0], 60000);
	}

	public void template_register_check(Player player)
	{
		if (_status == 0)
		{
			show("Battle at the moment is not created", player);
			return;
		}

		if (_status > 1)
		{
			show("Wait for the fight", player);
			return;
		}

		if (_team1list.contains(player.getStoredId()) || _team2list.contains(player.getStoredId()) || (player.getTeam() != TeamType.NONE))
		{
			show("You are already registered", player);
			return;
		}

		if (getItemCount(player, ITEM_ID) < _price)
		{
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			return;
		}

		int size1 = _team1list.size(), size2 = _team2list.size();

		if (size1 > size2)
		{
			String t = null;
			if (tryRegister(2, player) != null)
			{
				if ((t = tryRegister(1, player)) != null)
				{
					show(t, player);
				}
			}
		}
		else if (size1 < size2)
		{
			String t = null;
			if (tryRegister(1, player) != null)
			{
				if ((t = tryRegister(2, player)) != null)
				{
					show(t, player);
				}
			}
		}
		else
		{
			int team = Rnd.get(1, 2);
			String t = null;
			if (tryRegister(team, player) != null)
			{
				if ((t = tryRegister(team == 1 ? 2 : 1, player)) != null)
				{
					show(t, player);
				}
			}
		}
	}

	private String tryRegister(int team, Player player)
	{
		if (team == 1)
		{
			if (player.getLevel() < _team1min || player.getLevel() > _team1max)
			{
				return "You do not approach the level of";
			}
			if (_team1list.size() >= _team1count)
			{
				return "Team 1 full";
			}
			doRegister(1, player);
			return null;
		}
		if (player.getLevel() < _team2min || player.getLevel() > _team2max)
		{
			return "You do not approach the level of";
		}
		if (_team2list.size() >= _team2count)
		{
			return "Team 2 full";
		}
		doRegister(2, player);
		return null;
	}

	private void doRegister(int team, Player player)
	{
		removeItem(player, ITEM_ID, _price, "TvTTemplate");

		if (team == 1)
		{
			_team1list.add(player.getStoredId());
			player.setTeam(TeamType.BLUE);
			sayToAll(player.getName() + " registered for 1 team");
		}
		else
		{
			_team2list.add(player.getStoredId());
			player.setTeam(TeamType.RED);
			sayToAll(player.getName() + " registered for 2 team");
		}

		if (_team1list.size() >= _team1count && _team2list.size() >= _team2count)
		{
			sayToAll("Teams are ready, start at 1 minute.");
			_timeToStart = 1;
		}
	}

	public void template_announce()
	{
		Player creator = GameObjectsStorage.getAsPlayer(_creatorId);

		if (_status != 1 || creator == null)
		{
			return;
		}

		if (_timeToStart > 1)
		{
			_timeToStart--;
			sayToAll(creator.getName() + " created a battle " + _team1count + "х" + _team2count + ", " + _team1min + "-" + _team1max + "lv vs " + _team2min + "-" + _team2max + "lv, rate " + _price + " " + ITEM_NAME + ", Start over " + _timeToStart + " min");
			executeTask("events.TvTArena." + _className, "announce", new Object[0], 60000);
		}
		else if (_team2list.size() > 0)
		{
			sayToAll("Prepare for battle");
			executeTask("events.TvTArena." + _className, "prepare", new Object[0], 5000);
		}
		else
		{
			sayToAll("The battle will not take place, no opponents");
			_status = 0;
			returnItemToTeams();
			clearTeams();
		}
	}

	public void template_prepare()
	{
		if (_status != 1)
		{
			return;
		}

		_status = 2;
		for (Player player : getPlayers(_team1list))
		{
			if (!player.isDead())
			{
				_team1live.add(player.getStoredId());
			}
		}
		for (Player player : getPlayers(_team2list))
		{
			if (!player.isDead())
			{
				_team2live.add(player.getStoredId());
			}
		}
		if (!checkTeams())
		{
			return;
		}
		saveBackCoords();
		clearArena();
		ressurectPlayers();
		removeBuff();
		healPlayers();
		paralyzeTeams();
		teleportTeamsToArena();
		sayToAll("Fight starts in 30 seconds");
		executeTask("events.TvTArena." + _className, "start", new Object[0], 30000);
	}

	public void template_start()
	{
		if ((_status != 2) || !checkTeams())
		{
			return;
		}
		sayToAll("Go!!!");
		unParalyzeTeams();
		_status = 3;
		executeTask("events.TvTArena." + _className, "timeOut", new Object[0], 180000);
		_timeOutTask = true;
	}

	public void clearArena()
	{
		for (GameObject obj : _zone.getObjects())
		{
			if (obj != null && obj.isPlayable())
			{
				((Playable) obj).teleToLocation(_zone.getSpawn());
			}
		}
	}

	public boolean checkTeams()
	{
		if (_team1live.isEmpty())
		{
			teamHasLost(1);
			return false;
		}
		else if (_team2live.isEmpty())
		{
			teamHasLost(2);
			return false;
		}
		return true;
	}

	public void saveBackCoords()
	{
		for (Player player : getPlayers(_team1list))
		{
			player.setVar("TvTArena_backCoords", player.getX() + " " + player.getY() + " " + player.getZ() + " " + player.getReflectionId(), -1);
		}
		for (Player player : getPlayers(_team2list))
		{
			player.setVar("TvTArena_backCoords", player.getX() + " " + player.getY() + " " + player.getZ() + " " + player.getReflectionId(), -1);
		}
	}

	public void teleportPlayersToSavedCoords()
	{
		for (Player player : getPlayers(_team1list))
		{
			try
			{
				String var = player.getVar("TvTArena_backCoords");
				if (var == null || var.equals(""))
				{
					continue;
				}
				String[] coords = var.split(" ");
				if (coords.length != 4)
				{
					continue;
				}
				player.teleToLocation(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), Integer.parseInt(coords[3]));
				player.unsetVar("TvTArena_backCoords");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		for (Player player : getPlayers(_team2list))
		{
			try
			{
				String var = player.getVar("TvTArena_backCoords");
				if (var == null || var.equals(""))
				{
					continue;
				}
				String[] coords = var.split(" ");
				if (coords.length != 4)
				{
					continue;
				}
				player.teleToLocation(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), Integer.parseInt(coords[3]));
				player.unsetVar("TvTArena_backCoords");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void healPlayers()
	{
		for (Player player : getPlayers(_team1list))
		{
			player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
			player.setCurrentCp(player.getMaxCp());
		}
		for (Player player : getPlayers(_team2list))
		{
			player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
			player.setCurrentCp(player.getMaxCp());
		}
	}

	public void ressurectPlayers()
	{
		for (Player player : getPlayers(_team1list))
		{
			if (player.isDead())
			{
				player.restoreExp();
				player.setCurrentHp(player.getMaxHp(), true);
				player.setCurrentMp(player.getMaxMp());
				player.setCurrentCp(player.getMaxCp());
				player.broadcastPacket(new Revive(player));
			}
		}
		for (Player player : getPlayers(_team2list))
		{
			if (player.isDead())
			{
				player.restoreExp();
				player.setCurrentHp(player.getMaxHp(), true);
				player.setCurrentMp(player.getMaxMp());
				player.setCurrentCp(player.getMaxCp());
				player.broadcastPacket(new Revive(player));
			}
		}
	}

	public void removeBuff()
	{
		for (Player player : getPlayers(_team1list))
		{
			if (player != null)
			{
				try
				{
					if (player.isCastingNow())
					{
						player.abortCast(true, true);
					}

					if (!ALLOW_CLAN_SKILL)
					{
						if (player.getClan() != null)
						{
							for (Skill skill : player.getClan().getAllSkills())
							{
								player.removeSkill(skill, false);
							}
						}
					}

					if (!ALLOW_HERO_SKILL)
					{
						if (player.isHero())
						{
							Hero.removeSkills(player);
						}
					}

					if (!ALLOW_BUFFS)
					{
						player.getEffectList().stopAllEffects();

						if (player.getPet() != null)
						{
							Summon summon = player.getPet();
							summon.getEffectList().stopAllEffects();
							if (summon.isPet())
							{
								summon.unSummon();
							}
						}

						if (player.getAgathionId() > 0)
						{
							player.setAgathion(0);
						}
					}

					player.sendPacket(new SkillList(player));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		for (Player player : getPlayers(_team2list))
		{
			if (player != null)
			{
				try
				{
					if (player.isCastingNow())
					{
						player.abortCast(true, true);
					}

					if (!ALLOW_CLAN_SKILL)
					{
						if (player.getClan() != null)
						{
							for (Skill skill : player.getClan().getAllSkills())
							{
								player.removeSkill(skill, false);
							}
						}
					}

					if (!ALLOW_HERO_SKILL)
					{
						if (player.isHero())
						{
							Hero.removeSkills(player);
						}
					}

					if (!ALLOW_BUFFS)
					{
						player.getEffectList().stopAllEffects();

						if (player.getPet() != null)
						{
							Summon summon = player.getPet();
							summon.getEffectList().stopAllEffects();
							if (summon.isPet())
							{
								summon.unSummon();
							}
						}

						if (player.getAgathionId() > 0)
						{
							player.setAgathion(0);
						}
					}

					player.sendPacket(new SkillList(player));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void backBuff()
	{
		for (Player player : getPlayers(_team1list))
		{
			if (player == null)
			{
				continue;
			}
			try
			{
				player.getEffectList().stopAllEffects();

				if (!ALLOW_CLAN_SKILL)
				{
					if (player.getClan() != null)
					{
						for (Skill skill : player.getClan().getAllSkills())
						{
							if (skill.getMinPledgeClass() <= player.getPledgeClass())
							{
								player.addSkill(skill, false);
							}
						}
					}
				}

				if (!ALLOW_HERO_SKILL)
				{
					if (player.isHero())
					{
						Hero.addSkills(player);
					}
				}

				player.sendPacket(new SkillList(player));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		for (Player player : getPlayers(_team2list))
		{
			if (player == null)
			{
				continue;
			}
			try
			{
				player.getEffectList().stopAllEffects();

				if (!ALLOW_CLAN_SKILL)
				{
					if (player.getClan() != null)
					{
						for (Skill skill : player.getClan().getAllSkills())
						{
							if (skill.getMinPledgeClass() <= player.getPledgeClass())
							{
								player.addSkill(skill, false);
							}
						}
					}
				}

				if (!ALLOW_HERO_SKILL)
				{
					if (player.isHero())
					{
						Hero.addSkills(player);
					}
				}

				player.sendPacket(new SkillList(player));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void paralyzeTeams()
	{
		Skill revengeSkill = SkillTable.getInstance().getInfo(Skill.SKILL_RAID_CURSE, 1);
		for (Player player : getPlayers(_team1list))
		{
			player.getEffectList().stopEffect(Skill.SKILL_MYSTIC_IMMUNITY);
			revengeSkill.getEffects(player, player, false, false);
			if (player.getPet() != null)
			{
				revengeSkill.getEffects(player, player.getPet(), false, false);
			}
		}
		for (Player player : getPlayers(_team2list))
		{
			player.getEffectList().stopEffect(Skill.SKILL_MYSTIC_IMMUNITY);
			revengeSkill.getEffects(player, player, false, false);
			if (player.getPet() != null)
			{
				revengeSkill.getEffects(player, player.getPet(), false, false);
			}
		}
	}

	public void unParalyzeTeams()
	{
		for (Player player : getPlayers(_team1list))
		{
			player.getEffectList().stopEffect(Skill.SKILL_RAID_CURSE);
			if (player.getPet() != null)
			{
				player.getPet().getEffectList().stopEffect(Skill.SKILL_RAID_CURSE);
			}

			player.leaveParty();
		}
		for (Player player : getPlayers(_team2list))
		{
			player.getEffectList().stopEffect(Skill.SKILL_RAID_CURSE);
			if (player.getPet() != null)
			{
				player.getPet().getEffectList().stopEffect(Skill.SKILL_RAID_CURSE);
			}

			player.leaveParty();
		}
	}

	public void teleportTeamsToArena()
	{
		Integer n = 0;
		for (Player player : getPlayers(_team1live))
		{
			unRide(player);
			unSummonPet(player, true);
			player.teleToLocation(_team1points.get(n), ReflectionManager.DEFAULT);
			n++;
		}
		n = 0;
		for (Player player : getPlayers(_team2live))
		{
			unRide(player);
			unSummonPet(player, true);
			player.teleToLocation(_team2points.get(n), ReflectionManager.DEFAULT);
			n++;
		}
	}

	public boolean playerHasLost(Player player)
	{
		if (player.getTeam() == TeamType.BLUE)
		{
			_team1live.remove(player.getStoredId());
		}
		else
		{
			_team2live.remove(player.getStoredId());
		}
		Skill revengeSkill = SkillTable.getInstance().getInfo(Skill.SKILL_RAID_CURSE, 1);
		revengeSkill.getEffects(player, player, false, false);
		return !checkTeams();
	}

	public void teamHasLost(Integer team_id)
	{
		if (team_id == 1)
		{
			sayToAll("Team 2 won");
			payItemToTeam(2);
		}
		else
		{
			sayToAll("Team 1 won");
			payItemToTeam(1);
		}
		unParalyzeTeams();
		backBuff();
		teleportPlayersToSavedCoords();
		ressurectPlayers();
		healPlayers();
		clearTeams();
		_status = 0;
		_timeOutTask = false;
	}

	public void template_timeOut()
	{
		if (_timeOutTask && _status == 3)
		{
			sayToAll("Time has run out, a draw!");
			returnItemToTeams();
			unParalyzeTeams();
			backBuff();
			teleportPlayersToSavedCoords();
			ressurectPlayers();
			healPlayers();
			clearTeams();
			_status = 0;
			_timeOutTask = false;
		}
	}

	public void payItemToTeam(Integer team_id)
	{
		if (team_id == 1)
		{
			for (Player player : getPlayers(_team1list))
			{
				addItem(player, ITEM_ID, _price + _team2list.size() * _price / _team1list.size(), "TvTTemplate");
			}
		}
		else
		{
			for (Player player : getPlayers(_team2list))
			{
				addItem(player, ITEM_ID, _price + _team2list.size() * _price / _team1list.size(), "TvTTemplate");
			}
		}
	}

	public void returnItemToTeams()
	{
		for (Player player : getPlayers(_team1list))
		{
			addItem(player, ITEM_ID, _price, "TvTTemplate");
		}
		for (Player player : getPlayers(_team2list))
		{
			addItem(player, ITEM_ID, _price, "TvTTemplate");
		}
	}

	public void clearTeams()
	{
		for (Player player : getPlayers(_team1list))
		{
			player.setTeam(TeamType.NONE);
		}
		for (Player player : getPlayers(_team2list))
		{
			player.setTeam(TeamType.NONE);
		}
		_team1list.clear();
		_team2list.clear();
		_team1live.clear();
		_team2live.clear();
	}

	public void onDeath(Creature self, Creature killer)
	{
		if (_status >= 2 && self.isPlayer() && self.getTeam() != TeamType.NONE && (_team1list.contains(self.getStoredId()) || _team2list.contains(self.getStoredId())))
		{
			Player player = self.getPlayer();
			Player kplayer = killer.getPlayer();
			if (kplayer != null)
			{
				sayToAll(kplayer.getName() + " killed " + player.getName());
				if (player.getTeam() == kplayer.getTeam() || !_team1list.contains(kplayer.getStoredId()) && !_team2list.contains(kplayer.getStoredId()))
				{
					sayToAll("Violation of the rules, a player " + kplayer.getName() + " fined " + _price + " " + ITEM_NAME);
					removeItem(kplayer, ITEM_ID, _price, "TvTTemplate");
				}
				playerHasLost(player);
			}
			else
			{
				sayToAll(player.getName() + " killed");
				playerHasLost(player);
			}
		}
	}

	public void onPlayerExit(Player player)
	{
		if (player != null && _status > 0 && player.getTeam() != TeamType.NONE && (_team1list.contains(player.getStoredId()) || _team2list.contains(player.getStoredId())))
		{
			switch (_status)
			{
			case 1:
				removePlayer(player);
				sayToAll(player.getName() + " disqualified");
				if (player.getStoredId() == _creatorId)
				{
					sayToAll("Fight interrupted rates returned");

					returnItemToTeams();
					backBuff();
					teleportPlayersToSavedCoords();
					unParalyzeTeams();
					ressurectPlayers();
					healPlayers();
					clearTeams();

					unParalyzeTeams();
					clearTeams();
					_status = 0;
					_timeOutTask = false;
				}
				break;
			case 2:
				removePlayer(player);
				sayToAll(player.getName() + " disqualified");
				checkTeams();
				break;
			case 3:
				removePlayer(player);
				sayToAll(player.getName() + " disqualified");
				checkTeams();
				break;
			}
		}
	}

	public void onTeleport(Player player)
	{
		if (player != null && _status > 1 && player.getTeam() != TeamType.NONE && player.isInZone(_zone))
		{
			onPlayerExit(player);
		}
	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			Player player = cha.getPlayer();
			if (_status >= 2 && player != null && !(_team1list.contains(player.getStoredId()) || _team2list.contains(player.getStoredId())))
			{
				ThreadPoolManager.getInstance().schedule(new TeleportTask(cha, _zone.getSpawn()), 3000);
			}
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
			Player player = cha.getPlayer();
			if (_status >= 2 && player != null && player.getTeam() != TeamType.NONE && (_team1list.contains(player.getStoredId()) || _team2list.contains(player.getStoredId())))
			{
				double angle = PositionUtils.convertHeadingToDegree(cha.getHeading()); // угол в градусах
				double radian = Math.toRadians(angle - 90); // угол в радианах
				int x = (int) (cha.getX() + 50 * Math.sin(radian));
				int y = (int) (cha.getY() - 50 * Math.cos(radian));
				int z = cha.getZ();
				ThreadPoolManager.getInstance().schedule(new TeleportTask(cha, new Location(x, y, z)), 3000);
			}
		}

		@Override
		public void onEquipChanged(Zone zone, Creature actor)
		{
		}
	}

	public class TeleportTask extends RunnableImpl
	{
		Location loc;
		Creature target;

		public TeleportTask(Creature target, Location loc)
		{
			this.target = target;
			this.loc = loc;
			target.block();
		}

		@Override
		public void runImpl() throws Exception
		{
			target.unblock();
			target.teleToLocation(loc);
		}
	}

	private void removePlayer(Player player)
	{
		if (player != null)
		{
			_team1list.remove(player.getStoredId());
			_team2list.remove(player.getStoredId());
			_team1live.remove(player.getStoredId());
			_team2live.remove(player.getStoredId());
			player.setTeam(TeamType.NONE);
		}
	}

	private List<Player> getPlayers(List<Long> list)
	{
		List<Player> result = new ArrayList<Player>();
		for (Long storeId : list)
		{
			Player player = GameObjectsStorage.getAsPlayer(storeId);
			if (player != null)
			{
				result.add(player);
			}
		}
		return result;
	}

	public void sayToAll(String text)
	{
		Announcements.getInstance().announceToAll(_manager.getName() + ": " + text, ChatType.CRITICAL_ANNOUNCE);
	}
}