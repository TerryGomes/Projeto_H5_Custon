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

import java.util.Calendar;

import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.UserInfo;

/**
 * Class is used on PvP. Manage rewards, points, ranks and checking all conditions.
 * @author Masterio
 */
public class RankPvpSystem
{
	private Player _killer = null;
	private Player _victim = null;

	private final long _protectionTime = RPSConfig.PROTECTION_TIME_RESET;
	private boolean _protectionTimeEnabled = false;

	public RankPvpSystem(Player killer, Player victim)
	{
		_victim = victim;
		_killer = killer;
	}

	/**
	 * Executed when kill player (from killer side)
	 */
	public synchronized void doPvp()
	{
		if (checkBasicConditions(_killer, _victim))
		{
			// set pvp times:
			Calendar c = Calendar.getInstance();
			long systemTime = c.getTimeInMillis(); // date & time

			c.set(Calendar.MILLISECOND, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.HOUR, 0);
			long systemDay = c.getTimeInMillis(); // date

			// get killer - victim pvp:
			Pvp pvp = PvpTable.getInstance().getPvp(_killer.getObjectId(), _victim.getObjectId(), systemDay, false);

			// check time protection:
			_protectionTimeEnabled = checkProtectionTime(pvp, systemTime);

			String nextRewardTime = "";
			if (_protectionTimeEnabled)
			{
				nextRewardTime = calculateTimeToString(systemTime, pvp.getKillTime());
			}

			// get Killer and Victim pvp stats:
			PvpSummary killerPvpSummary = PvpTable.getInstance().getKillerPvpSummary(_killer.getObjectId(), systemDay, false, false);
			PvpSummary victimPvpSummary = PvpTable.getInstance().getKillerPvpSummary(_victim.getObjectId(), systemDay, true, false);

			// update pvp:
			increasePvp(pvp, killerPvpSummary, systemTime, systemDay);

			// update killer Alt+T info.
			if (RPSConfig.PVP_COUNTER_FOR_ALTT_ENABLED)
			{
				if (RPSConfig.PVP_COUNTER_FOR_ALTT_LEGAL_KILLS_ONLY)
				{
					_killer.setPvpKills(killerPvpSummary.getTotalKillsLegal());
				}
				else
				{
					_killer.setPvpKills(killerPvpSummary.getTotalKills());
				}

				_killer.sendPacket(new UserInfo(_killer));
				_killer.broadcastUserInfo(true);
			}

			// start message separator:
			_killer.sendMessage("----------------------------------------------------------------");
			_victim.sendMessage("----------------------------------------------------------------");

			int[] pvpPointsTable = null; // used for check if points are added for decrease pvp exp system.

			// PvP Reward || Rank PvP Reward || RPC || RP
			if (checkRewardProtections(pvp))
			{
				// give PvP Reward:
				if (checkPvpRewardConditions(pvp))
				{
					RewardTable.getInstance().giveReward(_killer);
				}

				// give Rank PvP Reward:
				if (checkRankPvpRewardConditions(pvp))
				{
					RewardTable.getInstance().giveRankPvpRewards(_killer, victimPvpSummary.getRankId());
				}

				// add RPC:
				if (checkRpcConditions(pvp))
				{
					RPCTable.getInstance().addRpcForPlayer(_killer.getObjectId(), RPSConfig.RPC_REWARD_AMOUNT);
				}

				// add RP:
				if (checkRankPointsConditions(pvp))
				{
					pvpPointsTable = addRankPointsForKiller(pvp, killerPvpSummary, victimPvpSummary);
				}
			}

			// decrease PvP Exp (if not decreased before):
			if (RPSConfig.PVP_EXP_DECREASE_ENABLED && !RPSConfig.PVP_EXP_DECREASE_ON_LEGAL_KILL_ENABLED && pvpPointsTable == null)
			{
				pvpPointsTable = getPointsForKill(pvp, killerPvpSummary, victimPvpSummary, _killer, _victim);
				int loseExp = victimPvpSummary.decreasePvpExpBy(pvpPointsTable);

				if (loseExp > 0)
				{
					_victim.sendMessage("You lose " + loseExp + " PvP exp.");
				}
			}

			if (_protectionTimeEnabled)
			{
				_killer.sendMessage("Protection Time is activated for: " + nextRewardTime);
			}

			// update nick and title colors:
			updateNickAndTitleColor(_killer, killerPvpSummary);

			// show message:
			shoutPvpMessage(pvp);

			// end message separator:
			_killer.sendMessage("----------------------------------------------------------------");
			_victim.sendMessage("----------------------------------------------------------------");

			if (RPSConfig.DEATH_MANAGER_DETAILS_ENABLED)
			{
				_victim.getRPSCookie().setDeathStatus(new RPSHtmlDeathStatus(_killer));
			}

			if (RPSConfig.PVP_INFO_COMMAND_ON_DEATH_ENABLED)
			{
				if (!RPSProtection.isInDMRestrictedZone(_killer))
				{
					RPSHtmlPvpStatus.sendPage(_victim, _killer);
				}
			}

			// shout defeat message, if victim have combo level > 0
			if (_victim.getRPSCookie().isComboKillActive())
			{
				_victim.getRPSCookie().getComboKill().shoutDefeatMessage(_victim);
				_victim.getRPSCookie().setComboKill(null); // reset current combo for victim.
			}
		}
	}

