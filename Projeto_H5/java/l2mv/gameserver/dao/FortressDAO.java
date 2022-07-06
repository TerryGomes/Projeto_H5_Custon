package l2mv.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dao.JdbcEntityState;
import l2mv.commons.dbutils.DbUtils;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.entity.residence.Fortress;

/**
 * @author VISTALL
 * @date 18:10/15.04.2011
 */
public class FortressDAO
{
	private static final Logger _log = LoggerFactory.getLogger(FortressDAO.class);
	private static final FortressDAO _instance = new FortressDAO();

	public static final String SELECT_SQL_QUERY = "SELECT * FROM fortress WHERE id = ?";
	public static final String UPDATE_SQL_QUERY = "UPDATE fortress SET castle_id=?, state=?, cycle=?, reward_count=?, paid_cycle=?, supply_count=?, siege_date=?, last_siege_date=?, own_date=?, facility_0=?, facility_1=?, facility_2=?, facility_3=?, facility_4=? WHERE id=?";

	public static FortressDAO getInstance()
	{
		return _instance;
	}

	public void select(Fortress fortress)
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_SQL_QUERY);
			statement.setInt(1, fortress.getId());
			rset = statement.executeQuery();
			if (rset.next())
			{
				fortress.setFortState(rset.getInt("state"), rset.getInt("castle_id"));
				fortress.setCycle(rset.getInt("cycle"));
				fortress.setRewardCount(rset.getInt("reward_count"));
				fortress.setPaidCycle(rset.getInt("paid_cycle"));
				fortress.setSupplyCount(rset.getInt("supply_count"));
				fortress.getSiegeDate().setTimeInMillis(rset.getLong("siege_date"));
				fortress.getLastSiegeDate().setTimeInMillis(rset.getLong("last_siege_date"));
				fortress.getOwnDate().setTimeInMillis(rset.getLong("own_date"));
				for (int i = 0; i < Fortress.FACILITY_MAX; i++)
				{
					fortress.setFacilityLevel(i, rset.getInt("facility_" + i));
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("FortressDAO.select(Fortress):" + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public void update(Fortress fortress)
	{
		if (!fortress.getJdbcState().isUpdatable())
		{
			return;
		}

		fortress.setJdbcState(JdbcEntityState.STORED);
		update0(fortress);
	}

	private void update0(Fortress fortress)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(UPDATE_SQL_QUERY);)
		{
			statement.setInt(1, fortress.getCastleId());
			statement.setInt(2, fortress.getContractState());
			statement.setInt(3, fortress.getCycle());
			statement.setInt(4, fortress.getRewardCount());
			statement.setInt(5, fortress.getPaidCycle());
			statement.setInt(6, fortress.getSupplyCount());
			statement.setLong(7, fortress.getSiegeDate().getTimeInMillis());
			statement.setLong(8, fortress.getLastSiegeDate().getTimeInMillis());
			statement.setLong(9, fortress.getOwnDate().getTimeInMillis());
			for (int i = 0; i < Fortress.FACILITY_MAX; i++)
			{
				statement.setInt(10 + i, fortress.getFacilityLevel(i));
			}
			statement.setInt(10 + Fortress.FACILITY_MAX, fortress.getId());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.warn("FortressDAO#update0(Fortress): " + e, e);
		}
	}
}
