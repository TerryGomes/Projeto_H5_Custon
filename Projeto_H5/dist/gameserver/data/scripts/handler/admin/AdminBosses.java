package handler.admin;

import java.util.Calendar;

import bosses.AntharasManager;
import bosses.BaiumManager;
import bosses.ValakasManager;
import l2f.gameserver.handler.admincommands.AdminCommandHandler;
import l2f.gameserver.handler.admincommands.IAdminCommandHandler;
import l2f.gameserver.instancemanager.ServerVariables;
import l2f.gameserver.model.Player;
import l2f.gameserver.scripts.ScriptFile;

public class AdminBosses implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_epics_respawn
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, final Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.getPlayerAccess().CanEditNPC)
		{
			return false;
		}

		switch (command)
		{
		case admin_epics_respawn:
			getEpicsRespawn(activeChar);
			break;
		}

		return true;
	}

	private static void getEpicsRespawn(Player activeChar)
	{
		activeChar.sendMessage("Antharas: " + convertRespawnDate(AntharasManager.getState().getRespawnDate()));
		activeChar.sendMessage("Valakas: " + convertRespawnDate(ValakasManager.getState().getRespawnDate()));
		activeChar.sendMessage("Baium: " + convertRespawnDate(BaiumManager.getState().getRespawnDate()));
		activeChar.sendMessage("Beleth: " + convertRespawnDate(ServerVariables.getLong("BelethKillTime", 0L)));
	}

	private static String convertRespawnDate(long date)
	{
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(date);

		return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	@Override
	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
	}

	@Override
	public void onReload()
	{

	}

	@Override
	public void onShutdown()
	{

	}
}
