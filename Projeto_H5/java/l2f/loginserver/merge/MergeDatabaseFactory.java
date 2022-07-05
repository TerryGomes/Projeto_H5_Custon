package l2f.loginserver.merge;

import java.sql.Connection;
import java.sql.SQLException;

import l2f.commons.dbcp.BasicDataSource;
import l2f.loginserver.Config;

public class MergeDatabaseFactory extends BasicDataSource
{
	public MergeDatabaseFactory()
	{
		super(Config.DATABASE_DRIVER, Config.MERGE_URL, Config.MERGE_LOGIN, Config.MERGE_PASSWORD, Config.DATABASE_MAX_CONNECTIONS, Config.DATABASE_MAX_CONNECTIONS, Config.DATABASE_MAX_IDLE_TIMEOUT, Config.DATABASE_IDLE_TEST_PERIOD, false);
	}

	@Override
	public Connection getConnection() throws SQLException
	{
		return getConnection((Connection) null);
	}

	public static MergeDatabaseFactory getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final MergeDatabaseFactory instance = new MergeDatabaseFactory();
	}
}
