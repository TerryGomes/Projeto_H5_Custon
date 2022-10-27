package l2mv.gameserver.stats.funcs;

import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.stats.Env;
import l2mv.gameserver.stats.Stats;
import l2mv.gameserver.tables.EnchantHPBonusTable;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.templates.item.ItemType;
import l2mv.gameserver.templates.item.WeaponTemplate.WeaponType;

public class FuncEnchant extends Func
{
	public FuncEnchant(Stats stat, int order, Object owner, double value)
	{
		super(stat, order, owner);
	}

	@Override
	public void calc(Env env)
	{
		env.value += (double) valueToAdd(stat, (ItemInstance) owner);
	}

	public static int valueToAdd(Stats stat, ItemInstance item)
	{
		int enchant = item.getEnchantLevel();
		int overEnchant = Math.max(0, enchant - 3);
		switch (stat)
		{
		case SHIELD_DEFENCE:
		case MAGIC_DEFENCE:
		case POWER_DEFENCE:
		{
			return enchant + overEnchant * 2;
		}

		case MAX_HP:
		{
			return EnchantHPBonusTable.getInstance().getHPBonus(item);
		}

		case MAGIC_ATTACK:
		{
			switch (item.getTemplate().getCrystalType().cry)
			{
			case ItemTemplate.CRYSTAL_S:
				return 4 * (enchant + overEnchant);
			case ItemTemplate.CRYSTAL_A:
			case ItemTemplate.CRYSTAL_B:
			case ItemTemplate.CRYSTAL_C:
				return 3 * (enchant + overEnchant);
			case ItemTemplate.CRYSTAL_D:
			case ItemTemplate.CRYSTAL_NONE:
				return 2 * (enchant + overEnchant);
			default:
				return 0;
			}
		}

		case POWER_ATTACK:
		{
			ItemType itemType = item.getItemType();
			boolean isBow = itemType == WeaponType.BOW || itemType == WeaponType.CROSSBOW;
			boolean isSword = (itemType == WeaponType.DUALFIST || itemType == WeaponType.DUAL || itemType == WeaponType.BIGSWORD || itemType == WeaponType.SWORD || itemType == WeaponType.RAPIER || itemType == WeaponType.ANCIENTSWORD) && item.getTemplate().getBodyPart() == ItemTemplate.SLOT_LR_HAND;
			switch (item.getTemplate().getCrystalType().cry)
			{
			case ItemTemplate.CRYSTAL_S:
				if (isBow)
				{
					return 10 * (enchant + overEnchant);
				}
				else if (isSword)
				{
					return 6 * (enchant + overEnchant);
				}
				else
				{
					return 5 * (enchant + overEnchant);
				}
			case ItemTemplate.CRYSTAL_A:
				if (isBow)
				{
					return 8 * (enchant + overEnchant);
				}
				else if (isSword)
				{
					return 5 * (enchant + overEnchant);
				}
				else
				{
					return 4 * (enchant + overEnchant);
				}
			case ItemTemplate.CRYSTAL_B:
			case ItemTemplate.CRYSTAL_C:
				if (isBow)
				{
					return 6 * (enchant + overEnchant);
				}
				else if (isSword)
				{
					return 4 * (enchant + overEnchant);
				}
				else
				{
					return 3 * (enchant + overEnchant);
				}
			case ItemTemplate.CRYSTAL_D:
			case ItemTemplate.CRYSTAL_NONE:
				if (isBow)
				{
					return 4 * (enchant + overEnchant);
				}
				else
				{
					return 2 * (enchant + overEnchant);
				}
			}
		}
		}
		return 0;
	}
}