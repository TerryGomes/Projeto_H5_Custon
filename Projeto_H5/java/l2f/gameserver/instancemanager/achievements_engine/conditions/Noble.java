package l2f.gameserver.instancemanager.achievements_engine.conditions;

import l2f.gameserver.instancemanager.achievements_engine.base.Condition;
import l2f.gameserver.model.Player;

public class Noble extends Condition
{
	public Noble(Object value)
	{
		super(value);
		setName("Noble");
	}

	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
		{
			return false;
		}

		if (player.isNoble())
		{
			return true;
		}

		return false;
	}
}