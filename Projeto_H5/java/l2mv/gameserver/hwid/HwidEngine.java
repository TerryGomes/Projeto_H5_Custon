package l2mv.gameserver.hwid;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.hwid.HwidGamer.PlayerThreat;
import l2mv.gameserver.hwid.HwidLogging.SimpleLog;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.ChangeLogManager;
import l2mv.gameserver.utils.BatchStatement;

public class HwidEngine
{
	private static final Logger _log = LoggerFactory.getLogger(HwidEngine.class);

	private final List<HwidGamer> _allHwids = new CopyOnWriteArrayList<>();
	private final List<SimpleLog> _logsToSave = new CopyOnWriteArrayList<>();

	public HwidGamer getGamerByHwid(String hwid)
	{
		for (HwidGamer gamer : _allHwids)
		{
			if (gamer.getHwid().equals(hwid))
			{
				return gamer;
			}
		}

		return null;
	}

	public HwidGamer newPlayer(Player player)
	{
		final String hwid = player.getHWID();
		for (HwidGamer gamer : _allHwids)
		{
			if (gamer.getHwid().equals(hwid))
			{
				gamer.addPlayer(player);
				return gamer;
			}
		}

		// First logged char on this pc, loading from db
		final HwidGamer newHwid = loadHwidFromDatabase(hwid);
		newHwid.addPlayer(player);
		_allHwids.add(newHwid);
		return newHwid;
	}

	public void logFailedLogin(Player player)
	{
		String hwid = player.getHWID();
		for (HwidGamer gamer : _allHwids)
		{
			if (gamer.getHwid().equals(hwid))
			{
				// Log.LogHwid(gamer, player.getName(), "Login", "Failed! Too many online characters!");
				// Log.LogToPlayerCommunity(gamer, player, "Failed to login. Too many online characters!");
				return;
			}
		}
	}

	private HwidGamer loadHwidFromDatabase(String hwid)
	{
		HwidGamer foundGamer = null;

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT * FROM hwid WHERE HWID=?"))
		{
			statement.setString(1, hwid);

			ResultSet rset = statement.executeQuery();
			if (rset.next())
			{
				long firstTimePlayed = rset.getLong("first_time_played");
				long totalTimePlayed = rset.getLong("total_time_played") * 1000L;
				int pollAnswer = rset.getInt("poll_answer");
				int warnings = rset.getInt("warnings");
				int seenChangeLog = rset.getInt("seenChangeLog");
				PlayerThreat threat = PlayerThreat.valueOf(rset.getString("threat"));
				long banned = rset.getLong("banned");

				foundGamer = new HwidGamer(hwid, firstTimePlayed, totalTimePlayed, pollAnswer, warnings, seenChangeLog, threat, banned);
			}
		}
		catch (Exception e)
		{
			_log.error("Failed to load Hwid(" + hwid + ") from Database: ", e);
		}

		if (foundGamer == null)
		{
			foundGamer = justJoinedServer(hwid);
		}
		return foundGamer;
	}

	private HwidGamer justJoinedServer(String hwid)
	{
		HwidGamer newGamer = new HwidGamer(hwid, System.currentTimeMillis() / 1000L, 0, -1, 0, ChangeLogManager.getInstance().getLatestChangeId(), PlayerThreat.NONE, 0);
		saveNewGamer(newGamer);
		return newGamer;
	}

	public void updateGamerInDb(HwidGamer gamer)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("UPDATE hwid SET first_time_played=?, total_time_played=?, poll_answer=?, warnings=?, seenChangeLog=?, threat=?, banned=? WHERE HWID=?"))
		{
			statement.setLong(1, gamer.getFirstTimePlayed());
			statement.setLong(2, gamer.getTotalTimePlayed() / 1000L);
			statement.setInt(3, gamer.getPollAnswer());
			statement.setInt(4, gamer.getWarnings());
			statement.setInt(5, gamer.getSeenChangeLog());
			statement.setString(6, gamer.getThreat().toString());
			statement.setLong(7, gamer.getBannedToDate());
			statement.setString(8, gamer.getHwid());
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			_log.error("Failed to insert Hwid(" + gamer.getHwid() + ") to Database: ", e);
		}
	}

	private void saveNewGamer(HwidGamer gamer)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("INSERT INTO hwid SET HWID=?, first_time_played=?, total_time_played=?, poll_answer=?, warnings=?, seenChangeLog=?, threat=?, banned=?"))
		{
			statement.setString(1, gamer.getHwid());
			statement.setLong(2, gamer.getFirstTimePlayed());
			statement.setLong(3, gamer.getTotalTimePlayed() / 1000L);
			statement.setInt(4, gamer.getPollAnswer());
			statement.setInt(5, gamer.getWarnings());
			statement.setInt(6, gamer.getSeenChangeLog());
			statement.setString(7, gamer.getThreat().toString());
			statement.setLong(8, 0L);// Banned
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("Failed to insert Hwid(" + gamer.getHwid() + ") to Database: ", e);
		}
	}

	public synchronized void addToSaveLog(int charObjId, String hwid, String msg, long currentTimeMillis)
	{
		final SimpleLog log = new SimpleLog();
		log._charObjId = charObjId;
		log._hwid = hwid;
		log._msg = msg;
		log._time = currentTimeMillis / 1000;

		HwidLogging.getInstance().addNewLog(log);
		_logsToSave.add(log);
	}

	/**
	 * Save all data on shutdown
	 */
	public void saveAllData()
	{
		// Saving logs
		if (!_logsToSave.isEmpty())
		{
			try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = BatchStatement.createPreparedStatement(con, "INSERT INTO character_logs (obj_Id, HWID, action, time) VALUES (?, ?, ?, ?)"))
			{
				for (SimpleLog log : _logsToSave)
				{
					statement.setInt(1, log._charObjId);
					statement.setString(2, log._hwid);
					statement.setString(3, log._msg);
					statement.setLong(4, log._time);
					statement.addBatch();
				}

				statement.executeBatch();
			}
			catch (Exception e)
			{
				_log.error("Failed to save all hwid logs to db: ", e);
			}
		}

		// Saving hwid time
		if (!_allHwids.isEmpty())
		{
			try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = BatchStatement.createPreparedStatement(con, "INSERT INTO `hwid` (HWID,first_time_played,total_time_played,poll_answer,warnings,threat) VALUES" + "(?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE first_time_played=VALUES(first_time_played),total_time_played=VALUES(total_time_played),poll_answer=VALUES(poll_answer),warnings=VALUES(warnings),threat=VALUES(threat);"))
			{
				for (HwidGamer gamer : _allHwids)
				{
					statement.setString(1, gamer.getHwid());
					statement.setLong(2, gamer.getFirstTimePlayed());
					statement.setLong(3, gamer.getTotalTimePlayed() / 1000L);
					statement.setInt(4, gamer.getPollAnswer());
					statement.setInt(5, gamer.getWarnings());
					statement.setString(6, gamer.getThreat().toString());
					statement.addBatch();
				}

				statement.executeBatch();
			}
			catch (Exception e)
			{
				_log.error("Failed to save all hwid times to db: ", e);
			}
		}
	}

	public static HwidEngine getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final HwidEngine _instance = new HwidEngine();
	}
}
