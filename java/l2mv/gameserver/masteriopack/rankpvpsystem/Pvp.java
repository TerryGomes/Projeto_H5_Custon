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

import java.util.Calendar;

/**
 * Class contains informations about victim. <br>
 * Question: How many times Killer killed the Victim? <br>
 * Answer: getKills().
 * @author Masterio
 */
public class Pvp
{
	private int _victimId = 0;
	private int _kills = 0;
	private int _killsToday = 0;
	private int _killsLegal = 0;
	private int _killsLegalToday = 0;
	private long _rankPoints = 0;
	private long _rankPointsToday = 0;

	private long _killTime = 0; // store date and time, used in anti-farm options.
	private long _killDay = 0; // store only date without time.

	private byte _dbStatus = DBStatus.INSERTED;

	public void increaseKills()
	{
		_kills++;
		onUpdate();
	}

	public void increaseKillsToday()
	{
		_killsToday++;
		onUpdate();
	}

	public void increaseKillsLegal()
	{
		_killsLegal++;
		onUpdate();
	}

	public void increaseKillsLegalToday()
	{
		_killsLegalToday++;
		onUpdate();
	}

	public void increaseRankPointsBy(long rankPoints)
	{
		_rankPoints += rankPoints;
		onUpdate();
	}

	public void increaseRankPointsTodayBy(long rankPoints)
	{
		_rankPointsToday += rankPoints;
		onUpdate();
	}

	/**
	 * @return the _victimObjId
	 */
	public int getVictimId()
	{
		return _victimId;
	}

	/**
	 * @param victimId the _victimId to set
	 */
	public void setVictimId(int victimId)
	{
		_victimId = victimId;
		onUpdate();
	}

	/**
	 * @return the _kills
	 */
	public int getKills()
	{
		return _kills;
	}

	/**
	 * @param kills the _kills to set
	 */
	public void setKills(int kills)
	{
		_kills = kills;
		onUpdate();
	}

	/**
	 * @return the _killsToday
	 */
	public int getKillsToday()
	{
		if (!checkToday())
		{
			return 0;
		}

		return _killsToday;
	}

	/**
	 * @param killsToday the _killsToday to set
	 */
	public void setKillsToday(int killsToday)
	{
		_killsToday = killsToday;
		onUpdate();
	}

	/**
	 * @return the _killsLegal
	 */
	public int getKillsLegal()
	{
		return _killsLegal;
	}

	/**
	 * @param killsLegal the _killsLegal to set
	 */
	public void setKillsLegal(int killsLegal)
	{
		_killsLegal = killsLegal;
		onUpdate();
	}

	/**
	 * @return the _killsLegalToday
	 */
	public int getKillsLegalToday()
	{
		if (!checkToday())
		{
			return 0;
		}

		return _killsLegalToday;
	}

	/**
	 * @param killsLegalToday the _killsLegalToday to set
	 */
	public void setKillsLegalToday(int killsLegalToday)
	{
		_killsLegalToday = killsLegalToday;
		onUpdate();
	}

	/**
	 * @return the _rankPoints
	 */
	public long getRankPoints()
	{
		return _rankPoints;
	}

	/**
	 * @param rankPoints the _rankPoints to set
	 */
	public void setRankPoints(long rankPoints)
	{
		_rankPoints = rankPoints;
		onUpdate();
	}

	/**
	 * @return the _rankPointsToday
	 */
	public long getRankPointsToday()
	{
		if (!checkToday())
		{
			return 0;
		}

		return _rankPointsToday;
	}

	/**
	 * @param rankPointsToday the _rankPointsToday to set
	 */
	public void setRankPointsToday(long rankPointsToday)
	{
		_rankPointsToday = rankPointsToday;
		onUpdate();
	}

	/**
	 * @return the _killTime
	 */
	public long getKillTime()
	{
		return _killTime;
	}

	/**
	 * @param killTime the _killTime to set
	 */
	public void setKillTime(long killTime)
	{
		_killTime = killTime;
		onUpdate();
	}

	/**
	 * @return the _killDay
	 */
	public long getKillDay()
	{
		return _killDay;
	}

	/**
	 * @param killDay the _killDay to set
	 */
	public void setKillDay(long killDay)
	{
		_killDay = killDay;
		onUpdate();
	}

	/**
	 * @return the _dbStatus
	 */
	public byte getDbStatus()
	{
		return _dbStatus;
	}

	/**
	 * @param dbStatus the _dbStatus to set
	 */
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

	/** If Kill Day == System Day
	 * @return */
	private boolean checkToday()
	{

		Calendar c = Calendar.getInstance();

		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.HOUR, 0);
		long systemDay = c.getTimeInMillis(); // date

		if (_killDay != systemDay)
		{
			return false;
		}

		return true;
	}

	/**
	 * Clear KillsToday, KillsLegalToday, RankPointsToday. It not cause DBStatus change.
	 */
	public void resetDailyFields()
	{
		_killsToday = 0;
		_killsLegalToday = 0;
		_rankPointsToday = 0;
	}
}
