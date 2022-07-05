package l2f.gameserver.utils;

import java.awt.Color;

import l2f.gameserver.geodata.GeoEngine;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.ExServerPrimitive;

public class GeodataUtils
{
	public static final byte EAST = 1, WEST = 2, SOUTH = 4, NORTH = 8, NSWE_ALL = 15, NSWE_NONE = 0;

	public static void debug2DLine(Player player, int x, int y, int tx, int ty, int z)
	{
		int gx = GeoEngine.getGeoX(x);
		int gy = GeoEngine.getGeoY(y);

		int tgx = GeoEngine.getGeoX(tx);
		int tgy = GeoEngine.getGeoY(ty);

		ExServerPrimitive prim = new ExServerPrimitive("Debug2DLine", x, y, z);
		prim.addLine(Color.BLUE, GeoEngine.getWorldX(gx), GeoEngine.getWorldY(gy), z, GeoEngine.getWorldX(tgx), GeoEngine.getWorldY(tgy), z);

		LinePointIterator iter = new LinePointIterator(gx, gy, tgx, tgy);

		while (iter.next())
		{
			int wx = GeoEngine.getWorldX(iter.x());
			int wy = GeoEngine.getWorldY(iter.y());

			prim.addPoint(Color.RED, wx, wy, z);
		}
		player.sendPacket(prim);
	}

	public static void debug3DLine(Player player, int x, int y, int z, int tx, int ty, int tz)
	{
		int gx = GeoEngine.getGeoX(x);
		int gy = GeoEngine.getGeoY(y);

		int tgx = GeoEngine.getGeoX(tx);
		int tgy = GeoEngine.getGeoY(ty);

		ExServerPrimitive prim = new ExServerPrimitive("Debug3DLine", x, y, z);
		prim.addLine(Color.BLUE, GeoEngine.getWorldX(gx), GeoEngine.getWorldY(gy), z, GeoEngine.getWorldX(tgx), GeoEngine.getWorldY(tgy), tz);

		LinePointIterator3D iter = new LinePointIterator3D(gx, gy, z, tgx, tgy, tz);
		iter.next();
		int prevX = iter.x();
		int prevY = iter.y();
		int wx = GeoEngine.getWorldX(prevX);
		int wy = GeoEngine.getWorldY(prevY);
		int wz = iter.z();
		prim.addPoint(Color.RED, wx, wy, wz);

		while (iter.next())
		{
			int curX = iter.x();
			int curY = iter.y();

			if ((curX != prevX) || (curY != prevY))
			{
				wx = GeoEngine.getWorldX(curX);
				wy = GeoEngine.getWorldY(curY);
				wz = iter.z();

				prim.addPoint(Color.RED, wx, wy, wz);

				prevX = curX;
				prevY = curY;
			}
		}
		player.sendPacket(prim);
	}

	private static Color getDirectionColor(int x, int y, int z, int geoIndex, byte NSWE)
	{
		if ((GeoEngine.getNSWE(x, y, z, geoIndex) & NSWE) != 0)
		{
			return Color.GREEN;
		}
		return Color.RED;
	}

	public static void debugGrid(Player player)
	{
		int geoRadius = 10;
		int blocksPerPacket = 10;
		if (geoRadius < 0)
		{
			throw new IllegalArgumentException("geoRadius < 0");
		}

		int iBlock = blocksPerPacket;
		int iPacket = 0;

		ExServerPrimitive exsp = null;
		int playerGx = GeoEngine.getGeoX(player.getX());
		int playerGy = GeoEngine.getGeoY(player.getY());
		for (int dx = -geoRadius; dx <= geoRadius; ++dx)
		{
			for (int dy = -geoRadius; dy <= geoRadius; ++dy)
			{
				if (iBlock >= blocksPerPacket)
				{
					iBlock = 0;
					if (exsp != null)
					{
						++iPacket;
						player.sendPacket(exsp);
					}
					exsp = new ExServerPrimitive("DebugGrid_" + iPacket, player.getX(), player.getY(), -16000);
				}

				if (exsp == null)
				{
					throw new IllegalStateException();
				}

				int gx = playerGx + dx;
				int gy = playerGy + dy;

				int geoIndex = player.getReflection().getGeoIndex();
				int x = GeoEngine.getWorldX(gx);
				int y = GeoEngine.getWorldY(gy);
				int z = GeoEngine.getHeight(player.getLoc(), geoIndex);
				// north arrow
				Color col = getDirectionColor(x, y, z, geoIndex, NORTH);
				exsp.addLine(col, x - 1, y - 7, z, x + 1, y - 7, z);
				exsp.addLine(col, x - 2, y - 6, z, x + 2, y - 6, z);
				exsp.addLine(col, x - 3, y - 5, z, x + 3, y - 5, z);
				exsp.addLine(col, x - 4, y - 4, z, x + 4, y - 4, z);

				// east arrow
				col = getDirectionColor(x, y, z, geoIndex, EAST);
				exsp.addLine(col, x + 7, y - 1, z, x + 7, y + 1, z);
				exsp.addLine(col, x + 6, y - 2, z, x + 6, y + 2, z);
				exsp.addLine(col, x + 5, y - 3, z, x + 5, y + 3, z);
				exsp.addLine(col, x + 4, y - 4, z, x + 4, y + 4, z);

				// south arrow
				col = getDirectionColor(x, y, z, geoIndex, SOUTH);
				exsp.addLine(col, x - 1, y + 7, z, x + 1, y + 7, z);
				exsp.addLine(col, x - 2, y + 6, z, x + 2, y + 6, z);
				exsp.addLine(col, x - 3, y + 5, z, x + 3, y + 5, z);
				exsp.addLine(col, x - 4, y + 4, z, x + 4, y + 4, z);

				col = getDirectionColor(x, y, z, geoIndex, WEST);
				exsp.addLine(col, x - 7, y - 1, z, x - 7, y + 1, z);
				exsp.addLine(col, x - 6, y - 2, z, x - 6, y + 2, z);
				exsp.addLine(col, x - 5, y - 3, z, x - 5, y + 3, z);
				exsp.addLine(col, x - 4, y - 4, z, x - 4, y + 4, z);

				++iBlock;
			}
		}

		player.sendPacket(exsp);
	}
}