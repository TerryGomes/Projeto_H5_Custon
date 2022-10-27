package ai.dragonvalley;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

public class Knoriks5 extends Patrollers
{
	public Knoriks5(NpcInstance actor)
	{
		super(actor);
		_points = new Location[]
		{
			new Location(154040, 118696, -3834),
			new Location(152600, 119992, -3834),
			new Location(151816, 121480, -3834),
			new Location(152808, 121960, -3834),
			new Location(153768, 121480, -3834),
			new Location(152136, 121672, -3834),
			new Location(152248, 120200, -3834)
		};
	}
}
