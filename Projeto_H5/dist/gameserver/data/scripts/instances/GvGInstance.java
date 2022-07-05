package instances;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.lang3.mutable.MutableInt;

import events.GvG.GvG;
import gnu.trove.map.hash.TIntObjectHashMap;
import l2f.commons.lang.reference.HardReference;
import l2f.commons.lang.reference.HardReferences;
import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.listener.actor.OnDeathListener;
import l2f.gameserver.listener.actor.player.OnPlayerPartyLeaveListener;
import l2f.gameserver.listener.actor.player.OnTeleportListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Party;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.Zone;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.ExCubeGameAddPlayer;
import l2f.gameserver.network.serverpackets.ExCubeGameChangePoints;
import l2f.gameserver.network.serverpackets.ExCubeGameCloseUI;
import l2f.gameserver.network.serverpackets.ExCubeGameEnd;
import l2f.gameserver.network.serverpackets.ExCubeGameExtendedChangePoints;
import l2f.gameserver.network.serverpackets.ExCubeGameRemovePlayer;
import l2f.gameserver.network.serverpackets.ExShowScreenMessage;
import l2f.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2f.gameserver.network.serverpackets.L2GameServerPacket;
import l2f.gameserver.network.serverpackets.Revive;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.tables.SkillTable;
import l2f.gameserver.utils.Location;

/**
 * Инстанс для GvG турнира
 *
 * @author pchayka
 */
public class GvGInstance extends Reflection
{
	private final static int BOX_ID = 18822;
	private final static int BOSS_ID = 25655;

	private final static int SCORE_BOX = 20;
	private final static int SCORE_BOSS = 100;
	private final static int SCORE_KILL = 5;
	private final static int SCORE_DEATH = 3;

	private int eventTime = 1200;
	private long bossSpawnTime = 10 * 60 * 1000L;

	private boolean active = false;

	private Party team1;
	private Party team2;
	private List<HardReference<Player>> bothTeams = new CopyOnWriteArrayList<HardReference<Player>>();

	private TIntObjectHashMap<MutableInt> score = new TIntObjectHashMap<MutableInt>();
	private int team1Score = 0;
	private int team2Score = 0;

	private long startTime;

	private ScheduledFuture<?> _bossSpawnTask;
	private ScheduledFuture<?> _countDownTask;
	private ScheduledFuture<?> _battleEndTask;

	private DeathListener _deathListener = new DeathListener();
	private TeleportListener _teleportListener = new TeleportListener();
	private PlayerPartyLeaveListener _playerPartyLeaveListener = new PlayerPartyLeaveListener();

	@SuppressWarnings("unused")
	private Zone zonebattle;
	private Zone zonepvp;

	@SuppressWarnings("unused")
	private Zone zonepeace1;
	private Zone peace1;

	@SuppressWarnings("unused")
	private Zone zonepeace2;
	private Zone peace2;

	public void setTeam1(Party party1)
	{
		team1 = party1;
	}

	public void setTeam2(Party party2)
	{
		team2 = party2;
	}

	public GvGInstance()
	{
		super();
	}

	/**
	 * General instance initialization and assigning global variables
	 */
	public void start()
	{
		zonepvp = getZone("[gvg_battle_zone]");
		peace1 = getZone("[gvg_1_peace]");
		peace2 = getZone("[gvg_2_peace]");
		// Box spawns
		Location boxes[] =
		{
			new Location(142696, 139704, -15264, 0),
			new Location(142696, 145944, -15264, 0),
			new Location(145784, 142824, -15264, 0),
			new Location(145768, 139704, -15264, 0),
			new Location(145768, 145944, -15264, 0),
			new Location(141752, 142760, -15624, 0),
			new Location(145720, 142008, -15880, 0),
			new Location(145720, 143640, -15880, 0),
			new Location(139592, 142824, -15264, 0)
		};

		for (int i = 0; i < boxes.length; i++)
		{
			addSpawnWithoutRespawn(BOX_ID, boxes[i], 0);
		}

		addSpawnWithoutRespawn(35423, new Location(139640, 139736, -15264), 0); // Red team flag
		addSpawnWithoutRespawn(35426, new Location(139672, 145896, -15264), 0); // Blue team flag

		_bossSpawnTask = ThreadPoolManager.getInstance().schedule(new BossSpawn(), bossSpawnTime); //
		_countDownTask = ThreadPoolManager.getInstance().schedule(new CountingDown(), (eventTime - 1) * 1000L);
		_battleEndTask = ThreadPoolManager.getInstance().schedule(new BattleEnd(), (eventTime - 6) * 1000L); // -6 is about to prevent built-in BlockChecker countdown task

		// Assigning players to teams
		for (Player member : team1.getMembers())
		{
			bothTeams.add(member.getRef());
			member.addListener(_deathListener);
			member.addListener(_teleportListener);
			member.addListener(_playerPartyLeaveListener);
		}

		for (Player member : team2.getMembers())
		{
			bothTeams.add(member.getRef());
			member.addListener(_deathListener);
			member.addListener(_teleportListener);
			member.addListener(_playerPartyLeaveListener);
		}

		startTime = System.currentTimeMillis() + eventTime * 1000L; // Used in packet broadcasting

		// Forming packets to send everybody
		final ExCubeGameChangePoints initialPoints = new ExCubeGameChangePoints(eventTime, team1Score, team2Score);
		final ExCubeGameCloseUI cui = new ExCubeGameCloseUI();
		ExCubeGameExtendedChangePoints clientSetUp;

		for (Player tm : HardReferences.unwrap(bothTeams))
		{
			score.put(tm.getObjectId(), new MutableInt());

			tm.setCurrentCp(tm.getMaxCp());
			tm.setCurrentHp(tm.getMaxHp(), false);
			tm.setCurrentMp(tm.getMaxMp());
			clientSetUp = new ExCubeGameExtendedChangePoints(eventTime, team1Score, team2Score, isRedTeam(tm), tm, 0);
			tm.sendPacket(clientSetUp);
			tm.sendActionFailed(); // useless? copy&past from BlockChecker
			tm.sendPacket(initialPoints);
			tm.sendPacket(cui); // useless? copy&past from BlockChecker
			broadCastPacketToBothTeams(new ExCubeGameAddPlayer(tm, isRedTeam(tm)));
		}

		active = true;
	}

