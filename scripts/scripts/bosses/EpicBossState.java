package bosses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dbutils.DbUtils;
import l2mv.gameserver.database.DatabaseFactory;

public class EpicBossState
{
	private static final Logger LOG = LoggerFactory.getLogger(EpicBossState.class);
//	public static List<EpicBossState> list = new ArrayList<EpicBossState>();

//	public static List<EpicBossState> getEpics()
//	{
//		return list;
//	}

//	public static EpicBossState getState(int boss)
//	{
//		for (EpicBossState state : list)
//		{
//			if (state.getBossId() == boss)
//				return state;
//		}

//		return null;
//	}

	public static enum State
	{
		NOTSPAWN, ALIVE, DEAD, INTERVAL
	}

	private int _bossId;
	private long _respawnDate;
	private State _state;

	public int getBossId()
	{
		return _bossId;
	}

	public void setBossId(int newId)
	{
		_bossId = newId;
	}

	public State getState()
	{
		return _state;
	}

	public void setState(State newState)
	{
		_state = newState;
	}

	public long getRespawnDate()
	{
		return _respawnDate;
	}

	public void setRespawnDate(long interval)
	{
		_respawnDate = interval + System.currentTimeMillis();
	}

	public void setRespawnDateFull(long time)
	{
		_respawnDate = time;
	}

	public EpicBossState(int bossId)
	{
		this(bossId, true);
	}

	public EpicBossState(int bossId, boolean isDoLoad)
	{
		_bossId = bossId;
		if (isDoLoad)
		{
			load();
		}

		_epics.put(bossId, this);
	}

	public void load()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("SELECT * FROM epic_boss_spawn WHERE bossId = ? LIMIT 1");
			statement.setInt(1, _bossId);
			rset = statement.executeQuery();

			if (rset.next())
			{
				_respawnDate = rset.getLong("respawnDate") * 1000L;

				if (_respawnDate - System.currentTimeMillis() <= 0)
				{
					_state = State.NOTSPAWN;
				}
				else
				{
					int tempState = rset.getInt("state");
					if (tempState == State.NOTSPAWN.ordinal())
					{
						_state = State.NOTSPAWN;
					}
					else if (tempState == State.INTERVAL.ordinal())
					{
						_state = State.INTERVAL;
					}
					else if (tempState == State.ALIVE.ordinal())
					{
						_state = State.ALIVE;
					}
					else if (tempState == State.DEAD.ordinal())
					{
						_state = State.DEAD;
					}
					else
					{
						_state = State.NOTSPAWN;
					}
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while loading Epic Boss States", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public void save()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("REPLACE INTO epic_boss_spawn (bossId,respawnDate,state) VALUES(?,?,?)");
			statement.setInt(1, _bossId);
			statement.setInt(2, (int) (_respawnDate / 1000));
			statement.setInt(3, _state.ordinal());
			statement.execute();
		}
		catch (SQLException e)
		{
			LOG.error("Error while saving Epic Boss States", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void update()
	{
		Connection con = null;
		Statement statement = null;

		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			statement.executeUpdate("UPDATE epic_boss_spawn SET respawnDate=" + _respawnDate / 1000 + ", state=" + _state.ordinal() + " WHERE bossId=" + _bossId);
			final Date dt = new Date(_respawnDate);
			LOG.info("update EpicBossState: ID:" + _bossId + ", RespawnDate:" + dt + ", State:" + _state.toString());
		}
		catch (SQLException e)
		{
			LOG.error("Exception on update EpicBossState: ID " + _bossId + ", RespawnDate:" + _respawnDate / 1000 + ", State:" + _state.toString(), e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void setNextRespawnDate(long newRespawnDate)
	{
		_respawnDate = newRespawnDate;
	}

	public long getInterval()
	{
		long interval = _respawnDate - System.currentTimeMillis();
		return interval > 0 ? interval : 0;
	}

	// Synerge - Support for storing the bosses status here in a static array
	private static final Map<Integer, EpicBossState> _epics = new HashMap<>();

	public static Collection<EpicBossState> getEpics()
	{
		return _epics.values();
	}

	public static EpicBossState getState(int epicId)
	{
		return _epics.get(epicId);
	}
}
