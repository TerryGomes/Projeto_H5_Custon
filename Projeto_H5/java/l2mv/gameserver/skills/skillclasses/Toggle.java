package l2mv.gameserver.skills.skillclasses;

import java.util.List;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.templates.StatsSet;

public class Toggle extends Skill
{
	public Toggle(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		if (activeChar.getEffectList().getEffectsBySkillId(_id) != null)
		{
			activeChar.getEffectList().stopEffect(_id);
			activeChar.sendActionFailed();
			return;
		}

		getEffects(activeChar, activeChar, getActivateRate() > 0, false);
	}
}
