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
			_adena = activeChar.getAdena();
			_taxRate = taxRate;

			if (tradeList != null)
			{
				_listId = tradeList.getListId();
				_buyList = tradeList.getItems();
				activeChar.setBuyListId(_listId);
			}
			else
			{
				_listId = 0;
				_buyList = Collections.emptyList();
				activeChar.setBuyListId(0);
			}
		}

		@Override
		protected void writeImpl()
		{
			super.writeImpl();
			writeQ(_adena); // current money
			writeD(_listId);
			writeH(_buyList.size());
			for (TradeItem item : _buyList)
			{
				writeItemInfo(item, item.getCurrentValue());
				writeQ((long) (item.getOwnersPrice() * (1. + _taxRate)));
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
			_done = done ? 1 : 0;
			if (done)
			{
				_refundList = Collections.emptyList();
				_sellList = Collections.emptyList();
			}
			else
			{
				ItemInstance[] items = activeChar.getRefund().getItems();
				_refundList = new ArrayList<TradeItem>(items.length);
				for (ItemInstance item : items)
				{
					_refundList.add(new TradeItem(item));
				}

				items = activeChar.getInventory().getItems();
				_sellList = new ArrayList<TradeItem>(items.length);
				for (ItemInstance item : items)
				{
					if (item.canBeSold(activeChar))
					{
						_sellList.add(new TradeItem(item));
					}
				}
			}
		}

		@Override
		protected void writeImpl()
		{
			super.writeImpl();
			writeH(_sellList.size());
			for (TradeItem item : _sellList)
			{
				writeItemInfo(item);
				writeQ(item.getReferencePrice() / 2);
			}
			writeH(_refundList.size());
			for (TradeItem item : _refundList)
			{
				writeItemInfo(item);
				writeD(item.getObjectId());
				writeQ(item.getCount() * item.getReferencePrice() / 2);
			}
			writeC(_done);
		}
	}

	private int _type;

	public ExBuySellList(int type)
	{
		_type = type;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0xB7);
		writeD(_type);
	}
}