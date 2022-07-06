package l2mv.gameserver.stats.conditions;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Effect;
import l2mv.gameserver.skills.EffectType;
import l2mv.gameserver.stats.Env;

public class ConditionPlayerHasBuff extends Condition
{
	private final EffectType _effectType;
	private final int _level;

	public ConditionPlayerHasBuff(EffectType effectType, int level)
	{
		_effectType = effectType;
		_level = level;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		Creature character = env.character;
		if (character == null)
		{
			return false;
		}
		Effect effect = character.getEffectList().getEffectByType(_effectType);
		if (effect == null)
		{
			return false;
		}
		if (_level == -1 || effect.getSkill().getLevel() >= _level)
		{
			return true;
		}
		return false;
	}
}