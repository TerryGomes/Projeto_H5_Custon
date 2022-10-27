package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInfo;
import l2mv.gameserver.model.items.TradeItem;
import l2mv.gameserver.templates.item.ItemTemplate;

public class ShopPreviewList extends L2GameServerPacket
{
	private int _listId;
	private List<ItemInfo> _itemList;
	private long _money;

	public ShopPreviewList(NpcTradeList list, Player player)
	{
		this._listId = list.getListId();
		this._money = player.getAdena();
		List<TradeItem> tradeList = list.getItems();
		this._itemList = new ArrayList<ItemInfo>(tradeList.size());
		for (TradeItem item : list.getItems())
		{
			if (item.getItem().isEquipable())
			{
				this._itemList.add(item);
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xf5);
		this.writeD(0x13c0); // ?
		this.writeQ(this._money);
		this.writeD(this._listId);
		this.writeH(this._itemList.size());

		for (ItemInfo item : this._itemList)
		{
			if (item.getItem().isEquipable())
			{
				this.writeD(item.getItemId());
				this.writeH(item.getItem().getType2ForPackets()); // item type2
				this.writeH(item.getItem().isEquipable() ? item.getItem().getBodyPart() : 0x00);
				this.writeQ(getWearPrice(item.getItem()));
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