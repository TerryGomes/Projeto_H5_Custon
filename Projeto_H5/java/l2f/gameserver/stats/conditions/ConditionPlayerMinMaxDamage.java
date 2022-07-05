package l2f.gameserver.stats.conditions;

import l2f.gameserver.stats.Env;

public class ConditionPlayerMinMaxDamage extends Condition
{
	private final double _min;
	private final double _max;

	public ConditionPlayerMinMaxDamage(double min, double max)
	{
		_min = min;
		_max = max;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if ((_min > 0 && env.value < _min) || (_max > 0 && env.value > _max))
		{
			return false;
		}
		return true;
	}
}
