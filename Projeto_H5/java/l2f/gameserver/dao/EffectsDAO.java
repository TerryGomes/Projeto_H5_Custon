package l2f.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.dbutils.DbUtils;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.model.Effect;
import l2f.gameserver.model.Playable;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.instances.SummonInstance;
import l2f.gameserver.skills.EffectType;
import l2f.gameserver.skills.effects.EffectTemplate;
import l2f.gameserver.stats.Env;
import l2f.gameserver.tables.SkillTable;
import l2f.gameserver.utils.BatchStatement;
import l2f.gameserver.utils.SqlBatch;

public class EffectsDAO
{
	private static final int SUMMON_SKILL_OFFSET = 100000;
	private static final Logger _log = LoggerFactory.getLogger(EffectsDAO.class);
	private static final EffectsDAO _instance = new EffectsDAO();

	EffectsDAO()
	{
		//
	}

	public static EffectsDAO getInstance()
	{
		return _instance;
	}

	public void restoreEffects(Playable playable, boolean heal, double healToHp, double healToCp, double healToMp)
	{
		int objectId;
		int id;
		if (playable.isPlayer())
		{
			objectId = playable.getObjectId();
			id = ((Player) playable).getActiveClassId();
		}
		else if (playable.isSummon())
		{
			objectId = playable.getPlayer().getObjectId();
			id = ((SummonInstance) playable).getEffectIdentifier() + SUMMON_SKILL_OFFSET;
		}
		else
		{
			return;
		}

		if (playable.getPlayer().isInOlympiadMode() || playable.getPlayer().isInFightClub())
		{
			if (heal)
			{
				heal(playable, healToHp, healToCp, healToMp);
			}
			return;
		}

		final List<Effect> effectsToRestore = new LinkedList<>();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT `skill_id`,`skill_level`,`effect_count`,`effect_cur_time`,`duration` FROM `character_effects_save` WHERE `object_id`=? AND `id`=? ORDER BY `order` ASC");
			statement.setInt(1, objectId);
			statement.setInt(2, id);
			rset = statement.executeQuery();
			while (rset.next())
			{
				int skillId = rset.getInt("skill_id");
				int skillLvl = rset.getInt("skill_level");
				int effectCount = rset.getInt("effect_count");
				long effectCurTime = rset.getLong("effect_cur_time");
				long duration = rset.getLong("duration");

				Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl);
				if (skill == null)
				{
					continue;
				}

				for (EffectTemplate et : skill.getEffectTemplates())
				{
					if (et == null)
					{
						continue;
					}
					Env env = new Env(playable, playable, skill);
					Effect effect = et.getEffect(env);
					if (effect == null || effect.isOneTime())
					{
						continue;
					}

					effect.setCount(effectCount);
					effect.setPeriod(effectCount == 1 ? duration - effectCurTime : duration);

					effectsToRestore.add(effect);
				}
			}

			DbUtils.closeQuietly(statement, rset);

			statement = con.prepareStatement("DELETE FROM character_effects_save WHERE object_id = ? AND id=?");
			statement.setInt(1, objectId);
			statement.setInt(2, id);
			statement.execute();
			DbUtils.close(statement);
		}
		catch (SQLException e)
		{
			_log.error("Could not restore active effects data!", e);
		}
		finally
		{
			DbUtils.closeQuietly(con);
		}

		ThreadPoolManager.getInstance().execute(new Runnable()
		{
			@Override
			public void run()
			{
				for (Effect e : effectsToRestore)
				{
					e.schedule();

					try
					{
						Thread.sleep(5);
					}
					catch (InterruptedException e1)
					{
						e1.printStackTrace();
					}
				}

				if (heal)
				{
					heal(playable, healToHp, healToCp, healToMp);
				}
			}
		});
	}

	public static void deletePossiblePetEffects(Player player)
	{
		final int playerObjectId = player.getObjectId();
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = BatchStatement.createPreparedStatement(con, "DELETE FROM character_effects_save WHERE object_id = ? AND id=?"))
		{
			for (Skill skill : player.getAllSkills())
			{
				if (skill.getSkillType() == Skill.SkillType.SUMMON || skill.getSkillType() == Skill.SkillType.PET_SUMMON)
				{
					statement.setInt(1, playerObjectId);
					statement.setInt(2, 100000 + skill.getId());
					statement.addBatch();
				}
			}
			statement.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			_log.error("Could not delete possible Pet Effects of " + player.toString() + "!", e);
		}
	}

	public void deleteEffects(int objectId, int skillId)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("DELETE FROM character_effects_save WHERE object_id = ? AND id=?"))
		{
			statement.setInt(1, objectId);
			statement.setInt(2, SUMMON_SKILL_OFFSET + skillId);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("Could not delete effects active effects data!" + e, e);
		}
	}

	private void heal(Playable playable, double hp, double cp, double mp)
	{
		if (!playable.isPlayer())
		{
			hp = playable.getMaxHp();
			cp = playable.getMaxCp();
			mp = playable.getMaxMp();
		}
		playable.setCurrentHpMp(hp, mp);
		playable.setCurrentCp(cp);
	}

	public void insert(Playable playable)
	{
		int objectId, id;
		if (playable.isPlayer())
		{
			objectId = playable.getObjectId();
			id = ((Player) playable).getActiveClassId();
		}
		else if (playable.isSummon())
		{
			objectId = playable.getPlayer().getObjectId();
			id = ((SummonInstance) playable).getEffectIdentifier() + SUMMON_SKILL_OFFSET;
		}
		else
		{
			return;
		}

		List<Effect> effects = playable.getEffectList().getAllEffects();
		if (effects.isEmpty())
		{
			return;
		}

		Connection con = null;
		Statement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();

			int order = 0;
			SqlBatch b = new SqlBatch("INSERT IGNORE INTO `character_effects_save` (`object_id`,`skill_id`,`skill_level`,`effect_count`,`effect_cur_time`,`duration`,`order`,`id`) VALUES");

			StringBuilder sb;
			for (Effect effect : effects)
			{
				if (effect != null && effect.isInUse() && !effect.getSkill().isToggle() && effect.getEffectType() != EffectType.HealOverTime && effect.getEffectType() != EffectType.CombatPointHealOverTime)
				{
					// Synerge - Summons should not store debuffs, only buffs
					if (playable.isSummon() && effect.getSkill().isOffensive())
					{
						continue;
					}

					if (effect.isSaveable())
					{
						sb = new StringBuilder("(");
						sb.append(objectId).append(",");
						sb.append(effect.getSkill().getId()).append(",");
						sb.append(effect.getSkill().getLevel()).append(",");
						sb.append(effect.getCount()).append(",");
						sb.append(effect.getTime()).append(",");
						sb.append(effect.getPeriod()).append(",");
						sb.append(order).append(",");
						sb.append(id).append(")");
						b.write(sb.toString());
					}
					while ((effect = effect.getNext()) != null && effect.isSaveable())
					{
						sb = new StringBuilder("(");
						sb.append(objectId).append(",");
						sb.append(effect.getSkill().getId()).append(",");
						sb.append(effect.getSkill().getLevel()).append(",");
						sb.append(effect.getCount()).append(",");
						sb.append(effect.getTime()).append(",");
						sb.append(effect.getPeriod()).append(",");
						sb.append(order).append(",");
						sb.append(id).append(")");
						b.write(sb.toString());
					}
					order++;
				}
			}

			if (!b.isEmpty())
			{
				statement.executeUpdate(b.close());
			}
		}
		catch (SQLException e)
		{
			_log.error("Could not store active effects data!", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}
