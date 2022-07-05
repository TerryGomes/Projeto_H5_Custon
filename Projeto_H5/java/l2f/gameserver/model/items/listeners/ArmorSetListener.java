package l2f.gameserver.model.items.listeners;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import l2f.gameserver.cache.Msg;
import l2f.gameserver.data.xml.holder.ArmorSetsHolder;
import l2f.gameserver.listener.inventory.OnEquipListener;
import l2f.gameserver.model.ArmorSet;
import l2f.gameserver.model.Playable;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.Inventory;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.SkillList;
import l2f.gameserver.tables.SkillTable;

public final class ArmorSetListener implements OnEquipListener
{
	private static final ArmorSetListener _instance = new ArmorSetListener();

	public static ArmorSetListener getInstance()
	{
		return _instance;
	}

	@Override
	public void onEquip(int slot, ItemInstance item, Playable actor)
	{
		if (!item.isEquipable())
		{
			return;
		}

		Player player = actor.getPlayer();

		// checks if player wears chest item
		ItemInstance chestItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
		if (chestItem == null)
		{
			return;
		}

		// checks if there is armorSet for chest item that player wears
		ArmorSet armorSet = ArmorSetsHolder.getInstance().getArmorSet(chestItem.getItemId());
		if (armorSet == null)
		{
			return;
		}

		boolean update = false;
		// checks if equipped item is part of set
		if (armorSet.containItem(slot, item.getItemId()))
		{
			if (armorSet.containAll(player))
			{
				Map<Integer, Integer> skills = armorSet.getSkills();
				for (Map.Entry<Integer, Integer> skill : skills.entrySet())
				{
					player.addSkill(SkillTable.getInstance().getInfo(skill.getKey().intValue(), skill.getValue().intValue()), false);
					update = true;
				}

				if (armorSet.containShield(player)) // has shield from set
				{
					skills = armorSet.getShieldSkills();
					for (Map.Entry<Integer, Integer> skill : skills.entrySet())
					{
						player.addSkill(SkillTable.getInstance().getInfo(skill.getKey().intValue(), skill.getValue().intValue()), false);
						update = true;
					}
				}
				if (armorSet.isEnchanted6(player)) // has all parts of set enchanted to 6 or more
				{
					skills = armorSet.getEnchant6skills();
					for (Map.Entry<Integer, Integer> skill : skills.entrySet())
					{
						player.addSkill(SkillTable.getInstance().getInfo(skill.getKey().intValue(), skill.getValue().intValue()), false);
						update = true;
					}
				}
			}
		}
		else if (armorSet.containShield(item.getItemId()))
		{
			if (armorSet.containAll(player))
			{
				Map<Integer, Integer> skills = armorSet.getShieldSkills();
				for (Map.Entry<Integer, Integer> skill : skills.entrySet())
				{
					player.addSkill(SkillTable.getInstance().getInfo(skill.getKey().intValue(), skill.getValue().intValue()), false);
					update = true;
				}
			}
		}

		if (update)
		{
			player.sendPacket(new SkillList(player));
			player.updateStats();

			// Synerge - If the player has the complete set then enable dressme visual ids
			if (player.getInventory().hasAllDressMeItemsEquipped())
			{
				player.getInventory().setMustShowDressMe(true);
				player.broadcastUserInfo(true);
			}
		}
	}

	@Override
	public void onUnequip(int slot, ItemInstance item, Playable actor)
	{
		if (!item.isEquipable())
		{
			return;
		}

		Player player = actor.getPlayer();

		boolean remove = false;
		Set<Integer> removeSkillId1 = null; // set skill
		Set<Integer> removeSkillId2 = null; // shield skill
		Set<Integer> removeSkillId3 = null; // enchant +6 skill

		if (slot == Inventory.PAPERDOLL_CHEST)
		{
			ArmorSet armorSet = ArmorSetsHolder.getInstance().getArmorSet(item.getItemId());
			if (armorSet == null)
			{
				return;
			}

			remove = true;
			removeSkillId1 = armorSet.getSkills().keySet();
			removeSkillId2 = armorSet.getShieldSkills().keySet();
			removeSkillId3 = armorSet.getEnchant6skills().keySet();

		}
		else
		{
			ItemInstance chestItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
			if (chestItem == null)
			{
				return;
			}

			ArmorSet armorSet = ArmorSetsHolder.getInstance().getArmorSet(chestItem.getItemId());
			if (armorSet == null)
			{
				return;
			}

			if (armorSet.containItem(slot, item.getItemId())) // removed part of set
			{
				remove = true;
				removeSkillId1 = armorSet.getSkills().keySet();
				removeSkillId2 = armorSet.getShieldSkills().keySet();
				removeSkillId3 = armorSet.getEnchant6skills().keySet();
			}
			else if (armorSet.containShield(item.getItemId())) // removed shield
			{
				remove = true;
				removeSkillId1 = Collections.emptySet();
				removeSkillId2 = armorSet.getShieldSkills().keySet();
				removeSkillId3 = Collections.emptySet();
			}
		}

		boolean update = false;
		if (remove)
		{
			for (Integer skillId : removeSkillId1)
			{
				player.removeSkill(skillId.intValue(), false);
				update = true;
			}
			for (Integer skillId : removeSkillId2)
			{
				player.removeSkill(skillId.intValue(), false);
				update = true;
			}
			for (Integer skillId : removeSkillId3)
			{
				player.removeSkill(skillId.intValue(), false);
				update = true;
			}

			// Synerge - If the player has no longer the complete set then disable dressme visual ids
			if (player.getInventory().mustShowDressMe())
			{
				player.getInventory().setMustShowDressMe(false);
				player.broadcastUserInfo(true);
			}
		}

		if (update)
		{
			if (!player.getInventory().isRefresh)
			{
				// При снятии вещей из состава S80 или S84 сета снимаем плащ
				if (!player.getOpenCloak() && player.getInventory().setPaperdollItem(Inventory.PAPERDOLL_BACK, null) != null)
				{
					player.sendPacket(Msg.THE_CLOAK_EQUIP_HAS_BEEN_REMOVED_BECAUSE_THE_ARMOR_SET_EQUIP_HAS_BEEN_REMOVED);
				}
			}

			player.sendPacket(new SkillList(player));
			player.updateStats();
		}
	}
}