	/** Check all conditions and increase or not PvP <br>(rank points are increased in addRankPointsForKiller() method)
	 * @param pvp
	 * @param killerPvpSummary
	 * @param systemTime
	 * @param systemDay
	 */
	private void increasePvp(Pvp pvp, PvpSummary killerPvpSummary, long systemTime, long systemDay)
	{
		// killerPvpSummary today fields are updated on the end.

		// add normal kills, checking is outside this method:
		pvp.increaseKills();
		killerPvpSummary.addTotalKills(1);

		if (pvp.getKillDay() == systemDay)
		{ // daily
			pvp.increaseKillsToday();
		}
		else
		{ // daily
			pvp.setKillsToday(1);
		}

		if (RPSProtection.checkWar(_killer, _victim))
		{
			killerPvpSummary.addTotalWarKills(1);
		}

		// shout combo kill, if legal kill protection is disabled:
		if (RPSConfig.COMBO_KILL_ENABLED && !RPSConfig.COMBO_KILL_PROTECTION_WITH_LEGAL_KILL_ENABLED)
		{
			shoutComboKill(systemTime);
		}

		if (checkLegalKillConditions(_killer, _victim, pvp))
		{
			if (!_protectionTimeEnabled)
			{
				pvp.increaseKillsLegal();
				killerPvpSummary.addTotalKillsLegal(1);

				if (RPSProtection.checkWar(_killer, _victim))
				{
					killerPvpSummary.addTotalWarKillsLegal(1);
				}

				if (pvp.getKillDay() == systemDay)
				{
					pvp.increaseKillsLegalToday(); // daily
				}
				else
				{
					pvp.setKillsLegalToday(1); // daily
				}

				// shout combo kill, if legal kill protection is enabled:
				if (RPSConfig.COMBO_KILL_ENABLED && RPSConfig.COMBO_KILL_PROTECTION_WITH_LEGAL_KILL_ENABLED)
				{
					shoutComboKill(systemTime);
				}

				// if protection is OFF set the current kill time.
				pvp.setKillTime(systemTime);
			}
		}

		if (pvp.getKillTime() == 0) // set last kill time if it is initial kill.
		{
			pvp.setKillTime(systemTime);
		}

		pvp.setKillDay(systemDay);

		// update daily fields for killerPvpSummary:
		killerPvpSummary.updateDailyStats(systemDay);

		// used for check not active killers on top list filter:
		killerPvpSummary.setLastKillTime(systemTime);
	}

