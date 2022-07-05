package l2f.gameserver.network.clientpackets;

import org.apache.commons.lang3.ArrayUtils;

import l2f.gameserver.Config;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.model.base.TeamType;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.ExAutoSoulShot;
import l2f.gameserver.network.serverpackets.ExGMViewQuestItemList;
import l2f.gameserver.network.serverpackets.ExUseSharedGroupItem;
import l2f.gameserver.network.serverpackets.GMHennaInfo;
import l2f.gameserver.network.serverpackets.GMViewItemList;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.skills.TimeStamp;
import l2f.gameserver.tables.PetDataTable;
import l2f.gameserver.utils.Util;

public class UseItem extends L2GameClientPacket
{
	private static final int[] AUTO_POTIONS_IDS = new int[]
	{
		1539,
		728
		/* , 5592 */ };

	private int _objectId;
	private boolean _ctrlPressed;
	private long _timeSent;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_ctrlPressed = readD() == 1;
		_timeSent = System.currentTimeMillis();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		activeChar.setActive();

		ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);
		if (item == null) // Support for GMs deleting items from alt+g inventory.
		{
			for (Player player : GameObjectsStorage.getAllPlayersForIterate()) // There is no way to get item by objectId!!! Or im very stupid to not know such.
			{
				if ((item = player.getInventory().getItemByObjectId(_objectId)) != null)
				{
					break;
				}
			}
		}

		if (item == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		int itemId = item.getItemId();

		if (activeChar.isInStoreMode())
		{
			if (PetDataTable.isPetControlItem(item))
			{
				activeChar.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_A_PRIVATE_STORE);
			}
			else
			{
				activeChar.sendPacket(SystemMsg.YOU_MAY_NOT_USE_ITEMS_IN_A_PRIVATE_STORE_OR_PRIVATE_WORK_SHOP);
			}
			return;
		}

		if (activeChar.isFishing() && (itemId < 6535 || itemId > 6540))
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
			return;
		}

		/*
		 * if (activeChar._event != null && activeChar._event.getName().equalsIgnoreCase("Capture The Flag") && (itemId == 13560 || itemId == 13561))
		 * {
		 * activeChar.sendMessage(new CustomMessage("l2f.gameserver.network.l2.c2s.UseItem.NotUseIsEvents", activeChar));
		 * return;
		 * }
		 */

		if (ArrayUtils.contains(Config.ITEM_USE_LIST_ID, itemId) && !Config.ITEM_USE_IS_COMBAT_FLAG && (activeChar.getPvpFlag() != 0 || activeChar.isInDuel() || activeChar.isInCombat()))
		{
			activeChar.sendMessage(new CustomMessage("l2f.gameserver.network.l2.c2s.UseItem.NotUseIsFlag", activeChar));
			return;
		}

		/*
		 * if (ArrayUtils.contains(Config.ITEM_USE_LIST_ID, itemId) && !Config.ITEM_USE_IS_EVENTS && Events.onAction(activeChar, activeChar, true))
		 * {
		 * activeChar.sendMessage(new CustomMessage("l2f.gameserver.network.l2.c2s.UseItem.NotUseIsEvents", activeChar));
		 * return;
		 * }
		 */

		if (ArrayUtils.contains(Config.ITEM_USE_LIST_ID, itemId) && !Config.ITEM_USE_IS_ATTACK && activeChar.isAttackingNow())
		{
			activeChar.sendMessage(new CustomMessage("l2f.gameserver.network.l2.c2s.UseItem.NotUseIsFlag", activeChar));
			return;
		}

		if (activeChar.isInAwayingMode())
		{
			activeChar.sendMessage(new CustomMessage("Away.ActionFailed", activeChar, new Object[0]));
			return;
		}

		if (activeChar.getInventory().isLockedItem(item))
		{
			return;
		}

		if (item.getTemplate().isForPet())
		{
			activeChar.sendPacket(SystemMsg.YOU_MAY_NOT_EQUIP_A_PET_ITEM);
			return;
		}

		// Baby Buffalo Improved
		if (Config.ALT_IMPROVED_PETS_LIMITED_USE && activeChar.isMageClass() && item.getItemId() == 10311)
		{
			activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
			return;
		}

		// Improved Baby Kookaburra
		if (Config.ALT_IMPROVED_PETS_LIMITED_USE && !activeChar.isMageClass() && item.getItemId() == 10313)
		{
			activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
			return;
		}

		if (activeChar.isOutOfControl())
		{
			activeChar.sendActionFailed();
			return;
		}

		if ((item.getItemId() == 3936 || item.getItemId() == 1540) && activeChar.getTeam() != TeamType.NONE)
		{
			activeChar.sendMessage("Cannot use that during Event!");
			return;
		}

		// Synerge - Support for auto potions like toogles. There is no support for right click on other items than soulshots, so we use ctrl
		if (_ctrlPressed && activeChar.getObjectId() == item.getOwnerId() && Util.arrayContains(AUTO_POTIONS_IDS, item.getItemId()))
		{
			if (activeChar.getAutoPotions().contains(item.getItemId()))
			{
				activeChar.removeAutoPotion(item.getItemId());
				activeChar.sendPacket(new ExAutoSoulShot(item.getItemId(), false));
				activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED).addString(item.getName()));
			}
			else
			{
				activeChar.addAutoPotion(item.getItemId());
				activeChar.sendPacket(new ExAutoSoulShot(item.getItemId(), true));
				activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(item.getName()));
			}
			return;
		}

		if (activeChar.isSharedGroupDisabled(item.getTemplate().getReuseGroup()))
		{
			activeChar.sendReuseMessage(item);
			return;
		}

		if (!item.getTemplate().testCondition(activeChar, item))
		{
			return;
		}

		if (activeChar.getObjectId() == item.getOwnerId())
		{
			if (!activeChar.getPermissions().canUseItem(item, _ctrlPressed, true))
			{
				return;
			}

			boolean success = item.getTemplate().getHandler().useItem(activeChar, item, _ctrlPressed);
			if (success)
			{
				long nextTimeUse = item.getTemplate().getReuseType().next(item);
				if (nextTimeUse > System.currentTimeMillis())
				{
					TimeStamp timeStamp = new TimeStamp(item.getItemId(), nextTimeUse, item.getTemplate().getReuseDelay());
					activeChar.addSharedGroupReuse(item.getTemplate().getReuseGroup(), timeStamp);

					if (item.getTemplate().getReuseDelay() > 0)
					{
						activeChar.sendPacket(new ExUseSharedGroupItem(item.getTemplate().getDisplayReuseGroup(), timeStamp));
					}
				}
			}
		}
		else // Support for GM Alt+G inventory item use
		{
			Player owner = World.getPlayer(item.getOwnerId());
			if (owner == null)
			{
				return;
			}

			boolean success = item.getTemplate().getHandler().useItem(owner, item, _ctrlPressed);
			if (success)
			{
				long nextTimeUse = item.getTemplate().getReuseType().next(item);
				if (nextTimeUse > System.currentTimeMillis())
				{
					TimeStamp timeStamp = new TimeStamp(item.getItemId(), nextTimeUse, item.getTemplate().getReuseDelay());
					owner.addSharedGroupReuse(item.getTemplate().getReuseGroup(), timeStamp);

					if (item.getTemplate().getReuseDelay() > 0)
					{
						owner.sendPacket(new ExUseSharedGroupItem(item.getTemplate().getDisplayReuseGroup(), timeStamp));
					}
				}
			}

			// Update Inventory
			ItemInstance[] items = owner.getInventory().getItems();
			int questSize = 0;
			for (ItemInstance i : items)
			{
				if (i.getTemplate().isQuest())
				{
					questSize++;
				}
			}

			activeChar.sendPacket(new GMViewItemList(owner, items, items.length - questSize));
			activeChar.sendPacket(new ExGMViewQuestItemList(owner, items, questSize));
			activeChar.sendPacket(new GMHennaInfo(owner));
		}
	}
}