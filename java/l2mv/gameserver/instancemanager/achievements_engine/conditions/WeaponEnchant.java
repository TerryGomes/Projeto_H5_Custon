package l2mv.gameserver.instancemanager.achievements_engine.conditions;

import l2mv.gameserver.instancemanager.achievements_engine.base.Condition;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.Inventory;
import l2mv.gameserver.model.items.ItemInstance;

public class WeaponEnchant extends Condition
{
	public WeaponEnchant(Object value)
	{
		super(value);
		setName("Weapon Enchant");
	}

	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
		{
			return false;
		}

		int val = Integer.parseInt(getValue().toString());

		ItemInstance weapon = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);

		if (weapon != null)
		{
			if (weapon.getEnchantLevel() >= val)
			{
				return true;
			}
		}

		return false;
	}
}