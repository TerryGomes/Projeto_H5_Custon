package l2mv.gameserver.model.entity.forum;

public class ForumPost implements Comparable<ForumPost>
{
	private final int postId;
	private final ForumTopic topic;
	private ForumMember writer;
	private final long date;
	private String subject;
	private String message;
	private boolean isSaved;
	private long lastModificationDate = -1L;
	private ForumMember lastModificationWriter;

	public ForumPost(int postId, ForumTopic topic, ForumMember writer, long date, String subject, String message, boolean isSaved)
	{
		this.postId = postId;
		this.topic = topic;
		this.writer = writer;
		this.date = date;
		this.subject = subject;
		this.message = message;
		this.isSaved = isSaved;
	}

	public ForumPost(int postId, ForumTopic topic, ForumMember writer, long date, String subject, String message, boolean isSaved, long lastModificationDate, ForumMember lastModificationWriter)
	{
		this(postId, topic, writer, date, subject, message, isSaved);
		this.lastModificationDate = lastModificationDate;
		this.lastModificationWriter = lastModificationWriter;
	}

	public int getPostId()
	{
		return postId;
	}

	public ForumTopic getTopic()
	{
		return topic;
	}

	public ForumMember getWriter()
	{
		return writer;
	}

	public void setWriter(ForumMember writer)
	{
		this.writer = writer;
	}

	public long getDate()
	{
		return date;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public String getSubject()
	{
		return subject;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getMessage()
	{
		return message;
	}

	public void setSaved(boolean isSaved)
	{
		this.isSaved = isSaved;
	}

	public boolean isSaved()
	{
		return isSaved;
	}

	public void setLastModificationDate(long lastModificationDate)
	{
		this.lastModificationDate = lastModificationDate;
	}

	public long getLastModificationDate()
	{
		return lastModificationDate;
	}

	public void setLastModificationWriter(ForumMember lastModWriter)
	{
		lastModificationWriter = lastModWriter;
	}

	public ForumMember getLastModificationWriter()
	{
		return lastModificationWriter;
	}

	@Override
	public int compareTo(ForumPost o)
	{
		return Long.valueOf(o.date).compareTo(Long.valueOf(date));
	}
}
