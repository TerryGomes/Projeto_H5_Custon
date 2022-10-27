package l2mv.gameserver.geodata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.ExShowTrace;
import l2mv.gameserver.utils.Location;

/**
 * @Author: claww
 * @Date: 23/07/2012
 */
public class GeoMove
{
	private static final Logger _log = Logger.getLogger(GeoMove.class.getName());

	private static List<Location> findPath(int x, int y, int z, Location target, GameObject obj, boolean showTrace, int geoIndex)
	{
		if (Math.abs(z - target.z) > 256)
		{
			return Collections.emptyList();
		}

		z = GeoEngine.getHeight(x, y, z, geoIndex);
		target.z = GeoEngine.getHeight(target, geoIndex);
		final PathFind n = new PathFind(x, y, z, target.x, target.y, target.z, obj, geoIndex);
		if (n.getPath() == null || n.getPath().isEmpty())
		{
			return Collections.emptyList();
		}

		List<Location> targetRecorder = new ArrayList<Location>(n.getPath().size() + 2);

		// add the first point in the list (starting position Chara)
		targetRecorder.add(new Location(x, y, z));

		for (Location p : n.getPath())
		{
			targetRecorder.add(p.geo2world());
		}

		// add the last point in the list (target)
		targetRecorder.add(target);

		if (Config.PATH_CLEAN)
		{
			pathClean(targetRecorder, geoIndex);
		}

		if (showTrace && obj.isPlayer() && ((Player) obj).getVarB("trace"))
		{
			final Player player = (Player) obj;
			final ExShowTrace trace = new ExShowTrace();
			int i = 0;
			for (Location loc : targetRecorder)
			{
				i++;
				if (i == 1 || i == targetRecorder.size())
				{
					continue;
				}
				trace.addTrace(loc.x, loc.y, loc.z + 15, 30000);
			}
			player.sendPacket(trace);
		}

		return targetRecorder;
	}

	public static List<List<Location>> findMovePath(int x, int y, int z, Location target, GameObject obj, boolean showTrace, int geoIndex)
	{
		try
		{
			return getNodePath(findPath(x, y, z, target, obj, showTrace, geoIndex), geoIndex);
		}
		catch (Exception e)
		{
			_log.error("Error while finding move path from x: " + x + " y: " + y + " z: " + z + " to location: " + target + " at geoIndex: " + geoIndex);
		}

		return Collections.emptyList();
	}

	private static List<List<Location>> getNodePath(List<Location> path, int geoIndex)
	{
		final int size = path.size();
		if (size <= 1)
		{
			return Collections.emptyList();
		}
		List<List<Location>> result = new ArrayList<List<Location>>(size);
		for (int i = 1; i < size; i++)
		{
			Location p2 = path.get(i);
			Location p1 = path.get(i - 1);
			List<Location> moveList = GeoEngine.MoveList(p1.x, p1.y, p1.z, p2.x, p2.y, geoIndex, true); // onlyFullPath = true - check all the way through
			if (moveList == null) // If though, would be through one of the sites you can not pass, reject all the way
			{
				return Collections.emptyList();
			}
			if (!moveList.isEmpty()) // this can only happen if two identical consecutive points
			{
				result.add(moveList);
			}
		}
		return result;
	}

	public static List<Location> constructMoveList(Location begin, Location end)
	{
		begin.world2geo();
		end.world2geo();

		int diff_x = end.x - begin.x, diff_y = end.y - begin.y, diff_z = end.z - begin.z;
		int dx = Math.abs(diff_x), dy = Math.abs(diff_y), dz = Math.abs(diff_z);
		float steps = Math.max(Math.max(dx, dy), dz);
		if (steps == 0) // Will not go
		{
			return Collections.emptyList();
		}

		float step_x = diff_x / steps, step_y = diff_y / steps, step_z = diff_z / steps;
		float next_x = begin.x, next_y = begin.y, next_z = begin.z;

		List<Location> result = new ArrayList<Location>((int) steps + 1);
		result.add(new Location(begin.x, begin.y, begin.z)); // The first point

		for (int i = 0; i < steps; i++)
		{
			next_x += step_x;
			next_y += step_y;
			next_z += step_z;

			result.add(new Location((int) (next_x + 0.5f), (int) (next_y + 0.5f), (int) (next_z + 0.5f)));
		}

		return result;
	}

	/**
	 *Clears the way of unnecessary points.
	 * @ Param path the path to be cleared
	 */
	private static void pathClean(List<Location> path, int geoIndex)
	{
		int size = path.size();
		if (size > 2)
		{
			for (int i = 2; i < size; i++)
			{
				Location p3 = path.get(i); // the end point of the movement
				Location p2 = path.get(i - 1); // point in the middle, a candidate for elimination
				Location p1 = path.get(i - 2); // of the start of the movement
				if (p1.equals(p2) || p3.equals(p2) || IsPointInLine(p1, p2, p3)) // if the second point is the same as the first / third or in line with them - it is not needed
				{
					path.remove(i - 1); // remove it
					size--; // note is in the amount of solid
					i = Math.max(2, i - 2); // move back, FIXME: I can not quite right here
				}
			}
		}

		int current = 0;
		int sub;
		while (current < path.size() - 2)
		{
			Location one = path.get(current);
			sub = current + 2;
			while (sub < path.size())
			{
				Location two = path.get(sub);
				if (one.equals(two) || GeoEngine.canMoveWithCollision(one.x, one.y, one.z, two.x, two.y, two.z, geoIndex)) // canMoveWithCollision / canMoveToCoord
				{
					while (current + 1 < sub)
					{
						path.remove(current + 1);
						sub--;
					}
				}
				sub++;
			}
			current++;
		}
	}

	private static boolean IsPointInLine(Location p1, Location p2, Location p3)
	{
		// All three points on one of the axes X and Y.
		if (p1.x == p3.x && p3.x == p2.x || p1.y == p3.y && p3.y == p2.y)
		{
			return true;
		}
		// Condition executed if all the following three points are arranged diagonally.
		// This works because we are comparing the neighboring points (the distance between them is equal, is only important sign).
		// For the case with an arbitrary point will not work.
		if ((p1.x - p2.x) * (p1.y - p2.y) == (p2.x - p3.x) * (p2.y - p3.y))
		{
			return true;
		}
		return false;
	}
}