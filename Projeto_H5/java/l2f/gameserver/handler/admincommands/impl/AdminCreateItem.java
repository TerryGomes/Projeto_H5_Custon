package l2f.gameserver.handler.admincommands.impl;

import java.util.ArrayList;
import java.util.List;

import l2f.commons.dao.JdbcEntityState;
import l2f.gameserver.handler.admincommands.IAdminCommandHandler;
import l2f.gameserver.model.GameObject;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.model.base.Element;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.InventoryUpdate;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.utils.ItemFunctions;
import l2f.gameserver.utils.Location;

public class AdminCreateItem implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_itemcreate, admin_create_item, admin_create_item_all, admin_create_item_hwid, admin_create_item_char, admin_create_item_target, admin_create_item_range, admin_ci, admin_spreaditem, admin_add_pp, admin_add_pcp, admin_create_item_element
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.getPlayerAccess().UseGMShop)
		{
			return false;
		}

		switch (command)
		{
		case admin_itemcreate:
			activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/itemcreation.htm"));
			break;
		case admin_ci:
		case admin_create_item:
			try
			{
				if (wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: create_item id [count]");
					return false;
				}

				int item_id = Integer.parseInt(wordList[1]);
				long item_count = wordList.length < 3 ? 1 : Long.parseLong(wordList[2]);
				createItem(activeChar, item_id, item_count);
			}
			catch (NumberFormatException nfe)
			{
				activeChar.sendMessage("USAGE: create_item id [count]");
			}
			activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/itemcreation.htm"));
			break;
		case admin_create_item_hwid:
			try
			{
				if (wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: create_item id [count]");
					return false;
				}

				int item_id = Integer.parseInt(wordList[1]);
				long item_count = wordList.length < 3 ? 1 : Long.parseLong(wordList[2]);
				List<Player> rewardedPlayers = new ArrayList<>();
				for (Player player : GameObjectsStorage.getAllPlayersForIterate())
				{
					if (!player.isInOfflineMode() && player.getNetConnection() != null && !player.isInStoreMode() && noSameHwid(rewardedPlayers, player))
					{
						rewardedPlayers.add(player);
						createItem(player, item_id, item_count);
						player.sendMessage("You have been rewarded!");
					}
				}
			}
			catch (NumberFormatException nfe)
			{
				activeChar.sendMessage("USAGE: create_item id [count]");
			}
			activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/itemcreation.htm"));
			break;
		case admin_create_item_char:
			try
			{
				if (wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: create_item id [count]");
					return false;
				}

				int item_id = Integer.parseInt(wordList[1]);
				long item_count = wordList.length < 3 ? 1 : Long.parseLong(wordList[2]);
				List<Player> rewardedPlayers = new ArrayList<>();
				for (Player player : GameObjectsStorage.getAllPlayersForIterate())
				{
					if (!player.isInOfflineMode() && player.getNetConnection() != null && !player.isInStoreMode())
					{
						rewardedPlayers.add(player);
						createItem(player, item_id, item_count);
					}
				}
			}
			catch (NumberFormatException nfe)
			{
				activeChar.sendMessage("USAGE: create_item id [count]");
			}
			activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/itemcreation.htm"));
			break;
		case admin_create_item_target:
			try
			{
				GameObject target = activeChar.getTarget();
				if (target == null || !(target.isPlayer() || target.isPet()))
				{
					activeChar.sendPacket(SystemMsg.INVALID_TARGET);
					return false;
				}
				if (wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: create_item_target id [count]");
					return false;
				}

				int item_id = Integer.parseInt(wordList[1]);
				long item_count = wordList.length < 3 ? 1 : Long.parseLong(wordList[2]);
				createItem((Player) activeChar.getTarget(), item_id, item_count);
			}
			catch (NumberFormatException nfe)
			{
				activeChar.sendMessage("USAGE: create_item_target id [count]");
			}
			activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/itemcreation.htm"));
			break;
		case admin_create_item_range:
			try
			{
				if (wordList.length < 3)
				{
					activeChar.sendMessage("USAGE: create_item_range id count range");
					return false;
				}

				int item_id = Integer.parseInt(wordList[1]);
				int itemCount = Integer.parseInt(wordList[2]);
				int distance = Integer.parseInt(wordList[3]);
				List<Player> playersToReward = World.getAroundPlayers(activeChar, distance, 1000);
				int rewardedCount = 0;
				for (Player player : playersToReward)
				{
					if (!player.isInOfflineMode() && !player.isInStoreMode())
					{
						createItem(player, item_id, itemCount);
						rewardedCount++;
					}
				}
				activeChar.sendMessage("You have rewarded " + rewardedCount + " players!");
			}
			catch (NumberFormatException nfe)
			{
				activeChar.sendMessage("USAGE: create_item id [count]");
			}
			break;
		case admin_add_pp:
			try
			{
				GameObject target = activeChar.getTarget();
				if (target == null || !(target.isPlayer() || target.isPet()))
				{
					activeChar.sendPacket(SystemMsg.INVALID_TARGET);
					return false;
				}
				Player player = target.getPlayer();
				if (wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: add_pp [count]");
					return false;
				}

				int item_count = Integer.parseInt(wordList[1]);
				player.addPremiumPoints(item_count);
			}
			catch (NumberFormatException nfe)
			{
				activeChar.sendMessage("USAGE: add_pp [count]");
			}
			activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/itemcreation.htm"));
			break;
		case admin_add_pcp:
			try
			{
				GameObject target = activeChar.getTarget();
				if (target == null || !(target.isPlayer() || target.isPet()))
				{
					activeChar.sendPacket(SystemMsg.INVALID_TARGET);
					return false;
				}
				if (wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: add_pcp [count]");
					return false;
				}

				int item_count = Integer.parseInt(wordList[1]);
				Player player = target.getPlayer();
				player.addPcBangPoints(item_count, false);
			}
			catch (NumberFormatException nfe)
			{
				activeChar.sendMessage("USAGE: add_pcp [count]");
			}
			activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/itemcreation.htm"));
			break;
		case admin_spreaditem:
			try
			{
				int id = Integer.parseInt(wordList[1]);
				int num = wordList.length > 2 ? Integer.parseInt(wordList[2]) : 1;
				long count = wordList.length > 3 ? Long.parseLong(wordList[3]) : 1;
				for (int i = 0; i < num; i++)
				{
					ItemInstance createditem = ItemFunctions.createItem(id);
					createditem.setCount(count);
					createditem.dropMe(activeChar, Location.findPointToStay(activeChar, 100));
				}
			}
			catch (NumberFormatException nfe)
			{
				activeChar.sendMessage("Specify a valid number.");
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Can't create this item.");
			}
			break;
		case admin_create_item_element:
			try
			{
				if (wordList.length < 4)
				{
					activeChar.sendMessage("USAGE: create_item_attribue [id] [element id] [value]");
					return false;
				}

				int item_id = Integer.parseInt(wordList[1]);
				int elementId = Integer.parseInt(wordList[2]);
				int value = Integer.parseInt(wordList[3]);
				if (elementId > 5 || elementId < 0)
				{
					activeChar.sendMessage("Improper element Id");
					return false;
				}
				if (value < 1 || value > 300)
				{
					activeChar.sendMessage("Improper element value");
					return false;
				}

				ItemInstance item = createItem(activeChar, item_id, 1);
				Element element = Element.getElementById(elementId);
				item.setAttributeElement(element, item.getAttributeElementValue(element, false) + value);
				item.setJdbcState(JdbcEntityState.UPDATED);
				item.update();
				activeChar.sendPacket(new InventoryUpdate().addModifiedItem(item));
			}
			catch (NumberFormatException nfe)
			{
				activeChar.sendMessage("USAGE: create_item id [count]");
			}
			activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/itemcreation.htm"));
			break;
		}

		return true;
	}

	private boolean noSameHwid(List<Player> rewardedPlayers, Player player)
	{
		for (Player iPlayer : rewardedPlayers)
		{
			if (iPlayer.getHWID().equals(player.getHWID()))
			{
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private ItemInstance createItem(Player activeChar, int itemId, long count)
	{
		ItemInstance createditem = ItemFunctions.createItem(itemId);
		createditem.setCount(count);
		activeChar.getInventory().addItem(createditem, "AdminCreateItem");
		if (!createditem.isStackable())
		{
			for (long i = 0; i < count - 1; i++)
			{
				createditem = ItemFunctions.createItem(itemId);
				activeChar.getInventory().addItem(createditem, "AdminCreateItem");
			}
		}
		activeChar.sendPacket(SystemMessage2.obtainItems(itemId, count, 0));
		return createditem;
	}
}