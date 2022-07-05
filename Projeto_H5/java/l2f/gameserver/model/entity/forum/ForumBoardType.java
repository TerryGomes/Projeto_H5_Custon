package l2f.gameserver.model.entity.forum;

import l2f.gameserver.ConfigHolder;

public enum ForumBoardType
{
	ANNOUNCEMENTS("Announcements", 1, "ForumBoardIdAnnouncements", true), HALLOFFAME("Hall of Fame", 2, "ForumBoardIdHallofFame", true), EVENTS("Events", 3, "ForumBoardIdEvents", true),
	RECRUITMENT("Recruitment", 4, "ForumBoardIdRecruitment", true), GENERAL_DISCUSSION("General Discussion", 5, "ForumBoardIdGeneralDiscussion", false),
	SUGGESTIONS("Suggestions", 6, "ForumBoardIdSuggestions", false), BUG_TRACKER("Bug Tracker", 7, "ForumBoardIdBugTracker", false);

	private final String niceName;
	private final int boardIndex;
	private final String databaseIdConfig;
	private final boolean readOnly;

	private ForumBoardType(String niceName, int boardIndex, String databaseIdConfig, boolean readOnly)
	{
		this.niceName = niceName;
		this.boardIndex = boardIndex;
		this.databaseIdConfig = databaseIdConfig;
		this.readOnly = readOnly;
	}

	public String getNiceName()
	{
		return niceName;
	}

	public int getBoardIndex()
	{
		return boardIndex;
	}

	public int getBoardDatabaseId()
	{
		return ConfigHolder.getInt(databaseIdConfig);
	}

	public boolean isReadOnly()
	{
		return readOnly;
	}

	public static ForumBoardType getTypeByIndex(int boardIndex)
	{
		for (ForumBoardType type : values())
		{
			if (type.boardIndex == boardIndex)
			{
				return type;
			}
		}
		return null;
	}
}
