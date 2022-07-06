package l2mv.gameserver.model.base;

import l2mv.gameserver.stats.Env;
import l2mv.gameserver.stats.Stats;

public enum SkillTrait
{
	NONE, BLEED
	{
		@Override
		public final double calcVuln(Env env)
		{
			return env.target.calcStat(Stats.BLEED_RESIST, 100., env.character, env.skill) - 100.;
		}

		@Override
		public final double calcProf(Env env)
		{
			return env.character.calcStat(Stats.BLEED_POWER, 100., env.target, env.skill) - 100.;
		}
	},
	BOSS, DEATH, DERANGEMENT
	{
		@Override
		public final double calcVuln(Env env)
		{
			return env.target.calcStat(Stats.MENTAL_RESIST, 100., env.character, env.skill) - 100.;
		}

		@Override
		public final double calcProf(Env env)
		{
			return env.character.calcStat(Stats.MENTAL_POWER, 100., env.target, env.skill) - 100. + calcEnchantMod(env);
			// return Math.min(40., env.character.calcStat(Stats.MENTAL_POWER, 100., env.target, env.skill) - 100. + calcEnchantMod(env));
		}
	},
	ETC, GUST, HOLD
	{
		@Override
		public final double calcVuln(Env env)
		{
			return env.target.calcStat(Stats.ROOT_RESIST, 100., env.character, env.skill) - 100.;
		}

		@Override
		public final double calcProf(Env env)
		{
			return env.character.calcStat(Stats.ROOT_POWER, 100., env.target, env.skill) - 100.;
		}
	},
	PARALYZE
	{
		@Override
		public final double calcVuln(Env env)
		{
			return env.target.calcStat(Stats.PARALYZE_RESIST, 100., env.character, env.skill) - 100.;
		}

		@Override
		public final double calcProf(Env env)
		{
			return env.character.calcStat(Stats.PARALYZE_POWER, 100., env.target, env.skill) - 100.;
		}
	},
	PHYSICAL_BLOCKADE, POISON
	{
		@Override
		public final double calcVuln(Env env)
		{
			return env.target.calcStat(Stats.POISON_RESIST, 100., env.character, env.skill) - 100.;
		}

		@Override
		public final double calcProf(Env env)
		{
			return env.character.calcStat(Stats.POISON_POWER, 100., env.target, env.skill) - 100.;
		}
	},
	SHOCK
	{
		@Override
		public final double calcVuln(Env env)
		{
			return env.target.calcStat(Stats.STUN_RESIST, 100., env.character, env.skill) - 100.;
		}

		@Override
		public final double calcProf(Env env)
		{
			return env.character.calcStat(Stats.STUN_POWER, 100., env.target, env.skill) - 100.;
			// return Math.min(40., env.character.calcStat(Stats.STUN_POWER, env.target, env.skill) + calcEnchantMod(env));
		}
	},
	SLEEP
	{
		@Override
		public final double calcVuln(Env env)
		{
			return env.target.calcStat(Stats.SLEEP_RESIST, 100., env.character, env.skill) - 100.;
		}

		@Override
		public final double calcProf(Env env)
		{
			return env.character.calcStat(Stats.SLEEP_POWER, 100., env.target, env.skill) - 100.;
		}
	},
	VALAKAS;

	public double calcVuln(Env env)
	{
		return 0;
	}

	public double calcProf(Env env)
	{
		return 0;
	}

	/*
	 * public double calcResistMod(Env env)
	 * {
	 * final double vulnMod = calcVuln(env);
	 * final double profMod = calcProf(env);
	 * final double maxResist = 90. + Math.max(calcEnchantMod(env), profMod * 0.85);
	 * return (maxResist - vulnMod) / 60.;
	 * }
	 */
	public static double calcEnchantMod(Env env)
	{
		int enchantLevel = env.skill.getDisplayLevel();
		if (enchantLevel <= 100)
		{
			return 0;
		}
		enchantLevel = enchantLevel % 100;
		return env.skill.getEnchantLevelCount() == 15 ? enchantLevel * 2 : enchantLevel;
	}
}