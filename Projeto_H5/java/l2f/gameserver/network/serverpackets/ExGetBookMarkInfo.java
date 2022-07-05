package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.actor.instances.player.BookMark;

/**
 * dd d*[ddddSdS]
 */
public class ExGetBookMarkInfo extends L2GameServerPacket
{
	private final int bookmarksCapacity;
	private final BookMark[] bookmarks;

	public ExGetBookMarkInfo(Player player)
	{
		bookmarksCapacity = player.bookmarks.getCapacity();
		bookmarks = player.bookmarks.toArray();
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0x84);

		writeD(0x00); // должно быть 0
		writeD(bookmarksCapacity);
		writeD(bookmarks.length);
		int slotId = 0;
		for (BookMark bookmark : bookmarks)
		{
			writeD(++slotId);
			writeD(bookmark.x);
			writeD(bookmark.y);
			writeD(bookmark.z);
			writeS(bookmark.getName());
			writeD(bookmark.getIcon());
			writeS(bookmark.getAcronym());
		}
	}
}