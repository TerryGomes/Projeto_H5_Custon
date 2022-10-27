package l2mv.gameserver.skills.effects;

import l2mv.gameserver.model.Effect;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.stats.Env;

public final class EffectVitalityStop extends Effect
{
	public EffectVitalityStop(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectVitalityStop(Effect effect)
	{
		super(effect);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		Player player = _effected.getPlayer();
		player.VitalityStop(true);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		Player player = _effected.getPlayer();
		player.VitalityStop(false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}