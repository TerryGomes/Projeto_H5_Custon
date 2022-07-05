package l2f.gameserver.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.threading.RunnableImpl;
import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.instancemanager.games.HandysBlockCheckerManager;
import l2f.gameserver.instancemanager.games.HandysBlockCheckerManager.ArenaParticipantsHolder;
import l2f.gameserver.listener.actor.player.OnPlayerExitListener;
import l2f.gameserver.listener.actor.player.OnTeleportListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.SimpleSpawner;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.Zone;
import l2f.gameserver.model.base.TeamType;
import l2f.gameserver.model.instances.BlockInstance;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.items.PcInventory;
import l2f.gameserver.network.serverpackets.ExBasicActionList;
import l2f.gameserver.network.serverpackets.ExCubeGameChangePoints;
import l2f.gameserver.network.serverpackets.ExCubeGameCloseUI;
import l2f.gameserver.network.serverpackets.ExCubeGameEnd;
import l2f.gameserver.network.serverpackets.ExCubeGameExtendedChangePoints;
import l2f.gameserver.network.serverpackets.RelationChanged;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.tables.SkillTable;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.ReflectionUtils;

public final class BlockCheckerEngine
{
	private static final Logger _log = LoggerFactory.getLogger(BlockCheckerEngine.class);
	// The object which holds all basic members info
	private HandysBlockCheckerManager.ArenaParticipantsHolder _holder;
	// Maps to hold player of each team and his points
	private Map<Player, Integer> _redTeamPoints = new ConcurrentHashMap<Player, Integer>();
	private Map<Player, Integer> _blueTeamPoints = new ConcurrentHashMap<Player, Integer>();
	// The initial points of the event
	private int _redPoints = 15;
	private int _bluePoints = 15;
	// Current used arena
	private int _arena = -1;
	// All blocks
	private List<SimpleSpawner> _spawns = new CopyOnWriteArrayList<SimpleSpawner>();
	// Sets if the red team won the event at the end of this (used for packets)
	private boolean _isRedWinner;
	// Time when the event starts. Used on packet sending
	private long _startedTime;
	// The needed arena coordinates
	// Arena X: team1X, team1Y, team2X, team2Y, ArenaCenterX, ArenaCenterY
	private static final int[][] _arenaCoordinates =
	{
		// Arena 0 - Team 1 XY, Team 2 XY - CENTER XY
		{
			-58368,
			-62745,
			-57751,
			-62131,
			-58053,
			-62417
		},
		// Arena 1 - Team 1 XY, Team 2 XY - CENTER XY
		{
			-58350,
			-63853,
			-57756,
			-63266,
			-58053,
			-63551
		},
		// Arena 2 - Team 1 XY, Team 2 XY - CENTER XY
		{
			-57194,
			-63861,
			-56580,
			-63249,
			-56886,
			-63551
		},
		// Arena 3 - Team 1 XY, Team 2 XY - CENTER XY
		{
			-57200,
			-62727,
			-56584,
			-62115,
			-56850,
			-62391
		}
	};
	// Common z coordinate
	private static final int _zCoord = -2405;
	// Girl Npc
	private NpcInstance _girlNpc;
	// List of dropped items in event (for later deletion)
	private List<ItemInstance> _drops = new ArrayList<ItemInstance>();
	// Default arena
	private static final byte DEFAULT_ARENA = -1;
	// Event is started
	private boolean _isStarted = false;
	// Event end
	private ScheduledFuture<?> _task;
	// Preserve from exploit reward by logging out
	private boolean _abnormalEnd = false;
	private final String[] zoneNames =
	{
		"[block_checker_1]",
		"[block_checker_2]",
		"[block_checker_3]",
		"[block_checker_4]"
	};

