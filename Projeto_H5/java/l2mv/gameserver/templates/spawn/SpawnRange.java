package l2mv.gameserver.templates.spawn;

import l2mv.gameserver.utils.Location;

public interface SpawnRange
{
	Location getRandomLoc(int geoIndex);
}
