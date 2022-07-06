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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2mv.gameserver.masteriopack.rankpvpsystem.RPSConfig.DecreaseMethod;

/**
 * The class contains summary of killer PvP's, and list of all PvP's with his victims. Contains Rank informations.
 * @author Masterio
 */
public class PvpSummary
{
	private int _killerId = 0; // killer id
	private int _rankId = 0; // id of current rank

	// calculated fields:
	private int _totalKills = 0; // sum of kill's
	private int _totalKillsLegal = 0; // sum of legal kill's
	private long _totalRankPoints = 0; // sum of rank point's
	private long _totalRankPointsToday = 0; // sum of rank point's in this day

	// stored fields (in DB):
	private long _pvpExp = 0; // killer PvP experience. [1 RP == 1 EXP]
	private int _totalWarKills = 0; // sum of kill's on victims from war clan
	private int _totalWarKillsLegal = 0; // sum of legal kill's on victims from war clan
	private int _maxRankId = 0; // maximum rank obtained by player. It is never decrease (reward security).

	/**
	 * [victimID, PvP] contains Killer's PvP data (victim, kills on victim, last kill time, etc.)
	 */
	private Map<Integer, Pvp> _victimPvpTable = new ConcurrentHashMap<>();

	private long _lastKillTime = 0; // store last <legal> kill time
	private byte _dbStatus = DBStatus.INSERTED;

	public void increasePvpExp(long value)
	{
		if (value > 0)
		{
			_pvpExp += value;
			updateRankId();
			onUpdate();
		}
	}

	/**
	 * Decrease Victim pvp exp.
	 * @param values - received from getPointsForKill().
	 * @return
	 */
	public int decreasePvpExpBy(int[] values)
	{
		if (values == null || values.length != 4)
		{
			return 0;
		}

		int value = 0;

		switch (RPSConfig.PVP_EXP_DECREASE_METHOD)
		{
		case DecreaseMethod.FULL:
			value = values[0];
			break;
		case DecreaseMethod.BASIC:
			value = values[0] - values[1] - values[2] - values[3];
			break;
		case DecreaseMethod.CONSTANT:
			value = RPSConfig.PVP_EXP_DECREASE_CONSTANT;
			break;
		case DecreaseMethod.FRACTION:
			value = (int) ((RPSConfig.PVP_EXP_DECREASE_FRACTION) * values[0]);
			break;
		default:
			break;
		}

		if (value > 0)
		{
			_pvpExp -= value;

			if (_pvpExp < 0)
			{
				_pvpExp = 0;
			}

			updateRankId();
			onUpdate();
		}

		return value;
	}

	public void addTotalKills(int kills)
	{
		_totalKills += kills;
	}

	public void addTotalKillsLegal(int killsLegal)
	{
		_totalKillsLegal += killsLegal;
		onUpdate();
	}

	/** Add Rank Points to Total Rank Points and update Rank. <br>
	 * @param rankPoints */
	public void addTotalRankPoints(long rankPoints)
	{
		_totalRankPoints += rankPoints;
	}

	public void addTotalRankPointsToday(long rankPointsToday)
	{
		_totalRankPointsToday += rankPointsToday;
	}

	public void addTotalWarKills(int warKills)
	{
		_totalWarKills += warKills;
		onUpdate();
	}

	public void addTotalWarKillsLegal(int warKillsLegal)
	{
		_totalWarKillsLegal += warKillsLegal;
	}

	/**
	 * @return the _killerId
	 */
	public int getKillerId()
	{
		return _killerId;
	}

	/**
	 * @param killerId the _killerId to set
	 */
	public void setKillerId(int killerId)
	{
		_killerId = killerId;
	}

	/**
	 * @return the _totalKills
	 */
	public int getTotalKills()
	{
		return _totalKills;
	}

	/**
	 * @param totalKills the _totalKills to set
	 */
	public void setTotalKills(int totalKills)
	{
		_totalKills = totalKills;
	}

	/**
	 * @return the _totalKillsLegal
	 */
	public int getTotalKillsLegal()
	{
		return _totalKillsLegal;
	}

	/**
	 * @param totalKillsLegal the _totalKillsLegal to set
	 */
	public void setTotalKillsLegal(int totalKillsLegal)
	{
		_totalKillsLegal = totalKillsLegal;
		onUpdate();
	}

	/**
	 * @return the _totalRankPoints
	 */
	public long getTotalRankPoints()
	{
		return _totalRankPoints;
	}

	/**
	 * Set Total Rank Points and update Rank.<br>
	 * Is NOT recommended use this method in LOOP, because this method use: <b>onUpdateRankPoints()</b>.
	 * @param totalRankPoints the _totalRankPoints to set
	 */
	public void setTotalRankPoints(long totalRankPoints)
	{
		_totalRankPoints = totalRankPoints;
	}

	/**
	 * Set Total Rank Points and update Rank.<br>
	 * The Rank is <b>NOT</b> updated here!
	 * @param totalRankPoints
	 */
	public void setTotalRankPointsOnly(long totalRankPoints)
	{
		_totalRankPoints = totalRankPoints;
	}

