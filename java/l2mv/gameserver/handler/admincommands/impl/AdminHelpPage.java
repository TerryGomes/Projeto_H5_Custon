package l2mv.gameserver.handler.admincommands.impl;

import l2mv.gameserver.handler.admincommands.IAdminCommandHandler;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;

public class AdminHelpPage implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_showhtml
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.getPlayerAccess().Menu)
		{
			return false;
		}

		switch (command)
		{
		case admin_showhtml:
			if (wordList.length != 2)
			{
				activeChar.sendMessage("Usage: //showhtml <file>");
				return false;
			}
			activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/" + wordList[1]));
			break;
		}

		return true;
	}

	public static void showHelpHtml(Player targetChar, String content)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setHtml(content);
		targetChar.sendPacket(adminReply);
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}