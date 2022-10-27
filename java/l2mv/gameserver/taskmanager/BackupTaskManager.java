package l2mv.gameserver.taskmanager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.dao.DatabaseBackupManager;
import l2mv.gameserver.utils.TimeUtils;

public final class BackupTaskManager
{
	private static final Logger LOG = LoggerFactory.getLogger(BackupTaskManager.class);

	private static final int MAX_ITERATIONS_TILL_BREAK = 10;

	public static void startThread()
	{
		scheduleNextBackup();
	}

	private static void scheduleNextBackup()
	{
		final long millisTillBackup = getMillisTillNextBackup();
		if (millisTillBackup > 0L)
		{
			printNextBackupDate(millisTillBackup);
			ThreadPoolManager.getInstance().schedule(new AutoBackup(), millisTillBackup);
		}
	}

	private static long getMillisTillNextBackup()
	{
		final Calendar c = Calendar.getInstance();
		final long currentTime = c.getTimeInMillis();
		c.set(13, 0);
		c.set(14, 0);
		final int[][] dates = Config.ADDITIONAL_BACKUP_DATES;
		if (dates.length == 0)
		{
			return -1L;
		}
		int iterations = 0;
		int index = 0;
		while (iterations <= MAX_ITERATIONS_TILL_BREAK)
		{
			if (dates.length <= index)
			{
				c.add(6, 1);
				index = 0;
			}
			c.set(11, dates[index][0]);
			c.set(12, dates[index][1]);
			++index;
			++iterations;
			if (c.getTimeInMillis() >= currentTime)
			{
				return c.getTimeInMillis() - currentTime;
			}
		}
		LOG.warn("Config AdditionalBackupDates data is wrong! It would cause infinite loop.");
		return -1L;
	}

	private static void printNextBackupDate(long millisTillBackup)
	{
		LOG.info("Next Auto Backup in " + TimeUtils.minutesToFullString(TimeUnit.MILLISECONDS.toMinutes(millisTillBackup)));
	}

	private static class AutoBackup extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			final long beforeBackupTime = System.currentTimeMillis();
			DatabaseBackupManager.getInstance().doBackup(true);
			final long afterBackupTime = System.currentTimeMillis();
			LOG.info("Backup finished. Server frozen for " + TimeUnit.MILLISECONDS.toSeconds(afterBackupTime - beforeBackupTime) + " seconds!");
			scheduleNextBackup();
		}
	}
}
