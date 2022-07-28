package l2mv.gameserver.network.clientpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.commons.math.SafeMath;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Request;
import l2mv.gameserver.model.Request.L2RequestType;
import l2mv.gameserver.model.entity.CCPHelpers.itemLogs.ItemActionType;
import l2mv.gameserver.model.entity.CCPHelpers.itemLogs.ItemLogHandler;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.TradeItem;
import l2mv.gameserver.network.serverpackets.SendTradeDone;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.TradePressOtherOk;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.utils.ItemActionLog;
import l2mv.gameserver.utils.ItemStateLog;
import l2mv.gameserver.utils.Log;

public class TradeDone extends L2GameClientPacket
{
	private int _response;

	@Override
	protected void readImpl()
	{
		this._response = this.readD();
	}

	@Override
	protected void runImpl()
	{
		Player parthner1 = this.getClient().getActiveChar();
		if (parthner1 == null)
		{
			return;
		}
		Request request = parthner1.getRequest();
		if (request == null || !request.isTypeOf(L2RequestType.TRADE))
		{
			parthner1.sendActionFailed();
			return;
		}

		if (!request.isInProgress() || parthner1.isOutOfControl())
		{
			request.cancel();
			parthner1.sendPacket(SendTradeDone.FAIL);
			parthner1.sendActionFailed();
			return;
		}

		Player parthner2 = request.getOtherPlayer(parthner1);
		if (parthner2 == null)
		{
			request.cancel();
			parthner1.sendPacket(SendTradeDone.FAIL);
			parthner1.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
			parthner1.sendActionFailed();
			return;
		}

		if (parthner2.getRequest() != request)
		{
			request.cancel();
			parthner1.sendPacket(SendTradeDone.FAIL);
			parthner1.sendActionFailed();
			return;
		}

		if (this._response == 0)
		{
			request.cancel();
			parthner1.sendPacket(SendTradeDone.FAIL);
			parthner2.sendPacket(SendTradeDone.FAIL, new SystemMessage2(SystemMsg.C1_HAS_CANCELLED_THE_TRADE).addString(parthner1.getName()));
			return;
		}

		if (!parthner1.isInRangeZ(parthner2, Creature.INTERACTION_DISTANCE))
		{
			parthner1.sendPacket(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
			return;
		}

		// first party accepted the trade
		// notify clients that "OK" button has been pressed.
		request.confirm(parthner1);
		parthner2.sendPacket(new SystemMessage2(SystemMsg.C1_HAS_CONFIRMED_THE_TRADE).addString(parthner1.getName()), TradePressOtherOk.STATIC);

		if (!request.isConfirmed(parthner2)) // Check for dual confirmation
		{
			parthner1.sendActionFailed();
			return;
		}

		List<TradeItem> tradeList1 = parthner1.getTradeList();
		List<TradeItem> tradeList2 = parthner2.getTradeList();
		int slots = 0;
		long weight = 0;
		boolean success = false;

		parthner1.getInventory().writeLock();
		parthner2.getInventory().writeLock();
		try
		{
			slots = 0;
			weight = 0;

			for (TradeItem ti : tradeList1)
			{
				ItemInstance item = parthner1.getInventory().getItemByObjectId(ti.getObjectId());
				if (item == null || item.getCount() < ti.getCount() || !item.canBeTraded(parthner1))
				{
					return;
				}

				weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(ti.getCount(), ti.getItem().getWeight()));
				if (!ti.getItem().isStackable() || parthner2.getInventory().getItemByItemId(ti.getItemId()) == null)
				{
					slots++;
				}
			}

			if (!parthner2.getInventory().validateWeight(weight))
			{
				parthner2.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
				return;
			}

			if (!parthner2.getInventory().validateCapacity(slots))
			{
				parthner2.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
				return;
			}

			slots = 0;
			weight = 0;

			for (TradeItem ti : tradeList2)
			{
				ItemInstance item = parthner2.getInventory().getItemByObjectId(ti.getObjectId());
				if (item == null || item.getCount() < ti.getCount() || !item.canBeTraded(parthner2))
				{
					return;
				}

				weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(ti.getCount(), ti.getItem().getWeight()));
				if (!ti.getItem().isStackable() || parthner1.getInventory().getItemByItemId(ti.getItemId()) == null)
				{
					slots++;
				}
			}

			if (!parthner1.getInventory().validateWeight(weight))
			{
				parthner1.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
				return;
			}

			if (!parthner1.getInventory().validateCapacity(slots))
			{
				parthner1.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
				return;
			}

			final List<ItemActionLog> actionLogs = new ArrayList<ItemActionLog>((tradeList1.size() + tradeList2.size()) * 2);
			for (TradeItem ti2 : tradeList1)
			{
				final ItemInstance oldFullItem = parthner1.getInventory().getItemByObjectId(ti2.getObjectId());
				actionLogs.add(new ItemActionLog(ItemStateLog.EXCHANGE_LOSE, "Trade", parthner1, oldFullItem, ti2.getCount()));
				final ItemInstance itemToTrade = parthner1.getInventory().removeItemByObjectId(ti2.getObjectId(), ti2.getCount(), null);
				final ItemInstance newFullItem = parthner2.getInventory().addItem(itemToTrade, null);
				actionLogs.add(new ItemActionLog(ItemStateLog.EXCHANGE_GAIN, "Trade", parthner2, newFullItem, ti2.getCount()));
			}
			for (TradeItem ti2 : tradeList2)
			{
				final ItemInstance oldFullItem = parthner2.getInventory().getItemByObjectId(ti2.getObjectId());
				actionLogs.add(new ItemActionLog(ItemStateLog.EXCHANGE_LOSE, "Trade", parthner2, oldFullItem, ti2.getCount()));
				final ItemInstance item2 = parthner2.getInventory().removeItemByObjectId(ti2.getObjectId(), ti2.getCount(), null);
				final ItemInstance newFullItem = parthner1.getInventory().addItem(item2, null);
				actionLogs.add(new ItemActionLog(ItemStateLog.EXCHANGE_GAIN, "Trade", parthner1, newFullItem, ti2.getCount()));
			}

			Log.logItemActions(parthner1.toString() + " Trade With " + parthner2.toString(), actionLogs);

			parthner1.sendPacket(SystemMsg.YOUR_TRADE_WAS_SUCCESSFUL);
			parthner2.sendPacket(SystemMsg.YOUR_TRADE_WAS_SUCCESSFUL);

			ItemLogHandler.getInstance().addLog(parthner1, parthner2, tradeList2, tradeList1, ItemActionType.TRADE);

			success = true;
		}
		finally
		{
			parthner2.getInventory().writeUnlock();
			parthner1.getInventory().writeUnlock();

			request.done();

			parthner1.sendPacket(success ? SendTradeDone.SUCCESS : SendTradeDone.FAIL);
			parthner2.sendPacket(success ? SendTradeDone.SUCCESS : SendTradeDone.FAIL);
		}
	}
}