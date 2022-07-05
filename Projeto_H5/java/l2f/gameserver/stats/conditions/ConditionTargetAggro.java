package l2f.gameserver.stats.conditions;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.MonsterInstance;
import l2f.gameserver.stats.Env;

public class ConditionTargetAggro extends Condition
{
	private final boolean _isAggro;

	public ConditionTargetAggro(boolean isAggro)
	{
		_isAggro = isAggro;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		Creature target = env.target;
		if (target == null)
		{
			return false;
		}
		if (target.isMonster())
		{
			return ((MonsterInstance) target).isAggressive() == _isAggro;
		}
		if (target.isPlayer())
		{
			return target.getKarma() > 0;
		}
		return false;
	}
}
