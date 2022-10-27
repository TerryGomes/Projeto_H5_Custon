package l2mv.gameserver.model.entity.achievements;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dbutils.DbUtils;
import l2mv.gameserver.dao.CharacterDAO;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;

/**
 * @author Nik
 */
public class PlayerCounters
{
	private static final Logger _log = LoggerFactory.getLogger(PlayerCounters.class);
	public static PlayerCounters DUMMY_COUNTER = new PlayerCounters(null);

	// Player
	public int pvpKills = 0;
	public int pkInARowKills = 0;
	public int highestKarma = 0;
	public int timesDied = 0;
	public int playersRessurected = 0;
	public int duelsWon = 0;
	public int fameAcquired = 0;
	public long expAcquired = 0;
	public int recipesSucceeded = 0;
	public int recipesFailed = 0;
	public int manorSeedsSow = 0;
	public int critsDone = 0;
	public int mcritsDone = 0;
	public int fishCaught = 0;
	public int treasureBoxesOpened = 0;
	public int unrepeatableQuestsCompleted = 0;
	public int repeatableQuestsCompleted = 0;
	public long adenaDestroyed = 0;
	public int recommendsMade = 0;
	public int foundationItemsMade = 0;
	public long distanceWalked = 0;

	// Enchants
	public int enchantNormalSucceeded = 0;
	public int enchantBlessedSucceeded = 0;
	public int highestEnchant = 0;
	public int maxSoulCrystalLevel = 0;

	// Clan & Olympiad
	public int olyHiScore = 0;
	public int olyGamesWon = 0;
	public int olyGamesLost = 0;
	public int timesNoble = 0;
	public int timesHero = 0;
	public int timesMarried = 0;
	public int castleSiegesWon = 0;
	public int fortSiegesWon = 0;
	public int dominionSiegesWon = 0;

	// Epic Bosses.
	public int antharasKilled = 0;
	public int baiumKilled = 0;
	public int valakasKilled = 0;
	public int orfenKilled = 0;
	public int antQueenKilled = 0;
	public int coreKilled = 0;
	public int belethKilled = 0;
	public int sailrenKilled = 0;
	public int baylorKilled = 0;
	public int zakenKilled = 0;
	public int tiatKilled = 0;
	public int freyaKilled = 0;
	public int frintezzaKilled = 0;
	// Other kills
	public int mobsKilled = 0;
	public int raidsKilled = 0;
	public int championsKilled = 0;
	public int townGuardsKilled = 0;
	public int siegeGuardsKilled = 0;
	public int playersKilledInSiege = 0;
	public int playersKilledInDominion = 0;

	public int timesVoted = 0;
	public int krateisCubePoints = 0;
	public int krateisCubeTotalPoints = 0;

	// Here comes the code...
	private Player _activeChar = null;
	private int _playerObjId = 0;

	public PlayerCounters(Player activeChar)
	{
		_activeChar = activeChar;
		_playerObjId = activeChar == null ? 0 : activeChar.getObjectId();
	}

	public PlayerCounters(int playerObjId)
	{
		_activeChar = World.getPlayer(playerObjId);
		_playerObjId = playerObjId;
	}

	protected Player getChar()
	{
		return _activeChar;
	}

