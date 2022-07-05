/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2f.gameserver.masteriopack.rankpvpsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.database.DatabaseFactory;

/**
 * This class contains informations about the best killers and rank points gatherers.
 * @author Masterio
 */
public class TopTable
{
	public static final Logger log = Logger.getLogger(TopTable.class.getName());

	public static final int TOP_LIMIT = 100; // limit for top list players data.

	private static TopTable _instance = null;

	private boolean _isUpdating = false;

	public static final long DAY = 86400000;
	public static final long HOUR = 3600000;

	/**
	* <PlayerId, TopField> contains top list. <br>
	* List is ordered from 1st position to last.
	*/
	private Map<Integer, TopField> _topKillsTable = new LinkedHashMap<>();

	/**
	* <PlayerId, TopField> contains top list. <br>
	* List is ordered from 1st position to last.
	*/
	private Map<Integer, TopField> _topGatherersTable = new LinkedHashMap<>();

	private static long _lastUpdateTime = 0; // used in initialization table
	private static long _nextUpdateTime = 0; // used in CB

	private TopTable()
	{
		load();
	}

	public static TopTable getInstance()
	{
		if (_instance == null)
		{
			_instance = new TopTable();
		}

		return _instance;
	}

	private void load()
	{
		long startTime = Calendar.getInstance().getTimeInMillis();

		String info = "Loaded";

		// initialize _lastUpdateTime:
		loadLastUpdate();

		// load times:
		long[] times = calculateNextUpdateTime();

		TopTable.setNextUpdateTime(times[2]);

		// initialize Top Tables:
		// if last update time < previous update time
		if (_lastUpdateTime < times[3])
		{
			updateTopTable();
			info = "Updated";
		}
		else
		{
			restoreTopTable();
		}

		// update schedule:
		ThreadPoolManager.getInstance().schedule(new TopTableSchedule(), times[1]);

		long endTime = Calendar.getInstance().getTimeInMillis();

		log.info(" - TopTable: Data " + info + ". " + _topKillsTable.size() + " killers and " + _topGatherersTable.size() + " gatherers in " + (endTime - startTime) + " ms.");
		log.info(" - TopTable: Next update on " + RPSUtil.dateToString(times[2]) + " at " + RPSUtil.timeToString(times[2]) + ".");
	}

	/**
	 * Returns:<br>
	 * [0] - currentTime <b>date and time in milliseconds</b>,<br>
	 * [1] - timeToNextUpdate <b>time in milliseconds</b>,<br>
	 * [2] - nextUpdateTime <b>date and time in milliseconds</b>,<br>
	 * [3] - prevUpdateTime <b>date and time in milliseconds</b>
	 *
	 * @return
	 */
	public static long[] calculateNextUpdateTime()
	{
		// initialize current & update time (today update):
		Calendar uTime = Calendar.getInstance();
		long currentTime = uTime.getTimeInMillis(); // date & time

		uTime.set(Calendar.HOUR_OF_DAY, 0);
		uTime.set(Calendar.MINUTE, 0);
		uTime.set(Calendar.SECOND, 0);
		uTime.set(Calendar.MILLISECOND, 0);

		long currentTimeNoHour = uTime.getTimeInMillis(); // date
		long currentDayTime = currentTime - currentTimeNoHour - HOUR; // time
		long nextUpdateTime = 0; // date & time
		long prevUpdateTime = 0; // date & time

		// get next and previous update time:
		prevUpdateTime = (currentTimeNoHour - DAY) + RPSConfig.TOP_TABLE_UPDATE_TIMES.get(RPSConfig.TOP_TABLE_UPDATE_TIMES.size() - 1) + HOUR;

		for (Long time : RPSConfig.TOP_TABLE_UPDATE_TIMES)
		{
			// the TOP_TABLE_UPDATE_TIMES are ordered from 0 to 24h in ms.
			if (currentDayTime < time)
			{
				nextUpdateTime = currentTimeNoHour + time + HOUR;
				break;
			}

			prevUpdateTime = currentTimeNoHour + time + HOUR;
		}

		// get next update time for next day update exception:
		if (nextUpdateTime == 0)
		{
			// nextUpdateTime = current time + (24h - currentDayTime) + firstDayUpdateTime:
			nextUpdateTime = currentTime + (DAY - currentDayTime) + RPSConfig.TOP_TABLE_UPDATE_TIMES.get(0);
		}

		// calculate time to next update:
		long timeToNextUpdate = nextUpdateTime - currentTime; // time

		long[] a = new long[4];
		a[0] = currentTime;
		a[1] = timeToNextUpdate;
		a[2] = nextUpdateTime;
		a[3] = prevUpdateTime;

		return a;
	}

