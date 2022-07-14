package ai.adept;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

public class AdeptGiran4 extends Adept
{
	public AdeptGiran4(NpcInstance actor)
	{
		super(actor);
		_points = new Location[]
		{
			new Location(84872, 149608, -3400),
			new Location(81544, 149592, -3472),
			new Location(81544, 152216, -3536),
			new Location(81544, 149592, -3472)
		};
	}
}