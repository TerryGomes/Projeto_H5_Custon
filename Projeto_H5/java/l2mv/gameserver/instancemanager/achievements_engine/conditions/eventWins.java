package l2mv.gameserver.instancemanager.achievements_engine.conditions;

import l2mv.gameserver.instancemanager.achievements_engine.base.Condition;
import l2mv.gameserver.model.Player;

public class eventWins extends Condition
{
	public eventWins(Object value)
	{
		super(value);
		setName("Event Wins");
	}

	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
		{
			return false;
		}

		@SuppressWarnings("unused")
		int val = Integer.parseInt(getValue().toString());

		// if (EventStats.getInstance().getEventWins(player.getObjectId()) >= val)
		// return true;

		return false;
	}
}