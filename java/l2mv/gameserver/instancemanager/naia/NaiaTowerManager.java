package l2mv.gameserver.instancemanager.naia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javolution.util.FastTable;
import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.taskmanager.SpawnTaskManager;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */
public final class NaiaTowerManager
{
	private static final Logger _log = LoggerFactory.getLogger(NaiaTowerManager.class);

	public static final int SPORE_FIRE_ID = 25605;
	public static final int SPORE_WATER_ID = 25606;
	public static final int SPORE_WIND_ID = 25607;
	public static final int SPORE_EARTH_ID = 25608;

	public static final int EPIDOS_FIRE_ID = 25609;
	public static final int EPIDOS_WATER_ID = 25610;
	public static final int EPIDOS_WIND_ID = 256011;
	public static final int EPIDOS_EARTH_ID = 25612;

	public static final Location CENTRAL_COLUMN = new Location(-45474, 247450, -13994);
	public static final Location TELEPORT_LOCATION = new Location(-46344, 247784, -14207);

	private static Attribute _currentAttribute = Attribute.FIRE;
	private static int _currentEpidosIndex = 0;
	private static boolean _isEpidosSpawned = false;

	private static FastTable<NpcInstance> _spores = new FastTable<>();

	public static enum Attribute
	{
		FIRE, WATER, WIND, EARTH;
	}

	private static Map<Integer, List<Player>> _groupList = new HashMap<>();
	private static Map<Integer, List<Player>> _roomsDone = new HashMap<>();
	private static Map<Integer, Long> _groupTimer = new HashMap<>();
	private static Map<Integer, List<NpcInstance>> _roomMobs;
	private static List<NpcInstance> _roomMobList;
	private static long _towerAccessible = 0;
	private static int _index = 0;
	public static HashMap<Integer, Boolean> lockedRooms;
	private static final NaiaTowerManager _instance = new NaiaTowerManager();

	public static final NaiaTowerManager getInstance()
	{
		return _instance;
	}

	private NaiaTowerManager()
	{
		if (lockedRooms == null)
		{
			lockedRooms = new HashMap<>();
			for (int i = 18494; i <= 18505; i++)
			{
				lockedRooms.put(i, false);
			}

			_roomMobs = new HashMap<>();
			for (int i = 18494; i <= 18505; i++)
			{
				_roomMobList = new ArrayList<>();
				_roomMobs.put(i, _roomMobList);
			}

			_log.info("Naia Tower Manager: Loaded 12 rooms");
		}
		ThreadPoolManager.getInstance().schedule(new GroupTowerTimer(), 30 * 1000L);
	}

	public static void startNaiaTower(Player leader)
	{
		if ((leader == null) || (_towerAccessible > System.currentTimeMillis()))
		{
			return;
		}

		for (Player member : leader.getParty().getMembers())
		{
			member.teleToLocation(new Location(-47271, 246098, -9120));
		}

		addGroupToTower(leader);
		_towerAccessible += 20 * 60 * 1000L;

		ReflectionUtils.getDoor(18250001).openMe();
	}

	private static void addGroupToTower(Player leader)
	{
		_index = _groupList.keySet().size() + 1;
		_groupList.put(_index, leader.getParty().getMembers());
		_groupTimer.put(_index, System.currentTimeMillis() + 5 * 60 * 1000L);

		leader.sendMessage("The Tower of Naia countdown has begun. You have only 5 minutes to pass each room.");
	}

	public static void updateGroupTimer(Player player)
	{
		for (int i : _groupList.keySet())
		{
			if (_groupList.get(i).contains(player))
			{
				_groupTimer.put(i, System.currentTimeMillis() + 5 * 60 * 1000L);
				player.sendMessage("Group timer has been updated");
				break;
			}
		}
	}

	public static void removeGroupTimer(Player player)
	{
		for (int i : _groupList.keySet())
		{
			if (_groupList.get(i).contains(player))
			{
				_groupList.remove(i);
				_groupTimer.remove(i);
			}
		}
	}

	public static boolean isLegalGroup(Player player)
	{
		if (_groupList == null || _groupList.isEmpty())
		{
			return false;
		}

		for (int i : _groupList.keySet())
		{
			if (_groupList.get(i).contains(player))
			{
				return true;
			}
		}

		return false;
	}

	public static void lockRoom(int npcId)
	{
		lockedRooms.put(npcId, true);
	}

	public static void unlockRoom(int npcId)
	{
		lockedRooms.put(npcId, false);
	}

	public static boolean isLockedRoom(int npcId)
	{
		return lockedRooms.get(npcId);
	}

	public static void addRoomDone(int roomId, Player player)
	{
		if (player.getParty() != null)
		{
			_roomsDone.put(roomId, player.getParty().getMembers());
		}
	}

	public static boolean isRoomDone(int roomId, Player player)
	{
		if (_roomsDone == null || _roomsDone.isEmpty() || _roomsDone.get(roomId) == null || _roomsDone.get(roomId).isEmpty())
		{
			return false;
		}

		if (_roomsDone.get(roomId).contains(player))
		{
			return true;
		}

		return false;
	}

	public static void addMobsToRoom(int roomId, List<NpcInstance> mob)
	{
		_roomMobs.put(roomId, mob);
	}

	public static List<NpcInstance> getRoomMobs(int roomId)
	{
		return _roomMobs.get(roomId);
	}

	public static void removeRoomMobs(int roomId)
	{
		_roomMobs.get(roomId).clear();
	}