	/**
	* Update top table and save results in rank_pvp_system_top table.
	* @return
	*/
	protected boolean updateTopTable()
	{
		boolean ok = false;

		// lock table:
		setUpdating(true);

		// clear tables:
		_topKillsTable.clear();
		_topGatherersTable.clear();

		// get minimum allowed time:
		long sysTime = Calendar.getInstance().getTimeInMillis() - RPSConfig.TOP_LIST_IGNORE_TIME_LIMIT;

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;

		// order and load top killers & gatherers from model:
		Map<Integer, PvpSummary> pvpList = PvpTable.getInstance().getPvpTable();

		Map<Integer, TopField> tmpTopKillsTable = new LinkedHashMap<>();
		Map<Integer, TopField> tmpTopGatherersTable = new LinkedHashMap<>();

		int KillPosition = 0;
		int PointPosition = 0;

		try
		{
			con = DatabaseFactory.getInstance().getConnection();

			// for TopKillersTable:
			for (int i = 1; i <= TOP_LIMIT; i++)
			{
				int maxKills = 0;
				int bestKiller = 0;

				long maxPoints = 0;
				int bestGatherer = 0;

				for (Map.Entry<Integer, PvpSummary> e : pvpList.entrySet())
				{
					PvpSummary kps = e.getValue();

					if ((RPSConfig.TOP_LIST_IGNORE_TIME_LIMIT == 0) || (kps.getLastKillTime() >= sysTime)) // if last kill is in TOP_LIST_IGNORE_TIME_LIMIT.
					{
						if (!tmpTopKillsTable.containsKey(kps.getKillerId())) // don't check already added killer.
						{
							if ((kps.getTotalKillsLegal() > maxKills) && (kps.getTotalKillsLegal() > 0)) // finding the best.
							{
								maxKills = kps.getTotalKillsLegal();
								bestKiller = kps.getKillerId();
							}
						}

						if (!tmpTopGatherersTable.containsKey(kps.getKillerId())) // don't check already added gatherer.
						{
							if ((kps.getTotalRankPoints() > maxPoints) && (kps.getTotalRankPoints() > 0)) // finding the best.
							{
								maxPoints = kps.getTotalRankPoints();
								bestGatherer = kps.getKillerId();
							}
						}
					}
				}

				// if killer found:
				if (bestKiller != 0)
				{
					KillPosition++;

					// if founded, add him to list:
					TopField tf = new TopField();

					tf.setCharacterId(bestKiller);
					tf.setValue(maxKills);
					tf.setTopPosition(KillPosition);

					// get character data:
					statement = con.prepareStatement("SELECT char_name as name, base_class as base_class, level as level FROM characters WHERE " + RPSConfig.CHAR_ID_COLUMN_NAME + " = ?");
					statement.setInt(1, bestKiller);

					rset = statement.executeQuery();

					while (rset.next())
					{
						tf.setCharacterName(rset.getString("name"));
						tf.setCharacterLevel(rset.getInt("level"));
						tf.setCharacterBaseClassId(rset.getInt("base_class"));
					}

					rset.close();

					// add this killer on temporary top list:
					tmpTopKillsTable.put(bestKiller, tf);
				}

				// if any best killer not found, break action:
				if (bestGatherer != 0)
				{
					PointPosition++;

					// if founded, add him to list:
					TopField tf = new TopField();

					tf.setCharacterId(bestGatherer);
					tf.setValue(maxPoints);
					tf.setTopPosition(PointPosition);

					// get character data:
					statement = con.prepareStatement("SELECT char_name as name, base_class as base_class, level as level FROM characters WHERE " + RPSConfig.CHAR_ID_COLUMN_NAME + " = ?");
					statement.setInt(1, bestGatherer);

					rset = statement.executeQuery();

					while (rset.next())
					{
						tf.setCharacterName(rset.getString("name"));
						tf.setCharacterLevel(rset.getInt("level"));
						tf.setCharacterBaseClassId(rset.getInt("base_class"));
					}

					rset.close();

					// add this gatherer on top list:
					tmpTopGatherersTable.put(bestGatherer, tf);
				}

			}

			// TODO reorder the tmpTopKillsTable and tmpTopGatherersTable here, can be required in special situations.

			// add new top tables:
			setTopKillsTable(tmpTopKillsTable);
			setTopGatherersTable(tmpTopGatherersTable);

			if (statement == null)
			{
				statement = con.prepareStatement("");
			}

			// clear Top Table:
			statement.addBatch("DELETE FROM rank_pvp_system_top_table");

			// insert new Top Killers list:
			for (Map.Entry<Integer, TopField> e : _topKillsTable.entrySet())
			{
				statement.addBatch("INSERT INTO rank_pvp_system_top_table (position, player_id, value, table_id) VALUES (" + e.getValue().getTopPosition() + "," + e.getValue().getCharacterId() + ","
							+ e.getValue().getValue() + ",1)");
			}

			// insert new Top Killers list:
			for (Map.Entry<Integer, TopField> e : _topGatherersTable.entrySet())
			{
				statement.addBatch("INSERT INTO rank_pvp_system_top_table (position, player_id, value, table_id) VALUES (" + e.getValue().getTopPosition() + "," + e.getValue().getCharacterId() + ","
							+ e.getValue().getValue() + ",2)");
			}

			statement.executeBatch();

			// save time of update in rank_pvp_system_options table:
			long calendar = Calendar.getInstance().getTimeInMillis();
			statement = con.prepareStatement("UPDATE rank_pvp_system_options SET option_value_long=? WHERE option_id=1");
			statement.setLong(1, calendar);

			statement.execute();
			statement.close();

			_lastUpdateTime = calendar;

			ok = true;

		}
		catch (SQLException e)
		{
			log.info(e.getMessage());

			// clear tables:
			_topKillsTable.clear();
			_topGatherersTable.clear();

			ok = false;
		}
		finally
		{
			try
			{
				if (con != null)
				{
					con.close();
					con = null;
				}
			}
			catch (Exception e)
			{
				log.info(e.getMessage());
			}
		}

		// unlock table:
		setUpdating(false);
		return ok;

	}

