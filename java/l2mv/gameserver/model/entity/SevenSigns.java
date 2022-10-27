package l2mv.gameserver.model.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dbutils.DbUtils;
import l2mv.commons.listener.Listener;
import l2mv.commons.listener.ListenerList;
import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.Config;
import l2mv.gameserver.GameServer;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.cache.Msg;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.instancemanager.AutoSpawnManager;
import l2mv.gameserver.instancemanager.AutoSpawnManager.AutoSpawnInstance;
import l2mv.gameserver.instancemanager.RaidBossSpawnManager;
import l2mv.gameserver.listener.GameListener;
import l2mv.gameserver.listener.game.OnSSPeriodListener;
import l2mv.gameserver.listener.game.OnStartListener;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2mv.gameserver.network.serverpackets.SSQInfo;
import l2mv.gameserver.network.serverpackets.SystemMessage;
import l2mv.gameserver.templates.StatsSet;
import l2mv.gameserver.utils.Util;

public class SevenSigns
{
	private class OnStartListenerImpl implements OnStartListener
	{
		@Override
		public void onStart()
		{
			getListenerEngine().onPeriodChange();
		}
	}

	protected class SSListenerList extends ListenerList<GameServer>
	{
		public void onPeriodChange()
		{
			int mode = 0;
			if (SevenSigns.getInstance().getCurrentPeriod() == SevenSigns.PERIOD_SEAL_VALIDATION)
			{
				mode = SevenSigns.getInstance().getCabalHighestScore();
			}

			for (Listener<GameServer> listener : getListeners())
			{
				if (listener instanceof OnSSPeriodListener)
				{
					((OnSSPeriodListener) listener).onPeriodChange(mode);
				}
			}
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(SevenSigns.class);
	private static SevenSigns _instance;
	private ScheduledFuture<?> _periodChange;

	// Basic Seven Signs Constants \\
	public static final String SEVEN_SIGNS_HTML_PATH = "seven_signs/";

	public static final int CABAL_NULL = 0;
	public static final int CABAL_DUSK = 1;
	public static final int CABAL_DAWN = 2;

	public static final int SEAL_NULL = 0;
	public static final int SEAL_AVARICE = 1;
	public static final int SEAL_GNOSIS = 2;
	public static final int SEAL_STRIFE = 3;

	public static final int PERIOD_COMP_RECRUITING = 0;
	public static final int PERIOD_COMPETITION = 1;
	public static final int PERIOD_COMP_RESULTS = 2;
	public static final int PERIOD_SEAL_VALIDATION = 3;

	public static final int PERIOD_START_HOUR = 18;
	public static final int PERIOD_START_MINS = 00;
	public static final int PERIOD_START_DAY = Calendar.MONDAY;

	// The quest event and seal validation periods last for approximately one week
	// with a 15 minutes "interval" period sandwiched between them.
	public static final int PERIOD_MINOR_LENGTH = 900000;
	public static final int PERIOD_MAJOR_LENGTH = 603900000;

	public static final int ANCIENT_ADENA_ID = 5575;
	public static final int RECORD_SEVEN_SIGNS_ID = 5707;
	public static final int CERTIFICATE_OF_APPROVAL_ID = 6388;
	public static final int RECORD_SEVEN_SIGNS_COST = 500;
	public static final int ADENA_JOIN_DAWN_COST = 50000;

	// NPC Related Constants
	public static final int ORATOR_NPC_ID = 31094;
	public static final int PREACHER_NPC_ID = 31093;
	public static final int MAMMON_MERCHANT_ID = 31113;
	public static final int MAMMON_BLACKSMITH_ID = 31126;
	public static final int MAMMON_MARKETEER_ID = 31092;
	public static final int SPIRIT_IN_ID = 31111;
	public static final int SPIRIT_OUT_ID = 31112;
	public static final int LILITH_NPC_ID = 25283;
	public static final int ANAKIM_NPC_ID = 25286;
	public static final int CREST_OF_DAWN_ID = 31170;
	public static final int CREST_OF_DUSK_ID = 31171;

	// Seal Stone Related Constants
	public static final int SEAL_STONE_BLUE_ID = 6360;
	public static final int SEAL_STONE_GREEN_ID = 6361;
	public static final int SEAL_STONE_RED_ID = 6362;

	public static final int SEAL_STONE_BLUE_VALUE = 3;
	public static final int SEAL_STONE_GREEN_VALUE = 5;
	public static final int SEAL_STONE_RED_VALUE = 10;

	public static final int BLUE_CONTRIB_POINTS = 3;
	public static final int GREEN_CONTRIB_POINTS = 5;
	public static final int RED_CONTRIB_POINTS = 10;

	// There is a max on official, but not sure what!
	public static final long MAXIMUM_PLAYER_CONTRIB = Config.MAX_PLAYER_CONTRIBUTION;

	private final Calendar _calendar = Calendar.getInstance();

	protected int _activePeriod;
	protected int _currentCycle;
	protected long _dawnStoneScore;
	protected long _duskStoneScore;
	protected long _dawnFestivalScore;
	protected long _duskFestivalScore;
	protected int _compWinner;
	protected int _previousWinner;

	private Map<Integer, StatsSet> _signsPlayerData;

	private Map<Integer, Integer> _signsSealOwners;
	private Map<Integer, Integer> _signsDuskSealTotals;
	private Map<Integer, Integer> _signsDawnSealTotals;

	private SSListenerList _listenerList = new SSListenerList();

	public SevenSigns()
	{
		GameServer.getInstance().addListener(new OnStartListenerImpl());

		_signsPlayerData = new ConcurrentHashMap<Integer, StatsSet>();
		_signsSealOwners = new ConcurrentHashMap<Integer, Integer>();
		_signsDuskSealTotals = new ConcurrentHashMap<Integer, Integer>();
		_signsDawnSealTotals = new ConcurrentHashMap<Integer, Integer>();

		try
		{
			restoreSevenSignsData();
		}
		catch (RuntimeException e)
		{
			_log.error("SevenSigns: Failed to load configuration: ", e);
		}

		_log.info("SevenSigns: Currently in the " + getCurrentPeriodName() + " period!");
		initializeSeals();

		if (isSealValidationPeriod())
		{
			if (getCabalHighestScore() == CABAL_NULL)
			{
				_log.info("SevenSigns: The Competition last week ended with a tie.");
			}
			else
			{
				_log.info("SevenSigns: The " + getCabalName(getCabalHighestScore()) + " were victorious last week.");
			}
		}
		else if (getCabalHighestScore() == CABAL_NULL)
		{
			_log.info("SevenSigns: The Competition this week, if the trend continue, will end with a tie.");
		}
		else
		{
			_log.info("SevenSigns: The " + getCabalName(getCabalHighestScore()) + " are in the lead this week.");
		}

		int numMins = 0;
		int numHours = 0;
		int numDays = 0;

		setCalendarForNextPeriodChange();
		long milliToChange = getMilliToPeriodChange();
		if (milliToChange < 10)
		{
			milliToChange = 10;
		}
		// Schedule a time for the next period change.
		_periodChange = ThreadPoolManager.getInstance().schedule(new SevenSignsPeriodChange(), milliToChange);

		double numSecs = milliToChange / 1000 % 60;
		double countDown = (milliToChange / 1000 - numSecs) / 60;
		numMins = (int) Math.floor(countDown % 60);
		countDown = (countDown - numMins) / 60;
		numHours = (int) Math.floor(countDown % 24);
		numDays = (int) Math.floor((countDown - numHours) / 24);

		_log.info("SevenSigns: Next period begins in " + numDays + " days, " + numHours + " hours and " + numMins + " mins.");

		if (Config.SS_ANNOUNCE_PERIOD > 0)
		{
			ThreadPoolManager.getInstance().schedule(new SevenSignsAnnounce(), Config.SS_ANNOUNCE_PERIOD * 1000L * 60);
		}
	}

	public void spawnSevenSignsNPC()
	{
		AutoSpawnInstance _merchantSpawn = AutoSpawnManager.getInstance().getAutoSpawnInstance(MAMMON_MERCHANT_ID, false);
		AutoSpawnInstance _blacksmithSpawn = AutoSpawnManager.getInstance().getAutoSpawnInstance(MAMMON_BLACKSMITH_ID, false);
		Map<Integer, AutoSpawnInstance> _marketeerSpawns = AutoSpawnManager.getInstance().getAllAutoSpawnInstance(MAMMON_MARKETEER_ID);
		AutoSpawnInstance _spiritInSpawn = AutoSpawnManager.getInstance().getAutoSpawnInstance(SPIRIT_IN_ID, false);
		AutoSpawnInstance _spiritOutSpawn = AutoSpawnManager.getInstance().getAutoSpawnInstance(SPIRIT_OUT_ID, false);
		AutoSpawnInstance _lilithSpawn = AutoSpawnManager.getInstance().getAutoSpawnInstance(LILITH_NPC_ID, false);
		AutoSpawnInstance _anakimSpawn = AutoSpawnManager.getInstance().getAutoSpawnInstance(ANAKIM_NPC_ID, false);
		AutoSpawnInstance _crestOfDawnSpawn = AutoSpawnManager.getInstance().getAutoSpawnInstance(CREST_OF_DAWN_ID, false);
		AutoSpawnInstance _crestOfDuskSpawn = AutoSpawnManager.getInstance().getAutoSpawnInstance(CREST_OF_DUSK_ID, false);
		Map<Integer, AutoSpawnInstance> _oratorSpawns = AutoSpawnManager.getInstance().getAllAutoSpawnInstance(ORATOR_NPC_ID);
		Map<Integer, AutoSpawnInstance> _preacherSpawns = AutoSpawnManager.getInstance().getAllAutoSpawnInstance(PREACHER_NPC_ID);

		if (isSealValidationPeriod() || isCompResultsPeriod())
		{
			for (AutoSpawnInstance spawnInst : _marketeerSpawns.values())
			{
				AutoSpawnManager.getInstance().setSpawnActive(spawnInst, true);
			}

			if (getSealOwner(SEAL_GNOSIS) == getCabalHighestScore() && getSealOwner(SEAL_GNOSIS) != CABAL_NULL)
			{
				if (!Config.ANNOUNCE_MAMMON_SPAWN)
				{
					_blacksmithSpawn.setBroadcast(false);
				}

				if (!AutoSpawnManager.getInstance().getAutoSpawnInstance(_blacksmithSpawn.getObjectId(), true).isSpawnActive())
				{
					AutoSpawnManager.getInstance().setSpawnActive(_blacksmithSpawn, true);
				}
				for (AutoSpawnInstance spawnInst : _oratorSpawns.values())
				{
					if (!AutoSpawnManager.getInstance().getAutoSpawnInstance(spawnInst.getObjectId(), true).isSpawnActive())
					{
						AutoSpawnManager.getInstance().setSpawnActive(spawnInst, true);
					}
				}
				for (AutoSpawnInstance spawnInst : _preacherSpawns.values())
				{
					if (!AutoSpawnManager.getInstance().getAutoSpawnInstance(spawnInst.getObjectId(), true).isSpawnActive())
					{
						AutoSpawnManager.getInstance().setSpawnActive(spawnInst, true);
					}
				}
			}
			else
			{
				AutoSpawnManager.getInstance().setSpawnActive(_blacksmithSpawn, false);
				for (AutoSpawnInstance spawnInst : _oratorSpawns.values())
				{
					AutoSpawnManager.getInstance().setSpawnActive(spawnInst, false);
				}
				for (AutoSpawnInstance spawnInst : _preacherSpawns.values())
				{
					AutoSpawnManager.getInstance().setSpawnActive(spawnInst, false);
				}
			}

			if (getSealOwner(SEAL_AVARICE) == getCabalHighestScore() && getSealOwner(SEAL_AVARICE) != CABAL_NULL)
			{
				if (!Config.ANNOUNCE_MAMMON_SPAWN)
				{
					_merchantSpawn.setBroadcast(false);
				}

				if (!AutoSpawnManager.getInstance().getAutoSpawnInstance(_merchantSpawn.getObjectId(), true).isSpawnActive())
				{
					AutoSpawnManager.getInstance().setSpawnActive(_merchantSpawn, true);
				}

				if (!AutoSpawnManager.getInstance().getAutoSpawnInstance(_spiritInSpawn.getObjectId(), true).isSpawnActive())
				{
					AutoSpawnManager.getInstance().setSpawnActive(_spiritInSpawn, true);
				}
				if (!AutoSpawnManager.getInstance().getAutoSpawnInstance(_spiritOutSpawn.getObjectId(), true).isSpawnActive())
				{
					AutoSpawnManager.getInstance().setSpawnActive(_spiritOutSpawn, true);
				}

				switch (getCabalHighestScore())
				{
				case CABAL_DAWN:
					if (!AutoSpawnManager.getInstance().getAutoSpawnInstance(_lilithSpawn.getObjectId(), true).isSpawnActive())
					{
						AutoSpawnManager.getInstance().setSpawnActive(_lilithSpawn, true);
					}
					AutoSpawnManager.getInstance().setSpawnActive(_anakimSpawn, false);
					if (!AutoSpawnManager.getInstance().getAutoSpawnInstance(_crestOfDawnSpawn.getObjectId(), true).isSpawnActive())
					{
						AutoSpawnManager.getInstance().setSpawnActive(_crestOfDawnSpawn, true);
					}
					AutoSpawnManager.getInstance().setSpawnActive(_crestOfDuskSpawn, false);
					break;
				case CABAL_DUSK:
					if (!AutoSpawnManager.getInstance().getAutoSpawnInstance(_anakimSpawn.getObjectId(), true).isSpawnActive())
					{
						AutoSpawnManager.getInstance().setSpawnActive(_anakimSpawn, true);
					}
					AutoSpawnManager.getInstance().setSpawnActive(_lilithSpawn, false);
					if (!AutoSpawnManager.getInstance().getAutoSpawnInstance(_crestOfDuskSpawn.getObjectId(), true).isSpawnActive())
					{
						AutoSpawnManager.getInstance().setSpawnActive(_crestOfDuskSpawn, true);
					}
					AutoSpawnManager.getInstance().setSpawnActive(_crestOfDawnSpawn, false);
					break;
				}
			}
			else
			{
				AutoSpawnManager.getInstance().setSpawnActive(_merchantSpawn, false);
				AutoSpawnManager.getInstance().setSpawnActive(_lilithSpawn, false);
				AutoSpawnManager.getInstance().setSpawnActive(_anakimSpawn, false);
				AutoSpawnManager.getInstance().setSpawnActive(_crestOfDawnSpawn, false);
				AutoSpawnManager.getInstance().setSpawnActive(_crestOfDuskSpawn, false);
				AutoSpawnManager.getInstance().setSpawnActive(_spiritInSpawn, false);
				AutoSpawnManager.getInstance().setSpawnActive(_spiritOutSpawn, false);
			}
		}
		else
		{
			AutoSpawnManager.getInstance().setSpawnActive(_merchantSpawn, false);
			AutoSpawnManager.getInstance().setSpawnActive(_blacksmithSpawn, false);
			AutoSpawnManager.getInstance().setSpawnActive(_lilithSpawn, false);
			AutoSpawnManager.getInstance().setSpawnActive(_anakimSpawn, false);
			AutoSpawnManager.getInstance().setSpawnActive(_crestOfDawnSpawn, false);
			AutoSpawnManager.getInstance().setSpawnActive(_crestOfDuskSpawn, false);
			AutoSpawnManager.getInstance().setSpawnActive(_spiritInSpawn, false);
			AutoSpawnManager.getInstance().setSpawnActive(_spiritOutSpawn, false);
			for (AutoSpawnInstance spawnInst : _oratorSpawns.values())
			{
				AutoSpawnManager.getInstance().setSpawnActive(spawnInst, false);
			}
			for (AutoSpawnInstance spawnInst : _preacherSpawns.values())
			{
				AutoSpawnManager.getInstance().setSpawnActive(spawnInst, false);
			}
			for (AutoSpawnInstance spawnInst : _marketeerSpawns.values())
			{
				AutoSpawnManager.getInstance().setSpawnActive(spawnInst, false);
			}
		}
	}

	public static SevenSigns getInstance()
	{
		if (_instance == null)
		{
			_instance = new SevenSigns();
		}
		return _instance;
	}

	public static long calcContributionScore(long blueCount, long greenCount, long redCount)
	{
		long contrib = blueCount * BLUE_CONTRIB_POINTS;
		contrib += greenCount * GREEN_CONTRIB_POINTS;
		contrib += redCount * RED_CONTRIB_POINTS;

		return contrib;
	}

	public static long calcAncientAdenaReward(long blueCount, long greenCount, long redCount)
	{
		long reward = blueCount * SEAL_STONE_BLUE_VALUE;
		reward += greenCount * SEAL_STONE_GREEN_VALUE;
		reward += redCount * SEAL_STONE_RED_VALUE;

		return reward;
	}

	public static int getCabalNumber(String cabal)
	{
		if (cabal.equalsIgnoreCase("dawn"))
		{
			return CABAL_DAWN;
		}
		else if (cabal.equalsIgnoreCase("dusk"))
		{
			return CABAL_DUSK;
		}
		else
		{
			return CABAL_NULL;
		}
	}

	public static String getCabalShortName(int cabal)
	{
		switch (cabal)
		{
		case CABAL_DAWN:
			return "dawn";
		case CABAL_DUSK:
			return "dusk";
		}
		return "No Cabal";
	}

	public static String getCabalName(int cabal)
	{
		switch (cabal)
		{
		case CABAL_DAWN:
			return "Lords of Dawn";
		case CABAL_DUSK:
			return "Revolutionaries of Dusk";
		}
		return "No Cabal";
	}

	public static String getSealName(int seal, boolean shortName)
	{
		String sealName = !shortName ? "Seal of " : "";

		switch (seal)
		{
		case SEAL_AVARICE:
			sealName += "Avarice";
			break;
		case SEAL_GNOSIS:
			sealName += "Gnosis";
			break;
		case SEAL_STRIFE:
			sealName += "Strife";
			break;
		}
		return sealName;
	}

	/**
	 * Used to capitalize the first letter of every "word" in a string.<br>
	 * (Ported from the idea in Perl and PHP)
	 *
	 * @param String str
	 * @return String containing the modified string.
	 */
	public static String capitalizeWords(String str)
	{
		char[] charArray = str.toCharArray();
		StringBuilder buf = new StringBuilder();

		// Capitalize the first letter in the given string!
		charArray[0] = Character.toUpperCase(charArray[0]);
		for (int i = 0; i < charArray.length; i++)
		{
			if (Character.isWhitespace(charArray[i]) && i != charArray.length - 1)
			{
				charArray[i + 1] = Character.toUpperCase(charArray[i + 1]);
			}

			buf.append(Character.toString(charArray[i]));
		}

		return buf.toString();
	}

	public final int getCurrentCycle()
	{
		return _currentCycle;
	}

	public final int getCurrentPeriod()
	{
		return _activePeriod;
	}

	private int getDaysToPeriodChange()
	{
		int numDays = _calendar.get(Calendar.DAY_OF_WEEK) - PERIOD_START_DAY;

		if (numDays < 0)
		{
			return 0 - numDays;
		}
		return 7 - numDays;
	}

	public final long getMilliToPeriodChange()
	{
		return _calendar.getTimeInMillis() - System.currentTimeMillis();
	}

	protected void setCalendarForNextPeriodChange()
	{
		// Calculate the number of days until the next period
		// A period starts at 18:00 pm (local time), like on official servers.
		switch (getCurrentPeriod())
		{
		case PERIOD_SEAL_VALIDATION:
		case PERIOD_COMPETITION:
			int daysToChange = getDaysToPeriodChange();

			if (daysToChange == 7)
			{
				if ((_calendar.get(Calendar.HOUR_OF_DAY) < PERIOD_START_HOUR) || (_calendar.get(Calendar.HOUR_OF_DAY) == PERIOD_START_HOUR && _calendar.get(Calendar.MINUTE) < PERIOD_START_MINS))
				{
					daysToChange = 0;
				}
			}

			// Otherwise...
			if (daysToChange > 0)
			{
				_calendar.add(Calendar.DATE, daysToChange);
			}
			_calendar.set(Calendar.HOUR_OF_DAY, PERIOD_START_HOUR);
			_calendar.set(Calendar.MINUTE, PERIOD_START_MINS);
			break;
		case PERIOD_COMP_RECRUITING:
		case PERIOD_COMP_RESULTS:
			_calendar.add(Calendar.MILLISECOND, PERIOD_MINOR_LENGTH);
			break;
		}
	}

	public final String getCurrentPeriodName()
	{
		String periodName = null;

		switch (_activePeriod)
		{
		case PERIOD_COMP_RECRUITING:
			periodName = "Quest Event Initialization";
			break;
		case PERIOD_COMPETITION:
			periodName = "Competition (Quest Event)";
			break;
		case PERIOD_COMP_RESULTS:
			periodName = "Quest Event Results";
			break;
		case PERIOD_SEAL_VALIDATION:
			periodName = "Seal Validation";
			break;
		}
		return periodName;
	}

	public final boolean isSealValidationPeriod()
	{
		return _activePeriod == PERIOD_SEAL_VALIDATION;
	}

	public final boolean isCompResultsPeriod()
	{
		return _activePeriod == PERIOD_COMP_RESULTS;
	}

	public final long getCurrentScore(int cabal)
	{
		double totalStoneScore = _dawnStoneScore + _duskStoneScore;

		switch (cabal)
		{
		case CABAL_NULL:
			return 0;
		case CABAL_DAWN:
			return Math.round(_dawnStoneScore / (totalStoneScore == 0 ? 1 : totalStoneScore) * 500) + _dawnFestivalScore;
		case CABAL_DUSK:
			return Math.round(_duskStoneScore / (totalStoneScore == 0 ? 1 : totalStoneScore) * 500) + _duskFestivalScore;
		}
		return 0;
	}

	public final long getCurrentStoneScore(int cabal)
	{
		switch (cabal)
		{
		case CABAL_NULL:
			return 0;
		case CABAL_DAWN:
			return _dawnStoneScore;
		case CABAL_DUSK:
			return _duskStoneScore;
		}
		return 0;
	}

	public final long getCurrentFestivalScore(int cabal)
	{
		switch (cabal)
		{
		case CABAL_NULL:
			return 0;
		case CABAL_DAWN:
			return _dawnFestivalScore;
		case CABAL_DUSK:
			return _duskFestivalScore;
		}
		return 0;
	}

	public final int getCabalHighestScore()
	{
		long diff = getCurrentScore(CABAL_DUSK) - getCurrentScore(CABAL_DAWN);
		if (diff == 0)
		{
			return CABAL_NULL;
		}
		else if (diff > 0)
		{
			return CABAL_DUSK;
		}

		return CABAL_DAWN;
	}

	public final int getSealOwner(int seal)
	{
		if (_signsSealOwners == null || !_signsSealOwners.containsKey(seal))
		{
			return CABAL_NULL;
		}
		return _signsSealOwners.get(seal);
	}

	public final int getSealProportion(int seal, int cabal)
	{
		if (cabal == CABAL_NULL)
		{
			return 0;
		}
		else if (cabal == CABAL_DUSK)
		{
			return _signsDuskSealTotals.get(seal);
		}
		else
		{
			return _signsDawnSealTotals.get(seal);
		}
	}

	public final int getTotalMembers(int cabal)
	{
		int cabalMembers = 0;

		for (StatsSet sevenDat : _signsPlayerData.values())
		{
			if (sevenDat.getInteger("cabal") == cabal)
			{
				cabalMembers++;
			}
		}

		return cabalMembers;
	}

	public final StatsSet getPlayerStatsSet(Player player)
	{
		if (!hasRegisteredBefore(player.getObjectId()))
		{
			return null;
		}

		return _signsPlayerData.get(player.getObjectId());
	}

	public long getPlayerStoneContrib(Player player)
	{
		if (!hasRegisteredBefore(player.getObjectId()))
		{
			return 0;
		}

		long stoneCount = 0;

		StatsSet currPlayer = _signsPlayerData.get(player.getObjectId());

		if (getPlayerCabal(player) == CABAL_DAWN)
		{
			stoneCount += currPlayer.getLong("dawn_red_stones");
			stoneCount += currPlayer.getLong("dawn_green_stones");
			stoneCount += currPlayer.getLong("dawn_blue_stones");
		}
		else
		{
			stoneCount += currPlayer.getLong("dusk_red_stones");
			stoneCount += currPlayer.getLong("dusk_green_stones");
			stoneCount += currPlayer.getLong("dusk_blue_stones");
		}

		return stoneCount;
	}

	public long getPlayerContribScore(Player player)
	{
		if (!hasRegisteredBefore(player.getObjectId()))
		{
			return 0;
		}

		StatsSet currPlayer = _signsPlayerData.get(player.getObjectId());
		if (getPlayerCabal(player) == CABAL_DAWN)
		{
			return currPlayer.getInteger("dawn_contribution_score");
		}
		return currPlayer.getInteger("dusk_contribution_score");
	}

	public long getPlayerAdenaCollect(Player player)
	{
		if (!hasRegisteredBefore(player.getObjectId()))
		{
			return 0;
		}
		return _signsPlayerData.get(player.getObjectId()).getLong(getPlayerCabal(player) == CABAL_DAWN ? "dawn_ancient_adena_amount" : "dusk_ancient_adena_amount");
	}

	public int getPlayerSeal(Player player)
	{
		if (!hasRegisteredBefore(player.getObjectId()))
		{
			return SEAL_NULL;
		}

		return _signsPlayerData.get(player.getObjectId()).getInteger("seal");
	}

	public int getPlayerCabal(Player player)
	{
		if (!hasRegisteredBefore(player.getObjectId()))
		{
			return CABAL_NULL;
		}

		return _signsPlayerData.get(player.getObjectId()).getInteger("cabal");
	}

	/**
	 * Restores all Seven Signs data and settings, usually called at server startup.
	 */
	protected void restoreSevenSignsData()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT char_obj_id, cabal, seal, dawn_red_stones, dawn_green_stones, dawn_blue_stones, dawn_ancient_adena_amount, dawn_contribution_score, dusk_red_stones, dusk_green_stones, dusk_blue_stones, dusk_ancient_adena_amount, dusk_contribution_score FROM seven_signs");
			rset = statement.executeQuery();
			while (rset.next())
			{
				int charObjId = rset.getInt("char_obj_id");

				StatsSet sevenDat = new StatsSet();
				sevenDat.set("char_obj_id", charObjId);
				sevenDat.set("cabal", getCabalNumber(rset.getString("cabal")));
				sevenDat.set("seal", rset.getInt("seal"));
				sevenDat.set("dawn_red_stones", rset.getInt("dawn_red_stones"));
				sevenDat.set("dawn_green_stones", rset.getInt("dawn_green_stones"));
				sevenDat.set("dawn_blue_stones", rset.getInt("dawn_blue_stones"));
				sevenDat.set("dawn_ancient_adena_amount", rset.getInt("dawn_ancient_adena_amount"));
				sevenDat.set("dawn_contribution_score", rset.getInt("dawn_contribution_score"));
				sevenDat.set("dusk_red_stones", rset.getInt("dusk_red_stones"));
				sevenDat.set("dusk_green_stones", rset.getInt("dusk_green_stones"));
				sevenDat.set("dusk_blue_stones", rset.getInt("dusk_blue_stones"));
				sevenDat.set("dusk_ancient_adena_amount", rset.getInt("dusk_ancient_adena_amount"));
				sevenDat.set("dusk_contribution_score", rset.getInt("dusk_contribution_score"));

				_signsPlayerData.put(charObjId, sevenDat);
			}
			DbUtils.close(statement, rset);

			statement = con.prepareStatement("SELECT * FROM seven_signs_status");
			rset = statement.executeQuery();

			while (rset.next())
			{
				_currentCycle = rset.getInt("current_cycle");
				_activePeriod = rset.getInt("active_period");
				_previousWinner = rset.getInt("previous_winner");

				_dawnStoneScore = rset.getLong("dawn_stone_score");
				_dawnFestivalScore = rset.getLong("dawn_festival_score");
				_duskStoneScore = rset.getLong("dusk_stone_score");
				_duskFestivalScore = rset.getLong("dusk_festival_score");

				_signsSealOwners.put(SEAL_AVARICE, rset.getInt("avarice_owner"));
				_signsSealOwners.put(SEAL_GNOSIS, rset.getInt("gnosis_owner"));
				_signsSealOwners.put(SEAL_STRIFE, rset.getInt("strife_owner"));

				_signsDawnSealTotals.put(SEAL_AVARICE, rset.getInt("avarice_dawn_score"));
				_signsDawnSealTotals.put(SEAL_GNOSIS, rset.getInt("gnosis_dawn_score"));
				_signsDawnSealTotals.put(SEAL_STRIFE, rset.getInt("strife_dawn_score"));
				_signsDuskSealTotals.put(SEAL_AVARICE, rset.getInt("avarice_dusk_score"));
				_signsDuskSealTotals.put(SEAL_GNOSIS, rset.getInt("gnosis_dusk_score"));
				_signsDuskSealTotals.put(SEAL_STRIFE, rset.getInt("strife_dusk_score"));
			}
			DbUtils.close(statement, rset);

			statement = con.prepareStatement("UPDATE seven_signs_status SET date=?");
			statement.setInt(1, Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("Unable to load Seven Signs Data: ", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		// Festival data is loaded now after the Seven Signs engine data.
	}

	/**
	 * Сохраняет всю информацию о печатях, как для игроков, так и глобальные переменные.
	 *
	 * @param playerId — если больше нуля то сохранение личных параметров производится только для одного игрока, иначе для всех
	 * @param updateSettings — если true сохраняет глобальные параметры, иначе только личные
	 */
	public synchronized void saveSevenSignsData(int playerId, boolean updateSettings)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE seven_signs SET cabal=?, seal=?, dawn_red_stones=?, dawn_green_stones=?, dawn_blue_stones=?, dawn_ancient_adena_amount=?, dawn_contribution_score=?, dusk_red_stones=?, dusk_green_stones=?, dusk_blue_stones=?, dusk_ancient_adena_amount=?, dusk_contribution_score=? WHERE char_obj_id=?");

			if (playerId > 0)
			{
				processStatement(statement, _signsPlayerData.get(playerId));
			}
			else
			{
				for (StatsSet sevenDat : _signsPlayerData.values())
				{
					processStatement(statement, sevenDat);
				}
			}

			DbUtils.close(statement);

			if (updateSettings)
			{
				StringBuilder buf = new StringBuilder();

				buf.append("UPDATE seven_signs_status SET current_cycle=?, active_period=?, previous_winner=?, " //
							+ "dawn_stone_score=?, dawn_festival_score=?, dusk_stone_score=?, dusk_festival_score=?, " //
							+ "avarice_owner=?, gnosis_owner=?, strife_owner=?, avarice_dawn_score=?, gnosis_dawn_score=?, " //
							+ "strife_dawn_score=?, avarice_dusk_score=?, gnosis_dusk_score=?, strife_dusk_score=?, festival_cycle=?, ");
				for (int i = 0; i < SevenSignsFestival.FESTIVAL_COUNT; i++)
				{
					buf.append("accumulated_bonus" + String.valueOf(i) + "=?, ");
				}
				buf.append("date=?");

				statement = con.prepareStatement(buf.toString());
				statement.setInt(1, _currentCycle);
				statement.setInt(2, _activePeriod);
				statement.setInt(3, _previousWinner);
				statement.setLong(4, _dawnStoneScore);
				statement.setLong(5, _dawnFestivalScore);
				statement.setLong(6, _duskStoneScore);
				statement.setLong(7, _duskFestivalScore);
				statement.setInt(8, _signsSealOwners.get(SEAL_AVARICE));
				statement.setInt(9, _signsSealOwners.get(SEAL_GNOSIS));
				statement.setInt(10, _signsSealOwners.get(SEAL_STRIFE));
				statement.setInt(11, _signsDawnSealTotals.get(SEAL_AVARICE));
				statement.setInt(12, _signsDawnSealTotals.get(SEAL_GNOSIS));
				statement.setInt(13, _signsDawnSealTotals.get(SEAL_STRIFE));
				statement.setInt(14, _signsDuskSealTotals.get(SEAL_AVARICE));
				statement.setInt(15, _signsDuskSealTotals.get(SEAL_GNOSIS));
				statement.setInt(16, _signsDuskSealTotals.get(SEAL_STRIFE));

				statement.setInt(17, getCurrentCycle());
				for (int i = 0; i < SevenSignsFestival.FESTIVAL_COUNT; i++)
				{
					statement.setLong(18 + i, SevenSignsFestival.getInstance().getAccumulatedBonus(i));
				}
				statement.setInt(18 + SevenSignsFestival.FESTIVAL_COUNT, Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
				statement.executeUpdate();
			}
		}
		catch (SQLException e)
		{
			_log.error("Unable to save Seven Signs data", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	private static void processStatement(PreparedStatement statement, StatsSet sevenDat) throws SQLException
	{
		statement.setString(1, getCabalShortName(sevenDat.getInteger("cabal")));
		statement.setInt(2, sevenDat.getInteger("seal"));
		statement.setInt(3, sevenDat.getInteger("dawn_red_stones"));
		statement.setInt(4, sevenDat.getInteger("dawn_green_stones"));
		statement.setInt(5, sevenDat.getInteger("dawn_blue_stones"));
		statement.setInt(6, sevenDat.getInteger("dawn_ancient_adena_amount"));
		statement.setInt(7, sevenDat.getInteger("dawn_contribution_score"));
		statement.setInt(8, sevenDat.getInteger("dusk_red_stones"));
		statement.setInt(9, sevenDat.getInteger("dusk_green_stones"));
		statement.setInt(10, sevenDat.getInteger("dusk_blue_stones"));
		statement.setInt(11, sevenDat.getInteger("dusk_ancient_adena_amount"));
		statement.setInt(12, sevenDat.getInteger("dusk_contribution_score"));
		statement.setInt(13, sevenDat.getInteger("char_obj_id"));
		statement.executeUpdate();
	}

	/**
	 * Used to reset the cabal details of all players, and update the database.<BR>
	 * Primarily used when beginning a new cycle, and should otherwise never be called.
	 */
	protected void resetPlayerData()
	{
		for (StatsSet sevenDat : _signsPlayerData.values())
		{
			int charObjId = sevenDat.getInteger("char_obj_id");
			// Reset seal stones and contribution score for winning cabal
			if (sevenDat.getInteger("cabal") == getCabalHighestScore())
			{
				switch (getCabalHighestScore())
				{
				case CABAL_DAWN:
					sevenDat.set("dawn_red_stones", 0);
					sevenDat.set("dawn_green_stones", 0);
					sevenDat.set("dawn_blue_stones", 0);
					sevenDat.set("dawn_contribution_score", 0);
					break;
				case CABAL_DUSK:
					sevenDat.set("dusk_red_stones", 0);
					sevenDat.set("dusk_green_stones", 0);
					sevenDat.set("dusk_blue_stones", 0);
					sevenDat.set("dusk_contribution_score", 0);
					break;
				}
			}
			else if (sevenDat.getInteger("cabal") == CABAL_DAWN || sevenDat.getInteger("cabal") == CABAL_NULL)
			{
				sevenDat.set("dusk_red_stones", 0);
				sevenDat.set("dusk_green_stones", 0);
				sevenDat.set("dusk_blue_stones", 0);
				sevenDat.set("dusk_contribution_score", 0);
			}
			else if (sevenDat.getInteger("cabal") == CABAL_DUSK || sevenDat.getInteger("cabal") == CABAL_NULL)
			{
				sevenDat.set("dawn_red_stones", 0);
				sevenDat.set("dawn_green_stones", 0);
				sevenDat.set("dawn_blue_stones", 0);
				sevenDat.set("dawn_contribution_score", 0);
			}

			// Reset the player's cabal and seal information
			sevenDat.set("cabal", CABAL_NULL);
			sevenDat.set("seal", SEAL_NULL);
			_signsPlayerData.put(charObjId, sevenDat);
		}
		// A database update should soon follow this!
	}

	/**
	 * Tests whether the specified player has joined a cabal in the past.
	 *
	 * @param player
	 * @return boolean hasRegistered
	 */
	private boolean hasRegisteredBefore(int charObjId)
	{
		return _signsPlayerData.containsKey(charObjId);
	}

	/**
	 * Used to specify cabal-related details for the specified player. This method
	 * checks to see if the player has registered before and will update the database
	 * if necessary.
	 * <BR>
	 * Returns the cabal ID the player has joined.
	 *
	 * @param player
	 * @param chosenCabal
	 * @param chosenSeal
	 * @return int cabal
	 */
	public int setPlayerInfo(int charObjId, int chosenCabal, int chosenSeal)
	{
		StatsSet currPlayer = null;
		if (hasRegisteredBefore(charObjId))
		{
			// If the seal validation period has passed,
			// cabal information was removed and so "re-register" player
			currPlayer = _signsPlayerData.get(charObjId);
			currPlayer.set("cabal", chosenCabal);
			currPlayer.set("seal", chosenSeal);

			_signsPlayerData.put(charObjId, currPlayer);
		}
		else
		{
			currPlayer = new StatsSet();
			currPlayer.set("char_obj_id", charObjId);
			currPlayer.set("cabal", chosenCabal);
			currPlayer.set("seal", chosenSeal);
			currPlayer.set("dawn_red_stones", 0);
			currPlayer.set("dawn_green_stones", 0);
			currPlayer.set("dawn_blue_stones", 0);
			currPlayer.set("dawn_ancient_adena_amount", 0);
			currPlayer.set("dawn_contribution_score", 0);
			currPlayer.set("dusk_red_stones", 0);
			currPlayer.set("dusk_green_stones", 0);
			currPlayer.set("dusk_blue_stones", 0);
			currPlayer.set("dusk_ancient_adena_amount", 0);
			currPlayer.set("dusk_contribution_score", 0);

			_signsPlayerData.put(charObjId, currPlayer);

			Connection con = null;
			PreparedStatement statement = null;
			// Update data in database, as we have a new player signing up.
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("INSERT INTO seven_signs (char_obj_id, cabal, seal) VALUES (?,?,?)");
				statement.setInt(1, charObjId);
				statement.setString(2, getCabalShortName(chosenCabal));
				statement.setInt(3, chosenSeal);
				statement.execute();
			}
			catch (SQLException e)
			{
				_log.error("SevenSigns: Failed to save data: ", e);
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}
		}
		long contribScore = 0;

		switch (chosenCabal)
		{
		case CABAL_DAWN:
			contribScore = calcContributionScore(currPlayer.getInteger("dawn_blue_stones"), currPlayer.getInteger("dawn_green_stones"), currPlayer.getInteger("dawn_red_stones"));
			_dawnStoneScore += contribScore;
			break;
		case CABAL_DUSK:
			contribScore = calcContributionScore(currPlayer.getInteger("dusk_blue_stones"), currPlayer.getInteger("dusk_green_stones"), currPlayer.getInteger("dusk_red_stones"));
			_duskStoneScore += contribScore;
			break;
		}

		// Increasing Seal total score for the player chosen Seal.
		if (currPlayer.getInteger("cabal") == CABAL_DAWN)
		{
			_signsDawnSealTotals.put(chosenSeal, _signsDawnSealTotals.get(chosenSeal) + 1);
		}
		else
		{
			_signsDuskSealTotals.put(chosenSeal, _signsDuskSealTotals.get(chosenSeal) + 1);
		}

		saveSevenSignsData(charObjId, true);

		return chosenCabal;
	}

	/**
	 * Returns the amount of ancient adena the specified player can claim, if any.<BR>
	 * If removeReward = True, all the ancient adena owed to them is removed, then
	 * DB is updated.
	 *
	 * @param player
	 * @param removeReward
	 * @return int rewardAmount
	 */
	public int getAncientAdenaReward(Player player, boolean removeReward)
	{
		int charObjId = player.getObjectId();
		StatsSet currPlayer = _signsPlayerData.get(charObjId);

		int rewardAmount = 0;
		if (currPlayer.getInteger("cabal") == CABAL_DAWN)
		{
			rewardAmount = currPlayer.getInteger("dawn_ancient_adena_amount");
			currPlayer.set("dawn_ancient_adena_amount", 0);
		}
		else
		{
			rewardAmount = currPlayer.getInteger("dusk_ancient_adena_amount");
			currPlayer.set("dusk_ancient_adena_amount", 0);
		}

		if (removeReward)
		{
			_signsPlayerData.put(charObjId, currPlayer);
			saveSevenSignsData(charObjId, false);
		}

		return rewardAmount;
	}

	/**
	 * Used to add the specified player's seal stone contribution points
	 * to the current total for their cabal. Returns the point score the
	 * contribution was worth.
	 *
	 * Each stone count <B>must be</B> broken down and specified by the stone's color.
	 *
	 * @param player
	 * @param blueCount
	 * @param greenCount
	 * @param redCount
	 * @return int contribScore
	 */
	public long addPlayerStoneContrib(Player player, long blueCount, long greenCount, long redCount)
	{
		return addPlayerStoneContrib(player.getObjectId(), blueCount, greenCount, redCount);
	}

	public long addPlayerStoneContrib(int charObjId, long blueCount, long greenCount, long redCount)
	{
		StatsSet currPlayer = _signsPlayerData.get(charObjId);

		long contribScore = calcContributionScore(blueCount, greenCount, redCount);
		long totalAncientAdena = 0;
		long totalContribScore = 0;

		if (currPlayer.getInteger("cabal") == CABAL_DAWN)
		{
			totalAncientAdena = currPlayer.getInteger("dawn_ancient_adena_amount") + calcAncientAdenaReward(blueCount, greenCount, redCount);
			totalContribScore = currPlayer.getInteger("dawn_contribution_score") + contribScore;

			if (totalContribScore > MAXIMUM_PLAYER_CONTRIB)
			{
				return -1;
			}

			currPlayer.set("dawn_red_stones", currPlayer.getInteger("dawn_red_stones") + redCount);
			currPlayer.set("dawn_green_stones", currPlayer.getInteger("dawn_green_stones") + greenCount);
			currPlayer.set("dawn_blue_stones", currPlayer.getInteger("dawn_blue_stones") + blueCount);
			currPlayer.set("dawn_ancient_adena_amount", totalAncientAdena);
			currPlayer.set("dawn_contribution_score", totalContribScore);
			_signsPlayerData.put(charObjId, currPlayer);
			_dawnStoneScore += contribScore;
		}
		else
		{
			totalAncientAdena = currPlayer.getInteger("dusk_ancient_adena_amount") + calcAncientAdenaReward(blueCount, greenCount, redCount);
			totalContribScore = currPlayer.getInteger("dusk_contribution_score") + contribScore;

			if (totalContribScore > MAXIMUM_PLAYER_CONTRIB)
			{
				return -1;
			}

			currPlayer.set("dusk_red_stones", currPlayer.getInteger("dusk_red_stones") + redCount);
			currPlayer.set("dusk_green_stones", currPlayer.getInteger("dusk_green_stones") + greenCount);
			currPlayer.set("dusk_blue_stones", currPlayer.getInteger("dusk_blue_stones") + blueCount);
			currPlayer.set("dusk_ancient_adena_amount", totalAncientAdena);
			currPlayer.set("dusk_contribution_score", totalContribScore);
			_signsPlayerData.put(charObjId, currPlayer);
			_duskStoneScore += contribScore;
		}

		saveSevenSignsData(charObjId, true);

		return contribScore;
	}

	public synchronized void updateFestivalScore()
	{
		_duskFestivalScore = 0;
		_dawnFestivalScore = 0;
		for (int i = 0; i < SevenSignsFestival.FESTIVAL_COUNT; i++)
		{
			long dusk = SevenSignsFestival.getInstance().getHighestScore(CABAL_DUSK, i);
			long dawn = SevenSignsFestival.getInstance().getHighestScore(CABAL_DAWN, i);
			if (dusk > dawn)
			{
				_duskFestivalScore += SevenSignsFestival.FESTIVAL_LEVEL_SCORES[i];
			}
			else if (dusk < dawn)
			{
				_dawnFestivalScore += SevenSignsFestival.FESTIVAL_LEVEL_SCORES[i];
			}
		}
	}

	/**
	 * Send info on the current Seven Signs period to the specified player.
	 *
	 * @param player
	 */
	public void sendCurrentPeriodMsg(Player player)
	{
		switch (_activePeriod)
		{
		case PERIOD_COMP_RECRUITING:
			player.sendPacket(Msg.SEVEN_SIGNS_PREPARATIONS_HAVE_BEGUN_FOR_THE_NEXT_QUEST_EVENT);
			return;
		case PERIOD_COMPETITION:
			player.sendPacket(Msg.SEVEN_SIGNS_THE_QUEST_EVENT_PERIOD_HAS_BEGUN_SPEAK_WITH_A_PRIEST_OF_DAWN_OR_DUSK_PRIESTESS_IF_YOU_WISH_TO_PARTICIPATE_IN_THE_EVENT);
			return;
		case PERIOD_COMP_RESULTS:
			player.sendPacket(Msg.SEVEN_SIGNS_QUEST_EVENT_HAS_ENDED_RESULTS_ARE_BEING_TALLIED);
			return;
		case PERIOD_SEAL_VALIDATION:
			player.sendPacket(Msg.SEVEN_SIGNS_THIS_IS_THE_SEAL_VALIDATION_PERIOD_A_NEW_QUEST_EVENT_PERIOD_BEGINS_NEXT_MONDAY);
			return;
		}
	}

	/**
	 * Sends the built-in system message specified by sysMsgId to all online players.
	 *
	 * @param sysMsgId
	 */
	public void sendMessageToAll(int sysMsgId)
	{
		SystemMessage sm = new SystemMessage(sysMsgId);
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			player.sendPacket(sm);
		}
	}

	/**
	 * Used to initialize the seals for each cabal. (Used at startup or at beginning of a new cycle).
	 * This method should	be called after <B>resetSeals()</B> and <B>calcNewSealOwners()</B> on a new cycle.
	 */
	protected void initializeSeals()
	{
		for (Integer currSeal : _signsSealOwners.keySet())
		{
			int sealOwner = _signsSealOwners.get(currSeal);

			if (sealOwner != CABAL_NULL)
			{
				if (isSealValidationPeriod())
				{
					_log.info("SevenSigns: The " + getCabalName(sealOwner) + " have won the " + getSealName(currSeal, false) + ".");
				}
				else
				{
					_log.info("SevenSigns: The " + getSealName(currSeal, false) + " is currently owned by " + getCabalName(sealOwner) + ".");
				}
			}
			else
			{
				_log.info("SevenSigns: The " + getSealName(currSeal, false) + " remains unclaimed.");
			}
		}
	}

	/**
	 * Only really used at the beginning of a new cycle, this method resets all seal-related data.
	 */
	protected void resetSeals()
	{
		_signsDawnSealTotals.put(SEAL_AVARICE, 0);
		_signsDawnSealTotals.put(SEAL_GNOSIS, 0);
		_signsDawnSealTotals.put(SEAL_STRIFE, 0);
		_signsDuskSealTotals.put(SEAL_AVARICE, 0);
		_signsDuskSealTotals.put(SEAL_GNOSIS, 0);
		_signsDuskSealTotals.put(SEAL_STRIFE, 0);
	}

	/**
	 * Calculates the ownership of the three Seals of the Seven Signs,
	 * based on various criterion.
	 * <BR><BR>
	 * Should only ever called at the beginning of a new cycle.
	 */
	protected void calcNewSealOwners()
	{
		for (Integer currSeal : _signsDawnSealTotals.keySet())
		{
			int prevSealOwner = _signsSealOwners.get(currSeal);
			int newSealOwner = CABAL_NULL;
			int dawnProportion = getSealProportion(currSeal, CABAL_DAWN);
			int totalDawnMembers = getTotalMembers(CABAL_DAWN) == 0 ? 1 : getTotalMembers(CABAL_DAWN);
			int duskProportion = getSealProportion(currSeal, CABAL_DUSK);
			int totalDuskMembers = getTotalMembers(CABAL_DUSK) == 0 ? 1 : getTotalMembers(CABAL_DUSK);

			/*
			 * - If a Seal was already closed or owned by the opponent and the new winner wants
			 * to assume ownership of the Seal, 35% or more of the members of the Cabal must
			 * have chosen the Seal. If they chose less than 35%, they cannot own the Seal.
			 * - If the Seal was owned by the winner in the previous Seven Signs, they can retain
			 * that seal if 10% or more members have chosen it. If they want to possess a new Seal,
			 * at least 35% of the members of the Cabal must have chosen the new Seal.
			 */
			switch (prevSealOwner)
			{
			case CABAL_NULL:
				switch (getCabalHighestScore())
				{
				case CABAL_NULL:
					if (dawnProportion >= Math.round(0.35 * totalDawnMembers) && dawnProportion > duskProportion)
					{
						newSealOwner = CABAL_DAWN;
					}
					else if (duskProportion >= Math.round(0.35 * totalDuskMembers) && duskProportion > dawnProportion)
					{
						newSealOwner = CABAL_DUSK;
					}
					else
					{
						newSealOwner = prevSealOwner;
					}
					break;
				case CABAL_DAWN:
					if (dawnProportion >= Math.round(0.35 * totalDawnMembers))
					{
						newSealOwner = CABAL_DAWN;
					}
					else if (duskProportion >= Math.round(0.35 * totalDuskMembers))
					{
						newSealOwner = CABAL_DUSK;
					}
					else
					{
						newSealOwner = prevSealOwner;
					}
					break;
				case CABAL_DUSK:
					if (duskProportion >= Math.round(0.35 * totalDuskMembers))
					{
						newSealOwner = CABAL_DUSK;
					}
					else if (dawnProportion >= Math.round(0.35 * totalDawnMembers))
					{
						newSealOwner = CABAL_DAWN;
					}
					else
					{
						newSealOwner = prevSealOwner;
					}
					break;
				}
				break;
			case CABAL_DAWN:
				switch (getCabalHighestScore())
				{
				case CABAL_NULL:
					if (dawnProportion >= Math.round(0.10 * totalDawnMembers))
					{
						newSealOwner = prevSealOwner;
					}
					else if (duskProportion >= Math.round(0.35 * totalDuskMembers))
					{
						newSealOwner = CABAL_DUSK;
					}
					else
					{
						newSealOwner = CABAL_NULL;
					}
					break;
				case CABAL_DAWN:
					if (dawnProportion >= Math.round(0.10 * totalDawnMembers))
					{
						newSealOwner = prevSealOwner;
					}
					else if (duskProportion >= Math.round(0.35 * totalDuskMembers))
					{
						newSealOwner = CABAL_DUSK;
					}
					else
					{
						newSealOwner = CABAL_NULL;
					}
					break;
				case CABAL_DUSK:
					if (duskProportion >= Math.round(0.10 * totalDuskMembers))
					{
						newSealOwner = CABAL_DUSK;
					}
					else if (dawnProportion >= Math.round(0.35 * totalDawnMembers))
					{
						newSealOwner = prevSealOwner;
					}
					else
					{
						newSealOwner = CABAL_NULL;
					}
					break;
				}
				break;
			case CABAL_DUSK:
				switch (getCabalHighestScore())
				{
				case CABAL_NULL:
					if (duskProportion >= Math.round(0.10 * totalDuskMembers))
					{
						newSealOwner = prevSealOwner;
					}
					else if (dawnProportion >= Math.round(0.35 * totalDawnMembers))
					{
						newSealOwner = CABAL_DAWN;
					}
					else
					{
						newSealOwner = CABAL_NULL;
					}
					break;
				case CABAL_DAWN:
					if (dawnProportion >= Math.round(0.35 * totalDawnMembers))
					{
						newSealOwner = CABAL_DAWN;
					}
					else if (duskProportion >= Math.round(0.10 * totalDuskMembers))
					{
						newSealOwner = prevSealOwner;
					}
					else
					{
						newSealOwner = CABAL_NULL;
					}
					break;
				case CABAL_DUSK:
					if (duskProportion >= Math.round(0.10 * totalDuskMembers))
					{
						newSealOwner = prevSealOwner;
					}
					else if (dawnProportion >= Math.round(0.35 * totalDawnMembers))
					{
						newSealOwner = CABAL_DAWN;
					}
					else
					{
						newSealOwner = CABAL_NULL;
					}
					break;
				}
				break;
			}

			_signsSealOwners.put(currSeal, newSealOwner);

			// Alert all online players to new seal status.
			switch (currSeal)
			{
			case SEAL_AVARICE:
				if (newSealOwner == CABAL_DAWN)
				{
					sendMessageToAll(SystemMessage.SEVEN_SIGNS_THE_LORDS_OF_DAWN_HAVE_OBTAINED_THE_SEAL_OF_AVARICE);
				}
				else if (newSealOwner == CABAL_DUSK)
				{
					sendMessageToAll(SystemMessage.SEVEN_SIGNS_THE_REVOLUTIONARIES_OF_DUSK_HAVE_OBTAINED_THE_SEAL_OF_AVARICE);
				}
				break;
			case SEAL_GNOSIS:
				if (newSealOwner == CABAL_DAWN)
				{
					sendMessageToAll(SystemMessage.SEVEN_SIGNS_THE_LORDS_OF_DAWN_HAVE_OBTAINED_THE_SEAL_OF_GNOSIS);
				}
				else if (newSealOwner == CABAL_DUSK)
				{
					sendMessageToAll(SystemMessage.SEVEN_SIGNS_THE_REVOLUTIONARIES_OF_DUSK_HAVE_OBTAINED_THE_SEAL_OF_GNOSIS);
				}
				break;
			case SEAL_STRIFE:
				if (newSealOwner == CABAL_DAWN)
				{
					sendMessageToAll(SystemMessage.SEVEN_SIGNS_THE_LORDS_OF_DAWN_HAVE_OBTAINED_THE_SEAL_OF_STRIFE);
				}
				else if (newSealOwner == CABAL_DUSK)
				{
					sendMessageToAll(SystemMessage.SEVEN_SIGNS_THE_REVOLUTIONARIES_OF_DUSK_HAVE_OBTAINED_THE_SEAL_OF_STRIFE);
				}
				break;
			}
		}
	}

	public class SevenSignsAnnounce extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (Player player : GameObjectsStorage.getAllPlayersForIterate())
			{
				sendCurrentPeriodMsg(player);
			}
			ThreadPoolManager.getInstance().schedule(new SevenSignsAnnounce(), Config.SS_ANNOUNCE_PERIOD * 1000L * 60);
		}
	}

