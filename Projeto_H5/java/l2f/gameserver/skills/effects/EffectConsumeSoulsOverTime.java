package l2f.gameserver.skills.effects;

import l2f.gameserver.model.Effect;
import l2f.gameserver.stats.Env;

public class EffectConsumeSoulsOverTime extends Effect
{
	public EffectConsumeSoulsOverTime(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectConsumeSoulsOverTime(Effect effect)
	{
		super(effect);
	}

	@Override
	public boolean onActionTime()
	{
		if (_effected.isDead() || (_effected.getConsumedSouls() < 0))
		{
			return false;
		}

		int damage = (int) calc();

		if (_effected.getConsumedSouls() < damage)
		{
			_effected.setConsumedSouls(0, null);
		}
		else
		{
			_effected.setConsumedSouls(_effected.getConsumedSouls() - damage, null);
		}

		return true;
	}
}