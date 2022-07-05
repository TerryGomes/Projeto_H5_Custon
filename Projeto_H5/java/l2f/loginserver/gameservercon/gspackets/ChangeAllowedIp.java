package l2f.loginserver.gameservercon.gspackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.loginserver.database.L2DatabaseFactory;
import l2f.loginserver.gameservercon.ReceivablePacket;

public class ChangeAllowedIp extends ReceivablePacket
{
	private static final Logger _log = LoggerFactory.getLogger(ChangeAllowedIp.class);

	private String account;
	private String ip;

	@Override
	protected void readImpl()
	{
		account = readS();
		ip = readS();
	}

	@Override
	protected void runImpl()
	{

		Connection con = null;
		PreparedStatement statement = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE accounts SET allow_ip=? WHERE login=?");
			statement.setString(1, ip);
			statement.setString(2, account);
			statement.execute();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.warn("ChangeAllowedIP: Could not write data. Reason: " + e);
		}
		finally
		{
			try
			{
				if (con != null)
				{
					con.close();
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

}
