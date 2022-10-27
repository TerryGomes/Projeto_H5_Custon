package l2mv.gameserver.instancemanager.achievements_engine.conditions;

import l2mv.gameserver.instancemanager.achievements_engine.base.Condition;
import l2mv.gameserver.model.Player;

public class eventKills extends Condition
{
	public eventKills(Object value)
	{
		super(value);
		setName("Event Kills");
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

		// if (EventStats.getInstance().getEventKills(player.getObjectId()) >= val)
		// return true;

		return false;
	}
}