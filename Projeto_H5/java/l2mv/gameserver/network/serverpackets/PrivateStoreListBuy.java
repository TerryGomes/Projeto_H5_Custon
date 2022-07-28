package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.TradeItem;

public class PrivateStoreListBuy extends L2GameServerPacket
{
	private int _buyerId;
	private long _adena;
	private List<TradeItem> _sellList;

	/**
	 * Список вещей в личном магазине покупки, показываемый продающему
	 * @param seller
	 * @param buyer
	 */
	public PrivateStoreListBuy(Player seller, Player buyer)
	{
		this._adena = seller.getAdena();
		this._buyerId = buyer.getObjectId();
		this._sellList = new ArrayList<TradeItem>();
		final List<TradeItem> buyList = buyer.getBuyList();
		final ItemInstance[] items = seller.getInventory().getItems();
		for (TradeItem bi : buyList)
		{
			TradeItem si = null;
			for (ItemInstance item : items)
			{
				if (item.getItemId() == bi.getItemId() && item.canBeTraded(seller))
				{
					si = new TradeItem(item);
					this._sellList.add(si);
					si.setOwnersPrice(bi.getOwnersPrice());
					si.setCount(bi.getCount());
					si.setCurrentValue(Math.min(bi.getCount(), item.getCount()));
				}
			}
			if (si == null)
			{
				si = new TradeItem();
				si.setItemId(bi.getItemId());
				si.setOwnersPrice(bi.getOwnersPrice());
				si.setCount(bi.getCount());
				si.setCurrentValue(0);
				this._sellList.add(si);
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xBE);

		this.writeD(this._buyerId);
		this.writeQ(this._adena);
		this.writeD(this._sellList.size());
		for (TradeItem si : this._sellList)
		{
			this.writeItemInfo(si, si.getCurrentValue());
			this.writeD(si.getObjectId());
			this.writeQ(si.getOwnersPrice());
			this.writeQ(si.getStorePrice());
			this.writeQ(si.getCount()); // maximum possible tradecount
		}
	}
}