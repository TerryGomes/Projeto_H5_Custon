package l2f.gameserver.stats;

import java.util.NoSuchElementException;

public enum Stats
{
	MAX_HP("maxHp", 0., Double.POSITIVE_INFINITY, 1.), MAX_MP("maxMp", 0., Double.POSITIVE_INFINITY, 1.), MAX_CP("maxCp", 0., Double.POSITIVE_INFINITY, 1.),

	REGENERATE_HP_RATE("regHp"), REGENERATE_CP_RATE("regCp"), REGENERATE_MP_RATE("regMp"),

	HP_LIMIT("hpLimit", 1., 100., 100.), MP_LIMIT("mpLimit", 1., 100., 100.), CP_LIMIT("cpLimit", 1., 100., 100.),

	RUN_SPEED("runSpd"),

	POWER_DEFENCE("pDef"), MAGIC_DEFENCE("mDef"), POWER_ATTACK("pAtk"), MAGIC_ATTACK("mAtk"), POWER_ATTACK_SPEED("pAtkSpd"), MAGIC_ATTACK_SPEED("mAtkSpd"),

	MAGIC_REUSE_RATE("mReuse"), PHYSIC_REUSE_RATE("pReuse"), MUSIC_REUSE_RATE("musicReuse"), ATK_REUSE("atkReuse"), ATK_BASE("atkBaseSpeed"),

	CRITICAL_DAMAGE("cAtk", 0., Double.POSITIVE_INFINITY, 100.), CRITICAL_DAMAGE_STATIC("cAtkStatic"), EVASION_RATE("rEvas"), ACCURACY_COMBAT("accCombat"), CRITICAL_BASE("baseCrit", 0., Double.POSITIVE_INFINITY, 100.), // static
																																																							// crit
																																																							// rate.
																																																							// Use
																																																							// it
																																																							// to
																																																							// ADD
																																																							// some
																																																							// crit
																																																							// points.
																																																							// Sample:
																																																							// <add
																																																							// order="0x40"
																																																							// stat="baseCrit"
																																																							// val="27.4"
																																																							// />
	CRITICAL_RATE("rCrit", 0., Double.POSITIVE_INFINITY, 100.), // dynamic crit rate. Use it to MULTIPLE crit for 1.3, 1.5 etc. Sample: <add order="0x40" stat="rCrit" val="50" /> =
																// (x1.5)
	MCRITICAL_RATE("mCritRate", 0., Double.POSITIVE_INFINITY, 10.), MCRITICAL_DAMAGE("mCritDamage", 0., 10., 2.5),

	PHYSICAL_DAMAGE("physDamage"), MAGIC_DAMAGE("magicDamage"),

	CAST_INTERRUPT("concentration", 0., 100.), SHIELD_DEFENCE("sDef"), SHIELD_RATE("rShld", 0., 90.), SHIELD_ANGLE("shldAngle", 0., 360., 60.),

	POWER_ATTACK_RANGE("pAtkRange", 0., 1500.), MAGIC_ATTACK_RANGE("mAtkRange", 0., 1500.), POLE_ATTACK_ANGLE("poleAngle", 0., 180.), POLE_TARGET_COUNT("poleTargetCount"),

	STAT_STR("STR", 1., 99.), STAT_CON("CON", 1., 99.), STAT_DEX("DEX", 1., 99.), STAT_INT("INT", 1., 99.), STAT_WIT("WIT", 1., 99.), STAT_MEN("MEN", 1., 99.),

	BREATH("breath"), FALL("fall"), EXP_LOST("expLost"),

