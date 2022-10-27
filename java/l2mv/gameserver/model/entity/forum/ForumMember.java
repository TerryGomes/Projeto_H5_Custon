package l2mv.gameserver.model.entity.forum;

import java.util.ArrayList;
import java.util.List;

import l2mv.commons.annotations.Nullable;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;

public class ForumMember
{
	private final int memberId;
	private final String memberName;
	private final String passwordHash;
	private final ForumMemberGroup memberGroup;
	private final String emailAddress;
	private int postCount = 0;
	private int postsToIncInDatabase = 0;
	private int warningLevel;
	private String lastIp;

	public ForumMember(int memberId, String memberName, String passwordHash, ForumMemberGroup memberGroup, String emailAddress, int warningLevel)
	{
		this.memberId = memberId;
		this.memberName = memberName;
		this.passwordHash = passwordHash;
		this.memberGroup = memberGroup;
		this.emailAddress = emailAddress;
		this.warningLevel = warningLevel;
	}

	public int getMemberId()
	{
		return memberId;
	}

	public String getMemberName()
	{
		return memberName;
	}

	public ForumMemberGroup getMemberGroup()
	{
		return memberGroup;
	}

	public void incPostCount()
	{
		++postCount;
	}

	public void setPostCount(int postCount)
	{
		this.postCount = postCount;
	}

	public int getPostCount()
	{
		return postCount;
	}

	public void setPostsToIncInDatabase(int postsToIncInDatabase)
	{
		this.postsToIncInDatabase = postsToIncInDatabase;
	}

	public void incPostsToIncInDatabase()
	{
		++postsToIncInDatabase;
	}

	public int getPostCountToIncInDatabase()
	{
		return postsToIncInDatabase;
	}

	public void setWarningLevel(int warningLevel)
	{
		this.warningLevel = warningLevel;
	}

	public int getWarningLevel()
	{
		return warningLevel;
	}

	public String getPasswordHash()
	{
		return passwordHash;
	}

	public String getEmailAddress()
	{
		return emailAddress;
	}

	public void setLastIp(String lastIp)
	{
		this.lastIp = lastIp;
	}

	public String getLastIp()
	{
		return lastIp;
	}

	@Nullable
	public Player getFirstOnlineOwner()
	{
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player.getForumMember() != null && player.getForumMember().memberId == memberId)
			{
				return player;
			}
		}
		return null;
	}

	public List<Player> getOnlineOwners()
	{
		final List<Player> owners = new ArrayList<Player>();
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player.getForumMember() != null && player.getForumMember().memberId == memberId)
			{
				owners.add(player);
			}
		}
		return owners;
	}
}
