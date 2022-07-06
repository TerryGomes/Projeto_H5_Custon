package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;

public class RequestExBuySellUIClose extends L2GameClientPacket
{
	@Override
	protected void runImpl()
	{
		// trigger
	}

	@Override
	protected void readImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		activeChar.setBuyListId(0);
		activeChar.sendItemList(true);
	}
}