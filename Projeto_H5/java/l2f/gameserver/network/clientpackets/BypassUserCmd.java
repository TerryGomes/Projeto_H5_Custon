package l2f.gameserver.network.clientpackets;

import l2f.gameserver.handler.usercommands.IUserCommandHandler;
import l2f.gameserver.handler.usercommands.UserCommandHandler;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.components.CustomMessage;

/**
 * format:  cd
 * Example package team /loc:
 * AA 00 00 00 00
 */
public class BypassUserCmd extends L2GameClientPacket
{
	private int _command;

	@Override
	protected void readImpl()
	{
		_command = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		IUserCommandHandler handler = UserCommandHandler.getInstance().getUserCommandHandler(_command);

		if (handler == null)
		{
			activeChar.sendMessage(new CustomMessage("common.S1NotImplemented", activeChar).addString(String.valueOf(_command)));
		}
		else
		{
			handler.useUserCommand(_command, activeChar);
		}
	}
}