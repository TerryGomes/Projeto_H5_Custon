package l2mv.gameserver.model.entity.CCPHelpers.itemLogs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.TradeItem;

public class ItemLogHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(ItemLogHandler.class);
	private static final SingleItemLog[] EMPTY_RECEIVED_ITEMS = new SingleItemLog[0];
	private int lastActionId;
	private final Object lock;

	public ItemLogHandler()
	{
		lastActionId = 0;
		lock = new Object();
	}

	public void addLog(Player player, ItemInstance itemLost, ItemActionType actionType)
	{
		addLog(player, itemLost, 1L, actionType);
	}

	public void addLog(Player player, ItemInstance itemLost, long count, ItemActionType actionType)
	{
		final SingleItemLog[] lostItems =
		{
			new SingleItemLog(itemLost.getItemId(), count, itemLost.getEnchantLevel(), itemLost.getObjectId())
		};
		this.addLog(player, lostItems, actionType);
	}

	public void addLog(Player player, List<ItemInstance> itemsLost, ItemActionType actionType)
	{
		SingleItemLog[] lostItems = new SingleItemLog[itemsLost.size()];
		for (int i = 0; i < itemsLost.size(); i++)
		{
			final ItemInstance itemLost = itemsLost.get(i);
			lostItems[i] = new SingleItemLog(itemLost.getItemId(), itemLost.getCount(), itemLost.getEnchantLevel(), itemLost.getObjectId());
		}
		addLog(player, lostItems, actionType);
	}

	public void addLog(Player player, List<ItemInstance> itemsLost, String receiverName, ItemActionType actionType)
	{
		SingleItemLog[] lostItems = new SingleItemLog[itemsLost.size()];
		for (int i = 0; i < itemsLost.size(); i++)
		{
			final ItemInstance itemLost = itemsLost.get(i);
			(lostItems[i] = new SingleItemLog(itemLost.getItemId(), itemLost.getCount(), itemLost.getEnchantLevel(), itemLost.getObjectId())).setReceiverName(receiverName);
		}

		addLog(player, lostItems, actionType);
	}

	public void addLog(Player player, List<TradeItem> items, boolean lost, ItemActionType actionType)
	{
		SingleItemLog[] itemLogs = new SingleItemLog[items.size()];
		for (int i = 0; i < items.size(); i++)
		{
			final TradeItem item = items.get(i);
			itemLogs[i] = new SingleItemLog(item.getItemId(), item.getCount(), item.getEnchantLevel(), item.getObjectId());
		}
		addLog(player, itemLogs, actionType);
	}

	public SingleItemLog[] getAdenaItemLog(Player oldOwner, Player newOwner, long count, boolean lost)
	{
		SingleItemLog[] lostItems = new SingleItemLog[1];
		ItemInstance item = oldOwner.getInventory().getItemByItemId(57);
		lostItems[0] = new SingleItemLog(57, count, 0, item == null ? -1 : item.getObjectId());
		lostItems[0].setReceiverName(newOwner.getName());
		return lostItems;
	}

	public void addLogItemsForAdena(Player itemsWinner, Player adenaWinner, List<TradeItem> receivedItems, long lostAdenaCount, ItemActionType itemsWinnerActionType, ItemActionType adenaWinnerActionType)
	{
		SingleItemLog[] itemLogs = new SingleItemLog[receivedItems.size()];
		for (int i = 0; i < receivedItems.size(); i++)
		{
			TradeItem item = receivedItems.get(i);
			itemLogs[i] = new SingleItemLog(item.getItemId(), item.getCount(), item.getEnchantLevel(), item.getObjectId());
			itemLogs[i].setReceiverName(itemsWinner.getName());
		}
		SingleItemLog[] adenaLogs = getAdenaItemLog(itemsWinner, adenaWinner, lostAdenaCount, true);

		addLog(itemsWinner, adenaLogs, itemLogs, itemsWinnerActionType);

		addLog(adenaWinner, itemLogs, adenaLogs, adenaWinnerActionType);
	}

	public void addLogItemForItems(Player buyer, Player seller, List<TradeItem> boughtItems, List<TradeItem> givenItems, ItemActionType actionType)
	{
		SingleItemLog[] boughtItemsLogs = new SingleItemLog[boughtItems.size()];
		for (int i = 0; i < boughtItems.size(); i++)
		{
			TradeItem item = boughtItems.get(i);
			boughtItemsLogs[i] = new SingleItemLog(item.getItemId(), item.getCount(), item.getEnchantLevel(), item.getObjectId());
			boughtItemsLogs[i].setReceiverName(buyer.getName());
		}
		SingleItemLog[] givenItemsLogs = new SingleItemLog[givenItems.size()];
		for (int i = 0; i < givenItems.size(); i++)
		{
			TradeItem item = givenItems.get(i);
			givenItemsLogs[i] = new SingleItemLog(item.getItemId(), item.getCount(), item.getEnchantLevel(), item.getObjectId());
			givenItemsLogs[i].setReceiverName(seller.getName());
		}

		addLog(buyer, givenItemsLogs, boughtItemsLogs, actionType);
	}

	public void addLog(Player seller, Player buyer, List<TradeItem> sellList, long adenaReward, ItemActionType sellerActionType, ItemActionType buyerActionType)
	{
		addLogItemsForAdena(buyer, seller, sellList, adenaReward, buyerActionType, sellerActionType);
	}

	public void addLog(Player trader1, Player trader2, List<TradeItem> itemsFromTrader1, List<TradeItem> itemsFromTrader2, ItemActionType actionType)
	{
		addLogItemForItems(trader2, trader1, itemsFromTrader2, itemsFromTrader1, actionType);
		addLogItemForItems(trader1, trader2, itemsFromTrader1, itemsFromTrader2, actionType);
	}

	public void addLog(Player player, SingleItemLog[] lostItems, ItemActionType actionType)
	{
		addLog(player, lostItems, EMPTY_RECEIVED_ITEMS, actionType);
	}

	public void addLog(Player player, SingleItemLog[] lostItems, SingleItemLog[] receivedItems, ItemActionType actionType)
	{
		final long time = System.currentTimeMillis();
		final ItemActionLog actionLog = new ItemActionLog(getNextActionId(), player.getObjectId(), actionType, time, lostItems, receivedItems);
		ItemLogList.getInstance().addLogs(actionLog);
	}

	public void onPickUp(Player player, ItemInstance item)
	{
		ItemLogList.getInstance().fillReceiver(item.getObjectId(), player.getName());
	}

	public int getNextActionId()
	{
		final int actionId;
		synchronized (lock)
		{
			++lastActionId;
			actionId = lastActionId;
		}
		return actionId;
	}

	public void loadLastActionId(Connection con)
	{
		if (!Config.ENABLE_PLAYER_ITEM_LOGS || !Config.PLAYER_ITEM_LOGS_SAVED_IN_DB)
		{
			return;
		}
		try (PreparedStatement statement = con.prepareStatement("SELECT log_id FROM logs ORDER BY log_id DESC LIMIT 1"); final ResultSet rset = statement.executeQuery())
		{
			if (rset.next())
			{
				final int lastSavedActionId = rset.getInt("log_id");
				synchronized (lock)
				{
					lastActionId = (lastSavedActionId > 0 ? lastSavedActionId : 0) + 1;
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while loading LastActionId: ", e);
		}
	}

	public static ItemLogHandler getInstance()
	{
		return ItemLogHandlerHolder.instance;
	}

	private static class ItemLogHandlerHolder
	{
		private static final ItemLogHandler instance = new ItemLogHandler();
	}
}