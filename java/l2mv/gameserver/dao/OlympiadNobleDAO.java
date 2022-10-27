package l2mv.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.base.ClassId;
import l2mv.gameserver.model.entity.olympiad.Olympiad;
import l2mv.gameserver.templates.StatsSet;

public class OlympiadNobleDAO
{
	private static final Logger _log = LoggerFactory.getLogger(OlympiadNobleDAO.class);
	private static final OlympiadNobleDAO _instance = new OlympiadNobleDAO();

	public static final String SELECT_SQL_QUERY = "SELECT char_id, characters.char_name as char_name, class_id, olympiad_points, olympiad_points_past, olympiad_points_past_static, competitions_done, competitions_loose, competitions_win, game_classes_count, game_noclasses_count, game_team_count FROM olympiad_nobles LEFT JOIN characters ON characters.obj_Id = olympiad_nobles.char_id";
	public static final String SELECT_BY_CHAR_ID_SQL_QUERY = "SELECT characters.char_name as char_name, class_id, olympiad_points, olympiad_points_past, olympiad_points_past_static, competitions_done, competitions_loose, competitions_win, game_classes_count, game_noclasses_count, game_team_count FROM olympiad_nobles LEFT JOIN characters ON characters.obj_Id = olympiad_nobles.char_id WHERE olympiad_nobles.char_id = ?";
	public static final String REPLACE_SQL_QUERY = "REPLACE INTO `olympiad_nobles` (`char_id`, `class_id`, `olympiad_points`, `olympiad_points_past`, `olympiad_points_past_static`, `competitions_done`, `competitions_win`, `competitions_loose`, game_classes_count, game_noclasses_count, game_team_count) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
	public static final String OLYMPIAD_GET_HEROS = "SELECT `char_id`, characters.char_name AS char_name FROM `olympiad_nobles` LEFT JOIN characters ON char_id=characters.obj_Id WHERE `class_id` = ? AND `competitions_done` >= ? AND `competitions_win` > 0 ORDER BY `olympiad_points` DESC, `competitions_win` DESC, `competitions_done` DESC";
	public static final String OLYMPIAD_GET_HEROS_SOULHOUND = "SELECT `char_id`, characters.char_name AS char_name FROM `olympiad_nobles` LEFT JOIN characters ON char_id=characters.obj_Id WHERE `class_id` IN (?, 133) AND `competitions_done` >= ? AND `competitions_win` > 0 ORDER BY `olympiad_points` DESC, `competitions_win` DESC, `competitions_done` DESC";
	public static final String GET_EACH_PAST_CLASS_LEADER = "SELECT characters.char_name AS char_name FROM `olympiad_nobles` LEFT JOIN characters ON char_id=characters.obj_Id WHERE `class_id` = ? AND `olympiad_points_past_static` != 0 ORDER BY `olympiad_points_past_static` DESC LIMIT 10";
	public static final String GET_EACH_PAST_CLASS_LEADER_SOULHOUND = "SELECT characters.char_name AS char_name FROM `olympiad_nobles` LEFT JOIN characters ON characters.obj_Id=olympiad_nobles.char_id WHERE `class_id` IN (?, 133) AND `olympiad_points_past_static` != 0 ORDER BY `olympiad_points_past_static` DESC LIMIT 10";
	// public static final String GET_EACH_CURRENT_CLASS_LEADER = "SELECT characters.char_name, olympiad_points FROM `olympiad_nobles` LEFT JOIN characters ON char_id=characters.obj_Id
	// WHERE `class_id` = ? AND `competitions_done` >= ? AND `competitions_win` > 0 ORDER BY `olympiad_points` DESC, `competitions_win` DESC, `competitions_done` DESC LIMIT 10";
	// public static final String GET_EACH_CURRENT_CLASS_LEADER_SOULHOUND = "SELECT characters.char_name, olympiad_points FROM `olympiad_nobles` LEFT JOIN characters ON
	// characters.obj_Id=olympiad_nobles.char_id WHERE `class_id` IN (?, 133) AND `competitions_done` >= ? AND `competitions_win` > 0 ORDER BY `olympiad_points` DESC, `competitions_win`
	// DESC, `competitions_done` DESC LIMIT 10";
	public static final String GET_EACH_CURRENT_CLASS_LEADER = "SELECT characters.char_name, olympiad_points FROM `olympiad_nobles` LEFT JOIN characters ON char_id=characters.obj_Id WHERE `class_id` = ? AND `competitions_done` >= ? AND `competitions_win` > 0 ORDER BY `olympiad_points` DESC LIMIT 10";
	public static final String GET_EACH_CURRENT_CLASS_LEADER_SOULHOUND = "SELECT characters.char_name, olympiad_points FROM `olympiad_nobles` LEFT JOIN characters ON characters.obj_Id=olympiad_nobles.char_id WHERE `class_id` IN (?, 133) AND `competitions_done` >= ? AND `competitions_win` > 0 ORDER BY `olympiad_points` DESC LIMIT 10";
	public static final String GET_ALL_CLASSIFIED_NOBLESS = "SELECT `char_id` FROM `olympiad_nobles` ORDER BY olympiad_points_past_static DESC";
	public static final String OLYMPIAD_CALCULATE_LAST_PERIOD = "UPDATE `olympiad_nobles` SET `olympiad_points_past` = `olympiad_points`, `olympiad_points_past_static` = `olympiad_points` WHERE `competitions_done` >= ?";
	public static final String OLYMPIAD_CLEANUP_NOBLES = "UPDATE `olympiad_nobles` SET `olympiad_points` = ?, `competitions_done` = 0, `competitions_win` = 0, `competitions_loose` = 0, game_classes_count=0, game_noclasses_count=0, game_team_count=0";

