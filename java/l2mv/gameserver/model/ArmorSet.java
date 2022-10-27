package l2mv.gameserver.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import l2mv.gameserver.model.items.Inventory;
import l2mv.gameserver.model.items.ItemInstance;

public final class ArmorSet
{
	private final int _set_id;
	private final List<Integer> _chest = new ArrayList<Integer>(1);
	private final List<Integer> _legs = new ArrayList<Integer>(1);
	private final List<Integer> _head = new ArrayList<Integer>(1);
	private final List<Integer> _gloves = new ArrayList<Integer>(1);
	private final List<Integer> _feet = new ArrayList<Integer>(1);
	private final List<Integer> _shield = new ArrayList<Integer>(1);
	private final Map<Integer, Integer> _skills = new HashMap<>(1);
	private final Map<Integer, Integer> _shieldSkills = new HashMap<>(1);
	private final Map<Integer, Integer> _enchant6skills = new HashMap<>(1);

	public ArmorSet(int set_id, String[] chest, String[] legs, String[] head, String[] gloves, String[] feet, String[] skills, String[] shield, String[] shield_skills, String[] enchant6skills)
	{
		_set_id = set_id;

		if (chest != null)
		{
			for (String chestId : chest)
			{
				_chest.add(Integer.parseInt(chestId));
			}
		}

		if (legs != null)
		{
			for (String legsId : legs)
			{
				_legs.add(Integer.parseInt(legsId));
			}
		}

		if (head != null)
		{
			for (String headId : head)
			{
				_head.add(Integer.parseInt(headId));
			}
		}

		if (gloves != null)
		{
			for (String glovesId : gloves)
			{
				_gloves.add(Integer.parseInt(glovesId));
			}
		}

		if (feet != null)
		{
			for (String feetId : feet)
			{
				_feet.add(Integer.parseInt(feetId));
			}
		}

		if (shield != null)
		{
			for (String shieldId : shield)
			{
				_shield.add(Integer.parseInt(shieldId));
			}
		}

		if (skills != null)
		{
			for (String skill : skills)
			{
				StringTokenizer st = new StringTokenizer(skill, "-");
				if (st.hasMoreTokens())
				{
					int skillId = Integer.parseInt(st.nextToken());
					int skillLvl = Integer.parseInt(st.nextToken());
					_skills.put(skillId, skillLvl);
				}
				// Adding constant 'set' skill
				_skills.put(3006, 1);
			}
		}

		if (shield_skills != null)
		{
			for (String skill : shield_skills)
			{
				StringTokenizer st = new StringTokenizer(skill, "-");
				if (st.hasMoreTokens())
				{
					int skillId = Integer.parseInt(st.nextToken());
					int skillLvl = Integer.parseInt(st.nextToken());
					_shieldSkills.put(skillId, skillLvl);
				}
			}
		}

		if (enchant6skills != null)
		{
			for (String skill : enchant6skills)
			{
				StringTokenizer st = new StringTokenizer(skill, "-");
				if (st.hasMoreTokens())
				{
					int skillId = Integer.parseInt(st.nextToken());
					int skillLvl = Integer.parseInt(st.nextToken());
					_enchant6skills.put(skillId, skillLvl);
				}
			}
		}
	}

	/**
	 * Checks if player have equipped all items from set (not checking shield)
	 * @param player whose inventory is being checked
	 * @return True if player equips whole set
	 */
	public boolean containAll(Player player)
	{
		Inventory inv = player.getInventory();

		ItemInstance chestItem = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
		ItemInstance legsItem = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
		ItemInstance headItem = inv.getPaperdollItem(Inventory.PAPERDOLL_HEAD);
		ItemInstance glovesItem = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
		ItemInstance feetItem = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);

		int chest = 0;
		int legs = 0;
		int head = 0;
		int gloves = 0;
		int feet = 0;

		if (chestItem != null)
		{
			legs = chestItem.getItemId();
		}
		if (legsItem != null)
		{
			legs = legsItem.getItemId();
		}
		if (headItem != null)
		{
			head = headItem.getItemId();
		}
		if (glovesItem != null)
		{
			gloves = glovesItem.getItemId();
		}
		if (feetItem != null)
		{
			feet = feetItem.getItemId();
		}

