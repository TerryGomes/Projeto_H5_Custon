package l2mv.gameserver.skills.skillclasses;

import java.util.List;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.ai.CtrlEvent;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.instances.MonsterInstance;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.stats.Formulas;
import l2mv.gameserver.stats.Formulas.AttackInfo;
import l2mv.gameserver.templates.StatsSet;

public class Spoil extends Skill
{
	public Spoil(StatsSet set)
	{
		super(set);
	}

//    @Override
//    public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
//    {
//        if (target == null)
//        {
//            return false;
//        }
//
//        if (!target.isMonster() || target.isDead())
//        {
//            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
//            return false;
//        }
//
//        if (((MonsterInstance) target).isSpoiled())
//        {
//            activeChar.sendPacket(SystemMsg.IT_HAS_ALREADY_BEEN_SPOILED);
//            return false;
//        }
//        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
//    }

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		if (!activeChar.isPlayer())
		{
			return;
		}

		int ss = isSSPossible() ? (isMagic() ? activeChar.getChargedSpiritShot() : activeChar.getChargedSoulShot() ? 2 : 0) : 0;
		if (ss > 0 && getPower() > 0)
		{
			activeChar.unChargeShots(false);
		}

		for (Creature target : targets)
		{
			if (target != null && !target.isDead())
			{
				if (target.isMonster())
				{
					if (isSpoilUse(target))
					{
						if (((MonsterInstance) target).isSpoiled())
						{
							activeChar.sendPacket(SystemMsg.IT_HAS_ALREADY_BEEN_SPOILED);
						}
						else
						{
							MonsterInstance monster = (MonsterInstance) target;
							boolean success;
							if (!Config.ALT_SPOIL_FORMULA)
							{
								int monsterLevel = monster.getLevel();
								int modifier = Math.abs(monsterLevel - activeChar.getLevel());
								double rateOfSpoil = Config.BASE_SPOIL_RATE;
								if (modifier > 8)
								{
									rateOfSpoil = rateOfSpoil - rateOfSpoil * (modifier - 8) * 9 / 100;
								}

								rateOfSpoil = rateOfSpoil * getMagicLevel() / monsterLevel;
								if (rateOfSpoil < Config.MINIMUM_SPOIL_RATE)
								{
									rateOfSpoil = Config.MINIMUM_SPOIL_RATE;
								}
								else if (rateOfSpoil > 99.)
								{
									rateOfSpoil = 99.;
								}

								if (((Player) activeChar).isGM())
								{
									activeChar.sendMessage(new CustomMessage("l2mv.gameserver.skills.skillclasses.Spoil.Chance", (Player) activeChar).addNumber((long) rateOfSpoil));
								}
								success = Rnd.chance(rateOfSpoil);

							}
							else
							{
								success = Formulas.calcSkillSuccess(activeChar, target, this, getActivateRate());
							}
							if (success && monster.setSpoiled(activeChar.getPlayer()))
							{
								activeChar.sendPacket(SystemMsg.THE_SPOIL_CONDITION_HAS_BEEN_ACTIVATED);
							}
							else
							{
								activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_HAS_FAILED).addSkillName(_id, getDisplayLevel()));
							}
						}
					}
					else
					{
						activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_HAS_FAILED).addSkillName(_id, getDisplayLevel()));
					}
				}

				if (getPower() > 0)
				{
					double damage;
					if (isMagic())
					{
						damage = Formulas.calcMagicDam(activeChar, target, this, ss, false);
					}
					else
					{
						AttackInfo info = Formulas.calcPhysDam(activeChar, target, this, false, false, ss > 0, false);
						damage = info.damage;

						if (info.lethal_dmg > 0)
						{
							target.reduceCurrentHp(info.lethal_dmg, activeChar, this, true, true, false, false, false, false, false);
						}
					}

					target.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, false, true);
					target.doCounterAttack(this, activeChar, false);
				}

				getEffects(activeChar, target, false, false);

				target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, activeChar, Math.max(_effectPoint, 1));
			}
		}
	}

	private boolean isSpoilUse(Creature target)
	{
		if (getLevel() == 1 && target.getLevel() > 22 && getId() == 254)
		{
			return false;
		}
		return true;
	}
}