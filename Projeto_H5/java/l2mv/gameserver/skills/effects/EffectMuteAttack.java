package l2mv.gameserver.skills.effects;

import l2mv.gameserver.model.Effect;
import l2mv.gameserver.stats.Env;

public class EffectMuteAttack extends Effect
{
	public EffectMuteAttack(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectMuteAttack(Effect effect)
	{
		super(effect);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if (!_effected.startAMuted())
		{
			_effected.abortCast(true, true);
			_effected.abortAttack(true, true);
		}
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopAMuted();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}