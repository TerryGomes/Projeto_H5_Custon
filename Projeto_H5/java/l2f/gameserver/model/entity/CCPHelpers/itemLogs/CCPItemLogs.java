package l2f.gameserver.model.entity.CCPHelpers.itemLogs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import l2f.gameserver.Config;
import l2f.gameserver.data.HtmPropHolder;
import l2f.gameserver.data.HtmPropList;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.ShowBoard;
import l2f.gameserver.templates.item.ItemTemplate;

public class CCPItemLogs
{
	public static final int FIRST_PAGE_INDEX = 0;

	private static int tableHeight = -100;
	private static int headerHeight = -100;
	private static int itemHeight = -100;
	private static int maxHeight = -100;

	public static void showPage(Player player)
	{
		showPage(player, player, 0);
	}

	public static void showPage(Player activeChar, Player logsOwnerPlayer, int pageIndex)
	{
		if (!Config.ENABLE_PLAYER_ITEM_LOGS)
		{
			return;
		}
		if (tableHeight == -100)
		{
			final HtmPropList props = HtmPropHolder.getList(Config.BBS_HOME_DIR + "pages/itemLogs.prop.htm");
			tableHeight = Integer.parseInt(props.getText("table_height"));
			headerHeight = Integer.parseInt(props.getText("header_height"));
			itemHeight = Integer.parseInt(props.getText("item_height"));
			maxHeight = Integer.parseInt(props.getText("page_max_height"));
		}

		final String html = preparePage(activeChar, logsOwnerPlayer, pageIndex);
		ShowBoard.separateAndSend(html, activeChar);
	}

	private static String preparePage(Player activeChar, Player logsOwnerPlayer, int pageIndex)
	{
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
		final HtmPropList props = HtmPropHolder.getList(Config.BBS_HOME_DIR + "pages/itemLogs.prop.htm");
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/itemLogs.htm", activeChar);
		final List<ItemActionLog> wrongOrderAllLogs = ItemLogList.getInstance().getLogs(logsOwnerPlayer);
		final List<ItemActionLog> allLogs = changeOrder(wrongOrderAllLogs);
		final int[] pageItemToStartFrom = getLogIndexToStartFrom(allLogs, pageIndex);
		final StringBuilder tablesLeft = new StringBuilder();
		final StringBuilder tablesRight = new StringBuilder();
		int side = 0;
		int heightReached = 0;
		int startingItemIndex = pageItemToStartFrom[1];
		int itemIndex = 0;
		int logCount = startingItemIndex;

		for (int logIndex = pageItemToStartFrom[0]; (logIndex < allLogs.size()) && (side < 2); logIndex++)
		{
			boolean changeSide = false;
			final ItemActionLog log = allLogs.get(logIndex);
			final String table = getLogsTable(activeChar, logsOwnerPlayer, log, heightReached, startingItemIndex, itemIndex, dateFormat);
			if (table == null || activeChar.containsQuickVar("CCPItemLogsStartingItemIndex"))
			{
				changeSide = true;
				heightReached = 0;
				logCount--;
				startingItemIndex = activeChar.getQuickVarI("CCPItemLogsStartingItemIndex", 0);
				activeChar.deleteQuickVar("CCPItemLogsStartingItemIndex");
			}
			else
			{
				itemIndex += log.getItemsReceived().length + log.getItemsLost().length;
				heightReached += activeChar.getQuickVarI("CCPItemLogsHeightReached", 0);
				startingItemIndex = 0;
				logCount++;
			}

			if (table != null)
			{
				if (side == 0)
				{
					tablesLeft.append(table);
				}
				else
				{
					tablesRight.append(table);
				}
			}
			if (changeSide)
			{
				side++;
			}
		}
		html = html.replace("%tablesLeft%", tablesLeft.length() > 0 ? tablesLeft : "<br>");
		html = html.replace("%tablesRight%", tablesRight.length() > 0 ? tablesRight : "<br>");
		html = html.replace("%previousBtn%", pageIndex > 0 ? props.getText("PreviousBtn").replace("%page%", String.valueOf(pageIndex - 1)) : "<br>");
		html = html.replace("%nextBtn%", (logCount < allLogs.size()) || (startingItemIndex > 0) ? props.getText("NextBtn").replace("%page%", String.valueOf(pageIndex + 1)) : "<br>");
		html = html.replace("%targetId%", String.valueOf(logsOwnerPlayer.getObjectId()));

		return html;
	}

