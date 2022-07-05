package l2f.gameserver.skills.effects;

import l2f.gameserver.model.Effect;
import l2f.gameserver.stats.Env;

public class EffectNegateMusic extends Effect
{
	public EffectNegateMusic(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectNegateMusic(Effect effect)
	{
		super(effect);
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public void onExit()
	{
		super.onExit();
	}

	@Override
	public boolean onActionTime()
	{
		for (Effect e : _effected.getEffectList().getAllEffects())
		{
			if (e.getSkill().isMusic())
			{
				e.exit();
			}
		}
		return false;
	}
}