package l2mv.gameserver.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.trove.map.hash.TIntObjectHashMap;
import l2mv.commons.dbutils.DbUtils;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.SkillLearn;
import l2mv.gameserver.model.Summon;

public class PetSkillsTable
{
	private static final Logger _log = LoggerFactory.getLogger(PetSkillsTable.class);
	private TIntObjectHashMap<List<SkillLearn>> _skillTrees = new TIntObjectHashMap<List<SkillLearn>>();

	private static PetSkillsTable _instance = new PetSkillsTable();

	public static PetSkillsTable getInstance()
	{
		return _instance;
	}

	public void reload()
	{
		_instance = new PetSkillsTable();
	}

	private PetSkillsTable()
	{
		load();
	}

	private void load()
	{
		int npcId = 0;
		int count = 0;
		int id = 0;
		int lvl = 0;
		int minLvl = 0;

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM pets_skills ORDER BY templateId");
			rset = statement.executeQuery();

			while (rset.next())
			{
				npcId = rset.getInt("templateId");
				id = rset.getInt("skillId");
				lvl = rset.getInt("skillLvl");
				minLvl = rset.getInt("minLvl");

				List<SkillLearn> list = _skillTrees.get(npcId);
				if (list == null)
				{
					_skillTrees.put(npcId, (list = new ArrayList<SkillLearn>()));
				}

				SkillLearn skillLearn = new SkillLearn(id, lvl, minLvl, 0, 0, 0, false);
				list.add(skillLearn);
				count++;
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while creating pet skill tree (Pet ID " + npcId + ')', e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		_log.info("PetSkillsTable: Loaded " + count + " skills.");
	}

	public int getAvailableLevel(Summon cha, int skillId)
	{
		List<SkillLearn> skills = _skillTrees.get(cha.getNpcId());
		if (skills == null)
		{
			return 0;
		}

		int lvl = 0;
		for (SkillLearn temp : skills)
		{
			if (temp.getId() != skillId)
			{
				continue;
			}
			if (temp.getLevel() == 0)
			{
				if (cha.getLevel() < 70)
				{
					lvl = cha.getLevel() / 10;
					if (lvl <= 0)
					{
						lvl = 1;
					}
				}
				else
				{
					lvl = 7 + (cha.getLevel() - 70) / 5;
				}

				// formula usable for skill that have 10 or more skill levels
				int maxLvl = SkillTable.getInstance().getMaxLevel(temp.getId());
				if (lvl > maxLvl)
				{
					lvl = maxLvl;
				}
				break;
			}
			else if (temp.getMinLevel() <= cha.getLevel())
			{
				if (temp.getLevel() > lvl)
				{
					lvl = temp.getLevel();
				}
			}
		}
		return lvl;
	}
}