package l2f.gameserver.skills.effects;

import l2f.gameserver.model.Effect;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.stats.Env;
import l2f.gameserver.stats.Stats;

public class EffectSummonHealPercent extends Effect
{
	private final boolean _ignoreHpEff;

	public EffectSummonHealPercent(Env env, EffectTemplate template)
	{
		super(env, template);
		_ignoreHpEff = template.getParam().getBool("ignoreHpEff", true);
	}

	public EffectSummonHealPercent(Effect effect)
	{
		super(effect);
		_ignoreHpEff = getTemplate().getParam().getBool("ignoreHpEff", true);
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