package l2mv.gameserver.skills.effects;

import l2mv.gameserver.model.Effect;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.stats.Env;

public class EffectManaDamOverTime extends Effect
{
	public EffectManaDamOverTime(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectManaDamOverTime(Effect effect)
	{
		super(effect);
	}

	@Override
	public boolean onActionTime()
	{
		if (_effected.isDead())
		{
			return false;
		}

		double manaDam = calc();
		if (manaDam > _effected.getCurrentMp() && getSkill().isToggle())
		{
			_effected.sendPacket(SystemMsg.NOT_ENOUGH_MP);
			_effected.sendPacket(new SystemMessage2(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(getSkill().getId(), getSkill().getDisplayLevel()));
			return false;
		}

		_effected.reduceCurrentMp(manaDam, null);
		return true;
	}
}