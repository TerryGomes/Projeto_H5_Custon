package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;

public class RequestTeleportBookMark extends L2GameClientPacket
{
	private int slot;

	@Override
	protected void readImpl()
	{
		this.slot = this.readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar != null)
		{
			activeChar.bookmarks.tryTeleport(this.slot);
		}
	}
}