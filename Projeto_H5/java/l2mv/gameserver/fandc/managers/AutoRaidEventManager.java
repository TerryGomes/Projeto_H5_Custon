//package l2mv.gameserver.fandc.managers;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//import java.util.concurrent.Future;
//
//import l2mv.commons.util.Rnd;
//import l2mv.gameserver.Announcements;
//import l2mv.gameserver.Config;
//import l2mv.gameserver.ThreadPoolManager;
//import l2mv.gameserver.data.xml.holder.NpcHolder;
//import l2mv.gameserver.instancemanager.ReflectionManager;
//import l2mv.gameserver.model.instances.MonsterInstance;
//import l2mv.gameserver.templates.npc.NpcTemplate;
//import l2mv.gameserver.utils.Location;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * Manager to handle all the event of the automatic raid that appears once every day in a random location
// *
// * @author Prims
// */
//public class AutoRaidEventManager
//{
//	protected static final Logger _log = LoggerFactory.getLogger(AutoRaidEventManager.class);
//
//	// Locations
//	protected static final List<RaidLocation> LOCATIONS = new ArrayList<>();
//	static
//	{
//		LOCATIONS.add(new RaidLocation("Dragon Valley", new Location(71640, 117976, -3680)));
//		LOCATIONS.add(new RaidLocation("The Outsides of Elven Village", new Location(54184, 44440, -3584)));
//		//LOCATIONS.add(new RaidLocation("The Outsides of Goddard Castle", new Location(148776, 43272, -2320)));
//		LOCATIONS.add(new RaidLocation("Hellbound Island - Anomic Foundry", new Location(26648, 247144, -3232)));
//		LOCATIONS.add(new RaidLocation("Beast Farm", new Location(52456, -79512, -3104)));
//		LOCATIONS.add(new RaidLocation("Anghel Waterfall", new Location(170728, 85352, -1984)));
//		LOCATIONS.add(new RaidLocation("Tower of Insolence 11 Floor", new Location(114648, 16088, 6992)));
//	}
//
//	protected static final NpcTemplate TEMPLATE = NpcHolder.getInstance().getTemplate(Config.RAID_EVENT_RAID_ID);
//
//	protected MonsterInstance _raid = null;
//	protected RaidLocation _currentLocation = null;
//	protected Future<?> _notifyThread = null;
//	protected Future<?> _despawnThread = null;
//
//	public AutoRaidEventManager()
//	{
//		if (TEMPLATE == null)
//		{
//			_log.warn(getClass().getSimpleName() + " : The raid template couldn't be found. Something is wrong in the npcs");
//			return;
//		}
//
//		// Fix all z spawn locations so the npc is always on the ground, or above
//		for (RaidLocation loc : LOCATIONS)
//		{
//			loc.getLocation().setZ(loc.getLocation().getZ() + (int)(TEMPLATE.collisionHeight / 20));
//		}
//
//		// Schedule next raid spawn
//		setNextRaidSpawn();
//	}
//
//	/**
//	 * Calculates the time for the next spawn and schedules it
//	 */
//	protected void setNextRaidSpawn()
//	{
//		final long currentTime = System.currentTimeMillis();
//		final Calendar spawnTime = Calendar.getInstance();
//		spawnTime.set(Calendar.HOUR_OF_DAY, Config.RAID_EVENT_TIME_HOUR);
//		spawnTime.set(Calendar.MINUTE, Config.RAID_EVENT_TIME_MINUTE);
//
//		// If we already passed the spawn time, then the next is for tomorrow
//		if (spawnTime.getTimeInMillis() <= currentTime)
//			spawnTime.add(Calendar.DAY_OF_MONTH, 1);
//
//		ThreadPoolManager.getInstance().schedule(new RaidSpawnTask(), spawnTime.getTimeInMillis() - currentTime);
//	}
//
//	/**
//	 * Called when a monster dies. If its the event raid, then announce the death, stop threads and set a new spawn task
//	 *
//	 * @param raid
//	 */
//	public void onRaidDeath(MonsterInstance raid)
//	{
//		if (raid == null || raid.getNpcId() != Config.RAID_EVENT_RAID_ID || _raid == null)
//			return;
//
//		// Cancel notify thread
//		if (_notifyThread != null)
//		{
//			_notifyThread.cancel(true);
//			_notifyThread = null;
//		}
//
//		// Cancel despawn thread
//		if (_despawnThread != null)
//		{
//			_despawnThread.cancel(true);
//			_despawnThread = null;
//		}
//
//		// Announce that the raid has died
//		Announcements.getInstance().announceToAll(_raid.getName() + " has dissapair... But it will come back!");
//
//		// Reset variables
//		_raid = null;
//		_currentLocation = null;
//
//		// Schedule the next raid spawn
//		setNextRaidSpawn();
//	}
//
//	// Task to control the raid spawn
//	protected class RaidSpawnTask implements Runnable
//	{
//		@Override
//		public void run()
//		{
//			// Choose a random location to spawn the raid
//			_currentLocation = LOCATIONS.get(Rnd.get(LOCATIONS.size()));
//
//			// Spawn the raid in the choosen location
//			_raid = (MonsterInstance)TEMPLATE.getNewInstance();
//			_raid.setHeading(0);
//			_raid.setSpawnedLoc(_currentLocation.getLocation());
//			_raid.setReflection(ReflectionManager.DEFAULT);
//			_raid.setCurrentHpMp(_raid.getMaxHp(), _raid.getMaxMp(), true);
//			_raid.spawnMe(_raid.getSpawnedLoc());
//
//			// Announce that the raid has spawned
//			Announcements.getInstance().announceToAll(_raid.getName() + " has spawned in " + _currentLocation.getName() + " for next 1 hour!");
//
//			// Log spawn
//			_log.info(super.getClass().getSimpleName() + " : Special Raid spawned in " + _currentLocation.getName());
//
//			// Create a thread to repeat each 2 minutes to announce to all players that the raid is in x location
//			_notifyThread = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RaidNotifyTask(), Config.RAID_EVENT_NOTIFY_DELAY, Config.RAID_EVENT_NOTIFY_DELAY);
//
//			// Create a thread to schedule the despawn of the raid if it was not killed by that time
//			_despawnThread = ThreadPoolManager.getInstance().schedule(new RaidDespawnTask(), Config.RAID_EVENT_DURATION);
//		}
//	}
//
//	// Task to control the messages that are sent each 2 minutes to announce the raid status
//	protected class RaidNotifyTask implements Runnable
//	{
//		@Override
//		public void run()
//		{
//			if (_raid == null)
//				return;
//
//			Announcements.getInstance().announceToAll(_raid.getName() + " is in " + _currentLocation.getName() + ", kill it !!");
//		}
//	}
//
//	// Task to despawn the raid when the max time expire
//	protected class RaidDespawnTask implements Runnable
//	{
//		@Override
//		public void run()
//		{
//			// Cancel notify thread
//			if (_notifyThread != null)
//			{
//				_notifyThread.cancel(true);
//				_notifyThread = null;
//			}
//
//			// Cancel despawn thread
//			if (_despawnThread != null)
//			{
//				_despawnThread.cancel(false);
//				_despawnThread = null;
//			}
//
//			if (_raid != null)
//			{
//				// Announce that the raid time has ended
//				Announcements.getInstance().announceToAll(_raid.getName() + " wasn't killed in time and escaped. He will be back!");
//
//				// Delete the raid and reset variables
//				_raid.deleteMe();
//				_raid = null;
//				_currentLocation = null;
//			}
//
//			// Schedule the next raid spawn
//			setNextRaidSpawn();
//		}
//	}
//
//	// Class to save each raid spawn location
//	protected static final class RaidLocation
//	{
//		private final String _name;
//		private final Location _loc;
//
//		public RaidLocation(String name, Location loc)
//		{
//			_name = name;
//			_loc = loc;
//		}
//
//		public String getName()
//		{
//			return _name;
//		}
//
//		public Location getLocation()
//		{
//			return _loc;
//		}
//	}
//
//	public static AutoRaidEventManager getInstance()
//	{
//		return SingletonHolder._instance;
//	}
//
//	private static class SingletonHolder
//	{
//		protected static final AutoRaidEventManager _instance = new AutoRaidEventManager();
//	}
//}
