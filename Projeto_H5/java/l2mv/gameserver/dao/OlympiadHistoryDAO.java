package l2mv.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.entity.olympiad.OlympiadHistory;

public class OlympiadHistoryDAO
{
	private static final Logger _log = LoggerFactory.getLogger(OlympiadHistoryDAO.class);
	private static final OlympiadHistoryDAO _instance = new OlympiadHistoryDAO();
	public static final String SELECT_SQL_QUERY = "SELECT * FROM olympiad_history ORDER BY game_start_time";
	public static final String DELETE_SQL_QUERY = "DELETE FROM olympiad_history WHERE old=1";
	public static final String UPDATE_SQL_QUERY = "UPDATE olympiad_history SET old=1";
	public static final String INSERT_SQL_QUERY = "INSERT INTO olympiad_history(object_id_1, object_id_2, class_id_1, class_id_2, name_1, name_2, game_start_time, game_time, game_status, game_type, old) VALUES (?,?,?,?,?,?,?,?,?,?,?)";

	public static OlympiadHistoryDAO getInstance()
	{
		return _instance;
	}

	public Map<Boolean, List<OlympiadHistory>> select()
	{
		Map<Boolean, List<OlympiadHistory>> map = null;

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(SELECT_SQL_QUERY))
		{
			try (ResultSet rset = statement.executeQuery())
			{
				map = new HashMap<>(2);
				map.put(Boolean.TRUE, new ArrayList<OlympiadHistory>());
				map.put(Boolean.FALSE, new ArrayList<OlympiadHistory>());

				while (rset.next())
				{
					int objectId1 = rset.getInt("object_id_1");
					int objectId2 = rset.getInt("object_id_2");

					int classId1 = rset.getInt("class_id_1");
					int classId2 = rset.getInt("class_id_2");

					String name1 = rset.getString("name_1");
					String name2 = rset.getString("name_2");

					boolean old = rset.getBoolean("old");

					OlympiadHistory history = new OlympiadHistory(objectId1, objectId2, classId1, classId2, name1, name2, rset.getLong("game_start_time"), rset.getInt("game_time"), rset.getInt("game_status"), rset.getInt("game_type"));

					map.get(old).add(history);
				}
			}
		}
		catch (SQLException e)
		{
			map = Collections.emptyMap();
			_log.error("OlympiadHistoryDAO: select(): " + e, e);
		}
		return map;
	}

	public void insert(OlympiadHistory history)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(INSERT_SQL_QUERY))
		{
			statement.setInt(1, history.getObjectId1());
			statement.setInt(2, history.getObjectId2());
			statement.setInt(3, history.getClassId1());
			statement.setInt(4, history.getClassId2());
			statement.setString(5, history.getName1());
			statement.setString(6, history.getName2());
			statement.setLong(7, history.getGameStartTime());
			statement.setInt(8, history.getGameTime());
			statement.setInt(9, history.getGameStatus());
			statement.setInt(10, history.getGameType());
			statement.setInt(11, 0);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("OlympiadHistoryDAO: insert(OlympiadHistory): " + e, e);
		}
	}

	public void switchData()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			try (PreparedStatement statement = con.prepareStatement(DELETE_SQL_QUERY))
			{
				statement.execute();
			}
			try (PreparedStatement statement = con.prepareStatement(UPDATE_SQL_QUERY))
			{
				statement.execute();
			}
		}
		catch (SQLException e)
		{
			_log.error("OlympiadHistoryDAO: select(): " + e, e);
		}
	}
}