	/**
	 * Add rank points for kill, and update killer rank. Gives RPC and Rank Rewards for killer. <br> Decrease victim PvP experience if enabled.
	 * @param pvp
	 * @param killerPvpSummary
	 * @param victimPvpSummary
	 * @return Points for Kill for this PvP.
	 */
	private int[] addRankPointsForKiller(Pvp pvp, PvpSummary killerPvpSummary, PvpSummary victimPvpSummary)
	{
		int[] points_table = getPointsForKill(pvp, killerPvpSummary, victimPvpSummary, _killer, _victim);

		// old rank id:
		int oldRankId = killerPvpSummary.getRankId();
		int oldMaxRankId = killerPvpSummary.getMaxRankId();

		// increase rank points:
		pvp.increaseRankPointsBy(points_table[0]);
		pvp.increaseRankPointsTodayBy(points_table[0]);

		// required update this object for increasePvp() (only in this method):
		killerPvpSummary.addTotalRankPoints(points_table[0]);
		killerPvpSummary.addTotalRankPointsToday(points_table[0]); // required for show in chat below.
		killerPvpSummary.increasePvpExp(points_table[0]); // add pvp exp, and update rankId and maxRankId.

		// decrease victim PvP experience:
		int loseExp = 0;
		if (RPSConfig.PVP_EXP_DECREASE_ENABLED && RPSConfig.PVP_EXP_DECREASE_ON_LEGAL_KILL_ENABLED)
		{
			loseExp = victimPvpSummary.decreasePvpExpBy(points_table);
		}

		// add rank RPC for killer:
		if (RPSConfig.RANK_RPC_ENABLED)
		{
			// cut RPC if enabled:
			if (RPSConfig.RANK_POINTS_CUT_ENABLED && killerPvpSummary.getRank().getRpc() < victimPvpSummary.getRank().getRpc())
			{
				RPCTable.getInstance().addRpcForPlayer(_killer.getObjectId(), killerPvpSummary.getRank().getRpc());
			}
			else
			{
				RPCTable.getInstance().addRpcForPlayer(_killer.getObjectId(), victimPvpSummary.getRank().getRpc());
			}
		}

		// new rank shout (include deleveled ranks):
		if (oldRankId < killerPvpSummary.getRankId())
		{
			_killer.sendMessage("You have reached a new rank: " + RankTable.getInstance().getRankById(killerPvpSummary.getRankId()).getName());

			// give rank rewards and skill rewards for new ranks (exclude deleveled ranks):
			if (RPSConfig.RANK_LEVEL_REWARD_ENABLED || RPSConfig.RANK_SKILL_REWARD_ENABLED)
			{
				// if player reached 1 or more new ranks.
				// oldRankId+1 because we want get reward for new rank.
				for (int i = oldRankId + 1; i <= killerPvpSummary.getRankId(); i++)
				{
					if (i > oldMaxRankId)
					{
						if (RPSConfig.RANK_LEVEL_REWARD_ENABLED)
						{
							RewardTable.getInstance().giveRankLevelRewards(_killer, i);
						}

						if (RPSConfig.RANK_SKILL_REWARD_ENABLED)
						{
							RewardTable.getInstance().giveRankSkillRewards(_killer, i);
						}
					}
				}
			}
		}

		// shout current PvP informations:
		if (RPSConfig.RANK_SHOUT_INFO_ON_KILL_ENABLED)
		{
			_killer.sendMessage("You have obtained " + points_table[0] + " Rank Points for kill " + _victim.getName());

			showBonusDataPointsForKiller(points_table);

			_killer.sendMessage("Your Rank Points: " + killerPvpSummary.getTotalRankPoints() + " (" + killerPvpSummary.getTotalRankPointsToday() + " today)");
			_victim.sendMessage("You have been killed by " + _killer.getName() + " (" + killerPvpSummary.getRank().getName() + ")");
		}

		if (loseExp > 0)
		{
			_victim.sendMessage("You lose " + loseExp + " PvP exp.");
		}

		return points_table;
	}

