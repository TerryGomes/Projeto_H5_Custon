package l2f.gameserver.stats;

import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Effect;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.Skill.SkillType;
import l2f.gameserver.model.base.BaseStats;
import l2f.gameserver.model.base.ClassId;
import l2f.gameserver.model.base.Element;
import l2f.gameserver.model.base.SkillTrait;
import l2f.gameserver.model.instances.ReflectionBossInstance;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.skills.EffectType;
import l2f.gameserver.skills.effects.EffectTemplate;
import l2f.gameserver.templates.item.WeaponTemplate;
import l2f.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2f.gameserver.utils.PositionUtils;

public class Formulas
{
	public static double calcHpRegen(Creature cha)
	{
		double init;
		if (cha.isPlayer())
		{
			init = (cha.getLevel() <= 10 ? 1.5 + (cha.getLevel() / 20.) : 1.4 + (cha.getLevel() / 10.)) * cha.getLevelMod();
		}
		else
		{
			init = cha.getTemplate().baseHpReg;
		}

		if (cha.isPlayable())
		{
			init *= BaseStats.CON.calcBonus(cha);
			if (cha.isSummon())
			{
				init *= 2;
			}
		}

		return cha.calcStat(Stats.REGENERATE_HP_RATE, init, null, null);
	}

	public static double calcMpRegen(Creature cha)
	{
		double init;
		if (cha.isPlayer())
		{
			init = (.87 + (cha.getLevel() * .03)) * cha.getLevelMod();
		}
		else
		{
			init = cha.getTemplate().baseMpReg;
		}

		if (cha.isPlayable())
		{
			init *= BaseStats.MEN.calcBonus(cha);
			if (cha.isSummon())
			{
				init *= 2;
			}
		}

		return cha.calcStat(Stats.REGENERATE_MP_RATE, init, null, null);
	}

	public static double calcCpRegen(Creature cha)
	{
		double init = (1.5 + (cha.getLevel() / 10)) * cha.getLevelMod() * BaseStats.CON.calcBonus(cha);
		double cpRegenMultiplier = 1;
		if (cha.isPlayer())
		{
			Player player = cha.getPlayer();

			// Calculate Movement bonus
			if (player.isSitting())
			{
				cpRegenMultiplier *= 1.5; // Sitting
			}
			else if (!player.isMoving)
			{
				cpRegenMultiplier *= 1.1; // Staying
			}
			else if (player.isRunning())
			{
				cpRegenMultiplier *= 0.7; // Running
			}
		}
		else // Calculate Movement bonus
		if (!cha.isMoving)
		{
			cpRegenMultiplier *= 1.1; // Staying
		}
		else if (cha.isRunning())
		{
			cpRegenMultiplier *= 0.7; // Running
		}

		return cha.calcStat(Stats.REGENERATE_CP_RATE, init, null, null) * cpRegenMultiplier;
	}

	public static class AttackInfo
	{
		public double damage = 0;
		public double defence = 0;
		public double crit_static = 0;
		public double death_rcpt = 0;
		public double lethal1 = 0;
		public double lethal2 = 0;
		public double lethal_dmg = 0;
		public boolean crit = false;
		public boolean shld = false;
		public boolean lethal = false;
		public boolean miss = false;
	}

