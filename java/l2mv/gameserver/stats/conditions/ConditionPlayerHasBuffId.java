package l2mv.gameserver.stats.conditions;

import java.util.List;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Effect;
import l2mv.gameserver.stats.Env;

public class ConditionPlayerHasBuffId extends Condition
{
	private final int _id;
	private final int _level;

	public ConditionPlayerHasBuffId(int id, int level)
	{
		_id = id;
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
		if (_level == -1)
		{
			return character.getEffectList().getEffectsBySkillId(_id) != null;
		}
		List<Effect> el = character.getEffectList().getEffectsBySkillId(_id);
		if (el == null)
		{
			return false;
		}
		for (Effect effect : el)
		{
			if (effect != null && effect.getSkill().getLevel() >= _level)
			{
				return true;
			}
		}
		return false;
	}
}