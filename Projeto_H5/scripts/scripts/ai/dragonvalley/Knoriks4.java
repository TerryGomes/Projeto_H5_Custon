package ai.dragonvalley;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

public class Knoriks4 extends Patrollers
{
	public Knoriks4(NpcInstance actor)
	{
		super(actor);
		_points = new Location[]
		{
			new Location(147960, 110216, -3974),
			new Location(146072, 109400, -3974),
			new Location(145576, 110856, -3974),
			new Location(144504, 107768, -3974),
			new Location(145864, 109224, -3974)
		};
	}
}
