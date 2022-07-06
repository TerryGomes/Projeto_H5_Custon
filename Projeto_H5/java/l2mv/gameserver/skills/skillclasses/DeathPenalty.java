package l2mv.gameserver.skills.skillclasses;

import java.util.List;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.templates.StatsSet;

public class DeathPenalty extends Skill
{
	public DeathPenalty(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		// Chaotic characters can't use scrolls of recovery
		if (activeChar.getKarma() > 0 && !Config.ALT_DEATH_PENALTY_C5_CHAOTIC_RECOVERY)
		{
			activeChar.sendActionFailed();
			return false;
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for (Creature target : targets)
		{
			if (target != null)
			{
				if (!target.isPlayer())
				{
					continue;
				}
				((Player) target).getDeathPenalty().reduceLevel();
			}
		}
	}
}