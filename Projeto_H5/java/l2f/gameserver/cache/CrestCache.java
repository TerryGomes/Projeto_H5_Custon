package l2f.gameserver.cache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import l2f.gameserver.database.DatabaseFactory;

public class CrestCache
{
	public static final int ALLY_CREST_SIZE = 192;
	public static final int CREST_SIZE = 256;
	public static final int LARGE_CREST_SIZE = 2176;

	private static final Logger _log = LoggerFactory.getLogger(CrestCache.class);

	private final static CrestCache _instance = new CrestCache();

	public final static CrestCache getInstance()
	{
		return _instance;
	}

	/** Требуется для получения ID значка по ID клана */
	private final TIntIntHashMap _pledgeCrestId = new TIntIntHashMap();
	private final TIntIntHashMap _pledgeCrestLargeId = new TIntIntHashMap();
	private final TIntIntHashMap _allyCrestId = new TIntIntHashMap();

	/** Получение значка по ID */
	private final TIntObjectHashMap<byte[]> _pledgeCrest = new TIntObjectHashMap<byte[]>();
	private final TIntObjectHashMap<byte[]> _pledgeCrestLarge = new TIntObjectHashMap<byte[]>();
	private final TIntObjectHashMap<byte[]> _allyCrest = new TIntObjectHashMap<byte[]>();

	/** Блокировка для чтения/записи объектов из "кэша" */
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
	private final Lock writeLock = lock.writeLock();

	private CrestCache()
	{
		load();
	}

	public void load()
	{
		int count = 0;
		int pledgeId, crestId;
		byte[] crest;

		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{

			try (PreparedStatement statement = con.prepareStatement("SELECT clan_id, crest FROM clan_data WHERE crest IS NOT NULL"); ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					count++;

					pledgeId = rset.getInt("clan_id");
					crest = rset.getBytes("crest");

					crestId = getCrestId(pledgeId, crest);

					_pledgeCrestId.put(pledgeId, crestId);
					_pledgeCrest.put(crestId, crest);
				}
			}

			try (PreparedStatement statement = con.prepareStatement("SELECT clan_id, largecrest FROM clan_data WHERE largecrest IS NOT NULL"); ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					count++;

					pledgeId = rset.getInt("clan_id");
					crest = rset.getBytes("largecrest");

					crestId = getCrestId(pledgeId, crest);

					_pledgeCrestLargeId.put(pledgeId, crestId);
					_pledgeCrestLarge.put(crestId, crest);
				}
			}

