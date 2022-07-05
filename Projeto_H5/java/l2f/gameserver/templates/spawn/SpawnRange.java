package l2f.gameserver.templates.spawn;

import l2f.gameserver.utils.Location;

public interface SpawnRange
{
	Location getRandomLoc(int geoIndex);
}