	public BlockCheckerEngine(HandysBlockCheckerManager.ArenaParticipantsHolder holder, int arena)
	{
		_holder = holder;
		if (arena > -1 && arena < 4)
		{
			_arena = arena;
		}

		for (Player player : holder.getRedPlayers())
		{
			_redTeamPoints.put(player, 0);
		}
		for (Player player : holder.getBluePlayers())
		{
			_blueTeamPoints.put(player, 0);
		}
	}

	/**
	 * Updates the player holder before the event starts
	 * to synchronize all info
	 * @param holder
	 */
	public void updatePlayersOnStart(ArenaParticipantsHolder holder)
	{
		_holder = holder;
	}

	/**
	 * Returns the current holder object of this
	 * object engine
	 * @return HandysBlockCheckerManager.ArenaParticipantsHolder
	 */
	public ArenaParticipantsHolder getHolder()
	{
		return _holder;
	}

	/**
	 * Will return the id of the arena used
	 * by this event
	 * @return false;
	 */
	public int getArena()
	{
		return _arena;
	}

	/**
	 * Returns the time when the event
	 * started
	 * @return long
	 */
	public long getStarterTime()
	{
		return _startedTime;
	}

	/**
	 * Returns the current red team points
	 * @return int
	 */
	public int getRedPoints()
	{
		synchronized (this)
		{
			return _redPoints;
		}
	}

	/**
	 * Returns the current blue team points
	 * @return int
	 */
	public int getBluePoints()
	{
		synchronized (this)
		{
			return _bluePoints;
		}
	}

	/**
	 * Returns the player points
	 * @param player
	 * @param isRed
	 * @return int
	 */
	public int getPlayerPoints(Player player, boolean isRed)
	{
		if (!_redTeamPoints.containsKey(player) && !_blueTeamPoints.containsKey(player))
		{
			return 0;
		}

		if (isRed)
		{
			return _redTeamPoints.get(player);
		}
		else
		{
			return _blueTeamPoints.get(player);
		}
	}

	/**
	 * Increases player points for his teams
	 * @param player
	 * @param team
	 */
	public synchronized void increasePlayerPoints(Player player, int team)
	{
		if (player == null)
		{
			return;
		}

		if (team == 0)
		{
			int points = getPlayerPoints(player, true) + 1;
			_redTeamPoints.put(player, points);
			_redPoints++;
			_bluePoints--;
		}
		else
		{
			int points = getPlayerPoints(player, false) + 1;
			_blueTeamPoints.put(player, points);
			_bluePoints++;
			_redPoints--;
		}
	}

	/**
	 * Will add a new drop into the list of
	 * dropped items
	 * @param item
	 */
	public void addNewDrop(ItemInstance item)
	{
		if (item != null)
		{
			_drops.add(item);
		}
	}

	/**
	 * Will return true if the event is alredy
	 * started
	 * @return boolean
	 */
	public boolean isStarted()
	{
		return _isStarted;
	}

	/**
	 * Will send all packets for the event members with
	 * the relation info
	 */
	private void broadcastRelationChanged(Player plr)
	{
		for (Player p : _holder.getAllPlayers())
		{
			p.sendPacket(RelationChanged.update(plr, p, plr));
		}
	}

	/**
	 * Called when a there is an empty team. The event
	 * will end.
	 */
	public void endEventAbnormally()
	{
		try
		{
			synchronized (this)
			{
				_isStarted = false;

				if (_task != null)
				{
					_task.cancel(true);
				}

				_abnormalEnd = true;

				ThreadPoolManager.getInstance().execute(new EndEvent());
			}
		}
		catch (RuntimeException e)
		{
			_log.error("Couldnt end Block Checker event at " + _arena, e);
		}
	}

	public void clearArena(String zoneName)
	{
		Zone zone = ReflectionUtils.getZone(zoneName);
		if (zone != null)
		{
			for (Creature cha : zone.getObjects())
			{
				if (cha.isPlayer() && cha.getPlayer().getBlockCheckerArena() < 0)
				{
					cha.getPlayer().teleToClosestTown();
				}
				else if (cha.isNpc())
				{
					cha.deleteMe();
				}
			}
		}
	}

