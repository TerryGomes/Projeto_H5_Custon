package l2f.gameserver.network.clientpackets;

import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.ExBR_ProductList;

public class RequestExBR_ProductList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();

		if (activeChar == null)
		{
			return;
		}

		activeChar.sendPacket(new ExBR_ProductList());
	}
}