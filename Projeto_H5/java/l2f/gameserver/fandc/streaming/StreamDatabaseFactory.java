package l2f.gameserver.fandc.streaming;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.dbcp.BasicDataSource;
import l2f.gameserver.Config;
import l2f.gameserver.ConfigHolder;
import l2f.gameserver.GameServer;
import l2f.gameserver.Shutdown.ShutdownMode;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.listener.game.OnShutdownListener;

public class StreamDatabaseFactory extends BasicDataSource implements OnShutdownListener
{
	private static final Logger LOG = LoggerFactory.getLogger(StreamDatabaseFactory.class);

	public StreamDatabaseFactory()
	{
		super(Config.DATABASE_DRIVER, ConfigHolder.getString("StreamTableDBURL"), ConfigHolder.getString("StreamTableDBLogin"), ConfigHolder.getString("StreamTableDBPassword"), Config.DATABASE_MAX_CONNECTIONS, Config.DATABASE_MAX_CONNECTIONS, Config.DATABASE_MAX_IDLE_TIMEOUT, Config.DATABASE_IDLE_TEST_PERIOD, false);

		GameServer.getInstance().addListener(this);
	}

	public static Connection getStreamDatabaseConnection() throws SQLException
	{
		if (ConfigHolder.getBool("StreamTableInGameServerDB"))
		{
			return DatabaseFactory.getInstance().getConnection();
		}
		return SingletonHolder.instance.getConnection();
	}

	@Override
	public Connection getConnection() throws SQLException
	{
		return getConnection(null);
	}

	@Override
	public void onShutdown(ShutdownMode shutdownMode)
	{
		try
		{
			shutdown();
		}
		catch (Exception e)
		{
			LOG.error("Error while shutting down StreamDatabaseFactory", e);
		}
	}

	private static class SingletonHolder
	{
		private static final StreamDatabaseFactory instance = new StreamDatabaseFactory();
	}
}
