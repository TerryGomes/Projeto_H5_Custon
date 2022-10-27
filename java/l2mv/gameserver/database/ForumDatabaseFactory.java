package l2mv.gameserver.database;

import java.sql.Connection;
import java.sql.SQLException;

import l2mv.commons.dbcp.BasicDataSource;
import l2mv.gameserver.ConfigHolder;

public class ForumDatabaseFactory extends BasicDataSource
{
	private static final ForumDatabaseFactory _instance = new ForumDatabaseFactory();

	public ForumDatabaseFactory()
	{
		super(ConfigHolder.getString("ForumDriver"), ConfigHolder.getString("ForumURL"), ConfigHolder.getString("ForumLogin"), ConfigHolder.getString("ForumPassword"), ConfigHolder.getInt("ForumMaximumDbConnections"), ConfigHolder.getInt("ForumMaximumDbConnections"), ConfigHolder.getInt("ForumMaxIdleConnectionTimeout"), ConfigHolder.getInt("ForumIdleConnectionTestPeriod"), false);
	}

	@Override
	public Connection getConnection() throws SQLException
	{
		return getConnection(null);
	}

	public static ForumDatabaseFactory getInstance()
	{
		return ForumDatabaseFactory._instance;
	}
}
