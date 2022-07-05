package l2f.gameserver.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.dbutils.DbUtils;
import l2f.gameserver.Config;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.instancemanager.ServerVariables;

public class GameStats
{
	private static final Logger LOG = LoggerFactory.getLogger(GameStats.class);

	/* database statistics */
	private static AtomicLong _updatePlayerBase = new AtomicLong(0L);

	/* in-game statistics */
	private static AtomicLong _playerEnterGameCounter = new AtomicLong(0L);

	private static AtomicLong _taxSum = new AtomicLong(0L);
	private static long _taxLastUpdate;
	private static AtomicLong _rouletteSum = new AtomicLong(0L);
	private static long _rouletteLastUpdate;
	private static AtomicLong _adenaSum = new AtomicLong(0L);

	static
	{
		_taxSum.set(ServerVariables.getLong("taxsum", 0));
		_rouletteSum.set(ServerVariables.getLong("rouletteSum", 0));

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT (SELECT SUM(count) FROM items WHERE item_id=57) + (SELECT SUM(treasury) FROM castle) AS `count`");
			rset = statement.executeQuery();
			if (Config.RATE_DROP_ADENA < 25)
			{
				if (rset.next())
				{
					_adenaSum.addAndGet((rset.getLong("count") / (long) Config.RATE_DROP_ADENA));
				}
			}
			else
			{
				_adenaSum.addAndGet(140000000000L);
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while getting GameStats", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public static void increaseUpdatePlayerBase()
	{
		_updatePlayerBase.incrementAndGet();
	}

	public static long getUpdatePlayerBase()
	{
		return _updatePlayerBase.get();
	}

	public static void incrementPlayerEnterGame()
	{
		_playerEnterGameCounter.incrementAndGet();
	}

	public static long getPlayerEnterGame()
	{
		return _playerEnterGameCounter.get();
	}

	public static void addTax(long sum)
	{
		long taxSum = _taxSum.addAndGet(sum);
		if (System.currentTimeMillis() - _taxLastUpdate < 10000)
		{
			return;
		}
		_taxLastUpdate = System.currentTimeMillis();
		ServerVariables.set("taxsum", taxSum);
	}

	public static void addRoulette(long sum)
	{
		long rouletteSum = _rouletteSum.addAndGet(sum);
		if (System.currentTimeMillis() - _rouletteLastUpdate < 10000)
		{
			return;
		}
		_rouletteLastUpdate = System.currentTimeMillis();
		ServerVariables.set("rouletteSum", rouletteSum);
	}

	public static long getTaxSum()
	{
		return _taxSum.get();
	}

	public static long getRouletteSum()
	{
		return _rouletteSum.get();
	}

	public static void addAdena(long sum)
	{
		_adenaSum.addAndGet(sum);
	}

	public static long getAdena()
	{
		return _adenaSum.get();
	}
}