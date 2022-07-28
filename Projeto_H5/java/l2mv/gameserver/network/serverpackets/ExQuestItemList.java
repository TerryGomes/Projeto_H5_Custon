package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.LockType;

/**
 * @author VISTALL
 * @date 1:02/23.02.2011
 */
public class ExQuestItemList extends L2GameServerPacket
{
	private int _size;
	private ItemInstance[] _items;

	private LockType _lockType;
	private int[] _lockItems;

	public ExQuestItemList(int size, ItemInstance[] t, LockType lockType, int[] lockItems)
	{
		this._size = size;
		this._items = t;
		this._lockType = lockType;
		this._lockItems = lockItems;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xC6);
		this.writeH(this._size);

		for (ItemInstance temp : this._items)
		{
			if (!temp.getTemplate().isQuest())
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
