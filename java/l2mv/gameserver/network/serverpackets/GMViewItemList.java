package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;

public class GMViewItemList extends L2GameServerPacket
{
	private int _size;
	private ItemInstance[] _items;
	private int _limit;
	private String _name;

	public GMViewItemList(Player cha, ItemInstance[] items, int size)
	{
		this._size = size;
		this._items = items;
		this._name = cha.getName();
		this._limit = cha.getInventoryLimit();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x9a);
		this.writeS(this._name);
		this.writeD(this._limit); // c4?
		this.writeH(1); // show window ??

		this.writeH(this._size);
		for (ItemInstance temp : this._items)
		{
			if (!temp.getTemplate().isQuest())
			{
				this.writeItemInfo(temp);
			}
		}
	}
}