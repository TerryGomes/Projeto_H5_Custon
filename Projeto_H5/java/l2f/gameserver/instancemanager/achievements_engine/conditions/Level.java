package l2f.gameserver.instancemanager.achievements_engine.conditions;

import l2f.gameserver.instancemanager.achievements_engine.base.Condition;
import l2f.gameserver.model.Player;

public class Level extends Condition
{
	public Level(Object value)
	{
		super(value);
		setName("Level");
	}

	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
		{
			return false;
		}
		int val = Integer.parseInt(getValue().toString());

		if (player.getLevel() >= val)
		{
			return true;
		}
		return false;
	}
}