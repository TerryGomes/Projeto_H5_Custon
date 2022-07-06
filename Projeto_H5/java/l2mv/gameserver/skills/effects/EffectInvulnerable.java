package l2mv.gameserver.skills.effects;

import l2mv.gameserver.model.Effect;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.Skill.SkillType;
import l2mv.gameserver.stats.Env;

public final class EffectInvulnerable extends Effect
{
	public EffectInvulnerable(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectInvulnerable(Effect effect)
	{
		super(effect);
	}

	@Override
	public boolean checkCondition()
	{
		if (_effected.isInvul())
		{
			return false;
		}
		Skill skill = _effected.getCastingSkill();
		if (skill != null && (skill.getSkillType() == SkillType.TAKECASTLE || skill.getSkillType() == SkillType.TAKEFORTRESS || skill.getSkillType() == SkillType.TAKEFLAG))
		{
			return false;
		}
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		_effected.startHealBlocked();
		_effected.setIsInvul(true);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopHealBlocked();
		_effected.setIsInvul(false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}