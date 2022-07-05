package ai.adept;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;

public class AdeptAden extends Adept
{
	public AdeptAden(NpcInstance actor)
	{
		super(actor);
		_points = new Location[]
		{
			new Location(146363, 24149, -2008),
			new Location(146345, 25803, -2008),
			new Location(147443, 25811, -2008),
			new Location(146369, 25817, -2008)
		};
	}
}