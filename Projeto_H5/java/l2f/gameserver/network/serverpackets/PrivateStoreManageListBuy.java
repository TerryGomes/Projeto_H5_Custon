package l2f.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2f.commons.lang.ArrayUtils;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.items.TradeItem;
import l2f.gameserver.model.items.Warehouse.ItemClassComparator;
import l2f.gameserver.templates.item.ItemTemplate;

public class PrivateStoreManageListBuy extends L2GameServerPacket
{
	private final int _buyerId;
	private final long _adena;
	private final List<TradeItem> _buyList0;
	private final List<TradeItem> _buyList;

	/**
	 * Окно управления личным магазином покупки
	 * @param buyer
	 */
	public PrivateStoreManageListBuy(Player buyer)
	{
		_buyerId = buyer.getObjectId();
		_adena = buyer.getAdena();
		_buyList0 = buyer.getBuyList();
		_buyList = new ArrayList<TradeItem>();

		ItemInstance[] items = buyer.getInventory().getItems();
		ArrayUtils.eqSort(items, ItemClassComparator.getInstance());
		TradeItem bi;
		for (ItemInstance item : items)
		{
			if (item.canBeTraded(buyer) && item.getItemId() != ItemTemplate.ITEM_ID_ADENA)
			{
				_buyList.add(bi = new TradeItem(item));
				bi.setObjectId(0);
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xBD);
		// section 1
		writeD(_buyerId);
		writeQ(_adena);

		// section2
		writeD(_buyList.size());// for potential sells
		for (TradeItem bi : _buyList)
		{
			writeItemInfo(bi);
			writeQ(bi.getStorePrice());
		}

		// section 3
		writeD(_buyList0.size());// count for any items already added for sell
		for (TradeItem bi : _buyList0)
		{
			writeItemInfo(bi);
			writeQ(bi.getOwnersPrice());
			writeQ(bi.getStorePrice());
			writeQ(bi.getCount());
		}
	}
}