	/**
	 * @return the _totalRankPointsToday
	 */
	public long getTotalRankPointsToday()
	{
		return _totalRankPointsToday;
	}

	/**
	 * @param totalRankPointsToday the _totalRankPointsToday to set
	 */
	public void setTotalRankPointsToday(long totalRankPointsToday)
	{
		_totalRankPointsToday = totalRankPointsToday;
	}

	/**
	 * @return the _totalWarKills
	 */
	public int getTotalWarKills()
	{
		return _totalWarKills;
	}

	/**
	 * @param totalWarKills the _totalWarKills to set
	 */
	public void setTotalWarKills(int totalWarKills)
	{
		_totalWarKills = totalWarKills;
		onUpdate();
	}

	/**
	 * @return the _totalWarKillsLegal
	 */
	public int getTotalWarKillsLegal()
	{
		return _totalWarKillsLegal;
	}

	/**
	 * @param totalWarKillsLegal the _totalWarKillsLegal to set
	 */
	public void setTotalWarKillsLegal(int totalWarKillsLegal)
	{
		_totalWarKillsLegal = totalWarKillsLegal;
	}

	/**
	 * @return the _rank
	 */
	public int getRankId()
	{
		return _rankId;
	}

	public Rank getRank()
	{
		return RankTable.getInstance().getRankById(getRankId());
	}

	/**
	 * @param rankId the _rank to set
	 */
	public void setRankId(int rankId)
	{
		_rankId = rankId;
	}

	/** Update current Rank for this character,<br>should be executed always when pvpExp is updated. */
	public void updateRankId()
	{
		Map<Integer, Rank> list = RankTable.getInstance().getRankList();

		if (list == null)
		{
			return;
		}

		// if Pvp Exp equals 0 return minimum rank:
		if (_pvpExp <= 0)
		{
			Rank rank = list.get(1);

			if (rank != null)
			{
				_rankId = rank.getId();
				return;
			}
		}

		int rankId = 1; // ranks starts from id = 1.
		for (Map.Entry<Integer, Rank> e : list.entrySet())
		{
			Rank rank = e.getValue();

			if (rank != null)
			{
				// ranks are checked from rankId == 1, so if the pvpExp is lower than minExp then we found the rank.
				// last iteration of this loop returns the highest rankId.
				if (_pvpExp < rank.getMinExp())
				{
					break;
				}

				rankId = rank.getId();
			}
		}

		_rankId = rankId;

		// update the max rank id
		if (rankId > _maxRankId)
		{
			_maxRankId = rankId;
			onUpdate();
		}
	}

	/**
	 * Updates daily fields like: total kills today, etc.<br>
	 * Other fields are actual (updated on each PvP).
	 * @param systemDay
	 * */
	public void updateDailyStats(long systemDay)
	{
		long totalRankPointsToday = 0;

		for (Map.Entry<Integer, Pvp> e : _victimPvpTable.entrySet())
		{
			Pvp pvp = e.getValue();

			if (pvp != null)
			{
				if (pvp.getKillDay() == systemDay)
				{
					totalRankPointsToday += pvp.getRankPointsToday();
				}
				else
				{
					pvp.resetDailyFields();
				}
			}
		}

		_totalRankPointsToday = totalRankPointsToday;
	}

	public Map<Integer, Pvp> getVictimPvpTable()
	{
		return _victimPvpTable;
	}

	public void setVictimPvpTable(Map<Integer, Pvp> victimPvpTable)
	{
		_victimPvpTable = victimPvpTable;
	}

	/**
	 * Add pvp into victimPvpTable and updates killerPvpSummary.<br>
	 * Used when LOAD from database ONLY.
	 * @param pvp
	 * @return
	 */
	public boolean addVictimPvpOnLoadFromDB(Pvp pvp)
	{
		// add PvP:
		_victimPvpTable.put(pvp.getVictimId(), pvp);

		// update killer pvp stats (only calcualted fields):
		addTotalKills(pvp.getKills());
		addTotalKillsLegal(pvp.getKillsLegal());

		addTotalRankPoints(pvp.getRankPoints());
		addTotalRankPointsToday(pvp.getRankPointsToday());

		// set last kill time:
		if (pvp.getKillTime() > getLastKillTime())
		{
			_lastKillTime = pvp.getKillTime();
		}

		return true;
	}

	public long getLastKillTime()
	{
		return _lastKillTime;
	}

	public void setLastKillTime(long lastKillTime)
	{
		_lastKillTime = lastKillTime;
	}

	public long getPvpExp()
	{
		return _pvpExp;
	}

	public void setPvpExp(long pvpExp)
	{
		_pvpExp = pvpExp;
	}

	public int getMaxRankId()
	{
		return _maxRankId;
	}

	public void setMaxRankId(int maxRankId)
	{
		_maxRankId = maxRankId;
	}

	public byte getDbStatus()
	{
		return _dbStatus;
	}

	public void setDbStatus(byte dbStatus)
	{
		_dbStatus = dbStatus;
	}

	private void onUpdate()
	{
		if (_dbStatus == DBStatus.NONE)
		{
			_dbStatus = DBStatus.UPDATED;
		}
	}
}
