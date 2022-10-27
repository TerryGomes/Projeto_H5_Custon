package l2mv.gameserver.model.entity.forum;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.database.ForumDatabaseFactory;

public final class ForumDatabaseHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(ForumDatabaseHandler.class);

	private final AtomicInteger currentThreadsCount = new AtomicInteger(0);

	private ForumDatabaseHandler()
	{
		if (!ConfigHolder.getBool("AllowForum"))
		{
			return;
		}

		ThreadPoolManager.getInstance().scheduleAtFixedRate(new ThreadStarted(), 0L, ConfigHolder.getLong("ForumTasksDelay"));
	}

	private AtomicInteger getCurrentThreadsCount()
	{
		return currentThreadsCount;
	}

	public static ForumDatabaseHandler getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final ForumDatabaseHandler instance = new ForumDatabaseHandler();
	}

	private static class ThreadStarted extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if (ForumDatabaseHandler.getInstance().getCurrentThreadsCount().get() >= ConfigHolder.getInt("ForumMaxTasksInSameMoment"))
			{
				return;
			}

			ForumDatabaseHandler.getInstance().getCurrentThreadsCount().incrementAndGet();
			final Thread realThread = new Thread(new ForumSynchronization());
			realThread.start();
		}
	}

	private static class ForumSynchronization extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if (!ConfigHolder.getBool("AllowForum"))
			{
				ForumDatabaseHandler.getInstance().getCurrentThreadsCount().decrementAndGet();
				return;
			}

			try (Connection con = ForumDatabaseFactory.getInstance().getConnection())
			{
				ForumMembersHolder.getInstance().synchronizeMembers(con);
				ForumMembersHolder.synchronizeOnlineStatus(con);
				for (ForumBoard board : ForumHandler.getInstance().getBoards())
				{
					board.synchronizeBoard(con);
				}
				ShoutboxHandler.getInstance().synchronizeShoutbox(con);
			}
			catch (SQLException e)
			{
				LOG.error("Error while connecting to Forum Database!", e);
			}
			ForumDatabaseHandler.getInstance().getCurrentThreadsCount().decrementAndGet();
		}
	}
}