	/**
	 * For simple strokes patk = patk a critical simple stroke: patk = patk * (1 + crit_damage_rcpt) * crit_damage_mod + crit_damage_static to blow skill TODO To skillovyh crits, damage just doubled buffs have no effect (except for blow, for them above) patk = (1 + crit_damage_rcpt) * (patk +
	 * Skill_power) For normal attacks damage = patk * ss_bonus * 70 / pdef
	 * @param attacker
	 * @param target
	 * @param skill
	 * @param dual
	 * @param blow
	 * @param ss
	 * @param onCrit
	 * @return
	 */
	public static AttackInfo calcPhysDam(Creature attacker, Creature target, Skill skill, boolean dual, boolean blow, boolean ss, boolean onCrit)
	{
		AttackInfo info = new AttackInfo();

		info.damage = attacker.getPAtk(target);
		info.defence = target.getPDef(attacker);
		info.crit_static = attacker.calcStat(Stats.CRITICAL_DAMAGE_STATIC, target, skill);
		info.death_rcpt = 0.01 * target.calcStat(Stats.DEATH_VULNERABILITY, attacker, skill);
		info.lethal1 = skill == null ? 0 : skill.getLethal1() * info.death_rcpt;
		info.lethal2 = skill == null ? 0 : skill.getLethal2() * info.death_rcpt;
		info.crit = Rnd.chance(calcCrit(attacker, target, skill, blow));
		info.shld = ((skill == null) || !skill.getShieldIgnore()) && Formulas.calcShldUse(attacker, target);
		info.lethal = false;
		info.miss = false;
		boolean isPvP = attacker.isPlayable() && target.isPlayable();

		if (info.shld)
		{
			info.defence += target.getShldDef();
		}

		info.defence = Math.max(info.defence, 1);

		if (skill != null)
		{
			final SkillTrait trait = skill.getTraitType();
			if (trait != null)
			{
				final Env env = new Env(attacker, target, skill);
				double traitMul = 1. + (trait.calcProf(env) - trait.calcVuln(env)) / 100.;
				if (traitMul == Double.NEGATIVE_INFINITY) // invul
				{
					info.damage = 0;
					return info;
				}
				/*
				 * else if (traitMul > 2.) // DS: нужны тесты
				 * traitMul = 2.;
				 * else if (traitMul < 0.05)
				 * traitMul = 0.05;
				 * power *= traitMul;
				 */
			}

			if (!blow && !target.isLethalImmune() && target.getLevel() - skill.getMagicLevel() <= 5) // считаем леталы для не blow скиллов
			{
				if (info.lethal1 > 0 && Rnd.chance(info.lethal1))
				{
					if (target.isPlayer())
					{
						info.lethal = true;
						info.lethal_dmg = target.getCurrentCp();
						target.sendPacket(SystemMsg.YOUR_CP_WAS_DRAINED_BECAUSE_YOU_WERE_HIT_WITH_A_CP_SIPHON_SKILL);
					}
					else
					{
						info.lethal_dmg = target.getCurrentHp() / 2;
					}
					attacker.sendPacket(SystemMsg.CP_SIPHON);
				}
				else if (info.lethal2 > 0 && Rnd.chance(info.lethal2))
				{
					if (target.isPlayer())
					{
						info.lethal = true;
						info.lethal_dmg = (target.getCurrentHp() + target.getCurrentCp()) - 1.1; // Oly \ Duel hack installation is not exactly 1 HP, and a little more to prevent
																									// psevdosmerti
						target.sendPacket(SystemMsg.LETHAL_STRIKE);
					}
					else
					{
						info.lethal_dmg = target.getCurrentHp() - 1;
					}
					attacker.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
				}
			}

			// If skill does not have his strength is useless to go further, you can immediately return to damage from flying
			if (skill.getPower(target) == 0)
			{
				info.damage = 0; // normal damage in this case does not apply
				return info;
			}

			if (blow && !skill.isBehind() && ss) // Для обычных blow не влияет на power
			{
				info.damage *= 2.04;
			}

			// Для зарядок влияет на суммарный бонус
			if (skill.isChargeBoost())
			{
				info.damage = attacker.calcStat(Stats.SKILL_POWER, info.damage + skill.getPower(target), null, null);
			}
			else
			{
				info.damage += attacker.calcStat(Stats.SKILL_POWER, skill.getPower(target), null, null);
			}

			if (blow && skill.isBehind() && ss) // Для backstab влияет на power, но меньше множитель
			{
				info.damage *= 1.5;
			}

			// Заряжаемые скилы имеют постоянный урон
			if (!skill.isChargeBoost())
			{
				info.damage *= 1 + (Rnd.get() * attacker.getRandomDamage() * 2 - attacker.getRandomDamage()) / 100;
			}

			if (blow)
			{
				info.damage *= 0.01 * attacker.calcStat(Stats.CRITICAL_DAMAGE, target, skill);
				info.damage = target.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, info.damage, attacker, skill);
				// Synerge - Boost 20% the damage of static critical damage bonuses
				info.damage += 6.1 * info.crit_static * 1.2;
			}

			if (skill.isChargeBoost())
			{
				info.damage *= 0.8 + 0.2 * (attacker.getIncreasedForce() + Math.max(skill.getNumCharges(), 0));
			}
			else if (skill.isSoulBoost())
			{
				info.damage *= 1.0 + 0.06 * Math.min(attacker.getConsumedSouls(), 5);
			}

			// Gracia Physical Skill Damage Bonus
			info.damage *= 1.10113;

			if (info.crit)
			{
				info.damage *= 2.;
			}
		}
		else
		{
			info.damage *= 1 + (((Rnd.get() * attacker.getRandomDamage() * 2) - attacker.getRandomDamage()) / 100);

			if (dual)
			{
				info.damage /= 2.;
			}

			if (info.crit)
			{
				info.damage *= 0.01 * attacker.calcStat(Stats.CRITICAL_DAMAGE, target, skill);
				info.damage = 2 * target.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, info.damage, attacker, skill);
				info.damage += info.crit_static;
			}
		}

		if (info.crit)
		{
			// Absorption chance soul (no animation) on crit if Soul Mastery Level 4 or higher
			int chance = attacker.getSkillLevel(Skill.SKILL_SOUL_MASTERY);
			if (chance > 0)
			{
				if (chance >= 21)
				{
					chance = 30;
				}
				else if (chance >= 15)
				{
					chance = 25;
				}
				else if (chance >= 9)
				{
					chance = 20;
				}
				else if (chance >= 4)
				{
					chance = 15;
				}
				if (Rnd.chance(chance))
				{
					attacker.setConsumedSouls(attacker.getConsumedSouls() + 1, null);
				}
			}
		}

		if (skill == null || !skill.isChargeBoost())
		{
			switch (PositionUtils.getDirectionTo(target, attacker))
			{
			case BEHIND:
				info.damage *= 1.2;
				break;
			case SIDE:
				info.damage *= 1.1;
				break;
			}
		}

		if (ss && !blow)
		{
			info.damage *= 2.0;
		}

		info.damage *= 70. / info.defence;
		info.damage = attacker.calcStat(Stats.PHYSICAL_DAMAGE, info.damage, target, skill);

		if (info.shld && Rnd.chance(5))
		{
			info.damage = 1;
		}

		if (isPvP)
		{
			if (skill == null)
			{
				info.damage *= attacker.calcStat(Stats.PVP_PHYS_DMG_BONUS, 1, null, null);
				info.damage /= target.calcStat(Stats.PVP_PHYS_DEFENCE_BONUS, 1, null, null);
			}
			else
			{
				info.damage *= attacker.calcStat(Stats.PVP_PHYS_SKILL_DMG_BONUS, 1, null, null);
				info.damage /= target.calcStat(Stats.PVP_PHYS_SKILL_DEFENCE_BONUS, 1, null, null);
			}
		}

		// Custom PVE Balance for doombringer increase normal dmg 50% / critical 30% / skill dmg 10%
		boolean isPVE = attacker.isPlayable() && target.isMonster();
		if (isPVE)
		{
			if (attacker.getPlayer().getClassId() == ClassId.doombringer || attacker.getPlayer().getClassId() == ClassId.berserker)
			{
				if (!info.crit && skill == null)
				{
					info.damage *= 1.5;
				}
				else if (info.crit && skill == null)
				{
					info.damage *= 1.3;
				}
				else if (skill != null)
				{
					info.damage *= 1.1;
				}
			}
		}

		// Synerge - Custom PvP Balance
		if (attacker.isPlayer() && attacker.getPlayer().getActiveWeaponItem() != null && target.isPlayable())
		{
			switch (attacker.getPlayer().getClassId())
			{
			// Synerge - Archers boost for normal attack damage +150%. 50% on crit. +10% on skills. 10% on olympiad
			case hawkeye:
			case silverRanger:
			case phantomRanger:
			case sagittarius:
			case moonlightSentinel:
			case ghostSentinel:
			case trickster:
			case arbalester:
			{
				if (!info.crit && skill == null)
				{
					info.damage *= 2.5;
				}
				else if (info.crit && skill == null)
				{
					info.damage *= 1.9;
				}
				else if (skill != null)
				{
					info.damage *= 1.1;
				}
				if (attacker.isInOlympiadMode())
				{
					info.damage *= 1.1;
				}
				break;
			}
			// Synerge - Daggers +10% on skills not in olympiad
			case treasureHunter:
			case plainsWalker:
			case abyssWalker:
			case adventurer:
			case windRider:
			case ghostHunter:
			{
				if (skill != null && !attacker.isInOlympiadMode())
				{
					info.damage *= 1.1;
				}
				break;
			}
			// Synerge - Doombridger general damage boost
			case doombringer:
			{
				info.damage *= 1.3;
				if (!attacker.isInOlympiadMode())
				{
					info.damage *= 1.1;
				}
				break;
			}
			// Synerge - Titan +20% damage and +10% in olympiad. +10% for two handed blunt
			case titan:
			{
				info.damage *= 1.30;
				if (!attacker.isInOlympiadMode())
				{
					info.damage *= 1.15;
				}
				if (attacker.getPlayer().getActiveWeaponItem().getItemType() == WeaponType.BIGBLUNT)
				{
					info.damage *= 1.1;
				}
				break;
			}
			// Synerge - Nerf for duelist 10% when using Triple Sonic Slash in olympiad. +10% on skills
			case duelist:
			{
				if (attacker.isInOlympiadMode() && skill != null && skill.getId() == 261)
				{
					info.damage *= 0.9;
				}
				else if (skill != null)
				{
					info.damage *= 1.1;
				}
				break;
			}
			// Synerge - Nerf for grandkhavatari 10% on skills in olympiad
			case grandKhauatari:
			{
				info.damage *= 1.1;
				if (attacker.isInOlympiadMode() && skill != null)
				{
					info.damage *= 0.8;
				}
				break;
			}
			// Nerf for SoulHound 5% on skills in olympiad
			case maleSoulhound:
			case femaleSoulhound:
			{
				info.damage *= 0.95;
				if (attacker.isInOlympiadMode() && skill != null)
				{
					info.damage *= 0.95;
				}
				break;
			}
			}
		}

		// Then only check if skill! = Null, since Player.onHitTimer not cheat damage.
		if (skill != null)
		{
			if (info.shld)
			{
				if (info.damage == 1)
				{
					target.sendPacket(SystemMsg.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
				}
				else
				{
					target.sendPacket(SystemMsg.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);
				}
			}

			// Flee from physical attack skills leads to 0
			// if ((info.damage > 1.0) && !skill.hasEffects() && Rnd.chance(target.calcStat(Stats.PSKILL_EVASION, 0.0, attacker, skill)))
			if (info.damage > 1 && !skill.hasNotSelfEffects() && Rnd.chance(target.calcStat(Stats.PSKILL_EVASION, 0, attacker, skill)))
			{
				attacker.sendPacket(new SystemMessage2(SystemMsg.C1S_ATTACK_WENT_ASTRAY).addName(attacker));
				target.sendPacket(new SystemMessage2(SystemMsg.C1_HAS_EVADED_C2S_ATTACK).addName(target).addName(attacker));
				info.damage = 0.0;
			}

			if ((info.damage > 1.0) && skill.isDeathlink())
			{
				info.damage *= 1.8 * (1.0 - attacker.getCurrentHpRatio());
			}

			if (onCrit && !calcBlow(attacker, target, skill))
			{
				info.miss = true;
				info.damage = 0.0;
				attacker.sendPacket(new SystemMessage2(SystemMsg.C1S_ATTACK_WENT_ASTRAY).addName(attacker));
			}

			if (blow && target.getLevel() - skill.getMagicLevel() <= 5)
			{
				if (info.lethal1 > 0 && Rnd.chance(info.lethal1))
				{
					if (target.isPlayer())
					{
						info.lethal = true;
						info.lethal_dmg = target.getCurrentCp();
						target.sendPacket(SystemMsg.YOUR_CP_WAS_DRAINED_BECAUSE_YOU_WERE_HIT_WITH_A_CP_SIPHON_SKILL);
					}
					else if (target.isLethalImmune())
					{
						info.damage *= 2.0;
					}
					else
					{
						info.lethal_dmg = target.getCurrentHp() / 2.0;
					}
					attacker.sendPacket(SystemMsg.CP_SIPHON);
				}
				else if (info.lethal2 > 0 && Rnd.chance(info.lethal2))
				{
					if (target.isPlayer())
					{
						info.lethal = true;
						info.lethal_dmg = (target.getCurrentHp() + target.getCurrentCp()) - 1.1;
						target.sendPacket(SystemMsg.LETHAL_STRIKE);
					}
					else if (target.isLethalImmune())
					{
						info.damage *= 3;
					}
					else
					{
						info.lethal_dmg = target.getCurrentHp() - 1.0;
					}
					attacker.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
				}
			}

			if (info.damage > 0.0)
			{
				attacker.displayGiveDamageMessage(target, (int) info.damage, info.crit || blow, false, false, false);
			}

			if (target.isStunned() && calcStunBreak(info.crit))
			{
				target.getEffectList().stopEffects(EffectType.Stun);
			}

			if (calcCastBreak(target, info.crit))
			{
				target.abortCast(false, true);
			}
		}

		// Synerge - Add the damage done to the player stats
		// if (attacker.isPlayer() && info.damage > 5)
		// attacker.getPlayer().addPlayerStats(Ranking.STAT_TOP_DAMAGE, (long)info.damage);

		return info;
	}

	public static double calcMagicDam(Creature attacker, Creature target, Skill skill, int sps, boolean toMp)
	{
		final boolean isCubic = skill.getMatak() > 0;
		boolean isPvP = attacker.isPlayable() && target.isPlayable();
		// ShieldIgnore option for magical skills is inverted
		boolean shield = skill.getShieldIgnore() && calcShldUse(attacker, target);

		double mAtk = attacker.getMAtk(target, skill);

		if (sps == 2)
		{
			mAtk *= 4;
		}
		else if (sps == 1)
		{
			mAtk *= 2;
		}

		double mdef = target.getMDef(null, skill);

		if (shield)
		{
			mdef += target.getShldDef();
		}
		if (mdef == 0)
		{
			mdef = 1;
		}

		double power = skill.getPower(target);

		boolean gradePenalty = attacker.isPlayer() && ((Player) attacker).getWeaponsExpertisePenalty() > 0;

		final SkillTrait trait = skill.getTraitType();
		if (trait != null)
		{
			final Env env = new Env(attacker, target, skill);
			double traitMul = 1. + (trait.calcProf(env) - trait.calcVuln(env)) / 100.;
			if (traitMul == Double.NEGATIVE_INFINITY)
			{ // invul
				return 0;
			}
			else if (traitMul > 2.)
			{
				traitMul = 2.;
			}
			else if (traitMul < 0.05)
			{
				traitMul = 0.05;
			}

			power *= traitMul;
		}

		double lethalDamage = 0;

		if (target.getLevel() - skill.getMagicLevel() <= 9 && !gradePenalty)
		{
			if (skill.getLethal1() > 0 && Rnd.chance(skill.getLethal1()))
			{
				if (target.isPlayer())
				{
					lethalDamage = target.getCurrentCp();
					target.sendPacket(SystemMsg.YOUR_CP_WAS_DRAINED_BECAUSE_YOU_WERE_HIT_WITH_A_CP_SIPHON_SKILL);
				}
				else if (!target.isLethalImmune())
				{
					lethalDamage = target.getCurrentHp() / 2;
				}
				else
				{
					power *= 2;
				}
				attacker.sendPacket(SystemMsg.CP_SIPHON);
			}
			else if (skill.getLethal2() > 0 && Rnd.chance(skill.getLethal2()))
			{
				if (target.isPlayer())
				{
					lethalDamage = (target.getCurrentHp() + target.getCurrentCp()) - 1.1;
					target.sendPacket(SystemMsg.LETHAL_STRIKE);
				}
				else if (!target.isLethalImmune())
				{
					lethalDamage = target.getCurrentHp() - 1;
				}
				else
				{
					power *= 3;
				}
				attacker.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
			}
		}

		if (power == 0)
		{
			if (lethalDamage > 0)
			{
				attacker.displayGiveDamageMessage(target, (int) lethalDamage, false, false, false, false);
			}
			return lethalDamage;
		}

		if (skill.isSoulBoost())
		{
			power *= 1.0 + 0.0225 * Math.min(attacker.getConsumedSouls(), 5);
		}

		boolean crit = false;
		double damage = power * Math.sqrt(mAtk) / mdef;
		if (toMp)
		{
			if (isPvP)
			{
				damage *= target.getMaxMp() / 97.;
			}
			else
			{
				damage *= 91.;
				damage = Math.max(1, damage / 2.);
			}
		}
		else
		{
			damage *= 91.;
		}

		if (skill.getMatak() == 0) // у кубиков нет рандомдамага и критов
		{
			damage *= 1 + (Rnd.get() * attacker.getRandomDamage() * 2 - attacker.getRandomDamage()) / 100;
			crit = calcMCrit(attacker.getMagicCriticalRate(target, skill));
		}

		if (crit)
		{
			// Synerge - Nerfed with 10% Magic Critical Damage
			damage *= attacker.calcStat(Stats.MCRITICAL_DAMAGE, attacker.isPlayable() && target.isPlayable() ? 2.4 : 3., target, skill);

			// Synerge - Nerf 10% critical magic damage when using Enlightment on olympiad
			if (attacker.isInOlympiadMode() && attacker.getEffectList().getEffectsCountForSkill(1532) > 0)
			{
				damage *= 0.9;
			}
		}

		damage = attacker.calcStat(Stats.MAGIC_DAMAGE, damage, target, skill);

		if (shield)
		{
			if (Rnd.chance(5))
			{
				damage = 0;
				target.sendPacket(SystemMsg.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
				attacker.sendPacket(new SystemMessage2(SystemMsg.C1_RESISTED_C2S_MAGIC).addName(target).addName(attacker));
			}
			else
			{
				target.sendPacket(SystemMsg.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);
				attacker.sendPacket(new SystemMessage2(SystemMsg.YOUR_OPPONENT_HAS_RESISTANCE_TO_MAGIC_THE_DAMAGE_WAS_DECREASED));
			}
		}

		int levelDiff = target.getLevel() - attacker.getLevel(); // C Gracia Epilogue уровень маг. атак считается только по уроню атакующего

		if (damage > 1)
		{
			if (skill.isDeathlink())
			{
				damage *= 1.8 * (1.0 - attacker.getCurrentHpRatio());
			}

			if (skill.isBasedOnTargetDebuff())
			{
				int effectCount = 0;
				for (Effect e : target.getEffectList().getAllFirstEffects())
				{
					if (!e.getSkill().isToggle())
					{
						effectCount++;
					}
				}

				damage *= 0.3 + 0.0875 * effectCount;
			}
		}

		damage += lethalDamage;

		if (isPvP && (damage > 1))
		{
			damage *= attacker.calcStat(Stats.PVP_MAGIC_SKILL_DMG_BONUS, 1, null, null);
			damage /= target.calcStat(Stats.PVP_MAGIC_SKILL_DEFENCE_BONUS, 1, null, null);
		}

		// Synerge - Custom Balance
		if (attacker.isPlayer() && target.isPlayable())
		{
			switch (attacker.getPlayer().getClassId())
			{
			// Synerge - Mages +5% damage
			case sorceror:
			case necromancer:
			case spellsinger:
			case spellhowler:
			case archmage:
			case soultaker:
			case mysticMuse:
			case stormScreamer:
			{
				damage *= 1.05;
				break;
			}
			}
		}

		double magic_rcpt = target.calcStat(Stats.MAGIC_RESIST, attacker, skill) - attacker.calcStat(Stats.MAGIC_POWER, target, skill);
		double lvlMod = 4. * Math.max(1., target.getLevel() >= 80 ? (levelDiff - 4) * 1.6 : (levelDiff - 14) * 2);
		double failChance = gradePenalty ? 95. : Math.min(lvlMod * (1. + magic_rcpt / 100.), 95.);
		double resistChance = gradePenalty ? 95. : 5 * Math.max(levelDiff - 10, 1);

		if (attacker.isPlayer() && ((Player) attacker).isDebug())
		{
			attacker.sendMessage("Fail chance " + (int) failChance + "/" + (int) resistChance);
		}

		if (Rnd.chance(failChance))
		{
			if (Rnd.chance(resistChance))
			{
				damage = 0;
				SystemMessage msg = new SystemMessage(SystemMessage.C1_RESISTED_C2S_MAGIC).addName(target).addName(attacker);
				attacker.sendPacket(msg);
				target.sendPacket(msg);
			}
			else
			{
				damage /= 2;
				SystemMessage msg = new SystemMessage(SystemMessage.DAMAGE_IS_DECREASED_BECAUSE_C1_RESISTED_AGAINST_C2S_MAGIC).addName(target).addName(attacker);
				attacker.sendPacket(msg);
				target.sendPacket(msg);
			}
		}

		if (damage > 0)
		{
			attacker.displayGiveDamageMessage(target, (int) damage, crit, false, false, true);
		}

		if (calcCastBreak(target, crit))
		{
			target.abortCast(false, true);
		}

		// Synerge - Add the damage done to the player stats
		// if (attacker.isPlayer() && damage > 5)
		// attacker.getPlayer().addPlayerStats(Ranking.STAT_TOP_DAMAGE, (long)damage);

		return damage;
	}

	public static boolean calcStunBreak(boolean crit)
	{
		return Rnd.chance(crit ? 75 : 10);
	}

	/**
	 * @param activeChar
	 * @param target
	 * @param skill
	 * @return Returns true in case of fatal blow success
	 */
	@SuppressWarnings("incomplete-switch")
	public static boolean calcBlow(Creature activeChar, Creature target, Skill skill)
	{
		WeaponTemplate weapon = activeChar.getActiveWeaponItem();

		double base_weapon_crit = weapon == null ? 4. : weapon.getCritical();
		double crit_height_bonus = (0.008 * Math.min(25, Math.max(-25, target.getZ() - activeChar.getZ()))) + 1.1;
		double buffs_mult = activeChar.calcStat(Stats.FATALBLOW_RATE, target, skill);
		double skill_mod = skill.isBehind() ? 6 : 5; // CT 2.3 blowrate increase

		double chance = base_weapon_crit * buffs_mult * crit_height_bonus * skill_mod;

		if (!target.isInCombat())
		{
			chance *= 1.1;
		}

		switch (PositionUtils.getDirectionTo(target, activeChar))
		{
		case BEHIND:
			// Backstabs always suceed on behind
			if (skill.isBehind())
			{
				return true;
			}

			chance *= 1.3;
			break;
		case SIDE:
			chance *= 1.1;
			break;
		case FRONT:
			// Backstabs always fail on front
			if (skill.isBehind())
			{
				return false;
			}
			break;
		}
		chance = Math.min(skill.isBehind() ? 100 : 80, chance);
		return Rnd.chance(chance);
	}

	public static double calcCrit(Creature attacker, Creature target, Skill skill, boolean blow)
	{
		if (attacker.isPlayer() && (attacker.getActiveWeaponItem() == null))
		{
			return 0;
		}
		if (skill != null)
		{
			return skill.getCriticalRate() * (blow ? BaseStats.DEX.calcBonus(attacker) : BaseStats.STR.calcBonus(attacker)) * 0.01 * attacker.calcStat(Stats.SKILL_CRIT_CHANCE_MOD, target, skill);
		}

		double rate = attacker.getCriticalHit(target, null) * 0.01 * target.calcStat(Stats.CRIT_CHANCE_RECEPTIVE, attacker, skill);

		switch (PositionUtils.getDirectionTo(target, attacker))
		{
		case BEHIND:
			rate *= 1.4;
			break;
		case SIDE:
			rate *= 1.2;
			break;
		}

		// Synerge - Nerfed 40% the chance of critical hit
		rate *= 0.6;

		return rate / 10;
	}

	public static boolean calcMCrit(double mRate)
	{
		// floating point random gives more accuracy calculation, because argument also floating point
		// Synerge - Nerfed 30% the chance of critical magic hit
		return (Rnd.get() * 100) <= Math.min(Config.LIM_MCRIT, mRate * 0.7);
	}

	public static boolean calcCastBreak(Creature target, boolean crit)
	{
		if ((target == null) || target.isInvul() || target.isRaid() || !target.isCastingNow())
		{
			return false;
		}
		Skill skill = target.getCastingSkill();
		if ((skill != null) && ((skill.getSkillType() == SkillType.TAKECASTLE) || (skill.getSkillType() == SkillType.TAKEFORTRESS) || (skill.getSkillType() == SkillType.TAKEFLAG)))
		{
			return false;
		}
		return Rnd.chance(target.calcStat(Stats.CAST_INTERRUPT, crit ? 75 : 10, null, skill));
	}

	/**
	 * Calculate delay (in milliseconds) before next ATTACK
	 * @param rate
	 * @return
	 */
	public static int calcPAtkSpd(double rate)
	{
		return (int) (500000.0 / rate); // в миллисекундах поэтому 500*1000
	}

	/**
	 * Calculate delay (in milliseconds) for skills cast
	 * @param attacker
	 * @param skill
	 * @param skillTime
	 * @return
	 */
	public static int calcMAtkSpd(Creature attacker, Skill skill, double skillTime)
	{
		if (skill.isMagic())
		{
			return (int) ((skillTime * 333) / Math.max(attacker.getMAtkSpd(), 1));
		}
		return (int) ((skillTime * 333) / Math.max(attacker.getPAtkSpd(), 1));
	}

	/**
	 * Calculate reuse delay (in milliseconds) for skills
	 * @param actor
	 * @param skill
	 * @return
	 */
	public static long calcSkillReuseDelay(Creature actor, Skill skill)
	{
		long reuseDelay = skill.getReuseDelay(actor);
		if (actor.isMonster())
		{
			reuseDelay = skill.getReuseForMonsters();
		}
		if (skill.isReuseDelayPermanent() || skill.isHandler() || skill.isItemSkill())
		{
			return reuseDelay;
		}
		if (actor.getSkillMastery(skill.getId()) == 1)
		{
			actor.removeSkillMastery(skill.getId());
			return 0;
		}
		if (skill.isMagic())
		{
			return (long) actor.calcStat(Stats.MAGIC_REUSE_RATE, reuseDelay, null, skill);
		}
		return (long) actor.calcStat(Stats.PHYSIC_REUSE_RATE, reuseDelay, null, skill);
	}

	/**
	 * Returns true if hit missed (target evaded)
	 * @param attacker
	 * @param target
	 * @return
	 */
	public static boolean calcHitMiss(Creature attacker, Creature target)
	{
		int chanceToHit = 88 + (2 * (attacker.getAccuracy() - target.getEvasionRate(attacker)));

		PositionUtils.TargetDirection direction = PositionUtils.getDirectionTo(attacker, target);
		switch (direction)
		{
		case BEHIND:
			chanceToHit *= 1.2;
			break;
		case SIDE:
			chanceToHit *= 1.1;
			break;
		}

		chanceToHit = Math.max(chanceToHit, 28);
		chanceToHit = Math.min(chanceToHit, 98);

		return !Rnd.chance(chanceToHit);
	}

	/**
	 * Returns true if shield defence successfull
	 * @param attacker
	 * @param target
	 * @return
	 */
	public static boolean calcShldUse(Creature attacker, Creature target)
	{
		WeaponTemplate template = target.getSecondaryWeaponItem();
		if ((template == null) || (template.getItemType() != WeaponTemplate.WeaponType.NONE))
		{
			return false;
		}
		int angle = (int) target.calcStat(Stats.SHIELD_ANGLE, attacker, null);
		if (!PositionUtils.isFacing(target, attacker, angle))
		{
			return false;
		}
		return Rnd.chance((int) target.calcStat(Stats.SHIELD_RATE, attacker, null));
	}

	public static boolean calcSkillSuccess(Env env, EffectTemplate et, int spiritshot)
	{
		if (env.value == -1)
		{
			return true;
		}

		env.value = Math.max(Math.min(env.value, 120), 1); // На всякий случай
		final double base = env.value; // Запоминаем базовый шанс (нужен позже)

		final Skill skill = env.skill;
		if (!skill.isOffensive())
		{
			return Rnd.chance(env.value);
		}

		final Creature caster = env.character;
		final Creature target = env.target;

		boolean debugCaster = false;
		boolean debugTarget = false;
		boolean debugGlobal = false;
		if (Config.ALT_DEBUG_ENABLED || caster.getAccessLevel() > 0)
		{
			debugCaster = (caster.getPlayer() != null) && (caster.getPlayer().isDebug() || caster.getAccessLevel() > 0);
			debugTarget = (target.getPlayer() != null) && target.getPlayer().isDebug();
			final boolean debugPvP = Config.ALT_DEBUG_PVP_ENABLED && (debugCaster && debugTarget) && (!Config.ALT_DEBUG_PVP_DUEL_ONLY || (caster.getPlayer().isInDuel() && target.getPlayer().isInDuel()));
			debugGlobal = debugPvP || caster.getAccessLevel() > 0 || (Config.ALT_DEBUG_PVE_ENABLED && ((debugCaster && target.isMonster()) || (debugTarget && caster.isMonster())));
		}

		if (debugCaster)
		{
			caster.getPlayer().sendMessage("Chance Initial: " + env.value);
		}

		double statMod = 1.;
		if (skill.getSaveVs() != null)
		{
			statMod = skill.getSaveVs().calcChanceMod(target);
			env.value *= statMod; // Бонус от MEN/CON/etc
		}

		env.value = Math.max(env.value, 1);

		double mAtkMod = 1.;
		int ssMod = 0;
		if (skill.isMagic() && (et == null || et.chance(100) < 0)) // Этот блок только для магических скиллов, эффекты с отдельным шансом тоже пропускаются
		{
			int mdef = Math.max(1, target.getMDef(target, skill)); // Вычисляем mDef цели
			double matk = caster.getMAtk(target, skill);

			if (skill.isSSPossible())
			{
				switch (spiritshot)
				{
				case ItemInstance.CHARGED_BLESSED_SPIRITSHOT:
					ssMod = 4;
					break;
				case ItemInstance.CHARGED_SPIRITSHOT:
					ssMod = 2;
					break;
				default:
					ssMod = 1;
				}
				matk *= ssMod;
			}

			mAtkMod = Config.SKILLS_CHANCE_MOD * Math.pow(matk, Config.SKILLS_CHANCE_POW) / mdef;

			/*
			 * if (mAtkMod < 0.7)
			 * mAtkMod = 0.7;
			 * else if (mAtkMod > 1.4)
			 * mAtkMod = 1.4;
			 */

			env.value *= mAtkMod;
			env.value = Math.max(env.value, 1);
		}

		double lvlDependMod = skill.getLevelModifier();
		if (lvlDependMod != 0)
		{
			final int attackLevel = skill.getMagicLevel() > 0 ? skill.getMagicLevel() : caster.getLevel();
			/*
			 * final int delta = attackLevel - target.getLevel();
			 * lvlDependMod = delta / 5;
			 * lvlDependMod = lvlDependMod * 5;
			 * if (lvlDependMod != delta)
			 * lvlDependMod = delta < 0 ? lvlDependMod - 5 : lvlDependMod + 5;
			 * env.value += lvlDependMod;
			 */
			lvlDependMod = 1. + (attackLevel - target.getLevel()) * 0.03 * lvlDependMod;
			if (lvlDependMod < 0)
			{
				lvlDependMod = 0;
			}
			else if (lvlDependMod > 2)
			{
				lvlDependMod = 2;
			}

			env.value *= lvlDependMod;
		}

		double vulnMod = 0;
		double profMod = 0;
		double resMod = 1.;
		double debuffMod = 1.;
		if (!skill.isIgnoreResists())
		{
			if (et == null || et.chance(100) < 0) // Effects with an individual chance - not debuffs (reset goals). TODO: individual flag
			{
				debuffMod = 1. - (target.calcStat(Stats.DEBUFF_RESIST, 100., caster, skill) - 100.) / 120.;

				if (debuffMod != 1) // Attention, the sign has been reversed!
				{
					if (debuffMod == Double.NEGATIVE_INFINITY)
					{
						if (debugGlobal)
						{
							if (debugCaster)
							{
								caster.getPlayer().sendMessage("Full debuff immunity");
							}
							if (debugTarget)
							{
								target.getPlayer().sendMessage("Full debuff immunity");
							}
						}
						return false;
					}
					if (debuffMod == Double.POSITIVE_INFINITY)
					{
						if (debugGlobal)
						{
							if (debugCaster)
							{
								caster.getPlayer().sendMessage("Full debuff vulnerability");
							}
							if (debugTarget)
							{
								target.getPlayer().sendMessage("Full debuff vulnerability");
							}
						}
						return true;
					}

					debuffMod = Math.max(debuffMod, 0);
					env.value *= debuffMod;
				}
			}

			SkillTrait trait = skill.getTraitType();
			if (trait != null)
			{
				vulnMod = trait.calcVuln(env);
				profMod = trait.calcProf(env);

				final double maxResist = 90 + profMod * 0.85;
				resMod = (maxResist - vulnMod) / 60.;
			}

			if (resMod != 1) // Внимание, знак был изменен на противоположный !
			{
				if (resMod == Double.NEGATIVE_INFINITY)
				{
					if (debugGlobal)
					{
						if (debugCaster)
						{
							caster.getPlayer().sendMessage("Full immunity");
						}
						if (debugTarget)
						{
							target.getPlayer().sendMessage("Full immunity");
						}
					}
					return false;
				}
				if (resMod == Double.POSITIVE_INFINITY)
				{
					if (debugGlobal)
					{
						if (debugCaster)
						{
							caster.getPlayer().sendMessage("Full vulnerability");
						}
						if (debugTarget)
						{
							target.getPlayer().sendMessage("Full vulnerability");
						}
					}
					return true;
				}

				resMod = Math.max(resMod, 0);
				env.value *= resMod;
			}
		}

		double elementMod = 0;
		final Element element = skill.getElement();
		if (element != Element.NONE)
		{
			elementMod = skill.getElementPower();
			Element attackElement = getAttackElement(caster, target);
			if (attackElement == element)
			{
				elementMod += caster.calcStat(element.getAttack(), 0, target, null);
			}

			elementMod = getElementMod(elementMod, target.calcStat(element.getDefence(), 0, caster, null), caster.isPlayer() && target.isPlayer());
			env.value *= elementMod;
		}

		// if(skill.isSoulBoost())
		// env.value *= 0.85 + 0.06 * Math.min(character.getConsumedSouls(), 5);

		env.value = Math.max(env.value, Math.min(base, Config.SKILLS_CHANCE_MIN)); // If the base chance is more than Config.SKILLS_CHANCE_MIN, then with a small difference in the
																					// levels, make a cap from below.
		env.value = Math.max(Math.min(env.value, Config.SKILLS_CHANCE_CAP), 1); // We apply the drop
		final boolean result = Rnd.chance((int) env.value);

		if (debugGlobal)
		{
			StringBuilder stat = new StringBuilder(100);
			stat.append(skill.getId());
			stat.append("/");
			stat.append(skill.getDisplayLevel());
			stat.append(" ");
			if (et == null)
			{
				stat.append(skill.getName());
			}
			else
			{
				stat.append(et._effectType.name());
			}
			stat.append(" AR:");
			stat.append((int) base);
			stat.append(" ");
			if (skill.getSaveVs() != null)
			{
				stat.append(skill.getSaveVs().name());
				stat.append(":");
				stat.append(String.format("%1.1f", statMod));
			}
			if (skill.isMagic())
			{
				stat.append(" ");
				stat.append(" mAtk:");
				stat.append(String.format("%1.1f", mAtkMod));
				stat.append(" SS:");
				stat.append(ssMod);
			}
			if (skill.getTraitType() != null)
			{
				stat.append(" ");
				stat.append(skill.getTraitType().name());
			}
			stat.append(" ");
			stat.append(String.format("%1.1f", resMod));
			stat.append("(");
			stat.append(String.format("%1.1f", profMod));
			stat.append("/");
			stat.append(String.format("%1.1f", vulnMod));
			if (debuffMod != 0)
			{
				stat.append("+");
				stat.append(String.format("%1.1f", debuffMod));
			}
			stat.append(") lvl:");
			stat.append(String.format("%1.1f", lvlDependMod));
			stat.append(" elem:");
			stat.append(String.format("%1.1f", elementMod));
			stat.append(" Chance:");
			stat.append(String.format("%1.1f", env.value));
			if (!result)
			{
				stat.append(" failed");
			}

			// Send debugging messages
			if (debugCaster)
			{
				caster.getPlayer().sendMessage(stat.toString());
			}
			if (debugTarget)
			{
				target.getPlayer().sendMessage(stat.toString());
			}
		}
		return result;
	}

	public static boolean calcSkillSuccess(Creature player, Creature target, Skill skill, int activateRate)
	{
		Env env = new Env();
		env.character = player;
		env.target = target;
		env.skill = skill;
		if (skill != null && skill.getId() == Skill.SKILL_SERVITOR_SHARE)
		{
			env.target = player.getPlayer().getPet();
		}
		else
		{
			env.target = target;
		}
		env.value = activateRate;

		return calcSkillSuccess(env, null, player.getChargedSpiritShot());
	}

	public static void calcSkillMastery(Skill skill, Creature activeChar)
	{
		if (skill.isHandler())
		{
			return;
		}

		// Skill id 330 for fighters, 331 for mages
		// Actually only GM can have 2 skill masteries, so let's make them more lucky ^^
		if (calcSkillMasterySuccess(activeChar, skill))
		{
			// byte mastery level, 0 = no skill mastery, 1 = no reuseTime, 2 = buff duration*2, 3 = power*3
			int masteryLevel;
			SkillType type = skill.getSkillType();
			if (skill.isMusic() || (type == SkillType.BUFF) || (type == SkillType.HOT) || (type == SkillType.HEAL_PERCENT))
			{
				masteryLevel = 2;
			}
			else if (type == SkillType.HEAL)
			{
				masteryLevel = 3;
			}
			else
			{
				masteryLevel = 1;
			}
			if (masteryLevel > 0)
			{
				activeChar.setSkillMastery(skill.getId(), masteryLevel);
			}
		}
	}

	private static boolean calcSkillMasterySuccess(Creature activeChar, Skill skill)
	{
		if (skill.isIgnoreSkillMastery())
		{
			return false;
		}

		if ((activeChar.getSkillLevel(331) > 0 && activeChar.calcStat(Stats.SKILL_MASTERY, activeChar.getINT(), null, skill) >= Rnd.get(1000))
					|| ((activeChar.getSkillLevel(330) > 0) && (activeChar.calcStat(Stats.SKILL_MASTERY, activeChar.getSTR(), null, skill) >= Rnd.get(1000))))
		{
			return true;
		}

		return false;
	}

	public static double calcDamageResists(Skill skill, Creature attacker, Creature defender, double value)
	{
		if (attacker == defender)
		{
			return value; // TODO: should be considered on a good defense, but because these non-magical skills you should make a separate mechanism
		}

		if (attacker.isBoss())
		{
			value *= Config.RATE_EPIC_ATTACK;
		}
		else if (attacker.isRaid() || (attacker instanceof ReflectionBossInstance))
		{
			value *= Config.RATE_RAID_ATTACK;
		}

		if (defender.isBoss())
		{
			value /= Config.RATE_EPIC_DEFENSE;
		}
		else if (defender.isRaid() || (defender instanceof ReflectionBossInstance))
		{
			value /= Config.RATE_RAID_DEFENSE;
		}

		Player pAttacker = attacker.getPlayer();

		// if the player's level is lower than 2 or more mobs 78 + levels, its damage is reduced by mob
		int diff = defender.getLevel() - (pAttacker != null ? pAttacker.getLevel() : attacker.getLevel());
		if (attacker.isPlayable() && defender.isMonster() && (defender.getLevel() >= 78) && (diff > 2))
		{
			value *= .7 / Math.pow(diff - 2, .25);
		}

		Element element = Element.NONE;
		double power = 0.;

		if (skill != null)
		{
			element = skill.getElement();
			power = skill.getElementPower();
		}
		else
		{
			element = getAttackElement(attacker, defender);
		}

		if (element == Element.NONE)
		{
			return value;
		}

		if (pAttacker != null && pAttacker.isGM() && Config.DEBUG)
		{
			pAttacker.sendMessage("Element: " + element.name());
			pAttacker.sendMessage("Attack: " + attacker.calcStat(element.getAttack(), power, defender, skill));
			pAttacker.sendMessage("Defence: " + defender.calcStat(element.getDefence(), 0., attacker, skill));
			pAttacker.sendMessage("Modifier: " + getElementMod(attacker.calcStat(element.getAttack(), power, defender, skill), defender.calcStat(element.getDefence(), 0., attacker, skill),
						attacker.isPlayer() && defender.isPlayer()));
		}

		return value * getElementMod(attacker.calcStat(element.getAttack(), power, defender, skill), defender.calcStat(element.getDefence(), 0., attacker, skill), attacker.isPlayer() && defender.isPlayer());
	}

	/**
	 * Возвращает множитель для атаки из значений атакующего и защитного элемента.
	 * <br /><br />
	 * Диапазон от 1.0 до 1.7 (Freya)
	 * <br /><br />
	 * @param attack значение атаки
	 * @param defense значение защиты
	 * @param isPvP если оба игроки (самоны не считаются)
	 * @return множитель
	 */
	private static double getElementMod(double attack, double defense, boolean isPvP)
	{
		double diff = attack - defense;
		if (diff <= 0)
		{
			return 1.0;
		}
		else if (diff < 50)
		{
			return 1.0 + (diff * 0.003948);
		}
		else if (diff < 150)
		{
			return 1.2;
		}
		else if (diff < 300)
		{
			return 1.4;
		}
		else
		{
			return 1.7;
		}
	}

	public static Element getAttackElement(Creature attacker, Creature target)
	{
		double val, max = Double.MIN_VALUE;
		Element result = Element.NONE;
		for (Element e : Element.VALUES)
		{
			val = attacker.calcStat(e.getAttack(), 0., target, null);
			if (val <= 0.)
			{
				continue;
			}

			if (target != null)
			{
				val -= target.calcStat(e.getDefence(), 0., attacker, null);
			}

			if (val > max)
			{
				result = e;
				max = val;
			}
		}

		return result;
	}
}
