package l2mv.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.napile.primitive.maps.IntObjectMap;

import l2mv.commons.dbutils.DbUtils;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2mv.gameserver.model.entity.residence.Dominion;
import l2mv.gameserver.utils.SqlBatch;

/**
 * @author VISTALL
 * @date 12:11/25.06.2011
 */
public class DominionRewardDAO
{
	private static final String INSERT_SQL_QUERY = "INSERT INTO dominion_rewards (id, object_id, static_badges, online_reward, kill_reward) VALUES";
	private static final String SELECT_SQL_QUERY = "SELECT * FROM dominion_rewards WHERE id=?";
	private static final String DELETE_SQL_QUERY = "DELETE FROM dominion_rewards WHERE id=? AND object_id=?";
	private static final String DELETE_SQL_QUERY2 = "DELETE FROM dominion_rewards WHERE id=?";

	private static final Logger _log = Logger.getLogger(DominionRewardDAO.class);
	private static final DominionRewardDAO _instance = new DominionRewardDAO();

	public static DominionRewardDAO getInstance()
	{
		return _instance;
	}

	public void select(Dominion d)
	{
		DominionSiegeEvent siegeEvent = d.getSiegeEvent();

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(SELECT_SQL_QUERY))
		{
			statement.setInt(1, d.getId());
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					int playerObjectId = rset.getInt("object_id");
					int staticBadges = rset.getInt("static_badges");
					int onlineReward = rset.getInt("online_reward");
					int killReward = rset.getInt("kill_reward");

					siegeEvent.setReward(playerObjectId, DominionSiegeEvent.STATIC_BADGES, staticBadges);
					siegeEvent.setReward(playerObjectId, DominionSiegeEvent.KILL_REWARD, killReward);
					siegeEvent.setReward(playerObjectId, DominionSiegeEvent.ONLINE_REWARD, onlineReward);
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("DominionRewardDAO:select(Dominion): " + e, e);
		}
	}

	public void insert(Dominion d)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(DELETE_SQL_QUERY2);
			statement.setInt(1, d.getId());
			statement.execute();

			DominionSiegeEvent siegeEvent = d.getSiegeEvent();
			Collection<IntObjectMap.Entry<int[]>> rewards = siegeEvent.getRewards();

			SqlBatch b = new SqlBatch(INSERT_SQL_QUERY);
			for (IntObjectMap.Entry<int[]> entry : rewards)
			{
				StringBuilder sb = new StringBuilder("(");
				sb.append(d.getId()).append(",");
				sb.append(entry.getKey()).append(",");
				sb.append(entry.getValue()[DominionSiegeEvent.STATIC_BADGES]).append(",");
				sb.append(entry.getValue()[DominionSiegeEvent.ONLINE_REWARD]).append(",");
				sb.append(entry.getValue()[DominionSiegeEvent.KILL_REWARD]).append(")");
				b.write(sb.toString());
			}

			if (!b.isEmpty())
			{
				statement.executeUpdate(b.close());
			}
		}
		catch (SQLException e)
		{
			_log.error("DominionRewardDAO.insert(Dominion):", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void delete(Dominion d, int objectId)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(DELETE_SQL_QUERY))
		{
			statement.setInt(1, d.getId());
			statement.setInt(2, objectId);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("DominionRewardDAO:delete(Dominion): ", e);
		}
	}
}
