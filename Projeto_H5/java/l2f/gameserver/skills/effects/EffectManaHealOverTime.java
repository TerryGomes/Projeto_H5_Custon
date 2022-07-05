package l2f.gameserver.skills.effects;

import l2f.gameserver.model.Effect;
import l2f.gameserver.stats.Env;
import l2f.gameserver.stats.Stats;

public class EffectManaHealOverTime extends Effect
{
	private final boolean _ignoreMpEff;

	public EffectManaHealOverTime(Env env, EffectTemplate template)
	{
		super(env, template);
		_ignoreMpEff = template.getParam().getBool("ignoreMpEff", false);
	}

	public EffectManaHealOverTime(Effect effect)
	{
		super(effect);
		_ignoreMpEff = getTemplate().getParam().getBool("ignoreMpEff", false);
	}

	@Override
	public boolean onActionTime()
	{
		if (_effected.isHealBlocked())
		{
			return true;
		}

		double mp = calc();
		double newMp = mp * (!_ignoreMpEff ? _effected.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100., _effector, getSkill()) : 100.) / 100.;
		double addToMp = Math.max(0, Math.min(newMp, _effected.calcStat(Stats.MP_LIMIT, null, null) * _effected.getMaxMp() / 100. - _effected.getCurrentMp()));

		if (addToMp > 0)
		{
			_effected.setCurrentMp(_effected.getCurrentMp() + addToMp);
		}

		return true;
	}
}