	/**
	 * The primary controller of period change of the Seven Signs system.
	 * This runs all related tasks depending on the period that is about to begin.
	 */
	public class SevenSignsPeriodChange extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			_log.info("SevenSignsPeriodChange: old=" + _activePeriod);
			int periodEnded = _activePeriod;
			_activePeriod++;
			switch (periodEnded)
			{
			case PERIOD_COMP_RECRUITING: // Initialization
				sendMessageToAll(SystemMessage.SEVEN_SIGNS_THE_QUEST_EVENT_PERIOD_HAS_BEGUN_VISIT_A_PRIEST_OF_DAWN_OR_DUSK_TO_PARTICIPATE_IN_THE_EVENT);
				// раздаем награды за рейдов, к печатям не относится но чтобы не заводить еще один таймер...
				RaidBossSpawnManager.getInstance().distributeRewards();
				break;
			case PERIOD_COMPETITION: // Results Calculation
				sendMessageToAll(SystemMessage.SEVEN_SIGNS_THE_QUEST_EVENT_PERIOD_HAS_ENDED_THE_NEXT_QUEST_EVENT_WILL_START_IN_ONE_WEEK);
				int compWinner = getCabalHighestScore();
				calcNewSealOwners();
				if (compWinner == CABAL_DUSK)
				{
					sendMessageToAll(SystemMessage.SEVEN_SIGNS_THE_REVOLUTIONARIES_OF_DUSK_HAVE_WON);
				}
				else
				{
					sendMessageToAll(SystemMessage.SEVEN_SIGNS_THE_LORDS_OF_DAWN_HAVE_WON);
				}
				_previousWinner = compWinner;
				break;
			case PERIOD_COMP_RESULTS: // Seal Validation
				SevenSignsFestival.getInstance().distribAccumulatedBonus();
				// reward highest ranking members from cycle
				SevenSignsFestival.getInstance().rewardHighestRanked();
				// Perform initial Seal Validation set up.
				initializeSeals();
				// раздаем награды за рейдов, к печатям не относится но чтобы не заводить еще один таймер...
				RaidBossSpawnManager.getInstance().distributeRewards();
				// Send message that Seal Validation has begun.
				sendMessageToAll(SystemMessage.SEVEN_SIGNS_THE_SEAL_VALIDATION_PERIOD_HAS_BEGUN);
				_log.info("SevenSigns: The " + getCabalName(_previousWinner) + " have won the competition with " + getCurrentScore(_previousWinner) + " points!");
				break;
			case PERIOD_SEAL_VALIDATION: // Reset for New Cycle
				// Ensure a cycle restart when this period ends.
				_activePeriod = PERIOD_COMP_RECRUITING;
				// Send message that Seal Validation has ended.
				sendMessageToAll(SystemMessage.SEVEN_SIGNS_THE_SEAL_VALIDATION_PERIOD_HAS_ENDED);
				// Reset all data
				resetPlayerData();
				resetSeals();
				_dawnStoneScore = 0;
				_duskStoneScore = 0;
				_dawnFestivalScore = 0;
				_duskFestivalScore = 0;
				_currentCycle++;
				// Reset all Festival-related data and remove any unused blood offerings.
				SevenSignsFestival.getInstance().resetFestivalData(false);
				break;
			}
			// Make sure all Seven Signs data is saved for future use.
			saveSevenSignsData(0, true);
			_log.info("SevenSignsPeriodChange: new=" + _activePeriod);
			try
			{
				_log.info("SevenSigns: Change Catacomb spawn...");
				getListenerEngine().onPeriodChange();
				SSQInfo ss = new SSQInfo();
				for (Player player : GameObjectsStorage.getAllPlayersForIterate())
				{
					player.sendPacket(ss);
				}
				_log.info("SevenSigns: Spawning NPCs...");
				spawnSevenSignsNPC();
				_log.info("SevenSigns: The " + getCurrentPeriodName() + " period has begun!");
				_log.info("SevenSigns: Calculating next period change time...");
				setCalendarForNextPeriodChange();
				_log.info("SevenSignsPeriodChange: time to next change=" + Util.formatTime((int) (getMilliToPeriodChange() / 1000L)));
				SevenSignsPeriodChange sspc = new SevenSignsPeriodChange();
				_periodChange = ThreadPoolManager.getInstance().schedule(sspc, getMilliToPeriodChange());
			}
			catch (RuntimeException e)
			{
				_log.error("Error on SevenSignsPeriodChange", e);
			}
		}
	}

	public int getPriestCabal(int id)
	{
		switch (id)
		{
		case 31078:
		case 31079:
		case 31080:
		case 31081:
		case 31082: // Dawn Priests
		case 31083:
		case 31084:
		case 31168:
		case 31997:
		case 31692:
		case 31694:
			return CABAL_DAWN;
		case 31085:
		case 31086:
		case 31087:
		case 31088: // Dusk Priest
		case 31089:
		case 31090:
		case 31091:
		case 31169:
		case 31998:
		case 31693:
		case 31695:
			return CABAL_DUSK;
		}
		return CABAL_NULL;
	}

	public void changePeriod()
	{
		_periodChange = ThreadPoolManager.getInstance().schedule(new SevenSignsPeriodChange(), 10);
	}

	public void changePeriod(int period)
	{
		changePeriod(period, 1);
	}

	public void changePeriod(int period, int seconds)
	{
		_activePeriod = period - 1;
		if (_activePeriod < 0)
		{
			_activePeriod += 4;
		}
		_periodChange = ThreadPoolManager.getInstance().schedule(new SevenSignsPeriodChange(), seconds * 1000L);
	}

	public void setTimeToNextPeriodChange(int time)
	{
		_calendar.setTimeInMillis(System.currentTimeMillis() + time * 1000L * 60);
		if (_periodChange != null)
		{
			_periodChange.cancel(false);
		}
		_periodChange = ThreadPoolManager.getInstance().schedule(new SevenSignsPeriodChange(), getMilliToPeriodChange());
	}

	public SSListenerList getListenerEngine()
	{
		return _listenerList;
	}

	public <T extends GameListener> boolean addListener(T listener)
	{
		return _listenerList.add(listener);
	}

	public <T extends GameListener> boolean removeListener(T listener)
	{
		return _listenerList.remove(listener);
	}
}