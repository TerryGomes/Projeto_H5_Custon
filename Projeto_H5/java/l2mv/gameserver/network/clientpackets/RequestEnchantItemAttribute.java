package l2mv.gameserver.network.clientpackets;

import l2mv.commons.dao.JdbcEntityState;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.cache.Msg;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.Element;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.PcInventory;
import l2mv.gameserver.network.serverpackets.ActionFail;
import l2mv.gameserver.network.serverpackets.ExAttributeEnchantResult;
import l2mv.gameserver.network.serverpackets.InventoryUpdate;
import l2mv.gameserver.network.serverpackets.SystemMessage;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.utils.ItemFunctions;

/**
 * Time : 4 October
 * @author SYS, Synerge
 * Format: d
 */
public class RequestEnchantItemAttribute extends L2GameClientPacket
{
	private int _objectId;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (_objectId == -1)
		{
			activeChar.setEnchantScroll(null);
			activeChar.sendPacket(Msg.ELEMENTAL_POWER_ENCHANCER_USAGE_HAS_BEEN_CANCELLED);
			return;
		}

		if (activeChar.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.isInStoreMode())
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_ADD_ELEMENTAL_POWER_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP, ActionFail.STATIC);
			return;
		}

		if (activeChar.isInTrade())
		{
			activeChar.sendActionFailed();
			return;
		}

		PcInventory inventory = activeChar.getInventory();
		ItemInstance itemToEnchant = inventory.getItemByObjectId(_objectId);
		ItemInstance stone = activeChar.getEnchantScroll();
		activeChar.setEnchantScroll(null);

		if (itemToEnchant == null || stone == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		ItemTemplate item = itemToEnchant.getTemplate();

		if (!itemToEnchant.canBeEnchanted(true) || item.getCrystalType().cry < ItemTemplate.CRYSTAL_S || (itemToEnchant.getLocation() != ItemInstance.ItemLocation.INVENTORY && itemToEnchant.getLocation() != ItemInstance.ItemLocation.PAPERDOLL))
		{
			activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFail.STATIC);
			return;
		}

		if (itemToEnchant.isStackable() || (stone = inventory.getItemByObjectId(stone.getObjectId())) == null || !activeChar.getPermissions().canAttributeItem(itemToEnchant, stone, true))
		{
			activeChar.sendActionFailed();
			return;
		}

		Element element = ItemFunctions.getEnchantAttributeStoneElement(stone.getItemId(), itemToEnchant.isArmor());

		if (itemToEnchant.isArmor())
		{
			if (itemToEnchant.getAttributeElementValue(Element.getReverseElement(element), false) != 0)
			{
				activeChar.sendPacket(Msg.ANOTHER_ELEMENTAL_POWER_HAS_ALREADY_BEEN_ADDED_THIS_ELEMENTAL_POWER_CANNOT_BE_ADDED, ActionFail.STATIC);
				return;
			}
		}
		else if (itemToEnchant.isWeapon())
		{
			if (itemToEnchant.getAttributeElement() != Element.NONE && itemToEnchant.getAttributeElement() != element)
			{
				activeChar.sendPacket(Msg.ANOTHER_ELEMENTAL_POWER_HAS_ALREADY_BEEN_ADDED_THIS_ELEMENTAL_POWER_CANNOT_BE_ADDED, ActionFail.STATIC);
				return;
			}
		}
		else
		{
			activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFail.STATIC);
			return;
		}

		if (item.isUnderwear() || item.isCloak() || item.isBracelet() || item.isBelt() || !item.isAttributable())
		{
			activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFail.STATIC);
			return;
		}

		// Запрет на заточку чужих вещей, баг может вылезти на серверных лагах
		if (itemToEnchant.getOwnerId() != activeChar.getObjectId())
		{
			activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFail.STATIC);
			return;
		}

		final int currentValue = itemToEnchant.getAttributeElementValue(element, false);
		int maxValue = itemToEnchant.isWeapon() ? Config.ATT_MOD_MAX_WEAPON : Config.ATT_MOD_MAX_ARMOR;
		if (stone.getTemplate().isAttributeCrystal())
		{
			maxValue += itemToEnchant.isWeapon() ? Config.ATT_MOD_MAX_WEAPON : Config.ATT_MOD_MAX_ARMOR;
		}

		if (currentValue >= maxValue)
		{
			activeChar.sendPacket(Msg.ELEMENTAL_POWER_ENCHANCER_USAGE_HAS_BEEN_CANCELLED, ActionFail.STATIC);
			return;
		}

		if (!inventory.destroyItem(stone, 1L, "AttributeTry"))
		{
			activeChar.sendActionFailed();
			return;
		}

		// Synerge - Support for premium bonus on attributing items
		int premiumBonusChance = 0;
		if (activeChar.getNetConnection().getBonusExpire() > System.currentTimeMillis() / 1000L)
		{
			premiumBonusChance = activeChar.getBonus().getAttributeChance();
		}

		final boolean mustUseAllStones = activeChar.isEnchantAllAttribute();
		boolean success = Rnd.chance((stone.getTemplate().isAttributeCrystal() ? Config.ENCHANT_ATTRIBUTE_CRYSTAL_CHANCE : Config.ENCHANT_ATTRIBUTE_STONE_CHANCE) + premiumBonusChance);
		final boolean equipped = itemToEnchant.isEquipped();
		int usedStones = 0;
		int value = 0;

		// Go through all the stones available to make a massive attribute enchanting. Only if that was intented, otherwise it will be done 1 time
		while (true)
		{
			usedStones++;

			// Enchant
			if (success)
			{
				// Для оружия 1й камень дает +20 атрибута
				if (currentValue + value == 0 && itemToEnchant.isWeapon())
				{
					value += Config.ATT_MOD_WEAPON1;
				}
				else
				{
					value += itemToEnchant.isWeapon() ? Config.ATT_MOD_WEAPON : Config.ATT_MOD_ARMOR;
				}
			}

			// Only continue doing it if we must use all the stones

			// Dont go over the max value
			// Use the next stone, if we cant then we stop enchanting
			if (!mustUseAllStones || (currentValue + value >= maxValue) || !inventory.destroyItem(stone, 1L, "AttributeTry"))
			{
				break;
			}

			success = Rnd.chance((stone.getTemplate().isAttributeCrystal() ? Config.ENCHANT_ATTRIBUTE_CRYSTAL_CHANCE : Config.ENCHANT_ATTRIBUTE_STONE_CHANCE) + premiumBonusChance);
		}

		// Send messages, update status and equip item again
		if (value > 0)
		{
			// Unequip
			if (equipped)
			{
				activeChar.getInventory().isRefresh = true;
				activeChar.getInventory().unEquipItem(itemToEnchant);
			}

			// Update item
			itemToEnchant.setAttributeElement(element, currentValue + value);
			itemToEnchant.setJdbcState(JdbcEntityState.UPDATED);
			itemToEnchant.update();

			// Messages
			if (itemToEnchant.getEnchantLevel() == 0 && !mustUseAllStones)
			{
				SystemMessage sm = new SystemMessage(SystemMessage.S2_ELEMENTAL_POWER_HAS_BEEN_ADDED_SUCCESSFULLY_TO_S1);
				sm.addItemName(itemToEnchant.getItemId());
				sm.addItemName(stone.getItemId());
				activeChar.sendPacket(sm);
			}
			else if (!mustUseAllStones)
			{
				SystemMessage sm = new SystemMessage(SystemMessage.S3_ELEMENTAL_POWER_HAS_BEEN_ADDED_SUCCESSFULLY_TO__S1S2);
				sm.addNumber(itemToEnchant.getEnchantLevel());
				sm.addItemName(itemToEnchant.getItemId());
				sm.addItemName(stone.getItemId());
				activeChar.sendPacket(sm);
			}

			// Equip item
			if (equipped)
			{
				activeChar.getInventory().equipItem(itemToEnchant);
				activeChar.getInventory().isRefresh = false;
			}

			// Broadcast status
			activeChar.sendPacket(new InventoryUpdate().addModifiedItem(itemToEnchant));
			activeChar.sendPacket(new ExAttributeEnchantResult(value));

			// Special message if all stones were used
			if (mustUseAllStones)
			{
				activeChar.sendMessage("Result of enchanting process: " + usedStones + " Attribute Items used. +" + value + " Elemental Value");
			}
		}
		else // Special message if all stones were used
		if (mustUseAllStones)
		{
			activeChar.sendMessage("Result of enchanting process: " + usedStones + " Attribute Items used. +" + value + " Elemental Value");
		}
		else
		{
			activeChar.sendPacket(Msg.YOU_HAVE_FAILED_TO_ADD_ELEMENTAL_POWER);
		}

		activeChar.updateStats();
	}
}