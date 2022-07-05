package ai.door;

import org.apache.commons.lang3.ArrayUtils;

import l2f.commons.geometry.Rectangle;
import l2f.gameserver.ai.DoorAI;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Territory;
import l2f.gameserver.model.instances.DoorInstance;
import l2f.gameserver.model.instances.NpcInstance;

public class SSQDoor extends DoorAI
{
	private static final Territory room1 = new Territory().add(new Rectangle(-89696, 217741, -88858, 218085).setZmin(-7520).setZmax(-7320));
	private static final Territory room2 = new Territory().add(new Rectangle(-88773, 220765, -88429, 219596).setZmin(-7520).setZmax(-7320));
	private static final Territory room3 = new Territory().add(new Rectangle(-87485, 220463, -86501, 220804).setZmin(-7520).setZmax(-7320));
	private static final Territory room4 = new Territory().add(new Rectangle(-85646, 219054, -84787, 219408).setZmin(-7520).setZmax(-7320));
	private static final Territory room5 = new Territory().add(new Rectangle(-87739, 216646, -87159, 217842).setZmin(-7520).setZmax(-7320));
	private static final int[] ssqDoors =
	{
		17240102,
		17240104,
		17240106,
		17240108,
		17240110
	};

	public SSQDoor(DoorInstance actor)
	{
		super(actor);
	}

	@Override
	public void onEvtTwiceClick(final Player player)
	{
		final DoorInstance door = getActor();

		if (door.getReflection().isDefault() || !ArrayUtils.contains(ssqDoors, door.getDoorId()) || !player.isInRange(door, 150))
		{
			return;
		}

		switch (door.getDoorId())
		{
		case 17240102:
			for (NpcInstance n : door.getReflection().getNpcs())
			{
				if (room1.isInside(n.getX(), n.getY(), n.getZ()) && !n.isDead())
				{
					return;
				}
			}
			break;
		case 17240104:
			for (NpcInstance n : door.getReflection().getNpcs())
			{
				if (room2.isInside(n.getX(), n.getY(), n.getZ()) && !n.isDead())
				{
					return;
				}
			}
			break;
		case 17240106:
			for (NpcInstance n : door.getReflection().getNpcs())
			{
				if (room3.isInside(n.getX(), n.getY(), n.getZ()) && !n.isDead())
				{
					return;
				}
			}
			break;
		case 17240108:
			for (NpcInstance n : door.getReflection().getNpcs())
			{
				if (room4.isInside(n.getX(), n.getY(), n.getZ()) && !n.isDead())
				{
					return;
				}
			}
			break;
		case 17240110:
			for (NpcInstance n : door.getReflection().getNpcs())
			{
				if (room5.isInside(n.getX(), n.getY(), n.getZ()) && !n.isDead())
				{
					return;
				}
			}
			break;
		}

		door.getReflection().openDoor(door.getDoorId());
	}
}
