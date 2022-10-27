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
package l2mv.gameserver.masteriopack.rankpvpsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.database.DatabaseFactory;

/**
 * This class contains all killer's PvP data.<br>
 * Each KillerPvpSummary contains all killer versus victim data.<br>
 * PvP is searched by Killer Id.
 * @author Masterio
 */
public class PvpTable
{
	public static final Logger log = Logger.getLogger(PvpTable.class.getName());

	private static PvpTable _instance = null;

	/** [killer_id, PvpSummary] */
	private Map<Integer, PvpSummary> _pvpTable = new ConcurrentHashMap<>();

	private PvpTable()
	{
		long startTime = Calendar.getInstance().getTimeInMillis();

		if (RPSConfig.DATABASE_CLEANER_ENABLED)
		{
			cleanPvpTable();
		}

		load();

		long endTime = Calendar.getInstance().getTimeInMillis();

		log.info(" - PvpTable: Data loaded. " + (_pvpTable.size()) + " objects in " + (endTime - startTime) + " ms.");

		ThreadPoolManager.getInstance().schedule(new PvpTableSchedule(), RPSConfig.PVP_TABLE_UPDATE_INTERVAL);
	}

	public static PvpTable getInstance()
	{
		if (_instance == null)
		{
			_instance = new PvpTable();
		}

		return _instance;
	}

	/**
	 * Get PvP object, if not found returns new PvP object for killer -> victim and save in PvPTable.<br>
	 * 1: Search killerPvpTable by killerId<br>
	 * 2: Search victimPvpTable by victimId.
	 * @param killerId
	 * @param victimId
	 * @param readOnly - If TRUE and PvP will be not founded in table, it will returns dummy PvP object.<br>
	 * If FALSE and it will not found the PvP in table, it will create new one and returns it (it will be saved in database).
	 * @param updateDailyStats - update daily stats will execute database update.
	 * @return
	 */
	public Pvp getPvp(int killerId, int victimId, boolean readOnly, boolean updateDailyStats)
	{
		// find and get PvP:
		PvpSummary kps = getKillerPvpSummary(killerId, readOnly, updateDailyStats);

		if (kps != null)
		{
			Pvp pvp = kps.getVictimPvpTable().get(victimId);

			if (pvp != null)
			{
				return pvp;
			}
		}

		// otherwise create and get new PvP:
		if (kps == null)
		{
			kps = new PvpSummary();
			kps.setKillerId(killerId);
			kps.updateRankId();
		}

		Pvp pvp = kps.getVictimPvpTable().get(victimId);

		if (pvp == null)
		{
			pvp = new Pvp();

			pvp.setVictimId(victimId);

			if (readOnly)
			{
				pvp.setDbStatus(DBStatus.NONE);
			}
			else
			{
				kps.getVictimPvpTable().put(victimId, pvp);
			}
		}

		return pvp;
	}

	/**
	 * Get PvP object, if not found returns new PvP object for killer -> victim and save in PvPTable.<br>
	 * 1: Search killerPvpTable by killerId<br>
	 * 2: Search victimPvpTable by victimId.<br>
	 * Reset daily fields if required.
	 * @param killerId
	 * @param victimId
	 * @param systemDay
	 * @param updateDailyStats - update daily stats will execute database update.
	 * @return
	 */
	public Pvp getPvp(int killerId, int victimId, long systemDay, boolean updateDailyStats)
	{
		// find and get PvP:
		PvpSummary kps = getKillerPvpSummary(killerId, systemDay, false, updateDailyStats);

		if (kps != null)
		{
			Pvp pvp = kps.getVictimPvpTable().get(victimId);

			if (pvp != null)
			{
				// check daily fields, reset if kill day is other than system day:
				if (pvp.getKillDay() != systemDay)
				{
					pvp.resetDailyFields();
				}

				return pvp;
			}
		}

		// otherwise create and get new PvP:
		if (kps == null)
		{
			kps = new PvpSummary();
			kps.setKillerId(killerId);
			kps.updateRankId();
		}

		Pvp pvp = kps.getVictimPvpTable().get(victimId);

		if (pvp == null)
		{
			pvp = new Pvp();

			pvp.setVictimId(victimId);

			kps.getVictimPvpTable().put(victimId, pvp);
		}

		return pvp;
	}

