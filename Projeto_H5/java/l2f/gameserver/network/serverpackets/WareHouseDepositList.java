package l2f.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2f.commons.lang.ArrayUtils;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.ItemInfo;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.items.Warehouse.ItemClassComparator;
import l2f.gameserver.model.items.Warehouse.WarehouseType;

public class WareHouseDepositList extends L2GameServerPacket
{
	private int _whtype;
	private long _adena;
	private List<ItemInfo> _itemList;

	public WareHouseDepositList(Player cha, WarehouseType whtype)
	{
		_whtype = whtype.ordinal();
		_adena = cha.getAdena();

		ItemInstance[] items = cha.getInventory().getItems();
		ArrayUtils.eqSort(items, ItemClassComparator.getInstance());
		_itemList = new ArrayList<ItemInfo>(items.length);
		for (ItemInstance item : items)
		{
			switch (_whtype)
			{
			case 1:
				if (item.canBeStored(cha, true))
				{
					_itemList.add(new ItemInfo(item));
				}
				break;
			case 2:
				if (item.canBeStored(cha, false))
				{
					_itemList.add(new ItemInfo(item));
				}
				break;
			case 3:
				if (item.canBeStored(cha, false))
				{
					_itemList.add(new ItemInfo(item));
				}
				break;
			default:
				if (_whtype == WarehouseType.FREIGHT.ordinal())
				{
					if (item.canBeTraded(cha) && !item.isStackable() || item.getCrystalType().externalOrdinal >= 4)
					{
						_itemList.add(new ItemInfo(item));
					}
				}
				break;
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x41);
		writeH(_whtype);
		writeQ(_adena);
		writeH(_itemList.size());
		for (ItemInfo item : _itemList)
		{
			writeItemInfo(item);
			writeD(item.getObjectId());
		}
	}
}