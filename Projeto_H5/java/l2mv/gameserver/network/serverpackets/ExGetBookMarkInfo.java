package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.actor.instances.player.BookMark;

/**
 * dd d*[ddddSdS]
 */
public class ExGetBookMarkInfo extends L2GameServerPacket
{
	private final int bookmarksCapacity;
	private final BookMark[] bookmarks;

	public ExGetBookMarkInfo(Player player)
	{
		this.bookmarksCapacity = player.bookmarks.getCapacity();
		this.bookmarks = player.bookmarks.toArray();
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x84);

		this.writeD(0x00); // должно быть 0
		this.writeD(this.bookmarksCapacity);
		this.writeD(this.bookmarks.length);
		int slotId = 0;
		for (BookMark bookmark : this.bookmarks)
		{
			this.writeD(++slotId);
			this.writeD(bookmark.x);
			this.writeD(bookmark.y);
			this.writeD(bookmark.z);
			this.writeS(bookmark.getName());
			this.writeD(bookmark.getIcon());
			this.writeS(bookmark.getAcronym());
		}
	}
}