package l2mv.gameserver.network.clientpackets;

import java.util.List;

import l2mv.commons.dao.JdbcEntityState;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.EnchantItemHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.actor.instances.player.Bonus;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.instances.WarehouseInstance;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.PcInventory;
import l2mv.gameserver.network.serverpackets.EnchantResult;
import l2mv.gameserver.network.serverpackets.InventoryUpdate;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.network.serverpackets.SystemMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.templates.item.support.EnchantScroll;
import l2mv.gameserver.utils.ItemActionLog;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.ItemStateLog;
import l2mv.gameserver.utils.Log;

public class RequestEnchantItem extends L2GameClientPacket
{
	private int _objectId, _catalystObjId;

	@Override
	protected void readImpl()
	{
		this._objectId = this.readD();
		this._catalystObjId = this.readD();
	}

	public static void showEnchantAnimation(Player player, int enchantLevel)
	{
		enchantLevel = Math.min(enchantLevel, 20);
		final int skillId = 23096 + enchantLevel;
		final MagicSkillUse msu = new MagicSkillUse(player, player, skillId, 1, 1, 1);
		player.broadcastPacket(msu);
	}

	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		player.isntAfk();

		if (player.isActionsDisabled() || player.isBlocked() || player.isInTrade())
		{
			player.setEnchantScroll(null);
			player.sendActionFailed();
			return;
		}

		if (player.isSitting())
		{
			player.setEnchantScroll(null);
			player.sendPacket(EnchantResult.CANCEL);
			player.sendMessage("You can't enchant while sitting.");
			player.sendActionFailed();
			return;
		}

