package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;

public class RequestRecipeShopManageQuit extends L2GameClientPacket
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

		activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
		activeChar.standUp();
		activeChar.broadcastCharInfo();
	}
}