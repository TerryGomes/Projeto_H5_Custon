package l2f.gameserver.handler.admincommands.impl;

import l2f.gameserver.handler.admincommands.IAdminCommandHandler;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.components.ChatType;

public class AdminChat implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_clear_chat, admin_show_message_colors, admin_show_message_colors3, admin_catch_pms
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		switch (command)
		{
		case admin_clear_chat:
		{
			final ChatType chatType = ChatType.getTypeFromName(wordList[1]);
			final Say2 msg = new Say2(0, chatType, "", "");
			for (int i = 0; i < 200; ++i)
			{
				activeChar.sendPacket(msg);
			}
			break;
		}
		case admin_show_message_colors:
		{
			for (ChatType type : ChatType.values())
			{
				activeChar.sendPacket(new Say2(0, type, "Chat", " Message From Type " + type.toString() + '!'));
			}
			break;
		}
		case admin_show_message_colors3:
		{
			for (ChatType type : ChatType.values())
			{
				activeChar.sendPacket(new Say2(0, type, "Chat", " Messageaa From Type " + type.toString() + '!'));
			}
			break;
		}
		case admin_catch_pms:
		{
			final int minLength = Integer.parseInt(wordList[1]);
			if (minLength > 0)
			{
				activeChar.setVar("catchMessagesGM", minLength, -1L);
				activeChar.sendMessage("You will now catch Private Messages longer than " + minLength + " chars.");
			}
			else
			{
				activeChar.unsetVar("catchMessagesGM");
				activeChar.sendMessage("You will no longer catch Private Messages");
			}
			break;
		}
		default:
		{
			return false;
		}
		}

		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
