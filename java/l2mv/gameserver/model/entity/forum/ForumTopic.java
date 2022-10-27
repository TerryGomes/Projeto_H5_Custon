package l2mv.gameserver.model.entity.forum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.utils.BatchStatement;

public class ForumTopic implements Comparable<ForumTopic>
{
	private static final Logger LOG = LoggerFactory.getLogger(ForumTopic.class);

	private final int topicId;
	private final ForumBoard board;
	private boolean isSticky;
	private boolean isLocked;
	private boolean isSaved;
	private int views;
	private boolean isLastPostSavedInDb = true;
	private List<ForumPost> posts = new CopyOnWriteArrayList<ForumPost>();

	public ForumTopic(int topicId, ForumBoard board, boolean isSticky, boolean isLocked, boolean isSaved, int views)
	{
		this.topicId = topicId;
		this.board = board;
		this.isSticky = isSticky;
		this.isLocked = isLocked;
		this.isSaved = isSaved;
		this.views = views;
	}

	public int getTopicId()
	{
		return topicId;
	}

	public ForumBoard getBoard()
	{
		return board;
	}

	public void setSticky(boolean isSticky)
	{
		this.isSticky = isSticky;
	}

	public boolean isSticky()
	{
		return isSticky;
	}

	public void setLocked(boolean isLocked)
	{
		this.isLocked = isLocked;
	}

	public boolean isLocked()
	{
		return isLocked;
	}

	public void setSaved(boolean isSaved)
	{
		this.isSaved = isSaved;
	}

	public boolean isSaved()
	{
		return isSaved;
	}

	public void incViews()
	{
		++views;
	}

	public int getViews()
	{
		return views;
	}

	public void setIsLastPostSavedInDb(boolean isLastPostSavedInDb)
	{
		this.isLastPostSavedInDb = isLastPostSavedInDb;
	}

	public boolean isLastPostSavedInDb()
	{
		return isLastPostSavedInDb;
	}

	public ForumPost getFirstPost()
	{
		return posts.get(0);
	}

	public ForumPost getLastPost()
	{
		if (posts.isEmpty())
		{
			return null;
		}
		return posts.get(posts.size() - 1);
	}

	public int getPostsCount()
	{
		return posts.size();
	}

	public ForumPost getPostById(int postId)
	{
		for (ForumPost post : posts)
		{
			if (post.getPostId() == postId)
			{
				return post;
			}
		}
		return null;
	}

	public void addPost(ForumPost post)
	{
		posts.add(post);
	}

	public void removePost(ForumPost post)
	{
		posts.remove(post);
	}

	public void setPosts(List<ForumPost> posts)
	{
		this.posts = posts;
	}

	public List<ForumPost> getPosts()
	{
		return posts;
	}

	public int getIndexOfPost(ForumPost post)
	{
		return posts.indexOf(post);
	}

	public boolean checkCanAddPost(ForumMember member)
	{
		return !isLocked && !board.getType().isReadOnly() && ConfigHolder.getBool("ForumAllowPosting");
	}

	public boolean isForumOnlyTopic()
	{
		return ArrayUtils.contains(ConfigHolder.getIntArray("ForumOnlyForumTopics"), topicId);
	}

	public ForumPost createNewPost(Player player, String message)
	{
		final ForumMember member = player.getForumMember();
		member.setLastIp(player.getIP());
		final int postId = ForumHandler.getInstance().getNewPostId();
		final ForumPost newPost = new ForumPost(postId, this, player.getForumMember(), System.currentTimeMillis(), getFirstPost().getSubject(), message, false);
		member.incPostCount();
		member.incPostsToIncInDatabase();
		isLastPostSavedInDb = false;
		posts.add(newPost);
		return newPost;
	}

