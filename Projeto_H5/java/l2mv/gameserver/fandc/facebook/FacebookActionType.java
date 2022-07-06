package l2mv.gameserver.fandc.facebook;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import l2mv.gameserver.fandc.facebook.action.Comment;
import l2mv.gameserver.fandc.facebook.action.Like;
import l2mv.gameserver.fandc.facebook.action.Post;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.data.StringHolder;
import l2mv.gameserver.utils.Language;

public enum FacebookActionType
{
	LIKE("Facebook.Action.Like", "FacebookRewardLikeTask", "FacebookRewardLikeNoTask", true, false), COMMENT("Facebook.Action.Comment", "FacebookRewardCommentTask", "FacebookRewardCommentNoTask", true, true), POST("Facebook.Action.Post", (String) null, (String) null, false, false), SHARE("Facebook.Action.Share", (String) null, (String) null, true, false);

	private final String actionNameAddress;
	private final String rewardTaskConfig;
	private final String rewardNoTaskConfig;
	private final boolean haveFather;
	private final boolean haveCommentMessage;

	private FacebookActionType(String actionNameAddress, String rewardTaskConfig, String rewardNoTaskConfig, boolean haveFather, boolean haveCommentMessage)
	{
		this.actionNameAddress = actionNameAddress;
		this.rewardTaskConfig = rewardTaskConfig;
		this.rewardNoTaskConfig = rewardNoTaskConfig;
		this.haveFather = haveFather;
		this.haveCommentMessage = haveCommentMessage;
	}

	public String getActionName(Language language)
	{
		return StringHolder.getNotNull(language, actionNameAddress, new Object[0]);
	}

	public boolean isRewarded()
	{
		return rewardTaskConfig != null && rewardNoTaskConfig != null;
	}

	public Map<Integer, Long> getRewardForTask()
	{
		return ConfigHolder.getMap(rewardTaskConfig, Integer.class, Long.class);
	}

	public Map<Integer, Long> getRewardForNoTask()
	{
		return ConfigHolder.getMap(rewardNoTaskConfig, Integer.class, Long.class);
	}

	public boolean haveFather()
	{
		return haveFather;
	}

	public boolean haveCommentMessage()
	{
		return haveCommentMessage;
	}

	public FacebookAction createInstance(ResultSet rset) throws SQLException
	{
		switch (this)
		{
		case LIKE:
		{
			return new Like(rset);
		}
		case COMMENT:
		{
			return new Comment(rset);
		}
		case POST:
		{
			return new Post(rset);
		}
		default:
		{
			return null;
		}
		}
	}
}