			try (PreparedStatement statement = con.prepareStatement("SELECT ally_id, crest FROM ally_data WHERE crest IS NOT NULL"); ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					count++;
					pledgeId = rset.getInt("ally_id");
					crest = rset.getBytes("crest");

					crestId = getCrestId(pledgeId, crest);

					_allyCrestId.put(pledgeId, crestId);
					_allyCrest.put(crestId, crest);
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while loading CrestCache! ", e);
		}
		_log.info("CrestCache: Loaded " + count + " crests");
	}

	/**
	 * Генерирует уникальный положительный ID на основе данных: ID клана/альянса и значка
	 *
	 * @param pledgeId ID клана или альянса
	 * @param crest данные значка
	 * @return ID значка в "кэше"
	 */
	private static int getCrestId(int pledgeId, byte[] crest)
	{
		return Math.abs(new HashCodeBuilder(15, 87).append(pledgeId).append(crest).toHashCode());
	}

	public byte[] getPledgeCrest(int crestId)
	{
		byte[] crest = null;

		readLock.lock();
		try
		{
			crest = _pledgeCrest.get(crestId);
		}
		finally
		{
			readLock.unlock();
		}

		return crest;
	}

	public byte[] getPledgeCrestLarge(int crestId)
	{
		byte[] crest = null;

		readLock.lock();
		try
		{
			crest = _pledgeCrestLarge.get(crestId);
		}
		finally
		{
			readLock.unlock();
		}

		return crest;
	}

	public byte[] getAllyCrest(int crestId)
	{
		byte[] crest = null;

		readLock.lock();
		try
		{
			crest = _allyCrest.get(crestId);
		}
		finally
		{
			readLock.unlock();
		}

		return crest;
	}

	public int getPledgeCrestId(int pledgeId)
	{
		int crestId = 0;

		readLock.lock();
		try
		{
			crestId = _pledgeCrestId.get(pledgeId);
		}
		finally
		{
			readLock.unlock();
		}

		return crestId;
	}

	public int getPledgeCrestLargeId(int pledgeId)
	{
		int crestId = 0;

		readLock.lock();
		try
		{
			crestId = _pledgeCrestLargeId.get(pledgeId);
		}
		finally
		{
			readLock.unlock();
		}

		return crestId;
	}

	public int getAllyCrestId(int pledgeId)
	{
		int crestId = 0;
		readLock.lock();

		try
		{
			crestId = _allyCrestId.get(pledgeId);
		}
		finally
		{
			readLock.unlock();
		}

		return crestId;
	}

	public void removePledgeCrest(int pledgeId)
	{
		writeLock.lock();
		try
		{
			_pledgeCrest.remove(_pledgeCrestId.remove(pledgeId));
		}
		finally
		{
			writeLock.unlock();
		}

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET crest=? WHERE clan_id=?"))
		{
			statement.setNull(1, Types.VARBINARY);
			statement.setInt(2, pledgeId);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("Error while removing Pledge Crest!", e);
		}
	}

	public void removePledgeCrestLarge(int pledgeId)
	{
		writeLock.lock();
		try
		{
			_pledgeCrestLarge.remove(_pledgeCrestLargeId.remove(pledgeId));
		}
		finally
		{
			writeLock.unlock();
		}

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET largecrest=? WHERE clan_id=?"))
		{
			statement.setNull(1, Types.VARBINARY);
			statement.setInt(2, pledgeId);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("Error while removing Pledge Crest Large! ", e);
		}
	}

	public void removeAllyCrest(int pledgeId)
	{
		writeLock.lock();
		try
		{
			_allyCrest.remove(_allyCrestId.remove(pledgeId));
		}
		finally
		{
			writeLock.unlock();
		}

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("UPDATE ally_data SET crest=? WHERE ally_id=?"))
		{
			statement.setNull(1, Types.VARBINARY);
			statement.setInt(2, pledgeId);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("Error while Removing Ally Crest ", e);
		}
	}

	public int savePledgeCrest(int pledgeId, byte[] crest)
	{
		int crestId = getCrestId(pledgeId, crest);

		writeLock.lock();
		try
		{
			_pledgeCrestId.put(pledgeId, crestId);
			_pledgeCrest.put(crestId, crest);
		}
		finally
		{
			writeLock.unlock();
		}

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET crest=? WHERE clan_id=?"))
		{
			statement.setBytes(1, crest);
			statement.setInt(2, pledgeId);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("Error while saving Pledge Crest! ", e);
		}

		return crestId;
	}

	public int savePledgeCrestLarge(int pledgeId, byte[] crest)
	{
		int crestId = getCrestId(pledgeId, crest);

		writeLock.lock();
		try
		{
			_pledgeCrestLargeId.put(pledgeId, crestId);
			_pledgeCrestLarge.put(crestId, crest);
		}
		finally
		{
			writeLock.unlock();
		}

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET largecrest=? WHERE clan_id=?"))
		{
			statement.setBytes(1, crest);
			statement.setInt(2, pledgeId);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("Error while saving Pledge Crest Large! ", e);
		}

		return crestId;
	}

	public int saveAllyCrest(int pledgeId, byte[] crest)
	{
		int crestId = getCrestId(pledgeId, crest);

		writeLock.lock();
		try
		{
			_allyCrestId.put(pledgeId, crestId);
			_allyCrest.put(crestId, crest);
		}
		finally
		{
			writeLock.unlock();
		}

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("UPDATE ally_data SET crest=? WHERE ally_id=?"))
		{
			statement.setBytes(1, crest);
			statement.setInt(2, pledgeId);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("Error while saving Ally Crest! ", e);
		}

		return crestId;
	}
}