	/**
	 * Load Top Table from rank_pvp_system_top table. Used only on server start.
	 */
	private void restoreTopTable()
	{

		// clear tables:
		_topKillsTable.clear();
		_topGatherersTable.clear();

		// load Tables:
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = DatabaseFactory.getInstance().getConnection();

			// get top killers:
			statement = con.prepareStatement(
						"SELECT position, player_id, value, table_id, char_name as name, base_class as base_class, level as level FROM rank_pvp_system_top_table JOIN characters ON rank_pvp_system_top_table.player_id = characters."
									+ RPSConfig.CHAR_ID_COLUMN_NAME + " ORDER BY position");
			rset = statement.executeQuery();

			while (rset.next())
			{
				TopField tf = new TopField();

				tf.setCharacterId(rset.getInt("player_id"));
				tf.setValue(rset.getLong("value"));
				tf.setTopPosition(rset.getInt("position"));
				tf.setCharacterName(rset.getString("name"));
				tf.setCharacterLevel(rset.getInt("level"));
				tf.setCharacterBaseClassId(rset.getInt("base_class"));

				if (rset.getInt("table_id") == 1)
				{
					_topKillsTable.put(rset.getInt("player_id"), tf);
				}
				else if (rset.getInt("table_id") == 2)
				{
					_topGatherersTable.put(rset.getInt("player_id"), tf);
				}
			}

			rset.close();
			statement.close();
		}
		catch (SQLException e)
		{
			log.info(e.getMessage());
		}
		finally
		{
			try
			{
				if (con != null)
				{
					con.close();
					con = null;
				}
			}
			catch (Exception e)
			{
				log.info(e.getMessage());
			}
		}
	}

	private void loadLastUpdate()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = DatabaseFactory.getInstance().getConnection();

			// get top killers:
			statement = con.prepareStatement("SELECT option_value_long FROM rank_pvp_system_options WHERE option_id=1");

			rset = statement.executeQuery();

			while (rset.next())
			{
				_lastUpdateTime = (rset.getLong("option_value_long"));
			}

			rset.close();
			statement.close();
		}
		catch (SQLException e)
		{
			log.info(e.getMessage());
		}
		finally
		{
			try
			{
				if (con != null)
				{
					con.close();
					con = null;
				}
			}
			catch (Exception e)
			{
				log.info(e.getMessage());
			}
		}
	}

	/**
	 * List is ordered from 1st position to last.
	 * @return the _topKillsTable
	 */
	public Map<Integer, TopField> getTopKillsTable()
	{
		return _topKillsTable;
	}

	/**
	 * @param topKillsTable the _topKillsTable to set
	 */
	public void setTopKillsTable(Map<Integer, TopField> topKillsTable)
	{
		_topKillsTable = topKillsTable;
	}

	/**
	 * List is ordered from 1st position to last.
	 * @return the _topGatherersTable
	 */
	public Map<Integer, TopField> getTopGatherersTable()
	{
		return _topGatherersTable;
	}

	/**
	 * @param topGatherersTable the _topGatherersTable to set
	 */
	public void setTopGatherersTable(Map<Integer, TopField> topGatherersTable)
	{
		_topGatherersTable = topGatherersTable;
	}

	/**
	 * @return the _isUpdating
	 */
	public boolean isUpdating()
	{
		return _isUpdating;
	}

	/**
	 * @param isUpdating the _isUpdating to set
	 */
	public void setUpdating(boolean isUpdating)
	{
		_isUpdating = isUpdating;
	}

	public static long getLastUpdateTime()
	{
		return _lastUpdateTime;
	}

	public static void setLastUpdateTime(long lastUpdateTime)
	{
		_lastUpdateTime = lastUpdateTime;
	}

	public static long getNextUpdateTime()
	{
		return _nextUpdateTime;
	}

	public static void setNextUpdateTime(long _nextUpdateTime)
	{
		TopTable._nextUpdateTime = _nextUpdateTime;
	}

	private static class TopTableSchedule implements Runnable
	{
		public TopTableSchedule()
		{

		}

		@Override
		public void run()
		{
			long[] times = TopTable.calculateNextUpdateTime();

			TopTable.setNextUpdateTime(times[2]);

			if (!TopTable.getInstance().isUpdating() && TopTable.getInstance().updateTopTable())
			{
				log.info("TopTable: Data updated in " + (Calendar.getInstance().getTimeInMillis() - times[0]) + " ms <<< Next update on " + RPSUtil.dateToString(times[2]) + " at "
							+ RPSUtil.timeToString(times[2]));

				ThreadPoolManager.getInstance().schedule(new TopTableSchedule(), times[1]);
			}
			else
			{
				log.info("TopTable: Data update failed! <<< Next try for 5 minutes.");
				ThreadPoolManager.getInstance().schedule(new TopTableSchedule(), 300000);
			}
		}
	}
}