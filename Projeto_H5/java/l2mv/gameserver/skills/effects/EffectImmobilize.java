package l2mv.gameserver.skills.effects;

import l2mv.gameserver.model.Effect;
import l2mv.gameserver.stats.Env;

public final class EffectImmobilize extends Effect
{
	public EffectImmobilize(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectImmobilize(Effect effect)
	{
		super(effect);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		_effected.startImmobilized();
		_effected.stopMove();
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopImmobilized();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
