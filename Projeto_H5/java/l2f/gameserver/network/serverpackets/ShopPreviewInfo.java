package l2f.gameserver.network.serverpackets;

import java.util.Map;

import l2f.gameserver.model.items.Inventory;

public class ShopPreviewInfo extends L2GameServerPacket
{
	private Map<Integer, Integer> _itemlist;

	public ShopPreviewInfo(Map<Integer, Integer> itemlist)
	{
		_itemlist = itemlist;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xF6);
		writeD(Inventory.PAPERDOLL_MAX);

		// Slots
		for (int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
		{
			writeD(getFromList(PAPERDOLL_ID));
		}
	}

	private int getFromList(int key)
	{
		return ((_itemlist.get(key) != null) ? _itemlist.get(key) : 0);
	}
}