	/**
	 * This inner class set ups all player
	 * and arena parameters to start the event
	 */
	public class StartEvent extends RunnableImpl
	{
		// In event used skills
		private Skill _freeze, _transformationRed, _transformationBlue;
		// Common and unparametizer packet
		private final ExCubeGameCloseUI _closeUserInterface = new ExCubeGameCloseUI();

		public StartEvent()
		{
			// Initialize all used skills
			_freeze = SkillTable.getInstance().getInfo(6034, 1);
			_transformationRed = SkillTable.getInstance().getInfo(6035, 1);
			_transformationBlue = SkillTable.getInstance().getInfo(6036, 1);
		}

		/**
		 * Will set up all player parameters and
		 * port them to their respective location
		 * based on their teams
		 */
		private void setUpPlayers()
		{
			// Set current arena as being used
			HandysBlockCheckerManager.getInstance().setArenaBeingUsed(_arena);
			// Initialize packets avoiding create a new one per player
			_redPoints = _spawns.size() / 2;
			_bluePoints = _spawns.size() / 2;
			final ExCubeGameChangePoints initialPoints = new ExCubeGameChangePoints(300, _bluePoints, _redPoints);
			ExCubeGameExtendedChangePoints clientSetUp;

			for (Player player : _holder.getAllPlayers())
			{
				if (player == null)
				{
					continue;
				}

				player.addListener(_listener);

				// Send the secret client packet set up
				boolean isRed = _holder.getRedPlayers().contains(player);

				clientSetUp = new ExCubeGameExtendedChangePoints(300, _bluePoints, _redPoints, isRed, player, 0);
				player.sendPacket(clientSetUp);

				player.sendActionFailed();

				// Teleport Player - Array access
				// Team 0 * 2 = 0; 0 = 0, 0 + 1 = 1.
				// Team 1 * 2 = 2; 2 = 2, 2 + 1 = 3
				int tc = _holder.getPlayerTeam(player) * 2;
				// Get x and y coordinates
				int x = _arenaCoordinates[_arena][tc];
				int y = _arenaCoordinates[_arena][tc + 1];
				player.teleToLocation(x, y, _zCoord);
				// Set the player team
				if (isRed)
				{
					_redTeamPoints.put(player, 0);
					player.setTeam(TeamType.RED);
				}
				else
				{
					_blueTeamPoints.put(player, 0);
					player.setTeam(TeamType.BLUE);
				}
				player.getEffectList().stopAllEffects();

				if (player.getPet() != null)
				{
					player.getPet().unSummon();
				}

				// Give the player start up effects
				// Freeze
				_freeze.getEffects(player, player, false, false);
				// Tranformation
				if (_holder.getPlayerTeam(player) == 0)
				{
					_transformationRed.getEffects(player, player, false, false);
				}
				else
				{
					_transformationBlue.getEffects(player, player, false, false);
				}
				// Set the current player arena
				player.setBlockCheckerArena((byte) _arena);
				// Send needed packets
				player.sendPacket(initialPoints);
				player.sendPacket(_closeUserInterface);
				// ExBasicActionList
				player.sendPacket(new ExBasicActionList(player));
				broadcastRelationChanged(player);
				player.broadcastCharInfo();
			}
		}

		@Override
		public void runImpl()
		{
			// Wrong arena passed, stop event
			if (_arena == -1)
			{
				_log.error("Couldnt set up the arena Id for the Block Checker event, cancelling event...");
				return;
			}
			if (isStarted())
			{
				return;
			}
			clearArena(zoneNames[_arena]);
			_isStarted = true;
			// Spawn the blocks
			ThreadPoolManager.getInstance().execute(new SpawnRound(16, 1));
			// Start up player parameters
			setUpPlayers();
			// Set the started time
			_startedTime = System.currentTimeMillis() + 300000;
		}
	}

