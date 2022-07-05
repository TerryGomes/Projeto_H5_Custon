package l2f.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.napile.primitive.maps.IntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.dbutils.DbUtils;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.model.Player;
import l2f.gameserver.skills.TimeStamp;
import l2f.gameserver.utils.SqlBatch;

/**
 * @author VISTALL
 * @date 11:41/28.03.2011
 */
public class CharacterGroupReuseDAO
{
	private static final Logger _log = LoggerFactory.getLogger(CharacterGroupReuseDAO.class);
	private static CharacterGroupReuseDAO _instance = new CharacterGroupReuseDAO();
	public static final String DELETE_SQL_QUERY = "DELETE FROM character_group_reuse WHERE object_id=?";
	public static final String SELECT_SQL_QUERY = "SELECT * FROM character_group_reuse WHERE object_id=?";
	public static final String INSERT_SQL_QUERY = "REPLACE INTO `character_group_reuse` (`object_id`,`reuse_group`,`item_id`,`end_time`,`reuse`) VALUES";

	public static CharacterGroupReuseDAO getInstance()
	{
		return _instance;
	}

	public static void select(Player player, Connection con)
	{
		long curTime = System.currentTimeMillis();

		try (PreparedStatement statement = con.prepareStatement(SELECT_SQL_QUERY))
		{
			statement.setInt(1, player.getObjectId());

			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					int group = rset.getInt("reuse_group");
					int itemId = rset.getInt("item_id");
					long endTime = rset.getLong("end_time");
					long reuse = rset.getLong("reuse");

					if (endTime - curTime > 500)
					{
						TimeStamp stamp = new TimeStamp(itemId, endTime, reuse);
						player.addSharedGroupReuse(group, stamp);
					}
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("CharacterGroupReuseDAO.select(L2Player) 1:", e);
		}

		try (PreparedStatement statement = con.prepareStatement(DELETE_SQL_QUERY))
		{
			statement.setInt(1, player.getObjectId());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("CharacterGroupReuseDAO.select(L2Player) 2:", e);
		}
	}

	public void insert(Player player)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(DELETE_SQL_QUERY);
			statement.setInt(1, player.getObjectId());
			statement.execute();

			Collection<IntObjectMap.Entry<TimeStamp>> reuses = player.getSharedGroupReuses();
			if (reuses.isEmpty())
			{
				return;
			}

			SqlBatch b = new SqlBatch(INSERT_SQL_QUERY);
			synchronized (reuses)
			{
				for (IntObjectMap.Entry<TimeStamp> entry : reuses)
				{
					int group = entry.getKey();
					TimeStamp timeStamp = entry.getValue();
					if (timeStamp.hasNotPassed())
					{
						StringBuilder sb = new StringBuilder("(");
						sb.append(player.getObjectId()).append(",");
						sb.append(group).append(",");
						sb.append(timeStamp.getId()).append(",");
						sb.append(timeStamp.getEndTime()).append(",");
						sb.append(timeStamp.getReuseBasic()).append(")");
						b.write(sb.toString());
					}
				}
			}
			if (!b.isEmpty())
			{
				statement.executeUpdate(b.close());
			}
		}
		catch (SQLException e)
		{
			_log.error("CharacterGroupReuseDAO.insert(L2Player):", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}
