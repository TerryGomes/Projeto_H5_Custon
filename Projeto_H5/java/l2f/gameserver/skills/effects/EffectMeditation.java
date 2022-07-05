package l2f.gameserver.skills.effects;

import l2f.gameserver.model.Effect;
import l2f.gameserver.stats.Env;

public final class EffectMeditation extends Effect
{
	public EffectMeditation(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectMeditation(Effect effect)
	{
		super(effect);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		_effected.startParalyzed();
		_effected.setMeditated(true);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopParalyzed();
		_effected.setMeditated(false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}