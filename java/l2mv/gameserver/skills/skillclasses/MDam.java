package l2mv.gameserver.skills.skillclasses;

import java.util.List;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.stats.Formulas;
import l2mv.gameserver.templates.StatsSet;

public class MDam extends Skill
{
	public MDam(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		int sps = isSSPossible() ? (isMagic() ? activeChar.getChargedSpiritShot() : activeChar.getChargedSoulShot() ? 2 : 0) : 0;

		Creature realTarget;
		boolean reflected;

		for (Creature target : targets)
		{
			if (target != null)
			{
				if (target.isDead())
				{
					continue;
				}

				reflected = target.checkReflectSkill(activeChar, this);
				realTarget = reflected ? activeChar : target;

				double damage = Formulas.calcMagicDam(activeChar, realTarget, this, sps, false);
				if (damage >= 1)
				{
					realTarget.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, false, true);
				}

				getEffects(activeChar, target, getActivateRate() > 0, false, reflected);
			}
		}

		if (isSuicideAttack())
		{
			activeChar.doDie(null);
		}
		else if (isSSPossible())
		{
			activeChar.unChargeShots(isMagic());
		}
	}
}