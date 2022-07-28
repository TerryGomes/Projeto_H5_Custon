package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;

public class RequestPrivateStoreQuitSell extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (!activeChar.isInStoreMode() || activeChar.getPrivateStoreType() != Player.STORE_PRIVATE_SELL && activeChar.getPrivateStoreType() != Player.STORE_PRIVATE_SELL_PACKAGE || !Config.ALLOW_PRIVATE_STORES)
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
		activeChar.standUp();
		activeChar.broadcastCharInfo();
	}
}