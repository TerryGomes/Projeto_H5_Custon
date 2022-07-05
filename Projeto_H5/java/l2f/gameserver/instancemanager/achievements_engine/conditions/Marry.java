package l2f.gameserver.instancemanager.achievements_engine.conditions;

import l2f.gameserver.instancemanager.achievements_engine.base.Condition;
import l2f.gameserver.model.Player;

public class Marry extends Condition
{
	public Marry(Object value)
	{
		super(value);
		setName("Married");
	}

	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
		{
			return false;
		}

		if (player.isMaried())
		{
			return true;
		}

		return false;
	}
}