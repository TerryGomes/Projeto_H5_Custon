package l2mv.gameserver.instancemanager.achievements_engine.conditions;

import l2mv.gameserver.instancemanager.achievements_engine.base.Condition;
import l2mv.gameserver.model.Player;

public class OnlineTime extends Condition
{
	public OnlineTime(Object value)
	{
		super(value);
		setName("Online Time");
	}

	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
		{
			return false;
		}

		int val = Integer.parseInt(getValue().toString());

		if (player.getOnlineTime() >= (val * 24 * 60 * 60 * 1000))
		{
			return true;
		}
		return false;
	}
}