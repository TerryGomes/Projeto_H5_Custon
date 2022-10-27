package l2mv.gameserver.skills.effects;

import l2mv.gameserver.model.Effect;
import l2mv.gameserver.stats.Env;

public final class EffectBuff extends Effect
{
	public EffectBuff(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectBuff(Effect effect)
	{
		super(effect);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// Prims - Hardcoded buff immunity for Day of Doom debuff (Prevents from receiving buff)
		if (getSkill().getId() == 5145)
		{
			_effected.startBuffImmunity();
		}
	}

	@Override
	public void onExit()
	{
		super.onExit();

		// Prims - Hardcoded buff immunity for Day of Doom debuff (Prevents from receiving buff)
		if (getSkill().getId() == 5145)
		{
			_effected.stopBuffImmunity();
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}