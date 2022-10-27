package l2mv.gameserver.model.entity.events.impl.fightclub;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l2mv.commons.collections.MultiValueSet;
import l2mv.gameserver.instancemanager.SpawnManager;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.Spawner;
import l2mv.gameserver.model.entity.events.fightclubmanager.FightClubPlayer;
import l2mv.gameserver.model.entity.events.fightclubmanager.FightClubTeam;
import l2mv.gameserver.model.entity.events.fightclubmanager.FightClubTeamType;
import l2mv.gameserver.model.entity.events.impl.AbstractFightClub;
import l2mv.gameserver.model.entity.events.objects.DoorObject;
import l2mv.gameserver.model.instances.DoorInstance;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.utils.NpcGroupLocation;

public class FightForThroneEvent extends AbstractFightClub
{
	private static final String CRYSTAL_NAME = "crystal";
	private static final String BOTH_TEAMS_ATTACK_ON_START_NAME = "bothTeamsAttackOnStart";
	private static final String SPAWN_GUARDS_NO_DEFENDERS_NAME = "spawnGuardsOnNoDefenders";
	private static final String SPAWN_GUARDS_WITH_DEFENDERS_NAME = "spawnGuardsNearDefenders";
	private static final String GUARDS_NAME = "guards";
	private static final String LIFE_CONTROL_TOWERS_NAME = "controlTowers";
	private static final String SEAL_OF_RULER_ID_NAME = "sealOfRulerId";
	private static final String SEAL_OF_RULER_CAST_TIME_NAME = "sealOfRulerHitTime";
	private static final String RESPAWN_AFTER_TOWERS_DESTROYED_NAME = "respawnAfterTowersDestroyed";

	private NpcInstance _crystalNpc;
	private final List<NpcInstance> _lifeControlTowers;
	private final List<NpcInstance> _guards;
	private final int _respawnAfterTowersDestroyed;
	private final int _sealOfRulerId;
	private final int _sealOfRulerCastTime;

	public FightForThroneEvent(MultiValueSet<String> set)
	{
		super(set);

		_lifeControlTowers = new CopyOnWriteArrayList<>();
		_guards = new ArrayList<>();
		_respawnAfterTowersDestroyed = set.getInteger(RESPAWN_AFTER_TOWERS_DESTROYED_NAME);
		_sealOfRulerId = set.getInteger(SEAL_OF_RULER_ID_NAME);
		_sealOfRulerCastTime = set.getInteger(SEAL_OF_RULER_CAST_TIME_NAME);
	}

	@Override
	public String getShortName()
	{
		return "FFT";
	}

	@Override
	public void onKilled(Creature actor, Creature victim)
	{
		if ((actor != null) && (actor.isPlayable()))
		{
			FightClubPlayer realActor = getFightClubPlayer(actor.getPlayer());
			if ((victim.isPlayer()) && (realActor != null))
			{
				realActor.increaseKills(true);
				updatePlayerScore(realActor);
				updateScreenScores();
				sendMessageToPlayer(realActor, AbstractFightClub.MessageType.GM, "You have killed " + victim.getName());
			}
			else if (!victim.isPet())
			{
			}
			actor.getPlayer().sendUserInfo();
		}
		if (victim.isPlayer())
		{
			FightClubPlayer realVictim = getFightClubPlayer(victim);
			if (realVictim != null)
			{
				realVictim.increaseDeaths();
				if (actor != null)
				{
					sendMessageToPlayer(realVictim, AbstractFightClub.MessageType.GM, "You have been killed by " + actor.getName());
				}
			}
			victim.broadcastCharInfo();
		}
		else if ((victim.isNpc()) && (_lifeControlTowers.contains(victim)))
		{
			_lifeControlTowers.remove(victim);
			if (_lifeControlTowers.isEmpty())
			{
				sendMessageToFighting(AbstractFightClub.MessageType.GM, "All Life Control Towers are now destroyed!", false);
			}
		}
		super.onKilled(actor, victim);
	}

	@Override
	protected boolean allowCreateTeamType(FightClubTeamType teamType)
	{
		return teamType != FightClubTeamType.DEFENDER;
	}

	@Override
	public void startRound()
	{
		super.startRound();

		spawnAll(false);
	}

	private void spawnAll(boolean onTakeCastle)
	{
		if (onTakeCastle)
		{
			for (DoorObject doorObject : getObjects("doorsObject", DoorObject.class))
			{
				doorObject.spawnObject(this);
			}
		}
		if (!_guards.isEmpty())
		{
			for (NpcInstance guard : _guards)
			{
				guard.deleteMe();
			}
			_guards.clear();
		}
		if (((!onTakeCastle) && (getParamBool(BOTH_TEAMS_ATTACK_ON_START_NAME)) && (getParamBool(SPAWN_GUARDS_NO_DEFENDERS_NAME))) || ((onTakeCastle) && (getParamBool(SPAWN_GUARDS_WITH_DEFENDERS_NAME))))
		{
			_guards.addAll(spawnNpcs(this, getParam(GUARDS_NAME), true, true));
		}
		if (!onTakeCastle)
		{
			_crystalNpc = spawnNpc(this, getParam(CRYSTAL_NAME), false);
		}
		for (NpcInstance lifeControlTower : _lifeControlTowers)
		{
			lifeControlTower.deleteMe();
		}
		_lifeControlTowers.clear();

		for (NpcGroupLocation npcGroupLocation : getMap().getNpcLocations())
		{
			if (npcGroupLocation.getGroupName().equalsIgnoreCase(LIFE_CONTROL_TOWERS_NAME))
			{
				_lifeControlTowers.add(spawnNpc(npcGroupLocation.getNpcId(), npcGroupLocation, 0));
			}
		}
	}

