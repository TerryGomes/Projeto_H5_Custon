package l2f.gameserver.instancemanager.achievements_engine.conditions;

import l2f.gameserver.instancemanager.achievements_engine.base.Condition;
import l2f.gameserver.model.Player;

public class Crp extends Condition
{
	public Crp(Object value)
	{
		super(value);
		setName("Clan Reputation");
	}

	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
		{
			return false;
		}

		if (player.getClan() != null)
		{
			int val = Integer.parseInt(getValue().toString());

			if (player.getClan().getReputationScore() >= val)
			{
				return true;
			}
		}
		return false;
	}
}