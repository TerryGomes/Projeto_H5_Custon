package l2f.gameserver.network.clientpackets;

import l2f.gameserver.model.Player;

public class RequestTeleportBookMark extends L2GameClientPacket
{
	private int slot;

	@Override
	protected void readImpl()
	{
		slot = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar != null)
		{
			activeChar.bookmarks.tryTeleport(slot);
		}
	}
}