	/**
	 * This class spawns the second round of boxes
	 * and schedules the event end
	 */
	class SpawnRound extends RunnableImpl
	{
		int _numOfBoxes;
		int _round;

		SpawnRound(int numberOfBoxes, int round)
		{
			_numOfBoxes = numberOfBoxes;
			_round = round;
		}

		@Override
		public void runImpl()
		{
			if (!_isStarted)
			{
				return;
			}

			switch (_round)
			{
			case 1:
				// Schedule second spawn round
				_task = ThreadPoolManager.getInstance().schedule(new SpawnRound(20, 2), 60000);
				break;
			case 2:
				// Schedule third spawn round
				_task = ThreadPoolManager.getInstance().schedule(new SpawnRound(14, 3), 60000);
				break;
			case 3:
				// Schedule Event End Count Down
				_task = ThreadPoolManager.getInstance().schedule(new CountDown(), 175000);
				break;
			}
			// random % 2, if == 0 will spawn a red block
			// if != 0, will spawn a blue block
			byte random = 2;
			// common template
			final NpcTemplate template = NpcHolder.getInstance().getTemplate(18672);
			// Spawn blocks
			try
			{
				// Creates 50 new blocks
				for (int i = 0; i < _numOfBoxes; i++)
				{
					SimpleSpawner spawn = new SimpleSpawner(template);
					spawn.setLocx(_arenaCoordinates[_arena][4] + Rnd.get(-400, 400));
					spawn.setLocy(_arenaCoordinates[_arena][5] + Rnd.get(-400, 400));
					spawn.setLocz(_zCoord);
					spawn.setAmount(1);
					spawn.setHeading(1);
					spawn.setRespawnDelay(1);
					BlockInstance blockInstance = (BlockInstance) spawn.doSpawn(true);
					blockInstance.setRed(random % 2 == 0);

					_spawns.add(spawn);
					random++;
				}
			}
			catch (RuntimeException e)
			{
				_log.error("Error while Spawning Block Checker!", e);
			}

			// Spawn the block carrying girl
			if (_round == 1 || _round == 2)
			{
				NpcTemplate girl = NpcHolder.getInstance().getTemplate(18676);
				try
				{
					final SimpleSpawner girlSpawn = new SimpleSpawner(girl);
					girlSpawn.setLocx(_arenaCoordinates[_arena][4] + Rnd.get(-400, 400));
					girlSpawn.setLocy(_arenaCoordinates[_arena][5] + Rnd.get(-400, 400));
					girlSpawn.setLocz(_zCoord);
					girlSpawn.setAmount(1);
					girlSpawn.setHeading(1);
					girlSpawn.setRespawnDelay(1);
					girlSpawn.doSpawn(true);
					girlSpawn.init();
					_girlNpc = girlSpawn.getLastSpawn();
					// Schedule his deletion after 9 secs of spawn
					ThreadPoolManager.getInstance().schedule(new RunnableImpl()
					{
						@Override
						public void runImpl() throws Exception
						{
							if (_girlNpc == null)
							{
								return;
							}
							_girlNpc.deleteMe();
						}
					}, 9000);
				}
				catch (RuntimeException e)
				{
					_log.warn("Couldnt Spawn Block Checker NPCs! Wrong instance type at npc table?", e);
				}
			}

			_redPoints += _numOfBoxes / 2;
			_bluePoints += _numOfBoxes / 2;

			int timeLeft = (int) ((getStarterTime() - System.currentTimeMillis()) / 1000);
			ExCubeGameChangePoints changePoints = new ExCubeGameChangePoints(timeLeft, getBluePoints(), getRedPoints());
			getHolder().broadCastPacketToTeam(changePoints);
		}
	}

	class CountDown extends RunnableImpl
	{
		private int seconds = 5;