	public long getPoints(String fieldName)
	{
		if (_activeChar == null)
		{
			return 0;
		}

		try
		{
			return getClass().getField(fieldName).getLong(this);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	public void save()
	{
		if (_activeChar == null)
		{
			return;
		}

		// Because im SQL noob
		Connection con = null;
		Connection con2 = null;
		PreparedStatement statement2 = null;
		PreparedStatement statement3 = null;
		ResultSet rs = null;
		try
		{
			con2 = DatabaseFactory.getInstance().getConnection();
			statement2 = con2.prepareStatement("SELECT char_id FROM character_counters WHERE char_id = " + _playerObjId + ";");
			rs = statement2.executeQuery();
			if (!rs.next())
			{
				statement3 = con2.prepareStatement("INSERT INTO character_counters (char_id) values (?);");
				statement3.setInt(1, _playerObjId);
				statement3.execute();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con2, statement2, rs);
		}

		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			StringBuilder sb = new StringBuilder();
			sb.append("UPDATE character_counters SET ");
			boolean firstPassed = false;
			for (Field field : getClass().getFields())
			{
				switch (field.getName())
				// Fields that we wont save.
				{
				case "_activeChar":
				case "_playerObjId":
				case "DUMMY_COUNTER":
					continue;
				}

				if (firstPassed)
				{
					sb.append(",");
				}
				sb.append(field.getName());
				sb.append("=");

				try
				{
					sb.append(field.getInt(this));
				}
				catch (IllegalArgumentException | IllegalAccessException | SecurityException e)
				{
					sb.append(field.getLong(this));
				}

				firstPassed = true;
			}
			sb.append(" WHERE char_id=" + _playerObjId + ";");
			statement = con.prepareStatement(sb.toString());
			statement.execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void load()
	{
		if (_activeChar == null)
		{
			return;
		}

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM character_counters WHERE char_id = ?");
			statement.setInt(1, getChar().getObjectId());
			rs = statement.executeQuery();
			while (rs.next())
			{
				for (Field field : getClass().getFields())
				{
					switch (field.getName())
					// Fields that we dont use here.
					{
					case "_activeChar":
					case "_playerObjId":
					case "DUMMY_COUNTER":
						continue;
					}

					try
					{
						field.setInt(this, rs.getInt(field.getName()));
					}
					catch (SQLException sqle)
					{
						field.setLong(this, rs.getLong(field.getName()));
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rs);
		}
	}

	public static String generateTopHtml(String fieldName, int maxTop, boolean asc)
	{
		Map<Integer, Long> tops = loadCounter(fieldName, maxTop, asc);
		int order = 1;

		StringBuilder sb = new StringBuilder(tops.size() * 100);
		sb.append("<table width=300 border=0>");
		for (Entry<Integer, Long> top : tops.entrySet())
		{
			sb.append("<tr><td><table border=0 width=294 bgcolor=" + ((order % 2) == 0 ? "1E1E1E" : "090909") + ">").append("<tr><td fixwidth=10%><font color=LEVEL>").append(order++).append(".<font></td>").append("<td fixwidth=45%>").append(CharacterDAO.getNameByObjectId(top.getKey())).append("</td><td fixwidth=45%><font color=777777>").append(top.getValue()).append("</font></td></tr>").append("</table></td></tr>");
		}
		sb.append("</table>");

		return sb.toString();
	}

	public static Map<Integer, Long> loadCounter(String fieldName, int maxRetrieved, boolean asc)
	{
		Map<Integer, Long> ret = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			statement = con.prepareStatement("SELECT char_id, " + fieldName + " FROM character_counters ORDER BY " + fieldName + " " + (asc ? "ASC" : "DESC") + " LIMIT 0, " + maxRetrieved + ";");
			rs = statement.executeQuery();
			ret = new LinkedHashMap<>(rs.getFetchSize());
			while (rs.next())
			{
				int charObjId = rs.getInt(1);
				long value = rs.getLong(2);
				ret.put(charObjId, value);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(null, statement, rs);
		}

		return ret == null ? Collections.emptyMap() : ret;
	}

	public static void checkTable()
	{
		// Generate used fields list.
		List<String> fieldNames = new ArrayList<>();
		for (Field field : PlayerCounters.class.getFields())
		{
			switch (field.getName())
			// Fields that we dont use here.
			{
			case "_activeChar":
			case "_playerObjId":
			case "DUMMY_COUNTER":
				continue;
			default:
				fieldNames.add(field.getName());
			}
		}

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DESC character_counters");
			rs = statement.executeQuery();
			while (rs.next())
			{
				// _log.info("Checking column: " + rs.getString(1));
				fieldNames.remove(rs.getString(1));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rs);
		}

		if (!fieldNames.isEmpty())
		{
			StringBuilder sb = new StringBuilder(fieldNames.size() * 30);

			try
			{
				sb.append("ALTER TABLE character_counters");
				for (String str : fieldNames)
				{
					_log.info("PlayerCounters Update: Adding missing column name: " + str);

					Class<?> fieldType = PlayerCounters.class.getField(str).getType();
					if ((fieldType == int.class) || (fieldType == Integer.class))
					{
						sb.append(" ADD COLUMN " + str + " int(11) NOT NULL DEFAULT 0,");
					}
					else if ((fieldType == long.class) || (fieldType == Long.class))
					{
						sb.append(" ADD COLUMN " + str + " bigint(20) NOT NULL DEFAULT 0,");
					}
					else
					{
						_log.warn("Unsupported data type: " + fieldType);
					}

				}
				sb.setCharAt(sb.length() - 1, ';');

				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement(sb.toString());
				statement.execute();
				_log.info("PlayerCounters Update: Changes executed!");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				DbUtils.closeQuietly(con, statement, rs);
			}
		}
	}
}
