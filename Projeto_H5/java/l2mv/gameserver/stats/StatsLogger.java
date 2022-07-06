package l2mv.gameserver.stats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.utils.TimeUtils;

public class StatsLogger
{
	private static final Logger LOGGER = LoggerFactory.getLogger(StatsLogger.class);

	private final Collection<SkillStat> statsToAdd = new CopyOnWriteArrayList<>();

	private StatsLogger()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new LogStatThread(), TimeUtils.MINUTE_IN_MILLIS, TimeUtils.MINUTE_IN_MILLIS);
	}

	public void addNewStat(Skill skill, Player attacker, Player defender, double base, double finalChance, double statMod, double mAtkMod, double deltaMod, double debuffMod, double resMod, double elementMod)
	{
		statsToAdd.add(new SkillStat(skill, attacker, defender, base, finalChance, statMod, mAtkMod, deltaMod, debuffMod, resMod, elementMod));
	}

	private void clearStats()
	{
		statsToAdd.clear();
	}

	private Collection<SkillStat> getStatsToAdd()
	{
		return statsToAdd;
	}

	private static class SkillStat
	{
		public final String skillName;
		public final int skillId;
		public final int attackerLevel;
		public final int defenderLevel;
		public final String attackerClass;
		public final String defenderClass;
		public final double base;
		public final double finalChance;
		public final double statMod;
		public final double mAtkMod;
		public final double deltaMod;
		public final double debuffMod;
		public final double resMod;
		public final double elementMod;

		private SkillStat(Skill skill, Player attacker, Player defender, double base, double finalChance, double statMod, double mAtkMod, double deltaMod, double debuffMod, double resMod, double elementMod)
		{
			skillName = skill.getName();
			skillId = skill.getId();
			attackerLevel = attacker.getLevel();
			defenderLevel = defender.getLevel();
			attackerClass = attacker.getClassId().name();
			defenderClass = defender.getClassId().name();
			this.base = base;
			this.finalChance = finalChance;
			this.statMod = statMod;
			this.mAtkMod = mAtkMod;
			this.deltaMod = deltaMod;
			this.debuffMod = debuffMod;
			this.resMod = resMod;
			this.elementMod = elementMod;
		}
	}

	private static class LogStatThread implements Runnable
	{
		@Override
		public void run()
		{
			Collection<SkillStat> stats = getInstance().getStatsToAdd();
			if (Config.ALLOW_SKILLS_STATS_LOGGER && stats.size() > 1)
			{
				StringBuilder query = new StringBuilder();
				query.append("INSERT INTO skill_chance_logger VALUES ");
				for (SkillStat stat : stats)
				{
					query.append("('").append(stat.skillName).append("',");
					query.append(stat.skillId).append(',');
					query.append(stat.attackerLevel).append(',');
					query.append(stat.defenderLevel).append(',');
					query.append('\'').append(stat.attackerClass).append("',");
					query.append('\'').append(stat.defenderClass).append("',");
					query.append(stat.base).append(',');
					query.append(stat.finalChance).append(',');
					query.append(stat.statMod).append(',');
					query.append(stat.mAtkMod).append(',');
					query.append(stat.deltaMod).append(',');
					query.append(stat.debuffMod).append(',');
					query.append(stat.resMod).append(',');
					query.append(stat.elementMod).append("),");
				}
				String finalQuery = query.substring(0, query.length() - 1);

				try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(finalQuery))
				{
					statement.executeUpdate();
				}
				catch (SQLException e)
				{
					// LOGGER.error("Error while logging Skill Chance Stats. Query: "+finalQuery+" Error: ", e);
				}

				getInstance().clearStats();
			}
		}
	}

	public static StatsLogger getInstance()
	{
		return StatsLoggerHolder.instance;
	}

	private static class StatsLoggerHolder
	{
		private static final StatsLogger instance = new StatsLogger();
	}
}
