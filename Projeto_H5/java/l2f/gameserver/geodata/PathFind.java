package l2f.gameserver.geodata;

import static l2f.gameserver.geodata.GeoEngine.EAST;
import static l2f.gameserver.geodata.GeoEngine.NORTH;
import static l2f.gameserver.geodata.GeoEngine.NSWE_ALL;
import static l2f.gameserver.geodata.GeoEngine.NSWE_NONE;
import static l2f.gameserver.geodata.GeoEngine.SOUTH;
import static l2f.gameserver.geodata.GeoEngine.WEST;

import java.util.ArrayList;
import java.util.List;

import l2f.gameserver.Config;
import l2f.gameserver.geodata.PathFindBuffers.GeoNode;
import l2f.gameserver.geodata.PathFindBuffers.PathFindBuffer;
import l2f.gameserver.model.GameObject;
import l2f.gameserver.utils.Location;

public class PathFind
{
	private int geoIndex = 0;

	private PathFindBuffer buff;

	private List<Location> path;
	private final short[] hNSWE = new short[2];
	private final Location startPoint, endPoint;
	private GeoNode startNode, endNode, currentNode;

	public PathFind(int x, int y, int z, int destX, int destY, int destZ, GameObject obj, int geoIndex)
	{
		this.geoIndex = geoIndex;

		startPoint = Config.PATHFIND_BOOST == 0 ? new Location(x, y, z) : GeoEngine.moveCheckWithCollision(x, y, z, destX, destY, true, geoIndex);
		endPoint = Config.PATHFIND_BOOST != 2 || Math.abs(destZ - z) > 200 ? new Location(destX, destY, destZ)
					: GeoEngine.moveCheckBackwardWithCollision(destX, destY, destZ, startPoint.x, startPoint.y, true, geoIndex);

		startPoint.world2geo();
		endPoint.world2geo();

		startPoint.z = GeoEngine.NgetHeight(startPoint.x, startPoint.y, startPoint.z, geoIndex);
		endPoint.z = GeoEngine.NgetHeight(endPoint.x, endPoint.y, endPoint.z, geoIndex);

		int xdiff = Math.abs(endPoint.x - startPoint.x);
		int ydiff = Math.abs(endPoint.y - startPoint.y);

		if (xdiff == 0 && ydiff == 0)
		{
			if (Math.abs(endPoint.z - startPoint.z) < 32)
			{
				path = new ArrayList<>();
				path.add(0, startPoint);
			}
			return;
		}

		int mapSize = 2 * Math.max(xdiff, ydiff);

		if ((buff = PathFindBuffers.alloc(mapSize)) != null)
		{
			buff.offsetX = startPoint.x - buff.mapSize / 2;
			buff.offsetY = startPoint.y - buff.mapSize / 2;

			// статистика
			buff.totalUses++;
			if (obj.isPlayable())
			{
				buff.playableUses++;
			}

			findPath();

			buff.free();

			PathFindBuffers.recycle(buff);
		}
	}

	private List<Location> findPath()
	{
		startNode = buff.nodes[startPoint.x - buff.offsetX][startPoint.y - buff.offsetY].set(startPoint.x, startPoint.y, (short) startPoint.z);

		GeoEngine.NgetHeightAndNSWE(startPoint.x, startPoint.y, (short) startPoint.z, hNSWE, geoIndex);
		startNode.z = hNSWE[0];
		startNode.nswe = hNSWE[1];
		startNode.costFromStart = 0f;
		startNode.state = GeoNode.OPENED;
		startNode.parent = null;

		endNode = buff.nodes[endPoint.x - buff.offsetX][endPoint.y - buff.offsetY].set(endPoint.x, endPoint.y, (short) endPoint.z);

		startNode.costToEnd = pathCostEstimate(startNode);
		startNode.totalCost = startNode.costFromStart + startNode.costToEnd;

		buff.open.add(startNode);

		long nanos = System.nanoTime();
		long searhTime = 0;
		int itr = 0;

		while ((searhTime = System.nanoTime() - nanos) < Config.PATHFIND_MAX_TIME && (currentNode = buff.open.poll()) != null)
		{
			itr++;
			if (currentNode.x == endPoint.x && currentNode.y == endPoint.y && Math.abs(currentNode.z - endPoint.z) < 64)
			{
				path = tracePath(currentNode);
				break;
			}

			handleNode(currentNode);
			currentNode.state = GeoNode.CLOSED;
		}

		buff.totalTime += searhTime;
		buff.totalItr += itr;
		if (path != null)
		{
			buff.successUses++;
		}
		else if (searhTime > Config.PATHFIND_MAX_TIME)
		{
			buff.overtimeUses++;
		}

		return path;
	}

	private List<Location> tracePath(GeoNode f)
	{
		List<Location> locations = new ArrayList<>();
		do
		{
			locations.add(0, f.getLoc());
			f = f.parent;
		}
		while (f.parent != null);
		return locations;
	}

