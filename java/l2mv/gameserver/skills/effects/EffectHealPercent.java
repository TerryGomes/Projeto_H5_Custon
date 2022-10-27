package l2mv.gameserver.skills.effects;

import l2mv.gameserver.model.Effect;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.stats.Env;
import l2mv.gameserver.stats.Stats;

public class EffectHealPercent extends Effect
{
	private final boolean _ignoreHpEff;

	public EffectHealPercent(Env env, EffectTemplate template)
	{
		super(env, template);
		_ignoreHpEff = template.getParam().getBool("ignoreHpEff", true);
	}

	public EffectHealPercent(Effect effect)
	{
		super(effect);
		_ignoreHpEff = getTemplate().getParam().getBool("ignoreHpEff", true);
	}

	@Override
	public boolean checkCondition()
	{
		if (_effected.isHealBlocked())
		{
			return false;
		}
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if (_effected.isHealBlocked())
		{
			return;
		}

		double hp = calc() * _effected.getMaxHp() / 100.;
		double newHp = hp * (!_ignoreHpEff ? _effected.calcStat(Stats.HEAL_EFFECTIVNESS, 100., _effector, getSkill()) : 100.) / 100.;
		double addToHp = Math.max(0, Math.min(newHp, _effected.calcStat(Stats.HP_LIMIT, null, null) * _effected.getMaxHp() / 100. - _effected.getCurrentHp()));

		_effected.sendPacket(new SystemMessage2(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addInteger(Math.round(addToHp)));

		if (addToHp > 0)
		{
			_effected.setCurrentHp(addToHp + _effected.getCurrentHp(), false);
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}