	/**
	 * Shout current kills, kills_today, etc.
	 * @param pvp
	 */
	private void shoutPvpMessage(Pvp pvp)
	{

		if (RPSConfig.TOTAL_KILLS_IN_SHOUT_ENABLED)
		{
			if (pvp.getKills() > 1)
			{
				String timeStr1 = " times";
				if (pvp.getKillsToday() == 1)
				{
					timeStr1 = "st time";
				}

				if (RPSConfig.PROTECTION_TIME_RESET == 0)
				{
					_victim.sendMessage(_killer.getName() + " killed you " + pvp.getKills() + " times");
					_killer.sendMessage("You have killed " + _victim.getName() + " " + pvp.getKills() + " times");
				}
				else
				{
					_victim.sendMessage(_killer.getName() + " killed you " + pvp.getKills() + " times (" + pvp.getKillsToday() + "" + timeStr1 + " today)");
					_killer.sendMessage("You have killed " + _victim.getName() + " " + pvp.getKills() + " times (" + pvp.getKillsToday() + "" + timeStr1 + " today)");
				}
			}
			else
			{
				_victim.sendMessage("This is the first time you have been killed by " + _killer.getName());
				_killer.sendMessage("You have killed " + _victim.getName() + " for the first time");
			}
		}
		else if (pvp.getKillsLegal() > 1)
		{
			String timeStr1 = " times";
			if (pvp.getKillsLegalToday() == 1)
			{
				timeStr1 = "st time";
			}

			if (RPSConfig.PROTECTION_TIME_RESET == 0)
			{
				_victim.sendMessage(_killer.getName() + " killed you " + pvp.getKillsLegal() + " times legally");
				_killer.sendMessage("You have killed " + _victim.getName() + " " + pvp.getKillsLegal() + " times legally");
			}
			else
			{
				_victim.sendMessage(_killer.getName() + " killed you " + pvp.getKillsLegal() + " times (" + pvp.getKillsLegalToday() + "" + timeStr1 + " today) legally");
				_killer.sendMessage("You have killed " + _victim.getName() + " " + pvp.getKillsLegal() + " times (" + pvp.getKillsLegalToday() + "" + timeStr1 + " today) legally");
			}
		}
		else
		{
			_victim.sendMessage("This is the first time you have been killed by " + _killer.getName() + " legally.");
			_killer.sendMessage("You have killed " + _victim.getName() + " for the first time legally.");
		}

	}

	private void showBonusDataPointsForKiller(int[] points_table)
	{
		// show bonus points data for killer:
		String war = "";
		String area = "";
		String combo = "";

		if (points_table[1] > 0)
		{
			war = "war: " + points_table[1] + ", ";
		}

		if (points_table[2] > 0)
		{
			area = "area: " + points_table[2] + ", ";
		}

		if (points_table[3] > 0)
		{
			combo = "combo: " + points_table[3] + ", ";
		}

		if (points_table[1] > 0 || points_table[2] > 0 || points_table[3] > 0)
		{
			String msg = war + area + combo;
			msg = msg.substring(0, msg.length() - 2);

			_killer.sendMessage("Bonus RP (" + msg + ")");
		}
	}

	/**
	 * Update nick and title color for character with specified rank.
	 * @param killer - can not be null
	 * @param killerPvpSummary - if null then PvpSummary will be found.
	 */
	public static void updateNickAndTitleColor(Player killer, PvpSummary killerPvpSummary)
	{
		if ((killer == null) || (!RPSConfig.GM_IGNORE_ENABLED && killer.isGM()))
		{
			return;
		}

		PvpSummary PvpSummary = killerPvpSummary;

		if (PvpSummary == null)
		{
			PvpSummary = PvpTable.getInstance().getKillerPvpSummary(killer.getObjectId(), true, false);
			if (PvpSummary == null)
			{
				return;
			}
		}

		Rank rank = PvpSummary.getRank();

		if (rank == null)
		{
			return;
		}

		if (RPSConfig.NICK_COLOR_ENABLED && killer.getNameColor() != rank.getNickColor() && rank.getNickColor() > -1)
		{
			killer.setNameColor(rank.getNickColor());
			killer.sendPacket(new UserInfo(killer));
			killer.broadcastUserInfo(true);
		}

		if (RPSConfig.TITLE_COLOR_ENABLED && killer.getTitleColor() != rank.getTitleColor() && rank.getTitleColor() > -1)
		{
			killer.setTitleColor(rank.getTitleColor());
			killer.broadcastUserInfo(true);
		}
	}

