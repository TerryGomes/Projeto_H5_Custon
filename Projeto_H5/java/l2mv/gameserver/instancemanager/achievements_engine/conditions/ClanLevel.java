package l2mv.gameserver.instancemanager.achievements_engine.conditions;

import l2mv.gameserver.instancemanager.achievements_engine.base.Condition;
import l2mv.gameserver.model.Player;

public class ClanLevel extends Condition
{
	public ClanLevel(Object value)
	{
		super(value);
		setName("Clan Level");
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

			if (player.getClan().getLevel() >= val)
			{
				return true;
			}
		}
		return false;
	}
}