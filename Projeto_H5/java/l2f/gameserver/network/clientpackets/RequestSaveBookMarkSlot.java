package l2f.gameserver.network.clientpackets;

import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.ExGetBookMarkInfo;

/**
 * SdS
 */
public class RequestSaveBookMarkSlot extends L2GameClientPacket
{
	private String name, acronym;
	private int icon;

	@Override
	protected void readImpl()
	{
		name = readS(32);
		icon = readD();
		acronym = readS(4);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar != null && activeChar.bookmarks.add(name, acronym, icon))
		{
			activeChar.sendPacket(new ExGetBookMarkInfo(activeChar));
		}
	}
}