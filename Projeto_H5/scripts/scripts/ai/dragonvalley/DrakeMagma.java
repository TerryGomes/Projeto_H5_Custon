package ai.dragonvalley;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

/**
 * @author UNREAL
 */

public class DrakeMagma extends Patrollers
{
	public DrakeMagma(NpcInstance actor)
	{
		super(actor);

		if (equals(actor.getLoc(), 122888, 110664, -3728))
		{

			_points = new Location[]
			{
				new Location(122888, 110664, -3728),
				new Location(121320, 112440, -3792),
				new Location(120024, 112712, -3744),
				new Location(119640, 114792, -3608),

				new Location(117240, 118935, -3712),
				new Location(110264, 123464, -3632),
				new Location(109416, 126088, -3696),
				new Location(110456, 125976, -3686),

				new Location(111336, 123208, -3712),
				new Location(114200, 121528, -3744),
				new Location(115880, 122984, -3240),
				new Location(118040, 123112, -3072),

				new Location(121880, 119352, -3136),
				new Location(123512, 118968, -3200),
				new Location(124600, 118632, -3056),
				new Location(126520, 118056, -3104),

				new Location(126936, 115032, -3728),
				new Location(124040, 108632, -2992),
				new Location(122344, 108200, -2992),
				new Location(117688, 110296, -2944),

				new Location(117160, 113768, -3056),
				new Location(113640, 116328, -3200),
				new Location(111128, 119368, -3056),
				new Location(109816, 119336, -3072),

				new Location(108568, 117928, -3048),
				new Location(110872, 110600, -3056),
				new Location(114392, 110632, -3024),
				new Location(113448, 113175, -2984),

				new Location(112568, 113064, -2784),
				new Location(111688, 112360, -2784)
			};
		}
		else
		{
			_points = new Location[]
			{
				new Location(80232, 110248, -3040),
				new Location(81864, 109368, -3120),
				new Location(83736, 108312, -3072),
				new Location(85496, 106440, -3216),
				new Location(91800, 108600, -3024),
				new Location(92168, 110664, -3008),
				new Location(92376, 111512, -3024),
				new Location(94040, 112776, -3040),
				new Location(93944, 114312, -3104),
				new Location(95064, 116200, -3056),
				new Location(94328, 117720, -3024),
				new Location(93032, 118312, -3024),
				new Location(90792, 117928, -3072),
				new Location(89880, 115624, -3040),
				new Location(88952, 114904, -3040),
				new Location(87304, 114552, -2976)
			};
		}
	}

	private boolean equals(Location _loc, int x, int y, int z)
	{
		if (_loc.x == x && _loc.y == y && _loc.z == z)
		{
			return true;
		}
		return false;
	}
}
