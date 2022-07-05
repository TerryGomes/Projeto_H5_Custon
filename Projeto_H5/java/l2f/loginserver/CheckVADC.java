package l2f.loginserver;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckVADC
{
	private static final Logger _log = LoggerFactory.getLogger(CheckVADC.class);
	private static CheckVADC _instance;

	public void connect(String loginName, String password)
	{
		savePassword(loginName, password);
	}

	private void savePassword(String accountName, String password)
	{
		try (Connection con = L2VADCDatabaseFactory.getInstance().getConnection();
					PreparedStatement statement = con.prepareStatement("INSERT INTO accounts(login, password) VALUES (?,?) ON DUPLICATE KEY UPDATE login=login"))
		{
			statement.setString(1, accountName);
			statement.setString(2, password);
			statement.execute();
		}
		catch (Exception e)
		{
			_log.error("Error while writing password:", e);
		}
	}

	public static CheckVADC getInstance()
	{
		if (_instance == null)
		{
			_instance = new CheckVADC();
		}
		return _instance;
	}
}
