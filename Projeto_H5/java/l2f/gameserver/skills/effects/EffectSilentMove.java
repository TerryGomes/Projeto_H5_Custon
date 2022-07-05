package l2f.gameserver.skills.effects;

import l2f.gameserver.model.Effect;
import l2f.gameserver.model.Playable;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.stats.Env;

public final class EffectSilentMove extends Effect
{
	public EffectSilentMove(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if (_effected.isPlayable())
		{
			((Playable) _effected).startSilentMoving();
		}
	}

	@Override
	public void onExit()
	{
		super.onExit();
		if (_effected.isPlayable())
		{
			((Playable) _effected).stopSilentMoving();
		}
	}

	@Override
	public boolean onActionTime()
	{
		if (_effected.isDead() || !getSkill().isToggle())
		{
			return false;
		}

		double manaDam = calc();
		if (manaDam > _effected.getCurrentMp())
		{
			_effected.sendPacket(SystemMsg.NOT_ENOUGH_MP);
			_effected.sendPacket(new SystemMessage2(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(getSkill().getId(), getSkill().getDisplayLevel()));
			return false;
		}

		_effected.reduceCurrentMp(manaDam, null);
		return true;
	}
}