package l2f.gameserver.model.entity.forum;

public class ForumShout
{
	private final long idShout;
	private final int idMember;
	private final String shoutWriter;
	private final ForumMemberGroup writerGroup;
	private final String message;
	private final long creationTime;

	public ForumShout(long idShout, int idMember, String shoutWriter, ForumMemberGroup writerGroup, String message, long creationTime)
	{
		this.idShout = idShout;
		this.idMember = idMember;
		this.shoutWriter = shoutWriter;
		this.writerGroup = writerGroup;
		this.message = message;
		this.creationTime = creationTime;
	}

	public long getIdShout()
	{
		return idShout;
	}

	public int getIdMember()
	{
		return idMember;
	}

	public String getShoutWriter()
	{
		return shoutWriter;
	}

	public ForumMemberGroup getWriterGroup()
	{
		return writerGroup;
	}

	public String getMessage()
	{
		return message;
	}

	public long getCreationTime()
	{
		return creationTime;
	}
}
