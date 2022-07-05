package l2f.gameserver.utils;

import l2f.gameserver.model.GameObject;

public class GroupLocation extends Location
{
	private final String groupName;

	public GroupLocation(String groupName, int x, int y, int z, int heading)
	{
		super(x, y, z);
		h = heading;

		this.groupName = groupName;
	}

	public GroupLocation(String groupName, int x, int y, int z)
	{
		this(groupName, x, y, z, 0);
	}

	public GroupLocation(String groupName, GameObject obj)
	{
		this(groupName, obj.getX(), obj.getY(), obj.getZ(), obj.getHeading());
	}

	public GroupLocation(String groupName, Location loc)
	{
		this(groupName, loc.getX(), loc.getY(), loc.getZ(), loc.h);
	}

	public String getGroupName()
	{
		return groupName;
	}
}