	private class GroupTowerTimer extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			ThreadPoolManager.getInstance().schedule(new GroupTowerTimer(), 30 * 1000L);
			if (!_groupList.isEmpty() && !_groupTimer.isEmpty())
			{
				for (int i : _groupTimer.keySet())
				{
					if (_groupTimer.get(i) < System.currentTimeMillis())
					{
						for (Player kicked : _groupList.get(i))
						{
							kicked.teleToLocation(new Location(17656, 244328, 11595));
							kicked.sendMessage("The time has expired. You cannot stay in Tower of Naia any longer");
						}
						_groupList.remove(i);
						_groupTimer.remove(i);
					}
				}
			}
		}
	}

	public static synchronized void handleEpidosIndex(NpcInstance naiaSpore)
	{
		if ((naiaSpore == null) || _isEpidosSpawned)
		{
			return;
		}

		int sporeId = naiaSpore.getNpcId();
		switch (sporeId)
		{
		case SPORE_FIRE_ID:
			if (_currentAttribute == Attribute.FIRE)
			{
				_currentEpidosIndex += 1;
			}
			else if (_currentAttribute == Attribute.WATER)
			{
				_currentEpidosIndex += -2;
			}
			else
			{
				_currentEpidosIndex += -1;
			}
			break;
		case SPORE_WATER_ID:
			if (_currentAttribute == Attribute.WATER)
			{
				_currentEpidosIndex += 1;
			}
			else if (_currentAttribute == Attribute.FIRE)
			{
				_currentEpidosIndex += -2;
			}
			else
			{
				_currentEpidosIndex += -1;
			}
			break;
		case SPORE_WIND_ID:
			if (_currentAttribute == Attribute.WIND)
			{
				_currentEpidosIndex += 1;
			}
			else if (_currentAttribute == Attribute.EARTH)
			{
				_currentEpidosIndex += -2;
			}
			else
			{
				_currentEpidosIndex += -1;
			}
			break;
		case SPORE_EARTH_ID:
			if (_currentAttribute == Attribute.EARTH)
			{
				_currentEpidosIndex += 1;
			}
			else if (_currentAttribute == Attribute.WIND)
			{
				_currentEpidosIndex += -2;
			}
			else
			{
				_currentEpidosIndex += -1;
			}
		}

		if (_currentEpidosIndex >= 50)
		{
			for (NpcInstance spore : _spores)
			{
				if (spore != null)
				{
					notifyEpidosIndexReached(spore);
				}
			}

			ThreadPoolManager.getInstance().schedule(new SpawnEpidosTask(_currentAttribute), 3000);
			_isEpidosSpawned = true;
			_currentEpidosIndex = 0;
		}
		else
		{
			if (_currentEpidosIndex > 0)
			{
				return;
			}
			switch (sporeId)
			{
			case SPORE_FIRE_ID:
				_currentAttribute = Attribute.FIRE;
				break;
			case SPORE_WATER_ID:
				_currentAttribute = Attribute.WATER;
				break;
			case SPORE_WIND_ID:
				_currentAttribute = Attribute.WIND;
				break;
			case SPORE_EARTH_ID:
				_currentAttribute = Attribute.EARTH;
			}
		}
	}

	private static void notifyEpidosIndexReached(NpcInstance actor)
	{
		if (actor.isDead())
		{
			SpawnTaskManager.getInstance().cancelSpawnTask(actor);
		}
		actor.moveToLocation(NaiaTowerManager.CENTRAL_COLUMN, 0, false);
		ThreadPoolManager.getInstance().schedule(new DespawnTask(actor), 3000);
	}

	private static class DespawnTask implements Runnable
	{
		NpcInstance _spore = null;

		public DespawnTask(NpcInstance mob)
		{
			_spore = mob;
		}

		@Override
		public void run()
		{
			if (_spore != null)
			{
				_spore.decayMe();
			}
		}
	}

	public static synchronized void addSpore(NpcInstance naiaSpore)
	{
		int i = _spores.indexOf(naiaSpore);
		if (i != -1)
		{
			_spores.remove(i);
		}
		_spores.add(naiaSpore);
	}

	public static boolean isEpidosSpawned()
	{
		return _isEpidosSpawned;
	}

	public static void setEpidosState(boolean state)
	{
		_isEpidosSpawned = state;
	}

	private static class TeleportEpidosTask implements Runnable
	{
		NpcInstance _mob = null;

		public TeleportEpidosTask(NpcInstance mob)
		{
			_mob = mob;
		}

		@Override
		public void run()
		{
			if (_mob != null)
			{
				_mob.teleToLocation(TELEPORT_LOCATION);
			}
		}
	}

	private static class SpawnEpidosTask implements Runnable
	{
		private final Attribute _attribute;

		public SpawnEpidosTask(Attribute attribute)
		{
			_attribute = attribute;
		}

		@Override
		public void run()
		{
			int epidosId = 0;
			switch (_attribute)
			{
			case FIRE:
				epidosId = EPIDOS_FIRE_ID;
				break;
			case WATER:
				epidosId = EPIDOS_WATER_ID;
				break;
			case WIND:
				epidosId = EPIDOS_WIND_ID;
				break;
			case EARTH:
				epidosId = EPIDOS_EARTH_ID;
			}

			SimpleSpawner sp = new SimpleSpawner(epidosId);
			sp.setLoc(CENTRAL_COLUMN);
			sp.doSpawn(true);
			sp.stopRespawn();

			ThreadPoolManager.getInstance().schedule(new TeleportEpidosTask(sp.getLastSpawn()), 3000);
		}
	}
}