package ai.dragonvalley;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;

public class Knoriks3 extends Patrollers
{
	public Knoriks3(NpcInstance actor)
	{
		super(actor);
		_points = new Location[]
		{
			new Location(140904, 108856, -3764),
			new Location(140648, 112360, -3750),
			new Location(142856, 111768, -3974),
			new Location(142216, 109432, -3966)
		};
	}
}
