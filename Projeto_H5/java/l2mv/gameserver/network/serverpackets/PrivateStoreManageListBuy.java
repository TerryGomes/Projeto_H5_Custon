package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.commons.lang.ArrayUtils;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.TradeItem;
import l2mv.gameserver.model.items.Warehouse.ItemClassComparator;
import l2mv.gameserver.templates.item.ItemTemplate;

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
		this._buyerId = buyer.getObjectId();
		this._adena = buyer.getAdena();
		this._buyList0 = buyer.getBuyList();
		this._buyList = new ArrayList<TradeItem>();

		ItemInstance[] items = buyer.getInventory().getItems();
		ArrayUtils.eqSort(items, ItemClassComparator.getInstance());
		TradeItem bi;
		for (ItemInstance item : items)
		{
			if (item.canBeTraded(buyer) && item.getItemId() != ItemTemplate.ITEM_ID_ADENA)
			{
				this._buyList.add(bi = new TradeItem(item));
				bi.setObjectId(0);
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xBD);
		// section 1
		this.writeD(this._buyerId);
		this.writeQ(this._adena);

		// section2
		this.writeD(this._buyList.size());// for potential sells
		for (TradeItem bi : this._buyList)
		{
			this.writeItemInfo(bi);
			this.writeQ(bi.getStorePrice());
		}

		// section 3
		this.writeD(this._buyList0.size());// count for any items already added for sell
		for (TradeItem bi : this._buyList0)
		{
			this.writeItemInfo(bi);
			this.writeQ(bi.getOwnersPrice());
			this.writeQ(bi.getStorePrice());
			this.writeQ(bi.getCount());
		}
	}
}