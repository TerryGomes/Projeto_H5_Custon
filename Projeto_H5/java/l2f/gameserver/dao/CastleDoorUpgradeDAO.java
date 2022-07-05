package l2f.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.database.DatabaseFactory;

/**
 * @author VISTALL
 * @date 15:39/10.03.2011
 */
public class CastleDoorUpgradeDAO
{
	private static final CastleDoorUpgradeDAO _instance = new CastleDoorUpgradeDAO();
	private static final Logger _log = LoggerFactory.getLogger(CastleDoorUpgradeDAO.class);

	public static final String SELECT_SQL_QUERY = "SELECT hp FROM castle_door_upgrade WHERE door_id=?";
	public static final String REPLACE_SQL_QUERY = "REPLACE INTO castle_door_upgrade (door_id, hp) VALUES (?,?)";
	public static final String DELETE_SQL_QUERY = "DELETE FROM castle_door_upgrade WHERE door_id=?";

	public static CastleDoorUpgradeDAO getInstance()
	{
		return _instance;
	}

	public int load(int doorId)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(SELECT_SQL_QUERY))
		{
			statement.setInt(1, doorId);

			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					return rset.getInt("hp");
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("CastleDoorUpgradeDAO:load(int): ", e);
		}

		return 0;
	}

	public void insert(int uId, int val)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(REPLACE_SQL_QUERY))
		{
			statement.setInt(1, uId);
			statement.setInt(2, val);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("CastleDoorUpgradeDAO:insert(int, int): ", e);
		}
	}

	public void delete(int uId)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(DELETE_SQL_QUERY))
		{
			statement.setInt(1, uId);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("CastleDoorUpgradeDAO:delete(int): ", e);
		}
	}
}
