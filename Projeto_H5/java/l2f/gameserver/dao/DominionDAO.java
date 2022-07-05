package l2f.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.dao.JdbcEntityState;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.model.entity.residence.Dominion;

/**
 * @author VISTALL
 * @date 18:10/15.04.2011
 */
public class DominionDAO
{
	private static final Logger _log = LoggerFactory.getLogger(DominionDAO.class);
	private static final DominionDAO _instance = new DominionDAO();

	public static final String SELECT_SQL_QUERY = "SELECT lord_object_id, wards FROM dominion WHERE id=?";
	public static final String UPDATE_SQL_QUERY = "UPDATE dominion SET lord_object_id=?, wards=? WHERE id=?";

	public static DominionDAO getInstance()
	{
		return _instance;
	}

	public void select(Dominion dominion)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(SELECT_SQL_QUERY))
		{
			statement.setInt(1, dominion.getId());
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					dominion.setLordObjectId(rset.getInt("lord_object_id"));

					String flags = rset.getString("wards");
					if (!flags.isEmpty())
					{
						String[] values = flags.split(";");
						for (int i = 0; i < values.length; i++)
						{
							dominion.addFlag(Integer.parseInt(values[i]));
						}
					}
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("Dominion.loadData(): ", e);
		}
	}

	public void update(Dominion residence)
	{
		if (!residence.getJdbcState().isUpdatable())
		{
			return;
		}

		residence.setJdbcState(JdbcEntityState.STORED);
		update0(residence);
	}

	private void update0(Dominion dominion)
	{
		StringBuilder builder = new StringBuilder();
		int[] flags = dominion.getFlags();
		if (flags.length > 0)
		{
			for (int flag : flags)
			{
				builder.append(flag).append(';');
			}
		}

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(UPDATE_SQL_QUERY))
		{
			statement.setInt(1, dominion.getLordObjectId());
			statement.setString(2, builder.toString());
			statement.setInt(3, dominion.getId());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.warn("DominionDAO#update0(Dominion): " + e, e);
		}
	}
}
