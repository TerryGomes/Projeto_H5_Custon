package l2mv.gameserver.skills.effects;

import l2mv.gameserver.model.Effect;
import l2mv.gameserver.stats.Env;

public class EffectCharmOfCourage extends Effect
{
	public EffectCharmOfCourage(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectCharmOfCourage(Effect effect)
	{
		super(effect);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if (_effected.isPlayer())
		{
			_effected.getPlayer().setCharmOfCourage(true);
		}
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.getPlayer().setCharmOfCourage(false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}