package l2mv.gameserver.model.entity.forum;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import l2mv.gameserver.ConfigHolder;

public class ForumHandler
{
	private static final String[] ALLOWED_TAGS = new String[]
	{
		"quote"
	};

	private final Map<Integer, ForumBoard> boards = new HashMap<Integer, ForumBoard>();
	private int lastTopicId = 0;
	private int lastPostId = 0;

	private ForumHandler()
	{
		for (ForumBoardType boardType : ForumBoardType.values())
		{
			boards.put(boardType.getBoardIndex(), new ForumBoard(boardType));
		}
	}

	public ForumBoard getBoardByIndex(int boardIndex)
	{
		return boards.get(boardIndex);
	}

	public Collection<ForumBoard> getBoards()
	{
		return boards.values();
	}

	public ForumTopic getTopicById(int topicId)
	{
		for (ForumBoard board : boards.values())
		{
			for (ForumTopic topic : board.getTopics())
			{
				if (topic.getTopicId() == topicId)
				{
					return topic;
				}
			}
		}
		return null;
	}

	public ForumPost getPostById(int postId)
	{
		for (ForumBoard board : boards.values())
		{
			for (ForumTopic topic : board.getTopics())
			{
				for (ForumPost post : topic.getPosts())
				{
					if (post.getPostId() == postId)
					{
						return post;
					}
				}
			}
		}
		return null;
	}

	public void setLastTopicId(int lastTopicId)
	{
		this.lastTopicId = lastTopicId;
	}

	public int getRealLastTopicId()
	{
		return lastTopicId;
	}

	public int getNewTopicId()
	{
		return lastTopicId += 3;
	}

	public void setLastPostId(int lastPostId)
	{
		this.lastPostId = lastPostId;
	}

	public int getRealLastPostId()
	{
		return lastPostId;
	}

	public int getNewPostId()
	{
		return lastPostId += 6;
	}

	public static String convertSubjectFromDatabase(String subject)
	{
		String newSubject = subject.replace("<br />", "");
		newSubject = newSubject.replace("<br/>", "");
		newSubject = newSubject.replace("<br>", "");
		newSubject = newSubject.replace("&#039;", "");
		return newSubject;
	}

	public static String convertMessageFromDatabase(String postMessage)
	{
		String newMessage = replaceTags(postMessage, '[', ']', ForumHandler.ALLOWED_TAGS);
		newMessage = newMessage.replace("\r", "");
		newMessage = newMessage.replace("<br />", "<br1>");
		newMessage = newMessage.replace("<br/>", "<br1>");
		newMessage = newMessage.replace("<br>", "<br1>");
		newMessage = newMessage.replace("&#039;", "");
		if (newMessage.length() > ConfigHolder.getInt("ForumMaxPostMessageLength") || StringUtils.countMatches(newMessage, "<br1>") > 12)
		{
			return ConfigHolder.getString("ForumTooLongPostMsg");
		}
		return newMessage;
	}

	public static String replaceTags(String message, char startTagChar, char endTagChar, String[] allowedTags)
	{
		String newMessage = message;
		int startFromChar = 0;
		while (startFromChar < newMessage.length() - 2)
		{
			final int startTag = newMessage.indexOf(startTagChar, startFromChar);
			final int endTag = newMessage.indexOf(endTagChar, startTag);
			if (startTag < 0 || endTag <= 0)
			{
				return newMessage;
			}
			final String textWithTags = newMessage.substring(startTag, endTag + 1);
			if (startTag + 1 < endTag)
			{
				final String textWithoutBrackets = newMessage.substring(startTag + 1, endTag);
				if (isAllowedTag(allowedTags, textWithoutBrackets))
				{
					startFromChar = endTag;
				}
				else
				{
					newMessage = newMessage.replace(textWithTags, "");
				}
			}
			else
			{
				newMessage = newMessage.replace(textWithTags, "");
			}
		}
		return newMessage;
	}

	private static boolean isAllowedTag(String[] allowedTags, String textWithoutBrackets)
	{
		for (String allowed : allowedTags)
		{
			if (textWithoutBrackets.startsWith(allowed) || textWithoutBrackets.startsWith('/' + allowed))
			{
				return true;
			}
		}
		return false;
	}

	public static String convertMessageToDb(String message)
	{
		final String newMessage = message.replace("<br1>", "<br />");
		return newMessage;
	}

	public static String convertMessageFromTextBox(String message)
	{
		final String newMessage = message.replace("\n", "<br1>");
		return newMessage;
	}

	public static ForumHandler getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final ForumHandler instance = new ForumHandler();
	}
}
