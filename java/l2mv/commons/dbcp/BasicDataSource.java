package l2mv.commons.dbcp;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

public class BasicDataSource implements DataSource
{
	private final PoolingDataSource _source;
	private final ObjectPool _connectionPool;

	public BasicDataSource(String driver, String connectURI, String uname, String passwd, int maxActive, int maxIdle, int idleTimeOut, int idleTestPeriod, boolean poolPreparedStatements)
	{
		GenericObjectPool connectionPool = new GenericObjectPool(null);

		connectionPool.setMaxActive(maxActive);
		connectionPool.setMaxIdle(maxIdle);
		connectionPool.setMinIdle(1);
		connectionPool.setMaxWait(-1L);
		connectionPool.setWhenExhaustedAction((byte) 2);
		connectionPool.setTestOnBorrow(false);
		connectionPool.setTestWhileIdle(true);
		connectionPool.setTimeBetweenEvictionRunsMillis(idleTestPeriod * 1000L);
		connectionPool.setNumTestsPerEvictionRun(maxActive);
		connectionPool.setMinEvictableIdleTimeMillis(idleTimeOut * 1000L);

		GenericKeyedObjectPoolFactory statementPoolFactory = null;
		if (poolPreparedStatements)
		{
			statementPoolFactory = new GenericKeyedObjectPoolFactory(null, -1, (byte) 0, 0L, 1, -1);
		}
		Properties connectionProperties = new Properties();
		connectionProperties.put("user", uname);
		connectionProperties.put("password", passwd);

		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI, connectionProperties);

		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, statementPoolFactory, "SELECT 1", false, true);

		PoolingDataSource dataSource = new PoolingDataSource(connectionPool);

		_connectionPool = connectionPool;
		_source = dataSource;
	}

	public Connection getConnection(Connection con) throws SQLException
	{
		return (((con == null) || (con.isClosed())) ? (con = _source.getConnection()) : con);
	}

	public int getBusyConnectionCount() throws SQLException
	{
		return _connectionPool.getNumActive();
	}

	public int getIdleConnectionCount() throws SQLException
	{
		return _connectionPool.getNumIdle();
	}

	public void shutdown() throws Exception
	{
		_connectionPool.close();
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException
	{
		return _source.getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException
	{
		_source.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int getLoginTimeout() throws SQLException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException
	{
		return false;
	}

	@Override
	public Connection getConnection() throws SQLException
	{
		return _source.getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException
	{
		throw new UnsupportedOperationException();
	}
}