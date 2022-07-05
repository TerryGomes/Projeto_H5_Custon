package bosses;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2f.commons.threading.RunnableImpl;
import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.geodata.GeoEngine;
import l2f.gameserver.instancemanager.ReflectionManager;
import l2f.gameserver.listener.actor.OnDeathListener;
import l2f.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Zone;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.FlyToLocation;
import l2f.gameserver.network.serverpackets.FlyToLocation.FlyType;
import l2f.gameserver.network.serverpackets.SocialAction;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.Log;
import l2f.gameserver.utils.PositionUtils;
import l2f.gameserver.utils.ReflectionUtils;

public class BaylorManager extends Functions implements ScriptFile
{
	public static NpcInstance spawn(Location loc, int npcId)
	{
		NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
		NpcInstance npc = template.getNewInstance();
		npc.setSpawnedLoc(loc);
		npc.setHeading(loc.h);
		npc.setLoc(loc);
		npc.setReflection(currentReflection);
		npc.spawnMe();
		return npc;
	}

	private static class ActivityTimeEnd extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			setIntervalEndTask();
		}
	}

	private static class BaylorSpawn extends RunnableImpl
	{
		private final int _npcId;
		private final Location _pos = new Location(153569, 142075, -12711, 44732);

		public BaylorSpawn(int npcId)
		{
			_npcId = npcId;
		}

		@Override
		public void runImpl() throws Exception
		{
			switch (_npcId)
			{
			case CrystalPrisonGuard:

				Reflection ref = ReflectionManager.getInstance().get(currentReflection);
				for (int doorId : doors)
				{
					ref.openDoor(doorId);
				}

				for (int i = 0; i < _crystalineLocation.length; i++)
				{
					_crystaline[i] = spawn(_crystalineLocation[i], CrystalPrisonGuard);
					_crystaline[i].setRunning();
					_crystaline[i].moveToLocation(_pos, 300, false);
					ThreadPoolManager.getInstance().schedule(new Social(_crystaline[i], 2), 15000);
				}

				break;
			case Baylor:
				Dying = false;

				_baylor = spawn(new Location(153569, 142075, -12732, 59864), Baylor);
				_baylor.addListener(BaylorDeathListener.getInstance());

				_state.setRespawnDate(getRespawnInterval() + FWBA_ACTIVITYTIMEOFMOBS);
				_state.setState(EpicBossState.State.ALIVE);
				_state.update();

				if (_socialTask != null)
				{
					_socialTask.cancel(false);
					_socialTask = null;
				}
				_socialTask = ThreadPoolManager.getInstance().schedule(new Social(_baylor, 1), 500);

				if (_endSceneTask != null)
				{
					_endSceneTask.cancel(false);
					_endSceneTask = null;
				}
				_endSceneTask = ThreadPoolManager.getInstance().schedule(new EndScene(), 23000);

				if (_activityTimeEndTask != null)
				{
					_activityTimeEndTask.cancel(false);
					_activityTimeEndTask = null;
				}
				_activityTimeEndTask = ThreadPoolManager.getInstance().schedule(new ActivityTimeEnd(), FWBA_ACTIVITYTIMEOFMOBS);

				break;
			}
		}
	}

	// Interval end.
	private static class IntervalEnd extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			_state.setState(EpicBossState.State.NOTSPAWN);
			_state.update();
		}
	}

	private static class Social extends RunnableImpl
	{
		private final int _action;
		private final NpcInstance _npc;

		public Social(NpcInstance npc, int actionId)
		{
			_npc = npc;
			_action = actionId;
		}

		@Override
		public void runImpl() throws Exception
		{
			_npc.broadcastPacket(new SocialAction(_npc.getObjectId(), _action));
		}
	}

	private static class EndScene extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (Player player : getPlayersInside())
			{
				player.unblock();
				if (_baylor != null)
				{
					double angle = PositionUtils.convertHeadingToDegree(_baylor.getHeading());
					double radian = Math.toRadians(angle - 90);
					int x1 = -(int) (Math.sin(radian) * 600);
					int y1 = (int) (Math.cos(radian) * 600);
					Location flyLoc = GeoEngine.moveCheck(player.getX(), player.getY(), player.getZ(), player.getX() + x1, player.getY() + y1, player.getGeoIndex());
					player.setLoc(flyLoc);
					player.broadcastPacket(new FlyToLocation(player, flyLoc, FlyType.THROW_HORIZONTAL));
				}
			}
			for (NpcInstance npc : _crystaline)
			{
				if (npc != null)
				{
					npc.reduceCurrentHp(npc.getMaxHp() + 1, npc, null, true, true, false, false, false, false, false);
				}
			}
		}
	}

	private static final int Baylor = 29099;
	private static final int CrystalPrisonGuard = 29100;
	private static final int Parme = 32271;
	private static final int Oracle = 32273;

	private static final Location _crystalineLocation[] =
	{
		new Location(154404, 140596, -12711, 44732),
		new Location(153574, 140402, -12711, 44732),
		new Location(152105, 141230, -12711, 44732),
		new Location(151877, 142095, -12711, 44732),
		new Location(152109, 142920, -12711, 44732),
		new Location(152730, 143555, -12711, 44732),
		new Location(154439, 143538, -12711, 44732),
		new Location(155246, 142068, -12711, 44732)
	};

	private static final Location _baylorChestLocation[] =
	{
		new Location(153763, 142075, -12741, 64792),
		new Location(153701, 141942, -12741, 57739),
		new Location(153573, 141894, -12741, 49471),
		new Location(153445, 141945, -12741, 41113),
		new Location(153381, 142076, -12741, 32767),
		new Location(153441, 142211, -12741, 25730),
		new Location(153573, 142260, -12741, 16185),
		new Location(153706, 142212, -12741, 7579),
		new Location(153571, 142860, -12741, 16716),
		new Location(152783, 142077, -12741, 32176),
		new Location(153571, 141274, -12741, 49072),
		new Location(154365, 142073, -12741, 64149),
		new Location(154192, 142697, -12741, 7894),
		new Location(152924, 142677, -12741, 25072),
		new Location(152907, 141428, -12741, 39590),
		new Location(154243, 141411, -12741, 55500)
	};

	private static final int[] doors =
	{
		24220009,
		24220011,
		24220012,
		24220014,
		24220015,
		24220016,
		24220017,
		24220019
	};

	// Instance of monsters
	private static NpcInstance[] _crystaline = new NpcInstance[8];

	private static NpcInstance _baylor;
	// Tasks
	private static ScheduledFuture<?> _intervalEndTask = null;
	private static ScheduledFuture<?> _activityTimeEndTask = null;
	private static ScheduledFuture<?> _socialTask = null;
	private static ScheduledFuture<?> _endSceneTask = null;

	// State of baylor's lair.
	private static boolean _isAlreadyEnteredOtherParty = false;

	private static EpicBossState _state;
	private static Zone _zone;

	private static final int FWBA_ACTIVITYTIMEOFMOBS = 120 * 60000;
	private static final int FWBA_FIXINTERVALOFBAYLORSPAWN = Config.FIXINTERVALOFBAYLORSPAWN_HOUR * 60 * 60000;
	private static final int FWBA_RANDOMINTERVALOFBAYLORSPAWN = Config.RANDOMINTERVALOFBAYLORSPAWN * 60 * 60000;

	private static final boolean FWBA_ENABLESINGLEPLAYER = false;

	private static boolean Dying = false;

	private static int currentReflection;

	// Whether it is permitted to enter the baylor's lair is confirmed.
	public static int canIntoBaylorLair(Player pc)
	{
		if (pc.isGM())
		{
			return 0;
		}
		if (!FWBA_ENABLESINGLEPLAYER && !pc.isInParty())
		{
			return 4;
		}
		else if (_isAlreadyEnteredOtherParty)
		{
			return 2;
		}
		else if (_state.getState().equals(EpicBossState.State.NOTSPAWN))
		{
			return 0;
		}
		else if (_state.getState().equals(EpicBossState.State.ALIVE) || _state.getState().equals(EpicBossState.State.DEAD))
		{
			return 1;
		}
		else if (_state.getState().equals(EpicBossState.State.INTERVAL))
		{
			return 3;
		}
		else
		{
			return 0;
		}
	}

	private synchronized static void checkAnnihilated()
	{
		if (isPlayersAnnihilated())
		{
			setIntervalEndTask();
		}
	}

	// Teleporting player to baylor's lair.
	public synchronized static void entryToBaylorLair(Player pc)
	{
		currentReflection = pc.getReflectionId();
		_zone.setReflection(pc.getReflection());

		// Synerge - When they enter baylor cave, reset the instance time to 30 minutes
		if (pc.getReflection() != null)
		{
			pc.getReflection().startCollapseTimer(30 * 60 * 1000L);
		}

		ReflectionManager.getInstance().get(currentReflection).closeDoor(24220008);
		ThreadPoolManager.getInstance().schedule(new BaylorSpawn(CrystalPrisonGuard), 20000);
		ThreadPoolManager.getInstance().schedule(new BaylorSpawn(Baylor), 40000);

		if (pc.getParty() == null)
		{
			pc.teleToLocation(153569 + Rnd.get(-80, 80), 142075 + Rnd.get(-80, 80), -12732);
			pc.block();
		}
		else
		{
			List<Player> members = new ArrayList<Player>(); // list of member of teleport candidate.
			for (Player mem : pc.getParty().getMembers())
			{
				// teleporting it within alive and the range of recognition of the leader of the party.
				if (!mem.isDead() && mem.isInRange(pc, 1500))
				{
					members.add(mem);
				}
			}
			for (Player mem : members)
			{
				mem.teleToLocation(153569 + Rnd.get(-80, 80), 142075 + Rnd.get(-80, 80), -12732);
				mem.block();
			}
		}
		_isAlreadyEnteredOtherParty = true;
	}

	private static List<Player> getPlayersInside()
	{
		List<Player> result = new ArrayList<Player>();
		for (Player player : getZone().getInsidePlayers())
		{
			result.add(player);
		}
		return result;
	}

	private static int getRespawnInterval()
	{
		return (int) (Config.ALT_RAID_RESPAWN_MULTIPLIER * (FWBA_FIXINTERVALOFBAYLORSPAWN + Rnd.get(0, FWBA_RANDOMINTERVALOFBAYLORSPAWN)));
	}

	public static Zone getZone()
	{
		return _zone;
	}

	private static void init()
	{
		_state = new EpicBossState(Baylor);
		_zone = ReflectionUtils.getZone("[baylor_epic]");
		_zone.addListener(BaylorZoneListener.getInstance());

		_isAlreadyEnteredOtherParty = false;

		Log.add("BaylorManager : State of Baylor is " + _state.getState() + ".", "bosses");
		if (!_state.getState().equals(EpicBossState.State.NOTSPAWN))
		{
			setIntervalEndTask();
		}

		Date dt = new Date(_state.getRespawnDate());
		Log.add("BaylorManager : Next spawn date of Baylor is " + dt + ".", "bosses");
		Log.add("BaylorManager : Init BaylorManager.", "bosses");
	}

	private static boolean isPlayersAnnihilated()
	{
		for (Player pc : getPlayersInside())
		{
			if (!pc.isDead())
			{
				return false;
			}
		}
		return true;
	}

	private static void onBaylorDie()
	{
		if (Dying)
		{
			return;
		}

		Dying = true;
		_state.setRespawnDate(getRespawnInterval());
		_state.setState(EpicBossState.State.INTERVAL);
		_state.update();

		Log.add("Baylor died", "bosses");

		spawn(_baylorChestLocation[Rnd.get(_baylorChestLocation.length)], 29116);

		spawn(new Location(153570, 142067, -9727, 55500), Parme);
		spawn(new Location(153569, 142075, -12732, 55500), Oracle);

		startCollapse();
	}

	private static class BaylorZoneListener implements OnZoneEnterLeaveListener
	{
		private static OnZoneEnterLeaveListener _instance = new BaylorZoneListener();

		public static OnZoneEnterLeaveListener getInstance()
		{
			return _instance;
		}

		@Override
		public void onZoneEnter(Zone zone, Creature actor)
		{
			if (actor.isPlayer())
			{
				actor.addListener(PlayerDeathListener.getInstance());
			}
		}

		@Override
		public void onZoneLeave(Zone zone, Creature actor)
		{
			if (actor.isPlayer())
			{
				actor.removeListener(PlayerDeathListener.getInstance());
			}
		}

		@Override
		public void onEquipChanged(Zone zone, Creature actor)
		{
		}
	}

	private static class PlayerDeathListener implements OnDeathListener
	{
		private static OnDeathListener _instance = new PlayerDeathListener();

		public static OnDeathListener getInstance()
		{
			return _instance;
		}

		@Override
		public void onDeath(Creature actor, Creature killer)
		{
			checkAnnihilated();
		}
	}

	private static class BaylorDeathListener implements OnDeathListener
	{
		private static OnDeathListener _instance = new BaylorDeathListener();

		public static OnDeathListener getInstance()
		{
			return _instance;
		}

		@Override
		public void onDeath(Creature actor, Creature killer)
		{
			onBaylorDie();
		}
	}

	// Task of interval of baylor spawn.
	private static void setIntervalEndTask()
	{
		setUnspawn();

		if (_state.getState().equals(EpicBossState.State.ALIVE))
		{
			_state.setState(EpicBossState.State.NOTSPAWN);
			_state.update();
			return;
		}

		if (!_state.getState().equals(EpicBossState.State.INTERVAL))
		{
			_state.setRespawnDate(getRespawnInterval());
			_state.setState(EpicBossState.State.INTERVAL);
			_state.update();
		}

		_intervalEndTask = ThreadPoolManager.getInstance().schedule(new IntervalEnd(), _state.getInterval());
	}

	// Clean up Baylor's lair.
	private static void setUnspawn()
	{
		if (!_isAlreadyEnteredOtherParty)
		{
			return;
		}
		_isAlreadyEnteredOtherParty = false;

		startCollapse();

		if (_baylor != null)
		{
			_baylor.deleteMe();
		}
		_baylor = null;

		for (NpcInstance npc : _crystaline)
		{
			if (npc != null)
			{
				npc.deleteMe();
			}
		}

		if (_intervalEndTask != null)
		{
			_intervalEndTask.cancel(false);
			_intervalEndTask = null;
		}
		if (_activityTimeEndTask != null)
		{
			_activityTimeEndTask.cancel(false);
			_activityTimeEndTask = null;
		}
	}

	private static void startCollapse()
	{
		if (currentReflection > 0)
		{
			Reflection reflection = ReflectionManager.getInstance().get(currentReflection);
			if (reflection != null)
			{
				reflection.startCollapseTimer(300000);
			}
			currentReflection = 0;
		}
	}

	@Override
	public void onLoad()
	{
		init();
	}

	@Override
	public void onReload()
	{
		setUnspawn();
	}

	@Override
	public void onShutdown()
	{
	}
}