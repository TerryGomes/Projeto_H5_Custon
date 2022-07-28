package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;

public class GMViewWarehouseWithdrawList extends L2GameServerPacket
{
	private final ItemInstance[] _items;
	private String _charName;
	private long _charAdena;

	public GMViewWarehouseWithdrawList(Player cha)
	{
		this._charName = cha.getName();
		this._charAdena = cha.getAdena();
		this._items = cha.getWarehouse().getItems();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x9b);
		this.writeS(this._charName);
		this.writeQ(this._charAdena);
		this.writeH(this._items.length);
		for (ItemInstance temp : this._items)
		{
			this.writeItemInfo(temp);
			this.writeD(temp.getObjectId());
		}
	}
}