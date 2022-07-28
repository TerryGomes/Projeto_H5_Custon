package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;

public class RequestRecipeShopManageCancel extends L2GameClientPacket
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
			// TODO [G1ta0] проанализировать
		}
	}
}