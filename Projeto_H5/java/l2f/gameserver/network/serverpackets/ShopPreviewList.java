package l2f.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2f.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.ItemInfo;
import l2f.gameserver.model.items.TradeItem;
import l2f.gameserver.templates.item.ItemTemplate;

public class ShopPreviewList extends L2GameServerPacket
{
	private int _listId;
	private List<ItemInfo> _itemList;
	private long _money;

	public ShopPreviewList(NpcTradeList list, Player player)
	{
		_listId = list.getListId();
		_money = player.getAdena();
		List<TradeItem> tradeList = list.getItems();
		_itemList = new ArrayList<ItemInfo>(tradeList.size());
		for (TradeItem item : list.getItems())
		{
			if (item.getItem().isEquipable())
			{
				_itemList.add(item);
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xf5);
		writeD(0x13c0); // ?
		writeQ(_money);
		writeD(_listId);
		writeH(_itemList.size());

		for (ItemInfo item : _itemList)
		{
			if (item.getItem().isEquipable())
			{
				writeD(item.getItemId());
				writeH(item.getItem().getType2ForPackets()); // item type2
				writeH(item.getItem().isEquipable() ? item.getItem().getBodyPart() : 0x00);
				writeQ(getWearPrice(item.getItem()));
			}
		}
	}

	public static int getWearPrice(ItemTemplate item)
	{
		switch (item.getItemGrade())
		{
		case D:
			return 50;
		case C:
			return 100;
		// TODO: Не известно сколько на оффе стоит примерка B - S84 ранга.
		case B:
			return 200;
		case A:
			return 500;
		case S:
			return 1000;
		case S80:
			return 2000;
		case S84:
			return 2500;
		default:
			return 10;
		}
	}
}