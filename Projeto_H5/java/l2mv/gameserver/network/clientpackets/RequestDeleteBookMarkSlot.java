package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.ExGetBookMarkInfo;

public class RequestDeleteBookMarkSlot extends L2GameClientPacket
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
			// TODO Msg.THE_SAVED_TELEPORT_LOCATION_WILL_BE_DELETED_DO_YOU_WISH_TO_CONTINUE
			activeChar.bookmarks.remove(slot);
			activeChar.sendPacket(new ExGetBookMarkInfo(activeChar));
		}
	}
}