		@Override
		public void runImpl() throws Exception
		{
			switch (seconds)
			{
			case 5:
				_holder.broadCastPacketToTeam(new SystemMessage(SystemMessage.BLOCK_CHECKER_WILL_END_IN_5_SECONDS));
				break;
			case 4:
				_holder.broadCastPacketToTeam(new SystemMessage(SystemMessage.BLOCK_CHECKER_WILL_END_IN_4_SECONDS));
				break;
			case 3:
				_holder.broadCastPacketToTeam(new SystemMessage(SystemMessage.BLOCK_CHECKER_WILL_END_IN_3_SECONDS));
				break;
			case 2:
				_holder.broadCastPacketToTeam(new SystemMessage(SystemMessage.BLOCK_CHECKER_WILL_END_IN_2_SECONDS));
				break;
			case 1:
				_holder.broadCastPacketToTeam(new SystemMessage(SystemMessage.BLOCK_CHECKER_WILL_END_IN_1_SECOND));
				break;
			}

			if (--seconds > 0)
			{
				ThreadPoolManager.getInstance().schedule(this, 1000L);
			}
			else
			{
				ThreadPoolManager.getInstance().execute(new EndEvent());
			}
		}
	}

	/**
	 * This class erase all event parameters on player
	 * and port them back near Handy. Also, unspawn
	 * blocks, runs a garbage collector and set as free
	 * the used arena
	 */
	class EndEvent extends RunnableImpl
	{
		// Garbage collector and arena free setter
		private void clearMe()
		{
			HandysBlockCheckerManager.getInstance().clearPaticipantQueueByArenaId(_arena);
			for (Player player : _holder.getAllPlayers())
			{
				if (player == null)
				{
					continue;
				}

				player.removeListener(_listener);
			}
			_holder.clearPlayers();
			_blueTeamPoints.clear();
			_redTeamPoints.clear();
			HandysBlockCheckerManager.getInstance().setArenaFree(_arena);

			for (SimpleSpawner spawn : _spawns)
			{
				spawn.deleteAll();
			}

			_spawns.clear();

			for (ItemInstance item : _drops)
			{
				// npe
				// a player has it, it will be deleted later
				if ((item == null) || !item.isVisible() || item.getOwnerId() != 0)
				{
					continue;
				}

				item.deleteMe();
			}
			_drops.clear();
		}

		/**
		 * Reward players after event.
		 * Tie - No Reward
		 */
		private void rewardPlayers()
		{
			if (_redPoints == _bluePoints)
			{
				return;
			}

			_isRedWinner = _redPoints > _bluePoints ? true : false;

			if (_isRedWinner)
			{
				rewardAsWinner(true);
				rewardAsLooser(false);
				SystemMessage msg = new SystemMessage(SystemMessage.THE_C1_TEAM_HAS_WON).addString("Red Team");

				_holder.broadCastPacketToTeam(msg);
			}
			else if (_bluePoints > _redPoints)
			{
				rewardAsWinner(false);
				rewardAsLooser(true);
				SystemMessage msg = new SystemMessage(SystemMessage.THE_C1_TEAM_HAS_WON).addString("Blue Team");
				_holder.broadCastPacketToTeam(msg);
			}
			else
			{
				rewardAsLooser(true);
				rewardAsLooser(false);
			}
		}