	public static final String calculateTimeToString(long sys_time, long kill_time)
	{
		long TimeToRewardInMilli = ((kill_time + (RPSConfig.PROTECTION_TIME_RESET)) - sys_time);
		long TimeToRewardHours = TimeToRewardInMilli / 3600000;
		long TimeToRewardMinutes = (TimeToRewardInMilli % 3600000) / 60000;
		long TimeToRewardSeconds = (TimeToRewardInMilli % 60000) / 1000;

		String H = Long.toString(TimeToRewardHours);
		String M = Long.toString(TimeToRewardMinutes);
		String S = Long.toString(TimeToRewardSeconds);

		if (TimeToRewardHours <= 9)
		{
			H = "0" + H;
		}
		if (TimeToRewardMinutes <= 9)
		{
			M = "0" + M;
		}
		if (TimeToRewardSeconds <= 9)
		{
			S = "0" + S;
		}

		return H + ":" + M + ":" + S;
	}

	/**
	 * Calculate Rank Points awarded for Killer<br> [0] - Sum of Rank Points and Bonus Points. <br> [1] - Bonus points for War.<br>[2] - Bonus points for Area.<br>[3] - Bonus points for Combo.
	 * @param pvp
	 * @param killerPvpSummary
	 * @param victimPvpSummary
	 * @param killer
	 * @param victim
	 * @return Returns table of Rank Points awarded for Killer<br> [0] - Sum of Rank Points and Bonus Points. <br> [1] - Bonus points for War.<br>[2] - Bonus points for Area.<br>[3] - Bonus points for Combo.
	 */
	private int[] getPointsForKill(Pvp pvp, PvpSummary killerPvpSummary, PvpSummary victimPvpSummary, Player killer, Player victim)
	{

		int points = 0;
		int points_war = 0;
		int points_bonus_zone = 0;
		int points_combo = 0;

		// add basic points:
		if (RPSConfig.RANK_POINTS_DOWN_COUNT_ENABLED)
		{
			int i = 1;

			for (Integer value : RPSConfig.RANK_POINTS_DOWN_AMOUNTS)
			{
				if (pvp.getKillsLegalToday() == i)
				{
					points = value;
					break;
				}
				i++;
			}
		}
		else
		{
			points = victimPvpSummary.getRank().getPointsForKill();
		}

		// cut points if enabled:
		if (RPSConfig.RANK_POINTS_CUT_ENABLED && killerPvpSummary.getRank().getPointsForKill() < points)
		{
			points = killerPvpSummary.getRank().getPointsForKill();
		}

		// add war points, if Killer's clan and Victim's clan at war:
		if (RPSConfig.WAR_KILLS_ENABLED && points > 0 && RPSConfig.WAR_RANK_POINTS_RATIO > 1 && RPSProtection.checkWar(killer, victim))
		{
			points_war = (int) Math.floor((points * RPSConfig.WAR_RANK_POINTS_RATIO) - points);
		}

		// add bonus zone points, if Killer is inside bonus zone:
		if (points > 0)
		{
			double zone_ratio_killer = RPSProtection.getZoneBonusRatio(killer);
			if (zone_ratio_killer > 1)
			{
				points_bonus_zone = (int) Math.floor((points * zone_ratio_killer) - points);
			}
		}

		// add combo points:
		if (RPSConfig.COMBO_KILL_RANK_POINTS_RATIO_ENABLED && killer.getRPSCookie().getComboKill() != null)
		{
			double combo_ratio = killer.getRPSCookie().getComboKill().getComboKillRankPointsRatio();
			if (combo_ratio > 1)
			{
				points_combo = (int) Math.floor((points * combo_ratio) - points);
			}
		}

		points = points + points_war + points_bonus_zone + points_combo;

		int[] points_table = new int[4];
		points_table[0] = points;
		points_table[1] = points_war;
		points_table[2] = points_bonus_zone;
		points_table[3] = points_combo;

		return points_table;
	}