	private static String getLogsTable(Player activeChar, Player logsOwnerPlayer, ItemActionLog log, int heightReached, int startingItemIndex, int itemIndex, SimpleDateFormat dateFormat)
	{
		final HtmPropList props = HtmPropHolder.getList(Config.BBS_HOME_DIR + "pages/itemLogs.prop.htm");
		final String date = dateFormat.format(new Date(log.getTime()));
		if (heightReached + CCPItemLogs.tableHeight + CCPItemLogs.headerHeight + CCPItemLogs.itemHeight > CCPItemLogs.maxHeight)
		{
			return null;
		}
		int newHeight = heightReached + CCPItemLogs.tableHeight;
		String table = props.getText("table");

		if (startingItemIndex == 0)
		{
			String header = props.getText("header");
			header = header.replace("%actionType%", log.getActionType().getNiceName());
			table = table.replace("%header%", header);
			newHeight += headerHeight;
		}
		else
		{
			table = table.replace("%header%", "");
		}

		final StringBuilder itemsBuilder = new StringBuilder();
		for (int i = 0; i < 2; i++)
		{
			final SingleItemLog[] items = i == 0 ? log.getItemsReceived() : log.getItemsLost();
			if (startingItemIndex > items.length)
			{
				startingItemIndex -= items.length;
			}
			else
			{
				for (int currentItemIndex = startingItemIndex; currentItemIndex < items.length; currentItemIndex++)
				{
					SingleItemLog item = items[currentItemIndex];

					if (newHeight + itemHeight > maxHeight)
					{
						final int totalItemIndex = currentItemIndex + (i > 0 ? log.getItemsReceived().length : 0);
						activeChar.addQuickVar("CCPItemLogsStartingItemIndex", totalItemIndex);
						return table.replace("%items%", itemsBuilder.toString());
					}
					final ItemTemplate template = ItemHolder.getInstance().getTemplate(item.getItemTemplateId());
					String itemText = props.getText("item");
					itemText = itemText.replace("%itemTableColor%", itemIndex % 2 == 0 ? props.getText("item_table_color_0") : props.getText("item_table_color_1"));
					itemText = itemText.replace("%icon%", template.getIcon());
					final String itemName = template.getName() + (item.getItemEnchantLevel() > 0 ? " + " + item.getItemEnchantLevel() : "") + (item.getItemCount() > 1L ? " x " + item.getItemCount() : "");
					itemText = itemText.replace("%itemName%", itemName);
					itemText = itemText.replace("%time%", date);
					final String receiverName = item.getReceiverName() != null && !item.getReceiverName().isEmpty() ? item.getReceiverName() : "Nobody";
					itemText = itemText.replace("%receiverColor%", receiverName.equals(logsOwnerPlayer.getName()) ? props.getText("receiver_color_owner") : props.getText("receiver_color_alien"));
					itemText = itemText.replace("%receiverName%", receiverName);

					itemsBuilder.append(itemText);
					itemIndex++;
					newHeight += itemHeight;
				}

				startingItemIndex = 0;
			}
		}
		activeChar.deleteQuickVar("CCPItemLogsStartingItemIndex");
		activeChar.addQuickVar("CCPItemLogsHeightReached", newHeight - heightReached);
		return table.replace("%items%", itemsBuilder.toString());
	}

	public static int[] getLogIndexToStartFrom(List<ItemActionLog> allLogs, int pageIndexToReach)
	{
		if (pageIndexToReach <= 0)
		{
			return new int[]
			{
				0,
				0
			};
		}
		int pageReached = 0;
		boolean useRightSide = false;
		int heightReached = 0;
		int startingItem = 0;

		for (int logIndex = 0; logIndex < allLogs.size(); logIndex++)
		{
			final ItemActionLog log = allLogs.get(logIndex);
			final int[] itemHeightReached = getItemAndHeightReached(log, startingItem, heightReached);
			startingItem = itemHeightReached[0];
			heightReached = itemHeightReached[1];
			if (startingItem == -1 || startingItem < Integer.MAX_VALUE)
			{
				heightReached = 0;

				if (startingItem < 0)
				{
					startingItem = 0;
				}
				if (useRightSide)
				{
					pageReached++;
					if (pageReached >= pageIndexToReach)
					{
						return new int[]
						{
							logIndex,
							startingItem
						};
					}
				}
				else
				{
					useRightSide = true;
				}
				logIndex--;
			}
			else if (startingItem == Integer.MAX_VALUE)
			{
				startingItem = 0;
			}
		}
		return new int[]
		{
			0,
			0
		};
	}

	private static int[] getItemAndHeightReached(ItemActionLog log, int startFromItem, int heightReached)
	{
		if (heightReached + CCPItemLogs.tableHeight + CCPItemLogs.headerHeight + CCPItemLogs.itemHeight > CCPItemLogs.maxHeight)
		{
			return new int[]
			{
				-1,
				heightReached
			};
		}
		int newHeight = heightReached + CCPItemLogs.tableHeight;
		if (startFromItem == 0)
		{
			newHeight += CCPItemLogs.headerHeight;
		}
		for (int item = startFromItem; item < log.getItemsReceived().length + log.getItemsLost().length; ++item)
		{
			if (newHeight + CCPItemLogs.itemHeight > CCPItemLogs.maxHeight)
			{
				return new int[]
				{
					item,
					newHeight
				};
			}
			newHeight += CCPItemLogs.itemHeight;
		}
		return new int[]
		{
			Integer.MAX_VALUE,
			newHeight
		};
	}

	private static List<ItemActionLog> changeOrder(List<ItemActionLog> wrongOrderAllLogs)
	{
		if (wrongOrderAllLogs.isEmpty())
		{
			return wrongOrderAllLogs;
		}
		List<ItemActionLog> logs = new ArrayList<>();
		for (int i = wrongOrderAllLogs.size() - 1; i >= 0; i--)
		{
			logs.add(wrongOrderAllLogs.get(i));
		}
		return logs;
	}
}