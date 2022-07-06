package l2mv.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dbutils.DbUtils;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.model.entity.residence.ClanHall;
import l2mv.gameserver.model.entity.residence.Fortress;
import l2mv.gameserver.model.entity.residence.Residence;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.tables.ClanTable;

public class ClanDataDAO
{
	private static final Logger _log = LoggerFactory.getLogger(ClanDataDAO.class);
	private static final ClanDataDAO _instance = new ClanDataDAO();

	public static final String SELECT_CASTLE_OWNER = "SELECT clan_id FROM clan_data WHERE hasCastle = ? LIMIT 1";
	public static final String SELECT_FORTRESS_OWNER = "SELECT clan_id FROM clan_data WHERE hasFortress = ? LIMIT 1";
	public static final String SELECT_CLANHALL_OWNER = "SELECT clan_id FROM clan_data WHERE hasHideout = ? LIMIT 1";
	public static final String UPDATE_CLAN_DESCRIPTION = "UPDATE clan_description SET description=? WHERE clan_id=?";
	public static final String INSERT_CLAN_DESCRIPTION = "INSERT INTO clan_description (clan_id, description) VALUES (?, ?)";

	public static ClanDataDAO getInstance()
	{
		return _instance;
	}

	public Clan getOwner(Castle c)
	{
		return getOwner(c, SELECT_CASTLE_OWNER);
	}

	public Clan getOwner(Fortress f)
	{
		return getOwner(f, SELECT_FORTRESS_OWNER);
	}

	public Clan getOwner(ClanHall c)
	{
		return getOwner(c, SELECT_CLANHALL_OWNER);
	}

	private Clan getOwner(Residence residence, String sql)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(sql))
		{
			statement.setInt(1, residence.getId());
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					return ClanTable.getInstance().getClan(rset.getInt("clan_id"));
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("ClanDataDAO.getOwner(Residence, String)", e);
		}
		return null;
	}

	public void updateDescription(int id, String description)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(UPDATE_CLAN_DESCRIPTION);
			statement.setString(1, description);
			statement.setInt(2, id);
			statement.execute();
		}
		catch (Exception e)
		{
			_log.error("ClanDataDAO.updateDescription(int, String)", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void insertDescription(int id, String description)
	{

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(INSERT_CLAN_DESCRIPTION);
			statement.setInt(1, id);
			statement.setString(2, description);
			statement.execute();
		}
		catch (Exception e)
		{
			_log.error("ClanDataDAO.updateDescription(int, String)", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}
