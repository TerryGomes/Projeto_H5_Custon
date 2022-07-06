package l2mv.gameserver.skills.effects;

import l2mv.gameserver.model.Effect;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.stats.Env;

public class EffectMute extends Effect
{
	public EffectMute(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectMute(Effect effect)
	{
		super(effect);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if (!_effected.startMuted())
		{
			Skill castingSkill = _effected.getCastingSkill();
			if (castingSkill != null && castingSkill.isMagic())
			{
				_effected.abortCast(true, true);
			}
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
		_effected.stopMuted();
	}
}