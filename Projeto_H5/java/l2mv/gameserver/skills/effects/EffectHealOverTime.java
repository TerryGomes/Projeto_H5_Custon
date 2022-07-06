package l2mv.gameserver.skills.effects;

import l2mv.gameserver.model.Effect;
import l2mv.gameserver.network.serverpackets.ExRegenMax;
import l2mv.gameserver.stats.Env;
import l2mv.gameserver.stats.Stats;

public class EffectHealOverTime extends Effect
{
	private final boolean _ignoreHpEff;

	public EffectHealOverTime(Env env, EffectTemplate template)
	{
		super(env, template);
		_ignoreHpEff = template.getParam().getBool("ignoreHpEff", false);
	}

	public EffectHealOverTime(Effect effect)
	{
		super(effect);
		_ignoreHpEff = getTemplate().getParam().getBool("ignoreHpEff", false);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if (getEffected().isPlayer() && getCount() > 0 && getPeriod() > 0)
		{
			getEffected().sendPacket(new ExRegenMax(calc(), (int) (getCount() * getPeriod() / 1000), Math.round(getPeriod() / 1000)));
		}
	}

	@Override
	public boolean onActionTime()
	{
		if (_effected.isHealBlocked())
		{
			return true;
		}

		double hp = calc();
		double newHp = hp * (!_ignoreHpEff ? _effected.calcStat(Stats.HEAL_EFFECTIVNESS, 100., _effector, getSkill()) : 100.) / 100.;
		double addToHp = Math.max(0, Math.min(newHp, _effected.calcStat(Stats.HP_LIMIT, null, null) * _effected.getMaxHp() / 100. - _effected.getCurrentHp()));

		if (addToHp > 0)
		{
			getEffected().setCurrentHp(_effected.getCurrentHp() + addToHp, false);
		}

		return true;
	}
}