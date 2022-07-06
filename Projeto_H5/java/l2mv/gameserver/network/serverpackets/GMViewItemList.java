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
		_size = size;
		_items = items;
		_name = cha.getName();
		_limit = cha.getInventoryLimit();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x9a);
		writeS(_name);
		writeD(_limit); // c4?
		writeH(1); // show window ??

		writeH(_size);
		for (ItemInstance temp : _items)
		{
			if (!temp.getTemplate().isQuest())
			{
				writeItemInfo(temp);
			}
		}
	}
}