	private void handleNode(GeoNode node)
	{
		int clX = node.x;
		int clY = node.y;
		short clZ = node.z;

		getHeightAndNSWE(clX, clY, clZ);
		short NSWE = hNSWE[1];

		if (Config.PATHFIND_DIAGONAL)
		{
			// Юго-восток
			if ((NSWE & SOUTH) == SOUTH && (NSWE & EAST) == EAST)
			{
				getHeightAndNSWE(clX + 1, clY, clZ);
				if ((hNSWE[1] & SOUTH) == SOUTH)
				{
					getHeightAndNSWE(clX, clY + 1, clZ);
					if ((hNSWE[1] & EAST) == EAST)
					{
						handleNeighbour(clX + 1, clY + 1, node, true);
					}
				}
			}

			// Юго-запад
			if ((NSWE & SOUTH) == SOUTH && (NSWE & WEST) == WEST)
			{
				getHeightAndNSWE(clX - 1, clY, clZ);
				if ((hNSWE[1] & SOUTH) == SOUTH)
				{
					getHeightAndNSWE(clX, clY + 1, clZ);
					if ((hNSWE[1] & WEST) == WEST)
					{
						handleNeighbour(clX - 1, clY + 1, node, true);
					}
				}
			}

			// Северо-восток
			if ((NSWE & NORTH) == NORTH && (NSWE & EAST) == EAST)
			{
				getHeightAndNSWE(clX + 1, clY, clZ);
				if ((hNSWE[1] & NORTH) == NORTH)
				{
					getHeightAndNSWE(clX, clY - 1, clZ);
					if ((hNSWE[1] & EAST) == EAST)
					{
						handleNeighbour(clX + 1, clY - 1, node, true);
					}
				}
			}

			// Северо-запад
			if ((NSWE & NORTH) == NORTH && (NSWE & WEST) == WEST)
			{
				getHeightAndNSWE(clX - 1, clY, clZ);
				if ((hNSWE[1] & NORTH) == NORTH)
				{
					getHeightAndNSWE(clX, clY - 1, clZ);
					if ((hNSWE[1] & WEST) == WEST)
					{
						handleNeighbour(clX - 1, clY - 1, node, true);
					}
				}
			}
		}

		// Восток
		if ((NSWE & EAST) == EAST)
		{
			handleNeighbour(clX + 1, clY, node, false);
		}

		// Запад
		if ((NSWE & WEST) == WEST)
		{
			handleNeighbour(clX - 1, clY, node, false);
		}

		// Юг
		if ((NSWE & SOUTH) == SOUTH)
		{
			handleNeighbour(clX, clY + 1, node, false);
		}

		// Север
		if ((NSWE & NORTH) == NORTH)
		{
			handleNeighbour(clX, clY - 1, node, false);
		}
	}

	private float pathCostEstimate(GeoNode n)
	{
		int diffx = endNode.x - n.x;
		int diffy = endNode.y - n.y;
		int diffz = endNode.z - n.z;

		return (float) Math.sqrt(diffx * diffx + diffy * diffy + diffz * diffz / 256);
	}

	private float traverseCost(GeoNode from, GeoNode n, boolean d)
	{
		if (n.nswe != NSWE_ALL || Math.abs(n.z - from.z) > 16)
		{
			return 3f;
		}
		else
		{
			getHeightAndNSWE(n.x + 1, n.y, n.z);
			if (hNSWE[1] != NSWE_ALL || Math.abs(n.z - hNSWE[0]) > 16)
			{
				return 2f;
			}

			getHeightAndNSWE(n.x - 1, n.y, n.z);
			if (hNSWE[1] != NSWE_ALL || Math.abs(n.z - hNSWE[0]) > 16)
			{
				return 2f;
			}

			getHeightAndNSWE(n.x, n.y + 1, n.z);
			if (hNSWE[1] != NSWE_ALL || Math.abs(n.z - hNSWE[0]) > 16)
			{
				return 2f;
			}

			getHeightAndNSWE(n.x, n.y - 1, n.z);
			if (hNSWE[1] != NSWE_ALL || Math.abs(n.z - hNSWE[0]) > 16)
			{
				return 2f;
			}
		}

		return d ? 1.414f : 1f;
	}

	private void handleNeighbour(int x, int y, GeoNode from, boolean d)
	{
		int nX = x - buff.offsetX, nY = y - buff.offsetY;
		if (nX >= buff.mapSize || nX < 0 || nY >= buff.mapSize || nY < 0)
		{
			return;
		}

		GeoNode n = buff.nodes[nX][nY];
		float newCost;

		if (!n.isSet())
		{
			n = n.set(x, y, from.z);
			GeoEngine.NgetHeightAndNSWE(x, y, from.z, hNSWE, geoIndex);
			n.z = hNSWE[0];
			n.nswe = hNSWE[1];
		}

		int height = Math.abs(n.z - from.z);
		if (height > Config.PATHFIND_MAX_Z_DIFF || n.nswe == NSWE_NONE)
		{
			return;
		}

		newCost = from.costFromStart + traverseCost(from, n, d);
		if (n.state == GeoNode.OPENED || n.state == GeoNode.CLOSED)
		{
			if (n.costFromStart <= newCost)
			{
				return;
			}
		}

		if (n.state == GeoNode.NONE)
		{
			n.costToEnd = pathCostEstimate(n);
		}

		n.parent = from;
		n.costFromStart = newCost;
		n.totalCost = n.costFromStart + n.costToEnd;

		if (n.state == GeoNode.OPENED)
		{
			return;
		}

		n.state = GeoNode.OPENED;
		buff.open.add(n);
	}

	private void getHeightAndNSWE(int x, int y, short z)
	{
		int nX = x - buff.offsetX, nY = y - buff.offsetY;
		if (nX >= buff.mapSize || nX < 0 || nY >= buff.mapSize || nY < 0)
		{
			hNSWE[1] = NSWE_NONE; // Затычка
			return;
		}

		GeoNode n = buff.nodes[nX][nY];
		if (!n.isSet())
		{
			n = n.set(x, y, z);
			GeoEngine.NgetHeightAndNSWE(x, y, z, hNSWE, geoIndex);
			n.z = hNSWE[0];
			n.nswe = hNSWE[1];
		}
		else
		{
			hNSWE[0] = n.z;
			hNSWE[1] = n.nswe;
		}
	}

	public List<Location> getPath()
	{
		return path;
	}
}