package l2mv.gameserver.dao;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;

public final class DatabaseBackupManager
{
	private static final Logger _log = LoggerFactory.getLogger(DatabaseBackupManager.class);
	private static final Pattern DATABASE_NAME_PATTERN = Pattern.compile("jdbc:mysql://localhost/(.+?)\\?(.+?)");

	private final String FOLDER_PATH;

	public DatabaseBackupManager()
	{
		FOLDER_PATH = new File("").getAbsolutePath() + "\\log\\backups";
		new File(FOLDER_PATH).mkdir();
	}

	public String doBackup(boolean logToConsole)
	{
		final String databaseName = getDatabaseName();
		if (databaseName == null)
		{
			final String error = "Backup: Error while getting Database Name!";
			if (logToConsole)
			{
				_log.error(error);
			}
			return error;
		}

		final String path = getPath(databaseName);
		if (path == null)
		{
			final String error2 = "Error while creating Backup File!";
			if (logToConsole)
			{
				_log.error(error2);
			}
			return error2;
		}

		String pathToDump = Config.MYSQL_DUMP_PATH;
		if (!pathToDump.isEmpty())
		{
			pathToDump += "\\";
		}
		final String[] cmd =
		{
			pathToDump + "mysqldump",
			databaseName,
			"-r",
			path
		};
		return executeCMD(cmd, logToConsole);
	}

	private String executeCMD(String[] cmd, boolean logToConsole)
	{
		try
		{
			if (logToConsole)
			{
				_log.info("Executing CMD: " + Arrays.toString(cmd));
			}

			final Process runtimeProcess = Runtime.getRuntime().exec(cmd);
			final int processComplete = runtimeProcess.waitFor();
			String returnMsg;
			if (processComplete == 0)
			{
				returnMsg = "Backup finished successfully!";
				if (logToConsole)
				{
					_log.info(returnMsg);
				}
			}
			else
			{
				returnMsg = "Backup Failed! Code: " + processComplete;
				if (logToConsole)
				{
					_log.info(returnMsg);
				}
			}
			return returnMsg;
		}
		catch (IOException | InterruptedException ex)
		{
			_log.error("Error while making Backup!", ex);
			return ex.getMessage() + " Error!";
		}
	}

	private String getDatabaseName()
	{
		final Matcher m = DatabaseBackupManager.DATABASE_NAME_PATTERN.matcher(Config.DATABASE_GAME_URL);
		if (m.find())
		{
			return m.group(1);
		}
		return null;
	}

	private String getPath(String databaseName)
	{
		final Calendar c = Calendar.getInstance();
		final SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH.mm");
		final String formatted = format1.format(c.getTime());
		return FOLDER_PATH + "\\" + databaseName + " " + formatted + ".sql";
	}

	public static DatabaseBackupManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final DatabaseBackupManager _instance = new DatabaseBackupManager();
	}
}
