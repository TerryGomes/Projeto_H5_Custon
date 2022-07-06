package l2mv.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.database.DatabaseFactory;

public class CaptchaPunishmentDAO
{
	private static final Logger _log = LoggerFactory.getLogger(CaptchaPunishmentDAO.class);
	private static final CaptchaPunishmentDAO _instance = new CaptchaPunishmentDAO();

	private static final String SELECT_SQL_QUERY = "SELECT count FROM report_data WHERE obj_id = ?";
	private static final String UPDATE_SQL_QUERY = "UPDATE report_data SET count = ? WHERE obj_id = ?";
	private static final String INSERT_SQL_QUERY = "INSERT INTO report_data (obj_id,count) VALUES(?,?)";

	public int loadReportCount(String name)
	{
		int obj = CharacterDAO.getInstance().getObjectIdByName(name);
		if (obj == 0)
		{
			_log.info("CaptchaPunishmentDAO.loadReportCount(name): cannot find character obj_id by name -> " + name);
			return -1;
		}

		int count = 0;
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(SELECT_SQL_QUERY))
		{
			statement.setInt(1, obj);
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					count = rset.getInt("count");
				}
			}
		}
		catch (SQLException e)
		{
			_log.info("CaptchaPunishmentDAO.loadReportCount(name): " + e, e);
		}

		return count;
	}

	public synchronized void updateReportCount(String name, int count)
	{
		int obj = CharacterDAO.getInstance().getObjectIdByName(name);
		if (obj == 0)
		{
			_log.info("CaptchaPunishmentDAO.updateReportCount(name): don't find character obj by name " + name);
			return;
		}

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(UPDATE_SQL_QUERY))
		{
			statement.setInt(1, count);
			statement.setInt(2, obj);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.info("AccountReportDAO.updatePoints(name,count): ", e);
		}
	}

	public void insertReportCount(String name, int count)
	{
		int obj = CharacterDAO.getInstance().getObjectIdByName(name);
		if (obj == 0)
		{
			_log.info("CaptchaPunishmentDAO.insertReportCount(name,count): don't find character obj by name " + name);
			return;
		}

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(INSERT_SQL_QUERY))
		{
			statement.setInt(1, obj);
			statement.setInt(2, count);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.info("AccountReportDAO.insertReportCount(name,count): ", e);
		}
	}

	public static CaptchaPunishmentDAO getInstance()
	{
		return _instance;
	}
}
