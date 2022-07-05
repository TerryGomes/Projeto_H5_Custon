package l2f.gameserver.skills.effects;

import l2f.gameserver.model.Effect;
import l2f.gameserver.stats.Env;

public final class EffectSleep extends Effect
{
	public EffectSleep(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectSleep(Effect effect)
	{
		super(effect);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		_effected.startSleeping();
		_effected.abortAttack(true, true);
		_effected.abortCast(true, true);
		_effected.stopMove();
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopSleeping();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}