	/**
	 * Returns Killer PvP statistics like total kills, total legal kills, etc. for killer id.<br>
	 * Reset daily fields if required (today taken from current time).
	 * @param killerId
	 * @param readOnly - If TRUE then if not founded, it will not create new one. <br>If FALSE, it will create new KillerPvpSummary object in model (object with 0 values).
	 * @param updateDailyStats - if TRUE updates daily stats and all victim pvp daily stats. It will execute database update.
	 * @return
	 */
	public PvpSummary getKillerPvpSummary(int killerId, boolean readOnly, boolean updateDailyStats)
	{
		// get system day for update daily fields:
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.HOUR, 0);

		long systemDay = c.getTimeInMillis(); // current system day

		// find and get PvP:
		PvpSummary kps = _pvpTable.get(killerId);

		if (kps == null)
		{
			kps = new PvpSummary();
			kps.setKillerId(killerId);
			kps.updateRankId();
			kps.updateDailyStats(systemDay);

			if (!readOnly)
			{
				_pvpTable.put(killerId, kps);
			}
		}
		else if (updateDailyStats)
		{
			kps.updateDailyStats(systemDay);
		}

		return kps;
	}

	/**
	 * Returns Killer PvP statistics like total kills, total legal kills, etc. for killer id.<br>
	 * Reset daily fields if required. Automatic update Daily Stats.
	 * @param killerId
	 * @param systemDay
	 * @param readOnly - If TRUE then if not founded, it will not create new one. <br>If FALSE, it will create new KillerPvpSummary object in model (object with 0 values).
	 * @param updateDailyStats - if TRUE updates daily stats and victim pvp daily stats. It will execute database update.
	 * @return
	 */
	public PvpSummary getKillerPvpSummary(int killerId, long systemDay, boolean readOnly, boolean updateDailyStats)
	{
		// find and get PvP:
		PvpSummary kps = _pvpTable.get(killerId);

		if (kps == null)
		{
			kps = new PvpSummary();

			kps.setKillerId(killerId);
			kps.updateRankId();
			kps.updateDailyStats(systemDay);

			if (!readOnly)
			{
				_pvpTable.put(killerId, kps);
			}
		}
		else if (updateDailyStats)
		{
			kps.updateDailyStats(systemDay);
		}

		return kps;
	}

	/**
	 * Returns selected killer rank id. <br>This method is faster than getKillerPvpSummary().getRankId().
	 * @param killerId
	 * @return rank id or -1 if not founded.
	 */
	public int getRankId(int killerId)
	{
		// find and get PvP:
		PvpSummary kps = _pvpTable.get(killerId);

		if (kps != null)
		{
			return kps.getRankId();
		}

		return -1;
	}

	public Map<Integer, PvpSummary> getPvpTable()
	{
		return _pvpTable;
	}

	public void setPvpTable(Map<Integer, PvpSummary> pvpTable)
	{
		_pvpTable = pvpTable;
	}

	private void load()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT * FROM rank_pvp_system_pvp");

			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				Pvp pvp = new Pvp();

				pvp.setVictimId(rset.getInt("victim_id"));
				pvp.setKills(rset.getInt("kills"));
				pvp.setKillsToday(rset.getInt("kills_today"));
				pvp.setKillsLegal(rset.getInt("kills_legal"));
				pvp.setKillsLegalToday(rset.getInt("kills_today_legal"));
				pvp.setRankPoints(rset.getLong("rank_points"));
				pvp.setRankPointsToday(rset.getLong("rank_points_today"));
				pvp.setKillTime(rset.getLong("kill_time"));
				pvp.setKillDay(rset.getLong("kill_day"));

				PvpSummary kps = getKillerPvpSummary(rset.getInt("killer_id"), false, false);

				pvp.setDbStatus(DBStatus.NONE);

				kps.addVictimPvpOnLoadFromDB(pvp);
			}

			rset.close();
			statement.close();

			statement = con.prepareStatement("SELECT * FROM rank_pvp_system_pvp_summary");

			rset = statement.executeQuery();

			while (rset.next())
			{
				// get only existed summaries:
				PvpSummary kps = getKillerPvpSummary(rset.getInt("killer_id"), true, false);

				kps.setPvpExp(rset.getLong("pvp_exp"));
				kps.setTotalWarKills(rset.getInt("total_war_kills"));
				kps.setTotalWarKillsLegal(rset.getInt("total_war_kills_legal"));
				kps.setMaxRankId(rset.getInt("max_rank_id"));

				kps.setDbStatus(DBStatus.NONE);

				kps.updateRankId();
			}

			rset.close();
			statement.close();
		}
		catch (SQLException e)
		{
			log.info(e.getMessage());
		}
	}

	/**
	 * Returns an array with 3 values:<br>
	 * [0] - 0: update complete, -1: update failed.<br>
	 * [1] - inserts count.<br>
	 * [2] - updates count.
	 * @return
	 */
	public int[] updateDB()
	{
		int[] result =
		{
			0,
			0,
			0
		};

		int insertCount = 0; // count of insert queries
		int updateCount = 0; // count of update queries

		Connection con = null;
		Statement statement = null;

		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();

			// search new or updated fields in VictimPvpTable:
			for (Map.Entry<Integer, PvpSummary> e : _pvpTable.entrySet())
			{
				PvpSummary kps = e.getValue();

				if (kps == null)
				{
					break;
				}

				Map<Integer, Pvp> victimPvpTable = kps.getVictimPvpTable();

				for (Map.Entry<Integer, Pvp> f : victimPvpTable.entrySet())
				{
					Pvp pvp = f.getValue();

					if (pvp == null)
					{
						break;
					}

					if (pvp.getDbStatus() == DBStatus.UPDATED)
					{
						// rank_pvp_system_pvp:
						statement.addBatch("UPDATE rank_pvp_system_pvp SET kills=" + pvp.getKills() + ", kills_today=" + pvp.getKillsToday() + ", kills_legal=" + pvp.getKillsLegal() + ", kills_today_legal=" + pvp.getKillsLegalToday() + ", rank_points=" + pvp.getRankPoints() + ", rank_points_today=" + pvp.getRankPointsToday() + ", kill_time=" + pvp.getKillTime() + ", kill_day=" + pvp.getKillDay() + " WHERE killer_id=" + kps.getKillerId() + " AND victim_id=" + pvp.getVictimId());
						pvp.setDbStatus(DBStatus.NONE); // it is after query because PvP is updating in real time.
						updateCount++;
					}
					else if (pvp.getDbStatus() == DBStatus.INSERTED)
					{
						// rank_pvp_system_pvp:
						statement.addBatch("INSERT INTO rank_pvp_system_pvp (killer_id, victim_id, kills, kills_today, kills_legal, kills_today_legal, rank_points, rank_points_today, kill_time, kill_day) VALUES (" + kps.getKillerId() + ", " + pvp.getVictimId() + ", " + pvp.getKills() + ", " + pvp.getKillsToday() + ", " + pvp.getKillsLegal() + ", " + pvp.getKillsLegalToday() + ", " + pvp.getRankPoints() + ", " + pvp.getRankPointsToday() + ", " + pvp.getKillTime() + ", " + pvp.getKillDay() + ")");
						pvp.setDbStatus(DBStatus.NONE);
						insertCount++;
					}
				}

				if (kps.getDbStatus() == DBStatus.UPDATED)
				{
					// rank_pvp_system_pvp_summary:
					statement.addBatch("UPDATE rank_pvp_system_pvp_summary SET pvp_exp=" + kps.getPvpExp() + ", total_war_kills=" + kps.getTotalWarKills() + ", total_war_kills_legal=" + kps.getTotalWarKillsLegal() + ", max_rank_id=" + kps.getMaxRankId() + " WHERE killer_id=" + kps.getKillerId());
					kps.setDbStatus(DBStatus.NONE);
					updateCount++;
				}
				else if (kps.getDbStatus() == DBStatus.INSERTED)
				{
					// rank_pvp_system_pvp_summary:
					statement.addBatch("INSERT INTO rank_pvp_system_pvp_summary (killer_id, pvp_exp, total_war_kills, total_war_kills_legal, max_rank_id) VALUES (" + kps.getKillerId() + ", " + kps.getPvpExp() + ", " + kps.getTotalWarKills() + ", " + kps.getTotalWarKillsLegal() + ", " + kps.getMaxRankId() + ")");
					kps.setDbStatus(DBStatus.NONE);
					insertCount++;
				}
			}

			statement.executeBatch();
			statement.close();
		}
		catch (SQLException e)
		{
			log.info(e.getMessage());
			result[0] = -1;
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

		result[1] = insertCount;
		result[2] = updateCount;

		return result;
	}

	/**
	 * Remove permanently not active players! Only when server is starting!
	 */
	private static void cleanPvpTable()
	{
		Connection con = null;

		try
		{
			// calculate ignore time:
			Calendar c = Calendar.getInstance();
			long ignoreTime = c.getTimeInMillis() - RPSConfig.DATABASE_CLEANER_REPEAT_TIME;

			con = DatabaseFactory.getInstance().getConnection();
			Statement statement = con.createStatement();

			statement.addBatch("DELETE FROM rank_pvp_system_pvp WHERE (SELECT (lastAccess) FROM characters WHERE " + RPSConfig.CHAR_ID_COLUMN_NAME + " = killer_id) < " + ignoreTime);
			statement.addBatch("DELETE FROM rank_pvp_system_pvp_summary WHERE (SELECT (lastAccess) FROM characters WHERE " + RPSConfig.CHAR_ID_COLUMN_NAME + " = killer_id) < " + ignoreTime);

			statement.executeBatch();
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
				}
			}
			catch (Exception e)
			{
				log.info(e.getMessage());
			}
		}

		log.info(" - PvpTable: Data older than " + Math.round((double) RPSConfig.DATABASE_CLEANER_REPEAT_TIME / (double) 86400000) + " day(s) removed.");
	}

	private static class PvpTableSchedule implements Runnable
	{
		public PvpTableSchedule()
		{

		}

		@Override
		public void run()
		{
			if (!TopTable.getInstance().isUpdating())
			{
				int[] up = PvpTable.getInstance().updateDB();

				if (up[0] == 0)
				{
					log.info("PvpTable: Data updated [" + up[1] + " inserts and " + up[2] + " updates] <<< Next update at " + RPSUtil.timeToString(Calendar.getInstance().getTimeInMillis() + RPSConfig.PVP_TABLE_UPDATE_INTERVAL));
				}

				// update RPC here:
				if (RPSConfig.RPC_REWARD_ENABLED || RPSConfig.RANK_RPC_ENABLED || RPSConfig.RPC_TABLE_FORCE_UPDATE_ENABLED)
				{
					RPCTable.getInstance().updateDB();
				}

				ThreadPoolManager.getInstance().schedule(new PvpTableSchedule(), RPSConfig.PVP_TABLE_UPDATE_INTERVAL);
			}
			else
			{
				log.info("PvpTable: Waiting for update. <<< Next try for 30 seconds.");
				ThreadPoolManager.getInstance().schedule(new PvpTableSchedule(), 30000);
			}
		}
	}
}