	@Override
	public void onJoinedEvent(FightClubPlayer fPlayer)
	{
		if (fPlayer.getTeam().getTeamType() == FightClubTeamType.ATTACKER)
		{
			giveSkill(fPlayer);
		}
	}

	private void giveSkill(FightClubPlayer fPlayer)
	{
		Player player = fPlayer.getPlayer();
		if (player.getKnownSkill(_sealOfRulerId) == null)
		{
			player.addSkill(SkillTable.getInstance().getInfo(_sealOfRulerId, 1), false);
		}
	}

	private void removeSkill(FightClubPlayer fPlayer)
	{
		Player player = fPlayer.getPlayer();
		if (!player.isClanLeader())
		{
			player.removeSkill(_sealOfRulerId, false);
		}
	}

	@Override
	public void onLeaveEvent(FightClubPlayer fPlayer)
	{
		super.onLeaveEvent(fPlayer);
		removeSkill(fPlayer);
	}

	@Override
	public boolean canAttackDoor(DoorInstance door, Creature attacker)
	{
		return true;
	}

	@Override
	public boolean canUseSkill(Creature caster, Creature target, Skill skill)
	{
		if (skill.getId() != _sealOfRulerId)
		{
			return super.canUseSkill(caster, target, skill);
		}
		if (!caster.isPlayer())
		{
			return false;
		}
		FightClubPlayer fPlayer = getFightClubPlayer(caster);
		if ((fPlayer == null) || (fPlayer.getTeam().getTeamType() == FightClubTeamType.DEFENDER))
		{
			return false;
		}
		return true;
	}

	@Override
	public int getSkillHitTime(Creature caster, Skill skill, int originalHitTime)
	{
		if (skill.getId() == _sealOfRulerId)
		{
			return _sealOfRulerCastTime;
		}
		return originalHitTime;
	}

	@Override
	public void onFinishedSkill(Creature caster, Creature target, Skill skill)
	{
		super.onFinishedSkill(caster, target, skill);
		if ((skill.getId() != _sealOfRulerId) || !caster.isPlayer())
		{
			return;
		}
		FightClubPlayer fPlayer = getFightClubPlayer(caster);
		if ((fPlayer == null) || (fPlayer.getTeam().getTeamType() == FightClubTeamType.DEFENDER))
		{
			return;
		}
		onTakeCastle(fPlayer);
	}

	private void onTakeCastle(FightClubPlayer skillCaster)
	{
		spawnAll(true);
		for (FightClubTeam team : getTeams())
		{
			if (team.equals(skillCaster.getTeam()))
			{
				team.setTeamType(this, FightClubTeamType.DEFENDER);
			}
			else
			{
				team.setTeamType(this, FightClubTeamType.ATTACKER);
			}
		}
		for (FightClubPlayer iFPlayer : getPlayers(new String[]
		{
			FIGHTING_PLAYERS
		}))
		{
			FightClubTeam team = iFPlayer.getTeam();
			if (team.getTeamType() == FightClubTeamType.ATTACKER)
			{
				giveSkill(iFPlayer);
			}
			else
			{
				removeSkill(iFPlayer);
			}
			teleportSinglePlayer(iFPlayer, false, true);
		}
		sendMessageToFighting(AbstractFightClub.MessageType.CRITICAL, "Castle was taken by " + skillCaster.getPlayer().getName() + '!', false);
		skillCaster.getTeam().incScore(1);
		updateScreenScores();
	}

	@Override
	public void endRound()
	{
		super.endRound();

		List<Spawner> guardSpawnList = SpawnManager.getInstance().getSpawners(getMap().getSet().getString(GUARDS_NAME));
		for (Spawner guardSpawn : guardSpawnList)
		{
			guardSpawn.deleteAll();
		}
		_crystalNpc.deleteMe();
	}

	@Override
	public Boolean canAttack(Creature actor, Creature target, Skill skill, boolean force)
	{
		if ((target.isSiegeGuard()) || (actor.isSiegeGuard()))
		{
			boolean defender = false;
			if (getFightClubPlayer(target, new String[]
			{
				FIGHTING_PLAYERS
			}) != null)
			{
				defender = getFightClubPlayer(target, new String[]
				{
					FIGHTING_PLAYERS
				}).getTeam().getTeamType() == FightClubTeamType.DEFENDER;
			}
			else if (getFightClubPlayer(actor, new String[]
			{
				FIGHTING_PLAYERS
			}) != null)
			{
				defender = getFightClubPlayer(actor, new String[]
				{
					FIGHTING_PLAYERS
				}).getTeam().getTeamType() == FightClubTeamType.DEFENDER;
			}
			else
			{
				return null;
			}
			return !defender;
		}
		return super.canAttack(target, actor, skill, force);
	}

	protected FightClubTeam getWinnerTeam(boolean nullOnDraw)
	{
		for (FightClubTeam team : getTeams())
		{
			if (team.getTeamType() == FightClubTeamType.DEFENDER)
			{
				return team;
			}
		}
		return null;
	}

	@Override
	public int getRespawnTime(FightClubPlayer fPlayer)
	{
		if ((_lifeControlTowers.isEmpty()) && (fPlayer.getTeam().getTeamType() == FightClubTeamType.DEFENDER))
		{
			return _respawnAfterTowersDestroyed;
		}
		return super.getRespawnTime(fPlayer);
	}

	@Override
	public String getVisibleTitle(Player player, String currentTitle, boolean toMe)
	{
		FightClubPlayer fPlayer = getFightClubPlayer(player);
		if (fPlayer == null)
		{
			return currentTitle;
		}
		return "Kills: " + fPlayer.getKills(true) + " Deaths: " + fPlayer.getDeaths();
	}
}