		if (player.isInStoreMode())
		{
			player.setEnchantScroll(null);
			player.sendPacket(EnchantResult.CANCEL);
			player.sendPacket(SystemMsg.YOU_CANNOT_ENCHANT_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			player.sendActionFailed();
			return;
		}

		final List<NpcInstance> wh = player.getAroundNpc(200, 200);

		for (NpcInstance warehouse : wh)
		{
			if (warehouse instanceof WarehouseInstance)
			{
				player.sendMessage("You can't enchant near warehouse.");
				return;
			}
		}

		PcInventory inventory = player.getInventory();
		inventory.writeLock();
		try
		{
			ItemInstance item = inventory.getItemByObjectId(this._objectId);
			ItemInstance catalyst = this._catalystObjId > 0 ? inventory.getItemByObjectId(this._catalystObjId) : null;
			ItemInstance scroll = player.getEnchantScroll();

			if (item == null || scroll == null)
			{
				player.sendActionFailed();
				return;
			}

			EnchantScroll enchantScroll = EnchantItemHolder.getInstance().getEnchantScroll(scroll.getItemId());
			if (enchantScroll == null)
			{
				doEnchantOld(player, item, scroll, catalyst);
				return;
			}

			if (enchantScroll.getMaxEnchant() != -1 && item.getEnchantLevel() >= enchantScroll.getMaxEnchant())
			{
				player.sendPacket(EnchantResult.CANCEL);
				player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
				player.sendActionFailed();
				return;
			}

			if (enchantScroll.getItems().size() > 0)
			{
				if (!enchantScroll.getItems().contains(item.getItemId()))
				{
					player.sendPacket(EnchantResult.CANCEL);
					player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
					player.sendActionFailed();
					return;
				}
			}
			else if (!enchantScroll.getGrades().contains(item.getCrystalType()))
			{
				player.sendPacket(EnchantResult.CANCEL);
				player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
				player.sendActionFailed();
				return;
			}

			// Synerge - Max enchant for olf t shirt
			if (!item.canBeEnchanted(false) || !player.getPermissions().canEnchantItem(item, scroll, catalyst, true) || (item.getItemId() == 21580 && item.getEnchantLevel() >= Config.ENCHANT_MAX_OLF_T_SHIRT))
			{
				player.sendPacket(EnchantResult.CANCEL);
				player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
				player.sendActionFailed();
				return;
			}

			if (!inventory.destroyItem(scroll, 1L, "EnchantingItem") || catalyst != null && !inventory.destroyItem(catalyst, 1L, "EnchantingItem"))
			{
				player.sendPacket(EnchantResult.CANCEL);
				player.sendActionFailed();
				return;
			}

			boolean equipped = false;

			if (equipped = item.isEquipped())
			{
				inventory.unEquipItem(item);
			}

			int safeEnchantLevel = item.getTemplate().getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR ? 4 : 3;

			int chance = enchantScroll.getChance();

			if (item.getEnchantLevel() < safeEnchantLevel)
			{
				chance = 100;
			}

			// Olf's T-Shirt Custom Enchant Rates
			if (Config.OLF_TSHIRT_CUSTOM_ENABLED && item.getItemId() == 21580 && (enchantScroll.getItemId() == 21581 || enchantScroll.getItemId() == 21582))
			{
				chance = (item.getEnchantLevel() >= Config.ENCHANT_OLF_TSHIRT_CHANCES.size()) ? 10 : Config.ENCHANT_OLF_TSHIRT_CHANCES.get(item.getEnchantLevel()); // if item enchant
																																									// lvl is more than
																																									// +10 than chance is
																																									// 10
			}

			if (Rnd.chance(chance))
			{
				boolean isBlessedScroll = ItemFunctions.isBlessedEnchantScroll(enchantScroll.getItemId());
				boolean isCrystalScroll = ItemFunctions.isCrystallEnchantScroll(enchantScroll.getItemId());

				// success
				if (isBlessedScroll)
				{
					player.getCounters().enchantBlessedSucceeded++;
				}
				else if (!isBlessedScroll && !isCrystalScroll)
				{
					player.getCounters().enchantNormalSucceeded++;
				}

				item.setEnchantLevel(item.getEnchantLevel() + 1);
				Log.logItemActions(new ItemActionLog(ItemStateLog.ADD, "EnchantSuccess", player, item, 1L));
				item.setJdbcState(JdbcEntityState.UPDATED);
				item.update();

				if (equipped)
				{
					inventory.equipItem(item);
				}

				if (item.getEnchantLevel() > player.getCounters().highestEnchant)
				{
					player.getCounters().highestEnchant = item.getEnchantLevel();
				}

				player.sendPacket(new InventoryUpdate().addModifiedItem(item));

				player.sendPacket(EnchantResult.SUCESS);

				if (enchantScroll.isHasVisualEffect() && item.getEnchantLevel() > 3)
				{
					showEnchantAnimation(player, item.getEnchantLevel());
				}

				// Synerge - Add a enchant succesful to the stats
//				if (chance < 100)
//					player.addPlayerStats(Ranking.STAT_TOP_ENCHANTS_SUCCEED);
			}
			else
			{
				switch (enchantScroll.getResultType())
				{
				case CRYSTALS:
					if (item.isEquipped())
					{
						player.sendDisarmMessage(item);
					}

					if (!inventory.destroyItem(item, 1L, "EnchantFail"))
					{
						player.sendActionFailed();
						return;
					}

					int crystalId = item.getCrystalType().cry;
					if (crystalId > 0 && item.getTemplate().getCrystalCount() > 0)
					{
						int crystalAmount = (int) (item.getTemplate().getCrystalCount() * 0.87);

						if (item.getEnchantLevel() > 3)
						{
							crystalAmount += item.getTemplate().getCrystalCount() * 0.25 * (item.getEnchantLevel() - 3);
						}

						if (crystalAmount < 1)
						{
							crystalAmount = 1;
						}

						player.sendPacket(new EnchantResult(1, crystalId, crystalAmount));
						ItemFunctions.addItem(player, crystalId, crystalAmount, true, "EnchantFailCrystals");
					}
					else
					{
						player.sendPacket(EnchantResult.FAILED_NO_CRYSTALS);
					}

					if (enchantScroll.isHasVisualEffect())
					{
						showEnchantAnimation(player, 0);
					}
					break;
				case DROP_ENCHANT:
					item.setEnchantLevel(0);
					item.setJdbcState(JdbcEntityState.UPDATED);
					item.update();

					if (equipped)
					{
						inventory.equipItem(item);
					}

					player.sendPacket(new InventoryUpdate().addModifiedItem(item));
					player.sendPacket(SystemMsg.THE_BLESSED_ENCHANT_FAILED);
					player.sendPacket(EnchantResult.BLESSED_FAILED);
					showEnchantAnimation(player, 0);
					break;
				case NOTHING:
					player.sendPacket(EnchantResult.ANCIENT_FAILED);
					showEnchantAnimation(player, 0);
					break;
				}
			}
		}
		finally
		{
			inventory.writeUnlock();

			player.setEnchantScroll(null);
			player.updateStats();
		}
	}

