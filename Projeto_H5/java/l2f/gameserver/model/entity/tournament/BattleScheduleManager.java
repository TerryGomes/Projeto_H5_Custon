package l2f.gameserver.model.entity.tournament;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import l2f.commons.annotations.NotNull;
import l2f.commons.annotations.Nullable;
import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ConfigHolder;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.idfactory.IdFactory;
import l2f.gameserver.instancemanager.ServerVariables;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.items.ItemInstance.ItemLocation;
import l2f.gameserver.model.mail.Mail;
import l2f.gameserver.network.serverpackets.ExNoticePostArrived;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.utils.Debug;

public class BattleScheduleManager
{
	private static final Logger LOG = LoggerFactory.getLogger(BattleScheduleManager.class);

	public static final int FIRST_ROUND_INDEX = 0;
	public static final int NOT_LOST_ANY_ROUND_INDEX = Integer.MAX_VALUE;
	public static final int NOT_DETERMINED_FINAL_POSITION = -1;

	private final Map<Integer, List<BattleRecord>> _battlesPerRound = new HashMap<Integer, List<BattleRecord>>(8);
	private int _lastBattleObjectId = -1;
	private long _lastBattleDate = -1L;
	private final ScriptEngine _javaScriptEngine;
	private ScheduledFuture<?> _startBattlePeriodThread = null;
	private ScheduledFuture<?> _startRegisterPeriodThread = null;

	private BattleScheduleManager()
	{
		final ScriptEngineManager mgr = new ScriptEngineManager();
		_javaScriptEngine = mgr.getEngineByName("JavaScript");

		loadScheduleFromDatabase();
		fixMissedBattlesDates();
		TournamentTeamsManager.getInstance();
		initializeLostBattlesRecords();
		BattleNotificationManager.getInstance();
		ActiveBattleManager.initialize();
		BattleObservationManager.initialize();

		// Synerge - Set and check periods for automatic tournament start
		setCheckNewPeriodDates();
	}

	public boolean isScheduleActive()
	{
		if (getCurrentRoundIndex() < 0)
		{
			return false;
		}
		if (_lastBattleDate > System.currentTimeMillis())
		{
			return true;
		}
		final int currentRoundIndex = getCurrentRoundIndex();
		if (currentRoundIndex >= 0)
		{
			for (BattleRecord battleRecord : this.getBattlesForIterate(currentRoundIndex))
			{
				if (battleRecord.getWinnerId() < 0)
				{
					return true;
				}
			}
		}
		return false;
	}

	public Map<Integer, List<BattleRecord>> getBattlesPerRound()
	{
		return _battlesPerRound;
	}

	public boolean isTournamentOver()
	{
		return !isScheduleActive() && !_battlesPerRound.isEmpty();
	}

	public int getCurrentRoundIndex()
	{
		int biggestRoundIndex = -1;
		for (Integer roundIndex : _battlesPerRound.keySet())
		{
			if (roundIndex > biggestRoundIndex)
			{
				biggestRoundIndex = roundIndex;
			}
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(this, "getCurrentRoundIndex", biggestRoundIndex);
		}
		return biggestRoundIndex;
	}

	public boolean isFinalRound(int roundIndex)
	{
		return _battlesPerRound.containsKey(roundIndex) && !_battlesPerRound.get(roundIndex).isEmpty() && countBattlesWithNoLosers(roundIndex) == 1;
	}