		return containAll(chest, legs, head, gloves, feet);
	}

	public boolean containAll(int chest, int legs, int head, int gloves, int feet)
	{
		if ((_chest.isEmpty() && !_chest.contains(chest)) || (!_legs.isEmpty() && !_legs.contains(legs)))
		{
			return false;
		}
		if ((!_head.isEmpty() && !_head.contains(head)) || (!_gloves.isEmpty() && !_gloves.contains(gloves)))
		{
			return false;
		}
		if (!_feet.isEmpty() && !_feet.contains(feet))
		{
			return false;
		}

		return true;
	}

	public boolean containItem(int slot, int itemId)
	{
		switch (slot)
		{
		case Inventory.PAPERDOLL_CHEST:
			return _chest.contains(itemId);
		case Inventory.PAPERDOLL_LEGS:
			return _legs.contains(itemId);
		case Inventory.PAPERDOLL_HEAD:
			return _head.contains(itemId);
		case Inventory.PAPERDOLL_GLOVES:
			return _gloves.contains(itemId);
		case Inventory.PAPERDOLL_FEET:
			return _feet.contains(itemId);
		default:
			return false;
		}
	}

	public int getSetById()
	{
		return _set_id;
	}

	public List<Integer> getChestItemIds()
	{
		return _chest;
	}

	public Map<Integer, Integer> getSkills()
	{
		return _skills;
	}

	public Map<Integer, Integer> getShieldSkills()
	{
		return _shieldSkills;
	}

	public Map<Integer, Integer> getEnchant6skills()
	{
		return _enchant6skills;
	}

	public boolean containShield(Player player)
	{
		Inventory inv = player.getInventory();

		ItemInstance shieldItem = inv.getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		if (shieldItem != null && _shield.contains(shieldItem.getItemId()))
		{
			return true;
		}

		return false;
	}

	public boolean containShield(int shield_id)
	{
		if (_shield.isEmpty())
		{
			return false;
		}

		return _shield.contains(shield_id);
	}

	/**
	 * Checks if all parts of set are enchanted to +6 or more
	 * @param player
	 * @return
	 */
	public boolean isEnchanted6(Player player)
	{
		// Player don't have full set
		if (!containAll(player))
		{
			return false;
		}

		Inventory inv = player.getInventory();

		ItemInstance chestItem = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
		ItemInstance legsItem = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
		ItemInstance headItem = inv.getPaperdollItem(Inventory.PAPERDOLL_HEAD);
		ItemInstance glovesItem = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
		ItemInstance feetItem = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);

		if ((!_chest.isEmpty() && chestItem.getEnchantLevel() < 6) || (!_legs.isEmpty() && legsItem.getEnchantLevel() < 6))
		{
			return false;
		}
		if ((!_gloves.isEmpty() && glovesItem.getEnchantLevel() < 6) || (!_head.isEmpty() && headItem.getEnchantLevel() < 6))
		{
			return false;
		}
		if (!_feet.isEmpty() && feetItem.getEnchantLevel() < 6)
		{
			return false;
		}

		return true;
	}

	/**
	 * Order:   0     1      2      3     4      5 <br>
	 * Order: CHEST, LEGS, GLOVES, HEAD, FEET, SHIELD
	 * @return the first items from the set. ItemId is 0 for missing set part.
	 */
	public int[] getPrimarySetItems()
	{
		int[] ret = new int[6];
		if (!_chest.isEmpty())
		{
			ret[0] = _chest.get(0);
		}
		if (!_legs.isEmpty())
		{
			ret[1] = _legs.get(0);
		}
		if (!_gloves.isEmpty())
		{
			ret[2] = _gloves.get(0);
		}
		if (!_head.isEmpty())
		{
			ret[3] = _head.get(0);
		}
		if (!_feet.isEmpty())
		{
			ret[4] = _feet.get(0);
		}
		if (!_shield.isEmpty())
		{
			ret[5] = _shield.get(0);
		}

		return ret;
	}

	/**
	 * Creates a set with the first set items.
	 * @param player
	 */
	public void createDummySet(Player player)
	{
		for (int itemId : getPrimarySetItems())
		{
			if (itemId > 0)
			{
				player.getInventory().addItem(itemId, 1, "ArmorSetDummy");
			}
		}
	}

	/**
	 * Equips all set items.
	 * @param player
	 */
	public void equipSet(Player player)
	{
		for (ItemInstance item : player.getInventory().getItems())
		{
			if (item.isEquipable() && (_chest.contains(item.getItemId()) || _legs.contains(item.getItemId()) || _gloves.contains(item.getItemId()) || _head.contains(item.getItemId()) || _feet.contains(item.getItemId()) || _shield.contains(item.getItemId())))
			{
				player.getInventory().equipItem(item);
			}
		}
	}
}