	/**
	 * @param packet Broadcasting packet to every member of instance
	 */
	private void broadCastPacketToBothTeams(L2GameServerPacket packet)
	{
		for (Player tm : HardReferences.unwrap(bothTeams))
		{
			tm.sendPacket(packet);
		}
	}

	/**
	 * @return Whether event is active. active starts with instance dungeon and ends with team victory
	 */
	private boolean isActive()
	{
		return active;
	}

	/**
	 * @param player
	 * @return Whether player belongs to Red Team (team2)
	 */
	private boolean isRedTeam(Player player)
	{
		if (team2.containsMember(player))
		{
			return true;
		}
		return false;
	}

	/**
	 * Handles the end of event
	 */
	private void end()
	{
		active = false;

		startCollapseTimer(60 * 1000L);

		paralyzePlayers();
		ThreadPoolManager.getInstance().schedule(new Finish(), 55 * 1000L);

		if (_bossSpawnTask != null)
		{
			_bossSpawnTask.cancel(false);
			_bossSpawnTask = null;
		}
		if (_countDownTask != null)
		{
			_countDownTask.cancel(false);
			_countDownTask = null;
		}
		if (_battleEndTask != null)
		{
			_battleEndTask.cancel(false);
			_battleEndTask = null;
		}

		boolean isRedWinner = false;

		isRedWinner = getRedScore() >= getBlueScore();

		final ExCubeGameEnd end = new ExCubeGameEnd(isRedWinner);
		broadCastPacketToBothTeams(end);

		reward(isRedWinner ? team2 : team1);
		GvG.updateWinner(isRedWinner ? team2.getLeader() : team1.getLeader());

		// Удаление созданных зон из мира
		zonepvp.setActive(false);
		peace1.setActive(false);
		peace2.setActive(false);
	}

	private void reward(Party party)
	{
		for (Player member : party.getMembers())
		{
			member.sendMessage("Your team won the tournament GvG, leader of the group added in the top winners.");
			member.setFame(member.getFame() + 500, "GvG"); // fame
			Functions.addItem(member, 6673, 8, "GvG"); // Fantasy Isle Coin
		}
	}

	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			// Убийство произошло в инстанте
			if (!isActive() || self.getReflection() != killer.getReflection() || self.getReflection() != GvGInstance.this)
			{
				return;
			}

