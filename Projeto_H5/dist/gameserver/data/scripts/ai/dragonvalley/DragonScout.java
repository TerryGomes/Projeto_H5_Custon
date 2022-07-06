package ai.dragonvalley;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

/**
 * @author pchayka
 */

public class DragonScout extends Patrollers
{
	public DragonScout(NpcInstance actor)
	{
		super(actor);
		_points = new Location[]
		{
			new Location(116792, 116936, -3728),
			new Location(116056, 118984, -3728),
			new Location(114856, 120040, -3712),
			new Location(114184, 121464, -3776),
			new Location(115640, 122856, -3352),
			new Location(116600, 123304, -3136),
			new Location(118248, 122824, -3072),
			new Location(119800, 121656, -3024),
			new Location(120904, 119912, -3072),
			new Location(121720, 119384, -3136),
			new Location(124168, 118968, -3104),
			new Location(125864, 117832, -3056),
			new Location(126680, 117688, -3136),
			new Location(126728, 115256, -3728),
			new Location(123720, 114456, -3712),
			new Location(121208, 112536, -3792),
			new Location(120168, 114024, -3704),
			new Location(120232, 115368, -3712)
		};
	}
}