	@Nullable
	public BattleRecord getNextBattle()
	{
		final int currentRoundIndex = getCurrentRoundIndex();
		if (currentRoundIndex < 0)
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(this, "getMyBattle", currentRoundIndex, 0);
			}
			return null;
		}
		final long currentDate = System.currentTimeMillis();
		BattleRecord closestBattle = null;
		for (BattleRecord record : _battlesPerRound.get(currentRoundIndex))
		{
			if (record.isNowLive() || record.getBattleDate() > currentDate && (closestBattle == null || record.getBattleDate() < closestBattle.getBattleDate()))
			{
				closestBattle = record;
			}
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(this, "getMyBattle", closestBattle, currentDate, currentRoundIndex);
		}
		return closestBattle;
	}

	@Nullable
	public BattleRecord getMyBattle(Team team)
	{
		for (BattleRecord record : _battlesPerRound.get(getCurrentRoundIndex()))
		{
			if (record.isTeamFighting(team))
			{
				return record;
			}
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(this, "getMyBattle", "NULL", team);
		}
		return null;
	}

	@Nullable
	public BattleRecord getBattle(int roundIndex, int battleInRoundIndex)
	{
		if (battleInRoundIndex < 0 || !_battlesPerRound.containsKey(roundIndex))
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(this, "getBattle", "NULL", roundIndex, battleInRoundIndex);
			}
			return null;
		}
		final List<BattleRecord> records = _battlesPerRound.get(roundIndex);
		if (records.size() <= battleInRoundIndex)
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(this, "getBattle", "NULL", roundIndex, battleInRoundIndex, records.size());
			}
			return null;
		}
		return records.get(battleInRoundIndex);
	}

	@Nullable
	public BattleRecord getBattle(int battleId)
	{
		for (List<BattleRecord> battleList : _battlesPerRound.values())
		{
			for (BattleRecord battle : battleList)
			{
				if (battle.getId() == battleId)
				{
					return battle;
				}
			}
		}
		return null;
	}

	public BattleRecord getBattle(Player player)
	{
		if (!isScheduleActive())
		{
			return null;
		}
		return this.getBattle(player, getCurrentRoundIndex());
	}

	@Nullable
	public BattleRecord getBattle(Player player, int roundIndex)
	{
		for (BattleRecord record : _battlesPerRound.get(roundIndex))
		{
			if (record.getTeam(player) != null)
			{
				return record;
			}
		}
		return null;
	}

	public boolean isFightingBattle(Player player)
	{
		final BattleRecord battle = this.getBattle(player);
		return battle != null && battle.isNowLive();
	}

	public List<BattleRecord> getBattlesForIterate()
	{
		final int currentRoundIndex = getCurrentRoundIndex();
		if (currentRoundIndex < 0)
		{
			return Collections.emptyList();
		}
		return this.getBattlesForIterate(currentRoundIndex);
	}

	public List<BattleRecord> getBattlesForIterate(int roundIndex)
	{
		return _battlesPerRound.get(roundIndex);
	}

	private int countBattlesWithNoLosers(int roundIndex)
	{
		int count = 0;
		for (BattleRecord record : _battlesPerRound.get(roundIndex))
		{
			if (record.teamsWonAllBattles())
			{
				++count;
			}
		}
		return count;
	}

	public List<BattleRecord> getBattlesSortedByDate(int roundIndex)
	{
		final List<BattleRecord> dates = new ArrayList<BattleRecord>(_battlesPerRound.get(roundIndex));
		Collections.sort(dates, BattleDateComparator.INSTANCE);
		return dates;
	}

	public int getIndexInRound(BattleRecord battle)
	{
		return _battlesPerRound.get(battle.getRound()).indexOf(battle);
	}

	public int getLastBattleIndex(int roundIndex)
	{
		if (!_battlesPerRound.containsKey(roundIndex))
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(this, "getBattle", "getLastBattleIndex", _battlesPerRound.keySet(), roundIndex);
			}
			return -1;
		}
		return _battlesPerRound.get(roundIndex).size() - 1;
	}

	public boolean scheduleFirstRound()
	{
		final List<Team> teams = TournamentTeamsManager.getInstance().getTeamsForIterate();
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(this, "scheduleFirstRound", teams);
		}
		if (teams.isEmpty())
		{
			return false;
		}
		initializeNextRound(teams, 0);
		return true;
	}

	public void checkRoundOver()
	{
		for (BattleRecord record : _battlesPerRound.get(getCurrentRoundIndex()))
		{
			if (record.getWinnerId() < 0)
			{
				if (Debug.TOURNAMENT.isActive())
				{
					Debug.TOURNAMENT.debug(this, "checkRoundOver", record);
				}
				return;
			}
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(this, "checkRoundOver", new Object[0]);
		}
		handleRoundOver();
	}

	private void handleRoundOver()
	{
		final int currentRoundIndex = getCurrentRoundIndex();
		if (isFinalRound(currentRoundIndex))
		{
			final List<BattleRecord> battles = _battlesPerRound.get(currentRoundIndex);
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(this, "handleRoundOver", "FinalRound", currentRoundIndex, battles);
			}
			initializeLostBattlesRecords(battles, currentRoundIndex);
			for (BattleRecord record : battles)
			{
				setFinalPosition(record.getTeams(), currentRoundIndex);
			}
			BattleNotificationManager.announceTournamentResults();

			// Synerge - Give the rewards to the winning teams of the tournament
			giveWinnersPrizes();

			// Synerge - Set and check periods for next tournament start, after this tournament ends
			setCheckNewPeriodDates();
		}
		else
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(this, "handleRoundOver", "NotFinalRound", currentRoundIndex);
			}
			initializeNextRoundByPastRecords(_battlesPerRound.get(currentRoundIndex), currentRoundIndex + 1);
		}
	}

	private void initializeNextRoundByPastRecords(Collection<BattleRecord> pastRound, int newRoundIndex)
	{
		initializeLostBattlesRecords(pastRound, newRoundIndex - 1);
		final List<Team> teamsFoughtInRound = new ArrayList<Team>(pastRound.size() / 2);
		for (BattleRecord record : pastRound)
		{
			for (Team team : record.getTeams())
			{
				if (team != null)
				{
					teamsFoughtInRound.add(team);
				}
			}
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(this, "initializeNextRoundByPastRecords", pastRound, newRoundIndex, teamsFoughtInRound);
		}
		initializeNextRound(teamsFoughtInRound, newRoundIndex);
	}

	private void initializeNextRound(List<Team> teamsFoughtInRound, int newRoundIndex)
	{
		final int winnersBattlesCount = (int) teamsFoughtInRound.stream().filter(o -> !o.lostAnyRound()).count() / 2;
		final int lastRoundIndex = calculateLastRoundIndex(winnersBattlesCount, newRoundIndex);
		final long currentDate = System.currentTimeMillis();
		final List<BattleRecord> allNewRoundRecords = new ArrayList<BattleRecord>(teamsFoughtInRound.size() / 2);
		final Map<Integer, List<Team>> teamsByLostInRound = sortTeamsByLostInRound(teamsFoughtInRound, newRoundIndex - 1);
		for (Map.Entry<Integer, List<Team>> teamsLostInRounds : teamsByLostInRound.entrySet())
		{
			if (shouldLosersPlay(teamsLostInRounds.getKey(), lastRoundIndex))
			{
				final List<Team> teams = teamsLostInRounds.getValue();
				final int battles = getBattlesCount(teams.size());
				Collections.shuffle(teams);
				if (Debug.TOURNAMENT.isActive())
				{
					Debug.TOURNAMENT.debug(this, "initializeNextRound", teamsLostInRounds, teams, battles);
				}
				for (int i = 0; i < battles; ++i)
				{
					final Team team1 = teams.get(i);
					final Team team2 = teams.size() > battles + i ? teams.get(battles + i) : null;
					final long battleDate = team2 == null ? currentDate : calculateNextBattleDate(currentDate, newRoundIndex, lastRoundIndex);
					final int id = getNextBattleRecordId();
					allNewRoundRecords.add(new BattleRecord(id, team1.getId(), team2 == null ? -1 : team2.getId(), newRoundIndex, battleDate));
				}
			}
			else
			{
				setFinalPosition(teamsLostInRounds.getValue(), lastRoundIndex);
			}
		}
		_battlesPerRound.put(newRoundIndex, allNewRoundRecords);
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(this, "initializeNextRound", teamsFoughtInRound, newRoundIndex, winnersBattlesCount, lastRoundIndex, allNewRoundRecords, currentDate);
		}
		checkEmptyBattles(newRoundIndex);
		replaceBattleRecordsInDatabase(allNewRoundRecords);
		BattleNotificationManager.onNextRoundStarted(allNewRoundRecords);
	}

	private static void setFinalPosition(Iterable<Team> teams, int roundIndex)
	{
		for (Team team : teams)
		{
			if (team != null)
			{
				setFinalPosition(team, roundIndex);
			}
		}
	}

	private static void setFinalPosition(Team[] teams, int roundIndex)
	{
		for (Team team : teams)
		{
			if (team != null)
			{
				setFinalPosition(team, roundIndex);
			}
		}
	}

	private static void setFinalPosition(@NotNull final Team team, int roundIndex)
	{
		if (team == null)
		{
			throw new IllegalArgumentException(
						String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "team", "l2f/gameserver/model/entity/tournament/BattleScheduleManager", "setFinalPosition"));
		}
		final int finalPosition = decideFinalTeamPosition(team.getRoundsLost(), roundIndex);
		team.setFinalPosition(finalPosition);
		team.update();
	}

	private static int decideFinalTeamPosition(TIntCollection lostRounds, int lastRoundIndex)
	{
		int bestPositionInLostRound = 0;
		final TIntIterator iterator = lostRounds.iterator();
		while (iterator.hasNext())
		{
			final int roundsTillLast = lastRoundIndex - iterator.next();
			bestPositionInLostRound += (int) Math.pow(2.0, roundsTillLast);
		}
		++bestPositionInLostRound;
		final List<Team> teams = TournamentTeamsManager.getInstance().getTeamsForIterate();
		boolean changed;
		do
		{
			changed = false;
			for (Team team : teams)
			{
				if (team.getFinalPosition() == bestPositionInLostRound)
				{
					++bestPositionInLostRound;
					changed = true;
				}
			}
		}
		while (changed);
		return bestPositionInLostRound;
	}

	private void fixMissedBattlesDates()
	{
		final int currentRoundIndex = getCurrentRoundIndex();
		if (currentRoundIndex >= 0)
		{
			final long currentTime = System.currentTimeMillis();
			final List<BattleRecord> battles = _battlesPerRound.get(currentRoundIndex);
			final int winnersBattlesCountInRound = (int) battles.stream().filter(BattleRecord::teamsWonAllBattles).count();
			final int lastRound = calculateLastRoundIndex(winnersBattlesCountInRound, currentRoundIndex);
			for (BattleRecord record : battles)
			{
				if (record.getWinnerId() < 0 && record.getBattleDate() < currentTime && !ArrayUtils.contains(record.getTeams(), (Object) null))
				{
					final long newBattleDate = calculateNextBattleDate(currentTime, currentRoundIndex, lastRound);
					record.setBattleDate(newBattleDate);
					record.updateInDatabase();
					BattleNotificationManager.onChangedBattleDate(record);
					if (!Debug.TOURNAMENT.isActive())
					{
						continue;
					}
					Debug.TOURNAMENT.debug(this, "fixMissedBattlesDates", record, newBattleDate, currentRoundIndex);
				}
			}
		}
	}

	private void checkEmptyBattles(int roundIndex)
	{
		final List<BattleRecord> records = _battlesPerRound.get(roundIndex);
		for (BattleRecord record : records)
		{
			if (record.getTeam2() == null)
			{
				record.setBattleWinner(record.getTeam1Id(), -1);
				if (!Debug.TOURNAMENT.isActive())
				{
					continue;
				}
				Debug.TOURNAMENT.debug(this, "checkEmptyBattles", record);
			}
		}
		checkRoundOver();
	}

	private static Map<Integer, List<Team>> sortTeamsByLostInRound(Collection<Team> teamsFoughtInRound, int biggestRoundIndex)
	{
		final Map<Integer, List<Team>> map = new LinkedHashMap<Integer, List<Team>>(biggestRoundIndex + 2);
		for (int i = 0; i <= biggestRoundIndex; ++i)
		{
			map.put(i, new ArrayList<Team>(0));
		}
		map.put(Integer.MAX_VALUE, new ArrayList<Team>(teamsFoughtInRound.size() / 2));
		for (Team team : teamsFoughtInRound)
		{
			map.get(team.getOldestLostRoundIndex()).add(team);
		}
		for (int i = 0; i <= biggestRoundIndex; ++i)
		{
			if (map.get(i).isEmpty())
			{
				map.remove(i);
			}
		}
		return map;
	}

	private static boolean shouldLosersPlay(int lostInRoundIndex, int lastRoundIndex)
	{
		if (lostInRoundIndex == Integer.MAX_VALUE)
		{
			return true;
		}
		final int roundMinusLast = lastRoundIndex - lostInRoundIndex;
		return ArrayUtils.contains(ConfigHolder.getIntArray("TournamentDetermineLosersOfRounds"), roundMinusLast);
	}

	private long calculateNextBattleDate(long currentDate, int round, int lastRoundIndex)
	{
		final long minDelayFromLastBattle = _lastBattleDate + ActiveBattleManager.getMaxBattleDuration(TimeUnit.MILLISECONDS) + ConfigHolder.getMillis("TournamentDelayBetweenBattles", TimeUnit.SECONDS);
		long nextBattleDate = Math.max(minDelayFromLastBattle, currentDate + ConfigHolder.getMillis("TournamentMinScheduleForBattle", TimeUnit.SECONDS));
		if (!isInDateHourWindow(nextBattleDate, round, lastRoundIndex))
		{
			nextBattleDate = getNextBattleDate(nextBattleDate, round, lastRoundIndex);
		}
		return _lastBattleDate = nextBattleDate;
	}

	public void setLastBattleDate(long date)
	{
		_lastBattleDate = date;
	}

	private long getNextBattleDate(long minDate, int round, int lastRoundIndex)
	{
		final Calendar c = Calendar.getInstance();
		c.setTimeInMillis(minDate);
		final int[] daysWindow = getWindowOfRound(round, lastRoundIndex, "TournamentBattleDaysPerRound", _javaScriptEngine);
		final int[] hoursWindow = getWindowOfRound(round, lastRoundIndex, "TournamentBattleHoursPerRound", _javaScriptEngine);
		while (!ArrayUtils.contains(daysWindow, c.get(7)))
		{
			c.add(7, 1);
			c.set(11, hoursWindow[0]);
			c.set(12, 0);
		}
		if (c.get(11) < hoursWindow[0])
		{
			c.set(11, hoursWindow[0]);
			c.set(12, 0);
		}
		else if (c.get(11) >= hoursWindow[1])
		{
			do
			{
				c.add(7, 1);
			}
			while (!ArrayUtils.contains(daysWindow, c.get(7)));
			c.set(11, hoursWindow[0]);
			c.set(12, 0);
		}
		c.set(13, 0);
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(this, "getNextBattleDate", minDate, round, lastRoundIndex, Arrays.toString(daysWindow), Arrays.toString(hoursWindow), c.getTimeInMillis());
		}
		return c.getTimeInMillis();
	}

	private boolean isInDateHourWindow(long date, int round, int lastRoundIndex)
	{
		final Calendar c = Calendar.getInstance();
		final boolean dateWindow = isInDateWindow(date, round, lastRoundIndex, c, _javaScriptEngine);
		final boolean hourWindow = isInHourWindow(date, round, lastRoundIndex, c, _javaScriptEngine);
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(this, "isInDateHourWindow", date, round, lastRoundIndex, dateWindow, hourWindow);
		}
		return dateWindow && hourWindow;
	}

	private int getNextBattleRecordId()
	{
		return ++_lastBattleObjectId;
	}

	public void setLastBattleObjectId(int id)
	{
		_lastBattleObjectId = id;
	}

	private void initializeLostBattlesRecords()
	{
		for (Map.Entry<Integer, List<BattleRecord>> battlesPerRoundEntry : _battlesPerRound.entrySet())
		{
			initializeLostBattlesRecords(battlesPerRoundEntry.getValue(), battlesPerRoundEntry.getKey());
		}
	}

	private static void initializeLostBattlesRecords(Iterable<BattleRecord> records, int roundIndex)
	{
		for (BattleRecord record : records)
		{
			if (record.getWinnerId() >= 0 && record.getTeam2Id() >= 0)
			{
				final Team loser = record.getTeam1Id() == record.getWinnerId() ? record.getTeam2() : record.getTeam1();
				loser.addLostRound(roundIndex);
			}
		}
	}

	private static void replaceBattleRecordsInDatabase(Iterable<BattleRecord> records)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("REPLACE INTO tournament_battles VALUES (?,?,?,?,?,?,?)"))
		{
			for (BattleRecord record : records)
			{
				statement.setInt(1, record.getId());
				statement.setInt(2, record.getTeam1Id());
				statement.setInt(3, record.getTeam2Id());
				statement.setInt(4, record.getRound());
				statement.setLong(5, record.getBattleDate());
				statement.setInt(6, record.getWinnerId());
				statement.setInt(7, record.getWinnerWonGames());
				statement.executeUpdate();
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while replacing " + records + " in Database!", e);
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(BattleScheduleManager.class, "replaceBattleRecordsInDatabase", records);
		}
	}

	protected static void replaceBattleRecordInDatabase(BattleRecord record)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("REPLACE INTO tournament_battles VALUES (?,?,?,?,?,?,?)"))
		{
			statement.setInt(1, record.getId());
			statement.setInt(2, record.getTeam1Id());
			statement.setInt(3, record.getTeam2Id());
			statement.setInt(4, record.getRound());
			statement.setLong(5, record.getBattleDate());
			statement.setInt(6, record.getWinnerId());
			statement.setInt(7, record.getWinnerWonGames());
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			LOG.error("Error while replacing " + record + " in Database!", e);
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(BattleScheduleManager.class, "replaceBattleRecordInDatabase", record);
		}
	}

	private void loadScheduleFromDatabase()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT * FROM tournament_battles"); ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				final int id = rset.getInt("id");
				final int team1Id = rset.getInt("team_1_id");
				final int team2Id = rset.getInt("team_2_id");
				final int round = rset.getInt("round");
				final long battleDate = rset.getLong("battle_date");
				final int winnerId = rset.getInt("winner_id");
				final int winnerWonGames = rset.getInt("winner_won_games");
				final BattleRecord record = new BattleRecord(id, team1Id, team2Id, round, battleDate, winnerId, winnerWonGames);
				if (_battlesPerRound.containsKey(round))
				{
					_battlesPerRound.get(round).add(record);
				}
				else
				{
					final List<BattleRecord> records = new ArrayList<BattleRecord>();
					records.add(record);
					_battlesPerRound.put(round, records);
				}
				if (id > _lastBattleObjectId)
				{
					_lastBattleObjectId = id;
				}
				if (battleDate > _lastBattleDate)
				{
					_lastBattleDate = battleDate;
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while loading Battle Schedule from Database!", e);
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(this, "loadScheduleFromDatabase", _battlesPerRound, _lastBattleObjectId, _lastBattleDate);
		}
	}

	private static int getBattlesCount(int teamsCount)
	{
		int minBattlesCount;
		int battlesCount;
		for (minBattlesCount = (int) Math.ceil(teamsCount / 2.0), battlesCount = 1; minBattlesCount > battlesCount; battlesCount *= 2)
		{
		}
		return battlesCount;
	}

	private static int calculateLastRoundIndex(int winnersBattlesCountInRound, int roundIndex)
	{
		int pow = 1;
		int lastRoundIndex = roundIndex;
		while (winnersBattlesCountInRound > pow)
		{
			++lastRoundIndex;
			pow *= 2;
		}
		return lastRoundIndex;
	}

	private static boolean isInDateWindow(long date, int round, int lastRoundIndex, Calendar anyCalendar, ScriptEngine javaScriptEngine)
	{
		anyCalendar.setTimeInMillis(date);
		final int day = anyCalendar.get(7);
		final int[] windowOfRound = getWindowOfRound(round, lastRoundIndex, "TournamentBattleDaysPerRound", javaScriptEngine);
		return ArrayUtils.contains(windowOfRound, day);
	}

	private static boolean isInHourWindow(long date, int round, int lastRoundIndex, Calendar anyCalendar, ScriptEngine javaScriptEngine)
	{
		anyCalendar.setTimeInMillis(date);
		final int hour = anyCalendar.get(11);
		final int[] windowOfRound = getWindowOfRound(round, lastRoundIndex, "TournamentBattleHoursPerRound", javaScriptEngine);
		return hour >= windowOfRound[0] && hour < windowOfRound[1];
	}

	private static int[] getWindowOfRound(int round, int lastRoundIndex, String configName, ScriptEngine javaScriptEngine)
	{
		final String[][] windows = ConfigHolder.getMultiStringArray(configName);
		if (windows.length == 0)
		{
			throw new AssertionError("Config " + configName + " is EMPTY! It cannot be EMPTY!");
		}
		for (int i = windows.length - 1; i >= 0; --i)
		{
			String iNotProcessedRound = windows[i][0];
			iNotProcessedRound = iNotProcessedRound.replace("LAST_ROUND", String.valueOf(lastRoundIndex));
			try
			{
				final Integer iRound = (Integer) javaScriptEngine.eval(iNotProcessedRound);
				if (iRound <= round || i == 0)
				{
					final int[] result = new int[windows[i].length - 1];
					for (int x = 1; x < windows[i].length; ++x)
					{
						result[x - 1] = Integer.parseInt(windows[i][x]);
					}
					return result;
				}
			}
			catch (ScriptException e)
			{
				LOG.error("Error while processing " + configName + " Window of " + Arrays.toString(windows), e);
			}
		}
		throw new AssertionError("Config " + configName + " doesn't have VALUE for First Round Index!");
	}

	/**
	 * Synerge - Gives the rewards for each of the winners, generally the first 3 teams
	 */
	public static void giveWinnersPrizes()
	{
		final String[] rewards = ConfigHolder.getStringArray("TournamentWinnersReward");

		for (String reward : rewards)
		{
			final String[] parts = reward.split(",");
			final int place = Integer.parseInt(parts[0]);
			final Team placeWinner = TournamentTeamsManager.getInstance().getTeamByFinalPosition(place);
			if (placeWinner != null)
			{
				final int[] playerIds = placeWinner.getPlayerIdsForIterate();

				final String message = "Congratulations." + "\nYour team has finished in the " + place + " place in the last tournament." + "\nHere is the reward given for finishing the tournament in the "
							+ place + " place." + "\n\nThanks for participating";

				for (int playerId : playerIds)
				{
					Mail mail = new Mail();
					mail.setSenderId(0);
					mail.setSenderName("Tournament Engine");
					mail.setReceiverId(playerId);
					mail.setReceiverName("Tournament Winner");
					mail.setTopic("Tournament reward for " + place + " place");
					mail.setBody(message);
					mail.setPrice(0);
					mail.setUnread(true);
					mail.setType(Mail.SenderType.NONE);
					mail.setExpireTime(0);

					for (int i = 1; i < parts.length; i += 2)
					{
						final ItemInstance newItem = new ItemInstance(IdFactory.getInstance().getNextId(), Integer.parseInt(parts[i]));
						newItem.setCount(Integer.parseInt(parts[i + 1]));
						newItem.setOwnerId(playerId);
						newItem.setLocation(ItemLocation.MAIL);
						mail.addAttachment(newItem);
					}
					mail.save();

					final Player target = World.getPlayer(playerId);
					if (target != null)
					{
						target.sendPacket(ExNoticePostArrived.STATIC_TRUE);
						target.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
					}
				}
			}
		}
	}

	/**
	 * Synerge - Sets new period dates for registration and registration end with configs info
	 */
	public synchronized void setCheckNewPeriodDates()
	{
		if (TournamentStatus.getCurrentStatus(this) != TournamentStatus.OVER && TournamentStatus.getCurrentStatus(this) != TournamentStatus.NOT_ACTIVE)
		{
			return;
		}

		// Check if we must reinitialize one period that should be already active but was stopped due to restart
		final long currentTime = System.currentTimeMillis();
		final long start = ServerVariables.getLong("TournamentRegisterTime", -1);
		final long end = ServerVariables.getLong("TournamentBattleTime", -1);
		if (start > 0)
		{
			// Register period
			if (currentTime >= start && currentTime <= end)
			{
				if (_startRegisterPeriodThread != null)
				{
					_startRegisterPeriodThread.cancel(false);
				}
				ThreadPoolManager.getInstance().execute(new StartRegisterPeriod());

				if (_startBattlePeriodThread != null)
				{
					_startBattlePeriodThread.cancel(false);
				}
				_startBattlePeriodThread = ThreadPoolManager.getInstance().schedule(new StartBattlePeriod(), end - System.currentTimeMillis());
				return;
			}
			// Next register period
			else if (currentTime < start)
			{
				if (_startRegisterPeriodThread != null)
				{
					_startRegisterPeriodThread.cancel(false);
				}
				_startRegisterPeriodThread = ThreadPoolManager.getInstance().schedule(new StartRegisterPeriod(), start - System.currentTimeMillis());

				if (_startBattlePeriodThread != null)
				{
					_startBattlePeriodThread.cancel(false);
				}
				_startBattlePeriodThread = ThreadPoolManager.getInstance().schedule(new StartBattlePeriod(), end - System.currentTimeMillis());
				return;
			}
			// Out of period, set new dates
		}

		// If they were not set, then set new dates
		Calendar registerTime = Calendar.getInstance();
		int addMonth = 0;
		while (true)
		{
			for (int week = 1; week <= 4; week++)
			{
				registerTime = Calendar.getInstance();
				registerTime.add(Calendar.MONTH, addMonth);
				registerTime.set(Calendar.WEEK_OF_MONTH, week);
				registerTime.set(Calendar.DAY_OF_WEEK, ConfigHolder.getInt("TournamentRegistrationDay"));
				registerTime.set(Calendar.HOUR_OF_DAY, 0);
				registerTime.set(Calendar.MINUTE, 0);
				if (registerTime.getTimeInMillis() > currentTime)
				{
					addMonth = -1;
					break;
				}
			}

			if (addMonth == -1)
			{
				break;
			}

			addMonth++;
		}

		// Thread for the registration period
		ServerVariables.set("TournamentRegisterTime", registerTime.getTimeInMillis());
		if (_startRegisterPeriodThread != null)
		{
			_startRegisterPeriodThread.cancel(false);
		}
		_startRegisterPeriodThread = ThreadPoolManager.getInstance().schedule(new StartRegisterPeriod(), registerTime.getTimeInMillis() - System.currentTimeMillis());

		// Thread for the end of the registration period +1 day
		registerTime.add(Calendar.DAY_OF_MONTH, 1);
		ServerVariables.set("TournamentBattleTime", registerTime.getTimeInMillis());
		if (_startBattlePeriodThread != null)
		{
			_startBattlePeriodThread.cancel(false);
		}
		_startBattlePeriodThread = ThreadPoolManager.getInstance().schedule(new StartBattlePeriod(), registerTime.getTimeInMillis() - System.currentTimeMillis());
	}

	public static BattleScheduleManager getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final BattleScheduleManager instance = new BattleScheduleManager();
	}

	private static class BattleDateComparator implements Comparator<BattleRecord>
	{
		private static final Comparator<BattleRecord> INSTANCE = new BattleDateComparator();

		@Override
		public int compare(BattleRecord o1, BattleRecord o2)
		{
			final int walkover = Boolean.compare(o1.getTeam2() == null, o2.getTeam2() == null);
			if (walkover != 0)
			{
				return walkover;
			}
			final int value = Long.compare(o1.getBattleDate(), o2.getBattleDate());
			if (value != 0)
			{
				return value;
			}
			return Integer.compare(o1.getId(), o2.getId());
		}
	}

	private static class StartBattlePeriod extends RunnableImpl
	{
		private static final Object LOCK = new Object();

		@Override
		public void runImpl()
		{
			synchronized (StartBattlePeriod.LOCK)
			{
				if (TournamentStatus.getCurrentStatus() != TournamentStatus.REGISTRATION)
				{
					return;
				}
				BattleScheduleManager.getInstance().scheduleFirstRound();
			}
		}
	}

	private class StartRegisterPeriod extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if (TournamentStatus.getCurrentStatus() != TournamentStatus.OVER && TournamentStatus.getCurrentStatus() != TournamentStatus.NOT_ACTIVE)
			{
				return;
			}

			// Clear rounds
			getBattlesPerRound().clear();

			// Clear db
			try (Connection con = DatabaseFactory.getInstance().getConnection();
						PreparedStatement battlesStatement = con.prepareStatement("DELETE FROM tournament_battles");
						PreparedStatement teamsStatement = con.prepareStatement("DELETE FROM tournament_teams"))
			{
				battlesStatement.executeUpdate();
				teamsStatement.executeUpdate();
			}
			catch (Exception e)
			{
			}

			// Calculate new battle ids
			int currentRoundIndex = getCurrentRoundIndex();
			int newLastBattleId = -1;
			for (int roundIndex = 0; roundIndex <= currentRoundIndex; ++roundIndex)
			{
				for (BattleRecord record : getBattlesForIterate(roundIndex))
				{
					if (record.getId() > newLastBattleId)
					{
						newLastBattleId = record.getId();
					}
				}
			}
			setLastBattleObjectId(newLastBattleId);

			// Calculate new battle dates
			currentRoundIndex = getCurrentRoundIndex();
			long newLastBattleDate = -1L;
			for (int roundIndex = 0; roundIndex <= currentRoundIndex; ++roundIndex)
			{
				for (BattleRecord record : getBattlesForIterate(roundIndex))
				{
					if (record.getBattleDate() > newLastBattleDate)
					{
						newLastBattleDate = record.getBattleDate();
					}
				}
			}
			setLastBattleDate(newLastBattleDate);
		}
	}
}
