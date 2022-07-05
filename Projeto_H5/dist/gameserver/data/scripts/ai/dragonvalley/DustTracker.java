package ai.dragonvalley;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;

/**
 * @author pchayka
 */
public class DustTracker extends Patrollers
{

	public DustTracker(NpcInstance actor)
	{
		super(actor);
		_points = new Location[]
		{
			new Location(125176, 111896, -3168),
			new Location(124872, 109736, -3104),
			new Location(123608, 108712, -3024),
			new Location(122632, 108008, -2992),
			new Location(120504, 109000, -2944),
			new Location(118632, 109944, -2960),
			new Location(115208, 109928, -3040),
			new Location(112568, 110296, -2976),
			new Location(110264, 111320, -3152),
			new Location(109512, 113432, -3088),
			new Location(109272, 116104, -3104),
			new Location(108008, 117912, -3056)
		};
	}
}
