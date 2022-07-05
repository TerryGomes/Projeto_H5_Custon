package l2f.gameserver.skills.effects;

import l2f.gameserver.model.Effect;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.stats.Env;
import l2f.gameserver.stats.Stats;

public class EffectManaHealPercent extends Effect
{
	private final boolean _ignoreMpEff;

	public EffectManaHealPercent(Env env, EffectTemplate template)
	{
		super(env, template);
		_ignoreMpEff = template.getParam().getBool("ignoreMpEff", true);
	}

	public EffectManaHealPercent(Effect effect)
	{
		super(effect);
		_ignoreMpEff = getTemplate().getParam().getBool("ignoreMpEff", true);
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

		double mp = calc() * _effected.getMaxMp() / 100.;
		double newMp = mp * (!_ignoreMpEff ? _effected.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100., _effector, getSkill()) : 100.) / 100.;
		double addToMp = Math.max(0, Math.min(newMp, _effected.calcStat(Stats.MP_LIMIT, null, null) * _effected.getMaxMp() / 100. - _effected.getCurrentMp()));

		_effected.sendPacket(new SystemMessage2(SystemMsg.S1_MP_HAS_BEEN_RESTORED).addInteger(Math.round(addToMp)));

		if (addToMp > 0)
		{
			_effected.setCurrentMp(addToMp + _effected.getCurrentMp());
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}