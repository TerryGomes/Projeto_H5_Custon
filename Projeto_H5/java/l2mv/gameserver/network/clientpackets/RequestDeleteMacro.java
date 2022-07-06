package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;

public class RequestDeleteMacro extends L2GameClientPacket
{
	private int _id;

	/**
	 * format:		cd
	 */
	@Override
	protected void readImpl()
	{
		_id = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null || activeChar.isBlocked())
		{
			return;
		}
		activeChar.deleteMacro(_id);
	}
}