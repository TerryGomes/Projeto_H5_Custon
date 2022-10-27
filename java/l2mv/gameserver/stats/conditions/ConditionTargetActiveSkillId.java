package l2mv.gameserver.stats.conditions;

import l2mv.gameserver.model.Skill;
import l2mv.gameserver.stats.Env;

public class ConditionTargetActiveSkillId extends Condition
{

	private final int _skillId;
	private final int _skillLevel;

	public ConditionTargetActiveSkillId(int skillId)
	{
		_skillId = skillId;
		_skillLevel = -1;
	}

	public ConditionTargetActiveSkillId(int skillId, int skillLevel)
	{
		_skillId = skillId;
		_skillLevel = skillLevel;
	}

	@Override
	public boolean testImpl(Env env)
	{
		for (Skill sk : env.target.getAllSkills())
		{
			if (sk != null)
			{
				if (sk.getId() == _skillId)
				{
					if (_skillLevel == -1 || _skillLevel <= sk.getLevel())
					{
						return true;
					}
				}
			}
		}
		return false;
	}
}