	private static void doEnchantOld(Player player, ItemInstance item, ItemInstance scroll, ItemInstance catalyst)
	{
		PcInventory inventory = player.getInventory();

		if (!ItemFunctions.checkCatalyst(item, catalyst))
		{
			catalyst = null;
		}

		if (!item.canBeEnchanted(true))
		{
			player.sendPacket(EnchantResult.CANCEL);
			player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
			player.sendActionFailed();
			return;
		}

		int crystalId = ItemFunctions.getEnchantCrystalId(item, scroll, catalyst);

		if (crystalId == -1)
		{
			player.sendPacket(EnchantResult.CANCEL);
			player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
			player.sendActionFailed();
			return;
		}

		int scrollId = scroll.getItemId();

		if (scrollId == 13540 && item.getItemId() != 13539)
		{
			player.sendPacket(EnchantResult.CANCEL);
			player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
			player.sendActionFailed();
			return;
		}

		// ольф 21580(21581/21582)
		if ((scrollId == 21581 || scrollId == 21582) && item.getItemId() != 21580)
		{
			player.sendPacket(EnchantResult.CANCEL);
			player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
			player.sendActionFailed();
			return;
		}

		// ольф 21580(21581/21582)
		if ((scrollId != 21581 || scrollId != 21582) && item.getItemId() == 21580)
		{
			player.sendPacket(EnchantResult.CANCEL);
			player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
			player.sendActionFailed();
			return;
		}

		if (ItemFunctions.isDestructionWpnEnchantScroll(scrollId) && item.getEnchantLevel() >= 15 || ItemFunctions.isDestructionArmEnchantScroll(scrollId) && item.getEnchantLevel() >= 6)
		{
			player.sendPacket(EnchantResult.CANCEL);
			player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
			player.sendActionFailed();
			return;
		}

		final boolean isDivineScroll = scroll.getName().contains("Divine Armor Enchant Crystal") || scroll.getName().contains("Divine Weapon Enchant Crystal");

		int itemType = item.getTemplate().getType2();
		boolean fail = false;

		switch (item.getItemId())
		{
		// Yogi Staff
		case 13539:
			if (item.getEnchantLevel() >= Config.ENCHANT_MAX_MASTER_YOGI_STAFF)
			{
				fail = true;
			}
			break;
		// Olf T Shirt
		case 21580:
			if (item.getEnchantLevel() < Config.ENCHANT_MAX_OLF_T_SHIRT)
			{
				fail = false;
			}
			else
			{
				fail = true;
			}
			break;
		default:
		{
			switch (itemType)
			{
			case ItemTemplate.TYPE2_WEAPON:
				if ((isDivineScroll && Config.ENCHANT_MAX_DIVINE_SCROLL_WEAPON > 0 && item.getEnchantLevel() >= Config.ENCHANT_MAX_DIVINE_SCROLL_WEAPON) || (!isDivineScroll && Config.ENCHANT_MAX_WEAPON > 0 && item.getEnchantLevel() >= Config.ENCHANT_MAX_WEAPON))
				{
					fail = true;
				}
				break;
			case ItemTemplate.TYPE2_SHIELD_ARMOR:
				if ((isDivineScroll && Config.ENCHANT_MAX_DIVINE_SCROLL_ARMOR > 0 && item.getEnchantLevel() >= Config.ENCHANT_MAX_DIVINE_SCROLL_ARMOR) || (!isDivineScroll && Config.ENCHANT_MAX_ARMOR > 0 && item.getEnchantLevel() >= Config.ENCHANT_MAX_ARMOR))
				{
					fail = true;
				}
				break;
			case ItemTemplate.TYPE2_ACCESSORY:
				if ((isDivineScroll && Config.ENCHANT_MAX_DIVINE_SCROLL_JEWELRY > 0 && item.getEnchantLevel() >= Config.ENCHANT_MAX_DIVINE_SCROLL_JEWELRY) || (!isDivineScroll && Config.ENCHANT_MAX_JEWELRY > 0 && item.getEnchantLevel() >= Config.ENCHANT_MAX_JEWELRY))
				{
					fail = true;
				}
				break;
			default:
				fail = true;
				break;
			}
			break;
		}
		}

		if (!inventory.destroyItem(scroll, 1L, "Enchanting") || catalyst != null && !inventory.destroyItem(catalyst, 1L, "Enchanting"))
		{
			player.sendPacket(EnchantResult.CANCEL);
			player.sendActionFailed();
			return;
		}

		if (fail)
		{
			player.sendPacket(EnchantResult.CANCEL);
			player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
			player.sendActionFailed();
			return;
		}

		int safeEnchantLevel = item.getTemplate().getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR ? Config.SAFE_ENCHANT_FULL_BODY : Config.SAFE_ENCHANT_COMMON;

		double chance;
		if (item.getEnchantLevel() < safeEnchantLevel)
		{
			chance = 100;
		}
		else
		{
			switch (itemType)
			{
			case ItemTemplate.TYPE2_WEAPON:
				if (Config.USE_ALT_ENCHANT)
				{
					if (ItemFunctions.isCrystallEnchantScroll(scrollId))
					{
						chance = item.getEnchantLevel() > Config.ENCHANT_WEAPON_FIGHT_CRYSTAL.size() ? Config.ENCHANT_WEAPON_FIGHT_CRYSTAL.get(Config.ENCHANT_WEAPON_FIGHT_CRYSTAL.size() - 1) : Config.ENCHANT_WEAPON_FIGHT_CRYSTAL.get(item.getEnchantLevel());
					}
					else if (ItemFunctions.isBlessedEnchantScroll(scrollId))
					{
						chance = item.getEnchantLevel() > Config.ENCHANT_WEAPON_FIGHT_BLESSED.size() ? Config.ENCHANT_WEAPON_FIGHT_BLESSED.get(Config.ENCHANT_WEAPON_FIGHT_BLESSED.size() - 1) : Config.ENCHANT_WEAPON_FIGHT_BLESSED.get(item.getEnchantLevel());
					}
					else
					{
						chance = item.getEnchantLevel() > Config.ENCHANT_WEAPON_FIGHT.size() ? Config.ENCHANT_WEAPON_FIGHT.get(Config.ENCHANT_WEAPON_FIGHT.size() - 1) : Config.ENCHANT_WEAPON_FIGHT.get(item.getEnchantLevel());
					}
				}
				else if ((Config.SERVICES_RATE_TYPE != Bonus.NO_BONUS && player.getNetConnection().getBonus() > 0) && Config.USE_ALT_ENCHANT_PA)
				{
					if (ItemFunctions.isCrystallEnchantScroll(scrollId))
					{
						chance = item.getEnchantLevel() > Config.ENCHANT_WEAPON_FIGHT_CRYSTAL_PA.size() ? Config.ENCHANT_WEAPON_FIGHT_CRYSTAL_PA.get(Config.ENCHANT_WEAPON_FIGHT_CRYSTAL_PA.size() - 1) : Config.ENCHANT_WEAPON_FIGHT_CRYSTAL_PA.get(item.getEnchantLevel());
					}
					else if (ItemFunctions.isBlessedEnchantScroll(scrollId))
					{
						chance = item.getEnchantLevel() > Config.ENCHANT_WEAPON_FIGHT_BLESSED_PA.size() ? Config.ENCHANT_WEAPON_FIGHT_BLESSED_PA.get(Config.ENCHANT_WEAPON_FIGHT_BLESSED_PA.size() - 1) : Config.ENCHANT_WEAPON_FIGHT_BLESSED_PA.get(item.getEnchantLevel());
					}
					else
					{
						chance = item.getEnchantLevel() > Config.ENCHANT_WEAPON_FIGHT_PA.size() ? Config.ENCHANT_WEAPON_FIGHT_PA.get(Config.ENCHANT_WEAPON_FIGHT_PA.size() - 1) : Config.ENCHANT_WEAPON_FIGHT_PA.get(item.getEnchantLevel());
					}
				}
				else if (Config.SERVICES_RATE_TYPE != Bonus.NO_BONUS && player.getNetConnection().getBonus() > 0)
				{
					if (ItemFunctions.isBlessedEnchantScroll(scrollId))
					{
						chance = Config.ENCHANT_CHANCE_WEAPON_BLESS_PA;
					}
					else
					{
						chance = ItemFunctions.isCrystallEnchantScroll(scrollId) ? Config.ENCHANT_CHANCE_CRYSTAL_WEAPON_PA : Config.ENCHANT_CHANCE_WEAPON_PA;
					}
				}
				else if (ItemFunctions.isBlessedEnchantScroll(scrollId))
				{
					chance = Config.ENCHANT_CHANCE_WEAPON_BLESS;
				}
				else
				{
					chance = ItemFunctions.isCrystallEnchantScroll(scrollId) ? Config.ENCHANT_CHANCE_CRYSTAL_WEAPON : Config.ENCHANT_CHANCE_WEAPON;
				}
				break;
			case ItemTemplate.TYPE2_SHIELD_ARMOR:
				if (Config.USE_ALT_ENCHANT)
				{
					if (ItemFunctions.isCrystallEnchantScroll(scrollId))
					{
						chance = item.getEnchantLevel() > Config.ENCHANT_ARMOR_CRYSTAL.size() ? Config.ENCHANT_ARMOR_CRYSTAL.get(Config.ENCHANT_ARMOR_CRYSTAL.size() - 1) : Config.ENCHANT_ARMOR_CRYSTAL.get(item.getEnchantLevel());
					}
					else if (ItemFunctions.isBlessedEnchantScroll(scrollId))
					{
						chance = item.getEnchantLevel() > Config.ENCHANT_ARMOR_BLESSED.size() ? Config.ENCHANT_ARMOR_BLESSED.get(Config.ENCHANT_ARMOR_BLESSED.size() - 1) : Config.ENCHANT_ARMOR_BLESSED.get(item.getEnchantLevel());
					}
					else
					{
						chance = item.getEnchantLevel() > Config.ENCHANT_ARMOR.size() ? Config.ENCHANT_ARMOR.get(Config.ENCHANT_ARMOR.size() - 1) : Config.ENCHANT_ARMOR.get(item.getEnchantLevel());
					}
				}
				else if ((Config.SERVICES_RATE_TYPE != Bonus.NO_BONUS && player.getNetConnection().getBonus() > 0) && Config.USE_ALT_ENCHANT_PA)
				{
					if (ItemFunctions.isCrystallEnchantScroll(scrollId))
					{
						chance = item.getEnchantLevel() > Config.ENCHANT_ARMOR_CRYSTAL_PA.size() ? Config.ENCHANT_ARMOR_CRYSTAL_PA.get(Config.ENCHANT_ARMOR_CRYSTAL_PA.size() - 1) : Config.ENCHANT_ARMOR_CRYSTAL_PA.get(item.getEnchantLevel());
					}
					else if (ItemFunctions.isBlessedEnchantScroll(scrollId))
					{
						chance = item.getEnchantLevel() > Config.ENCHANT_ARMOR_BLESSED_PA.size() ? Config.ENCHANT_ARMOR_BLESSED_PA.get(Config.ENCHANT_ARMOR_BLESSED_PA.size() - 1) : Config.ENCHANT_ARMOR_BLESSED_PA.get(item.getEnchantLevel());
					}
					else
					{
						chance = item.getEnchantLevel() > Config.ENCHANT_ARMOR_PA.size() ? Config.ENCHANT_ARMOR_PA.get(Config.ENCHANT_ARMOR_PA.size() - 1) : Config.ENCHANT_ARMOR_PA.get(item.getEnchantLevel());
					}
				}
				else if (Config.SERVICES_RATE_TYPE != Bonus.NO_BONUS && player.getNetConnection().getBonus() > 0)
				{
					if (ItemFunctions.isBlessedEnchantScroll(scrollId))
					{
						chance = Config.ENCHANT_CHANCE_ARMOR_BLESS_PA;
					}
					else
					{
						chance = ItemFunctions.isCrystallEnchantScroll(scrollId) ? Config.ENCHANT_CHANCE_CRYSTAL_ARMOR_PA : Config.ENCHANT_CHANCE_ARMOR_PA;
					}
				}
				else if (ItemFunctions.isBlessedEnchantScroll(scrollId))
				{
					chance = Config.ENCHANT_CHANCE_ARMOR_BLESS;
				}
				else
				{
					chance = ItemFunctions.isCrystallEnchantScroll(scrollId) ? Config.ENCHANT_CHANCE_CRYSTAL_ARMOR : Config.ENCHANT_CHANCE_ARMOR;
				}
				break;
			case ItemTemplate.TYPE2_ACCESSORY:
				if (Config.USE_ALT_ENCHANT)
				{
					if (ItemFunctions.isCrystallEnchantScroll(scrollId))
					{
						chance = item.getEnchantLevel() > Config.ENCHANT_ARMOR_JEWELRY_CRYSTAL.size() ? Config.ENCHANT_ARMOR_JEWELRY_CRYSTAL.get(Config.ENCHANT_ARMOR_JEWELRY_CRYSTAL.size() - 1) : Config.ENCHANT_ARMOR_JEWELRY_CRYSTAL.get(item.getEnchantLevel());
					}
					else if (ItemFunctions.isBlessedEnchantScroll(scrollId))
					{
						chance = item.getEnchantLevel() > Config.ENCHANT_ARMOR_JEWELRY_BLESSED.size() ? Config.ENCHANT_ARMOR_JEWELRY_BLESSED.get(Config.ENCHANT_ARMOR_JEWELRY_BLESSED.size() - 1) : Config.ENCHANT_ARMOR_JEWELRY_BLESSED.get(item.getEnchantLevel());
					}
					else
					{
						chance = item.getEnchantLevel() > Config.ENCHANT_ARMOR_JEWELRY.size() ? Config.ENCHANT_ARMOR_JEWELRY.get(Config.ENCHANT_ARMOR_JEWELRY.size() - 1) : Config.ENCHANT_ARMOR_JEWELRY.get(item.getEnchantLevel());
					}
				}
				else if ((Config.SERVICES_RATE_TYPE != Bonus.NO_BONUS && player.getNetConnection().getBonus() > 0) && Config.USE_ALT_ENCHANT_PA)
				{
					if (ItemFunctions.isCrystallEnchantScroll(scrollId))
					{
						chance = item.getEnchantLevel() > Config.ENCHANT_ARMOR_JEWELRY_CRYSTAL_PA.size() ? Config.ENCHANT_ARMOR_JEWELRY_CRYSTAL_PA.get(Config.ENCHANT_ARMOR_JEWELRY_CRYSTAL_PA.size() - 1) : Config.ENCHANT_ARMOR_JEWELRY_CRYSTAL_PA.get(item.getEnchantLevel());
					}
					else if (ItemFunctions.isBlessedEnchantScroll(scrollId))
					{
						chance = item.getEnchantLevel() > Config.ENCHANT_ARMOR_JEWELRY_BLESSED_PA.size() ? Config.ENCHANT_ARMOR_JEWELRY_BLESSED_PA.get(Config.ENCHANT_ARMOR_JEWELRY_BLESSED_PA.size() - 1) : Config.ENCHANT_ARMOR_JEWELRY_BLESSED_PA.get(item.getEnchantLevel());
					}
					else
					{
						chance = item.getEnchantLevel() > Config.ENCHANT_ARMOR_JEWELRY_PA.size() ? Config.ENCHANT_ARMOR_JEWELRY_PA.get(Config.ENCHANT_ARMOR_JEWELRY_PA.size() - 1) : Config.ENCHANT_ARMOR_JEWELRY_PA.get(item.getEnchantLevel());
					}
				}
				else if (Config.SERVICES_RATE_TYPE != Bonus.NO_BONUS && player.getNetConnection().getBonus() > 0)
				{
					if (ItemFunctions.isBlessedEnchantScroll(scrollId))
					{
						chance = Config.ENCHANT_CHANCE_ACCESSORY_BLESS_PA;
					}
					else
					{
						chance = ItemFunctions.isCrystallEnchantScroll(scrollId) ? Config.ENCHANT_CHANCE_CRYSTAL_ACCESSORY_PA : Config.ENCHANT_CHANCE_ACCESSORY_PA;
					}
				}
				else if (ItemFunctions.isBlessedEnchantScroll(scrollId))
				{
					chance = Config.ENCHANT_CHANCE_ACCESSORY_BLESS;
				}
				else
				{
					chance = ItemFunctions.isCrystallEnchantScroll(scrollId) ? Config.ENCHANT_CHANCE_CRYSTAL_ACCESSORY : Config.ENCHANT_CHANCE_ACCESSORY;
				}
				break;
			default:
				player.sendPacket(EnchantResult.CANCEL);
				player.sendActionFailed();
				return;
			}
		}

		if (ItemFunctions.isDivineEnchantScroll(scrollId))
		{ // Item Mall divine
			chance = 100;
		}
		else if (ItemFunctions.isItemMallEnchantScroll(scrollId)) // Item Mall normal/ancient
		{
			chance += 10;
		}

		if (catalyst != null)
		{
			chance += ItemFunctions.getCatalystPower(catalyst.getItemId());
		}

		if (scrollId == 13540)
		{
			chance = item.getEnchantLevel() < Config.SAFE_ENCHANT_MASTER_YOGI_STAFF ? 100 : Config.ENCHANT_CHANCE_MASTER_YOGI_STAFF;
		}
		else if (scrollId == 21581 || scrollId == 21582)
		{
			if (item.getEnchantLevel() < 9)
			{
				chance = item.getEnchantLevel() < 3 ? 100 : Config.ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF;
			}
			else
			{
				chance = 0;
			}
		}

		boolean equipped = false;

		if (equipped = item.isEquipped())
		{
			inventory.unEquipItem(item);
		}

		boolean success = false;
		boolean isBlessedScroll = ItemFunctions.isBlessedEnchantScroll(scrollId);
		boolean isCrystalScroll = ItemFunctions.isCrystallEnchantScroll(scrollId);
		if (Rnd.chance(chance))
		{
			if (isBlessedScroll)
			{
				player.getCounters().enchantBlessedSucceeded++;
			}
			else if (!isBlessedScroll && !isCrystalScroll)
			{
				player.getCounters().enchantNormalSucceeded++;
			}

			item.setEnchantLevel(item.getEnchantLevel() + 1);
			item.setJdbcState(JdbcEntityState.UPDATED);
			item.update();

			if (equipped)
			{
				inventory.equipItem(item);
			}

			player.sendPacket(new InventoryUpdate().addModifiedItem(item));

			player.sendPacket(EnchantResult.SUCESS);

			Log.logItemActions(new ItemActionLog(ItemStateLog.ADD, "EnchantSuccess", player, item, 1L));

			if (scrollId == 13540 && item.getEnchantLevel() > 3 || Config.SHOW_ENCHANT_EFFECT_RESULT)
			{
				showEnchantAnimation(player, item.getEnchantLevel());
			}
			success = true;
		}
		else if (ItemFunctions.isBlessedEnchantScroll(scrollId))
		{
			item.setEnchantLevel(Config.SAFE_ENCHANT_LVL);
			item.setJdbcState(JdbcEntityState.UPDATED);
			item.update();

			if (equipped)
			{
				inventory.equipItem(item);
			}

			player.sendPacket(new InventoryUpdate().addModifiedItem(item));
			player.sendPacket(SystemMsg.THE_BLESSED_ENCHANT_FAILED);
			player.sendPacket(EnchantResult.BLESSED_FAILED);
			showEnchantAnimation(player, 0);

			Log.logItemActions(new ItemActionLog(ItemStateLog.ADD, "EnchantBlessedFail", player, item, 1L));
			success = true;
		}
		else if (ItemFunctions.isAncientEnchantScroll(scrollId) || ItemFunctions.isDestructionWpnEnchantScroll(scrollId) || ItemFunctions.isDestructionArmEnchantScroll(scrollId))
		{
			player.sendPacket(EnchantResult.ANCIENT_FAILED);
			Log.logItemActions(new ItemActionLog(ItemStateLog.ADD, "EnchantDestructionFail", player, item, 1L));
		}
		else
		{
			if (item.isEquipped())
			{
				player.sendDisarmMessage(item);
			}

			if (!inventory.destroyItem(item, 1L, "EnchantFail"))
			{
				player.sendPacket(new SystemMessage(SystemMessage.THE_ENCHANTMENT_HAS_FAILED_YOUR_S1_HAS_BEEN_CRYSTALLIZED).addItemName(item.getItemId()));
				showEnchantAnimation(player, 0);
				player.sendActionFailed();
				return;
			}

			if (crystalId > 0 && item.getTemplate().getCrystalCount() > 0)
			{
				int crystalAmount = (int) (item.getTemplate().getCrystalCount() * 0.87);

				if (item.getEnchantLevel() > 3)
				{
					crystalAmount += item.getTemplate().getCrystalCount() * 0.25 * (item.getEnchantLevel() - 3);
				}

				if (crystalAmount < 1)
				{
					crystalAmount = 1;
				}

				player.sendPacket(new EnchantResult(1, crystalId, crystalAmount));
				ItemFunctions.addItem(player, crystalId, crystalAmount, true, "EnchantFailCrystals");
			}
			else
			{
				player.sendPacket(EnchantResult.FAILED_NO_CRYSTALS);
			}

			if (scrollId == 13540 || Config.SHOW_ENCHANT_EFFECT_RESULT)
			{
				showEnchantAnimation(player, 0);
			}
		}

		// Synerge - Item Enchant listener
		if (chance < 100)
		{
			player.getListeners().onEnchantFinish(item, success);
		}
	}
}