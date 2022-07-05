package l2f.gameserver.skills.effects;

import l2f.gameserver.model.Effect;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.stats.Env;

public class EffectEnervation extends Effect
{
	public EffectEnervation(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectEnervation(Effect effect)
	{
		super(effect);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if (_effected.isNpc())
		{
			((NpcInstance) _effected).setParameter("DebuffIntention", 0.5);
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}

	@Override
	public void onExit()
	{
		super.onExit();
		if (_effected.isNpc())
		{
			((NpcInstance) _effected).setParameter("DebuffIntention", 1.);
		}
	}
}