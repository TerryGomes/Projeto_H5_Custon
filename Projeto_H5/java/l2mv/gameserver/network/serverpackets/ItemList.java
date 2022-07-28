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
		this._size = size;
		this._items = items;
		this._showWindow = showWindow;
		this._lockType = lockType;
		this._lockItems = lockItems;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x11);
		this.writeH(this._showWindow ? 1 : 0);

		this.writeH(this._size);
		for (ItemInstance temp : this._items)
		{
			if (temp.getTemplate().isQuest())
			{
				continue;
			}

			this.writeItemInfo(temp);
		}

		this.writeH(this._lockItems.length);
		if (this._lockItems.length > 0)
		{
			this.writeC(this._lockType.ordinal());
			for (int i : this._lockItems)
			{
				this.writeD(i);
			}
		}
	}
}