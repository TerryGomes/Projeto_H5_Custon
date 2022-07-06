package l2mv.gameserver.skills.effects;

import l2mv.gameserver.model.Effect;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.stats.Env;

/*    */
public class EffectDummy extends Effect
{
	public EffectDummy(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectDummy(Effect effect)
	{
		super(effect);
	}

	@Override
	public void onStart()
	{
		Player target = (Player) getEffected();
		if (target.getTransformation() == 303)
		{
			return;
		}
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
		return false;
	}
}