package l2f.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.dao.JdbcEntityState;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.model.entity.residence.ClanHall;

public class ClanHallDAO
{
	private static final Logger _log = LoggerFactory.getLogger(ClanHallDAO.class);
	private static final ClanHallDAO _instance = new ClanHallDAO();

	public static final String SELECT_SQL_QUERY = "SELECT siege_date, own_date, last_siege_date, auction_desc, auction_length, auction_min_bid, cycle, paid_cycle FROM clanhall WHERE id = ?";
	public static final String UPDATE_SQL_QUERY = "UPDATE clanhall SET siege_date=?, last_siege_date=?, own_date=?, auction_desc=?, auction_length=?, auction_min_bid=?, cycle=?, paid_cycle=? WHERE id=?";

	public static ClanHallDAO getInstance()
	{
		return _instance;
	}

	public void select(ClanHall clanHall)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(SELECT_SQL_QUERY);)
		{
			statement.setInt(1, clanHall.getId());
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					clanHall.getSiegeDate().setTimeInMillis(rset.getLong("siege_date"));
					clanHall.getLastSiegeDate().setTimeInMillis(rset.getLong("last_siege_date"));
					clanHall.getOwnDate().setTimeInMillis(rset.getLong("own_date"));
					//
					clanHall.setAuctionLength(rset.getInt("auction_length"));
					clanHall.setAuctionMinBid(rset.getLong("auction_min_bid"));
					clanHall.setAuctionDescription(rset.getString("auction_desc"));
					//
					clanHall.setCycle(rset.getInt("cycle"));
					clanHall.setPaidCycle(rset.getInt("paid_cycle"));
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("ClanHallDAO.select(ClanHall):", e);
		}
	}

	public void update(ClanHall c)
	{
		if (!c.getJdbcState().isUpdatable())
		{
			return;
		}

		c.setJdbcState(JdbcEntityState.STORED);
		update0(c);
	}

	private void update0(ClanHall c)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(UPDATE_SQL_QUERY))
		{
			statement.setLong(1, c.getSiegeDate().getTimeInMillis());
			statement.setLong(2, c.getLastSiegeDate().getTimeInMillis());
			statement.setLong(3, c.getOwnDate().getTimeInMillis());
			statement.setString(4, c.getAuctionDescription());
			statement.setInt(5, c.getAuctionLength());
			statement.setLong(6, c.getAuctionMinBid());
			statement.setInt(7, c.getCycle());
			statement.setInt(8, c.getPaidCycle());
			statement.setInt(9, c.getId());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("ClanHallDAO#update0(ClanHall): ", e);
		}
	}
}
