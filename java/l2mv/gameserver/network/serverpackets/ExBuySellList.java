package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2mv.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.TradeItem;

public abstract class ExBuySellList extends L2GameServerPacket
{
	public static class BuyList extends ExBuySellList
	{
		private final int _listId;
		private final List<TradeItem> _buyList;
		private final long _adena;
		private final double _taxRate;

		public BuyList(NpcTradeList tradeList, Player activeChar, double taxRate)
		{
			super(0);
			this._adena = activeChar.getAdena();
			this._taxRate = taxRate;

			if (tradeList != null)
			{
				this._listId = tradeList.getListId();
				this._buyList = tradeList.getItems();
				activeChar.setBuyListId(this._listId);
			}
			else
			{
				this._listId = 0;
				this._buyList = Collections.emptyList();
				activeChar.setBuyListId(0);
			}
		}

		@Override
		protected void writeImpl()
		{
			super.writeImpl();
			this.writeQ(this._adena); // current money
			this.writeD(this._listId);
			this.writeH(this._buyList.size());
			for (TradeItem item : this._buyList)
			{
				this.writeItemInfo(item, item.getCurrentValue());
				this.writeQ((long) (item.getOwnersPrice() * (1. + this._taxRate)));
			}
		}
	}

	public static class SellRefundList extends ExBuySellList
	{
		private final List<TradeItem> _sellList;
		private final List<TradeItem> _refundList;
		private int _done;

		public SellRefundList(Player activeChar, boolean done)
		{
			super(1);
			this._done = done ? 1 : 0;
			if (done)
			{
				this._refundList = Collections.emptyList();
				this._sellList = Collections.emptyList();
			}
			else
			{
				ItemInstance[] items = activeChar.getRefund().getItems();
				this._refundList = new ArrayList<TradeItem>(items.length);
				for (ItemInstance item : items)
				{
					this._refundList.add(new TradeItem(item));
				}

				items = activeChar.getInventory().getItems();
				this._sellList = new ArrayList<TradeItem>(items.length);
				for (ItemInstance item : items)
				{
					if (item.canBeSold(activeChar))
					{
						this._sellList.add(new TradeItem(item));
					}
				}
			}
		}

		@Override
		protected void writeImpl()
		{
			super.writeImpl();
			this.writeH(this._sellList.size());
			for (TradeItem item : this._sellList)
			{
				this.writeItemInfo(item);
				this.writeQ(item.getReferencePrice() / 2);
			}
			this.writeH(this._refundList.size());
			for (TradeItem item : this._refundList)
			{
				this.writeItemInfo(item);
				this.writeD(item.getObjectId());
				this.writeQ(item.getCount() * item.getReferencePrice() / 2);
			}
			this.writeC(this._done);
		}
	}

	private int _type;

	public ExBuySellList(int type)
	{
		this._type = type;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xB7);
		this.writeD(this._type);
	}
}