	public static OlympiadNobleDAO getInstance()
	{
		return _instance;
	}

	public void select()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(SELECT_SQL_QUERY); ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				int classId = rset.getInt(Olympiad.CLASS_ID);
				if (classId < 88) // Если это не 3-я профа, то исправляем со 2-й на 3-ю.
				{
					for (ClassId id : ClassId.VALUES)
					{
						if (id.level() == 3 && id.getParent(0).getId() == classId)
						{
							classId = id.getId();
							break;
						}
					}
				}

				StatsSet statDat = new StatsSet();
				int charId = rset.getInt(Olympiad.CHAR_ID);
				statDat.set(Olympiad.CLASS_ID, classId);
				statDat.set(Olympiad.CHAR_NAME, rset.getString(Olympiad.CHAR_NAME));
				statDat.set(Olympiad.POINTS, rset.getInt(Olympiad.POINTS));
				statDat.set(Olympiad.POINTS_PAST, rset.getInt(Olympiad.POINTS_PAST));
				statDat.set(Olympiad.POINTS_PAST_STATIC, rset.getInt(Olympiad.POINTS_PAST_STATIC));
				statDat.set(Olympiad.COMP_DONE, rset.getInt(Olympiad.COMP_DONE));
				statDat.set(Olympiad.COMP_WIN, rset.getInt(Olympiad.COMP_WIN));
				statDat.set(Olympiad.COMP_LOOSE, rset.getInt(Olympiad.COMP_LOOSE));
				statDat.set(Olympiad.GAME_CLASSES_COUNT, rset.getInt(Olympiad.GAME_CLASSES_COUNT));
				statDat.set(Olympiad.GAME_NOCLASSES_COUNT, rset.getInt(Olympiad.GAME_NOCLASSES_COUNT));
				statDat.set(Olympiad.GAME_TEAM_COUNT, rset.getInt(Olympiad.GAME_TEAM_COUNT));

				Olympiad._nobles.put(charId, statDat);
			}
		}
		catch (SQLException e)
		{
			_log.error("OlympiadNobleDAO: select():", e);
		}
	}

	public void replace(int nobleId)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(REPLACE_SQL_QUERY);)
		{
			StatsSet nobleInfo = Olympiad._nobles.get(nobleId);

			statement.setInt(1, nobleId);
			statement.setInt(2, nobleInfo.getInteger(Olympiad.CLASS_ID));
			statement.setInt(3, nobleInfo.getInteger(Olympiad.POINTS));
			statement.setInt(4, nobleInfo.getInteger(Olympiad.POINTS_PAST));
			statement.setInt(5, nobleInfo.getInteger(Olympiad.POINTS_PAST_STATIC));
			statement.setInt(6, nobleInfo.getInteger(Olympiad.COMP_DONE));
			statement.setInt(7, nobleInfo.getInteger(Olympiad.COMP_WIN));
			statement.setInt(8, nobleInfo.getInteger(Olympiad.COMP_LOOSE));
			statement.setInt(9, nobleInfo.getInteger(Olympiad.GAME_CLASSES_COUNT));
			statement.setInt(10, nobleInfo.getInteger(Olympiad.GAME_NOCLASSES_COUNT));
			statement.setInt(11, nobleInfo.getInteger(Olympiad.GAME_TEAM_COUNT));
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("OlympiadNobleDAO: replace(int): " + nobleId, e);
		}
	}

	public static void load(int charId)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(SELECT_BY_CHAR_ID_SQL_QUERY))
		{
			statement.setInt(1, charId);
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					int classId = rset.getInt("class_id");
					if (classId < 88)
					{
						for (ClassId id : ClassId.VALUES)
						{
							if (id.level() == 3 && id.getParent(0).getId() == classId)
							{
								classId = id.getId();
								break;
							}
						}
					}

					final StatsSet statDat = new StatsSet();
					statDat.set("class_id", classId);
					statDat.set("char_name", rset.getString("char_name"));
					statDat.set("olympiad_points", rset.getInt("olympiad_points"));
					statDat.set("olympiad_points_past", rset.getInt("olympiad_points_past"));
					statDat.set("olympiad_points_past_static", rset.getInt("olympiad_points_past_static"));
					statDat.set("competitions_done", rset.getInt("competitions_done"));
					statDat.set("competitions_win", rset.getInt("competitions_win"));
					statDat.set("competitions_loose", rset.getInt("competitions_loose"));
					statDat.set("game_classes_count", rset.getInt("game_classes_count"));
					statDat.set("game_noclasses_count", rset.getInt("game_noclasses_count"));
					statDat.set("game_team_count", rset.getInt("game_team_count"));
					Olympiad._nobles.put(charId, statDat);
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("OlympiadNobleDAO: select():", e);
		}
	}
}
