package l2mv.gameserver.handler.usercommands.impl;

import java.util.Calendar;

import l2mv.gameserver.handler.usercommands.IUserCommandHandler;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

/**
 * Support for /mybirthday command
 */
public class MyBirthday implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		126
	};

	@Override
	public boolean useUserCommand(int id, Player activeChar)
	{
		if (activeChar.getCreateTime() == 0)
		{
			return false;
		}

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(activeChar.getCreateTime());

		activeChar.sendPacket(new SystemMessage2(SystemMsg.C1S_BIRTHDAY_IS_S3S4S2).addName(activeChar).addInteger(c.get(Calendar.YEAR)).addInteger(c.get(Calendar.MONTH) + 1).addInteger(c.get(Calendar.DAY_OF_MONTH)));

		if (c.get(Calendar.MONTH) == Calendar.FEBRUARY && c.get(Calendar.DAY_OF_WEEK) == 29)
		{
			activeChar.sendPacket(SystemMsg.A_CHARACTER_BORN_ON_FEBRUARY_29_WILL_RECEIVE_A_GIFT_ON_FEBRUARY_28);
		}
		return true;
	}

	@Override
	public final int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}