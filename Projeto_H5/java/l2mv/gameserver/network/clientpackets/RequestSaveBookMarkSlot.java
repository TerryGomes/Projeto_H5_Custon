package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.ExGetBookMarkInfo;

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
		this.name = this.readS(32);
		this.icon = this.readD();
		this.acronym = this.readS(4);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar != null && activeChar.bookmarks.add(this.name, this.acronym, this.icon))
		{
			activeChar.sendPacket(new ExGetBookMarkInfo(activeChar));
		}
	}
}