package l2mv.gameserver.network.serverpackets;

import java.util.Map;

import l2mv.gameserver.model.items.Inventory;

public class ShopPreviewInfo extends L2GameServerPacket
{
	private Map<Integer, Integer> _itemlist;

	public ShopPreviewInfo(Map<Integer, Integer> itemlist)
	{
		this._itemlist = itemlist;
	}

	@Override
	protected void writeImpl()
	{
		this.writeC(0xF6);
		this.writeD(Inventory.PAPERDOLL_MAX);

		// Slots
		for (int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
		{
			this.writeD(this.getFromList(PAPERDOLL_ID));
		}
	}

	private int getFromList(int key)
	{
		return ((this._itemlist.get(key) != null) ? this._itemlist.get(key) : 0);
	}
}