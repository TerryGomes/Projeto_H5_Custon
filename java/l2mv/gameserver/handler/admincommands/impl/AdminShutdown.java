package l2mv.gameserver.handler.admincommands.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.Config;
import l2mv.gameserver.GameTimeController;
import l2mv.gameserver.Shutdown;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.dao.DatabaseBackupManager;
import l2mv.gameserver.handler.admincommands.IAdminCommandHandler;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;

public class AdminShutdown implements IAdminCommandHandler
{
	private static final SimpleDateFormat _format = new SimpleDateFormat("h:mm a");

	private static enum Commands
	{
		admin_shutdown,
		admin_shutdown_seconds,
		admin_restart_seconds,
		admin_restart_abort,
		admin_start_backup
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.getPlayerAccess().CanRestart)
		{
			return false;
		}

		switch (command)
		{
		case admin_shutdown:
		{
			int t = GameTimeController.getInstance().getGameTime();
			int h = t / 60;
			int m = t % 60;
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, h);
			cal.set(Calendar.MINUTE, m);

			final NpcHtmlMessage html = new NpcHtmlMessage(5);
			html.setFile("admin/shutdown.htm");

			String time = "";
			int seconds = Shutdown.getInstance().getSeconds();
			if (seconds > 86400)
			{
				int days = (int) Math.floor(seconds / 86400);
				time += days + " D";
				seconds = seconds - days * 86400;
			}
			if (seconds > 3600)
			{
				int hours = (int) Math.floor(seconds / 3600);
				time += hours + " H";
				seconds = seconds - hours * 3600;
			}
			if (seconds > 60)
			{
				int minutes = (int) Math.floor(seconds / 60);
				time += minutes + " M";
				seconds = seconds - minutes * 60;
			}
			if (seconds > 0)
			{
				time += seconds + " S";
			}

			html.replace("%shutdownMode%", Shutdown.getInstance().getMode().toString());
			html.replace("%time%", time);
			html.replace("%backup%", Shutdown.getInstance().isMakeBackup() ? "True" : "False");
			html.replace("%currentTime%", _format.format(cal.getTime()));
			html.replace("%onlineCount%", GameObjectsStorage.getAllPlayersCount() - Config.ONLINE_PLUS);
			html.replace("%tradersCount%", GameObjectsStorage.getAllStorePlayersCount());
			activeChar.sendPacket(html);
			break;
		}
		case admin_shutdown_seconds:
		{
			final int seconds = Integer.parseInt(wordList[1]);
			final boolean makeBackup = Boolean.parseBoolean(wordList[2]);
			Shutdown.getInstance().schedule(seconds, Shutdown.ShutdownMode.SHUTDOWN, makeBackup);
			break;
		}
		case admin_restart_seconds:
		{
			final int seconds = Integer.parseInt(wordList[1]);
			final boolean makeBackup = Boolean.parseBoolean(wordList[2]);
			Shutdown.getInstance().schedule(seconds, Shutdown.ShutdownMode.RESTART, makeBackup);
			break;
		}
		case admin_restart_abort:
		{
			if (Shutdown.getInstance().getMode() == Shutdown.ShutdownMode.NONE)
			{
				return true;
			}
			Shutdown.getInstance().cancel();
			break;
		}
		case admin_start_backup:
		{
			ThreadPoolManager.getInstance().execute(new BackupThread(activeChar));
			break;
		}
		default:
		{
			return false;
		}
		}

		return true;
	}

	private static class BackupThread extends RunnableImpl
	{
		private final Player activeChar;

		BackupThread(Player activeChar)
		{
			this.activeChar = activeChar;
		}

		@Override
		public void runImpl()
		{
			final String result = DatabaseBackupManager.getInstance().doBackup(true);
			activeChar.sendMessage(result);
		}
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
