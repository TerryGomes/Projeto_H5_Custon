package l2f.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.model.entity.residence.Castle;
import l2f.gameserver.model.entity.residence.Residence;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.utils.ItemFunctions;
import l2f.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 11:45/08.03.2011
 */
public class CastleHiredGuardDAO
{
	private static final Logger _log = LoggerFactory.getLogger(CastleHiredGuardDAO.class);
	private static final CastleHiredGuardDAO _instance = new CastleHiredGuardDAO();

	public static final String SELECT_SQL_QUERY = "SELECT * FROM castle_hired_guards WHERE residence_id=?";
	public static final String INSERT_SQL_QUERY = "INSERT INTO castle_hired_guards(residence_id, item_id, x, y, z) VALUES (?, ?, ?, ?, ?)";
	public static final String DELETE_SQL_QUERY = "DELETE FROM castle_hired_guards WHERE residence_id=?";
	public static final String DELETE_SQL_QUERY2 = "DELETE FROM castle_hired_guards WHERE residence_id=? AND item_id=? AND x=? AND y=? AND z=?";

	public static CastleHiredGuardDAO getInstance()
	{
		return _instance;
	}

	public void load(Castle r)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(SELECT_SQL_QUERY))
		{
			statement.setInt(1, r.getId());

			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					int itemId = rset.getInt("item_id");
					Location loc = new Location(rset.getInt("x"), rset.getInt("y"), rset.getInt("z"));

					ItemInstance item = ItemFunctions.createItem(itemId);
					item.spawnMe(loc);

					r.getSpawnMerchantTickets().add(item);
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("CastleHiredGuardDAO:load(Castle): ", e);
		}
	}

	public void insert(Residence residence, int itemId, Location loc)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(INSERT_SQL_QUERY))
		{
			statement.setInt(1, residence.getId());
			statement.setInt(2, itemId);
			statement.setInt(3, loc.x);
			statement.setInt(4, loc.y);
			statement.setInt(5, loc.z);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("CastleHiredGuardDAO:insert(Residence, int, Location): ", e);
		}
	}

	public void delete(Residence residence, ItemInstance item)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(DELETE_SQL_QUERY2))
		{
			statement.setInt(1, residence.getId());
			statement.setInt(2, item.getItemId());
			statement.setInt(3, item.getLoc().x);
			statement.setInt(4, item.getLoc().y);
			statement.setInt(5, item.getLoc().z);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("CastleHiredGuardDAO:delete(Residence): ", e);
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
			_log.error("CastleHiredGuardDAO:delete(Residence): ", e);
		}
	}
}