	BLEED_RESIST("bleedResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), POISON_RESIST("poisonResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), STUN_RESIST("stunResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), ROOT_RESIST("rootResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), MENTAL_RESIST("mentalResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), SLEEP_RESIST("sleepResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	PARALYZE_RESIST("paralyzeResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), CANCEL_RESIST("cancelResist", -200., 300.), DEBUFF_RESIST("debuffResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), MAGIC_RESIST("magicResist", -200., 300.),

	REAL_ROOT_RESIST("realRootResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), REAL_STUN_RESIST("realStunResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),

	BLEED_POWER("bleedPower", -200., 200.), POISON_POWER("poisonPower", -200., 200.), STUN_POWER("stunPower", -200., 200.), ROOT_POWER("rootPower", -200., 200.), MENTAL_POWER("mentalPower", -200., 200.), SLEEP_POWER("sleepPower", -200., 200.), PARALYZE_POWER("paralyzePower", -200., 200.), CANCEL_POWER("cancelPower", -200., 200.), DEBUFF_POWER("debuffPower", -200., 200.), MAGIC_POWER("magicPower", -200., 200.),

	FATALBLOW_RATE("blowRate", 0., 10., 1.), SKILL_CRIT_CHANCE_MOD("SkillCritChanceMod", 10., 190., 100.), DEATH_VULNERABILITY("deathVuln", 10., 190., 100.),

	CRIT_DAMAGE_RECEPTIVE("critDamRcpt", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), CRIT_CHANCE_RECEPTIVE("critChanceRcpt", 10., 190., 100.),

	DEFENCE_FIRE("defenceFire", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), DEFENCE_WATER("defenceWater", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), DEFENCE_WIND("defenceWind", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), DEFENCE_EARTH("defenceEarth", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), DEFENCE_HOLY("defenceHoly", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), DEFENCE_UNHOLY("defenceUnholy", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),

	ATTACK_FIRE("attackFire", 0., Double.POSITIVE_INFINITY), ATTACK_WATER("attackWater", 0., Double.POSITIVE_INFINITY), ATTACK_WIND("attackWind", 0., Double.POSITIVE_INFINITY), ATTACK_EARTH("attackEarth", 0., Double.POSITIVE_INFINITY), ATTACK_HOLY("attackHoly", 0., Double.POSITIVE_INFINITY), ATTACK_UNHOLY("attackUnholy", 0., Double.POSITIVE_INFINITY),

	SWORD_WPN_VULNERABILITY("swordWpnVuln", 10., 200., 100.), DUAL_WPN_VULNERABILITY("dualWpnVuln", 10., 200., 100.), BLUNT_WPN_VULNERABILITY("bluntWpnVuln", 10., 200., 100.), DAGGER_WPN_VULNERABILITY("daggerWpnVuln", 10., 200., 100.), BOW_WPN_VULNERABILITY("bowWpnVuln", 10., 200., 100.), CROSSBOW_WPN_VULNERABILITY("crossbowWpnVuln", 10., 200., 100.), POLE_WPN_VULNERABILITY("poleWpnVuln", 10., 200., 100.), FIST_WPN_VULNERABILITY("fistWpnVuln", 10., 200., 100.),

	ABSORB_DAMAGE_PERCENT("absorbDam", 0., 100.), ABSORB_DAMAGEMP_PERCENT("absorbDamMp", 0., 100.),

	TRANSFER_TO_SUMMON_DAMAGE_PERCENT("transferPetDam", 0., 100.), TRANSFER_TO_EFFECTOR_DAMAGE_PERCENT("transferToEffectorDam", 0., 100.), TRANSFER_TO_MP_DAMAGE_PERCENT("transferToMpDam", 0., 100.),

	REFLECT_AND_BLOCK_DAMAGE_CHANCE("reflectAndBlockDam", 0., 100.), REFLECT_AND_BLOCK_PSKILL_DAMAGE_CHANCE("reflectAndBlockPSkillDam", 0., 100.), REFLECT_AND_BLOCK_MSKILL_DAMAGE_CHANCE("reflectAndBlockMSkillDam", 0., 100.),

	REFLECT_DAMAGE_PERCENT("reflectDam", 0., 100.), REFLECT_PSKILL_DAMAGE_PERCENT("reflectPSkillDam", 0., 100.), REFLECT_MSKILL_DAMAGE_PERCENT("reflectMSkillDam", 0., 100.),

	REFLECT_PHYSIC_SKILL("reflectPhysicSkill", 0., 100.), REFLECT_MAGIC_SKILL("reflectMagicSkill", 0., 100.),

	REFLECT_PHYSIC_DEBUFF("reflectPhysicDebuff", 0., 100.), REFLECT_MAGIC_DEBUFF("reflectMagicDebuff", 0., 100.),

	PSKILL_EVASION("pSkillEvasion", 0., 100.), COUNTER_ATTACK("counterAttack", 0., 100.),

	SKILL_POWER("skillPower"), PVP_PHYS_DMG_BONUS("pvpPhysDmgBonus"), PVP_PHYS_SKILL_DMG_BONUS("pvpPhysSkillDmgBonus"), PVP_MAGIC_SKILL_DMG_BONUS("pvpMagicSkillDmgBonus"),

	PVP_PHYS_DEFENCE_BONUS("pvpPhysDefenceBonus"), PVP_PHYS_SKILL_DEFENCE_BONUS("pvpPhysSkillDefenceBonus"), PVP_MAGIC_SKILL_DEFENCE_BONUS("pvpMagicSkillDefenceBonus"),

	HEAL_EFFECTIVNESS("hpEff", 0., 1000.), MANAHEAL_EFFECTIVNESS("mpEff", 0., 1000.), CPHEAL_EFFECTIVNESS("cpEff", 0., 1000.), HEAL_POWER("healPower"), MP_MAGIC_SKILL_CONSUME("mpConsum"), MP_PHYSICAL_SKILL_CONSUME("mpConsumePhysical"), MP_DANCE_SKILL_CONSUME("mpDanceConsume"), MP_USE_BOW("cheapShot"), MP_USE_BOW_CHANCE("cheapShotChance"), SS_USE_BOW("miser"), SS_USE_BOW_CHANCE("miserChance"), SKILL_MASTERY("skillMastery"),

	MAX_LOAD("maxLoad"), MAX_NO_PENALTY_LOAD("maxNoPenaltyLoad"), INVENTORY_LIMIT("inventoryLimit"), STORAGE_LIMIT("storageLimit"), TRADE_LIMIT("tradeLimit"), COMMON_RECIPE_LIMIT("CommonRecipeLimit"), DWARVEN_RECIPE_LIMIT("DwarvenRecipeLimit"), BUFF_LIMIT("buffLimit"), SOULS_LIMIT("soulsLimit"), SOULS_CONSUME_EXP("soulsExp"), TALISMANS_LIMIT("talismansLimit", 0., 6.), CUBICS_LIMIT("cubicsLimit", 0., 3., 1.), CLOAK_SLOT("openCloakSlot", 0., 1.),

	GRADE_EXPERTISE_LEVEL("gradeExpertiseLevel"), EXP("ExpMultiplier"), SP("SpMultiplier"), ADENA_MULTIPLIER("AdenaMultiplier"), REWARD_MULTIPLIER("DropMultiplier");

	public static final int NUM_STATS = values().length;

	private final String _value;
	private double _min;
	private double _max;
	private double _init;

	public String getValue()
	{
		return _value;
	}

	public double getInit()
	{
		return _init;
	}

	private Stats(String s)
	{
		this(s, 0., Double.POSITIVE_INFINITY, 0.);
	}

	private Stats(String s, double min, double max)
	{
		this(s, min, max, 0.);
	}

	private Stats(String s, double min, double max, double init)
	{
		_value = s;
		_min = min;
		_max = max;
		_init = init;
	}

	public double validate(double val)
	{
		if (val < _min)
		{
			return _min;
		}
		if (val > _max)
		{
			return _max;
		}
		return val;
	}

	public static Stats valueOfXml(String name)
	{
		for (Stats s : values())
		{
			if (s.getValue().equals(name))
			{
				return s;
			}
		}

		throw new NoSuchElementException("Unknown name '" + name + "' for enum BaseStats");
	}

	@Override
	public String toString()
	{
		return _value;
	}
}