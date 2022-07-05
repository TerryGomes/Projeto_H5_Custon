package l2f.gameserver.model.entity.tournament;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.lang.ArrayUtils;
import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ConfigHolder;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.dao.CharacterDAO;
import l2f.gameserver.dao.MailDAO;
import l2f.gameserver.data.StringHolder;
import l2f.gameserver.data.StringNotFoundException;
import l2f.gameserver.instancemanager.ServerVariables;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.clientpackets.RequestExSendPost;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.network.serverpackets.components.IStaticPacket;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.utils.Debug;
import l2f.gameserver.utils.Language;
import l2f.gameserver.utils.TimeUtils;

public class BattleNotificationManager extends RunnableImpl
{
	private static final Logger LOG = LoggerFactory.getLogger(BattleNotificationManager.class);

	private static final String LAST_NOTIFICATION_DATE_VAR = "TournamentLastNotificationCheckDate";
	private static final SimpleDateFormat MAIL_BATTLE_DATE_FORMAT = new SimpleDateFormat("HH:mm dd.MM.yyyy");

	private BattleNotificationManager()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 1000L, 1000L);
	}

	@Override
	public void runImpl()
	{
		if (BattleScheduleManager.getInstance().isScheduleActive())
		{
			checkNotifyPlayers();
		}
	}

	private void checkNotifyPlayers()
	{
		final long currentDate = System.currentTimeMillis();
		final long lastNotificationDate = ServerVariables.getLong(LAST_NOTIFICATION_DATE_VAR, currentDate);
		final long[] millisToMail = ConfigHolder.getTimeDurationArray("TournamentMailNotificationSeconds", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
		final long[] millisToPM = ConfigHolder.getTimeDurationArray("TournamentPMNotificationSeconds", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
		final long[] millisToGlobal = ConfigHolder.getTimeDurationArray("TournamentGlobalNotificationStartBattleSeconds", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(this, "checkNotifyPlayers", currentDate, lastNotificationDate, Arrays.toString(millisToMail), Arrays.toString(millisToPM));
		}
		for (BattleRecord record : BattleScheduleManager.getInstance().getBattlesForIterate())
		{
			if (record.getBattleDate() > currentDate)
			{
				final long lastNotificationDiff = record.getBattleDate() - lastNotificationDate;
				final long currentDateDiff = record.getBattleDate() - currentDate;
				for (long millis : millisToMail)
				{
					if (millis < lastNotificationDiff && millis >= currentDateDiff)
					{
						sendMailNotifications(record, record.getBattleDate());
					}
				}
				for (long millis : millisToPM)
				{
					if (millis < lastNotificationDiff && millis >= currentDateDiff)
					{
						sendPMNotification(record, millis);
					}
				}
				for (long millis : millisToGlobal)
				{
					if (millis < lastNotificationDiff && millis >= currentDateDiff)
					{
						sendGlobalNotification(record, millis);
					}
				}
			}
		}
		ServerVariables.set(LAST_NOTIFICATION_DATE_VAR, currentDate);
	}

	private void sendMailNotifications(BattleRecord record, long battleDate)
	{
		final Map<Language, String> titlesPerLang = new EnumMap<Language, String>(Language.class);
		for (Language lang : Language.values())
		{
			titlesPerLang.put(lang, StringHolder.getNotNull(lang, "Tournament.Notifications.MailAboutMatchDateTopic", new Object[0]));
		}
		final String battleDateString = MAIL_BATTLE_DATE_FORMAT.format(new Date(battleDate));
		final Map<Integer, Long> emptyAttachments = new HashMap<Integer, Long>(0);
		for (Team team : record.getTeams())
		{
			if (team != null)
			{
				for (int playerId : team.getPlayerIdsForIterate())
				{
					final String playerName = CharacterDAO.getNameByObjectId(playerId);
					final Language playerLanguage = CharacterDAO.getLanguage(playerId);
					final String body = StringHolder.getNotNull(playerLanguage, "Tournament.Notifications.MailAboutMatchDateBody", RequestExSendPost.NEW_LINE_SEPARATOR, playerName, battleDateString);
					if (!MailDAO.hasUnreadMail(playerId, titlesPerLang.get(playerLanguage), body))
					{
						Functions.sendSystemMail(playerName, playerId, titlesPerLang.get(playerLanguage), body, emptyAttachments);
						if (Debug.TOURNAMENT.isActive())
						{
							Debug.TOURNAMENT.debug(this, "sendMailNotifications", playerId, playerName, playerLanguage, titlesPerLang.get(playerLanguage), body);
						}
					}
					else if (Debug.TOURNAMENT.isActive())
					{
						Debug.TOURNAMENT.debug(this, "sendMailNotifications", playerId);
					}
				}
			}
		}
	}

	private static void sendPMNotification(BattleRecord record, long millisToEvent)
	{
		final Map<Language, IStaticPacket> messagePerLang = getPMPacketPerLang(millisToEvent);
		for (Team team : record.getTeams())
		{
			if (team != null)
			{
				for (Player player : team.getOnlinePlayers())
				{
					player.sendPacket(messagePerLang.get(player.getLanguage()));
					if (Debug.TOURNAMENT.isActive())
					{
						Debug.TOURNAMENT.debug(BattleNotificationManager.class, "sendPMNotification", player, messagePerLang.get(player.getLanguage()));
					}
				}
			}
		}
	}

	private static void sendGlobalNotification(BattleRecord record, long millisToEvent)
	{
		final Map<Language, IStaticPacket> messagePerLang = getGlobalPacketPerLang(millisToEvent);
		final List<Player> players = GameObjectsStorage.getAllPlayersCopy();
		players.removeAll(record.getOnlinePlayers());
		for (Player player : players)
		{
			player.sendPacket(messagePerLang.get(player.getLanguage()));
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(BattleNotificationManager.class, "sendGlobalNotification", player, messagePerLang.get(player.getLanguage()));
			}
		}
	}

	public static void onNextRoundStarted(Iterable<BattleRecord> round)
	{
		final Map<Language, String> topics = new EnumMap<Language, String>(Language.class);
		for (Language lang : Language.values())
		{
			topics.put(lang, StringHolder.getNotNull(lang, "Tournament.Notifications.MailAboutMatchDateTopic", new Object[0]));
		}
		final Map<Integer, Long> emptyAttachments = new HashMap<Integer, Long>(0);
		final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy");
		for (BattleRecord battle : round)
		{
			final String dateString = dateFormat.format(new Date(battle.getBattleDate()));
			if (!ArrayUtils.contains(battle.getTeams(), (Object) null))
			{
				for (Team team : battle.getTeams())
				{
					for (int playerId : team.getPlayerIdsForIterate())
					{
						final String receiverName = CharacterDAO.getNameByObjectId(playerId);
						final Language lang2 = CharacterDAO.getLanguage(playerId, true);
						final String body = StringHolder.getNotNull(lang2, "Tournament.Notifications.MailAboutMatchDateBody", RequestExSendPost.NEW_LINE_SEPARATOR, receiverName, dateString);
						Functions.sendSystemMail(receiverName, playerId, topics.get(lang2), body, emptyAttachments);
						if (Debug.TOURNAMENT.isActive())
						{
							Debug.TOURNAMENT.debug(BattleNotificationManager.class, "onNextRoundStarted", playerId, receiverName, lang2, topics.get(lang2), body);
						}
					}
				}
			}
		}
	}

	public static void onChangedBattleDate(BattleRecord battle)
	{
		final Map<Language, String> topics = new EnumMap<Language, String>(Language.class);
		for (Language lang : Language.values())
		{
			topics.put(lang, StringHolder.getNotNull(lang, "Tournament.Notifications.MailAboutMatchDateTopic", new Object[0]));
		}
		final Map<Integer, Long> emptyAttachments = new HashMap<Integer, Long>(0);
		final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy");
		final String dateString = dateFormat.format(new Date(battle.getBattleDate()));
		for (Team team : battle.getTeams())
		{
			for (int playerId : team.getPlayerIdsForIterate())
			{
				final String receiverName = CharacterDAO.getNameByObjectId(playerId);
				final Language lang2 = CharacterDAO.getLanguage(playerId, true);
				final String body = StringHolder.getNotNull(lang2, "Tournament.Notifications.MailAboutMissedMatchDateBody", RequestExSendPost.NEW_LINE_SEPARATOR, receiverName, dateString);
				Functions.sendSystemMail(receiverName, playerId, topics.get(lang2), body, emptyAttachments);
				if (Debug.TOURNAMENT.isActive())
				{
					Debug.TOURNAMENT.debug(BattleNotificationManager.class, "onChangedBattleDate", playerId, receiverName, lang2, topics.get(lang2), body);
				}
			}
		}
	}

	private static Map<Language, IStaticPacket> getPMPacketPerLang(long millisToEvent)
	{
		final Map<Language, IStaticPacket> messagePerLang = new EnumMap<Language, IStaticPacket>(Language.class);
		for (Language lang : Language.values())
		{
			final String sender = StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateSender", new Object[0]);
			final String text = getPMString(lang, millisToEvent);
			final IStaticPacket packet = new Say2(0, ChatType.TELL, sender, text);
			messagePerLang.put(lang, packet);
		}
		return messagePerLang;
	}

	private static String getPMString(Language lang, long millisToEvent)
	{
		final Map<TimeUnit, Long> delays = TimeUtils.getDelayTillTimeUnits(millisToEvent, true, true);
		final long hours = delays.get(TimeUnit.HOURS);
		final long minutes = delays.get(TimeUnit.MINUTES);
		final long seconds = delays.get(TimeUnit.SECONDS);
		if (hours > 1L)
		{
			if (minutes > 1L)
			{
				if (seconds > 1L)
				{
					return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateHsMsSs", hours, minutes, seconds);
				}
				if (seconds == 1L)
				{
					return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateHsMsS", hours, minutes, 1L);
				}
				return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateHsMs", hours, minutes);
			}
			else
			{
				if (minutes != 1L)
				{
					return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateHs", hours);
				}
				if (seconds > 1L)
				{
					return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateHsMSs", hours, 1L, seconds);
				}
				if (seconds == 1L)
				{
					return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateHsMS", hours, 1L, 1L);
				}
				return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateHsM", hours, 1L);
			}
		}
		else if (hours == 1L)
		{
			if (minutes > 1L)
			{
				if (seconds > 1L)
				{
					return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateHMsSs", 1L, minutes, seconds);
				}
				if (seconds == 1L)
				{
					return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateHMsS", 1L, minutes, 1L);
				}
				return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateHMs", 1L, minutes);
			}
			else
			{
				if (minutes != 1L)
				{
					return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateH", 1L);
				}
				if (seconds > 1L)
				{
					return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateHMSs", 1L, 1L, seconds);
				}
				if (seconds == 1L)
				{
					return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateHMS", 1L, 1L, 1L);
				}
				return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateHM", 1L, 1L);
			}
		}
		else if (minutes > 1L)
		{
			if (seconds > 1L)
			{
				return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateMsSs", minutes, seconds);
			}
			if (seconds == 1L)
			{
				return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateMsS", minutes, 1L);
			}
			return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateMs", minutes);
		}
		else if (minutes == 1L)
		{
			if (seconds > 1L)
			{
				return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateMSs", 1L, seconds);
			}
			if (seconds == 1L)
			{
				return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateMS", 1L, 1L);
			}
			return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateM", 1L);
		}
		else
		{
			if (seconds > 1L)
			{
				return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateSs", seconds);
			}
			return StringHolder.getNotNull(lang, "Tournament.Notifications.PMAboutMatchDateS", 1L);
		}
	}

	private static Map<Language, IStaticPacket> getGlobalPacketPerLang(long millisToEvent)
	{
		final Map<Language, IStaticPacket> messagePerLang = new EnumMap<Language, IStaticPacket>(Language.class);
		final ChatType chatType = ConfigHolder.getChatType("TournamentGlobalNotificationChat");
		for (Language lang : Language.values())
		{
			final String sender = StringHolder.getNotNull(lang, "Tournament.GlobalNotification.SenderName", new Object[0]);
			final String text = getGlobalString(lang, millisToEvent);
			final IStaticPacket packet = new Say2(0, chatType, sender, text);
			messagePerLang.put(lang, packet);
		}
		return messagePerLang;
	}

	private static String getGlobalString(Language lang, long millisToEvent)
	{
		final Map<TimeUnit, Long> delays = TimeUtils.getDelayTillTimeUnits(millisToEvent, true, true);
		final long hours = delays.get(TimeUnit.HOURS);
		final long minutes = delays.get(TimeUnit.MINUTES);
		final long seconds = delays.get(TimeUnit.SECONDS);
		if (hours > 1L)
		{
			if (minutes > 1L)
			{
				if (seconds > 1L)
				{
					return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateHsMsSs", hours, minutes, seconds);
				}
				if (seconds == 1L)
				{
					return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateHsMsS", hours, minutes, 1L);
				}
				return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateHsMs", hours, minutes);
			}
			else
			{
				if (minutes != 1L)
				{
					return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateHs", hours);
				}
				if (seconds > 1L)
				{
					return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateHsMSs", hours, 1L, seconds);
				}
				if (seconds == 1L)
				{
					return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateHsMS", hours, 1L, 1L);
				}
				return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateHsM", hours, 1L);
			}
		}
		else if (hours == 1L)
		{
			if (minutes > 1L)
			{
				if (seconds > 1L)
				{
					return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateHMsSs", 1L, minutes, seconds);
				}
				if (seconds == 1L)
				{
					return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateHMsS", 1L, minutes, 1L);
				}
				return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateHMs", 1L, minutes);
			}
			else
			{
				if (minutes != 1L)
				{
					return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateH", 1L);
				}
				if (seconds > 1L)
				{
					return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateHMSs", 1L, 1L, seconds);
				}
				if (seconds == 1L)
				{
					return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateHMS", 1L, 1L, 1L);
				}
				return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateHM", 1L, 1L);
			}
		}
		else if (minutes > 1L)
		{
			if (seconds > 1L)
			{
				return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateMsSs", minutes, seconds);
			}
			if (seconds == 1L)
			{
				return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateMsS", minutes, 1L);
			}
			return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateMs", minutes);
		}
		else if (minutes == 1L)
		{
			if (seconds > 1L)
			{
				return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateMSs", 1L, seconds);
			}
			if (seconds == 1L)
			{
				return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateMS", 1L, 1L);
			}
			return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateM", 1L);
		}
		else
		{
			if (seconds > 1L)
			{
				return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateSs", seconds);
			}
			return StringHolder.getNotNull(lang, "Tournament.GlobalNotification.MatchDateS", 1L);
		}
	}

	public static void announceTournamentResults()
	{
		for (int place : ConfigHolder.getIntArray("TournamentGlobalNotificationAnnounceResultPlaces"))
		{
			final Team placeWinner = TournamentTeamsManager.getInstance().getTeamByFinalPosition(place);
			if (placeWinner != null)
			{
				final List<String> playerNames = placeWinner.getPlayerNames(false);
				final String[] playerNamesArray = new String[playerNames.size()];
				try
				{
					final Map<Language, IStaticPacket> results = getTournamentResults(place, playerNames.toArray(playerNamesArray));
					for (Player player : GameObjectsStorage.getAllPlayersForIterate())
					{
						player.sendPacket(results.get(player.getLanguage()));
					}
				}
				catch (StringNotFoundException e)
				{
					LOG.error("Failed Printing " + place + " place result!", e);
				}
			}
		}
	}

	public static Map<Language, IStaticPacket> getTournamentResults(int position, String[] playerNames) throws StringNotFoundException
	{
		if (playerNames.length == 0)
		{
			return Collections.EMPTY_MAP;
		}
		String address = null;
		switch (position)
		{
		case 1:
		{
			address = getTournamentResultAddress1st(playerNames.length);
			break;
		}
		case 2:
		{
			address = getTournamentResultAddress2nd(playerNames.length);
			break;
		}
		case 3:
		{
			address = getTournamentResultAddress3rd(playerNames.length);
			break;
		}
		case 4:
		{
			address = getTournamentResultAddress4th(playerNames.length);
			break;
		}
		default:
		{
			throw new StringNotFoundException("Could not find Strings for Winning " + position + " position in Tournament!");
		}
		}
		final Map<Language, IStaticPacket> packetsToSend = new EnumMap<Language, IStaticPacket>(Language.class);
		final ChatType chatType = ConfigHolder.getChatType("TournamentGlobalNotificationChat");
		for (Language lang : Language.values())
		{
			final String sender = StringHolder.getNotNull(lang, "Tournament.GlobalNotification.SenderName", new Object[0]);
			final String text = StringHolder.getNotNull(lang, address, (Object[]) playerNames);
			packetsToSend.put(lang, new Say2(0, chatType, sender, text));
		}
		return packetsToSend;
	}

	private static String getTournamentResultAddress1st(int playersCount)
	{
		switch (playersCount)
		{
		case 1:
		{
			return "Tournament.GlobalNotification.Tournament1stPlace.1";
		}
		case 2:
		{
			return "Tournament.GlobalNotification.Tournament1stPlace.2";
		}
		case 3:
		{
			return "Tournament.GlobalNotification.Tournament1stPlace.3";
		}
		case 4:
		{
			return "Tournament.GlobalNotification.Tournament1stPlace.4";
		}
		case 5:
		{
			return "Tournament.GlobalNotification.Tournament1stPlace.5";
		}
		case 6:
		{
			return "Tournament.GlobalNotification.Tournament1stPlace.6";
		}
		case 7:
		{
			return "Tournament.GlobalNotification.Tournament1stPlace.7";
		}
		case 8:
		{
			return "Tournament.GlobalNotification.Tournament1stPlace.8";
		}
		case 9:
		{
			return "Tournament.GlobalNotification.Tournament1stPlace.9";
		}
		default:
		{
			throw new StringNotFoundException("Couldnt find String for Tournament1stPlace with " + playersCount + " members!");
		}
		}
	}

	private static String getTournamentResultAddress2nd(int playersCount)
	{
		switch (playersCount)
		{
		case 1:
		{
			return "Tournament.GlobalNotification.Tournament2ndPlace.1";
		}
		case 2:
		{
			return "Tournament.GlobalNotification.Tournament2ndPlace.2";
		}
		case 3:
		{
			return "Tournament.GlobalNotification.Tournament2ndPlace.3";
		}
		case 4:
		{
			return "Tournament.GlobalNotification.Tournament2ndPlace.4";
		}
		case 5:
		{
			return "Tournament.GlobalNotification.Tournament2ndPlace.5";
		}
		case 6:
		{
			return "Tournament.GlobalNotification.Tournament2ndPlace.6";
		}
		case 7:
		{
			return "Tournament.GlobalNotification.Tournament2ndPlace.7";
		}
		case 8:
		{
			return "Tournament.GlobalNotification.Tournament2ndPlace.8";
		}
		case 9:
		{
			return "Tournament.GlobalNotification.Tournament2ndPlace.9";
		}
		default:
		{
			throw new StringNotFoundException("Couldnt find String for Tournament2ndPlace with " + playersCount + " members!");
		}
		}
	}

	private static String getTournamentResultAddress3rd(int playersCount)
	{
		switch (playersCount)
		{
		case 1:
		{
			return "Tournament.GlobalNotification.Tournament3rdPlace.1";
		}
		case 2:
		{
			return "Tournament.GlobalNotification.Tournament3rdPlace.2";
		}
		case 3:
		{
			return "Tournament.GlobalNotification.Tournament3rdPlace.3";
		}
		case 4:
		{
			return "Tournament.GlobalNotification.Tournament3rdPlace.4";
		}
		case 5:
		{
			return "Tournament.GlobalNotification.Tournament3rdPlace.5";
		}
		case 6:
		{
			return "Tournament.GlobalNotification.Tournament3rdPlace.6";
		}
		case 7:
		{
			return "Tournament.GlobalNotification.Tournament3rdPlace.7";
		}
		case 8:
		{
			return "Tournament.GlobalNotification.Tournament3rdPlace.8";
		}
		case 9:
		{
			return "Tournament.GlobalNotification.Tournament3rdPlace.9";
		}
		default:
		{
			throw new StringNotFoundException("Couldnt find String for Tournament3rdPlace with " + playersCount + " members!");
		}
		}
	}

	private static String getTournamentResultAddress4th(int playersCount)
	{
		switch (playersCount)
		{
		case 1:
		{
			return "Tournament.GlobalNotification.Tournament4thPlace.1";
		}
		case 2:
		{
			return "Tournament.GlobalNotification.Tournament4thPlace.2";
		}
		case 3:
		{
			return "Tournament.GlobalNotification.Tournament4thPlace.3";
		}
		case 4:
		{
			return "Tournament.GlobalNotification.Tournament4thPlace.4";
		}
		case 5:
		{
			return "Tournament.GlobalNotification.Tournament4thPlace.5";
		}
		case 6:
		{
			return "Tournament.GlobalNotification.Tournament4thPlace.6";
		}
		case 7:
		{
			return "Tournament.GlobalNotification.Tournament4thPlace.7";
		}
		case 8:
		{
			return "Tournament.GlobalNotification.Tournament4thPlace.8";
		}
		case 9:
		{
			return "Tournament.GlobalNotification.Tournament4thPlace.9";
		}
		default:
		{
			throw new StringNotFoundException("Couldnt find String for Tournament4thPlace with " + playersCount + " members!");
		}
		}
	}

	public static BattleNotificationManager getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final BattleNotificationManager instance = new BattleNotificationManager();
	}
}