		private void addRewardItemWithMessage(int id, long count, Player player)
		{
			player.getInventory().addItem(id, (long) (count * Config.ALT_RATE_COINS_REWARD_BLOCK_CHECKER), "Block Checked Reward");
			player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_OBTAINED_S2_S1).addItemName(id).addNumber(count));
		}

		/**
		 * Reward the speicifed team as a winner team
		 * 1) Higher score - 8 extra
		 * 2) Higher score - 5 extra
		 * @param isRed
		 */
		private void rewardAsWinner(boolean isRed)
		{
			Map<Player, Integer> tempPoints = isRed ? _redTeamPoints : _blueTeamPoints;

			// Main give
			for (Player pc : tempPoints.keySet())
			{
				if (pc == null)
				{
					continue;
				}

				if (tempPoints.get(pc) >= 10)
				{
					addRewardItemWithMessage(13067, 2, pc);
				}
				else
				{
					tempPoints.remove(pc);
				}
			}

			int first = 0, second = 0;
			Player winner1 = null, winner2 = null;
			for (Player pc : tempPoints.keySet())
			{
				int pcPoints = tempPoints.get(pc);
				if (pcPoints > first)
				{
					// Move old data
					second = first;
					winner2 = winner1;
					// Set new data
					first = pcPoints;
					winner1 = pc;
				}
				else if (pcPoints > second)
				{
					second = pcPoints;
					winner2 = pc;
				}
			}
			if (winner1 != null)
			{
				addRewardItemWithMessage(13067, 8, winner1);
			}
			if (winner2 != null)
			{
				addRewardItemWithMessage(13067, 5, winner2);
			}
		}

		/**
		 * Will reward the looser team with the
		 * predefined rewards
		 * Player got >= 10 points: 2 coins
		 * Player got < 10 points: 0 coins
		 * @param isRed
		 */
		private void rewardAsLooser(boolean isRed)
		{
			Map<Player, Integer> tempPoints = isRed ? _redTeamPoints : _blueTeamPoints;

			for (Player player : tempPoints.keySet())
			{
				if (player != null && tempPoints.get(player) >= 10)
				{
					addRewardItemWithMessage(13067, 2, player);
				}
			}
		}

		/**
		 * Telport players back, give status back and
		 * send final packet
		 */
		private void setPlayersBack()
		{
			final ExCubeGameEnd end = new ExCubeGameEnd(_isRedWinner);

			for (Player player : _holder.getAllPlayers())
			{
				if (player == null)
				{
					continue;
				}

				player.getEffectList().stopAllEffects();
				// Remove team aura
				player.setTeam(TeamType.NONE);
				// Set default arena
				player.setBlockCheckerArena(DEFAULT_ARENA);
				// Remove the event items
				PcInventory inv = player.getInventory();
				inv.destroyItemByItemId(13787, inv.getCountOf(13787), "Block Checker");
				inv.destroyItemByItemId(13788, inv.getCountOf(13788), "Block Checker");
				broadcastRelationChanged(player);
				// Teleport Back
				player.teleToLocation(-57478, -60367, -2370);
				// Send end packet
				player.sendPacket(end);
				player.broadcastCharInfo();
			}
		}

		@Override
		public void runImpl()
		{
			if (!_abnormalEnd)
			{
				rewardPlayers();
			}
			_isStarted = false;
			setPlayersBack();
			clearMe();
			_abnormalEnd = false;
		}
	}

	private OnExitPlayerListener _listener = new OnExitPlayerListener();

	private class OnExitPlayerListener implements OnTeleportListener, OnPlayerExitListener
	{
		private boolean _isExit = false;

		@Override
		public void onTeleport(Player player, int x, int y, int z, Reflection reflection)
		{
			if (_isExit)
			{
				return;
			}
			onPlayerExit(player);
		}

		@Override
		public void onPlayerExit(Player player)
		{
			if (player.getBlockCheckerArena() < 0)
			{
				return;
			}
			_isExit = true;
			player.teleToLocation(-57478, -60367, -2370);
			player.setTransformation(0);
			player.getEffectList().stopAllEffects();
			int arena = player.getBlockCheckerArena();
			int team = HandysBlockCheckerManager.getInstance().getHolder(arena).getPlayerTeam(player);
			HandysBlockCheckerManager.getInstance().removePlayer(player, arena, team);
			// Remove team aura
			player.setTeam(TeamType.NONE);
			player.broadcastCharInfo();

			// Remove the event items
			PcInventory inv = player.getInventory();
			inv.destroyItemByItemId(13787, inv.getCountOf(13787), "Block Checker");
			inv.destroyItemByItemId(13788, inv.getCountOf(13788), "Block Checker");
		}
	}

}