package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.LockType;

public class ItemList extends L2GameServerPacket
{
	private final int _size;
	private final ItemInstance[] _items;
	private final boolean _showWindow;

	private LockType _lockType;
	private int[] _lockItems;

	public ItemList(int size, ItemInstance[] items, boolean showWindow, LockType lockType, int[] lockItems)
	{
		_size = size;
		_items = items;
		_showWindow = showWindow;
		_lockType = lockType;
		_lockItems = lockItems;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x11);
		writeH(_showWindow ? 1 : 0);

		writeH(_size);
		for (ItemInstance temp : _items)
		{
			if (temp.getTemplate().isQuest())
			{
				continue;
			}

			writeItemInfo(temp);
		}

		writeH(_lockItems.length);
		if (_lockItems.length > 0)
		{
			writeC(_lockType.ordinal());
			for (int i : _lockItems)
			{
				writeD(i);
			}
		}
	}
}