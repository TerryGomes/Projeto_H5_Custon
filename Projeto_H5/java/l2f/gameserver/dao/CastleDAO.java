package l2f.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.dao.JdbcEntityState;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.model.entity.residence.Castle;

public class CastleDAO
{
	private static final Logger _log = LoggerFactory.getLogger(CastleDAO.class);
	private static final CastleDAO _instance = new CastleDAO();

	public static final String SELECT_SQL_QUERY = "SELECT tax_percent, treasury, reward_count, siege_date, last_siege_date, own_date FROM castle WHERE id=? LIMIT 1";
	public static final String UPDATE_SQL_QUERY = "UPDATE castle SET tax_percent=?, treasury=?, reward_count=?, siege_date=?, last_siege_date=?, own_date=? WHERE id=?";

	public static CastleDAO getInstance()
	{
		return _instance;
	}

	public void select(Castle castle)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(SELECT_SQL_QUERY))
		{
			statement.setInt(1, castle.getId());

			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					castle.setTaxPercent(rset.getInt("tax_percent"));
					castle.setTreasury(rset.getLong("treasury"));
					castle.setRewardCount(rset.getInt("reward_count"));
					castle.getSiegeDate().setTimeInMillis(rset.getLong("siege_date"));
					castle.getLastSiegeDate().setTimeInMillis(rset.getLong("last_siege_date"));
					castle.getOwnDate().setTimeInMillis(rset.getLong("own_date"));
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("CastleDAO.select(Castle):", e);
		}
	}

	public void update(Castle residence)
	{
		if (!residence.getJdbcState().isUpdatable())
		{
			return;
		}

		residence.setJdbcState(JdbcEntityState.STORED);
		update0(residence);
	}

	private void update0(Castle castle)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(UPDATE_SQL_QUERY))
		{
			statement.setInt(1, castle.getTaxPercent(false));
			statement.setLong(2, castle.getTreasury());
			statement.setInt(3, castle.getRewardCount());
			statement.setLong(4, castle.getSiegeDate().getTimeInMillis());
			statement.setLong(5, castle.getLastSiegeDate().getTimeInMillis());
			statement.setLong(6, castle.getOwnDate().getTimeInMillis());
			statement.setInt(7, castle.getId());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.warn("CastleDAO#update0(Castle): ", e);
		}
	}
}
