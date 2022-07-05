package ai.adept;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;

public class AdeptGiran3 extends Adept
{
	public AdeptGiran3(NpcInstance actor)
	{
		super(actor);
		_points = new Location[]
		{
			new Location(82840, 147848, -3472),
			new Location(81096, 147816, -3464),
			new Location(81096, 149352, -3472),
			new Location(82936, 149352, -3472)
		};
	}
}