	public static void synchronizeTopics(Connection con, ForumBoard board)
	{
		final Collection<Integer> savedPostIds = new HashSet<Integer>();
		final Collection<Integer> modifiedTopics = new HashSet<Integer>();
		try (PreparedStatement statement = con.prepareStatement("SELECT id_msg, id_topic, poster_time, id_member, subject, modified_time, modified_name, body FROM smf_messages WHERE id_board = ?"))
		{
			statement.setInt(1, board.getType().getBoardDatabaseId());
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					final int topicId = rset.getInt("id_topic");
					final int messageId = rset.getInt("id_msg");
					final boolean topicModified = loadTopicFromDatabase(board, rset);
					if (topicModified)
					{
						modifiedTopics.add(topicId);
					}
					savedPostIds.add(messageId);
				}
			}
		}
		catch (SQLException e)
		{
			ForumTopic.LOG.error("Error while loading Posts from Forum Database!", e);
		}
		for (ForumTopic topic : board.getTopics())
		{
			for (ForumPost post : topic.posts)
			{
				if (post.isSaved() && !savedPostIds.contains(post.getPostId()))
				{
					topic.removePost(post);
				}
			}
		}
		try (PreparedStatement statement = BatchStatement.createPreparedStatement(con, "REPLACE INTO smf_messages (id_msg, id_topic, id_board, poster_time, id_member, id_msg_modified, subject, poster_name, poster_email, poster_ip, body, icon) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)"))
		{
			for (ForumTopic topic2 : board.getTopics())
			{
				for (ForumPost post2 : topic2.posts)
				{
					if (!post2.isSaved())
					{
						post2.setSaved(true);
						statement.setInt(1, post2.getPostId());
						statement.setInt(2, topic2.getTopicId());
						statement.setInt(3, topic2.getBoard().getType().getBoardDatabaseId());
						statement.setLong(4, post2.getDate() / 1000L);
						statement.setInt(5, post2.getWriter().getMemberId());
						statement.setInt(6, post2.getPostId());
						statement.setString(7, post2.getSubject());
						statement.setString(8, post2.getWriter().getMemberName());
						statement.setString(9, post2.getWriter().getEmailAddress());
						statement.setString(10, post2.getWriter().getLastIp());
						statement.setString(11, ForumHandler.convertMessageToDb(post2.getMessage()));
						statement.setString(12, "grin");
						statement.addBatch();
					}
				}
			}
			statement.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			ForumTopic.LOG.error("Error while Saving New Post to Forum Database!", e);
		}
		for (Integer modifiedTopicId : modifiedTopics)
		{
			final ForumTopic topic3 = board.getTopicById(modifiedTopicId);
			sortPosts(topic3);
		}
	}

	private static boolean loadTopicFromDatabase(ForumBoard board, ResultSet rset) throws SQLException
	{
		final int messageId = rset.getInt("id_msg");
		final int topicId = rset.getInt("id_topic");
		final boolean topicModified = false;
		final ForumTopic topic = board.getTopicById(topicId);
		if (topic != null)
		{
			final ForumPost existingPost = topic.getPostById(messageId);
			if (existingPost != null)
			{
				final long modifiedTime = rset.getLong("modified_time");
				if (existingPost.getWriter().getMemberId() == -1)
				{
					existingPost.setWriter(ForumMembersHolder.getInstance().getMemberById(rset.getInt("id_member")));
				}
				if (existingPost.getLastModificationDate() / 1000L != modifiedTime)
				{
					String subject = rset.getString("subject");
					subject = ForumHandler.convertSubjectFromDatabase(subject);
					existingPost.setSubject(subject);
					existingPost.setLastModificationDate(modifiedTime * 1000L);
					existingPost.setLastModificationWriter(ForumMembersHolder.getInstance().getMemberByName(rset.getString("modified_name")));
					String body = rset.getString("body");
					body = ForumHandler.convertMessageFromDatabase(body);
					existingPost.setMessage(body);
				}
			}
			else
			{
				final long date = rset.getLong("poster_time") * 1000L;
				final ForumMember writer = ForumMembersHolder.getInstance().getMemberById(rset.getInt("id_member"));
				String subject2 = rset.getString("subject");
				subject2 = ForumHandler.convertSubjectFromDatabase(subject2);
				final long modifiedTime2 = rset.getLong("modified_time");
				final ForumMember modifiedWriter = modifiedTime2 > 0L ? ForumMembersHolder.getInstance().getMemberByName(rset.getString("modified_name")) : null;
				String message = rset.getString("body");
				message = ForumHandler.convertMessageFromDatabase(message);
				final ForumPost newPost = new ForumPost(messageId, topic, writer, date, subject2, message, true, modifiedTime2, modifiedWriter);
				topic.addPost(newPost);
				if (messageId > ForumHandler.getInstance().getRealLastPostId())
				{
					ForumHandler.getInstance().setLastPostId(messageId);
				}
			}
		}
		return topicModified;
	}

	private static void sortPosts(ForumTopic topic)
	{
		final List<ForumPost> posts = topic.posts;
		final ForumPost[] postsArray = new ForumPost[posts.size()];
		posts.toArray(postsArray);
		Arrays.sort(postsArray);
		topic.posts = new CopyOnWriteArrayList<ForumPost>(posts);
	}

	@Override
	public int compareTo(ForumTopic o)
	{
		final int stickyCompare = Boolean.compare(o.isSticky, isSticky);
		if (stickyCompare != 0)
		{
			return stickyCompare;
		}
		if (getLastPost() == null && o.getLastPost() == null)
		{
			return 0;
		}
		if (getLastPost() == null)
		{
			return 1;
		}
		if (o.getLastPost() == null)
		{
			return -1;
		}
		return Long.valueOf(o.getLastPost().getDate()).compareTo(Long.valueOf(getLastPost().getDate()));
	}
}
