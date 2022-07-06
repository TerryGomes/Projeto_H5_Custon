package l2mv.gameserver.skills.effects;

import l2mv.gameserver.model.Effect;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.stats.Env;

public class EffectUnAggro extends Effect
{
	public EffectUnAggro(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectUnAggro(Effect effect)
	{
		super(effect);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if (_effected.isNpc())
		{
			((NpcInstance) _effected).setUnAggred(true);
		}
	}

	@Override
	public void onExit()
	{
		super.onExit();
		if (_effected.isNpc())
		{
			((NpcInstance) _effected).setUnAggred(false);
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}