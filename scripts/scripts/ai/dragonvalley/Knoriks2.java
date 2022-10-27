package ai.dragonvalley;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

public class Knoriks2 extends Patrollers
{
	public Knoriks2(NpcInstance actor)
	{
		super(actor);
		_points = new Location[]
		{
			new Location(140456, 117832, -3942),
			new Location(142632, 117336, -3942),
			new Location(142680, 118680, -3942),
			new Location(141864, 119240, -3942),
			new Location(140856, 118904, -3942)
		};
	}
}
