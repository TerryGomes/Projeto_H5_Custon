package l2f.loginserver.gameservercon.gspackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

import l2f.loginserver.database.L2DatabaseFactory;
import l2f.loginserver.gameservercon.ReceivablePacket;
import l2f.loginserver.gameservercon.lspackets.ChangePasswordResponse;

public class ChangePassword extends ReceivablePacket
{
	private static final Logger log = Logger.getLogger(ChangePassword.class.getName());

	private String accname;
	private String oldPass;
	private String newPass;
	private String hwid;

	@Override
	protected void readImpl()
	{
		accname = readS();
		oldPass = readS();
		newPass = readS();
		hwid = readS();
	}

	@Override
	protected void runImpl()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			String dbPassword = null;
			try (PreparedStatement statement = con.prepareStatement("SELECT * FROM accounts WHERE login = ?"))
			{
				statement.setString(1, accname);
				try (ResultSet rs = statement.executeQuery())
				{
					if (rs.next())
					{
						dbPassword = rs.getString("password");
					}
				}
			}
			catch (Exception e)
			{
				log.warning("Can't recive old password for account " + accname + ", exciption :" + e);
			}

			// Encode old password and compare it to sended one, send packet to determine changed or not.
			if (!oldPass.equals(dbPassword) && oldPass.length() > 0)
			{
				sendPacket(new ChangePasswordResponse(accname, false));
			}
			else
			{
				try (PreparedStatement statement = con.prepareStatement("UPDATE accounts SET password = ? WHERE login = ?"))
				{
					statement.setString(1, newPass);
					statement.setString(2, accname);
					final int result = statement.executeUpdate();
					sendPacket(new ChangePasswordResponse(accname, result != 0));
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
