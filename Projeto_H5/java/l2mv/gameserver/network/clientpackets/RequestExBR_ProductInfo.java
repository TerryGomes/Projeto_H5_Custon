package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.ExBR_ProductInfo;

public class RequestExBR_ProductInfo extends L2GameClientPacket
{
	private int _productId;

	@Override
	protected void readImpl()
	{
		_productId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();

		if (activeChar == null)
		{
			return;
		}

		activeChar.sendPacket(new ExBR_ProductInfo(_productId));
	}
}