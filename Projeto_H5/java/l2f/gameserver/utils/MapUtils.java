package l2f.gameserver.utils;

import l2f.gameserver.Config;
import l2f.gameserver.model.GameObject;
import l2f.gameserver.model.World;

public class MapUtils
{
	private MapUtils()
	{
	}

	public static int regionX(GameObject o)
	{
		return regionX(o.getX());
	}

	public static int regionY(GameObject o)
	{
		return regionY(o.getY());
	}

	public static int regionX(int x)
	{
		return (x - World.MAP_MIN_X >> 15) + Config.GEO_X_FIRST;
	}

	public static int regionY(int y)
	{
		return (y - World.MAP_MIN_Y >> 15) + Config.GEO_Y_FIRST;
	}
}
