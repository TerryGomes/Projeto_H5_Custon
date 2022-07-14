package ai.adept;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

public class AdeptGiran1 extends Adept
{
	public AdeptGiran1(NpcInstance actor)
	{
		super(actor);
		_points = new Location[]
		{
			new Location(85464, 147352, -3400),
			new Location(83640, 147752, -3400),
			new Location(83368, 148184, -3400),
			new Location(81544, 147704, -3472),
			new Location(81528, 146952, -3536),
			new Location(80808, 146824, -3536),
			new Location(81064, 148632, -3472),
			new Location(80808, 146824, -3536),
			new Location(81528, 146952, -3536),
			new Location(81544, 147704, -3472),
			new Location(83368, 148184, -3400),
			new Location(83640, 147752, -3400)
		};
	}
}