	/**
	 * Method used for Combo Kill System.
	 * @param killTime
	 */
	private void shoutComboKill(long killTime)
	{
		// create new combo instance if not exists:
		if (_killer.getRPSCookie().getComboKill() == null)
		{
			_killer.getRPSCookie().setComboKill(new RPSHtmlComboKill());
		}
		// reset old combo if kill reseter enabled
		else if (RPSConfig.COMBO_KILL_RESETER > 0 && (killTime - _killer.getRPSCookie().getComboKill().getLastKillTime()) > RPSConfig.COMBO_KILL_RESETER * 1000)
		{
			_killer.getRPSCookie().setComboKill(new RPSHtmlComboKill());
		}

		// rise combo level and shout message:
		if (_killer.getRPSCookie().getComboKill().addVictim(_victim.getObjectId(), killTime))
		{
			_killer.getRPSCookie().getComboKill().shoutComboKill(_killer, _victim);
		}
	}

	/**
	 * Returns TRUE if protection time is activated.
	 * @param pvp
	 * @param systemTime
	 * @return
	 */
	private boolean checkProtectionTime(Pvp pvp, long systemTime)
	{
		if (RPSConfig.PROTECTION_TIME_RESET > 0 && pvp.getKillTime() + _protectionTime > systemTime)
		{
			return true;
		}

		return false;
	}

	/**
	 * Check Basic conditions for RPS, it's mean check if can I add +1 into kills and kills_today.<br>
	 * Basic mean: if killer is: in olympiad, in event, in restricted zone, etc.
	 * @param killer
	 * @param victim
	 * @return TRUE if conditions are correct.
	 */
	private boolean checkBasicConditions(Player killer, Player victim)
	{

		if (killer == null || victim == null || killer.isDead() || killer.isAlikeDead())
		{
			return false;
		}

		if (RPSProtection.checkEvent(killer))
		{
			return false;
		}

		if (RPSConfig.GM_IGNORE_ENABLED && (killer.isGM() || victim.isGM()))
		{
			killer.sendMessage("Rank PvP System ignore GM characters!");
			return false;
		}

		// check if killer is in allowed zone & not in restricted zone:
		if (!RPSProtection.isInPvpAllowedZone(killer) || RPSProtection.isInPvpRestrictedZone(killer))
		{
			if ((RPSConfig.RPC_REWARD_ENABLED || RPSConfig.PVP_REWARD_ENABLED || RPSConfig.RANK_PVP_REWARD_ENABLED) && RPSConfig.RANKS_ENABLED)
			{
				killer.sendMessage("You can't earn Reward or Rank Points in restricted zone");
				return false;
			}

			return false;
		}

		if (!RPSProtection.antiFarmCheck(killer, victim))
		{
			return false;
		}

		return true;
	}

	private boolean checkLegalKillProtection(Pvp pvp)
	{
		// 1: check total legal kills:
		// 2: check total legal kills today:
		if ((RPSConfig.LEGAL_KILL_PROTECTION > 0 && pvp.getKillsLegal() > RPSConfig.LEGAL_KILL_PROTECTION)
					|| (RPSConfig.DAILY_LEGAL_KILL_PROTECTION > 0 && pvp.getKillsLegalToday() > RPSConfig.DAILY_LEGAL_KILL_PROTECTION))
		{
			return false;
		}

		// 3. check protectionTimeEnabled
		if (_protectionTimeEnabled)
		{
			return false;
		}

		return true;
	}

