package l2f.gameserver.skills.effects;

import l2f.gameserver.model.Effect;
import l2f.gameserver.model.Player;
import l2f.gameserver.stats.Env;

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