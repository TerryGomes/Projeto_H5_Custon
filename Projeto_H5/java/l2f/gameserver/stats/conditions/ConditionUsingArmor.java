package l2f.gameserver.stats.conditions;

import l2f.gameserver.model.Player;
import l2f.gameserver.stats.Env;
import l2f.gameserver.templates.item.ArmorTemplate.ArmorType;

public class ConditionUsingArmor extends Condition
{
	private final ArmorType _armor;

	public ConditionUsingArmor(ArmorType armor)
	{
		_armor = armor;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if (env.character.isPlayer() && ((Player) env.character).isWearingArmor(_armor))
		{
			return true;
		}

		return false;
	}
}
