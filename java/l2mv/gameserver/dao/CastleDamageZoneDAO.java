package l2mv.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.entity.residence.Residence;

/**
 * @author VISTALL
 * @date 10:23/17.03.2011
 */
public class CastleDamageZoneDAO
{
	private static final CastleDamageZoneDAO _instance = new CastleDamageZoneDAO();
	private static final Logger _log = LoggerFactory.getLogger(CastleDoorUpgradeDAO.class);

	public static final String SELECT_SQL_QUERY = "SELECT zone FROM castle_damage_zones WHERE residence_id=?";
	public static final String INSERT_SQL_QUERY = "INSERT INTO castle_damage_zones (residence_id, zone) VALUES (?,?)";
	public static final String DELETE_SQL_QUERY = "DELETE FROM castle_damage_zones WHERE residence_id=?";

	public static CastleDamageZoneDAO getInstance()
	{
		return _instance;
	}

	public List<String> load(Residence r)
	{
		List<String> set = Collections.emptyList();

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(SELECT_SQL_QUERY))
		{
			statement.setInt(1, r.getId());
			set = new ArrayList<String>();

			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					set.add(rset.getString("zone"));
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("CastleDamageZoneDAO:load(Residence): ", e);
		}

		return set;
	}

	public void insert(Residence residence, String name)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(INSERT_SQL_QUERY))
		{
			statement.setInt(1, residence.getId());
			statement.setString(2, name);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("CastleDamageZoneDAO:insert(Residence, String): ", e);
		}
	}

	public void delete(Residence residence)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(DELETE_SQL_QUERY))
		{
			statement.setInt(1, residence.getId());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("CastleDamageZoneDAO:delete(Residence): ", e);
		}
	}
}
