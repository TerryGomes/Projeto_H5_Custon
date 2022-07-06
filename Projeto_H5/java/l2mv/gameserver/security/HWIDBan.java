package l2mv.gameserver.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dbutils.DbUtils;
import l2mv.gameserver.database.DatabaseFactory;

public class HWIDBan
{
	private static final Logger _log = LoggerFactory.getLogger(HWIDBan.class);
	private static ArrayList<String> _l = new ArrayList<String>();

	public static void LoadAllHWID()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			String hwid = "";
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM ban_hwid");
			rset = statement.executeQuery();
			while (rset.next())
			{
				hwid = rset.getString("hwid");
				if (hwid != "")
				{
					_l.add(hwid);
				}
			}
		}
		catch (Exception e)
		{
			_log.info("HWID not loaded?");
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
			_log.info("black list (Hwid) loaded size: " + _l.size() + "");
		}
	}

	public static void addBlackList(String hwid)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO ban_hwid (hwid) VALUES(?)");
			statement.setString(1, hwid);
			statement.execute();
		}
		catch (Exception e)
		{
		}
		finally
		{
			_l.add(hwid);
			_log.info("adding hwid to black list(hwid) " + hwid + "");
			DbUtils.closeQuietly(con, statement);
		}
	}

	public static void delBlackList(String hwid)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE from ban_hwid WHERE hwid like ?");
			statement.setString(1, hwid);
			statement.execute();
		}
		catch (Exception e)
		{
		}
		finally
		{
			_l.remove(hwid);
			_log.info("remove hwid from black list(hwid) " + hwid + "");
			DbUtils.closeQuietly(con, statement);
		}
	}

	public static ArrayList<String> getAllBannedHwid()
	{
		return _l;
	}
}