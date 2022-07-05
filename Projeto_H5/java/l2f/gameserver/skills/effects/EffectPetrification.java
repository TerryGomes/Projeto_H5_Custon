package l2f.gameserver.skills.effects;

import l2f.gameserver.model.Effect;
import l2f.gameserver.stats.Env;

public final class EffectPetrification extends Effect
{
	public EffectPetrification(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectPetrification(Effect effect)
	{
		super(effect);
	}

	@Override
	public boolean checkCondition()
	{
		if (_effected.isParalyzeImmune())
		{
			return false;
		}
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		_effected.startParalyzed();
		_effected.startDebuffImmunity();
		_effected.startBuffImmunity();
		_effected.startDamageBlocked();
		_effected.abortAttack(true, true);
		_effected.abortCast(true, true);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopParalyzed();
		_effected.stopDebuffImmunity();
		_effected.stopBuffImmunity();
		_effected.stopDamageBlocked();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}