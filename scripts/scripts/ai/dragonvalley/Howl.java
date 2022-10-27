package ai.dragonvalley;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

/**
 * @author pchayka
 */

public class Howl extends Patrollers
{
	public Howl(NpcInstance actor)
	{
		super(actor);
		_points = new Location[]
		{
			new Location(94664, 117368, -3056),
			new Location(93400, 118600, -2992),
			new Location(91256, 118536, -3056),
			new Location(90376, 119640, -3056),
			new Location(88904, 119352, -3056),
			new Location(87208, 120264, -3056),
			new Location(86040, 119576, -3008),
			new Location(84264, 118280, -3008),
			new Location(85016, 116360, -3056),
			new Location(86200, 115208, -3040),
			new Location(87352, 114632, -3008),
			new Location(89160, 114984, -3056),
			new Location(90056, 115976, -3056),
			new Location(91000, 117832, -3088),
			new Location(93224, 118360, -3024)
		};
	}
}
