package l2f.gameserver.database.merge;

import java.sql.Connection;
import java.sql.SQLException;

import l2f.commons.dbcp.BasicDataSource;
import l2f.gameserver.Config;
import l2f.gameserver.ConfigHolder;

public class MergeDatabaseFactory extends BasicDataSource
{
	public MergeDatabaseFactory()
	{
		super(Config.DATABASE_DRIVER, ConfigHolder.getString("MergeUrl"), ConfigHolder.getString("MergeLogin"), ConfigHolder.getString("MergePassword"), Config.DATABASE_MAX_CONNECTIONS,
					Config.DATABASE_MAX_CONNECTIONS, Config.DATABASE_MAX_IDLE_TIMEOUT, Config.DATABASE_IDLE_TEST_PERIOD, false);
	}

	@Override
	public Connection getConnection() throws SQLException
	{
		return getConnection(null);
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
