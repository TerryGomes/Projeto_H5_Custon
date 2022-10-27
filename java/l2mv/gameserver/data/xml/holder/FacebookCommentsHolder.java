package l2mv.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;

import l2mv.commons.data.xml.AbstractHolder;
import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.GameServer;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.fandc.facebook.ActiveTask;
import l2mv.gameserver.fandc.facebook.FacebookAction;
import l2mv.gameserver.fandc.facebook.FacebookIdentityType;
import l2mv.gameserver.fandc.facebook.OfficialPost;
import l2mv.gameserver.listener.game.OnConfigsReloaded;
import l2mv.gameserver.utils.Log;

public final class FacebookCommentsHolder extends AbstractHolder implements OnConfigsReloaded
{
	private static final char[] CHARS_TO_NOT_CHECK = new char[]
	{
		' ',
		'!',
		'$',
		'^',
		',',
		'.',
		'?'
	};

	private static final String NICKNAME_SYNTAX_COMMENT_KEY = "%originalComment%";
	private static final String NICKNAME_SYNTAX_CHAR_NAME_KEY = "%charName%";
	private static final int FIND_NOT_USED_COMMENT_MAX_TRIES = 15;
	private static final long CLEAR_LAST_USED_COMMENTS_DELAY = 30000L;

	private final HashMap<String, ArrayList<String>> _commentsByType;
	private final ConcurrentHashMap<String, Long> _lastUsedComments;
	private final ScheduledFuture<?> _clearLastUsedCommentsThread;
	private Pattern COMMENT_WITH_CHAR_NAME_PATTERN;

	private FacebookCommentsHolder()
	{
		_commentsByType = new HashMap<String, ArrayList<String>>(8);
		_lastUsedComments = new ConcurrentHashMap<String, Long>();
		GameServer.getInstance().addListener(this);
		reloadPattern();
		_clearLastUsedCommentsThread = ThreadPoolManager.getInstance().scheduleAtFixedDelay(new ClearLastUsedCommentsThread(_lastUsedComments), CLEAR_LAST_USED_COMMENTS_DELAY, CLEAR_LAST_USED_COMMENTS_DELAY);
	}

	public void addNewComment(String type, String comment)
	{
		ArrayList<String> comments = _commentsByType.get(type);
		if (comments == null)
		{
			comments = new ArrayList<String>(64);
			comments.add(comment.trim());
			_commentsByType.put(type, comments);
		}
		else
		{
			comments.add(comment.trim());
		}
	}

	public String getCommentToWrite(OfficialPost fatherAction, FacebookIdentityType identityType, String identityValue)
	{
		final String[] acceptedCommentTypes = _commentsByType.keySet().toArray(new String[_commentsByType.size()]);
		int tryIndex = 0;
		String comment = null;
		while (tryIndex < FIND_NOT_USED_COMMENT_MAX_TRIES)
		{
			comment = chooseRandomComment(acceptedCommentTypes);
			if (comment != null)
			{
				if (!_lastUsedComments.containsKey(comment))
				{
					addCommentToLastUsed(comment);
					return prepareComment(comment, identityType, identityValue);
				}
			}
			++tryIndex;
		}
		return comment;
	}

	public CommentMatchType checkCommentMatches(ActiveTask task, FacebookAction action)
	{
		if (task.getRequestedMessage().isEmpty() || action.getMessage().isEmpty())
		{
			if (task.getIdentityType() == FacebookIdentityType.NAME_IN_COMMENT)
			{
				return CommentMatchType.NONE_MATCHES;
			}
			return task.getRequestedMessage().isEmpty() && action.getMessage().isEmpty() ? CommentMatchType.FULL_MATCH : CommentMatchType.COMMENT_NOT_MATCHES;
		}
		else
		{
			if (task.getIdentityType() != FacebookIdentityType.NAME_IN_COMMENT)
			{
				return commentMatches(action.getMessage(), task.getRequestedMessage()) ? CommentMatchType.FULL_MATCH : CommentMatchType.COMMENT_NOT_MATCHES;
			}
			final Matcher wroteMessageMatcher = COMMENT_WITH_CHAR_NAME_PATTERN.matcher(action.getMessage());
			if (!wroteMessageMatcher.matches())
			{
				return CommentMatchType.NONE_MATCHES;
			}
			final Matcher requestedMessageMatcher = COMMENT_WITH_CHAR_NAME_PATTERN.matcher(task.getRequestedMessage());
			if (!requestedMessageMatcher.matches())
			{
				_log.error("Requested Message does not match Matcher! Msg: " + task.getRequestedMessage());
				Log.logFacebook("Requested Message does not match Matcher! Msg: " + task.getRequestedMessage());
				return CommentMatchType.NONE_MATCHES;
			}
			final boolean nameMatches = wroteMessageMatcher.group("charName").equalsIgnoreCase(requestedMessageMatcher.group("charName"));
			final boolean commentMatches = commentMatches(wroteMessageMatcher.group("comment"), requestedMessageMatcher.group("comment"));
			if (nameMatches && commentMatches)
			{
				return CommentMatchType.FULL_MATCH;
			}
			if (!nameMatches && !commentMatches)
			{
				return CommentMatchType.NONE_MATCHES;
			}
			if (nameMatches)
			{
				return CommentMatchType.COMMENT_NOT_MATCHES;
			}
			return CommentMatchType.IDENTITY_NOT_MATCHES;
		}
	}

	private static boolean commentMatches(String requestedMessage, String wroteMessage)
	{
		return prepareMsgToCompere(requestedMessage).equalsIgnoreCase(prepareMsgToCompere(wroteMessage));
	}

