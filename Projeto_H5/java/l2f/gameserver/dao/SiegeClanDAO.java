package l2f.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.model.entity.events.objects.SiegeClanObject;
import l2f.gameserver.model.entity.residence.Residence;

/**
 * @author VISTALL
 * @date 13:13/09.03.2011
 * siege_clans.sql
 */
public class SiegeClanDAO
{
	public static final String SELECT_SQL_QUERY = "SELECT clan_id, param, date FROM siege_clans WHERE residence_id=? AND type=? ORDER BY date";
	public static final String INSERT_SQL_QUERY = "INSERT INTO siege_clans(residence_id, clan_id, param, type, date) VALUES (?, ?, ?, ?, ?)";
	public static final String UPDATE_SQL_QUERY = "UPDATE siege_clans SET type=?, param=? WHERE residence_id=? AND clan_id=?";
	public static final String DELETE_SQL_QUERY = "DELETE FROM siege_clans WHERE residence_id=? AND clan_id=? AND type=?";
	public static final String DELETE_SQL_QUERY2 = "DELETE FROM siege_clans WHERE residence_id=?";

	private static final Logger _log = LoggerFactory.getLogger(SiegeClanDAO.class);
	private static final SiegeClanDAO _instance = new SiegeClanDAO();

	public static SiegeClanDAO getInstance()
	{
		return _instance;
	}

	public List<SiegeClanObject> load(Residence residence, String name)
	{
		List<SiegeClanObject> siegeClans = Collections.emptyList();

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(SELECT_SQL_QUERY);)
		{
			statement.setInt(1, residence.getId());
			statement.setString(2, name);
			siegeClans = new CopyOnWriteArrayList<>();
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					int clanId = rset.getInt("clan_id");
					long param = rset.getLong("param");
					long date = rset.getLong("date");
					SiegeClanObject object = residence.getSiegeEvent().newSiegeClan(name, clanId, param, date);
					if (object != null)
					{
						siegeClans.add(object);
					}
					else
					{
						_log.info("SiegeClanDAO#load(Residence, String): null clan: " + clanId + "; residence: " + residence.getId());
					}
				}
			}
		}
		catch (SQLException e)
		{
			_log.warn("SiegeClanDAO#load(Residence, String): " + e, e);
		}
		return siegeClans;
	}

	public void insert(Residence residence, SiegeClanObject siegeClan)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(INSERT_SQL_QUERY))
		{
			statement.setInt(1, residence.getId());
			statement.setInt(2, siegeClan.getObjectId());
			statement.setLong(3, siegeClan.getParam());
			statement.setString(4, siegeClan.getType());
			statement.setLong(5, siegeClan.getDate());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.warn("SiegeClanDAO#insert(Residence, SiegeClan): " + e, e);
		}
	}

	public void delete(Residence residence, SiegeClanObject siegeClan)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(DELETE_SQL_QUERY);)
		{
			statement.setInt(1, residence.getId());
			statement.setInt(2, siegeClan.getObjectId());
			statement.setString(3, siegeClan.getType());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.warn("SiegeClanDAO#delete(Residence, SiegeClan): " + e, e);
		}
	}

	public void delete(Residence residence)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(DELETE_SQL_QUERY2))
		{
			statement.setInt(1, residence.getId());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.warn("SiegeClanDAO#delete(Residence): " + e, e);
		}
	}

	public void update(Residence residence, SiegeClanObject siegeClan)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(UPDATE_SQL_QUERY))
		{
			statement.setString(1, siegeClan.getType());
			statement.setLong(2, siegeClan.getParam());
			statement.setInt(3, residence.getId());
			statement.setInt(4, siegeClan.getObjectId());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.warn("SiegeClanDAO#update(Residence, SiegeClan): ", e);
		}
	}
}
