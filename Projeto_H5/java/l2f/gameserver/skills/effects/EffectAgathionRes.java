package l2f.gameserver.skills.effects;

import l2f.gameserver.model.Effect;
import l2f.gameserver.stats.Env;

public final class EffectAgathionRes extends Effect
{
	public EffectAgathionRes(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectAgathionRes(Effect effect)
	{
		super(effect);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		getEffected().setIsBlessedByNoblesse(true);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		getEffected().setIsBlessedByNoblesse(false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}