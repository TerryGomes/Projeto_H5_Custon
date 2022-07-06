package l2mv.gameserver.skills.effects;

import l2mv.gameserver.model.Effect;
import l2mv.gameserver.skills.skillclasses.NegateStats;
import l2mv.gameserver.stats.Env;

public class EffectBlockStat extends Effect
{
	public EffectBlockStat(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectBlockStat(Effect effect)
	{
		super(effect);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		_effected.addBlockStats(((NegateStats) _skill).getNegateStats());
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.removeBlockStats(((NegateStats) _skill).getNegateStats());
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}