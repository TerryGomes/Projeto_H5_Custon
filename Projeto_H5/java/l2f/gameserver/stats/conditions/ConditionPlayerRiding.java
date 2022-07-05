package l2f.gameserver.stats.conditions;

import l2f.gameserver.model.Player;
import l2f.gameserver.stats.Env;

public class ConditionPlayerRiding extends Condition
{
	public enum CheckPlayerRiding
	{
		NONE, STRIDER, WYVERN
	}

	private final CheckPlayerRiding _riding;

	public ConditionPlayerRiding(CheckPlayerRiding riding)
	{
		_riding = riding;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if (!env.character.isPlayer())
		{
			return false;
		}
		if ((_riding == CheckPlayerRiding.STRIDER && ((Player) env.character).isRiding()) || (_riding == CheckPlayerRiding.WYVERN && ((Player) env.character).isFlying()))
		{
			return true;
		}
		if (_riding == CheckPlayerRiding.NONE && !((Player) env.character).isRiding() && !((Player) env.character).isFlying())
		{
			return true;
		}
		return false;
	}
}
