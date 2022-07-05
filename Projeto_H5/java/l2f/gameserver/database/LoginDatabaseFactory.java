package l2f.gameserver.database;

import java.sql.Connection;
import java.sql.SQLException;

import l2f.commons.dbcp.BasicDataSource;
import l2f.gameserver.Config;

public class LoginDatabaseFactory extends BasicDataSource
{
	private static final LoginDatabaseFactory _instance = new LoginDatabaseFactory();

	public static final LoginDatabaseFactory getInstance() throws SQLException
	{
		return _instance;
	}

	public LoginDatabaseFactory()
	{
		super(Config.DATABASE_DRIVER, Config.DATABASE_LOGIN_URL, Config.DATABASE_LOGIN_USER, Config.DATABASE_LOGIN_PASSWORD, Config.DATABASE_MAX_CONNECTIONS, Config.DATABASE_MAX_CONNECTIONS,
					Config.DATABASE_MAX_IDLE_TIMEOUT, Config.DATABASE_IDLE_TEST_PERIOD, false);
	}

	@Override
	public Connection getConnection() throws SQLException
	{
		return getConnection(null);
	}
}