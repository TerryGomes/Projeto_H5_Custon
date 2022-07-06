package l2mv.gameserver.skills.skillclasses;

import java.util.List;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.stats.Stats;
import l2mv.gameserver.templates.StatsSet;

public class Balance extends Skill
{
	public Balance(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		double summaryCurrentHp = 0;
		int summaryMaximumHp = 0;

		for (Creature target : targets)
		{
			if (target != null)
			{
				if (target.isAlikeDead())
				{
					continue;
				}
				summaryCurrentHp += target.getCurrentHp();
				summaryMaximumHp += target.getMaxHp();
			}
		}

		double percent = summaryCurrentHp / summaryMaximumHp;

		for (Creature target : targets)
		{
			if (target != null)
			{
				if (target.isAlikeDead())
				{
					continue;
				}

				double hp = target.getMaxHp() * percent;
				if (hp > target.getCurrentHp())
				{
					// увеличение HP, не выше лимита
					double limit = target.calcStat(Stats.HP_LIMIT, null, null) * target.getMaxHp() / 100.;
					if (target.getCurrentHp() < limit) // не "подрезаем" HP под лимит если больше
					{
						target.setCurrentHp(Math.min(hp, limit), false);
					}
				}
				else
				{
					// уменьшение HP, не ниже 1.01 для предотвращения "ложной смерти" на олимпе/дуэли
					target.setCurrentHp(Math.max(1.01, hp), false);
				}

				getEffects(activeChar, target, getActivateRate() > 0, false);
			}
		}

		if (isSSPossible())
		{
			activeChar.unChargeShots(isMagic());
		}
	}
}