	/**
	 * Returns TRUE if okay.<br>
	 * Check the reward protections like:<br>
	 *  REWARD_FOR_INNOCENT_KILL, <br>REWARD_FOR_PK_KILLER, <br>REWARD_LEGAL_KILL.
	 * @param pvp
	 * @return
	 */
	private boolean checkRewardProtections(Pvp pvp)
	{
		// if PK mode is disabled:
		if (!RPSConfig.REWARD_FOR_INNOCENT_KILL_ENABLED && _victim.getPvpFlag() == 0 && _victim.getKarma() == 0)
		{
			_killer.sendMessage("You can't earn reward on innocent players!");
			return false;
		}

		// if reward for PK kill is disabled:
		if (!RPSConfig.REWARD_FOR_PK_KILLER_ENABLED && _victim.getKarma() > 0)
		{
			_killer.sendMessage("No reward for kill player with Karma!");
			return false;
		}

		if (RPSConfig.REWARD_LEGAL_KILL_ENABLED && !checkLegalKillProtection(pvp))
		{
			return false;
		}

		return true;
	}

	/**
	 * Return True if it's Legal Kill (without farm check).
	 * @param killer
	 * @param victim
	 * @param pvp
	 * @return
	 */
	private boolean checkLegalKillConditions(Player killer, Player victim, Pvp pvp)
	{
		if ((RPSConfig.LEGAL_KILL_MIN_LVL > victim.getLevel()) || (RPSConfig.LEGAL_KILL_MIN_LVL > killer.getLevel()))
		{
			return false;
		}

		if (!RPSConfig.LEGAL_KILL_FOR_INNOCENT_KILL_ENABLED && victim.getKarma() == 0 && victim.getPvpFlag() == 0)
		{
			return false;
		}

		if ((!RPSConfig.LEGAL_KILL_FOR_PK_KILLER_ENABLED && victim.getKarma() > 0) || !checkLegalKillProtection(pvp))
		{
			return false;
		}

		return true;
	}

	private boolean checkRpcConditions(Pvp pvp)
	{

		if (!RPSConfig.RPC_REWARD_ENABLED)
		{
			return false;
		}

		if ((RPSConfig.RPC_REWARD_MIN_LVL > _victim.getLevel()) || (RPSConfig.RPC_REWARD_MIN_LVL > _killer.getLevel()))
		{
			_killer.sendMessage("You or your target have not required level!");
			return false;
		}

		return true;
	}

	private boolean checkPvpRewardConditions(Pvp pvp)
	{

		if (!RPSConfig.PVP_REWARD_ENABLED)
		{
			return false;
		}

		if ((RPSConfig.PVP_REWARD_MIN_LVL > _victim.getLevel()) || (RPSConfig.PVP_REWARD_MIN_LVL > _killer.getLevel()))
		{
			_killer.sendMessage("You or your target have not required level!");
			return false;
		}

		return true;
	}

	private boolean checkRankPvpRewardConditions(Pvp pvp)
	{

		if (!RPSConfig.RANK_PVP_REWARD_ENABLED)
		{
			return false;
		}

		if ((RPSConfig.RANK_PVP_REWARD_MIN_LVL > _victim.getLevel()) || (RPSConfig.RANK_PVP_REWARD_MIN_LVL > _killer.getLevel()))
		{
			_killer.sendMessage("You or your target have not required level!");
			return false;
		}

		return true;
	}

	private boolean checkRankPointsConditions(Pvp pvp)
	{

		if (!RPSConfig.RANKS_ENABLED)
		{
			return false;
		}

		if ((RPSConfig.RANK_POINTS_MIN_LVL > _victim.getLevel()) || (RPSConfig.RANK_POINTS_MIN_LVL > _killer.getLevel()))
		{
			_killer.sendMessage("You or your target have not required level!");
			return false;
		}

		return true;
	}

	/**
	 * @return the _killer
	 */
	public Player getKiller()
	{
		return _killer;
	}

	/**
	 * @param killer the _killer to set
	 */
	public void setKiller(Player killer)
	{
		_killer = killer;
	}

	/**
	 * @return the _victim
	 */
	public Player getVictim()
	{
		return _victim;
	}

	/**
	 * @param victim the _victim to set
	 */
	public void setVictim(Player victim)
	{
		_victim = victim;
	}
}