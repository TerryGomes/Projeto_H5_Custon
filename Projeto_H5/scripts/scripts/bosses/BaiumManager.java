package bosses;

import static l2mv.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bosses.EpicBossState.State;
import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.listener.actor.OnDeathListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.Zone;
import l2mv.gameserver.model.actor.listener.CharListenerList;
import l2mv.gameserver.model.instances.BossInstance;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.Earthquake;
import l2mv.gameserver.network.serverpackets.PlaySound;
import l2mv.gameserver.network.serverpackets.SocialAction;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.Log;
import l2mv.gameserver.utils.ReflectionUtils;
import l2mv.gameserver.utils.TimeUtils;

public class BaiumManager extends Functions implements ScriptFile, OnDeathListener
{
	private static final Logger LOG = LoggerFactory.getLogger(BaiumManager.class);

	// call Arcangels
	public static class CallArchAngel extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (SimpleSpawner spawn : _angelSpawns)
			{
				_angels.add(spawn.doSpawn(true));
			}
		}
	}

	public static class CheckLastAttack extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			if (_state.getState().equals(EpicBossState.State.ALIVE))
			{
				if (_lastAttackTime + FWB_LIMITUNTILSLEEP < System.currentTimeMillis())
				{
					sleepBaium();
				}
				else
				{
					_sleepCheckTask = ThreadPoolManager.getInstance().schedule(new CheckLastAttack(), 60000);
				}
			}
		}
	}

	// do spawn teleport cube.
	public static class CubeSpawn extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			_teleportCube = _teleportCubeSpawn.doSpawn(true);
		}
	}

	// at end of interval.
	public static class IntervalEnd extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			_state.setState(EpicBossState.State.NOTSPAWN);
			_state.update();

			// statue of Baium respawn.
			_statueSpawn.doSpawn(true);
		}
	}

	// kill pc
	public static class KillPc extends RunnableImpl
	{
		private final BossInstance _boss;
		private final Player _target;

		public KillPc(Player target, BossInstance boss)
		{
			_target = target;
			_boss = boss;
		}

		@Override
		public void runImpl() throws Exception
		{
			Skill skill = SkillTable.getInstance().getInfo(4136, 1);
			if (_target != null && skill != null)
			{
				_boss.setTarget(_target);
				_boss.doCast(skill, _target, false);
			}
		}
	}

	// Move at random on after Baium appears.
	public static class MoveAtRandom extends RunnableImpl
	{
		private final NpcInstance _npc;
		private final Location _pos;

		public MoveAtRandom(NpcInstance npc, Location pos)
		{
			_npc = npc;
			_pos = pos;
		}

		@Override
		public void runImpl() throws Exception
		{
			if (_npc.getAI().getIntention() == AI_INTENTION_ACTIVE)
			{
				_npc.moveToLocation(_pos, 0, false);
			}
		}
	}

	public static class SetMobilised extends RunnableImpl
	{
		private final BossInstance _boss;

		public SetMobilised(BossInstance boss)
		{
			_boss = boss;
		}

		@Override
		public void runImpl() throws Exception
		{
			_boss.stopImmobilized();
		}
	}

	// do social.
	public static class Social extends RunnableImpl
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
			SocialAction sa = new SocialAction(_npc.getObjectId(), _action);
			_npc.broadcastPacket(sa);
		}
	}

	// tasks.
	private static ScheduledFuture<?> _callAngelTask = null;
	private static ScheduledFuture<?> _cubeSpawnTask = null;
	private static ScheduledFuture<?> _intervalEndTask = null;
	private static ScheduledFuture<?> _killPcTask = null;
	private static ScheduledFuture<?> _mobiliseTask = null;
	private static ScheduledFuture<?> _moveAtRandomTask = null;
	private static ScheduledFuture<?> _sleepCheckTask = null;
	private static ScheduledFuture<?> _socialTask = null;
	private static ScheduledFuture<?> _socialTask2 = null;
	private static ScheduledFuture<?> _onAnnihilatedTask = null;

	private static EpicBossState _state;
	private static long _lastAttackTime = 0;

	private static NpcInstance _npcBaium;
	public static SimpleSpawner _statueSpawn = null;

	private static NpcInstance _teleportCube = null;
	private static SimpleSpawner _teleportCubeSpawn = null;

	private static List<NpcInstance> _monsters = new ArrayList<NpcInstance>();
	private static Map<Integer, SimpleSpawner> _monsterSpawn = new ConcurrentHashMap<Integer, SimpleSpawner>();

	private static List<NpcInstance> _angels = new ArrayList<NpcInstance>();
	private static List<SimpleSpawner> _angelSpawns = new ArrayList<SimpleSpawner>();

	private static Zone _zone;

	private final static int ARCHANGEL = 29021;
	private final static int BAIUM = 29020;
	private final static int BAIUM_NPC = 29025;

	private static boolean Dying = false;

	// location of arcangels.
	private final static Location[] ANGEL_LOCATION = new Location[]
	{
		new Location(113004, 16209, 10076, 60242),
		new Location(114053, 16642, 10076, 4411),
		new Location(114563, 17184, 10076, 49241),
		new Location(116356, 16402, 10076, 31109),
		new Location(115015, 16393, 10076, 32760),
		new Location(115481, 15335, 10076, 16241),
		new Location(114680, 15407, 10051, 32485),
		new Location(114886, 14437, 10076, 16868),
		new Location(115391, 17593, 10076, 55346),
		new Location(115245, 17558, 10076, 35536)
	};

	// location of teleport cube.
	private final static Location CUBE_LOCATION = new Location(115203, 16620, 10078, 0);
	private final static Location STATUE_LOCATION = new Location(115996, 17417, 10106, 41740);
	private final static int TELEPORT_CUBE = 31759;

	private final static int FWB_LIMITUNTILSLEEP = 30 * 60000;
	private final static int FWB_FIXINTERVALOFBAIUM = Config.BAIUM_DEFAULT_SPAWN_HOURS * 60 * 60000;
	private final static int FWB_RANDOMINTERVALOFBAIUM = Config.BAIUM_RANDOM_SPAWN_HOURS * 60 * 60000;

	private static void banishForeigners()
	{
		for (Player player : getPlayersInside())
		{
			player.teleToClosestTown();
		}
	}

	public static class onAnnihilated extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			sleepBaium();
		}
	}

	private synchronized static void checkAnnihilated()
	{
		if (_onAnnihilatedTask == null && isPlayersAnnihilated())
		{
			_onAnnihilatedTask = ThreadPoolManager.getInstance().schedule(new onAnnihilated(), 5000);
		}
	}

	// Archangel ascension.
	private static void deleteArchangels()
	{
		for (NpcInstance angel : _angels)
		{
			if (angel != null && angel.getSpawn() != null)
			{
				angel.getSpawn().stopRespawn();
				angel.deleteMe();
			}
		}
		_angels.clear();
	}

	private static List<Player> getPlayersInside()
	{
		return getZone().getInsidePlayers();
	}

	public static Zone getZone()
	{
		return _zone;
	}

	private void init()
	{
		_state = new EpicBossState(BAIUM);
		_zone = ReflectionUtils.getZone("[baium_epic]");

		CharListenerList.addGlobal(this);
		try
		{
			SimpleSpawner tempSpawn;

			// Statue of Baium
			_statueSpawn = new SimpleSpawner(NpcHolder.getInstance().getTemplate(BAIUM_NPC));
			_statueSpawn.setAmount(1);
			_statueSpawn.setLoc(STATUE_LOCATION);
			_statueSpawn.stopRespawn();

			// Baium
			tempSpawn = new SimpleSpawner(NpcHolder.getInstance().getTemplate(BAIUM));
			tempSpawn.setAmount(1);
			_monsterSpawn.put(BAIUM, tempSpawn);
		}
		catch (RuntimeException e)
		{
			LOG.error("Error on Baium Init", e);
		}

		// Teleport Cube
		try
		{
			NpcTemplate Cube = NpcHolder.getInstance().getTemplate(TELEPORT_CUBE);
			_teleportCubeSpawn = new SimpleSpawner(Cube);
			_teleportCubeSpawn.setAmount(1);
			_teleportCubeSpawn.setLoc(CUBE_LOCATION);
			_teleportCubeSpawn.setRespawnDelay(60);
		}
		catch (RuntimeException e)
		{
			LOG.error("Error on Baium Teleport Cube Init", e);
		}

		// Archangels
		try
		{
			NpcTemplate angel = NpcHolder.getInstance().getTemplate(ARCHANGEL);
			SimpleSpawner spawnDat;
			_angelSpawns.clear();

			// 5 random numbers of 10, no duplicates
			List<Integer> random = new ArrayList<Integer>();
			for (int i = 0; i < 5; i++)
			{
				int r = -1;
				while (r == -1 || random.contains(r))
				{
					r = Rnd.get(10);
				}
				random.add(r);
			}

			for (int i : random)
			{
				spawnDat = new SimpleSpawner(angel);
				spawnDat.setAmount(1);
				spawnDat.setLoc(ANGEL_LOCATION[i]);
				spawnDat.setRespawnDelay(300000);
				_angelSpawns.add(spawnDat);
			}
		}
		catch (RuntimeException e)
		{
			LOG.error("Error on Baium Archangels Init", e);
		}

		LOG.info("BaiumManager: State of Baium is " + _state.getState() + '.');
		if (_state.getState().equals(State.NOTSPAWN))
		{
			_statueSpawn.doSpawn(true);
		}
		else if (_state.getState().equals(State.ALIVE))
		{
			_state.setState(State.NOTSPAWN);
			_state.update();
			_statueSpawn.doSpawn(true);
		}
		else if (_state.getState().equals(State.INTERVAL) || _state.getState().equals(State.DEAD))
		{
			setIntervalEndTask();
		}

		LOG.info("BaiumManager: Next spawn date: " + TimeUtils.toSimpleFormat(_state.getRespawnDate()));
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

	@Override
	public void onDeath(Creature actor, Creature killer)
	{
		if (actor.isPlayer() && _state != null && _state.getState() == State.ALIVE && _zone != null && _zone.checkIfInZone(actor))
		{
			checkAnnihilated();
		}
		else if (actor.isNpc() && actor.getNpcId() == BAIUM)
		{
			onBaiumDie(actor);
		}
	}

	public static void onBaiumDie(Creature creature)
	{
		if (Dying)
		{
			return;
		}

		Dying = true;
		creature.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS02_D", 1, 0, creature.getLoc()));
		_state.setRespawnDate(getRespawnInterval());
		_state.setState(State.INTERVAL);
		_state.update();

		Log.add("Baium died", "bosses");

		deleteArchangels();

		_cubeSpawnTask = ThreadPoolManager.getInstance().schedule(new CubeSpawn(), 10000L);
	}

	private static long getRespawnInterval()
	{
		return (long) (Config.ALT_RAID_RESPAWN_MULTIPLIER * (FWB_FIXINTERVALOFBAIUM + Rnd.get(0, FWB_RANDOMINTERVALOFBAIUM)));
	}

	// start interval.
	private static void setIntervalEndTask()
	{
		setUnspawn();

		// init state of Baium's lair.
		if (!_state.getState().equals(State.INTERVAL))
		{
			_state.setRespawnDate(getRespawnInterval());
			_state.setState(State.INTERVAL);
			_state.update();
		}

		_intervalEndTask = ThreadPoolManager.getInstance().schedule(new IntervalEnd(), _state.getInterval());
	}

	public static void setLastAttackTime()
	{
		_lastAttackTime = System.currentTimeMillis();
	}

	// clean Baium's lair.
	public static void setUnspawn()
	{
		// eliminate players.
		banishForeigners();

		// delete monsters.
		deleteArchangels();
		for (NpcInstance mob : _monsters)
		{
			mob.getSpawn().stopRespawn();
			mob.deleteMe();
		}
		_monsters.clear();

		// delete teleport cube.
		if (_teleportCube != null)
		{
			_teleportCube.getSpawn().stopRespawn();
			_teleportCube.deleteMe();
			_teleportCube = null;
		}

		// not executed tasks is canceled.
		if (_cubeSpawnTask != null)
		{
			_cubeSpawnTask.cancel(false);
			_cubeSpawnTask = null;
		}
		if (_intervalEndTask != null)
		{
			_intervalEndTask.cancel(false);
			_intervalEndTask = null;
		}
		if (_socialTask != null)
		{
			_socialTask.cancel(false);
			_socialTask = null;
		}
		if (_mobiliseTask != null)
		{
			_mobiliseTask.cancel(false);
			_mobiliseTask = null;
		}
		if (_moveAtRandomTask != null)
		{
			_moveAtRandomTask.cancel(false);
			_moveAtRandomTask = null;
		}
		if (_socialTask2 != null)
		{
			_socialTask2.cancel(false);
			_socialTask2 = null;
		}
		if (_killPcTask != null)
		{
			_killPcTask.cancel(false);
			_killPcTask = null;
		}
		if (_callAngelTask != null)
		{
			_callAngelTask.cancel(false);
			_callAngelTask = null;
		}
		if (_sleepCheckTask != null)
		{
			_sleepCheckTask.cancel(false);
			_sleepCheckTask = null;
		}
		if (_onAnnihilatedTask != null)
		{
			_onAnnihilatedTask.cancel(false);
			_onAnnihilatedTask = null;
		}
	}

	// Baium sleeps if not attacked for 30 minutes.
	private static void sleepBaium()
	{
		setUnspawn();
		Log.add("Baium going to sleep, spawning statue", "bosses");
		_state.setState(State.NOTSPAWN);
		_state.update();

		// statue of Baium respawn.
		_statueSpawn.doSpawn(true);
	}

	public static class EarthquakeTask extends RunnableImpl
	{
		private final BossInstance baium;

		public EarthquakeTask(BossInstance baium)
		{
			this.baium = baium;
		}

		@Override
		public void runImpl() throws Exception
		{
			Earthquake eq = new Earthquake(baium.getLoc(), 40, 5);
			baium.broadcastPacket(eq);
		}
	}

	// do spawn Baium.
	public static void spawnBaium(NpcInstance npcBaium, Player awakeBy)
	{
		Dying = false;
		_npcBaium = npcBaium;

		// do spawn.
		SimpleSpawner baiumSpawn = _monsterSpawn.get(BAIUM);
		baiumSpawn.setLoc(_npcBaium.getLoc());

		// delete statue
		_npcBaium.getSpawn().stopRespawn();
		_npcBaium.deleteMe();

		final BossInstance baium = (BossInstance) baiumSpawn.doSpawn(true);
		_monsters.add(baium);

		_state.setRespawnDate(getRespawnInterval());
		_state.setState(State.ALIVE);
		_state.update();

		Log.add("Spawned Baium, awake by: " + awakeBy, "bosses");

		// set last attack time.
		setLastAttackTime();

		baium.startImmobilized();
		baium.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS02_A", 1, 0, baium.getLoc()));
		baium.broadcastPacket(new SocialAction(baium.getObjectId(), 2));

		_socialTask = ThreadPoolManager.getInstance().schedule(new Social(baium, 3), 15000);

		ThreadPoolManager.getInstance().schedule(new EarthquakeTask(baium), 25000);

		_socialTask2 = ThreadPoolManager.getInstance().schedule(new Social(baium, 1), 25000);
		_killPcTask = ThreadPoolManager.getInstance().schedule(new KillPc(awakeBy, baium), 26000);
		_callAngelTask = ThreadPoolManager.getInstance().schedule(new CallArchAngel(), 35000);
		_mobiliseTask = ThreadPoolManager.getInstance().schedule(new SetMobilised(baium), 35500);

		// move at random.
		Location pos = new Location(Rnd.get(112826, 116241), Rnd.get(15575, 16375), 10078, 0);
		_moveAtRandomTask = ThreadPoolManager.getInstance().schedule(new MoveAtRandom(baium, pos), 36000);

		_sleepCheckTask = ThreadPoolManager.getInstance().schedule(new CheckLastAttack(), 600000);
	}

	public static EpicBossState getState()
	{
		return _state;
	}

	@Override
	public void onLoad()
	{
		init();
	}

	@Override
	public void onReload()
	{
		sleepBaium();
	}

	@Override
	public void onShutdown()
	{
	}
}