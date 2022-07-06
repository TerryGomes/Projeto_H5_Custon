package l2mv.gameserver.utils;

import l2mv.gameserver.model.GameObject;

public class NpcGroupLocation extends GroupLocation
{
	private final int npcId;

	public NpcGroupLocation(String groupName, int npcId, int x, int y, int z, int heading)
	{
		super(groupName, x, y, z);
		h = heading;

		this.npcId = npcId;
	}

	public NpcGroupLocation(String groupName, int npcId, int x, int y, int z)
	{
		this(groupName, npcId, x, y, z, 0);
	}

	public NpcGroupLocation(String groupName, int npcId, GameObject obj)
	{
		this(groupName, npcId, obj.getX(), obj.getY(), obj.getZ(), obj.getHeading());
	}

	public NpcGroupLocation(String groupName, int npcId, Location loc)
	{
		this(groupName, npcId, loc.getX(), loc.getY(), loc.getZ(), loc.h);
	}

	public int getNpcId()
	{
		return npcId;
	}
}
