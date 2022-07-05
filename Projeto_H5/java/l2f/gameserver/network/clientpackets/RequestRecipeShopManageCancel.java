package l2f.gameserver.network.clientpackets;

import l2f.gameserver.model.Player;

public class RequestRecipeShopManageCancel extends L2GameClientPacket
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
			// TODO [G1ta0] проанализировать
		}
	}
}