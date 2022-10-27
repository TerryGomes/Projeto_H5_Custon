package ai.adept;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

public class AdeptGiran extends Adept
{
	public AdeptGiran(NpcInstance actor)
	{
		super(actor);
		_points = new Location[]
		{
			new Location(84856, 147760, -3400),
			new Location(83625, 147707, -3400),
			new Location(83617, 149544, -3400),
			new Location(83816, 149541, -3400),
			new Location(83632, 149559, -3400),
			new Location(83616, 147708, -3400)
		};
	}
}