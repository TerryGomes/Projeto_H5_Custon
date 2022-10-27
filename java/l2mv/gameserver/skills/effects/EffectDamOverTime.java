package l2mv.gameserver.skills.effects;

import l2mv.gameserver.model.Effect;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.stats.Env;
import l2mv.gameserver.stats.Stats;

public class EffectDamOverTime extends Effect
{
	// TODO уточнить уровни 1, 2, 9, 10, 11, 12
	private static int[] bleed = new int[]
	{
		12,
		17,
		25,
		34,
		44,
		54,
		62,
		67,
		72,
		77,
		82,
		87
	};
	private static int[] poison = new int[]
	{
		11,
		16,
		24,
		32,
		41,
		50,
		58,
		63,
		68,
		72,
		77,
		82
	};

	private boolean _percent;

	public EffectDamOverTime(Env env, EffectTemplate template)
	{
		super(env, template);
		_percent = getTemplate().getParam().getBool("percent", false);
	}

	public EffectDamOverTime(Effect effect)
	{
		super(effect);
		_percent = getTemplate().getParam().getBool("percent", false);
	}

	@Override
	public boolean onActionTime()
	{
		if (_effected.isDead())
		{
			return false;
		}

		double damage = calc();
		if (_percent)
		{
			damage = _effected.getMaxHp() * _template._value * 0.01;
		}
		if (damage < 2 && getStackOrder() != -1)
		{
			switch (getEffectType())
			{
			case Poison:
				damage = poison[getStackOrder() - 1] * getPeriod() / 1000;
				break;
			case Bleed:
				damage = bleed[getStackOrder() - 1] * getPeriod() / 1000;
				break;
			}
		}

		damage = _effector.calcStat(getSkill().isMagic() ? Stats.MAGIC_DAMAGE : Stats.PHYSICAL_DAMAGE, damage, _effected, getSkill());

		if (damage > _effected.getCurrentHp() - 1 && !_effected.isNpc())
		{
			if (!getSkill().isOffensive())
			{
				_effected.sendPacket(SystemMsg.NOT_ENOUGH_HP);
			}
			return false;
		}

		if (getSkill().getAbsorbPart() > 0)
		{
			_effector.setCurrentHp(getSkill().getAbsorbPart() * Math.min(_effected.getCurrentHp(), damage) + _effector.getCurrentHp(), false);
		}

		_effected.reduceCurrentHp(damage, _effector, getSkill(), !_effected.isNpc() && _effected != _effector, _effected != _effector, _effector.isNpc() || _effected == _effector, false, false, true, false);

		return true;
	}
}