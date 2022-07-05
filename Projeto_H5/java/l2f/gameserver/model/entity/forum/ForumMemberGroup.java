package l2f.gameserver.model.entity.forum;

public enum ForumMemberGroup
{
	ADMINISTRATOR(1, "9a0505"), GLOBAL_MODERATOR(2, "0653a2"), MODERATOR(3, "068025"), NORMAL(0, "63bea6"), DELETED(-2, "4a4d4b");

	private final int groupId;
	private final String color;

	private ForumMemberGroup(int groupId, String color)
	{
		this.groupId = groupId;
		this.color = color;
	}

	public int getGroupId()
	{
		return groupId;
	}

	public String getColor()
	{
		return color;
	}

	public static ForumMemberGroup findGroup(int groupId)
	{
		for (ForumMemberGroup group : values())
		{
			if (group.groupId == groupId)
			{
				return group;
			}
		}
		return ForumMemberGroup.NORMAL;
	}
}
