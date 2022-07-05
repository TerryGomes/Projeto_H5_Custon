package l2f.gameserver.instancemanager.achievements_engine.conditions;

import l2f.gameserver.instancemanager.achievements_engine.base.Condition;
import l2f.gameserver.model.Player;

public class events extends Condition
{
	public events(Object value)
	{
		super(value);
		setName("Events played");
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

		// if (EventStats.getInstance().getEvents(player.getObjectId()) >= val)
		// return true;

		return false;
	}
}