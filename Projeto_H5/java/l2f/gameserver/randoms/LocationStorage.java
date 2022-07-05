package l2f.gameserver.randoms;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2f.gameserver.utils.Location;

public class LocationStorage
{
	private static Map<Integer, List<Location>> _storage = new HashMap<Integer, List<Location>>();

	public static boolean addLocation(int objId, Location loc, int delayBeforeMove)
	{
		/*
		 * if (loc != null && loc.r == ReflectionManager.DEFAULT_ID)
		 * {
		 * loc.setH(delayBeforeMove); // Ub3r h4x0r drunkard hack. Use heading for move delay HAAHAHAHAH!
		 * List<Location> locs = _storage.get(objId);
		 * if (locs == null)
		 * _storage.put(objId, locs = new CopyOnWriteArrayList<Location>());
		 * if (locs.isEmpty())
		 * locs.add(loc);
		 * else
		 * {
		 * Location prevLoc = locs.get(locs.size() - 1); // Last location
		 * if (Location.checkIfInRange(25, prevLoc.x, prevLoc.y, loc.x, loc.y)) // Check if there is already a location nearby
		 * return false;
		 * locs.add(loc);
		 * }
		 * return true;
		 * }
		 */

		return false;
	}

	public static Location getLocation(int objId, int index)
	{
		List<Location> locs = _storage.get(objId);
		if (locs != null && !locs.isEmpty() && index >= 0 && index < locs.size())
		{
			return locs.get(index);
		}

		return null;
	}

	public static Location getLocationAround(int objId, int index, Location curLoc, int radius, int maxTries)
	{
		List<Location> locs = _storage.get(objId);
		if (locs != null && !locs.isEmpty())
		{
			int tries = 0;
			for (; index >= 0 && index < locs.size(); index++)
			{
				if (tries > maxTries)
				{
					break;
				}

				Location loc = locs.get(index);
				if (Location.checkIfInRange(radius, curLoc.x, curLoc.y, loc.x, loc.y))
				{
					return loc;
				}

				tries++;
			}
		}

		return null;
	}

	public static List<Location> getLocations(int objId)
	{
		List<Location> list = _storage.get(objId);
		if (list == null)
		{
			return Collections.emptyList();
		}

		return _storage.get(objId);
	}

	public static Integer[] getLocationsKeys()
	{
		return _storage.keySet().toArray(new Integer[_storage.size()]);
	}
}
