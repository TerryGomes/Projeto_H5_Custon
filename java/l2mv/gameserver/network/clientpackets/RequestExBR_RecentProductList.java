package l2mv.gameserver.network.clientpackets;

public class RequestExBR_RecentProductList extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{
		// триггер
	}

	@Override
	public void runImpl()
	{
		/*
		 * L2Player activeChar = getClient().getActiveChar();
		 * if (activeChar == null)
		 * return;
		 */

		// activeChar.sendMessage("triggered BrRecentProductList()");
	}
}