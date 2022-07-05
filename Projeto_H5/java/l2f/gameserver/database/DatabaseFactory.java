package l2f.gameserver.database;

import java.sql.Connection;
import java.sql.SQLException;

import l2f.commons.dbcp.BasicDataSource;
import l2f.gameserver.Config;

public class DatabaseFactory extends BasicDataSource
{
	private static final DatabaseFactory _instance = new DatabaseFactory();

	public static final DatabaseFactory getInstance() throws SQLException
	{
		return _instance;
	}

	public DatabaseFactory()
	{
		super(Config.DATABASE_DRIVER, Config.DATABASE_GAME_URL, Config.DATABASE_GAME_USER, Config.DATABASE_GAME_PASSWORD, Config.DATABASE_MAX_CONNECTIONS, Config.DATABASE_MAX_CONNECTIONS, Config.DATABASE_MAX_IDLE_TIMEOUT, Config.DATABASE_IDLE_TEST_PERIOD, false);
	}

	@Override
	public Connection getConnection() throws SQLException
	{
		return getConnection(null);
	}
}