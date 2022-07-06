package l2mv.gameserver.skills.skillclasses;

import java.util.List;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.stats.Formulas;
import l2mv.gameserver.stats.Stats;
import l2mv.gameserver.templates.StatsSet;

public class ManaDam extends Skill
{
	public ManaDam(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		int sps = 0;
		if (isSSPossible())
		{
			sps = activeChar.getChargedSpiritShot();
		}

		for (Creature target : targets)
		{
			if (target != null)
			{
				if (target.isDead())
				{
					continue;
				}

				double mpBefore = target.getCurrentMp();

				double mAtk = activeChar.getMAtk(target, this);
				if (sps == 2)
				{
					mAtk *= 4;
				}
				else if (sps == 1)
				{
					mAtk *= 2;
				}

				double mDef = target.getMDef(activeChar, this);
				if (mDef < 1.)
				{
					mDef = 1.;
				}

				double damage = Math.sqrt(mAtk) * getPower() * (target.getMaxMp() / 97) / mDef;

				boolean crit = Formulas.calcMCrit(activeChar.getMagicCriticalRate(target, this));
				if (crit)
				{
					activeChar.sendPacket(SystemMsg.MAGIC_CRITICAL_HIT);
					damage *= activeChar.calcStat(Stats.MCRITICAL_DAMAGE, activeChar.isPlayable() && target.isPlayable() ? 2.5 : 3., target, this);
				}
				target.reduceCurrentMp(damage, activeChar);
				target.sendMessage(activeChar.getName() + " has stolen " + (int) (mpBefore - target.getCurrentMp()) + " MP from you!");
				activeChar.sendMessage("You have stolen " + (int) (mpBefore - target.getCurrentMp()) + " MP from " + target.getName());

				getEffects(activeChar, target, getActivateRate() > 0, false);
			}
		}

		if (isSSPossible())
		{
			activeChar.unChargeShots(isMagic());
		}
	}
}