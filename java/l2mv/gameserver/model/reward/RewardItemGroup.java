package l2mv.gameserver.model.reward;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import l2mv.gameserver.templates.item.ItemTemplate;

public enum RewardItemGroup
{
	WEAPON(1.0D), ARMOR(1.0D), ACCESSORY(1.0D), ARROW_BOLT(1.0D), MATERIAL(1.0D), POTION(1.0D), RECIPE(1.0D), SCROLL(1.0D), SPELLBOOK(1.0D), MONEY(1.0D), OTHER(1.0D);

	public static final RewardItemGroup[] VALUES = values();
	private static final Logger _log = LoggerFactory.getLogger(RewardItemGroup.class);
	private double _rateModifier;

	private RewardItemGroup(double rateModifier)
	{
		_rateModifier = rateModifier;
	}

	public double getRateModifier()
	{
		return _rateModifier;
	}

	public void setRateModifier(double val)
	{
		_rateModifier = val;
		_log.info("RewardItemGroup: " + toString() + " rate modifier = " + val);
	}

	public static double getRate(ItemTemplate item)
	{
		if (item.isWeapon())
		{
			return WEAPON.getRateModifier();
		}
		if (item.isArmor())
		{
			return ARMOR.getRateModifier();
		}
		if (item.isAccessory())
		{
			return ACCESSORY.getRateModifier();
		}
		if ((item.getItemType() == EtcItemType.ARROW) || (item.getItemType() == EtcItemType.BOLT))
		{
			return ARROW_BOLT.getRateModifier();
		}
		if (item.getItemType() == EtcItemType.MATERIAL)
		{
			return MATERIAL.getRateModifier();
		}
		if (item.getItemType() == EtcItemType.POTION)
		{
			return POTION.getRateModifier();
		}
		if (item.getItemType() == EtcItemType.RECIPE)
		{
			return RECIPE.getRateModifier();
		}
		if (item.getItemType() == EtcItemType.SCROLL)
		{
			return SCROLL.getRateModifier();
		}
		if (item.getItemType() == EtcItemType.SPELLBOOK)
		{
			return SPELLBOOK.getRateModifier();
		}
		if (item.getItemType() == EtcItemType.MONEY)
		{
			if (item.isAdena())
			{
				return 1.0D;
			}
			return MONEY.getRateModifier();
		}
		return OTHER.getRateModifier();
	}
}