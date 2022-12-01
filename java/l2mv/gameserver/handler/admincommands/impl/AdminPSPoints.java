package l2mv.gameserver.handler.admincommands.impl;

import l2mv.gameserver.handler.admincommands.IAdminCommandHandler;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;

/**
 * @author KilRoy
 * AddPoints Manipulation //addpoints count <target>
 * DelPoints Manipulation //delpoints count <target>
 */
public class AdminPSPoints implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_addpoints,
		admin_delpoints
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.getPlayerAccess().CanEditChar)
		{
			return false;
		}

		switch (command)
		{
		case admin_addpoints:
			try
			{
				String targetName = wordList[1];
				Player obj = World.getPlayer(targetName);
				if (obj != null && obj.isPlayer())
				{
					// int add = (obj.getNetConnection().getPointG() + Integer.parseInt(wordList[2]));
					// obj.getNetConnection().setPointG(add);
					activeChar.sendMessage("Option Removed");
				}
				else
				{
					activeChar.sendMessage("Player " + targetName + " not found");
				}
			}
			catch (IndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Please specify correct name.");
			}
			break;
		case admin_delpoints:
			try
			{
				String targetName = wordList[1];
				Player obj = World.getPlayer(targetName);
				if (obj != null && obj.isPlayer())
				{
					activeChar.sendMessage("Option Removed");
				}
				else
				{
					activeChar.sendMessage("Player " + targetName + " not found");
				}
			}
			catch (IndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Please specify correct name.");
			}
			break;
		}

		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}