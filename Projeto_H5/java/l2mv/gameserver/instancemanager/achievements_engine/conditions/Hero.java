package l2mv.gameserver.instancemanager.achievements_engine.conditions;

import l2mv.gameserver.instancemanager.achievements_engine.base.Condition;
import l2mv.gameserver.model.Player;

public class Hero extends Condition
{
	public Hero(Object value)
	{
		super(value);
		setName("Hero");
	}

	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
		{
			return false;
		}

		if (player.isHero())
		{
			return true;
		}

		return false;
	}
}