			if (self.isPlayer() && killer.isPlayable()) // if PvP kill
			{
				if (team1.containsMember(self.getPlayer()) && team2.containsMember(killer.getPlayer()))
				{
					addPlayerScore(killer.getPlayer());
					changeScore(1, SCORE_KILL, SCORE_DEATH, true, true, killer.getPlayer());
				}
				else if (team2.containsMember(self.getPlayer()) && team1.containsMember(killer.getPlayer()))
				{
					addPlayerScore(killer.getPlayer());
					changeScore(2, SCORE_KILL, SCORE_DEATH, true, true, killer.getPlayer());
				}
				resurrectAtBase(self.getPlayer());
			}
			else if (self.isPlayer() && !killer.isPlayable())
			{ // if not-PvP kill
				resurrectAtBase(self.getPlayer());
			}
			else if (self.isNpc() && killer.isPlayable()) // onKill - mob death
			{
				if (self.getNpcId() == BOX_ID)
				{
					if (team1.containsMember(killer.getPlayer()))
					{
						changeScore(1, SCORE_BOX, 0, false, false, killer.getPlayer());
					}
					else if (team2.containsMember(killer.getPlayer()))
					{
						changeScore(2, SCORE_BOX, 0, false, false, killer.getPlayer());
					}
				}
				else if (self.getNpcId() == BOSS_ID)
				{
					if (team1.containsMember(killer.getPlayer()))
					{
						changeScore(1, SCORE_BOSS, 0, false, false, killer.getPlayer());
					}
					else if (team2.containsMember(killer.getPlayer()))
					{
						changeScore(2, SCORE_BOSS, 0, false, false, killer.getPlayer());
					}

					broadCastPacketToBothTeams(new ExShowScreenMessage("Treasure guard Gerald died at the hands of " + killer.getName(), 5000, ScreenMessageAlign.MIDDLE_CENTER, true));
					end();
				}
			}
		}
	}

	/**
	 * @param teamId
	 * @param toAdd			 - how much points to add
	 * @param toSub			 - how much points to remove
	 * @param subbing		   - whether change is reducing points
	 * @param affectAnotherTeam - change can affect only teamId or both
	 * @param player			Any score change are handled here.
	 */
	private synchronized void changeScore(int teamId, int toAdd, int toSub, boolean subbing, boolean affectAnotherTeam, Player player)
	{
		int timeLeft = (int) ((startTime - System.currentTimeMillis()) / 1000);
		if (teamId == 1)
		{
			if (subbing)
			{
				team1Score -= toSub;
				if (team1Score < 0)
				{
					team1Score = 0;
				}
				if (affectAnotherTeam)
				{
					team2Score += toAdd;
					broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, true, player, getPlayerScore(player)));
				}
				broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, false, player, getPlayerScore(player)));
			}
			else
			{
				team1Score += toAdd;
				if (affectAnotherTeam)
				{
					team2Score -= toSub;
					if (team2Score < 0)
					{
						team2Score = 0;
					}
					broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, true, player, getPlayerScore(player)));
				}
				broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, false, player, getPlayerScore(player)));
			}
		}
		else if (teamId == 2)
		{
			if (subbing)
			{
				team2Score -= toSub;
				if (team2Score < 0)
				{
					team2Score = 0;
				}
				if (affectAnotherTeam)
				{
					team1Score += toAdd;
					broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, false, player, getPlayerScore(player)));
				}
				broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, true, player, getPlayerScore(player)));
			}
			else
			{
				team2Score += toAdd;
				if (affectAnotherTeam)
				{
					team1Score -= toSub;
					if (team1Score < 0)
					{
						team1Score = 0;
					}
					broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, false, player, getPlayerScore(player)));
				}
				broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, true, player, getPlayerScore(player)));
			}
		}
	}

	/**
	 * @param player Handles the increase of personal player points
	 */
	private void addPlayerScore(Player player)
	{
		MutableInt points = score.get(player.getObjectId());
		points.increment();
	}

	/**
	 * @param player
	 * @return Returns personal player score
	 */
	public int getPlayerScore(Player player)
	{
		MutableInt points = score.get(player.getObjectId());
		return points.intValue();
	}

	/**
	 * Paralyzes everybody in instance to prevent any actions while event is !isActive
	 */
	public void paralyzePlayers()
	{
		for (Player tm : HardReferences.unwrap(bothTeams))
		{
			if (tm.isDead())
			{
				tm.setCurrentHp(tm.getMaxHp(), true);
				tm.broadcastPacket(new Revive(tm));
			}
			else
			{
				tm.setCurrentHp(tm.getMaxHp(), false);
			}

			tm.setCurrentMp(tm.getMaxMp());
			tm.setCurrentCp(tm.getMaxCp());

			tm.getEffectList().stopEffect(Skill.SKILL_MYSTIC_IMMUNITY);
			tm.block();
		}
	}

	/**
	 * Romoves paralization
	 */
	public void unParalyzePlayers()
	{
		for (Player tm : HardReferences.unwrap(bothTeams))
		{
			tm.unblock();
			removePlayer(tm, true);
		}
	}

	/**
	 * Cleans up every list and task
	 */
	private void cleanUp()
	{
		team1 = null;
		team2 = null;
		bothTeams.clear();
		team1Score = 0;
		team2Score = 0;
		score.clear();
	}

	/**
	 * @param player
	 * @param refId  Called by onDeath. Handles the resurrection at the proper base.
	 */
	public void resurrectAtBase(Player player)
	{
		if (player.isDead())
		{
			// player.setCurrentCp(player.getMaxCp());
			player.setCurrentHp(0.7 * player.getMaxHp(), true);
			// player.setCurrentMp(player.getMaxMp());
			player.broadcastPacket(new Revive(player));
		}
		player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(5660, 2)); // Battlefield Death Syndrome

		// Location pos;
		// if(team1.containsMember(player))
		// pos = Location.findPointToStay(GvG.TEAM1_LOC, 0, 150, getGeoIndex());
		// else
		// pos = Location.findPointToStay(GvG.TEAM2_LOC, 0, 150, getGeoIndex());

		// player.teleToLocation(pos, this);
	}

	/**
	 * @param player
	 * @param legalQuit - whether quit was called by event or by player escape
	 *                  Removes player from every list or instance, teleports him and stops the event timer
	 */
	private void removePlayer(Player player, boolean legalQuit)
	{
		bothTeams.remove(player.getRef());

		broadCastPacketToBothTeams(new ExCubeGameRemovePlayer(player, isRedTeam(player)));
		player.removeListener(_deathListener);
		player.removeListener(_teleportListener);
		player.removeListener(_playerPartyLeaveListener);
		player.leaveParty();
		if (!legalQuit)
		{
			player.sendPacket(new ExCubeGameEnd(false));
			// player.teleToLocation(Location.findPointToStay(GvG.RETURN_LOC, 0, 150, ReflectionManager.DEFAULT.getGeoIndex()), 0);
		}
	}

	/**
	 * @param isRed Handles the team withdraw from the area of event. Can only be called when !isActive
	 */
	private void teamWithdraw(Party party)
	{
		if (party == team1)
		{
			for (Player player : team1.getMembers())
			{
				removePlayer(player, false);
			}

			Player player = team2.getLeader();
			changeScore(2, 200, 0, false, false, player); // adding 200 to the team score for enemy team withdrawal. player - leader of the team who's left in the instance
		}
		else
		{
			for (Player player : team2.getMembers())
			{
				removePlayer(player, false);
			}

			Player player = team1.getLeader();
			changeScore(1, 200, 0, false, false, player); // adding 200 to the team score for enemy team withdrawal. player - leader of the team who's left in the instance
		}

		broadCastPacketToBothTeams(new ExShowScreenMessage("The opposing team has left the field of battle at full strength. End of battle.", 4000, ScreenMessageAlign.MIDDLE_CENTER, true));
		end();
	}

	private int getBlueScore()
	{
		return team1Score;
	}

	private int getRedScore()
	{
		return team2Score;
	}

	public class BossSpawn extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			broadCastPacketToBothTeams(new ExShowScreenMessage("There was a guard Treasure Herald", 5000, ScreenMessageAlign.MIDDLE_CENTER, true));
			addSpawnWithoutRespawn(BOSS_ID, new Location(147304, 142824, -15864, 32768), 0);
			openDoor(24220042);
		}
	}

	public class CountingDown extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			broadCastPacketToBothTeams(new ExShowScreenMessage("Until the end of the battle remained 1 minute", 4000, ScreenMessageAlign.MIDDLE_CENTER, true));
		}
	}

	public class BattleEnd extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			broadCastPacketToBothTeams(new ExShowScreenMessage("The battle has expired. Teleportation 1 minute.", 4000, ScreenMessageAlign.BOTTOM_RIGHT, true));
			end();
		}
	}

	public class Finish extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			unParalyzePlayers();
			cleanUp();
		}
	}

	/**
	 * @param npcId
	 * @param loc
	 * @param randomOffset
	 * @param refId		Custom instanced spawn method
	 */
	@Override
	public NpcInstance addSpawnWithoutRespawn(int npcId, Location loc, int randomOffset)
	{
		NpcInstance npc = super.addSpawnWithoutRespawn(npcId, loc, randomOffset);
		npc.addListener(_deathListener);
		return npc;
	}

	/**
	 * Handles any Teleport action of any player inside
	 */
	private class TeleportListener implements OnTeleportListener
	{
		@Override
		public void onTeleport(Player player, int x, int y, int z, Reflection reflection)
		{
			if (zonepvp.checkIfInZone(x, y, z, reflection) || peace1.checkIfInZone(x, y, z, reflection) || peace2.checkIfInZone(x, y, z, reflection))
			{
				return;
			}

			removePlayer(player, false);
			player.sendMessage("You left the area ahead of the battle and were disqualified.");
		}
	}

	/**
	 * Handles quit from the group
	 */
	private class PlayerPartyLeaveListener implements OnPlayerPartyLeaveListener
	{
		@Override
		public void onPartyLeave(Player player)
		{
			if (!isActive())
			{
				return;
			}

			Party party = player.getParty();

			if (party.size() >= 3) // when getMemberCount() >= 3 the party won't be dissolved.
			{
				removePlayer(player, false);
				return;
			}

			// else if getMemberCount() < 3 the party will be dissolved -> launching team withdrawal method
			teamWithdraw(party);
		}
	}
}