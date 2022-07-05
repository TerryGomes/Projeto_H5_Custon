package l2f.gameserver.hwid;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.model.Player;

public class HwidLogging
{
	private static final Logger _log = LoggerFactory.getLogger(HwidLogging.class);

	private static HwidLogging _instance;
	private final Map<Integer, List<SimpleLog>> _logs = new ConcurrentHashMap<>();

	public void addNewLog(SimpleLog log)
	{
		if (!_logs.containsKey(log._charObjId))
		{
			_logs.put(log._charObjId, loadLogs(log._charObjId));
		}

		List<SimpleLog> playerLogs = _logs.get(log._charObjId);
		playerLogs.add(log);
		_logs.put(log._charObjId, playerLogs);
	}

	public List<SimpleLog> getMyLogs(Player player)
	{
		return _logs.get(player.getObjectId());
	}

	private List<SimpleLog> loadLogs(int objId)
	{
		List<SimpleLog> playerLogs = new ArrayList<>();

		try (Connection con = DatabaseFactory.getInstance().getConnection();
					PreparedStatement statement = con.prepareStatement("SELECT * FROM character_logs WHERE obj_Id=" + objId);
					ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				SimpleLog msg = new SimpleLog();
				msg._charObjId = objId;
				msg._hwid = rset.getString("HWID");
				msg._msg = rset.getString("action");
				msg._time = rset.getLong("time");
				playerLogs.add(msg);
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while loading HWID logs:", e);
		}

		return playerLogs;
	}

	public static class SimpleLog
	{
		public int _charObjId;
		public String _hwid;
		public String _msg;
		public long _time;
	}

	public static HwidLogging getInstance()
	{
		if (_instance == null)
		{
			_instance = new HwidLogging();
		}
		return _instance;
	}
}