	private static String prepareComment(String comment, FacebookIdentityType identityType, CharSequence identityValue)
	{
		if (identityType == FacebookIdentityType.NAME_IN_COMMENT)
		{
			String preparedComment = ConfigHolder.getString("FacebookRegistrationNicknameSyntax").replace(NICKNAME_SYNTAX_COMMENT_KEY, comment);
			preparedComment = preparedComment.replace(NICKNAME_SYNTAX_CHAR_NAME_KEY, identityValue);
			return preparedComment;
		}
		return comment;
	}

	private String chooseRandomComment(String... fromTypes)
	{
		final int totalSize = countTotalSize(fromTypes);
		if (totalSize <= 0)
		{
			return null;
		}
		int commentIndex = Rnd.get(0, totalSize - 1);
		if (commentIndex < 0)
		{
			_log.error("Error while choosing random Comment, commentIndex = " + commentIndex + ". totalSize: " + totalSize + ". fromTypes: " + Arrays.toString(fromTypes));
			commentIndex = 0;
		}
		int reachedIndex = 0;
		try
		{
			for (Map.Entry<String, ArrayList<String>> entry : _commentsByType.entrySet())
			{
				if (ArrayUtils.contains(fromTypes, entry.getKey()))
				{
					if (reachedIndex + entry.getValue().size() - 1 > commentIndex)
					{
						return entry.getValue().get(commentIndex - reachedIndex);
					}
					reachedIndex += entry.getValue().size();
				}
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			_log.error("OutOfBounds in chooseRandomComment! commentIndex: " + commentIndex + ". totalSize: " + totalSize + ". fromTypes: " + Arrays.toString(fromTypes) + ". reachedIndex: " + reachedIndex, e);
		}
		return null;
	}

	private int countTotalSize(String... fromTypes)
	{
		int totalSize = 0;
		for (Map.Entry<String, ArrayList<String>> entry : _commentsByType.entrySet())
		{
			if (ArrayUtils.contains(fromTypes, entry.getKey()))
			{
				totalSize += entry.getValue().size();
			}
		}
		return totalSize;
	}

	private void addCommentToLastUsed(String comment)
	{
		_lastUsedComments.put(comment, System.currentTimeMillis() + ConfigHolder.getMillis("FacebookCommentContentReuse", TimeUnit.SECONDS));
	}

	private void reloadPattern()
	{
		final String commentSyntax = ConfigHolder.getString("FacebookRegistrationNicknameSyntax");
		String pattern = commentSyntax.replace(NICKNAME_SYNTAX_COMMENT_KEY, "(?<comment>[\\S\\s]+)");
		pattern = pattern.replace(NICKNAME_SYNTAX_CHAR_NAME_KEY, "(?<charName>" + Config.CHAR_NAME_TEMPLATE + ")");
		COMMENT_WITH_CHAR_NAME_PATTERN = Pattern.compile(pattern, 2);
	}

	public Set<String> getCommentTypesForIterate()
	{
		return _commentsByType.keySet();
	}

	public HashSet<String> getCommentTypesCopy()
	{
		return new HashSet<String>(_commentsByType.keySet());
	}

	public ArrayList<String> getCommentsForIterate(String type)
	{
		return _commentsByType.get(type);
	}

	public ArrayList<String> getCommentsCopy(String type)
	{
		return new ArrayList<String>(_commentsByType.get(type));
	}

	private static String prepareMsgToCompere(String msg)
	{
		final StringBuilder builder = new StringBuilder(msg.length());
		for (char c : msg.toCharArray())
		{
			if (!ArrayUtils.contains(FacebookCommentsHolder.CHARS_TO_NOT_CHECK, c))
			{
				builder.append(c);
			}
		}
		return builder.toString();
	}

	@Override
	protected void process()
	{
		for (ArrayList<String> comments : _commentsByType.values())
		{
			comments.trimToSize();
		}
	}

	@Override
	public int size()
	{
		return _commentsByType.size();
	}

	@Override
	public void clear()
	{
		_commentsByType.clear();
	}

	@Override
	public void onConfigsReloaded()
	{
		reloadPattern();
	}

	public static FacebookCommentsHolder getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		private static final FacebookCommentsHolder INSTANCE = new FacebookCommentsHolder();
	}

	@Override
	public String toString()
	{
		return "FacebookCommentsHolder{commentsByType=" + _commentsByType + '}';
	}

	public enum CommentMatchType
	{
		FULL_MATCH, COMMENT_NOT_MATCHES, IDENTITY_NOT_MATCHES, NONE_MATCHES;
	}

	private static class ClearLastUsedCommentsThread extends RunnableImpl
	{
		private final ConcurrentHashMap<String, Long> lastUsedComments;

		ClearLastUsedCommentsThread(ConcurrentHashMap<String, Long> lastUsedComments)
		{
			super();
			this.lastUsedComments = lastUsedComments;
		}

		@Override
		public void runImpl()
		{
			final long currentDate = System.currentTimeMillis();
			final ArrayList<String> commentsToDelete = new ArrayList<String>(3);
			for (Map.Entry<String, Long> lastUsedCommentEntry : lastUsedComments.entrySet())
			{
				if (lastUsedCommentEntry.getValue() < currentDate)
				{
					commentsToDelete.add(lastUsedCommentEntry.getKey());
				}
			}
			for (String commentToDelete : commentsToDelete)
			{
				lastUsedComments.remove(commentToDelete);
			}
		}
	}
}
