package ai.adept;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;

public class AdeptGiran2 extends Adept
{
	public AdeptGiran2(NpcInstance actor)
	{
		super(actor);
		_points = new Location[]
		{
			new Location(83336, 147992, -3400),
			new Location(83320, 148568, -3400),
			new Location(82856, 148872, -3472),
			new Location(82232, 149592, -3472),
			new Location(82856, 148872, -3472),
			new Location(83320, 148568, -3400)
		};
	}
}