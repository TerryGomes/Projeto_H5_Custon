package l2mv.gameserver.network.clientpackets;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.math.SafeMath;
import l2mv.gameserver.Config;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.CCPHelpers.itemLogs.ItemActionType;
import l2mv.gameserver.model.entity.CCPHelpers.itemLogs.ItemLogHandler;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.TradeItem;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.utils.TradeHelper;

/**
 * Список продаваемого в приватный магазин покупки
 *
 */
public class RequestPrivateStoreBuySellList extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestPrivateStoreBuySellList.class);

	private int _buyerId, _count;
	private int[] _items; // object id
	private long[] _itemQ; // count
	private long[] _itemP; // price

	@Override
	protected void readImpl()
	{
		this._buyerId = this.readD();
		this._count = this.readD();

		if (this._count * 28 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1)
		{
			this._count = 0;
			return;
		}

		this._items = new int[this._count];
		this._itemQ = new long[this._count];
		this._itemP = new long[this._count];

		for (int i = 0; i < this._count; i++)
		{
			this._items[i] = this.readD();
			this.readD(); // itemId
			this.readH();
			this.readH();
			this._itemQ[i] = this.readQ();
			this._itemP[i] = this.readQ();

			if (this._itemQ[i] < 1 || this._itemP[i] < 1 || ArrayUtils.indexOf(this._items, this._items[i]) < i)
			{
				this._count = 0;
				break;
			}
		}
	}

	@Override
	protected void runImpl()
	{
		Player seller = this.getClient().getActiveChar();
		if (seller == null || this._count == 0)
		{
			return;
		}

		if (seller.isActionsDisabled() || seller.isBlocked() || !Config.ALLOW_PRIVATE_STORES)
		{
			seller.sendActionFailed();
			return;
		}

		if (seller.isInStoreMode())
		{
			seller.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}

		if (seller.isInTrade())
		{
			seller.sendActionFailed();
			return;
		}

		if (seller.isFishing())
		{
			seller.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
			return;
		}

		if (!seller.getPlayerAccess().UseTrade)
		{
			seller.sendPacket(SystemMsg.SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_____);
			return;
		}

		Player buyer = (Player) seller.getVisibleObject(this._buyerId);
		if (buyer == null || buyer.getPrivateStoreType() != Player.STORE_PRIVATE_BUY || !seller.isInRangeZ(buyer, Creature.INTERACTION_DISTANCE))
		{
			seller.sendPacket(SystemMsg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
			seller.sendActionFailed();
			return;
		}

		List<TradeItem> buyList = buyer.getBuyList();
		if (buyList.isEmpty())
		{
			seller.sendPacket(SystemMsg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
			seller.sendActionFailed();
			return;
		}

		List<TradeItem> sellList = new ArrayList<TradeItem>();

		long totalCost = 0;
		int slots = 0;
		long weight = 0;

		buyer.getInventory().writeLock();
		seller.getInventory().writeLock();
		try
		{
			loop:
			for (int i = 0; i < this._count; i++)
			{
				int objectId = this._items[i];
				long count = this._itemQ[i];
				long price = this._itemP[i];

				ItemInstance item = seller.getInventory().getItemByObjectId(objectId);
				if (item == null || item.getCount() < count || !item.canBeTraded(seller))
				{
					break loop;
				}

				TradeItem si = null;

				for (TradeItem bi : buyList)
				{
					if (bi.getItemId() == item.getItemId())
					{
						if (bi.getOwnersPrice() == price)
						{
							if (count > bi.getCount())
							{
								break loop;
							}

							totalCost = SafeMath.addAndCheck(totalCost, SafeMath.mulAndCheck(count, price));
							weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(count, item.getTemplate().getWeight()));
							if (!item.isStackable() || buyer.getInventory().getItemByItemId(item.getItemId()) == null)
							{
								slots++;
							}

							si = new TradeItem();
							si.setObjectId(objectId);
							si.setItemId(item.getItemId());
							si.setCount(count);
							si.setOwnersPrice(price);

							sellList.add(si);
							break;
						}
					}
				}
			}
		}
		catch (ArithmeticException ae)
		{
			// TODO audit
			sellList.clear();
			seller.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			try
			{
				if (sellList.size() != this._count)
				{
					seller.sendPacket(SystemMsg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
					seller.sendActionFailed();
					return;
				}
				if (!buyer.getInventory().validateWeight(weight))
				{
					buyer.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
					seller.sendPacket(SystemMsg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
					seller.sendActionFailed();
					return;
				}
				if (!buyer.getInventory().validateCapacity(slots))
				{
					buyer.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
					seller.sendPacket(SystemMsg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
					seller.sendActionFailed();
					return;
				}
				if (!buyer.reduceAdena(totalCost, "Private Store Buy from" + seller.toString()))
				{
					buyer.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
					seller.sendPacket(SystemMsg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
					seller.sendActionFailed();
					return;
				}
				for (TradeItem si2 : sellList)
				{
					final ItemInstance item2 = seller.getInventory().removeItemByObjectId(si2.getObjectId(), si2.getCount(), "Private Store Sell to " + buyer.toString());
					for (TradeItem bi2 : buyList)
					{
						if (bi2.getItemId() == si2.getItemId() && bi2.getOwnersPrice() == si2.getOwnersPrice())
						{
							bi2.setCount(bi2.getCount() - si2.getCount());
							if (bi2.getCount() < 1L)
							{
								buyList.remove(bi2);
								break;
							}
							break;
						}
					}
					buyer.getInventory().addItem(item2, "Private Store Buy from " + seller.toString());
					TradeHelper.purchaseItem(buyer, seller, si2);
				}
				final long tax = TradeHelper.getTax(seller, totalCost);
				if (tax > 0L)
				{
					totalCost -= tax;
					seller.sendMessage(new CustomMessage("trade.HavePaidTax", seller, new Object[0]).addNumber(tax));
				}
				seller.addAdena(totalCost, "Private Store Sell to " + buyer.toString());
				buyer.saveTradeList();
				ItemLogHandler.getInstance().addLog(seller, buyer, sellList, totalCost, ItemActionType.SOLD_IN_STORE, ItemActionType.BOUGHT_IN_STORE);
			}
			finally
			{
				seller.getInventory().writeUnlock();
				buyer.getInventory().writeUnlock();
			}
			return;
		}
		finally
		{
			try
			{
				if (sellList.size() != this._count)
				{
					seller.sendPacket(SystemMsg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
					seller.sendActionFailed();
					return;
				}

				if (!buyer.getInventory().validateWeight(weight))
				{
					buyer.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
					seller.sendPacket(SystemMsg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
					seller.sendActionFailed();
					return;
				}

				if (!buyer.getInventory().validateCapacity(slots))
				{
					buyer.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
					seller.sendPacket(SystemMsg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
					seller.sendActionFailed();
					return;
				}

				if (!buyer.reduceAdena(totalCost, "Private Store Buy from" + seller.toString()))
				{
					buyer.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
					seller.sendPacket(SystemMsg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
					seller.sendActionFailed();
					return;
				}

				ItemInstance item;
				for (TradeItem si : sellList)
				{
					item = seller.getInventory().removeItemByObjectId(si.getObjectId(), si.getCount(), "Private Store Sell to " + buyer.toString());
					for (TradeItem bi : buyList)
					{
						if (bi.getItemId() == si.getItemId())
						{
							if (bi.getOwnersPrice() == si.getOwnersPrice())
							{
								bi.setCount(bi.getCount() - si.getCount());
								if (bi.getCount() < 1L)
								{
									buyList.remove(bi);
								}
								break;
							}
						}
					}
					buyer.getInventory().addItem(item, "Private Store Buy from " + seller.toString());
					TradeHelper.purchaseItem(buyer, seller, si);
				}

				long tax = TradeHelper.getTax(seller, totalCost);
				if (tax > 0)
				{
					totalCost -= tax;
					seller.sendMessage(new CustomMessage("trade.HavePaidTax", seller).addNumber(tax));
				}

				seller.addAdena(totalCost, "Private Store Sell to " + buyer.toString());
				buyer.saveTradeList();

				ItemLogHandler.getInstance().addLog(seller, buyer, sellList, totalCost, ItemActionType.SOLD_IN_STORE, ItemActionType.BOUGHT_IN_STORE);
			}
			finally
			{
				seller.getInventory().writeUnlock();
				buyer.getInventory().writeUnlock();
			}
		}

		if (buyList.isEmpty())
		{
			TradeHelper.cancelStore(buyer);
		}

		seller.sendChanges();
		buyer.sendChanges();

		seller.sendActionFailed();
	}
}