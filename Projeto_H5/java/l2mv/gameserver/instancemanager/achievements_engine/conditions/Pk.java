package l2mv.gameserver.instancemanager.achievements_engine.conditions;

import l2mv.gameserver.instancemanager.achievements_engine.base.Condition;
import l2mv.gameserver.model.Player;

public class Pk extends Condition
{
	public Pk(Object value)
	{
		super(value);
		setName("PK Count");
	}

	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
		{
			return false;
		}

		int val = Integer.parseInt(getValue().toString());

		if (player.getPkKills() >= val)
		{
			return true;
		}

		return false;
	}
}