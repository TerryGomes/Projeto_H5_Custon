package l2mv.gameserver.stats.conditions;

import l2mv.gameserver.stats.Env;

public class ConditionPlayerOlympiad extends Condition
{
	private final boolean _value;

	public ConditionPlayerOlympiad(boolean v)
	{
		_value = v;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return env.character.isInOlympiadMode() == _value || (env.character.isPlayable() && env.character.getPlayer().isInFightClub() == _value);
	}
}