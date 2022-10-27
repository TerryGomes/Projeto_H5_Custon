package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.commons.lang.ArrayUtils;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInfo;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.Warehouse.ItemClassComparator;
import l2mv.gameserver.model.items.Warehouse.WarehouseType;

public class WareHouseDepositList extends L2GameServerPacket
{
	private int _whtype;
	private long _adena;
	private List<ItemInfo> _itemList;

	public WareHouseDepositList(Player cha, WarehouseType whtype)
	{
		this._whtype = whtype.ordinal();
		this._adena = cha.getAdena();

		ItemInstance[] items = cha.getInventory().getItems();
		ArrayUtils.eqSort(items, ItemClassComparator.getInstance());
		this._itemList = new ArrayList<ItemInfo>(items.length);
		for (ItemInstance item : items)
		{
			switch (this._whtype)
			{
			case 1:
				if (item.canBeStored(cha, true))
				{
					this._itemList.add(new ItemInfo(item));
				}
				break;
			case 2:
				if (item.canBeStored(cha, false))
				{
					this._itemList.add(new ItemInfo(item));
				}
				break;
			case 3:
				if (item.canBeStored(cha, false))
				{
					this._itemList.add(new ItemInfo(item));
				}
				break;
			default:
				if (this._whtype == WarehouseType.FREIGHT.ordinal())
				{
					if (item.canBeTraded(cha) && !item.isStackable() || item.getCrystalType().externalOrdinal >= 4)
					{
						this._itemList.add(new ItemInfo(item));
					}
				}
				break;
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x41);
		this.writeH(this._whtype);
		this.writeQ(this._adena);
		this.writeH(this._itemList.size());
		for (ItemInfo item : this._itemList)
		{
			this.writeItemInfo(item);
			this.writeD(item.getObjectId());
		}
	}
}