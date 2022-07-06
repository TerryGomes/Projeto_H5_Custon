package ai.dragonvalley;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

public class DrakeRunners extends Patrollers
{
	public DrakeRunners(NpcInstance actor)
	{
		super(actor);
		_points = new Location[]
		{
			new Location(148984, 112952, -3720),
			new Location(149160, 114312, -3720),
			new Location(149096, 115480, -3720),
			new Location(147720, 116216, -3720),
			new Location(146536, 116296, -3720),
			new Location(145192, 115304, -3720),
			new Location(144888, 114504, -3720),
			new Location(145240, 113272, -3720),
			new Location(145960, 112696, -3720),
			new Location(147416, 112488, -3720)
		};
	}
}
