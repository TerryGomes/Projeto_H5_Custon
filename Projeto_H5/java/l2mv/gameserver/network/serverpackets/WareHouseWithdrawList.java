package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2mv.commons.lang.ArrayUtils;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInfo;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.Warehouse.ItemClassComparator;
import l2mv.gameserver.model.items.Warehouse.WarehouseType;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.templates.item.ItemTemplate.ItemClass;

public class WareHouseWithdrawList extends L2GameServerPacket
{
	private long _adena;
	private List<ItemInfo> _itemList = new ArrayList<ItemInfo>();
	private int _type;

	public WareHouseWithdrawList(Player player, WarehouseType type, ItemClass clss)
	{
		this._adena = player.getAdena();
		this._type = type.ordinal();

		ItemInstance[] items;
		switch (type)
		{
		case PRIVATE:
			items = player.getWarehouse().getItems(clss);
			break;
		case FREIGHT:
			items = player.getFreight().getItems(clss);
			break;
		case CLAN:
		case CASTLE:
			items = player.getClan().getWarehouse().getItems(clss);
			break;
		default:
			this._itemList = Collections.emptyList();
			return;
		}

		this._itemList = new ArrayList<ItemInfo>(items.length);
		ArrayUtils.eqSort(items, ItemClassComparator.getInstance());
		for (ItemInstance item : items)
		{
			this._itemList.add(new ItemInfo(item));
		}
	}

	public WareHouseWithdrawList(Clan clan, ItemClass clss)
	{
		this._adena = 0;
		this._type = WarehouseType.CLAN.ordinal();

		ItemInstance[] items = clan == null ? new ItemInstance[0] : clan.getWarehouse().getItems();

		this._itemList = new ArrayList<ItemInfo>(items.length);
		ArrayUtils.eqSort(items, ItemClassComparator.getInstance());
		for (ItemInstance item : items)
		{
			this._itemList.add(new ItemInfo(item));
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x42);
		this.writeH(this._type);
		this.writeQ(this._adena);
		this.writeH(this._itemList.size());
		for (ItemInfo item : this._itemList)
		{
			this.writeItemInfo(item);
			this.writeD(item.getObjectId());
		}
	}
}