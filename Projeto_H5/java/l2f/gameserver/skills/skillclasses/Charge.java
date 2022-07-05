package l2f.gameserver.skills.skillclasses;

import java.util.List;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.network.serverpackets.MagicSkillUse;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.stats.Formulas;
import l2f.gameserver.stats.Formulas.AttackInfo;
import l2f.gameserver.templates.StatsSet;

public class Charge extends Skill
{
	public static final int MAX_CHARGE = 8;

	private int _charges;
	private boolean _fullCharge;

	public Charge(StatsSet set)
	{
		super(set);
		_charges = set.getInteger("charges", getLevel());
		_fullCharge = set.getBool("fullCharge", false);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if (!activeChar.isPlayer())
		{
			return false;
		}

		Player player = (Player) activeChar;

		// Pebbles can juzat even if the charge is> 7, the rest only if the charge <skill level
		if (getPower() <= 0 && getId() != 2165 && player.getIncreasedForce() >= _charges)
		{
			activeChar.sendPacket(SystemMsg.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY);
			return false;
		}
		else if (getId() == 2165)
		{
			player.sendPacket(new MagicSkillUse(player, player, 2165, 1, 0, 0));
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		if (!activeChar.isPlayer())
		{
			return;
		}

		boolean ss = activeChar.getChargedSoulShot() && isSSPossible();
		if (ss && getTargetType() != SkillTargetType.TARGET_SELF)
		{
			activeChar.unChargeShots(false);
		}

		Creature realTarget;
		boolean reflected;

		for (Creature target : targets)
		{
			if (target.isDead() || target == activeChar)
			{
				continue;
			}

			reflected = target.checkReflectSkill(activeChar, this);
			realTarget = reflected ? activeChar : target;

			if (getPower() > 0) // If == 0 then the skill "disabled"
			{
				AttackInfo info = Formulas.calcPhysDam(activeChar, realTarget, this, false, false, ss, false);

				if (info.lethal_dmg > 0)
				{
					realTarget.reduceCurrentHp(info.lethal_dmg, activeChar, this, true, true, false, false, false, false, false);
				}

				realTarget.reduceCurrentHp(info.damage, activeChar, this, true, true, false, true, false, false, true);
				if (!reflected)
				{
					realTarget.doCounterAttack(this, activeChar, false);
				}
			}

			getEffects(activeChar, target, getActivateRate() > 0, false, reflected);
		}

		chargePlayer((Player) activeChar, getId());
	}

	public void chargePlayer(Player player, Integer skillId)
	{
		if (player.getIncreasedForce() >= _charges)
		{
			player.sendPacket(SystemMsg.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY_);
			return;
		}
		if (_fullCharge)
		{
			player.setIncreasedForce(_charges);
		}
		else
		{
			player.setIncreasedForce(player.getIncreasedForce() + 1);
		}
	}
}