package l2mv.gameserver.fandc.facebook.action;

import java.sql.ResultSet;
import java.sql.SQLException;

import l2mv.gameserver.fandc.facebook.FacebookAction;
import l2mv.gameserver.fandc.facebook.FacebookActionType;
import l2mv.gameserver.fandc.facebook.FacebookProfile;
import l2mv.gameserver.fandc.facebook.FacebookProfilesHolder;
import l2mv.gameserver.fandc.facebook.OfficialPostsHolder;

public class Comment implements FacebookAction
{
	private final String id;
	private final FacebookProfile executor;
	private String message;
	private final long createdTime;
	private final long extractionDate;
	private final FacebookAction fatherAction;

	public Comment(String id, FacebookProfile executor, String message, long createdTime, long extractionDate, FacebookAction fatherAction)
	{
		this.id = id;
		this.executor = executor;
		this.message = message;
		this.createdTime = createdTime;
		this.extractionDate = extractionDate;
		this.fatherAction = fatherAction;
	}

	public Comment(ResultSet rset) throws SQLException
	{
		id = rset.getString("action_id");
		executor = FacebookProfilesHolder.getInstance().getProfileById(rset.getString("executor_id"));
		message = rset.getString("message");
		createdTime = rset.getLong("created_date");
		extractionDate = rset.getLong("extraction_date");
		fatherAction = OfficialPostsHolder.getInstance().getOfficialPost(rset.getString("father_id"));
	}

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public FacebookActionType getActionType()
	{
		return FacebookActionType.COMMENT;
	}

	@Override
	public FacebookProfile getExecutor()
	{
		return executor;
	}

	@Override
	public long getCreatedDate()
	{
		return createdTime;
	}

	@Override
	public long getExtractionDate()
	{
		return extractionDate;
	}

	@Override
	public String getMessage()
	{
		return message;
	}

	@Override
	public void changeMessage(String newMessage)
	{
		message = newMessage;
	}

	@Override
	public FacebookAction getFather()
	{
		return fatherAction;
	}

	@Override
	public boolean canBeRemoved()
	{
		return false;
	}

	@Override
	public void remove()
	{
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Comment && id.equals(((Comment) obj).id);
	}

	@Override
	public int hashCode()
	{
		return id.hashCode();
	}

	@Override
	public String toString()
	{
		return "Comment{id='" + id + '\'' + ", executor=" + executor + ", message='" + message + '\'' + ", createdTime=" + createdTime + ", extractionDate=" + extractionDate + ", fatherAction=" + fatherAction + '}';
	}
}
