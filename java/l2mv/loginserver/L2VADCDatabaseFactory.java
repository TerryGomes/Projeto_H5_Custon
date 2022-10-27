package l2mv.loginserver;

import java.sql.Connection;
import java.sql.SQLException;

import l2mv.commons.dbcp.BasicDataSource;

public class L2VADCDatabaseFactory extends BasicDataSource
{
	private static final L2VADCDatabaseFactory _instance = new L2VADCDatabaseFactory();

	public static final L2VADCDatabaseFactory getInstance()
	{
		return _instance;
	}

	public L2VADCDatabaseFactory()
	{
		super(Config.DATABASE_DRIVER, "jdbc:mysql://localhost/freyahf", Config.DATABASE_LOGIN, Config.DATABASE_PASSWORD, Config.DATABASE_MAX_CONNECTIONS, Config.DATABASE_MAX_CONNECTIONS, Config.DATABASE_MAX_IDLE_TIMEOUT, Config.DATABASE_IDLE_TEST_PERIOD, false);
	}

	@Override
	public Connection getConnection() throws SQLException
	{
		return getConnection(null);
	}
}
