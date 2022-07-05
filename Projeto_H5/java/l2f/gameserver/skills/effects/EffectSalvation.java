package l2f.gameserver.skills.effects;

import l2f.gameserver.model.Effect;
import l2f.gameserver.stats.Env;

public final class EffectSalvation extends Effect
{
	public EffectSalvation(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectSalvation(Effect effect)
	{
		super(effect);
	}

	@Override
	public boolean checkCondition()
	{
		return getEffected().isPlayer() && super.checkCondition();
	}

	@Override
	public void onStart()
	{
		getEffected().setIsSalvation(true);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		getEffected().setIsSalvation(false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}