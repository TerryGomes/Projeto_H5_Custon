package l2f.gameserver.model.entity.forum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.ConfigHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.utils.BatchStatement;

/**
 *
 * @author claww
 *
 */
public class ForumBoard
{
	private static final Logger LOG = LoggerFactory.getLogger(ForumBoard.class);

	private final ForumBoardType type;
	private List<ForumTopic> topics = new CopyOnWriteArrayList<ForumTopic>();
	private ForumPost lastSavedLastPost;

	public ForumBoard(ForumBoardType type)
	{
		this.type = type;
	}

	public ForumBoardType getType()
	{
		return type;
	}

	public List<ForumTopic> getTopics()
	{
		return topics;
	}

	public ForumPost getFirstPost()
	{
		if (topics.isEmpty())
		{
			return null;
		}
		return topics.get(0).getLastPost();
	}

	public ForumPost getLastPost()
	{
		ForumTopic firstSticky = null;
		ForumTopic firstNonSticky = null;
		for (ForumTopic topic : topics)
		{
			if (topic.isSticky() && firstSticky == null)
			{
				firstSticky = topic;
			}
			else
			{
				if (!topic.isSticky())
				{
					firstNonSticky = topic;
					break;
				}
				continue;
			}
		}
		if (firstSticky == null && firstNonSticky == null)
		{
			return null;
		}
		if (firstSticky == null)
		{
			return firstNonSticky.getLastPost();
		}
		if (firstNonSticky == null)
		{
			return firstSticky.getLastPost();
		}
		if (firstNonSticky.getLastPost().getPostId() > firstSticky.getLastPost().getPostId())
		{
			return firstNonSticky.getLastPost();
		}
		return firstSticky.getLastPost();
	}

	public int getPostsCount()
	{
		int postsCount = 0;
		for (ForumTopic topic : topics)
		{
			postsCount += topic.getPostsCount();
		}
		return postsCount;
	}

	public int getTopicsCount()
	{
		return topics.size();
	}

	public ForumTopic getTopicById(int topicId)
	{
		for (ForumTopic topic : topics)
		{
			if (topic.getTopicId() == topicId)
			{
				return topic;
			}
		}
		return null;
	}

	public boolean canWriteNewTopic(ForumMember member)
	{
		return !type.isReadOnly() && ConfigHolder.getBool("ForumAllowPosting");
	}

	public void synchronizeBoard(Connection con)
	{
		final ForumHandler forumHandler = ForumHandler.getInstance();
		final Collection<Integer> savedTopicIds = new HashSet<Integer>();
		try (PreparedStatement statement = con.prepareStatement("SELECT id_topic, is_sticky, num_views, locked FROM smf_topics WHERE id_board = ?"))
		{
			statement.setInt(1, type.getBoardDatabaseId());
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					final int topicId = rset.getInt("id_topic");
					final boolean isSticky = rset.getInt("is_sticky") > 0;
					final boolean isLocked = rset.getInt("locked") > 0;
					final ForumTopic existingTopic = getTopicById(topicId);
					if (existingTopic != null)
					{
						if (isSticky != existingTopic.isSticky())
						{
							existingTopic.setSticky(isSticky);
						}
						if (isLocked != existingTopic.isLocked())
						{
							existingTopic.setLocked(isLocked);
						}
					}
					else
					{
						final int views = rset.getInt("num_views");
						final ForumTopic newTopic = new ForumTopic(topicId, this, isSticky, isLocked, true, views);
						topics.add(newTopic);
						if (topicId > forumHandler.getRealLastTopicId())
						{
							forumHandler.setLastTopicId(topicId);
						}
					}
					savedTopicIds.add(topicId);
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while loading Topics from Forum Database!", e);
		}

		for (ForumTopic topic : topics)
		{
			if (topic.isSaved() && !savedTopicIds.contains(topic.getTopicId()))
			{
				topics.remove(topic);
			}
		}

		ForumTopic.synchronizeTopics(con, this);
		try (PreparedStatement statement = BatchStatement.createPreparedStatement(con, "REPLACE INTO smf_topics (id_topic, id_board, id_first_msg, id_last_msg, id_member_started, id_member_updated) VALUES (?,?,?,?,?,?)"))
		{
			for (ForumTopic topic2 : topics)
			{
				if (!topic2.isSaved())
				{
					topic2.setSaved(true);
					final ForumPost firstPost = topic2.getFirstPost();
					statement.setInt(1, topic2.getTopicId());
					statement.setInt(2, topic2.getBoard().getType().getBoardDatabaseId());
					statement.setInt(3, firstPost.getPostId());
					statement.setInt(4, firstPost.getPostId());
					statement.setInt(5, firstPost.getWriter().getMemberId());
					statement.setInt(6, firstPost.getWriter().getMemberId());
					statement.addBatch();
				}
			}
			statement.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			LOG.error("Error while Saving New Topic to Forum Database!", e);
		}

		try (PreparedStatement statement = BatchStatement.createPreparedStatement(con, "UPDATE smf_topics SET id_last_msg = ?, id_member_updated = ?, num_replies = ?, num_views = ? WHERE id_topic = ?"))
		{
			for (ForumTopic topic2 : topics)
			{
				if (!topic2.isLastPostSavedInDb())
				{
					topic2.setIsLastPostSavedInDb(true);
					final ForumPost lastPost = topic2.getLastPost();
					statement.setInt(1, lastPost.getPostId());
					statement.setInt(2, lastPost.getWriter().getMemberId());
					statement.setInt(3, topic2.getPostsCount());
					statement.setInt(4, topic2.getViews());
					statement.setInt(5, topic2.getTopicId());
					statement.addBatch();
				}
			}
			statement.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			LOG.error("Error while Updating Topic after new Reply to Forum Database!", e);
		}

		sortTopics();
		final ForumPost lastPost2 = getLastPost();
		if (lastPost2 != null && !lastPost2.equals(lastSavedLastPost))
		{
			lastSavedLastPost = lastPost2;
			try (PreparedStatement statement2 = con.prepareStatement("UPDATE smf_boards SET id_last_msg = ?, num_topics = ?, num_posts = ? WHERE id_board = ?"))
			{
				statement2.setInt(1, lastPost2.getPostId());
				statement2.setInt(2, getTopicsCount());
				statement2.setInt(3, getPostsCount());
				statement2.setInt(4, type.getBoardDatabaseId());
				statement2.execute();
				con.commit();
			}
			catch (SQLException e2)
			{
				LOG.error("Error while Updating Board data in Forum Database!", e2);
			}
		}
	}

	public ForumPost createNewTopic(Player player, String subject, String message)
	{
		final ForumMember member = player.getForumMember();
		member.setLastIp(player.getIP());
		final int topicId = ForumHandler.getInstance().getNewTopicId();
		final ForumTopic topic = new ForumTopic(topicId, this, false, false, false, 0);
		final int postId = ForumHandler.getInstance().getNewPostId();
		final ForumPost firstPost = new ForumPost(postId, topic, player.getForumMember(), System.currentTimeMillis(), subject, message, false);
		member.incPostCount();
		member.incPostsToIncInDatabase();
		topic.addPost(firstPost);
		topics.add(topic);
		return firstPost;
	}

	private void sortTopics()
	{
		final ForumTopic[] topicsArray = new ForumTopic[topics.size()];
		topics.toArray(topicsArray);
		Arrays.sort(topicsArray);
		topics = new CopyOnWriteArrayList<ForumTopic>(topicsArray);
	}
}
