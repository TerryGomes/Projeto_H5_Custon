package l2mv.gameserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import gnu.trove.map.hash.TIntIntHashMap;
import l2mv.commons.configuration.ExProperties;
import l2mv.commons.net.AdvIP;
import l2mv.commons.net.nio.impl.SelectorConfig;
import l2mv.commons.time.cron.SchedulingPattern;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.model.actor.instances.player.Bonus;
import l2mv.gameserver.model.base.Experience;
import l2mv.gameserver.model.base.PlayerAccess;
import l2mv.gameserver.network.loginservercon.ServerType;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.utils.AddonsConfig;
import l2mv.gameserver.utils.GArray;
import l2mv.gameserver.utils.Location;

public class Config
{
	private static final Logger _log = LoggerFactory.getLogger(Config.class);

	public static final int NCPUS = Runtime.getRuntime().availableProcessors();
	/** Configuration files */
	public static final String ANUSEWORDS_CONFIG_FILE = "config/abusewords.txt";
	public static final String ADV_IP_FILE = "config/advipsystem.ini";
	public static final String GM_PERSONAL_ACCESS_FILE = "config/GMAccess.xml";
	public static final String GM_ACCESS_FILES_DIR = "config/GMAccess.d/";
	public static final String EVENT_CHANGE_OF_HEART_CONFIG_FILE = "config/events/ChangeOfHeart.ini";
	public static final String EVENT_COFFER_OF_SHADOWS_CONFIG_FILE = "config/events/CofferOfShadows.ini";
	public static Map<Integer, PlayerAccess> gmlist = new HashMap<Integer, PlayerAccess>();
//     public static final String FAKE_PLAYERS_LIST = "config/fake_players.list";
//	/** events */
//	public static final String EVENT_APRIL_FOOLS_CONFIG_FILE = "config/events/AprilFools.ini";
//	public static final String EVENT_GLITTERING_MEDAL_CONFIG_FILE = "config/events/GlitteringMedal.ini";
//	public static final String EVENT_L2_DAY_CONFIG_FILE = "config/events/L2Day.ini";
//	public static final String EVENT_LAST_HERO_CONFIG_FILE = "config/events/LastHero.ini";
//	public static final String EVENT_MARCH_8_CONFIG_FILE = "config/events/March8.ini";
//	public static final String EVENT_MASTER_OF_ENCHANING_CONFIG_FILE = "config/events/MasterOfEnchaning.ini";
//	public static final String EVENT_OTHER_EVENTS_CONFIG_FILE = "config/events/OtherEvents.ini";
//	public static final String EVENT_SAVING_SNOWMAN_CONFIG_FILE = "config/events/SavingSnowman.ini";
//	public static final String EVENT_THE_FALL_HARVEST_CONFIG_FILE = "config/events/TheFallHarvest.ini";
//	public static final String EVENT_TRICK_OF_TRANSMUTATION_CONFIG_FILE = "config/events/TrickOfTransmutation.ini";
//	public static final String EVENT_WORLD_DROP_CONFIG_FILE = "config/events/WorldDrop.ini";
//	public static final String PC_CONFIG_FILE = "config/events/PcBangPoints.ini";
//	public static final String DEFENSE_TOWNS_CONFIG_FILE = "config/events/DefenseTowns.ini";

	public static boolean GOODS_INVENTORY_ENABLED = false;
	// Flutuando

//	public static boolean LEVEL_CHANGE_ENABLED;
//	public static int LEVEL_UP_CHANGE_MAX;
//	public static int LEVEL_UP_CHANGE_PRICE;
//	public static int LEVEL_UP_CHANGE_ITEM;
//	public static int LEVEL_DOWN_CHANGE_MAX;
//	public static int LEVEL_DOWN_CHANGE_PRICE;
//	public static int LEVEL_DOWN_CHANGE_ITEM;
//
//	public static boolean REC_SERVICE;
//	public static int REC_ITEM;
//	public static int REC_PRICE;
//
//	public static boolean CRP_SERVICE;
//	public static int CRP_ITEM;
//	public static int CRP_PRICE;
//	public static int CRP_COUNT;
//
//	public static boolean FAME_SERVICE;
//	public static int FAME_ITEM;
//	public static int FAME_PRICE;
//	public static int FAME_COUNT;
//
//	public static boolean NOBLE_ENABLED;
//	public static int NOBLE_SELL_PRICE;
//	public static int NOBLE_SELL_ITEM;
//
//	public static int DRAGONKNIGHT_2ND_D_CHANCE;
//	public static int DRAGONKNIGHT_3ND_D_CHANCE;
//	public static boolean AllowBBSSubManager;
//	public static int LOA_CIRCLE_MOB_UNSPAWN_TIME;
//	public static boolean ALLOW_EPIC_BOSSES_PAGE;
//	public static boolean ALLOW_EPIC_BOSSES_TELEPORT;
//	public static boolean EPIC_BOSSES_TELEPORT_ONLY_FROM_PEACE;
//	public static boolean EPIC_BOSSES_TELEPORT_ONLY_FOR_PREMIUM;
//	public static int EPIC_BOSSES_TELEPORT_PRICE_ID;
//	public static int EPIC_BOSSES_TELEPORT_PRICE_COUNT;
//
//
//	/** Community Board */
//	public static boolean USE_BBS_BUFER_IS_COMBAT;
//	public static boolean USE_BBS_BUFER_IS_CURSE_WEAPON;
//	public static boolean USE_BBS_BUFER_IS_EVENTS;
//	public static boolean USE_BBS_TELEPORT_IS_COMBAT;
//	public static boolean USE_BBS_TELEPORT_IS_EVENTS;
//	public static boolean USE_BBS_PROF_IS_EVENTS;
//	public static boolean SAVE_BBS_TELEPORT_IS_EPIC;
//	public static boolean SAVE_BBS_TELEPORT_IS_BZ;
//
//	public static boolean ALLOW_COMMUNITYBOARD_IN_COMBAT;
//	public static boolean ALLOW_COMMUNITYBOARD_IS_IN_SIEGE;
//	public static boolean COMMUNITYBOARD_BUFFER_ENABLED;
//	public static boolean COMMUNITYBOARD_BUFFER_MAX_LVL_ALLOW;
//	public static boolean COMMUNITYBOARD_BUFFER_SIEGE_ENABLED;
//	public static boolean COMMUNITYBOARD_BUFFER_NO_IS_IN_PEACE_ENABLED;
//	public static boolean COMMUNITYBOARD_SELL_ENABLED;
//	public static boolean COMMUNITYBOARD_SHOP_ENABLED;
//	public static boolean COMMUNITYBOARD_SHOP_NO_IS_IN_PEACE_ENABLED;
//	public static boolean COMMUNITYBOARD_BUFFER_PET_ENABLED;
//	public static boolean COMMUNITYBOARD_BUFFER_SAVE_ENABLED;
//	public static boolean COMMUNITYBOARD_ABNORMAL_ENABLED;
//	public static boolean COMMUNITYBOARD_INSTANCE_ENABLED;
//	public static boolean COMMUNITYBOARD_EVENTS_ENABLED;
//	public static int COMMUNITYBOARD_BUFF_TIME;
//	public static int COMMUNITYBOARD_BUFFER_MAX_LVL;
//	public static int COMMUNITYBOARD_BUFF_PETS_TIME;
//	public static int COMMUNITYBOARD_BUFF_COMBO_TIME;
//	public static int COMMUNITYBOARD_BUFF_SONGDANCE_TIME;
//	public static int COMMUNITYBOARD_BUFF_PICE;
//	public static int COMMUNITYBOARD_BUFF_SAVE_PICE;
//	public static List<Integer> COMMUNITYBOARD_BUFF_ALLOW = new ArrayList<Integer>();
//	public static List<Integer> COMMUNITI_LIST_MAGE_SUPPORT = new ArrayList<Integer>();
//	public static List<Integer> COMMUNITI_LIST_FIGHTER_SUPPORT = new ArrayList<Integer>();
//	public static List<String> COMMUNITYBOARD_MULTISELL_ALLOW = new ArrayList<String>();
//
//	public static boolean COMMUNITYBOARD_TELEPORT_ENABLED;
//	public static int COMMUNITYBOARD_TELE_PICE;
//	public static int COMMUNITYBOARD_SAVE_TELE_PICE;
//	public static boolean COMMUNITYBOARD_TELEPORT_SIEGE_ENABLED;
//	/* Version Configs */
//	public static String SERVER_VERSION;
//	public static String SERVER_BUILD_DATE;
//
//	public static boolean COMMUNITYBOARD_BOARD_ALT_ENABLED;
//	public static int COMMUNITYBOARD_BUFF_PICE_NG;
//	public static int COMMUNITYBOARD_BUFF_PICE_D;
//	public static int COMMUNITYBOARD_BUFF_PICE_C;
//	public static int COMMUNITYBOARD_BUFF_PICE_B;
//	public static int COMMUNITYBOARD_BUFF_PICE_A;
//	public static int COMMUNITYBOARD_BUFF_PICE_S;
//	public static int COMMUNITYBOARD_BUFF_PICE_S80;
//	public static int COMMUNITYBOARD_BUFF_PICE_S84;
//	public static int COMMUNITYBOARD_BUFF_PICE_NG_GR;
//	public static int COMMUNITYBOARD_BUFF_PICE_D_GR;
//	public static int COMMUNITYBOARD_BUFF_PICE_C_GR;
//	public static int COMMUNITYBOARD_BUFF_PICE_B_GR;
//	public static int COMMUNITYBOARD_BUFF_PICE_A_GR;
//	public static int COMMUNITYBOARD_BUFF_PICE_S_GR;
//	public static int COMMUNITYBOARD_BUFF_PICE_S80_GR;
//	public static int COMMUNITYBOARD_BUFF_PICE_S84_GR;
//	public static int COMMUNITYBOARD_TELEPORT_PICE_NG;
//	public static int COMMUNITYBOARD_TELEPORT_PICE_D;
//	public static int COMMUNITYBOARD_TELEPORT_PICE_C;
//	public static int COMMUNITYBOARD_TELEPORT_PICE_B;
//	public static int COMMUNITYBOARD_TELEPORT_PICE_A;
//	public static int COMMUNITYBOARD_TELEPORT_PICE_S;
//	public static int COMMUNITYBOARD_TELEPORT_PICE_S80;
//	public static int COMMUNITYBOARD_TELEPORT_PICE_S84;
//
//	public static boolean COMMUNITYBOARD_ENCHANT_ENABLED;
//	public static boolean ALLOW_BBS_ENCHANT_ELEMENTAR;
//	public static boolean ALLOW_BBS_ENCHANT_ATT;
//	public static int COMMUNITYBOARD_ENCHANT_ITEM;
//	public static int COMMUNITYBOARD_MAX_ENCHANT;
//	public static int[] COMMUNITYBOARD_ENCHANT_LVL;
//	public static int[] COMMUNITYBOARD_ENCHANT_PRICE_WEAPON;
//	public static int[] COMMUNITYBOARD_ENCHANT_PRICE_ARMOR;
//	public static int[] COMMUNITYBOARD_ENCHANT_ATRIBUTE_LVL_WEAPON;
//	public static int[] COMMUNITYBOARD_ENCHANT_ATRIBUTE_PRICE_WEAPON;
//	public static int[] COMMUNITYBOARD_ENCHANT_ATRIBUTE_LVL_ARMOR;
//	public static int[] COMMUNITYBOARD_ENCHANT_ATRIBUTE_PRICE_ARMOR;
//	public static boolean COMMUNITYBOARD_ENCHANT_ATRIBUTE_PVP;
//    public static boolean ENTER_WORLD_SHOW_HTML_LOCK;

	public static final String VIKTORINA_CONFIG_FILE = "config/events/Victorina.ini";
	public static boolean VIKTORINA_ENABLED;// false;
	public static boolean VIKTORINA_REMOVE_QUESTION;// false;;
	public static boolean VIKTORINA_REMOVE_QUESTION_NO_ANSWER;// = false;
	public static int VIKTORINA_START_TIME_HOUR;// 16;
	public static int VIKTORINA_START_TIME_MIN;// 16;
	public static int VIKTORINA_WORK_TIME;// 2;
	public static int VIKTORINA_TIME_ANSER;// 1;
	public static int VIKTORINA_TIME_PAUSE;// 1;

	public static void loadVIKTORINAsettings()
	{
		ExProperties VIKTORINASettings = load(VIKTORINA_CONFIG_FILE);

		Config.VIKTORINA_ENABLED = VIKTORINASettings.getProperty("Victorina_Enabled", false);
		Config.VIKTORINA_REMOVE_QUESTION = VIKTORINASettings.getProperty("Victorina_Remove_Question", false);
		Config.VIKTORINA_REMOVE_QUESTION_NO_ANSWER = VIKTORINASettings.getProperty("Victorina_Remove_Question_No_Answer", false);
		Config.VIKTORINA_START_TIME_HOUR = VIKTORINASettings.getProperty("Victorina_Start_Time_Hour", 16);
		Config.VIKTORINA_START_TIME_MIN = VIKTORINASettings.getProperty("Victorina_Start_Time_Minute", 16);
		Config.VIKTORINA_WORK_TIME = VIKTORINASettings.getProperty("Victorina_Work_Time", 2);
		Config.VIKTORINA_TIME_ANSER = VIKTORINASettings.getProperty("Victorina_Time_Answer", 1);
		Config.VIKTORINA_TIME_PAUSE = VIKTORINASettings.getProperty("Victorina_Time_Pause", 1);

	}

// settings
	public static boolean R_GUARD;
	/** protocol revision */
	public static int MIN_PROTOCOL_REVISION;
	public static int MAX_PROTOCOL_REVISION;

	public static String RESTART_AT_TIME;
	public static boolean BACKUP_DURING_AUTO_RESTART;
	public static int[][] ADDITIONAL_BACKUP_DATES;

	public static int GAME_SERVER_LOGIN_PORT;
	public static boolean GAME_SERVER_LOGIN_CRYPT;
	public static String GAME_SERVER_LOGIN_HOST;
	public static String INTERNAL_HOSTNAME;
	public static String EXTERNAL_HOSTNAME;

	public static boolean SECOND_AUTH_ENABLED;
	public static boolean SECOND_AUTH_BAN_ACC;
	public static boolean SECOND_AUTH_STRONG_PASS;
	public static int SECOND_AUTH_MAX_ATTEMPTS;
	public static long SECOND_AUTH_BAN_TIME;
	public static String SECOND_AUTH_REC_LINK;
	public static int HTM_CACHE_MODE;
	public static boolean LOG_SERVICES;
	public static boolean ALLOW_ADDONS_CONFIG;
	public static boolean AUTH_SERVER_GM_ONLY;
	public static boolean AUTH_SERVER_BRACKETS;
	public static boolean AUTH_SERVER_IS_PVP;
	public static int AUTH_SERVER_AGE_LIMIT;
	public static int AUTH_SERVER_SERVER_TYPE;
	/** GameServer ports */
	public static int[] PORTS_GAME;
	public static String GAMESERVER_HOSTNAME;
	public static boolean ADVIPSYSTEM;
	public static List<AdvIP> GAMEIPS = new ArrayList<AdvIP>();
	public static String DATABASE_DRIVER;
	public static int DATABASE_MAX_CONNECTIONS;
	public static int DATABASE_MAX_IDLE_TIMEOUT;
	public static int DATABASE_IDLE_TEST_PERIOD;
	public static String DATABASE_GAME_URL;
	public static String DATABASE_GAME_USER;
	public static String DATABASE_GAME_PASSWORD;
	public static String DATABASE_LOGIN_URL;
	public static String DATABASE_LOGIN_USER;
	public static String DATABASE_LOGIN_PASSWORD;
	public static String MYSQL_DUMP_PATH;
	public static boolean ACCEPT_ALTERNATE_ID;
	public static int REQUEST_ID;
	public static String SERVER_NAME;
	public static String SHORT_SERVER_NAME;
	public static String SERVER_SUB_NAME;

	/** For test servers - evrybody has admin rights */
	public static boolean EVERYBODY_HAS_ADMIN_RIGHTS;
	public static boolean ENABLE_VOTE;
	public static String VOTE_ADDRESS;
	public static boolean HIDE_GM_STATUS;
	public static boolean SHOW_GM_LOGIN;
	public static boolean SAVE_GM_EFFECTS; // Silence, gmspeed, etc...
	public static String CHAR_NAME_TEMPLATE;
	public static String CLAN_NAME_TEMPLATE;
	public static String CLAN_TITLE_TEMPLATE;
	public static String ALLY_NAME_TEMPLATE;
	public static boolean PARALIZE_ON_RAID_DIFF;
	public static int AUTODESTROY_ITEM_AFTER;
	public static int AUTODESTROY_PLAYER_ITEM_AFTER;

	public static int DELETE_DAYS;

	public static int PURGE_BYPASS_TASK_FREQUENCY;
	/** Datapack root directory */
	public static File DATAPACK_ROOT;

	public static boolean ALLOW_DISCARDITEM;
	public static boolean ALLOW_DISCARDITEM_AT_PEACE;
	public static boolean ALLOW_MAIL;
	public static boolean ALLOW_WAREHOUSE;
	public static boolean ALLOW_WATER;
	public static boolean ALLOW_CURSED_WEAPONS;
	public static boolean DROP_CURSED_WEAPONS_ON_KICK;

	public static boolean ALLOW_ENTER_INSTANCE;
	public static boolean ALLOW_PRIVATE_STORES;
	public static boolean ALLOW_TALK_TO_NPCS;
	public static boolean ALLOW_JUST_MOVING;
	public static boolean ALLOW_TUTORIAL;
	public static boolean ALLOW_HWID_ENGINE;
	public static boolean ALLOW_SKILLS_STATS_LOGGER;
	public static boolean ALLOW_ITEMS_LOGGING;
	public static boolean ALLOW_SPAWN_PROTECTION;
	// Database additional options
	public static boolean AUTOSAVE;
	public static int MAXIMUM_ONLINE_USERS;
	public static int ONLINE_PLUS;
	public static long USER_INFO_INTERVAL;
	public static boolean BROADCAST_STATS_INTERVAL;
	public static long BROADCAST_CHAR_INFO_INTERVAL;

	public static int EFFECT_TASK_MANAGER_COUNT;
	/** Thread pools size */
	public static int SCHEDULED_THREAD_POOL_SIZE;
	public static int EXECUTOR_THREAD_POOL_SIZE;
	public static boolean ENABLE_RUNNABLE_STATS;
	/** Network settings */
	public static SelectorConfig SELECTOR_CONFIG = new SelectorConfig();
	public static String DEFAULT_LANG;
	public static String DEFAULT_GK_LANG;
	public static int SHIFT_BY;
	public static int SHIFT_BY_Z;
	public static int MAP_MIN_Z;
	public static int MAP_MAX_Z;
	public static int MOVE_PACKET_DELAY;
	public static int ATTACK_PACKET_DELAY;
	public static boolean DAMAGE_FROM_FALLING;
	public static int MAX_REFLECTIONS_COUNT;
	public static int WEAR_DELAY;
	public static double ALT_VITALITY_NEVIT_UP_POINT;
	public static double ALT_VITALITY_NEVIT_POINT;

	public static boolean ALLOW_IP_LOCK;
	public static boolean ALLOW_HWID_LOCK;
	public static int HWID_LOCK_MASK;

	public static boolean ENABLE_SECONDARY_PASSWORD;

	public static String MAIL_USER;
	public static String MAIL_PASS;
	public static String MAIL_SUBJECT;
	public static String MAIL_MESSAGE;

	public static String[] FORBIDDEN_CHAR_NAMES;

	public static boolean NORMAL_PLAYER_RECIEVE_MSG_ON_WRONG_ACCOUNT_PASS;
	public static ChatType NORMAL_PLAYER_MSG_TYPE_ON_WRONG_ACCOUNT;
	public static boolean NORMAL_PLAYER_MAIL_ON_WRONG_ACCOUNT_WHILE_OFFLINE;
	public static boolean NORMAL_PLAYER_MAIL_ON_WRONG_SECONDARY_PASSWORD;

	public static int CNAME_MAXLEN;

	public static final String CONFIGURATION_FILE = "config/server.ini";

	public static void loadServerConfig()
	{
		ExProperties serverSettings = load(CONFIGURATION_FILE);

		Config.R_GUARD = serverSettings.getProperty("R_GUARD", true);
		Config.LOG_SERVICES = serverSettings.getProperty("Services", false);
		Config.GAME_SERVER_LOGIN_HOST = serverSettings.getProperty("LoginHost", "127.0.0.1");
		Config.GAME_SERVER_LOGIN_PORT = serverSettings.getProperty("LoginPort", 9013);
		Config.GAME_SERVER_LOGIN_CRYPT = serverSettings.getProperty("LoginUseCrypt", true);

		Config.AUTH_SERVER_AGE_LIMIT = serverSettings.getProperty("ServerAgeLimit", 0);
		Config.AUTH_SERVER_GM_ONLY = serverSettings.getProperty("ServerGMOnly", false);
		Config.AUTH_SERVER_BRACKETS = serverSettings.getProperty("ServerBrackets", false);
		Config.AUTH_SERVER_IS_PVP = serverSettings.getProperty("PvPServer", false);
		for (String a : serverSettings.getProperty("ServerType", ArrayUtils.EMPTY_STRING_ARRAY))
		{
			if (a.trim().isEmpty())
			{
				continue;
			}

			ServerType t = ServerType.valueOf(a.toUpperCase());
			Config.AUTH_SERVER_SERVER_TYPE |= t.getMask();
		}

		Config.SECOND_AUTH_ENABLED = serverSettings.getProperty("SAEnabled", false);
		Config.SECOND_AUTH_BAN_ACC = serverSettings.getProperty("SABanAccEnabled", false);
		Config.SECOND_AUTH_STRONG_PASS = serverSettings.getProperty("SAStrongPass", false);
		Config.SECOND_AUTH_MAX_ATTEMPTS = serverSettings.getProperty("SAMaxAttemps", 5);
		Config.SECOND_AUTH_BAN_TIME = serverSettings.getProperty("SABanTime", 480);
		Config.SECOND_AUTH_REC_LINK = serverSettings.getProperty("SARecoveryLink", "http://www.my-domain.com/charPassRec.php");

		Config.INTERNAL_HOSTNAME = serverSettings.getProperty("InternalHostname", "127.0.0.1");
		Config.EXTERNAL_HOSTNAME = serverSettings.getProperty("ExternalHostname", "127.0.0.1");
		Config.ADVIPSYSTEM = serverSettings.getProperty("AdvIPSystem", false);
		Config.REQUEST_ID = serverSettings.getProperty("RequestServerID", 0);
		Config.ACCEPT_ALTERNATE_ID = serverSettings.getProperty("AcceptAlternateID", true);

		Config.GAMESERVER_HOSTNAME = serverSettings.getProperty("GameserverHostname", "127.0.0.1");
		Config.PORTS_GAME = serverSettings.getProperty("GameserverPort", new int[]
		{
			7777
		});

		Config.SERVER_NAME = serverSettings.getProperty("ServerName", "Server");
		Config.SHORT_SERVER_NAME = serverSettings.getProperty("ShortServerName", "Server");
		Config.SERVER_SUB_NAME = serverSettings.getProperty("ServerSubName", "Server");

		Config.EVERYBODY_HAS_ADMIN_RIGHTS = serverSettings.getProperty("EverybodyHasAdminRights", false);

		Config.ENABLE_VOTE = serverSettings.getProperty("EnableVoteReward", false);
		Config.VOTE_ADDRESS = serverSettings.getProperty("VoteAddress", "https://website.com/check/StringTake.php?IP=");

		Config.HIDE_GM_STATUS = serverSettings.getProperty("HideGMStatus", false);
		Config.SHOW_GM_LOGIN = serverSettings.getProperty("ShowGMLogin", true);
		Config.SAVE_GM_EFFECTS = serverSettings.getProperty("SaveGMEffects", false);

		Config.CHAR_NAME_TEMPLATE = serverSettings.getProperty("CnameTemplate", "([0-9A-Za-z]{2,16})|([0-9\u0410-\u044f-\u4e00-\u9fa5]{2,16})");
		Config.CLAN_NAME_TEMPLATE = serverSettings.getProperty("ClanNameTemplate", "[A-Za-z0-9\u0410-\u042f\u0430-\u044f]{3,16}");
		Config.CLAN_TITLE_TEMPLATE = serverSettings.getProperty("ClanTitleTemplate", "[A-Za-z0-9\u0410-\u042f\u0430-\u044f \\p{Punct}]{1,16}");
		Config.ALLY_NAME_TEMPLATE = serverSettings.getProperty("AllyNameTemplate", "[A-Za-z0-9\u0410-\u042f\u0430-\u044f]{3,16}");
		Config.CNAME_MAXLEN = serverSettings.getProperty("NameMaxlen", 32);

		Config.PARALIZE_ON_RAID_DIFF = serverSettings.getProperty("ParalizeOnRaidLevelDiff", true);

		Config.AUTODESTROY_ITEM_AFTER = serverSettings.getProperty("AutoDestroyDroppedItemAfter", 0);
		Config.AUTODESTROY_PLAYER_ITEM_AFTER = serverSettings.getProperty("AutoDestroyPlayerDroppedItemAfter", 0);
		Config.DELETE_DAYS = serverSettings.getProperty("DeleteCharAfterDays", 7);
		Config.PURGE_BYPASS_TASK_FREQUENCY = serverSettings.getProperty("PurgeTaskFrequency", 60);

		try
		{
			Config.DATAPACK_ROOT = new File(serverSettings.getProperty("DatapackRoot", ".")).getCanonicalFile();
		}
		catch (IOException e)
		{
			_log.error("Error while loading DATAPACK_ROOT", e);
		}

		Config.ALLOW_DISCARDITEM = serverSettings.getProperty("AllowDiscardItem", true);
		Config.ALLOW_DISCARDITEM_AT_PEACE = serverSettings.getProperty("AllowDiscardItemInTown", true);
		Config.ALLOW_MAIL = serverSettings.getProperty("AllowMail", true);
		Config.ALLOW_WAREHOUSE = serverSettings.getProperty("AllowWarehouse", true);
		Config.ALLOW_WATER = serverSettings.getProperty("AllowWater", true);
		Config.ALLOW_CURSED_WEAPONS = serverSettings.getProperty("AllowCursedWeapons", false);
		Config.DROP_CURSED_WEAPONS_ON_KICK = serverSettings.getProperty("DropCursedWeaponsOnKick", false);
		Config.ALLOW_ENTER_INSTANCE = serverSettings.getProperty("AllowEnterInstance", true);
		Config.ALLOW_PRIVATE_STORES = serverSettings.getProperty("AllowStores", true);
		Config.ALLOW_TALK_TO_NPCS = serverSettings.getProperty("AllowTalkToNpcs", true);
		Config.ALLOW_JUST_MOVING = serverSettings.getProperty("AllowJustMoving", false);
		Config.ALLOW_TUTORIAL = serverSettings.getProperty("AllowTutorial", true);
		Config.ALLOW_HWID_ENGINE = serverSettings.getProperty("AllowHWIDEngine", true);
		Config.ALLOW_SKILLS_STATS_LOGGER = serverSettings.getProperty("AllowSkillStatsLogger", true);
		Config.ALLOW_ITEMS_LOGGING = serverSettings.getProperty("AllowItemsLogging", true);
		Config.ALLOW_SPAWN_PROTECTION = serverSettings.getProperty("AllowSpawnProtection", true);

		Config.MIN_PROTOCOL_REVISION = serverSettings.getProperty("MinProtocolRevision", 267);
		Config.MAX_PROTOCOL_REVISION = serverSettings.getProperty("MaxProtocolRevision", 271);

		Config.AUTOSAVE = serverSettings.getProperty("Autosave", true);

		Config.MAXIMUM_ONLINE_USERS = serverSettings.getProperty("MaximumOnlineUsers", 3000);
		Config.ONLINE_PLUS = serverSettings.getProperty("OnlineUsersPlus", 1);

		Config.DATABASE_DRIVER = serverSettings.getProperty("Driver", "com.mysql.jdbc.Driver");
		Config.DATABASE_MAX_CONNECTIONS = serverSettings.getProperty("MaximumDbConnections", 10);
		Config.DATABASE_MAX_IDLE_TIMEOUT = serverSettings.getProperty("MaxIdleConnectionTimeout", 600);
		Config.DATABASE_IDLE_TEST_PERIOD = serverSettings.getProperty("IdleConnectionTestPeriod", 60);

		Config.DATABASE_GAME_URL = serverSettings.getProperty("GameURL", "jdbc:mysql://localhost/l2jdb");
		Config.DATABASE_GAME_USER = serverSettings.getProperty("GameUser", "root");
		Config.DATABASE_GAME_PASSWORD = serverSettings.getProperty("GamePassword", "");
		Config.DATABASE_LOGIN_URL = serverSettings.getProperty("LoginURL", "jdbc:mysql://localhost/l2jdb");
		Config.DATABASE_LOGIN_USER = serverSettings.getProperty("LoginUser", "root");
		Config.DATABASE_LOGIN_PASSWORD = serverSettings.getProperty("LoginPassword", "");
		Config.MYSQL_DUMP_PATH = serverSettings.getProperty("MySqlDumpPath", "");
		Config.USER_INFO_INTERVAL = serverSettings.getProperty("UserInfoInterval", 100L);
		Config.BROADCAST_STATS_INTERVAL = serverSettings.getProperty("BroadcastStatsInterval", true);
		Config.BROADCAST_CHAR_INFO_INTERVAL = serverSettings.getProperty("BroadcastCharInfoInterval", 100L);

		Config.EFFECT_TASK_MANAGER_COUNT = serverSettings.getProperty("EffectTaskManagers", 2);

		Config.SCHEDULED_THREAD_POOL_SIZE = serverSettings.getProperty("ScheduledThreadPoolSize", NCPUS * 4);
		Config.EXECUTOR_THREAD_POOL_SIZE = serverSettings.getProperty("ExecutorThreadPoolSize", NCPUS * 2);

		Config.ENABLE_RUNNABLE_STATS = serverSettings.getProperty("EnableRunnableStats", false);

		Config.SELECTOR_CONFIG.SLEEP_TIME = serverSettings.getProperty("SelectorSleepTime", 10L);
		Config.SELECTOR_CONFIG.INTEREST_DELAY = serverSettings.getProperty("InterestDelay", 30L);
		Config.SELECTOR_CONFIG.MAX_SEND_PER_PASS = serverSettings.getProperty("MaxSendPerPass", 32);
		Config.SELECTOR_CONFIG.READ_BUFFER_SIZE = serverSettings.getProperty("ReadBufferSize", 65536);
		Config.SELECTOR_CONFIG.WRITE_BUFFER_SIZE = serverSettings.getProperty("WriteBufferSize", 131072);
		Config.SELECTOR_CONFIG.HELPER_BUFFER_COUNT = serverSettings.getProperty("BufferPoolSize", 64);

		Config.DEFAULT_LANG = serverSettings.getProperty("DefaultLang", "ru");
		Config.RESTART_AT_TIME = serverSettings.getProperty("AutoRestartAt", "0 5 * * *");
		Config.BACKUP_DURING_AUTO_RESTART = serverSettings.getProperty("BackupDuringAutoRestart", false);

		final String backupDates = serverSettings.getProperty("AdditionalBackupDates", "6,40");
		final String[] datesSplit = backupDates.split(";");
		Config.ADDITIONAL_BACKUP_DATES = new int[datesSplit.length][2];
		for (int d = 0; d < datesSplit.length; d++)
		{
			final String[] dates = datesSplit[d].split(",");
			for (int i = 0; i < 2; i++)
			{
				ADDITIONAL_BACKUP_DATES[d][0] = Integer.parseInt(dates[0]);
				ADDITIONAL_BACKUP_DATES[d][1] = Integer.parseInt(dates[1]);
			}
		}

		Config.SHIFT_BY = serverSettings.getProperty("HShift", 12);
		Config.SHIFT_BY_Z = serverSettings.getProperty("VShift", 11);
		Config.MAP_MIN_Z = serverSettings.getProperty("MapMinZ", -32768);
		Config.MAP_MAX_Z = serverSettings.getProperty("MapMaxZ", 32767);

		Config.MOVE_PACKET_DELAY = serverSettings.getProperty("MovePacketDelay", 100);
		Config.ATTACK_PACKET_DELAY = serverSettings.getProperty("AttackPacketDelay", 500);

		Config.DAMAGE_FROM_FALLING = serverSettings.getProperty("DamageFromFalling", true);

		Config.MAX_REFLECTIONS_COUNT = serverSettings.getProperty("MaxReflectionsCount", 300);

		Config.WEAR_DELAY = serverSettings.getProperty("WearDelay", 5);

		Config.HTM_CACHE_MODE = serverSettings.getProperty("HtmCacheMode", HtmCache.LAZY);

		Config.ALT_VITALITY_NEVIT_UP_POINT = serverSettings.getProperty("WebServerDelay", 10);
		Config.ALT_VITALITY_NEVIT_POINT = serverSettings.getProperty("WebServerDelay", 10);

		Config.ALLOW_ADDONS_CONFIG = serverSettings.getProperty("AllowAddonsConfig", false);

		Config.ALLOW_IP_LOCK = serverSettings.getProperty("AllowLockIP", false);
		Config.ALLOW_HWID_LOCK = serverSettings.getProperty("AllowLockHwid", false);
		Config.HWID_LOCK_MASK = serverSettings.getProperty("HwidLockMask", 10);

		Config.ENABLE_SECONDARY_PASSWORD = serverSettings.getProperty("EnableSecondaryPassword", true);

		Config.MAIL_USER = serverSettings.getProperty("MailUser", "");
		Config.MAIL_PASS = serverSettings.getProperty("MailPass", "");
		Config.MAIL_SUBJECT = serverSettings.getProperty("MailSubject", "");
		Config.MAIL_MESSAGE = serverSettings.getProperty("MailMessage", "");

		Config.NORMAL_PLAYER_RECIEVE_MSG_ON_WRONG_ACCOUNT_PASS = serverSettings.getProperty("NormalPlayerReceiveMsgOnWrongAccountPass", true);
		Config.NORMAL_PLAYER_MSG_TYPE_ON_WRONG_ACCOUNT = ChatType.getTypeFromName(serverSettings.getProperty("NormalPlayerMsgTypeOnWrongAccount", "TELL"));
		Config.NORMAL_PLAYER_MAIL_ON_WRONG_ACCOUNT_WHILE_OFFLINE = serverSettings.getProperty("NormalPlayerMailOnWrongAccountWhileOffline", false);
		Config.NORMAL_PLAYER_MAIL_ON_WRONG_SECONDARY_PASSWORD = serverSettings.getProperty("NormalPlayerMailOnWrongSecondaryPassword", true);

		Config.FORBIDDEN_CHAR_NAMES = serverSettings.getProperty("ForbiddenCharNames", "").split(",");
	}

	public static final String EVENT_HITMAN = "config/events/Hitman.ini";

	public static boolean EVENT_HITMAN_ENABLED;
	public static int EVENT_HITMAN_COST_ITEM_ID;
	public static int EVENT_HITMAN_COST_ITEM_COUNT;
	public static int EVENT_HITMAN_TASKS_PER_PAGE;
	public static String[] EVENT_HITMAN_ALLOWED_ITEM_LIST;

	public static void loadHitmanSettings()
	{
		final ExProperties eventHitmanSettings = load(EVENT_HITMAN);

		EVENT_HITMAN_ENABLED = eventHitmanSettings.getProperty("HitmanEnabled", false);
		EVENT_HITMAN_COST_ITEM_ID = eventHitmanSettings.getProperty("CostItemId", 57);
		EVENT_HITMAN_COST_ITEM_COUNT = eventHitmanSettings.getProperty("CostItemCount", 1000);
		EVENT_HITMAN_TASKS_PER_PAGE = eventHitmanSettings.getProperty("TasksPerPage", 7);
		EVENT_HITMAN_ALLOWED_ITEM_LIST = eventHitmanSettings.getProperty("AllowedItems", new String[]
		{
			"4037",
			"57"
		});
	}

	public static final String CHAT_FILE = "config/chat.ini";
	/** Global chat state */
	public static boolean GLOBAL_SHOUT;
	public static boolean GLOBAL_TRADE_CHAT;
	public static int CHAT_RANGE;
	public static int SHOUT_OFFSET;

	public static int CHATS_REQUIRED_LEVEL;
	public static int PM_REQUIRED_LEVEL;
	public static int SHOUT_REQUIRED_LEVEL;
	public static GArray<String> TRADE_WORDS;
	/** Logging Chat Window */
	public static boolean LOG_CHAT;
	/** ChatBan */
	public static int CHAT_MESSAGE_MAX_LEN;
	public static boolean ABUSEWORD_BANCHAT;
	public static int[] BAN_CHANNEL_LIST = new int[18];
	public static boolean ABUSEWORD_REPLACE;
	public static String ABUSEWORD_REPLACE_STRING;
	public static int ABUSEWORD_BANTIME;
	public static Pattern[] ABUSEWORD_LIST = {};
	public static boolean BANCHAT_ANNOUNCE;
	public static boolean BANCHAT_ANNOUNCE_FOR_ALL_WORLD;
	public static boolean BANCHAT_ANNOUNCE_NICK;

	public static int[] CHATFILTER_CHANNELS = new int[18];
	public static int CHATFILTER_MIN_LEVEL = 0;
	public static int CHATFILTER_WORK_TYPE = 1;
	public static boolean RECORD_WROTE_CHAT_MSGS_COUNT;
	public static int ANNOUNCE_VOTE_DELAY;
	public static boolean TRADE_CHATS_REPLACE;

	public static void loadChatConfig()
	{
		final ExProperties chatSettings = load(CHAT_FILE);

		Config.GLOBAL_SHOUT = chatSettings.getProperty("GlobalShout", false);
		Config.TRADE_CHATS_REPLACE = chatSettings.getProperty("TradeChats", false);
		Config.GLOBAL_TRADE_CHAT = chatSettings.getProperty("GlobalTradeChat", false);
		Config.CHAT_RANGE = chatSettings.getProperty("ChatRange", 1250);
		Config.SHOUT_OFFSET = chatSettings.getProperty("ShoutOffset", 0);

		Config.TRADE_WORDS = new GArray<String>();

		String T_WORLD = chatSettings.getProperty("TradeWords", "trade,sell,selling,buy,exchange,barter,Ð’Ð¢Ð¢,Ð’Ð¢S,WTB,WTB,WTT,WTS");
		String[] T_WORLDS = T_WORLD.split(",", -1);
		for (String w : T_WORLDS)
		{
			Config.TRADE_WORDS.add(w);
		}
		_log.info("Loaded " + TRADE_WORDS.size() + " trade words.");

		Config.LOG_CHAT = chatSettings.getProperty("LogChat", false);
		Config.CHAT_MESSAGE_MAX_LEN = chatSettings.getProperty("ChatMessageLimit", 1000);
		Config.ABUSEWORD_BANCHAT = chatSettings.getProperty("ABUSEWORD_BANCHAT", false);
		int counter = 0;
		for (int id : chatSettings.getProperty("ABUSEWORD_BAN_CHANNEL", new int[]
		{
			0
		}))
		{
			Config.BAN_CHANNEL_LIST[counter] = id;
			counter++;
		}
		Config.ABUSEWORD_REPLACE = chatSettings.getProperty("ABUSEWORD_REPLACE", false);
		Config.ABUSEWORD_REPLACE_STRING = chatSettings.getProperty("ABUSEWORD_REPLACE_STRING", "[censored]");
		Config.BANCHAT_ANNOUNCE = chatSettings.getProperty("BANCHAT_ANNOUNCE", true);
		Config.BANCHAT_ANNOUNCE_FOR_ALL_WORLD = chatSettings.getProperty("BANCHAT_ANNOUNCE_FOR_ALL_WORLD", true);
		Config.BANCHAT_ANNOUNCE_NICK = chatSettings.getProperty("BANCHAT_ANNOUNCE_NICK", true);
		Config.ABUSEWORD_BANTIME = chatSettings.getProperty("ABUSEWORD_UNBAN_TIMER", 30);
		Config.CHATFILTER_MIN_LEVEL = chatSettings.getProperty("ChatFilterMinLevel", 0);

		Config.CHATS_REQUIRED_LEVEL = chatSettings.getProperty("ChatsRequiredLevel", 21);
		Config.PM_REQUIRED_LEVEL = chatSettings.getProperty("PMPlayersInChat", 61);
		Config.SHOUT_REQUIRED_LEVEL = chatSettings.getProperty("ShoutingInChat", 61);

		Config.RECORD_WROTE_CHAT_MSGS_COUNT = chatSettings.getProperty("RecordWroteChatMsgsCount", false);

		Config.ANNOUNCE_VOTE_DELAY = chatSettings.getProperty("AnnounceVoteDelay", 60);
		counter = 0;
		for (int id : chatSettings.getProperty("ChatFilterChannels", new int[]
		{
			1,
			8
		}))
		{
			Config.CHATFILTER_CHANNELS[counter] = id;
			counter++;
		}
		Config.CHATFILTER_WORK_TYPE = chatSettings.getProperty("ChatFilterWorkType", 1);
	}

	public static final String TELNET_CONFIGURATION_FILE = "config/telnet.ini";
	/** telnet enabled */
	public static boolean IS_TELNET_ENABLED;
	public static String TELNET_DEFAULT_ENCODING;
	public static String TELNET_PASSWORD;
	public static String TELNET_HOSTNAME;
	public static int TELNET_PORT;

	public static void loadTelnetConfig()
	{
		final ExProperties telnetSettings = load(TELNET_CONFIGURATION_FILE);

		Config.IS_TELNET_ENABLED = telnetSettings.getProperty("EnableTelnet", false);
		Config.TELNET_DEFAULT_ENCODING = telnetSettings.getProperty("TelnetEncoding", "UTF-8");
		Config.TELNET_PORT = telnetSettings.getProperty("Port", 7000);
		Config.TELNET_HOSTNAME = telnetSettings.getProperty("BindAddress", "127.0.0.1");
		Config.TELNET_PASSWORD = telnetSettings.getProperty("Password", "");
	}

	public static final String WEDDING_FILE = "config/services/Wedding.ini";
	/** Wedding Options */
	public static boolean ALLOW_WEDDING;
	public static int WEDDING_PRICE;
	public static boolean WEDDING_PUNISH_INFIDELITY;
	public static boolean WEDDING_TELEPORT;
	public static int WEDDING_TELEPORT_PRICE;
	public static int WEDDING_TELEPORT_INTERVAL;
	public static boolean WEDDING_SAMESEX;
	public static boolean WEDDING_FORMALWEAR;
	public static int WEDDING_DIVORCE_COSTS;
	public static boolean SPAWN_WEDDING;

	public static void loadWeddingConfig()
	{
		final ExProperties weddingSettings = load(WEDDING_FILE);

		Config.ALLOW_WEDDING = weddingSettings.getProperty("AllowWedding", false);
		Config.WEDDING_PRICE = weddingSettings.getProperty("WeddingPrice", 500000);
		Config.WEDDING_PUNISH_INFIDELITY = weddingSettings.getProperty("WeddingPunishInfidelity", true);
		Config.WEDDING_TELEPORT = weddingSettings.getProperty("WeddingTeleport", true);
		Config.WEDDING_TELEPORT_PRICE = weddingSettings.getProperty("WeddingTeleportPrice", 500000);
		Config.WEDDING_TELEPORT_INTERVAL = weddingSettings.getProperty("WeddingTeleportInterval", 120);
		Config.WEDDING_SAMESEX = weddingSettings.getProperty("WeddingAllowSameSex", true);
		Config.WEDDING_FORMALWEAR = weddingSettings.getProperty("WeddingFormalWear", true);
		Config.WEDDING_DIVORCE_COSTS = weddingSettings.getProperty("WeddingDivorceCosts", 20);
		Config.SPAWN_WEDDING = weddingSettings.getProperty("SPAWN_WEDDING", false);
	}

	public static final String RESIDENCE_CONFIG_FILE = "config/residence.ini";
	public static int CH_BID_GRADE1_MINCLANLEVEL;
	public static int CH_BID_GRADE1_MINCLANMEMBERS;
	public static int CH_BID_GRADE1_MINCLANMEMBERSLEVEL;
	public static int CH_BID_GRADE2_MINCLANLEVEL;
	public static int CH_BID_GRADE2_MINCLANMEMBERS;
	public static int CH_BID_GRADE2_MINCLANMEMBERSLEVEL;
	public static int CH_BID_GRADE3_MINCLANLEVEL;
	public static int CH_BID_GRADE3_MINCLANMEMBERS;
	public static int CH_BID_GRADE3_MINCLANMEMBERSLEVEL;
	public static double RESIDENCE_LEASE_FUNC_MULTIPLIER;
	public static double RESIDENCE_LEASE_MULTIPLIER;
	public static Calendar CASTLE_VALIDATION_DATE;
	public static int[] CASTLE_SELECT_HOURS;
	public static int PERIOD_CASTLE_SIEGE;
	public static boolean RETURN_WARDS_WHEN_TW_STARTS;
	// Fame Reward
	public static boolean ENABLE_ALT_FAME_REWARD;
	public static long ALT_FAME_CASTLE;
	public static long ALT_FAME_FORTRESS;
	public static int INTERVAL_FLAG_DROP;
	// Synerge
	public static int SIEGE_WINNER_REPUTATION_REWARD;
	public static boolean ALLOW_START_FORTRESS_SIEGE_FEE;
	public static int START_FORTRESS_SIEGE_PRICE_ID;
	public static int START_FORTRESS_SIEGE_PRICE_AMOUNT;
	public static boolean FORTRESS_SIEGE_ALLOW_SINGLE_PLAYERS;
	public static boolean SIEGE_ALLOW_FAME_WHILE_DEAD;
	public static boolean SIEGE_ALLOW_FAME_IN_SAFE;
	public static int[][] SIEGE_REWARDS_NEAR_FAME = null;
	public static boolean FORTRESS_REMOVE_FLAG_ON_LEAVE_ZONE;
	public static boolean DOMINION_REMOVE_FLAG_ON_LEAVE_ZONE;

	public static void loadResidenceConfig()
	{
		final ExProperties residenceSettings = load(RESIDENCE_CONFIG_FILE);

		Config.CH_BID_GRADE1_MINCLANLEVEL = residenceSettings.getProperty("ClanHallBid_Grade1_MinClanLevel", 2);
		Config.CH_BID_GRADE1_MINCLANMEMBERS = residenceSettings.getProperty("ClanHallBid_Grade1_MinClanMembers", 1);
		Config.CH_BID_GRADE1_MINCLANMEMBERSLEVEL = residenceSettings.getProperty("ClanHallBid_Grade1_MinClanMembersAvgLevel", 1);
		Config.CH_BID_GRADE2_MINCLANLEVEL = residenceSettings.getProperty("ClanHallBid_Grade2_MinClanLevel", 2);
		Config.CH_BID_GRADE2_MINCLANMEMBERS = residenceSettings.getProperty("ClanHallBid_Grade2_MinClanMembers", 1);
		Config.CH_BID_GRADE2_MINCLANMEMBERSLEVEL = residenceSettings.getProperty("ClanHallBid_Grade2_MinClanMembersAvgLevel", 1);
		Config.CH_BID_GRADE3_MINCLANLEVEL = residenceSettings.getProperty("ClanHallBid_Grade3_MinClanLevel", 2);
		Config.CH_BID_GRADE3_MINCLANMEMBERS = residenceSettings.getProperty("ClanHallBid_Grade3_MinClanMembers", 1);
		Config.CH_BID_GRADE3_MINCLANMEMBERSLEVEL = residenceSettings.getProperty("ClanHallBid_Grade3_MinClanMembersAvgLevel", 1);
		Config.RESIDENCE_LEASE_FUNC_MULTIPLIER = residenceSettings.getProperty("ResidenceLeaseFuncMultiplier", 1.);
		Config.RESIDENCE_LEASE_MULTIPLIER = residenceSettings.getProperty("ResidenceLeaseMultiplier", 1.);

		Config.PERIOD_CASTLE_SIEGE = residenceSettings.getProperty("CastleSiegeIntervalWeeks", 2);
		Config.CASTLE_SELECT_HOURS = residenceSettings.getProperty("CastleSelectHours", new int[]
		{
			16,
			20
		});
		final int[] tempCastleValidatonTime = residenceSettings.getProperty("CastleValidationDate", new int[]
		{
			2,
			4,
			2003
		});
		Config.CASTLE_VALIDATION_DATE = Calendar.getInstance();
		Config.CASTLE_VALIDATION_DATE.set(Calendar.DAY_OF_MONTH, tempCastleValidatonTime[0]);
		Config.CASTLE_VALIDATION_DATE.set(Calendar.MONTH, tempCastleValidatonTime[1] - 1);
		Config.CASTLE_VALIDATION_DATE.set(Calendar.YEAR, tempCastleValidatonTime[2]);
		Config.CASTLE_VALIDATION_DATE.set(Calendar.HOUR_OF_DAY, 0);
		Config.CASTLE_VALIDATION_DATE.set(Calendar.MINUTE, 0);
		Config.CASTLE_VALIDATION_DATE.set(Calendar.SECOND, 0);
		Config.CASTLE_VALIDATION_DATE.set(Calendar.MILLISECOND, 0);

		Config.RETURN_WARDS_WHEN_TW_STARTS = residenceSettings.getProperty("ReturnWardsWhenTWStarts", false);

		Config.ENABLE_ALT_FAME_REWARD = residenceSettings.getProperty("AltEnableCustomFame", false);
		Config.ALT_FAME_CASTLE = residenceSettings.getProperty("CastleFame", 125);
		Config.ALT_FAME_FORTRESS = residenceSettings.getProperty("FortressFame", 31);

		Config.FORTRESS_REMOVE_FLAG_ON_LEAVE_ZONE = residenceSettings.getProperty("FortFlagReturnOnLeaveZone", false);
		Config.DOMINION_REMOVE_FLAG_ON_LEAVE_ZONE = residenceSettings.getProperty("ReturnFlagOnSiegeZoneLeave", false);

		Config.INTERVAL_FLAG_DROP = residenceSettings.getProperty("IntervalFlagDrop", 60);
		Config.SIEGE_WINNER_REPUTATION_REWARD = residenceSettings.getProperty("SiegeWinnerReputationReward", 0);

		Config.ALLOW_START_FORTRESS_SIEGE_FEE = residenceSettings.getProperty("AllowStartFortressSiegeFee", true);
		Config.START_FORTRESS_SIEGE_PRICE_ID = residenceSettings.getProperty("StartFortressSiegePriceId", 57);
		Config.START_FORTRESS_SIEGE_PRICE_AMOUNT = residenceSettings.getProperty("StartFortressSiegePriceAmount", 250000);
		Config.FORTRESS_SIEGE_ALLOW_SINGLE_PLAYERS = residenceSettings.getProperty("FortressSiegeAllowSinglePlayers", false);

		Config.SIEGE_ALLOW_FAME_WHILE_DEAD = residenceSettings.getProperty("SiegeAllowFameWhileDead", false);
		Config.SIEGE_ALLOW_FAME_IN_SAFE = residenceSettings.getProperty("SiegeAllowFameInSafe", false);
		final String rewards = residenceSettings.getProperty("SiegeRewardsNearFame", "");
		if (!rewards.isEmpty())
		{
			final String[] rews = rewards.split(";");
			int i = 0;
			Config.SIEGE_REWARDS_NEAR_FAME = new int[rews.length][2];
			for (String reward : rews)
			{
				final String[] parts = reward.split(",");
				Config.SIEGE_REWARDS_NEAR_FAME[i][0] = Integer.parseInt(parts[0]);
				Config.SIEGE_REWARDS_NEAR_FAME[i][1] = Integer.parseInt(parts[1]);
				i++;
			}
		}
	}

	public static final String ITEM_USE_FILE = "config/mod/UseItems.ini";
	/** ÐšÐ¾Ð½Ñ„Ð¸Ð³ÑƒÑ€Ð°Ñ†Ð¸Ñ� Ð¸Ñ�Ð¿Ð¾Ð»ÑŒÐ·Ð¾Ð²Ð°Ð½Ð¸Ñ� Ð¸Ñ‚ÐµÐ¼Ð¾Ð² Ð¿Ð¾ ÑƒÐ¼Ð¾Ð»Ñ‡Ð°Ð½Ð¸ÑŽ Ð¿Ð¾ÑƒÑˆÐµÐ½Ñ‹ */
	public static int[] ITEM_USE_LIST_ID;
	public static boolean ITEM_USE_IS_COMBAT_FLAG;
	public static boolean ITEM_USE_IS_ATTACK;
	public static boolean ITEM_USE_IS_EVENTS;
	public static long MAX_ADENA;

	public static void loadItemsUseConfig()
	{
		final ExProperties itemsUseSettings = load(ITEM_USE_FILE);

		Config.ITEM_USE_LIST_ID = itemsUseSettings.getProperty("ItemUseListId", new int[]
		{
			725,
			726,
			727,
			728
		});
		Config.ITEM_USE_IS_COMBAT_FLAG = itemsUseSettings.getProperty("ItemUseIsCombatFlag", true);
		Config.ITEM_USE_IS_ATTACK = itemsUseSettings.getProperty("ItemUseIsAttack", true);
		Config.ITEM_USE_IS_EVENTS = itemsUseSettings.getProperty("ItemUseIsEvents", true);
		Config.MAX_ADENA = itemsUseSettings.getProperty("MaxAdena", -1);
	}

	public static final String NPCBUFFER_CONFIG_FILE = "config/npcbuffer.ini";

	// Scheme Buffer
	public static boolean NpcBuffer_VIP;
	public static int NpcBuffer_VIP_ALV;
	public static boolean NpcBuffer_EnableBuff;
	public static boolean NpcBuffer_EnableScheme;
	public static boolean NpcBuffer_EnableHeal;
	public static boolean NpcBuffer_EnableBuffs;
	public static boolean NpcBuffer_EnableResist;
	public static boolean NpcBuffer_EnableSong;
	public static boolean NpcBuffer_EnableDance;
	public static boolean NpcBuffer_EnableChant;
	public static boolean NpcBuffer_EnableOther;
	public static boolean NpcBuffer_EnableSpecial;
	public static boolean NpcBuffer_EnableCubic;
	public static boolean NpcBuffer_EnableCancel;
	public static boolean NpcBuffer_EnableBuffSet;
	public static boolean NpcBuffer_EnableBuffPK;
	public static boolean NpcBuffer_EnableFreeBuffs;
	public static boolean NpcBuffer_EnableTimeOut;
	public static int NpcBuffer_TimeOutTime;
	public static int NpcBuffer_MinLevel;
	public static int NpcBuffer_PriceCancel;
	public static int NpcBuffer_PriceHeal;
	public static int NpcBuffer_PriceBuffs;
	public static int NpcBuffer_PriceResist;
	public static int NpcBuffer_PriceSong;
	public static int NpcBuffer_PriceDance;
	public static int NpcBuffer_PriceChant;
	public static int NpcBuffer_PriceOther;
	public static int NpcBuffer_PriceSpecial;
	public static int NpcBuffer_PriceCubic;
	public static int NpcBuffer_PriceSet;
	public static int NpcBuffer_PriceScheme;
	public static int NpcBuffer_MaxScheme;
	public static boolean NpcBuffer_EnablePremiumBuffs;
	public static boolean SCHEME_ALLOW_FLAG;
	public static List<int[]> NpcBuffer_BuffSetMage = new ArrayList<int[]>();
	public static List<int[]> NpcBuffer_BuffSetFighter = new ArrayList<int[]>();
	public static List<int[]> NpcBuffer_BuffSetDagger = new ArrayList<int[]>();
	public static List<int[]> NpcBuffer_BuffSetSupport = new ArrayList<int[]>();
	public static List<int[]> NpcBuffer_BuffSetTank = new ArrayList<int[]>();
	public static List<int[]> NpcBuffer_BuffSetArcher = new ArrayList<int[]>();

	public static void loadSchemeBuffer()
	{
		final ExProperties npcbuffer = load(NPCBUFFER_CONFIG_FILE);

		Config.NpcBuffer_VIP = npcbuffer.getProperty("EnableVIP", false);
		Config.NpcBuffer_VIP_ALV = npcbuffer.getProperty("VipAccesLevel", 1);
		Config.NpcBuffer_EnableBuff = npcbuffer.getProperty("EnableBuffSection", true);
		Config.NpcBuffer_EnableScheme = npcbuffer.getProperty("EnableScheme", true);
		Config.NpcBuffer_EnableHeal = npcbuffer.getProperty("EnableHeal", true);
		Config.NpcBuffer_EnableBuffs = npcbuffer.getProperty("EnableBuffs", true);
		Config.NpcBuffer_EnableResist = npcbuffer.getProperty("EnableResist", true);
		Config.NpcBuffer_EnableSong = npcbuffer.getProperty("EnableSongs", true);
		Config.NpcBuffer_EnableDance = npcbuffer.getProperty("EnableDances", true);
		Config.NpcBuffer_EnableChant = npcbuffer.getProperty("EnableChants", true);
		Config.NpcBuffer_EnableOther = npcbuffer.getProperty("EnableOther", true);
		Config.NpcBuffer_EnableSpecial = npcbuffer.getProperty("EnableSpecial", true);
		Config.NpcBuffer_EnableCubic = npcbuffer.getProperty("EnableCubic", false);
		Config.NpcBuffer_EnableCancel = npcbuffer.getProperty("EnableRemoveBuffs", true);
		Config.NpcBuffer_EnableBuffSet = npcbuffer.getProperty("EnableBuffSet", true);
		Config.NpcBuffer_EnableBuffPK = npcbuffer.getProperty("EnableBuffForPK", false);
		Config.NpcBuffer_EnableFreeBuffs = npcbuffer.getProperty("EnableFreeBuffs", true);
		Config.NpcBuffer_EnableTimeOut = npcbuffer.getProperty("EnableTimeOut", true);
		Config.SCHEME_ALLOW_FLAG = npcbuffer.getProperty("EnableBuffforFlag", false);
		Config.NpcBuffer_TimeOutTime = npcbuffer.getProperty("TimeoutTime", 10);
		Config.NpcBuffer_MinLevel = npcbuffer.getProperty("MinimumLevel", 20);
		Config.NpcBuffer_PriceCancel = npcbuffer.getProperty("RemoveBuffsPrice", 100000);
		Config.NpcBuffer_PriceHeal = npcbuffer.getProperty("HealPrice", 100000);
		Config.NpcBuffer_PriceBuffs = npcbuffer.getProperty("BuffsPrice", 100000);
		Config.NpcBuffer_PriceResist = npcbuffer.getProperty("ResistPrice", 100000);
		Config.NpcBuffer_PriceSong = npcbuffer.getProperty("SongPrice", 100000);
		Config.NpcBuffer_PriceDance = npcbuffer.getProperty("DancePrice", 100000);
		Config.NpcBuffer_PriceChant = npcbuffer.getProperty("ChantsPrice", 100000);
		Config.NpcBuffer_PriceOther = npcbuffer.getProperty("OtherPrice", 100000);
		Config.NpcBuffer_PriceSpecial = npcbuffer.getProperty("SpecialPrice", 100000);
		Config.NpcBuffer_PriceCubic = npcbuffer.getProperty("CubicPrice", 100000);
		Config.NpcBuffer_PriceSet = npcbuffer.getProperty("SetPrice", 100000);
		Config.NpcBuffer_PriceScheme = npcbuffer.getProperty("SchemePrice", 100000);
		Config.NpcBuffer_MaxScheme = npcbuffer.getProperty("MaxScheme", 4);
		Config.NpcBuffer_EnablePremiumBuffs = npcbuffer.getProperty("EnablePremiumBuffs", false);

		String[] parts;
		String[] skills = npcbuffer.getProperty("BuffSetMage", "192,1").split(";");
		for (String sk : skills)
		{
			parts = sk.split(",");
			Config.NpcBuffer_BuffSetMage.add(new int[]
			{
				Integer.parseInt(parts[0]),
				Integer.parseInt(parts[1])
			});
		}

		skills = npcbuffer.getProperty("BuffSetFighter", "192,1").split(";");
		for (String sk : skills)
		{
			parts = sk.split(",");
			Config.NpcBuffer_BuffSetFighter.add(new int[]
			{
				Integer.parseInt(parts[0]),
				Integer.parseInt(parts[1])
			});
		}

		skills = npcbuffer.getProperty("BuffSetDagger", "192,1").split(";");
		for (String sk : skills)
		{
			parts = sk.split(",");
			Config.NpcBuffer_BuffSetDagger.add(new int[]
			{
				Integer.parseInt(parts[0]),
				Integer.parseInt(parts[1])
			});
		}

		skills = npcbuffer.getProperty("BuffSetSupport", "192,1").split(";");
		for (String sk : skills)
		{
			parts = sk.split(",");
			Config.NpcBuffer_BuffSetSupport.add(new int[]
			{
				Integer.parseInt(parts[0]),
				Integer.parseInt(parts[1])
			});
		}

		skills = npcbuffer.getProperty("BuffSetTank", "192,1").split(";");
		for (String sk : skills)
		{
			parts = sk.split(",");
			Config.NpcBuffer_BuffSetTank.add(new int[]
			{
				Integer.parseInt(parts[0]),
				Integer.parseInt(parts[1])
			});
		}

		skills = npcbuffer.getProperty("BuffSetArcher", "192,1").split(";");
		for (String sk : skills)
		{
			parts = sk.split(",");
			Config.NpcBuffer_BuffSetArcher.add(new int[]
			{
				Integer.parseInt(parts[0]),
				Integer.parseInt(parts[1])
			});
		}
	}

	public static final String EVENT_FIGHT_CLUB_FILE = "config/events/FightClub.ini";

	/** Ð�Ð°Ñ�Ñ‚Ñ€Ð¾Ð¹ÐºÐ¸ Ð´Ð»Ñ� ÐµÐ²ÐµÐ½Ñ‚Ð° Ð¤Ð°Ð¹Ñ‚ ÐšÐ»ÑƒÐ± */
	public static boolean FIGHT_CLUB_ENABLED;
	public static int MINIMUM_LEVEL_TO_PARRICIPATION;
	public static int MAXIMUM_LEVEL_TO_PARRICIPATION;
	public static int MAXIMUM_LEVEL_DIFFERENCE;
	public static String[] ALLOWED_RATE_ITEMS;
	public static int PLAYERS_PER_PAGE;
	public static int ARENA_TELEPORT_DELAY;
	public static boolean CANCEL_BUFF_BEFORE_FIGHT;
	public static boolean UNSUMMON_PETS;
	public static boolean UNSUMMON_SUMMONS;
	public static boolean REMOVE_CLAN_SKILLS;
	public static boolean REMOVE_HERO_SKILLS;
	public static int TIME_TO_PREPARATION;
	public static int FIGHT_TIME;
	public static boolean ALLOW_DRAW;
	public static int TIME_TELEPORT_BACK;
	public static boolean FIGHT_CLUB_ANNOUNCE_RATE;
	public static boolean FIGHT_CLUB_ANNOUNCE_RATE_TO_SCREEN;
	public static boolean FIGHT_CLUB_ANNOUNCE_START_TO_SCREEN;
	public static boolean FIGHT_CLUB_ANNOUNCE_TOP_KILLER;
	public static boolean FIGHT_CLUB_SUMMON_LOSE_BUFFS_ON_DEATH;

	public static void loadFightClubSettings()
	{
		final ExProperties eventFightClubSettings = load(EVENT_FIGHT_CLUB_FILE);

		Config.FIGHT_CLUB_ENABLED = eventFightClubSettings.getProperty("FightClubEnabled", false);
		Config.MINIMUM_LEVEL_TO_PARRICIPATION = eventFightClubSettings.getProperty("MinimumLevel", 1);
		Config.MAXIMUM_LEVEL_TO_PARRICIPATION = eventFightClubSettings.getProperty("MaximumLevel", 85);
		Config.MAXIMUM_LEVEL_DIFFERENCE = eventFightClubSettings.getProperty("MaximumLevelDifference", 10);
		Config.ALLOWED_RATE_ITEMS = eventFightClubSettings.getProperty("AllowedItems", "").trim().replaceAll(" ", "").split(",");
		Config.PLAYERS_PER_PAGE = eventFightClubSettings.getProperty("RatesOnPage", 10);
		Config.ARENA_TELEPORT_DELAY = eventFightClubSettings.getProperty("ArenaTeleportDelay", 5);
		Config.CANCEL_BUFF_BEFORE_FIGHT = eventFightClubSettings.getProperty("CancelBuffs", true);
		Config.UNSUMMON_PETS = eventFightClubSettings.getProperty("UnsummonPets", true);
		Config.UNSUMMON_SUMMONS = eventFightClubSettings.getProperty("UnsummonSummons", true);
		Config.REMOVE_CLAN_SKILLS = eventFightClubSettings.getProperty("RemoveClanSkills", false);
		Config.REMOVE_HERO_SKILLS = eventFightClubSettings.getProperty("RemoveHeroSkills", false);
		Config.TIME_TO_PREPARATION = eventFightClubSettings.getProperty("TimeToPreparation", 10);
		Config.FIGHT_TIME = eventFightClubSettings.getProperty("TimeToDraw", 300);
		Config.ALLOW_DRAW = eventFightClubSettings.getProperty("AllowDraw", true);
		Config.TIME_TELEPORT_BACK = eventFightClubSettings.getProperty("TimeToBack", 10);
		Config.FIGHT_CLUB_ANNOUNCE_RATE = eventFightClubSettings.getProperty("AnnounceRate", false);
		Config.FIGHT_CLUB_ANNOUNCE_RATE_TO_SCREEN = eventFightClubSettings.getProperty("AnnounceRateToAllScreen", false);
		Config.FIGHT_CLUB_ANNOUNCE_START_TO_SCREEN = eventFightClubSettings.getProperty("AnnounceStartBatleToAllScreen", false);
		Config.FIGHT_CLUB_ANNOUNCE_TOP_KILLER = eventFightClubSettings.getProperty("FightClubAnnounceTopKiller", false);
		Config.FIGHT_CLUB_SUMMON_LOSE_BUFFS_ON_DEATH = eventFightClubSettings.getProperty("SummonLoseBuffsOnDeath", true);
	}

	public static final String RATES_FILE = "config/rates.ini";
	/** Rate control */
	public static double RATE_XP;
	public static double RATE_SP;
	public static double RATE_QUESTS_REWARD;
	public static double RATE_QUESTS_DROP;
	public static double RATE_CLAN_REP_SCORE;
	public static int RATE_CLAN_REP_SCORE_MAX_AFFECTED;
	public static double RATE_DROP_ADENA;
	public static double RATE_DROP_CHAMPION;
	public static double RATE_CHAMPION_DROP_ADENA;
	public static double RATE_DROP_SPOIL_CHAMPION;
	public static double RATE_DROP_ITEMS;
	public static double RATE_CHANCE_GROUP_DROP_ITEMS;
	public static double RATE_CHANCE_DROP_ITEMS;
	public static double RATE_CHANCE_DROP_HERBS;
	public static double RATE_CHANCE_SPOIL;
	public static double RATE_CHANCE_SPOIL_WEAPON_ARMOR_ACCESSORY;
	public static double RATE_CHANCE_DROP_WEAPON_ARMOR_ACCESSORY;
	public static double RATE_CHANCE_DROP_EPOLET;
	public static boolean NO_RATE_ENCHANT_SCROLL;
	public static double RATE_ENCHANT_SCROLL;
	public static boolean CHAMPION_DROP_ONLY_ADENA;
	public static boolean NO_RATE_HERBS;
	public static double RATE_DROP_HERBS;
	public static boolean NO_RATE_ATT;
	public static double RATE_DROP_ATT;
	public static boolean NO_RATE_LIFE_STONE;
	public static boolean NO_RATE_FORGOTTEN_SCROLL;
	public static double RATE_DROP_LIFE_STONE;
	public static boolean NO_RATE_KEY_MATERIAL;
	public static double RATE_DROP_KEY_MATERIAL;
	public static boolean NO_RATE_RECIPES;
	public static double RATE_DROP_RECIPES;
	public static double RATE_DROP_COMMON_ITEMS;
	public static boolean NO_RATE_RAIDBOSS;
	public static double RATE_DROP_RAIDBOSS;
	public static double RATE_DROP_SPOIL;
	public static int[] NO_RATE_ITEMS;
	public static boolean NO_RATE_SIEGE_GUARD;
	public static double RATE_DROP_SIEGE_GUARD;
	public static double RATE_MANOR;
	public static double RATE_FISH_DROP_COUNT;
	public static boolean RATE_PARTY_MIN;
	public static double RATE_HELLBOUND_CONFIDENCE;
	public static boolean NO_RATE_EQUIPMENT;

	public static int RATE_MOB_SPAWN;
	public static int RATE_MOB_SPAWN_MIN_LEVEL;
	public static int RATE_MOB_SPAWN_MAX_LEVEL;

	public static void loadRatesConfig()
	{
		final ExProperties ratesSettings = load(RATES_FILE);

		Config.RATE_XP = ratesSettings.getProperty("RateXp", 1.);
		Config.RATE_SP = ratesSettings.getProperty("RateSp", 1.);
		Config.RATE_QUESTS_REWARD = ratesSettings.getProperty("RateQuestsReward", 1.);
		Config.RATE_QUESTS_DROP = ratesSettings.getProperty("RateQuestsDrop", 1.);
		Config.RATE_DROP_CHAMPION = ratesSettings.getProperty("RateDropChampion", 1.);
		Config.RATE_CLAN_REP_SCORE = ratesSettings.getProperty("RateClanRepScore", 1.);
		Config.RATE_CLAN_REP_SCORE_MAX_AFFECTED = ratesSettings.getProperty("RateClanRepScoreMaxAffected", 2);
		Config.RATE_DROP_ADENA = ratesSettings.getProperty("RateDropAdena", 1.);
		Config.RATE_CHAMPION_DROP_ADENA = ratesSettings.getProperty("RateChampionDropAdena", 1.);
		Config.RATE_DROP_SPOIL_CHAMPION = ratesSettings.getProperty("RateSpoilChampion", 1.);
		Config.RATE_DROP_ITEMS = ratesSettings.getProperty("RateDropItems", 1.);
		Config.RATE_CHANCE_GROUP_DROP_ITEMS = ratesSettings.getProperty("RateChanceGroupDropItems", 1.);
		Config.RATE_CHANCE_DROP_ITEMS = ratesSettings.getProperty("RateChanceDropItems", 1.);
		Config.RATE_CHANCE_DROP_HERBS = ratesSettings.getProperty("RateChanceDropHerbs", 1.);
		Config.RATE_CHANCE_SPOIL = ratesSettings.getProperty("RateChanceSpoil", 1.);
		Config.RATE_CHANCE_SPOIL_WEAPON_ARMOR_ACCESSORY = ratesSettings.getProperty("RateChanceSpoilWAA", 1.);
		Config.RATE_CHANCE_DROP_WEAPON_ARMOR_ACCESSORY = ratesSettings.getProperty("RateChanceDropWAA", 1.);
		Config.RATE_CHANCE_DROP_EPOLET = ratesSettings.getProperty("RateChanceDropEpolets", 1.);
		Config.NO_RATE_ENCHANT_SCROLL = ratesSettings.getProperty("NoRateEnchantScroll", true);
		Config.CHAMPION_DROP_ONLY_ADENA = ratesSettings.getProperty("ChampionDropOnlyAdena", false);
		Config.RATE_ENCHANT_SCROLL = ratesSettings.getProperty("RateDropEnchantScroll", 1.);
		Config.NO_RATE_HERBS = ratesSettings.getProperty("NoRateHerbs", true);
		Config.RATE_DROP_HERBS = ratesSettings.getProperty("RateDropHerbs", 1.);
		Config.NO_RATE_ATT = ratesSettings.getProperty("NoRateAtt", true);
		Config.RATE_DROP_ATT = ratesSettings.getProperty("RateDropAtt", 1.);
		Config.NO_RATE_LIFE_STONE = ratesSettings.getProperty("NoRateLifeStone", true);
		Config.NO_RATE_FORGOTTEN_SCROLL = ratesSettings.getProperty("NoRateForgottenScroll", true);
		Config.RATE_DROP_LIFE_STONE = ratesSettings.getProperty("RateDropLifeStone", 1.);
		Config.NO_RATE_KEY_MATERIAL = ratesSettings.getProperty("NoRateKeyMaterial", true);
		Config.RATE_DROP_KEY_MATERIAL = ratesSettings.getProperty("RateDropKeyMaterial", 1.);
		Config.NO_RATE_RECIPES = ratesSettings.getProperty("NoRateRecipes", true);
		Config.RATE_DROP_RECIPES = ratesSettings.getProperty("RateDropRecipes", 1.);
		Config.RATE_DROP_COMMON_ITEMS = ratesSettings.getProperty("RateDropCommonItems", 1.);
		Config.NO_RATE_RAIDBOSS = ratesSettings.getProperty("NoRateRaidBoss", false);
		Config.RATE_DROP_RAIDBOSS = ratesSettings.getProperty("RateRaidBoss", 1.);
		Config.RATE_DROP_SPOIL = ratesSettings.getProperty("RateDropSpoil", 1.);
		Config.NO_RATE_ITEMS = ratesSettings.getProperty("NoRateItemIds", new int[]
		{
			6660,
			6662,
			6661,
			6659,
			6656,
			6658,
			8191,
			6657,
			10170,
			10314,
			16025,
			16026
		});
		Config.NO_RATE_EQUIPMENT = ratesSettings.getProperty("NoRateEquipment", true);
		Config.NO_RATE_SIEGE_GUARD = ratesSettings.getProperty("NoRateSiegeGuard", false);
		Config.RATE_DROP_SIEGE_GUARD = ratesSettings.getProperty("RateSiegeGuard", 1.);
		Config.RATE_MANOR = ratesSettings.getProperty("RateManor", 1.);
		Config.RATE_FISH_DROP_COUNT = ratesSettings.getProperty("RateFishDropCount", 1.);
		Config.RATE_PARTY_MIN = ratesSettings.getProperty("RatePartyMin", false);
		Config.RATE_HELLBOUND_CONFIDENCE = ratesSettings.getProperty("RateHellboundConfidence", 1.);

		Config.RATE_MOB_SPAWN = ratesSettings.getProperty("RateMobSpawn", 1);
		Config.RATE_MOB_SPAWN_MIN_LEVEL = ratesSettings.getProperty("RateMobMinLevel", 1);
		Config.RATE_MOB_SPAWN_MAX_LEVEL = ratesSettings.getProperty("RateMobMaxLevel", 100);
	}

	public static final String BOSS_FILE = "config/boss.ini";
	public static double RATE_RAID_REGEN;
	public static double RATE_RAID_DEFENSE;
	public static double RATE_RAID_ATTACK;
	public static double RATE_EPIC_DEFENSE;
	public static double RATE_EPIC_ATTACK;
	public static int RAID_MAX_LEVEL_DIFF;
	public static int MUTATED_ELPY_COUNT;

	public static boolean FRINTEZZA_ALL_MEMBERS_NEED_SCROLL;

	public static void loadBossConfig()
	{
		final ExProperties bossSettings = load(BOSS_FILE);

		Config.RATE_RAID_REGEN = bossSettings.getProperty("RateRaidRegen", 1.);
		Config.RATE_RAID_DEFENSE = bossSettings.getProperty("RateRaidDefense", 1.);
		Config.RATE_RAID_ATTACK = bossSettings.getProperty("RateRaidAttack", 1.);
		Config.RATE_EPIC_DEFENSE = bossSettings.getProperty("RateEpicDefense", RATE_RAID_DEFENSE);
		Config.RATE_EPIC_ATTACK = bossSettings.getProperty("RateEpicAttack", RATE_RAID_ATTACK);
		Config.RAID_MAX_LEVEL_DIFF = bossSettings.getProperty("RaidMaxLevelDiff", 8);
		Config.MUTATED_ELPY_COUNT = bossSettings.getProperty("MutatedElpyCount", 16);

		Config.FRINTEZZA_ALL_MEMBERS_NEED_SCROLL = bossSettings.getProperty("FrintezzaAllMembersNeedScroll", true);
	}

	public static final String DONATE_CONFIG_FILE = "config/DonatorManager.ini";
	// Donate
	public static int DONATE_ID;
	public static int DONATOR_NPC_ITEM;
	public static String DONATOR_NPC_ITEM_NAME;
	public static int DONATOR_NPC_COUNT_FAME;
	public static int DONATOR_NPC_FAME;
	public static int DONATOR_NPC_COUNT_REP;
	public static int DONATOR_NPC_REP;
	public static int DONATOR_NPC_COUNT_NOBLESS;
	public static int DONATOR_NPC_COUNT_SEX;
	public static int DONATOR_NPC_COUNT_LEVEL;

	public static void loadDonateConfig()
	{
		final ExProperties donateConfig = load(DONATE_CONFIG_FILE);

		Config.DONATE_ID = donateConfig.getProperty("DonateId", 37000);
		Config.DONATOR_NPC_ITEM = donateConfig.getProperty("DonatorNPCitem", 6673);
		Config.DONATOR_NPC_ITEM_NAME = donateConfig.getProperty("DonatorNPCitemName", "Donator Coin");
		Config.DONATOR_NPC_COUNT_FAME = donateConfig.getProperty("DonateFame", 10000);
		Config.DONATOR_NPC_FAME = donateConfig.getProperty("DonateCountFame", 5);
		Config.DONATOR_NPC_COUNT_REP = donateConfig.getProperty("DonateRep", 10000);
		Config.DONATOR_NPC_REP = donateConfig.getProperty("DonateCountClanRep", 5);
		Config.DONATOR_NPC_COUNT_NOBLESS = donateConfig.getProperty("DonateCountNobless", 5);
		Config.DONATOR_NPC_COUNT_SEX = donateConfig.getProperty("DonateCountChangeSex", 5);
		Config.DONATOR_NPC_COUNT_LEVEL = donateConfig.getProperty("DonateCountMaxLevel", 5);

	}

	public static final String DONATION_STORE = "config/services/DonationStore.ini";
	public static boolean SERVICES_CHANGE_NICK_ALLOW_SYMBOL;
	public static boolean SERVICES_CHANGE_NICK_ENABLED;
	public static int SERVICES_CHANGE_NICK_PRICE;
	public static int SERVICES_CHANGE_NICK_ITEM;
	public static boolean SERVICES_CHANGE_CLAN_NAME_ENABLED;
	public static int SERVICES_CHANGE_CLAN_NAME_PRICE;
	public static int SERVICES_CHANGE_CLAN_NAME_ITEM;

	public static boolean SERVICES_BUY_RECOMMENDS_ENABLED;
	public static int SERVICES_BUY_RECOMMENDS_PRICE;
	public static int SERVICES_BUY_RECOMMENDS_ITEM;

	public static boolean SERVICES_BUY_CLAN_REPUTATION_ENABLED;
	public static int SERVICES_BUY_CLAN_REPUTATION_PRICE;
	public static int SERVICES_BUY_CLAN_REPUTATION_ITEM;
	public static int SERVICES_BUY_CLAN_REPUTATION_COUNT;

	public static boolean SERVICES_BUY_FAME_ENABLED;
	public static int SERVICES_BUY_FAME_PRICE;
	public static int SERVICES_BUY_FAME_ITEM;
	public static int SERVICES_BUY_FAME_COUNT;

	public static boolean SERVICES_NOBLESS_SELL_ENABLED;
	public static int SERVICES_NOBLESS_SELL_PRICE;
	public static int SERVICES_NOBLESS_SELL_ITEM;
	public static boolean SERVICES_AUGMENTATION_ENABLED;
	public static int SERVICES_AUGMENTATION_PRICE;
	public static int SERVICES_AUGMENTATION_ITEM;
	public static List<Integer> SERVICES_AUGMENTATION_DISABLED_LIST = new ArrayList<>();
	public static boolean SERVICES_LEVEL_UP_ENABLE;
	public static int[] SERVICES_LEVEL_UP;
	public static boolean SERVICES_DELEVEL_ENABLE;
	public static int[] SERVICES_DELEVEL;
	public static boolean SERVICES_UNBAN_ENABLED;
	public static int[] SERVICES_UNBAN_ITEM;
	public static boolean DONATE_NOBLESS_ENABLE;
	public static int DONATE_NOBLESS_SELL_ITEM;
	public static long DONATE_NOBLESS_SELL_PRICE;
	public static boolean SERVICES_CLAN_LEVEL_ENABLED;
	public static int SERVICES_CLAN_LEVEL_ITEM;
	public static int SERVICES_CLAN_LEVEL_8_PRICE;
	public static int SERVICES_CLAN_LEVEL_9_PRICE;
	public static int SERVICES_CLAN_LEVEL_10_PRICE;
	public static int SERVICES_CLAN_LEVEL_11_PRICE;

	public static boolean SERVICES_CLAN_SKILLS_ENABLED;
	public static int SERVICES_CLAN_SKILLS_ITEM;
	public static int SERVICES_CLAN_SKILLS_8_PRICE;
	public static int SERVICES_CLAN_SKILLS_9_PRICE;
	public static int SERVICES_CLAN_SKILLS_10_PRICE;
	public static int SERVICES_CLAN_SKILLS_11_PRICE;

	public static boolean SERVICES_OLF_STORE_ENABLED;
	public static int SERVICES_OLF_STORE_ITEM;
	public static int SERVICES_OLF_STORE_0_PRICE;
	public static int SERVICES_OLF_STORE_6_PRICE;
	public static int SERVICES_OLF_STORE_7_PRICE;
	public static int SERVICES_OLF_STORE_8_PRICE;
	public static int SERVICES_OLF_STORE_9_PRICE;
	public static int SERVICES_OLF_STORE_10_PRICE;

	public static boolean SERVICES_OLF_TRANSFER_ENABLED;
	public static int[] SERVICES_OLF_TRANSFER_ITEM;

	public static boolean SERVICES_SOUL_CLOAK_TRANSFER_ENABLED;
	public static int[] SERVICES_SOUL_CLOAK_TRANSFER_ITEM;

	public static boolean SERVICES_EXCHANGE_EQUIP;
	public static int SERVICES_EXCHANGE_EQUIP_ITEM;
	public static int SERVICES_EXCHANGE_EQUIP_ITEM_PRICE;
	public static int SERVICES_EXCHANGE_UPGRADE_EQUIP_ITEM;
	public static int SERVICES_EXCHANGE_UPGRADE_EQUIP_ITEM_PRICE;

	public static void loadDonationStore()
	{
		final ExProperties DonationStore = load(DONATION_STORE);

		Config.SERVICES_AUGMENTATION_ENABLED = DonationStore.getProperty("AugmentationEnabled", false);
		Config.SERVICES_AUGMENTATION_PRICE = DonationStore.getProperty("AugmentationPrice", 50);
		Config.SERVICES_AUGMENTATION_ITEM = DonationStore.getProperty("AugmentationItem", 37000);
		final String[] augs = DonationStore.getProperty("AugmentationDisabledList", "0").trim().split(",");
		for (String aug : augs)
		{
			if (!aug.isEmpty())
			{
				Config.SERVICES_AUGMENTATION_DISABLED_LIST.add(Integer.parseInt(aug.trim()));
			}
		}

		Config.SERVICES_CHANGE_NICK_ALLOW_SYMBOL = DonationStore.getProperty("NickChangeAllowSimbol", false);
		Config.SERVICES_CHANGE_NICK_ENABLED = DonationStore.getProperty("NickChangeEnabled", false);
		Config.SERVICES_CHANGE_NICK_PRICE = DonationStore.getProperty("NickChangePrice", 100);
		Config.SERVICES_CHANGE_NICK_ITEM = DonationStore.getProperty("NickChangeItem", 37000);

		Config.SERVICES_CHANGE_CLAN_NAME_ENABLED = DonationStore.getProperty("ClanNameChangeEnabled", false);
		Config.SERVICES_CHANGE_CLAN_NAME_PRICE = DonationStore.getProperty("ClanNameChangePrice", 100);
		Config.SERVICES_CHANGE_CLAN_NAME_ITEM = DonationStore.getProperty("ClanNameChangeItem", 4037);

		Config.SERVICES_LEVEL_UP_ENABLE = DonationStore.getProperty("LevelChangeEnabled", false);
		Config.SERVICES_DELEVEL_ENABLE = DonationStore.getProperty("DeLevelChangeEnabled", false);
		Config.SERVICES_LEVEL_UP = DonationStore.getProperty("LevelUp", new int[]
		{
			37000,
			1
		});
		Config.SERVICES_DELEVEL = DonationStore.getProperty("LevelDown", new int[]
		{
			37000,
			1
		});

		Config.SERVICES_UNBAN_ENABLED = DonationStore.getProperty("UnbanService", true);
		Config.SERVICES_UNBAN_ITEM = DonationStore.getProperty("UnbanItem", new int[]
		{
			37000,
			150
		});

		Config.SERVICES_BUY_RECOMMENDS_ENABLED = DonationStore.getProperty("BuyRecommendsEnabled", false);
		Config.SERVICES_BUY_RECOMMENDS_PRICE = DonationStore.getProperty("BuyRecommendsPrice", 50);
		Config.SERVICES_BUY_RECOMMENDS_ITEM = DonationStore.getProperty("BuyRecommendsItem", 37000);

		Config.SERVICES_BUY_CLAN_REPUTATION_ENABLED = DonationStore.getProperty("BuyClanReputationEnabled", false);
		Config.SERVICES_BUY_CLAN_REPUTATION_PRICE = DonationStore.getProperty("BuyClanReputationPrice", 100);
		Config.SERVICES_BUY_CLAN_REPUTATION_ITEM = DonationStore.getProperty("BuyClanReputationItem", 37000);
		Config.SERVICES_BUY_CLAN_REPUTATION_COUNT = DonationStore.getProperty("BuyClanReputationCount", 40000);

		Config.SERVICES_BUY_FAME_ENABLED = DonationStore.getProperty("BuyFameEnabled", false);
		Config.SERVICES_BUY_FAME_PRICE = DonationStore.getProperty("BuyFamePrice", 100);
		Config.SERVICES_BUY_FAME_ITEM = DonationStore.getProperty("BuyFameItem", 37000);
		Config.SERVICES_BUY_FAME_COUNT = DonationStore.getProperty("BuyFameCount", 37000);

		Config.SERVICES_NOBLESS_SELL_ENABLED = DonationStore.getProperty("NoblessSellEnabled", false);
		Config.SERVICES_NOBLESS_SELL_PRICE = DonationStore.getProperty("NoblessSellPrice", 1000);
		Config.SERVICES_NOBLESS_SELL_ITEM = DonationStore.getProperty("NoblessSellItem", 4037);

		Config.DONATE_NOBLESS_ENABLE = DonationStore.getProperty("DonateNoblessEnabled", false);
		Config.DONATE_NOBLESS_SELL_ITEM = DonationStore.getProperty("DonateNoblessItemId", 37000);
		Config.DONATE_NOBLESS_SELL_PRICE = DonationStore.getProperty("DonateNoblessItemCont", 100);

		Config.SERVICES_CLAN_LEVEL_ENABLED = DonationStore.getProperty("ClanLvlService", true);
		Config.SERVICES_CLAN_LEVEL_ITEM = DonationStore.getProperty("ClanLvLItem", 37000);
		Config.SERVICES_CLAN_LEVEL_8_PRICE = DonationStore.getProperty("ClanLvl8Price", 150);
		Config.SERVICES_CLAN_LEVEL_9_PRICE = DonationStore.getProperty("ClanLvl9Price", 400);
		Config.SERVICES_CLAN_LEVEL_10_PRICE = DonationStore.getProperty("ClanLvl10Price", 650);
		Config.SERVICES_CLAN_LEVEL_11_PRICE = DonationStore.getProperty("ClanLvl11Price", 900);

		Config.SERVICES_CLAN_SKILLS_ENABLED = DonationStore.getProperty("ClanSkillsService", true);
		Config.SERVICES_CLAN_SKILLS_ITEM = DonationStore.getProperty("ClanSkillsItem", 37000);
		Config.SERVICES_CLAN_SKILLS_8_PRICE = DonationStore.getProperty("ClanSkillLvl8Price", 150);
		Config.SERVICES_CLAN_SKILLS_9_PRICE = DonationStore.getProperty("ClanSkillLvl9Price", 400);
		Config.SERVICES_CLAN_SKILLS_10_PRICE = DonationStore.getProperty("ClanSkillLvl10Price", 650);
		Config.SERVICES_CLAN_SKILLS_11_PRICE = DonationStore.getProperty("ClanSkillLvl11Price", 900);

		Config.SERVICES_OLF_STORE_ENABLED = DonationStore.getProperty("OlfStoreService", true);
		Config.SERVICES_OLF_STORE_ITEM = DonationStore.getProperty("OlfStoreItem", 37000);
		Config.SERVICES_OLF_STORE_0_PRICE = DonationStore.getProperty("OlfStore0", 100);
		Config.SERVICES_OLF_STORE_6_PRICE = DonationStore.getProperty("OlfStore6", 200);
		Config.SERVICES_OLF_STORE_7_PRICE = DonationStore.getProperty("OlfStore7", 275);
		Config.SERVICES_OLF_STORE_8_PRICE = DonationStore.getProperty("OlfStore8", 350);
		Config.SERVICES_OLF_STORE_9_PRICE = DonationStore.getProperty("OlfStore9", 425);
		Config.SERVICES_OLF_STORE_10_PRICE = DonationStore.getProperty("OlfStore10", 500);

		Config.SERVICES_OLF_TRANSFER_ENABLED = DonationStore.getProperty("OlfTransfer", true);
		Config.SERVICES_OLF_TRANSFER_ITEM = DonationStore.getProperty("OlfTransferItem", new int[]
		{
			10639,
			100
		});

		Config.SERVICES_SOUL_CLOAK_TRANSFER_ENABLED = DonationStore.getProperty("SCTransfer", true);
		Config.SERVICES_SOUL_CLOAK_TRANSFER_ITEM = DonationStore.getProperty("SCTransferItem", new int[]
		{
			37000,
			50
		});

		Config.SERVICES_EXCHANGE_EQUIP = DonationStore.getProperty("ExchangeEquipService", true);
		Config.SERVICES_EXCHANGE_EQUIP_ITEM = DonationStore.getProperty("ExchangeEquipItem", 37000);
		Config.SERVICES_EXCHANGE_EQUIP_ITEM_PRICE = DonationStore.getProperty("ExchangeEquipPrice", 50);
		Config.SERVICES_EXCHANGE_UPGRADE_EQUIP_ITEM = DonationStore.getProperty("ExchangeUpgradeEquipItem", 37000);
		Config.SERVICES_EXCHANGE_UPGRADE_EQUIP_ITEM_PRICE = DonationStore.getProperty("ExchangeUpgradeEquipPrice", 50);
	}

	public static final String NPC_FILE = "config/mod/npc.ini";
	/** random animation interval */
	public static int MIN_NPC_ANIMATION;
	public static int MAX_NPC_ANIMATION;
	public static boolean SERVER_SIDE_NPC_NAME;
	public static boolean SERVER_SIDE_NPC_TITLE;
	public static boolean SERVER_SIDE_NPC_TITLE_ETC;

	public static void loadNpcConfig()
	{
		final ExProperties npcSettings = load(NPC_FILE);

		Config.MIN_NPC_ANIMATION = npcSettings.getProperty("MinNPCAnimation", 5);
		Config.MAX_NPC_ANIMATION = npcSettings.getProperty("MaxNPCAnimation", 90);
		Config.SERVER_SIDE_NPC_NAME = npcSettings.getProperty("ServerSideNpcName", false);
		Config.SERVER_SIDE_NPC_TITLE = npcSettings.getProperty("ServerSideNpcTitle", false);
		Config.SERVER_SIDE_NPC_TITLE_ETC = npcSettings.getProperty("ServerSideNpcTitleEtc", false);
	}

	public static final String OTHER_CONFIG_FILE = "config/mod/other.ini";
	public static int MULTISELL_SIZE;
	/** Deep Blue Mobs' Drop Rules Enabled */
	public static String VOTE_REWARD_MSG;
	public static boolean DEEPBLUE_DROP_RULES;
	public static int DEEPBLUE_DROP_MAXDIFF;
	public static int DEEPBLUE_DROP_RAID_MAXDIFF;
	public static boolean UNSTUCK_SKILL;
	/** Pets */
	public static int SWIMING_SPEED;
	public static boolean SELL_ALL_ITEMS_FREE;

	/** Inventory slots limits */
	public static int INVENTORY_MAXIMUM_NO_DWARF;
	public static int INVENTORY_MAXIMUM_DWARF;
	public static int INVENTORY_MAXIMUM_GM;
	public static int QUEST_INVENTORY_MAXIMUM;
	/** Warehouse slots limits */
	public static int WAREHOUSE_SLOTS_NO_DWARF;
	public static int WAREHOUSE_SLOTS_DWARF;
	public static int WAREHOUSE_SLOTS_CLAN;
	public static int FREIGHT_SLOTS;

	/** Chance that an item will succesfully be enchanted */
	public static int ENCHANT_CHANCE_WEAPON;
	public static int ENCHANT_CHANCE_ARMOR;
	public static int ENCHANT_CHANCE_ACCESSORY;
	public static int ENCHANT_CHANCE_CRYSTAL_WEAPON;
	public static int ENCHANT_CHANCE_CRYSTAL_ARMOR;
	public static int ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF;
	public static int ENCHANT_CHANCE_CRYSTAL_ACCESSORY;
	public static int ENCHANT_CHANCE_WEAPON_BLESS;
	public static int ENCHANT_CHANCE_ARMOR_BLESS;
	public static int ENCHANT_CHANCE_ACCESSORY_BLESS;
	/** Enchant Config **/
	public static int SAFE_ENCHANT_COMMON;
	public static int SAFE_ENCHANT_FULL_BODY;
	public static int SAFE_ENCHANT_LVL;

	public static int ENCHANT_MAX;
	public static int ENCHANT_MAX_DIVINE_SCROLL_WEAPON;
	public static int ENCHANT_MAX_DIVINE_SCROLL_ARMOR;
	public static int ENCHANT_MAX_DIVINE_SCROLL_JEWELRY;
	public static int ENCHANT_MAX_WEAPON;
	public static int ENCHANT_MAX_ARMOR;
	public static int ENCHANT_MAX_JEWELRY;
	public static int ENCHANT_MAX_OLF_T_SHIRT;
	public static int ENCHANT_ATTRIBUTE_STONE_CHANCE;
	public static int ENCHANT_ATTRIBUTE_CRYSTAL_CHANCE;
	public static int ARMOR_OVERENCHANT_HPBONUS_LIMIT;
	public static boolean SHOW_ENCHANT_EFFECT_RESULT;

	public static boolean USE_ALT_ENCHANT;
	public static boolean OLF_TSHIRT_CUSTOM_ENABLED;
	public static ArrayList<Integer> ENCHANT_WEAPON_FIGHT = new ArrayList<Integer>();
	public static ArrayList<Integer> ENCHANT_WEAPON_FIGHT_CRYSTAL = new ArrayList<Integer>();
	public static ArrayList<Integer> ENCHANT_WEAPON_FIGHT_BLESSED = new ArrayList<Integer>();
	public static ArrayList<Integer> ENCHANT_ARMOR = new ArrayList<Integer>();
	public static ArrayList<Integer> ENCHANT_ARMOR_CRYSTAL = new ArrayList<Integer>();
	public static ArrayList<Integer> ENCHANT_ARMOR_BLESSED = new ArrayList<Integer>();
	public static ArrayList<Integer> ENCHANT_ARMOR_JEWELRY = new ArrayList<Integer>();
	public static ArrayList<Integer> ENCHANT_ARMOR_JEWELRY_CRYSTAL = new ArrayList<Integer>();
	public static ArrayList<Integer> ENCHANT_ARMOR_JEWELRY_BLESSED = new ArrayList<Integer>();
	public static ArrayList<Integer> ENCHANT_OLF_TSHIRT_CHANCES = new ArrayList<Integer>();

	public static boolean REGEN_SIT_WAIT;
	public static boolean HTML_WELCOME;
	public static int STARTING_ADENA;
	/** Percent CP is restore on respawn */
	public static double RESPAWN_RESTORE_CP;
	/** Percent HP is restore on respawn */
	public static double RESPAWN_RESTORE_HP;
	/** Percent MP is restore on respawn */
	public static double RESPAWN_RESTORE_MP;
	/** Maximum number of available slots for pvt stores (sell/buy) - Dwarves */
	public static int MAX_PVTSTORE_SLOTS_DWARF;
	/** Maximum number of available slots for pvt stores (sell/buy) - Others */
	public static int MAX_PVTSTORE_SLOTS_OTHER;
	public static int MAX_PVTCRAFT_SLOTS;

	public static boolean SENDSTATUS_TRADE_JUST_OFFLINE;
	public static double SENDSTATUS_TRADE_MOD;
	public static boolean SHOW_OFFLINE_MODE_IN_ONLINE;
	public static boolean ANNOUNCE_MAMMON_SPAWN;

	public static int GM_NAME_COLOUR;
	public static boolean GM_HERO_AURA;
	public static int NORMAL_NAME_COLOUR;
	public static int CLANLEADER_NAME_COLOUR;
	/* Item-Mall Configs */
	public static int GAME_POINT_ITEM_ID;
	public static int STARTING_LVL;
	public static long MAX_PLAYER_CONTRIBUTION;
	// Captcha
	public static boolean CAPTCHA_ALLOW;
	public static long CAPTCHA_ANSWER_SECONDS;
	public static long CAPTCHA_JAIL_SECONDS;
	public static int CAPTCHA_COUNT;
	public static String[] CAPTCHA_PUNISHMENT;
	// Log items
	public static boolean ENABLE_PLAYER_ITEM_LOGS;
	public static boolean PLAYER_ITEM_LOGS_SAVED_IN_DB;
	public static long PLAYER_ITEM_LOGS_MAX_TIME;

	public static boolean DEBUFF_PROTECTION_SYSTEM;

	public static int BUFF_RETURN_OLYMPIAD_TIME;
	public static int BUFF_RETURN_AUTO_EVENTS_TIME;
	public static int BUFF_RETURN_NORMAL_LOCATIONS_TIME;
	public static boolean ENABLE_SPECIAL_TUTORIAL;
	public static boolean ENABLE_EMOTIONS;
	public static boolean ALLOW_SKILL_ENCHANTING_OUTSIDE_PEACE_ZONE;
	// Retail macro use bug
	public static boolean ALLOW_MACROS_REUSE_BUG;
	public static boolean ALLOW_MACROS_ENCHANT_BUG;

	public static void loadOtherConfig()
	{
		final ExProperties otherSettings = load(OTHER_CONFIG_FILE);

		Config.VOTE_REWARD_MSG = otherSettings.getProperty("VoteMsg", "");
		Config.DEEPBLUE_DROP_RULES = otherSettings.getProperty("UseDeepBlueDropRules", true);
		Config.DEEPBLUE_DROP_MAXDIFF = otherSettings.getProperty("DeepBlueDropMaxDiff", 8);
		Config.DEEPBLUE_DROP_RAID_MAXDIFF = otherSettings.getProperty("DeepBlueDropRaidMaxDiff", 2);

		Config.SWIMING_SPEED = otherSettings.getProperty("SwimingSpeedTemplate", 50);
		/* All item price 1 adena */
		Config.SELL_ALL_ITEMS_FREE = otherSettings.getProperty("SellAllItemsFree", false);
		/* Inventory slots limits */
		Config.INVENTORY_MAXIMUM_NO_DWARF = otherSettings.getProperty("MaximumSlotsForNoDwarf", 80);
		Config.INVENTORY_MAXIMUM_DWARF = otherSettings.getProperty("MaximumSlotsForDwarf", 100);
		Config.INVENTORY_MAXIMUM_GM = otherSettings.getProperty("MaximumSlotsForGMPlayer", 250);
		Config.QUEST_INVENTORY_MAXIMUM = otherSettings.getProperty("MaximumSlotsForQuests", 100);

		Config.MULTISELL_SIZE = otherSettings.getProperty("MultisellPageSize", 10);

		/* Warehouse slots limits */
		Config.WAREHOUSE_SLOTS_NO_DWARF = otherSettings.getProperty("BaseWarehouseSlotsForNoDwarf", 100);
		Config.WAREHOUSE_SLOTS_DWARF = otherSettings.getProperty("BaseWarehouseSlotsForDwarf", 120);
		Config.WAREHOUSE_SLOTS_CLAN = otherSettings.getProperty("MaximumWarehouseSlotsForClan", 200);
		Config.FREIGHT_SLOTS = otherSettings.getProperty("MaximumFreightSlots", 10);

		/* chance to enchant an item over safe level */
		Config.ENCHANT_CHANCE_WEAPON = otherSettings.getProperty("EnchantChance", 66);
		Config.ENCHANT_CHANCE_ARMOR = otherSettings.getProperty("EnchantChanceArmor", ENCHANT_CHANCE_WEAPON);
		Config.ENCHANT_CHANCE_ACCESSORY = otherSettings.getProperty("EnchantChanceAccessory", ENCHANT_CHANCE_ARMOR);
		Config.ENCHANT_CHANCE_CRYSTAL_WEAPON = otherSettings.getProperty("EnchantChanceCrystal", 66);
		Config.ENCHANT_CHANCE_CRYSTAL_ARMOR = otherSettings.getProperty("EnchantChanceCrystalArmor", ENCHANT_CHANCE_CRYSTAL_WEAPON);
		Config.ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF = otherSettings.getProperty("EnchantChanceCrystalArmorOlf", 66);
		Config.ENCHANT_CHANCE_CRYSTAL_ACCESSORY = otherSettings.getProperty("EnchantChanceCrystalAccessory", ENCHANT_CHANCE_CRYSTAL_ARMOR);
		Config.SAFE_ENCHANT_COMMON = otherSettings.getProperty("SafeEnchantCommon", 3);
		Config.SAFE_ENCHANT_FULL_BODY = otherSettings.getProperty("SafeEnchantFullBody", 4);
		Config.ENCHANT_MAX = otherSettings.getProperty("EnchantMax", 20);
		Config.SAFE_ENCHANT_LVL = otherSettings.getProperty("SafeEnchant", 0);
		Config.ARMOR_OVERENCHANT_HPBONUS_LIMIT = otherSettings.getProperty("ArmorOverEnchantHPBonusLimit", 10) - 3;
		Config.SHOW_ENCHANT_EFFECT_RESULT = otherSettings.getProperty("ShowEnchantEffectResult", false);

		Config.ENCHANT_CHANCE_WEAPON_BLESS = otherSettings.getProperty("EnchantChanceBless", 66);
		Config.ENCHANT_CHANCE_ARMOR_BLESS = otherSettings.getProperty("EnchantChanceArmorBless", ENCHANT_CHANCE_WEAPON);
		Config.ENCHANT_CHANCE_ACCESSORY_BLESS = otherSettings.getProperty("EnchantChanceAccessoryBless", ENCHANT_CHANCE_ARMOR);
		Config.USE_ALT_ENCHANT = Boolean.parseBoolean(otherSettings.getProperty("UseAltEnchant", "False"));
		Config.OLF_TSHIRT_CUSTOM_ENABLED = Boolean.parseBoolean(otherSettings.getProperty("EnableOlfTShirtEnchant", "False"));
		for (String prop : otherSettings.getProperty("EnchantWeaponFighter", "100,100,100,70,70,70,70,70,70,70,70,70,70,70,70,35,35,35,35,35").split(","))
		{
			Config.ENCHANT_WEAPON_FIGHT.add(Integer.parseInt(prop));
		}
		for (String prop : otherSettings.getProperty("EnchantWeaponFighterCrystal", "100,100,100,70,70,70,70,70,70,70,70,70,70,70,70,35,35,35,35,35").split(","))
		{
			Config.ENCHANT_WEAPON_FIGHT_BLESSED.add(Integer.parseInt(prop));
		}
		for (String prop : otherSettings.getProperty("EnchantWeaponFighterBlessed", "100,100,100,70,70,70,70,70,70,70,70,70,70,70,70,35,35,35,35,35").split(","))
		{
			Config.ENCHANT_WEAPON_FIGHT_CRYSTAL.add(Integer.parseInt(prop));
		}
		for (String prop : otherSettings.getProperty("EnchantArmor", "100,100,100,66,33,25,20,16,14,12,11,10,9,8,8,7,7,6,6,6").split(","))
		{
			Config.ENCHANT_ARMOR.add(Integer.parseInt(prop));
		}
		for (String prop : otherSettings.getProperty("EnchantArmorCrystal", "100,100,100,66,33,25,20,16,14,12,11,10,9,8,8,7,7,6,6,6").split(","))
		{
			Config.ENCHANT_ARMOR_CRYSTAL.add(Integer.parseInt(prop));
		}
		for (String prop : otherSettings.getProperty("EnchantArmorBlessed", "100,100,100,66,33,25,20,16,14,12,11,10,9,8,8,7,7,6,6,6").split(","))
		{
			Config.ENCHANT_ARMOR_BLESSED.add(Integer.parseInt(prop));
		}
		for (String prop : otherSettings.getProperty("EnchantJewelry", "100,100,100,66,33,25,20,16,14,12,11,10,9,8,8,7,7,6,6,6").split(","))
		{
			Config.ENCHANT_ARMOR_JEWELRY.add(Integer.parseInt(prop));
		}
		for (String prop : otherSettings.getProperty("EnchantJewelryCrystal", "100,100,100,66,33,25,20,16,14,12,11,10,9,8,8,7,7,6,6,6").split(","))
		{
			Config.ENCHANT_ARMOR_JEWELRY_CRYSTAL.add(Integer.parseInt(prop));
		}
		for (String prop : otherSettings.getProperty("EnchantJewelryBlessed", "100,100,100,66,33,25,20,16,14,12,11,10,9,8,8,7,7,6,6,6").split(","))
		{
			Config.ENCHANT_ARMOR_JEWELRY_BLESSED.add(Integer.parseInt(prop));
		}

		for (String prop : otherSettings.getProperty("EnchantOlfTShirtChances", "100,100,100,50,40,30,20,10,10").split(","))
		{
			Config.ENCHANT_OLF_TSHIRT_CHANCES.add(Integer.parseInt(prop));
		}

		Config.ENCHANT_ATTRIBUTE_STONE_CHANCE = otherSettings.getProperty("EnchantAttributeChance", 50);
		Config.ENCHANT_ATTRIBUTE_CRYSTAL_CHANCE = otherSettings.getProperty("EnchantAttributeCrystalChance", 30);

		Config.REGEN_SIT_WAIT = otherSettings.getProperty("RegenSitWait", false);
		Config.HTML_WELCOME = otherSettings.getProperty("ShowHTMLWelcome", false);
		Config.STARTING_ADENA = otherSettings.getProperty("StartingAdena", 0);

		Config.UNSTUCK_SKILL = otherSettings.getProperty("UnstuckSkill", true);

		/* Amount of HP, MP, and CP is restored */
		Config.RESPAWN_RESTORE_CP = otherSettings.getProperty("RespawnRestoreCP", 0.) / 100;
		Config.RESPAWN_RESTORE_HP = otherSettings.getProperty("RespawnRestoreHP", 65.) / 100;
		Config.RESPAWN_RESTORE_MP = otherSettings.getProperty("RespawnRestoreMP", 0.) / 100;

		/* Maximum number of available slots for pvt stores */
		Config.MAX_PVTSTORE_SLOTS_DWARF = otherSettings.getProperty("MaxPvtStoreSlotsDwarf", 5);
		Config.MAX_PVTSTORE_SLOTS_OTHER = otherSettings.getProperty("MaxPvtStoreSlotsOther", 4);
		Config.MAX_PVTCRAFT_SLOTS = otherSettings.getProperty("MaxPvtManufactureSlots", 20);

		Config.SENDSTATUS_TRADE_JUST_OFFLINE = otherSettings.getProperty("SendStatusTradeJustOffline", false);
		Config.SENDSTATUS_TRADE_MOD = otherSettings.getProperty("SendStatusTradeMod", 1.);
		Config.SHOW_OFFLINE_MODE_IN_ONLINE = otherSettings.getProperty("ShowOfflineTradeInOnline", false);

		Config.ANNOUNCE_MAMMON_SPAWN = otherSettings.getProperty("AnnounceMammonSpawn", true);

		Config.GM_NAME_COLOUR = Integer.decode("0x" + otherSettings.getProperty("GMNameColour", "FFFFFF"));
		Config.GM_HERO_AURA = otherSettings.getProperty("GMHeroAura", false);
		Config.NORMAL_NAME_COLOUR = Integer.decode("0x" + otherSettings.getProperty("NormalNameColour", "FFFFFF"));
		Config.CLANLEADER_NAME_COLOUR = Integer.decode("0x" + otherSettings.getProperty("ClanleaderNameColour", "FFFFFF"));

		Config.GAME_POINT_ITEM_ID = otherSettings.getProperty("GamePointItemId", -1);
		Config.STARTING_LVL = otherSettings.getProperty("StartingLvL", 0);
		Config.MAX_PLAYER_CONTRIBUTION = otherSettings.getProperty("MaxPlayerContribution", 1000000);

		Config.ENCHANT_MAX_DIVINE_SCROLL_WEAPON = otherSettings.getProperty("EnchantMaxDivineScrollWeapon", 20);
		Config.ENCHANT_MAX_DIVINE_SCROLL_ARMOR = otherSettings.getProperty("EnchantMaxDivineScrollArmor", 20);
		Config.ENCHANT_MAX_DIVINE_SCROLL_JEWELRY = otherSettings.getProperty("EnchantMaxDivineScrollJewelry", 20);
		Config.ENCHANT_MAX_WEAPON = otherSettings.getProperty("EnchantMaxWeapon", 20);
		Config.ENCHANT_MAX_ARMOR = otherSettings.getProperty("EnchantMaxArmor", 20);
		Config.ENCHANT_MAX_JEWELRY = otherSettings.getProperty("EnchantMaxJewelry", 20);
		Config.ENCHANT_MAX_OLF_T_SHIRT = otherSettings.getProperty("EnchantMaxOlfTShirt", 10);

		// Captcha
		Config.CAPTCHA_ALLOW = otherSettings.getProperty("AllowCaptcha", false);
		Config.CAPTCHA_ANSWER_SECONDS = otherSettings.getProperty("CaptchaAnswerTime", 15L);
		Config.CAPTCHA_JAIL_SECONDS = otherSettings.getProperty("CaptchaJailTime", 1800L);
//		CAPTCHA_TIME_BETWEEN_TESTED_SECONDS = otherSettings.getProperty("CaptchaDelayBetweenCaptchas", 1800);
//		CAPTCHA_TIME_BETWEEN_REPORTS_SECONDS = otherSettings.getProperty("CaptchaReportDelay", 7200);
//		CAPTCHA_MIN_LEVEL = otherSettings.getProperty("CaptchaMinLevel", 40);
		Config.CAPTCHA_COUNT = otherSettings.getProperty("CaptchaCount", 2);
		Config.CAPTCHA_PUNISHMENT = otherSettings.getProperty("CaptchaPunishment", new String[]
		{
			"JAIL:90",
			"JAIL:350",
			"JAIL:900",
			"BAN:-100"
		});

		Config.ENABLE_PLAYER_ITEM_LOGS = otherSettings.getProperty("EnablePlayerItemLogs", false);
		Config.PLAYER_ITEM_LOGS_SAVED_IN_DB = otherSettings.getProperty("PlayerItemLogsSavedInDB", false);
		Config.PLAYER_ITEM_LOGS_MAX_TIME = otherSettings.getProperty("PlayerItemLogsMaxTime", 172800000L);

		Config.DEBUFF_PROTECTION_SYSTEM = otherSettings.getProperty("DebuffProtectionSystem", false);

		Config.BUFF_RETURN_OLYMPIAD_TIME = otherSettings.getProperty("BuffReturnOlympiadTime", -1);
		Config.BUFF_RETURN_AUTO_EVENTS_TIME = otherSettings.getProperty("BuffReturnAutoEventsTime", -1);
		Config.BUFF_RETURN_NORMAL_LOCATIONS_TIME = otherSettings.getProperty("BuffReturnNormalLocationsTime", -1);

		Config.ENABLE_SPECIAL_TUTORIAL = otherSettings.getProperty("EnableSpecialTutorial", false);

		Config.ENABLE_EMOTIONS = otherSettings.getProperty("EnableEmotions", false);

		Config.ALLOW_SKILL_ENCHANTING_OUTSIDE_PEACE_ZONE = otherSettings.getProperty("AllowSkillEnchantingOutsidePeaceZone", true);

		Config.ALLOW_MACROS_REUSE_BUG = otherSettings.getProperty("AllowMacrosReuseBug", false);
		Config.ALLOW_MACROS_ENCHANT_BUG = otherSettings.getProperty("AllowMacrosEnchantBug", false);
	}

	/** Spoil Rates */
	public static double BASE_SPOIL_RATE;
	public static double MINIMUM_SPOIL_RATE;
	public static boolean ALT_SPOIL_FORMULA;
	/** Manor Config */
	public static double MANOR_SOWING_BASIC_SUCCESS;
	public static double MANOR_SOWING_ALT_BASIC_SUCCESS;
	public static double MANOR_HARVESTING_BASIC_SUCCESS;
	public static int MANOR_DIFF_PLAYER_TARGET;
	public static double MANOR_DIFF_PLAYER_TARGET_PENALTY;
	public static int MANOR_DIFF_SEED_TARGET;
	public static double MANOR_DIFF_SEED_TARGET_PENALTY;
	/** Allow Manor system */
	public static boolean ALLOW_MANOR;

	/** Manor Refresh Starting time */
	public static int MANOR_REFRESH_TIME;

	/** Manor Refresh Min */
	public static int MANOR_REFRESH_MIN;

	/** Manor Next Period Approve Starting time */
	public static int MANOR_APPROVE_TIME;

	/** Manor Next Period Approve Min */
	public static int MANOR_APPROVE_MIN;

	/** Manor Maintenance Time */
	public static int MANOR_MAINTENANCE_PERIOD;
	public static final String SPOIL_CONFIG_FILE = "config/spoil.ini";

	public static void loadSpoilConfig()
	{
		final ExProperties spoilSettings = load(SPOIL_CONFIG_FILE);

		Config.BASE_SPOIL_RATE = spoilSettings.getProperty("BasePercentChanceOfSpoilSuccess", 78.);
		Config.MINIMUM_SPOIL_RATE = spoilSettings.getProperty("MinimumPercentChanceOfSpoilSuccess", 1.);
		Config.ALT_SPOIL_FORMULA = spoilSettings.getProperty("AltFormula", false);
		Config.MANOR_SOWING_BASIC_SUCCESS = spoilSettings.getProperty("BasePercentChanceOfSowingSuccess", 100.);
		Config.MANOR_SOWING_ALT_BASIC_SUCCESS = spoilSettings.getProperty("BasePercentChanceOfSowingAltSuccess", 10.);
		Config.MANOR_HARVESTING_BASIC_SUCCESS = spoilSettings.getProperty("BasePercentChanceOfHarvestingSuccess", 90.);
		Config.MANOR_DIFF_PLAYER_TARGET = spoilSettings.getProperty("MinDiffPlayerMob", 5);
		Config.MANOR_DIFF_PLAYER_TARGET_PENALTY = spoilSettings.getProperty("DiffPlayerMobPenalty", 5.);
		Config.MANOR_DIFF_SEED_TARGET = spoilSettings.getProperty("MinDiffSeedMob", 5);
		Config.MANOR_DIFF_SEED_TARGET_PENALTY = spoilSettings.getProperty("DiffSeedMobPenalty", 5.);
		Config.ALLOW_MANOR = spoilSettings.getProperty("AllowManor", true);
		Config.MANOR_REFRESH_TIME = spoilSettings.getProperty("AltManorRefreshTime", 20);
		Config.MANOR_REFRESH_MIN = spoilSettings.getProperty("AltManorRefreshMin", 00);
		Config.MANOR_APPROVE_TIME = spoilSettings.getProperty("AltManorApproveTime", 6);
		Config.MANOR_APPROVE_MIN = spoilSettings.getProperty("AltManorApproveMin", 00);
		Config.MANOR_MAINTENANCE_PERIOD = spoilSettings.getProperty("AltManorMaintenancePeriod", 360000);
	}

	public static final String INSTANCES_FILE = "config/Boss/instances.ini";
	public static boolean ALLOW_INSTANCES_LEVEL_MANUAL;
	public static boolean ALLOW_INSTANCES_PARTY_MANUAL;
	public static int INSTANCES_LEVEL_MIN;
	public static int INSTANCES_LEVEL_MAX;
	public static int INSTANCES_PARTY_MIN;
	public static int INSTANCES_PARTY_MAX;

	public static void loadInstancesConfig()
	{
		final ExProperties instancesSettings = load(INSTANCES_FILE);

		Config.ALLOW_INSTANCES_LEVEL_MANUAL = instancesSettings.getProperty("AllowInstancesLevelManual", false);
		Config.ALLOW_INSTANCES_PARTY_MANUAL = instancesSettings.getProperty("AllowInstancesPartyManual", false);
		Config.INSTANCES_LEVEL_MIN = instancesSettings.getProperty("InstancesLevelMin", 1);
		Config.INSTANCES_LEVEL_MAX = instancesSettings.getProperty("InstancesLevelMax", 85);
		Config.INSTANCES_PARTY_MIN = instancesSettings.getProperty("InstancesPartyMin", 2);
		Config.INSTANCES_PARTY_MAX = instancesSettings.getProperty("InstancesPartyMax", 100);
	}

	public static final String EPIC_BOSS_FILE = "config/Boss/epic.ini";

	/* Epics */
	public static int ANTHARAS_DEFAULT_SPAWN_HOURS;
	public static int ANTHARAS_RANDOM_SPAWN_HOURS;
	public static int VALAKAS_DEFAULT_SPAWN_HOURS;
	public static int VALAKAS_RANDOM_SPAWN_HOURS;
	public static int BAIUM_DEFAULT_SPAWN_HOURS;
	public static int BAIUM_RANDOM_SPAWN_HOURS;

	public static int FIXINTERVALOFBAYLORSPAWN_HOUR;
	public static int RANDOMINTERVALOFBAYLORSPAWN;
	public static int FIXINTERVALOFBELETHSPAWN_HOUR;
	public static int BELETH_CLONES_RESPAWN_TIME;
	public static int FIXINTERVALOFSAILRENSPAWN_HOUR;
	public static int RANDOMINTERVALOFSAILRENSPAWN;
	public static int MIN_PLAYERS_TO_SPAWN_BELETH;

	public static void loadEpicBossConfig()
	{
		final ExProperties epicBossSettings = load(EPIC_BOSS_FILE);

		Config.ANTHARAS_DEFAULT_SPAWN_HOURS = epicBossSettings.getProperty("AntharasDefaultSpawnHours", 168);
		Config.ANTHARAS_RANDOM_SPAWN_HOURS = epicBossSettings.getProperty("AntharasRandomSpawnHours", 8);
		Config.VALAKAS_DEFAULT_SPAWN_HOURS = epicBossSettings.getProperty("ValakasDefaultSpawnHours", 240);
		Config.VALAKAS_RANDOM_SPAWN_HOURS = epicBossSettings.getProperty("ValakasRandomSpawnHours", 24);
		Config.BAIUM_DEFAULT_SPAWN_HOURS = epicBossSettings.getProperty("BaiumDefaultSpawnHours", 120);
		Config.BAIUM_RANDOM_SPAWN_HOURS = epicBossSettings.getProperty("BaiumRandomSpawnHours", 8);

		Config.FIXINTERVALOFBAYLORSPAWN_HOUR = epicBossSettings.getProperty("BaylorDefaultSpawnHours", 24);
		Config.RANDOMINTERVALOFBAYLORSPAWN = epicBossSettings.getProperty("BaylorRandomSpawnHours", 24);
		Config.FIXINTERVALOFBELETHSPAWN_HOUR = epicBossSettings.getProperty("BelethDefaultSpawnHours", 48);
		Config.BELETH_CLONES_RESPAWN_TIME = epicBossSettings.getProperty("BelethClonesRespawnTime", 40);
		Config.MIN_PLAYERS_TO_SPAWN_BELETH = epicBossSettings.getProperty("MinPlayersToSpawnBeleth", 18);
		Config.FIXINTERVALOFSAILRENSPAWN_HOUR = epicBossSettings.getProperty("SailrenDefaultSpawnHours", 24);
		Config.RANDOMINTERVALOFSAILRENSPAWN = epicBossSettings.getProperty("SailrenRandomSpawnHours", 24);

		Config.ALLOW_DUALBOX_EPIC = epicBossSettings.getProperty("AllowDualBoxEpic", true);
	}

	public static final String FORMULAS_CONFIGURATION_FILE = "config/formulas.ini";
	public static double SKILLS_CHANCE_MOD;
	public static double SKILLS_CHANCE_MIN;
	public static double SKILLS_CHANCE_POW;
	public static double SKILLS_CHANCE_CAP;
	public static double SKILLS_MOB_CHANCE;
	public static double SKILLS_DEBUFF_MOB_CHANCE;
	public static int SKILLS_CAST_TIME_MIN;
	public static double SKILLS_DELTA_MOD_MULT;
	public static double SKILLS_ATTACKER_WEAPON_MOD;
	public static double SKILLS_M_ATK_MOD_MAX;
	public static double SKILLS_M_ATK_MOD_MIN;
	public static double SKILLS_ELEMENT_MOD_MULT;
	public static double SKILLS_ELEMENT_MOD_MAX;
	public static double SKILLS_ELEMENT_MOD_MIN;
	public static boolean SKILLS_CALC_STAT_MOD;

	public static double ALT_ABSORB_DAMAGE_MODIFIER;
	/** limits of stats **/
	public static int LIM_PATK;
	public static int LIM_MATK;
	public static int LIM_PDEF;
	public static int LIM_MDEF;
	public static int LIM_MATK_SPD;
	public static int LIM_PATK_SPD;
	public static int LIM_CRIT_DAM;
	public static int LIM_CRIT;
	public static int LIM_MCRIT;
	public static int LIM_ACCURACY;
	public static int LIM_EVASION;
	public static int LIM_MOVE;
	public static int GM_LIM_MOVE;
	public static int LIM_FAME;
	public static double ALT_NPC_PATK_MODIFIER;
	public static double ALT_NPC_MATK_MODIFIER;
	public static double ALT_NPC_MAXHP_MODIFIER;
	public static double ALT_NPC_MAXMP_MODIFIER;
	public static double ALT_NPC_PDEF_MODIFIER;
	public static double ALT_NPC_MDEF_MODIFIER;
	public static double ALT_POLE_DAMAGE_MODIFIER;

	public static void loadFormulasConfig()
	{
		final ExProperties formulasSettings = load(FORMULAS_CONFIGURATION_FILE);

		Config.SKILLS_CHANCE_MOD = formulasSettings.getProperty("SkillsChanceMod", 11.);
		Config.SKILLS_CHANCE_POW = formulasSettings.getProperty("SkillsChancePow", 0.5);
		Config.SKILLS_CHANCE_MIN = formulasSettings.getProperty("SkillsChanceMin", 5.);
		Config.SKILLS_CHANCE_CAP = formulasSettings.getProperty("SkillsChanceCap", 95.);
		Config.SKILLS_MOB_CHANCE = formulasSettings.getProperty("SkillsMobChance", 0.5);
		Config.SKILLS_DEBUFF_MOB_CHANCE = formulasSettings.getProperty("SkillsDebuffMobChance", 0.5);
		Config.SKILLS_CAST_TIME_MIN = formulasSettings.getProperty("SkillsCastTimeMin", 333);

		Config.SKILLS_DELTA_MOD_MULT = formulasSettings.getProperty("SkillsDeltaModMult", 0.06);

		Config.SKILLS_ATTACKER_WEAPON_MOD = formulasSettings.getProperty("SkillsAttackerWeaponMod", 1.95);
		Config.SKILLS_M_ATK_MOD_MAX = formulasSettings.getProperty("SkillsMAtkModMax", 1.3);
		Config.SKILLS_M_ATK_MOD_MIN = formulasSettings.getProperty("SkillsMAtkModMin", 0.7);

		Config.SKILLS_ELEMENT_MOD_MULT = formulasSettings.getProperty("SkillsElementModMult", 0.2);
		Config.SKILLS_ELEMENT_MOD_MAX = formulasSettings.getProperty("SkillsElementModMax", 1.2);
		Config.SKILLS_ELEMENT_MOD_MIN = formulasSettings.getProperty("SkillsElementModMin", 0.8);

		Config.SKILLS_CALC_STAT_MOD = formulasSettings.getProperty("SkillsCalcStatMod", true);

		Config.ALT_ABSORB_DAMAGE_MODIFIER = formulasSettings.getProperty("AbsorbDamageModifier", 1.0);

		Config.LIM_PATK = formulasSettings.getProperty("LimitPatk", 20000);
		Config.LIM_MATK = formulasSettings.getProperty("LimitMAtk", 25000);
		Config.LIM_PDEF = formulasSettings.getProperty("LimitPDef", 15000);
		Config.LIM_MDEF = formulasSettings.getProperty("LimitMDef", 15000);
		Config.LIM_PATK_SPD = formulasSettings.getProperty("LimitPatkSpd", 1500);
		Config.LIM_MATK_SPD = formulasSettings.getProperty("LimitMatkSpd", 1999);
		Config.LIM_CRIT_DAM = formulasSettings.getProperty("LimitCriticalDamage", 2000);
		Config.LIM_CRIT = formulasSettings.getProperty("LimitCritical", 500);
		Config.LIM_MCRIT = formulasSettings.getProperty("LimitMCritical", 20);
		Config.LIM_ACCURACY = formulasSettings.getProperty("LimitAccuracy", 200);
		Config.LIM_EVASION = formulasSettings.getProperty("LimitEvasion", 200);
		Config.LIM_MOVE = formulasSettings.getProperty("LimitMove", 250);
		Config.GM_LIM_MOVE = formulasSettings.getProperty("GmLimitMove", 1500);

		Config.LIM_FAME = formulasSettings.getProperty("LimitFame", 50000);

		Config.ALT_NPC_PATK_MODIFIER = formulasSettings.getProperty("NpcPAtkModifier", 1.0);
		Config.ALT_NPC_MATK_MODIFIER = formulasSettings.getProperty("NpcMAtkModifier", 1.0);
		Config.ALT_NPC_MAXHP_MODIFIER = formulasSettings.getProperty("NpcMaxHpModifier", 1.00);
		Config.ALT_NPC_MAXMP_MODIFIER = formulasSettings.getProperty("NpcMapMpModifier", 1.00);
		Config.ALT_NPC_PDEF_MODIFIER = formulasSettings.getProperty("NpcPDefModifier", 1.0);
		Config.ALT_NPC_MDEF_MODIFIER = formulasSettings.getProperty("NpcMDefModifier", 1.0);
		Config.ALT_POLE_DAMAGE_MODIFIER = formulasSettings.getProperty("PoleDamageModifier", 1.0);
	}

	public static final String DEVELOP_FILE = "config/Debug/develop.ini";
	public static boolean DEBUG;
	public static boolean ALT_DEBUG_ENABLED;
	public static boolean ALT_DEBUG_PVP_ENABLED;
	public static boolean ALT_DEBUG_PVP_DUEL_ONLY;
	public static boolean ALT_DEBUG_PVE_ENABLED;
	public static boolean DONTLOADSPAWN;
	public static boolean DONTLOADQUEST;
	public static boolean LOAD_CUSTOM_SPAWN;
	public static boolean SAVE_GM_SPAWN;

	public static void loadDevelopSettings()
	{
		final ExProperties DevelopSettings = load(DEVELOP_FILE);

		Config.DEBUG = DevelopSettings.getProperty("DebugEnabled", false);
		Config.ALT_DEBUG_ENABLED = DevelopSettings.getProperty("AltDebugEnabled", false);
		Config.ALT_DEBUG_PVP_ENABLED = DevelopSettings.getProperty("AltDebugPvPEnabled", false);
		Config.ALT_DEBUG_PVP_DUEL_ONLY = DevelopSettings.getProperty("AltDebugPvPDuelOnly", true);
		Config.ALT_DEBUG_PVE_ENABLED = DevelopSettings.getProperty("AltDebugPvEEnabled", false);

		Config.DONTLOADSPAWN = DevelopSettings.getProperty("StartWithoutSpawn", false);
		Config.DONTLOADQUEST = DevelopSettings.getProperty("StartWithoutQuest", false);
		Config.LOAD_CUSTOM_SPAWN = DevelopSettings.getProperty("LoadAddGmSpawn", false);
		Config.SAVE_GM_SPAWN = DevelopSettings.getProperty("SaveGmSpawn", false);
	}

	public static final String EXT_FILE = "config/ext.ini";
	public static boolean EX_NEW_PETITION_SYSTEM;
	public static boolean EX_JAPAN_MINIGAME;
	public static boolean EX_LECTURE_MARK;
	// Bot Report
	public static boolean ENABLE_AUTO_HUNTING_REPORT;
	// RWHO system (off emulation)
	public static boolean RWHO_LOG;
	public static int RWHO_FORCE_INC;
	public static int RWHO_KEEP_STAT;
	public static int RWHO_MAX_ONLINE;
	public static boolean RWHO_SEND_TRASH;
	public static int RWHO_ONLINE_INCREMENT;
	public static float RWHO_PRIV_STORE_FACTOR;
	public static int RWHO_ARRAY[] = new int[13];
	public static boolean ENABLE_CAT_NEC_FREE_FARM;

	public static void loadExtSettings()
	{
		final ExProperties properties = load(EXT_FILE);

		Config.EX_NEW_PETITION_SYSTEM = properties.getProperty("NewPetitionSystem", false);
		Config.EX_JAPAN_MINIGAME = properties.getProperty("JapanMinigame", false);
		Config.EX_LECTURE_MARK = properties.getProperty("LectureMark", false);
		Config.ENABLE_AUTO_HUNTING_REPORT = properties.getProperty("AllowAutoHuntingReport", true);

		Random ppc = new Random();
		int z = ppc.nextInt(6);
		if (z == 0)
		{
			z += 2;
		}
		for (int x = 0; x < 8; x++)
		{
			if (x == 4)
			{
				Config.RWHO_ARRAY[x] = 44;
			}
			else
			{
				Config.RWHO_ARRAY[x] = 51 + ppc.nextInt(z);
			}
		}
		Config.RWHO_ARRAY[11] = 37265 + ppc.nextInt((z * 2) + 3);
		Config.RWHO_ARRAY[8] = 51 + ppc.nextInt(z);
		z = 36224 + ppc.nextInt(z * 2);
		Config.RWHO_ARRAY[9] = z;
		Config.RWHO_ARRAY[10] = z;
		Config.RWHO_ARRAY[12] = 1;
		Config.RWHO_LOG = properties.getProperty("RemoteWhoLog", false);
		Config.RWHO_SEND_TRASH = properties.getProperty("RemoteWhoSendTrash", false);
		Config.RWHO_MAX_ONLINE = properties.getProperty("RemoteWhoMaxOnline", 0);
		Config.RWHO_KEEP_STAT = properties.getProperty("RemoteOnlineKeepStat", 5);
		Config.RWHO_ONLINE_INCREMENT = properties.getProperty("RemoteOnlineIncrement", 0);
		Config.RWHO_PRIV_STORE_FACTOR = properties.getProperty("RemotePrivStoreFactor", 0);
		Config.RWHO_FORCE_INC = properties.getProperty("RemoteWhoForceInc", 0);
	}

	public static final String ITEMS_FILE = "config/mod/items.ini";
	// Items setting
	public static boolean CAN_BE_TRADED_NO_TARADEABLE;
	public static boolean CAN_BE_TRADED_NO_SELLABLE;
	public static boolean CAN_BE_TRADED_NO_STOREABLE;
	public static boolean CAN_BE_TRADED_SHADOW_ITEM;
	public static boolean CAN_BE_TRADED_HERO_WEAPON;
	public static boolean CAN_BE_WH_NO_TARADEABLE;
	public static boolean CAN_BE_CWH_NO_TARADEABLE;
	public static boolean CAN_BE_CWH_IS_AUGMENTED;
	public static boolean CAN_BE_WH_IS_AUGMENTED;
	public static boolean ALLOW_SOUL_SPIRIT_SHOT_INFINITELY;
	public static boolean ALLOW_ARROW_INFINITELY;

	public static boolean ALLOW_START_ITEMS;
	public static int[] START_ITEMS_MAGE;
	public static int[] START_ITEMS_MAGE_COUNT;
	public static int[] START_ITEMS_FITHER;
	public static int[] START_ITEMS_FITHER_COUNT;

	public static void loadItemsSettings()
	{
		final ExProperties itemsProperties = load(ITEMS_FILE);

		Config.CAN_BE_TRADED_NO_TARADEABLE = itemsProperties.getProperty("CanBeTradedNoTradeable", false);
		Config.CAN_BE_TRADED_NO_SELLABLE = itemsProperties.getProperty("CanBeTradedNoSellable", false);
		Config.CAN_BE_TRADED_NO_STOREABLE = itemsProperties.getProperty("CanBeTradedNoStoreable", false);
		Config.CAN_BE_TRADED_SHADOW_ITEM = itemsProperties.getProperty("CanBeTradedShadowItem", false);
		Config.CAN_BE_TRADED_HERO_WEAPON = itemsProperties.getProperty("CanBeTradedHeroWeapon", false);
		Config.CAN_BE_WH_NO_TARADEABLE = itemsProperties.getProperty("CanBeWhNoTradeable", false);
		Config.CAN_BE_CWH_NO_TARADEABLE = itemsProperties.getProperty("CanBeCwhNoTradeable", false);
		Config.CAN_BE_CWH_IS_AUGMENTED = itemsProperties.getProperty("CanBeCwhIsAugmented", false);
		Config.CAN_BE_WH_IS_AUGMENTED = itemsProperties.getProperty("CanBeWhIsAugmented", false);
		Config.ALLOW_SOUL_SPIRIT_SHOT_INFINITELY = itemsProperties.getProperty("AllowSoulSpiritShotInfinitely", false);
		Config.ALLOW_ARROW_INFINITELY = itemsProperties.getProperty("AllowArrowInfinitely", false);
		Config.ALLOW_START_ITEMS = itemsProperties.getProperty("AllowStartItems", false);
		Config.START_ITEMS_MAGE = itemsProperties.getProperty("StartItemsMageIds", new int[]
		{
			57
		});
		Config.START_ITEMS_MAGE_COUNT = itemsProperties.getProperty("StartItemsMageCount", new int[]
		{
			1
		});
		Config.START_ITEMS_FITHER = itemsProperties.getProperty("StartItemsFigtherIds", new int[]
		{
			57
		});
		Config.START_ITEMS_FITHER_COUNT = itemsProperties.getProperty("StartItemsFigtherCount", new int[]
		{
			1
		});
	}

	public static final String TOP_FILE = "config/services/tops.ini";
	/* Top's Config */
	public static boolean L2_TOP_MANAGER_ENABLED;
	public static int L2_TOP_MANAGER_INTERVAL;
	public static String L2_TOP_WEB_ADDRESS;
	public static String L2_TOP_SMS_ADDRESS;
	public static String L2_TOP_SERVER_ADDRESS;
	public static int L2_TOP_SAVE_DAYS;
	public static int[] L2_TOP_REWARD;

	public static boolean MMO_TOP_MANAGER_ENABLED;
	public static int MMO_TOP_MANAGER_INTERVAL;
	public static String MMO_TOP_WEB_ADDRESS;
	public static String MMO_TOP_SERVER_ADDRESS;
	public static int MMO_TOP_SAVE_DAYS;
	public static int[] MMO_TOP_REWARD;

	// global
	public static boolean ALLOW_HOPZONE_VOTE_REWARD;
	public static String HOPZONE_SERVER_LINK;
	public static String HOPZONE_FIRST_PAGE_LINK;
	public static int HOPZONE_VOTES_DIFFERENCE;
	public static int HOPZONE_FIRST_PAGE_RANK_NEEDED;
	public static int HOPZONE_REWARD_CHECK_TIME;
	public static int HOPZONE_REWARD_ID;
	public static int HOPZONE_REWARD_COUNT;
	public static int HOPZONE_DUALBOXES_ALLOWED;
	public static boolean ALLOW_HOPZONE_GAME_SERVER_REPORT;
	public static boolean ALLOW_TOPZONE_VOTE_REWARD;
	public static String TOPZONE_SERVER_LINK;
	public static String TOPZONE_FIRST_PAGE_LINK;
	public static int TOPZONE_VOTES_DIFFERENCE;
	public static int TOPZONE_FIRST_PAGE_RANK_NEEDED;
	public static int TOPZONE_REWARD_CHECK_TIME;
	public static int TOPZONE_REWARD_ID;
	public static int TOPZONE_REWARD_COUNT;
	public static int TOPZONE_DUALBOXES_ALLOWED;
	public static boolean ALLOW_TOPZONE_GAME_SERVER_REPORT;

	// Vote System
	// individual
	public static String VOTE_LINK_HOPZONE;
	public static String VOTE_LINK_TOPZONE;
	public static int VOTE_REWARD_ID1;
	public static int VOTE_REWARD_ID2;
	public static int VOTE_REWARD_ID3;
	public static int VOTE_REWARD_ID4;
	public static int VOTE_REWARD_AMOUNT1;
	public static int VOTE_REWARD_AMOUNT2;
	public static int VOTE_REWARD_AMOUNT3;
	public static int VOTE_REWARD_AMOUNT4;
	public static int SECS_TO_VOTE;
	public static int EXTRA_REW_VOTE_AM;

	public static void loadTopSettings()
	{
		final ExProperties topSetting = load(TOP_FILE);

		Config.L2_TOP_MANAGER_ENABLED = topSetting.getProperty("L2TopManagerEnabled", false);
		Config.L2_TOP_MANAGER_INTERVAL = topSetting.getProperty("L2TopManagerInterval", 300000);
		Config.L2_TOP_WEB_ADDRESS = topSetting.getProperty("L2TopWebAddress", "");
		Config.L2_TOP_SMS_ADDRESS = topSetting.getProperty("L2TopSmsAddress", "");
		Config.L2_TOP_SERVER_ADDRESS = topSetting.getProperty("L2TopServerAddress", "Ro-Team.com");
		Config.L2_TOP_SAVE_DAYS = topSetting.getProperty("L2TopSaveDays", 30);
		Config.L2_TOP_REWARD = topSetting.getProperty("L2TopReward", new int[0]);

		Config.MMO_TOP_MANAGER_ENABLED = topSetting.getProperty("MMOTopEnable", false);
		Config.MMO_TOP_MANAGER_INTERVAL = topSetting.getProperty("MMOTopManagerInterval", 300000);
		Config.MMO_TOP_WEB_ADDRESS = topSetting.getProperty("MMOTopUrl", "");
		Config.MMO_TOP_SERVER_ADDRESS = topSetting.getProperty("MMOTopServerAddress", "Ro-Team.com");
		Config.MMO_TOP_SAVE_DAYS = topSetting.getProperty("MMOTopSaveDays", 30);
		Config.MMO_TOP_REWARD = topSetting.getProperty("MMOTopReward", new int[0]);

		Config.ALLOW_HOPZONE_VOTE_REWARD = topSetting.getProperty("AllowHopzoneVoteReward", true);
		Config.HOPZONE_SERVER_LINK = topSetting.getProperty("HopzoneServerLink", "http://l2.hopzone.net/lineage2/");
		Config.HOPZONE_FIRST_PAGE_LINK = topSetting.getProperty("HopzoneFirstPageLink", "http://l2.hopzone.net/lineage2/");
		Config.HOPZONE_VOTES_DIFFERENCE = topSetting.getProperty("HopzoneVotesDifference", 5);
		Config.HOPZONE_FIRST_PAGE_RANK_NEEDED = topSetting.getProperty("HopzoneFirstPageRankNeeded", 15);
		Config.HOPZONE_REWARD_CHECK_TIME = topSetting.getProperty("HopzoneRewardCheckTime", 5);
		Config.HOPZONE_DUALBOXES_ALLOWED = topSetting.getProperty("HopzoneDualboxesAllowed", 1);
		Config.ALLOW_HOPZONE_GAME_SERVER_REPORT = topSetting.getProperty("AllowHopzoneGameServerReport", true);
		Config.ALLOW_TOPZONE_VOTE_REWARD = topSetting.getProperty("AllowTopzoneVoteReward", true);
		Config.TOPZONE_SERVER_LINK = topSetting.getProperty("TopzoneServerLink", "http://l2.topzone.net/lineage2/");
		Config.TOPZONE_FIRST_PAGE_LINK = topSetting.getProperty("TopzoneFirstPageLink", "http://l2.topzone.net/lineage2/");
		Config.TOPZONE_VOTES_DIFFERENCE = topSetting.getProperty("TopzoneVotesDifference", 5);
		Config.TOPZONE_FIRST_PAGE_RANK_NEEDED = topSetting.getProperty("TopzoneFirstPageRankNeeded", 15);
		Config.TOPZONE_REWARD_CHECK_TIME = topSetting.getProperty("TopzoneRewardCheckTime", 5);
		Config.TOPZONE_DUALBOXES_ALLOWED = topSetting.getProperty("TopzoneDualboxesAllowed", 1);
		Config.ALLOW_TOPZONE_GAME_SERVER_REPORT = topSetting.getProperty("AllowTopzoneGameServerReport", true);
		Config.HOPZONE_REWARD_ID = topSetting.getProperty("HopZoneRewardId", 6673);
		Config.HOPZONE_REWARD_COUNT = topSetting.getProperty("HopZoneTopRewardCount", 1);
		Config.TOPZONE_REWARD_ID = topSetting.getProperty("TopZoneRewardId", 6673);
		Config.TOPZONE_REWARD_COUNT = topSetting.getProperty("TopZoneRewardCount", 1);
		// individual reward by claww
		Config.VOTE_LINK_HOPZONE = topSetting.getProperty("HopzoneUrl", "null");
		Config.VOTE_LINK_TOPZONE = topSetting.getProperty("TopzoneUrl", "null");
		Config.VOTE_REWARD_ID1 = topSetting.getProperty("VoteRewardId1", 300);
		Config.VOTE_REWARD_ID2 = topSetting.getProperty("VoteRewardId2", 300);
		Config.VOTE_REWARD_ID3 = topSetting.getProperty("VoteRewardId3", 300);
		Config.VOTE_REWARD_ID4 = topSetting.getProperty("VoteRewardId4", 300);
		Config.VOTE_REWARD_AMOUNT1 = topSetting.getProperty("VoteRewardAmount1", 300);
		Config.VOTE_REWARD_AMOUNT2 = topSetting.getProperty("VoteRewardAmount2", 300);
		Config.VOTE_REWARD_AMOUNT3 = topSetting.getProperty("VoteRewardAmount3", 300);
		Config.VOTE_REWARD_AMOUNT4 = topSetting.getProperty("VoteRewardAmount4", 300);
		Config.SECS_TO_VOTE = topSetting.getProperty("SecondsToVote", 20);
		Config.EXTRA_REW_VOTE_AM = topSetting.getProperty("ExtraRewVoteAm", 20);

	}

	public static final String PAYMENT_FILE = "config/services/payment.ini";
	public static boolean SMS_PAYMENT_MANAGER_ENABLED;
	public static String SMS_PAYMENT_WEB_ADDRESS;
	public static int SMS_PAYMENT_MANAGER_INTERVAL;
	public static int SMS_PAYMENT_SAVE_DAYS;
	public static String SMS_PAYMENT_SERVER_ADDRESS;
	public static int[] SMS_PAYMENT_REWARD;

	public static void loadPaymentSettings()
	{
		final ExProperties paymentSettings = load(PAYMENT_FILE);

		Config.SMS_PAYMENT_MANAGER_ENABLED = paymentSettings.getProperty("SMSPaymentEnabled", false);
		Config.SMS_PAYMENT_WEB_ADDRESS = paymentSettings.getProperty("SMSPaymentWebAddress", "");
		Config.SMS_PAYMENT_MANAGER_INTERVAL = paymentSettings.getProperty("SMSPaymentManagerInterval", 300000);
		Config.SMS_PAYMENT_SAVE_DAYS = paymentSettings.getProperty("SMSPaymentSaveDays", 30);
		Config.SMS_PAYMENT_SERVER_ADDRESS = paymentSettings.getProperty("SMSPaymentServerAddress", "Ro-Team.com");
		Config.SMS_PAYMENT_REWARD = paymentSettings.getProperty("SMSPaymentReward", new int[0]);
	}

	public static final String ALT_SETTINGS_FILE = "config/altsettings.ini";
	/** Ð Ð°Ð·Ñ€ÐµÑˆÐ°Ñ‚ÑŒ Ð»Ð¸ Ð½Ð° Ð°Ñ€ÐµÐ½Ðµ Ð±Ð¾Ð¸ Ð·Ð° Ð¾Ð¿Ñ‹Ñ‚ */
	public static boolean ALT_ARENA_EXP;
	public static boolean AUTO_SOUL_CRYSTAL_QUEST;

	public static boolean ALT_GAME_SUBCLASS_WITHOUT_QUESTS;
	public static boolean ALT_ALLOW_SUBCLASS_WITHOUT_BAIUM;
	public static int ALT_GAME_START_LEVEL_TO_SUBCLASS;
	public static int ALT_GAME_LEVEL_TO_GET_SUBCLASS;
	public static int ALT_MAX_LEVEL;
	public static int ALT_MAX_SUB_LEVEL;
	public static int ALT_GAME_SUB_ADD;
	public static boolean ALT_GAME_SUB_BOOK;
	public static boolean ALT_NO_LASTHIT;
	public static boolean ALT_KAMALOKA_NIGHTMARES_PREMIUM_ONLY;
	public static boolean ALT_KAMALOKA_NIGHTMARE_REENTER;
	public static boolean ALT_KAMALOKA_ABYSS_REENTER;
	public static boolean ALT_KAMALOKA_LAB_REENTER;
	public static boolean ALT_PET_HEAL_BATTLE_ONLY;

	public static boolean ALT_SIMPLE_SIGNS;
	public static boolean ALT_TELE_TO_CATACOMBS;
	public static boolean ALT_BS_CRYSTALLIZE;
	public static int ALT_MAMMON_EXCHANGE;
	public static int ALT_MAMMON_UPGRADE;
	public static boolean ALT_ALLOW_TATTOO;

	public static int ALT_BUFF_LIMIT;

	public static boolean ALT_ALLOW_OTHERS_WITHDRAW_FROM_CLAN_WAREHOUSE;
	public static boolean ALT_ALLOW_CLAN_COMMAND_ONLY_FOR_CLAN_LEADER;
	public static boolean ALT_GAME_REQUIRE_CLAN_CASTLE;
	public static boolean ALT_GAME_REQUIRE_CASTLE_DAWN;
	public static boolean ALT_GAME_ALLOW_ADENA_DAWN;
	public static boolean RETAIL_SS;

	/** Alternative gameing - loss of XP on death */
	public static boolean ALT_GAME_DELEVEL;
	public static int ALT_MAIL_MIN_LVL;
	public static int VITAMIN_PETS_FOOD_ID;
	public static int VITAMIN_DESELOT_FOOD_ID;
	public static int VITAMIN_SUPERPET_FOOD_ID;
	public static boolean ALT_TELEPORTS_ONLY_FOR_GIRAN;

	public static double ALT_RAID_RESPAWN_MULTIPLIER;
	public static boolean ALT_ALLOW_AUGMENT_ALL;
	public static boolean ALT_ALLOW_DROP_AUGMENTED;
	public static boolean ALT_GAME_UNREGISTER_RECIPE;

	/** Delay for announce SS period (in minutes) */
	public static int SS_ANNOUNCE_PERIOD;

	/** Petition manager */
	public static boolean PETITIONING_ALLOWED;
	public static int MAX_PETITIONS_PER_PLAYER;
	public static int MAX_PETITIONS_PENDING;

	/** Show mob stats/droplist to players? */
	public static boolean ALT_GAME_SHOW_DROPLIST;
	public static boolean ALT_FULL_NPC_STATS_PAGE;
	public static boolean ALLOW_NPC_SHIFTCLICK;

	public static boolean ALT_ALLOW_SELL_COMMON;
	public static boolean ALT_ALLOW_SHADOW_WEAPONS;
	public static int[] ALT_DISABLED_MULTISELL;
	public static int[] ALT_SHOP_PRICE_LIMITS;
	public static int[] ALT_SHOP_UNALLOWED_ITEMS;
	public static int[] ALT_ALLOWED_PET_POTIONS;
	public static boolean SHIELD_SLAM_BLOCK_IS_MUSIC;
	public static boolean ALT_SAVE_UNSAVEABLE;
	public static int ALT_SAVE_EFFECTS_REMAINING_TIME;

	public static boolean ALLOW_PET_ATTACK_MASTER;
	public static boolean TELEPORT_PET_TO_MASTER;
	public static boolean ALT_SHOW_REUSE_MSG;
	public static boolean ALT_DELETE_SA_BUFFS;
	public static boolean AUTO_LOOT;
	public static boolean AUTO_LOOT_HERBS;
	public static boolean AUTO_LOOT_ONLY_ADENA;
	public static boolean AUTO_LOOT_INDIVIDUAL;
	public static boolean AUTO_LOOT_FROM_RAIDS;

	/** Auto-loot for/from players with karma also? */
	public static boolean AUTO_LOOT_PK;
	/** Karma Punishment */
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_SHOP;
	public static boolean SAVING_SPS;
	public static boolean MANAHEAL_SPS_BONUS;
	public static double CRAFT_MASTERWORK_CHANCE;
	public static double CRAFT_DOUBLECRAFT_CHANCE;
	public static double[] AUGMENTATION_CHANCE_MOD;
	public static int ALT_ADD_RECIPES;
	public static boolean AUTO_LEARN_SKILLS;
	public static boolean AUTO_LEARN_FORGOTTEN_SKILLS;
	/** Ð¢Ð°Ð¹Ð¼Ð°ÑƒÑ‚ Ð½Ð° Ð¸Ñ�Ð¿Ð¾Ð»ÑŒÐ·Ð¾Ð²Ð°Ð½Ð¸Ðµ social action */
	public static boolean ALT_SOCIAL_ACTION_REUSE;

	/** ÐžÑ‚ÐºÐ»ÑŽÑ‡ÐµÐ½Ð¸Ðµ ÐºÐ½Ð¸Ð³ Ð´Ð»Ñ� Ð¸Ð·ÑƒÑ‡ÐµÐ½Ð¸Ñ� Ñ�ÐºÐ¸Ð»Ð¾Ð² */
	public static boolean ALT_DISABLE_SPELLBOOKS;

	public static boolean ALT_DEATH_PENALTY;
	public static boolean ALLOW_DEATH_PENALTY_C5;
	public static int ALT_DEATH_PENALTY_C5_CHANCE;
	public static boolean ALT_DEATH_PENALTY_C5_CHAOTIC_RECOVERY;
	public static int ALT_DEATH_PENALTY_C5_EXPERIENCE_PENALTY;
	public static int ALT_DEATH_PENALTY_C5_KARMA_PENALTY;
	public static double ALT_PK_DEATH_RATE;
	public static long NONOWNER_ITEM_PICKUP_DELAY;
	public static long NONOWNER_ITEM_PICKUP_DELAY_RAIDS;
	// Remove dance and songs shot click
	public static boolean ALT_DISPEL_MUSIC;
	/** Ð¢Ð¸Ñ‚ÑƒÐ» Ð¿Ñ€Ð¸ Ñ�Ð¾Ð·Ð´Ð°Ð½Ð¸Ð¸ Ñ‡Ð°Ñ€Ð° */
	public static boolean CHAR_TITLE;
	public static String ADD_CHAR_TITLE;

	public static int FESTIVAL_MIN_PARTY_SIZE;
	public static double FESTIVAL_RATE_PRICE;
	public static boolean ENABLE_POLL_SYSTEM;
	public static int ANNOUNCE_POLL_EVERY_X_MIN;
	/** DimensionalRift Config **/
	public static int RIFT_MIN_PARTY_SIZE;
	public static int RIFT_SPAWN_DELAY; // Time in ms the party has to wait until the mobs spawn
	public static int RIFT_MAX_JUMPS;
	public static int RIFT_AUTO_JUMPS_TIME;
	public static int RIFT_AUTO_JUMPS_TIME_RAND;
	public static int RIFT_ENTER_COST_RECRUIT;
	public static int RIFT_ENTER_COST_SOLDIER;
	public static int RIFT_ENTER_COST_OFFICER;
	public static int RIFT_ENTER_COST_CAPTAIN;
	public static int RIFT_ENTER_COST_COMMANDER;
	public static int RIFT_ENTER_COST_HERO;

	public static boolean ALLOW_TALK_WHILE_SITTING;

	public static boolean PARTY_LEADER_ONLY_CAN_INVITE;

	/** Ð Ð°Ð·Ñ€ÐµÑˆÐµÐ½Ñ‹ Ð»Ð¸ ÐºÐ»Ð°Ð½Ð¾Ð²Ñ‹Ðµ Ñ�ÐºÐ¸Ð»Ñ‹? **/
	public static boolean ALLOW_CLANSKILLS;

	/** Ð Ð°Ð·Ñ€ÐµÑˆÐµÐ½Ð¾ Ð»Ð¸ Ð¸Ð·ÑƒÑ‡ÐµÐ½Ð¸Ðµ Ñ�ÐºÐ¸Ð»Ð¾Ð² Ñ‚Ñ€Ð°Ð½Ñ�Ñ„Ð¾Ñ€Ð¼Ð°Ñ†Ð¸Ð¸ Ð¸ Ñ�Ð°Ð± ÐºÐ»Ð°Ñ�Ñ�Ð¾Ð² Ð±ÐµÐ· Ð½Ð°Ð»Ð¸Ñ‡Ð¸Ñ� Ð²Ñ‹Ð¿Ð¾Ð»Ð½ÐµÐ½Ð½Ð¾Ð³Ð¾ ÐºÐ²ÐµÑ�Ñ‚Ð° */
	public static boolean ALLOW_LEARN_TRANS_SKILLS_WO_QUEST;

	public static double ALT_CHAMPION_CHANCE1;
	public static double ALT_CHAMPION_CHANCE2;
	public static boolean ALT_CHAMPION_CAN_BE_AGGRO;
	public static boolean ALT_CHAMPION_CAN_BE_SOCIAL;
	public static boolean ALT_CHAMPION_DROP_HERBS;
	public static boolean ALT_SHOW_MONSTERS_LVL;
	public static boolean ALT_SHOW_MONSTERS_AGRESSION;
	public static int ALT_CHAMPION_TOP_LEVEL;
	public static int ALT_CHAMPION_MIN_LEVEL;
	public static boolean ALLOW_NOBLE_TP_TO_ALL;
	public static double CLANHALL_BUFFTIME_MODIFIER;
	public static double SONGDANCETIME_MODIFIER;

	public static double MAXLOAD_MODIFIER;
	public static double GATEKEEPER_MODIFIER;
	public static boolean ALT_IMPROVED_PETS_LIMITED_USE;
	public static int GATEKEEPER_FREE;
	public static int CRUMA_GATEKEEPER_LVL;
	public static boolean ALT_VITALITY_ENABLED;
	public static double ALT_VITALITY_RATE;
	public static double ALT_VITALITY_CONSUME_RATE;
	public static int ALT_VITALITY_RAID_BONUS;
	public static final int[] VITALITY_LEVELS =
	{
		240,
		2000,
		13000,
		17000,
		20000
	};
	public static boolean ALT_PCBANG_POINTS_ENABLED;
	public static double ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE;
	public static int ALT_PCBANG_POINTS_BONUS;
	public static int ALT_PCBANG_POINTS_DELAY;
	public static int ALT_PCBANG_POINTS_MIN_LVL;
	public static int ALT_MAX_ALLY_SIZE;
	public static int ALT_PARTY_DISTRIBUTION_RANGE;
	public static double[] ALT_PARTY_BONUS;
	public static int ALT_LEVEL_DIFFERENCE_PROTECTION;
	public static boolean ALT_ALL_PHYS_SKILLS_OVERHIT;
	public static boolean ALT_REMOVE_SKILLS_ON_DELEVEL;
	public static boolean ALT_USE_BOW_REUSE_MODIFIER;

	public static boolean ALLOW_CH_DOOR_OPEN_ON_CLICK;
	public static boolean ALT_CH_ALL_BUFFS;
	public static boolean ALT_CH_ALLOW_1H_BUFFS;
	public static boolean ALT_CH_SIMPLE_DIALOG;
	/** Augmentations **/
	public static int AUGMENTATION_NG_SKILL_CHANCE; // Chance to get a skill while using a NoGrade Life Stone
	public static int AUGMENTATION_NG_GLOW_CHANCE; // Chance to get a Glow effect while using a NoGrade Life Stone(only if you get a skill)
	public static int AUGMENTATION_MID_SKILL_CHANCE; // Chance to get a skill while using a MidGrade Life Stone
	public static int AUGMENTATION_MID_GLOW_CHANCE; // Chance to get a Glow effect while using a MidGrade Life Stone(only if you get a skill)
	public static int AUGMENTATION_HIGH_SKILL_CHANCE; // Chance to get a skill while using a HighGrade Life Stone
	public static int AUGMENTATION_HIGH_GLOW_CHANCE; // Chance to get a Glow effect while using a HighGrade Life Stone
	public static int AUGMENTATION_TOP_SKILL_CHANCE; // Chance to get a skill while using a TopGrade Life Stone
	public static int AUGMENTATION_TOP_GLOW_CHANCE; // Chance to get a Glow effect while using a TopGrade Life Stone
	public static int AUGMENTATION_BASESTAT_CHANCE; // Chance to get a BaseStatModifier in the augmentation process
	public static int AUGMENTATION_ACC_SKILL_CHANCE;
	public static boolean ALT_OPEN_CLOAK_SLOT;
	public static int FOLLOW_RANGE;
	public static boolean ALT_ENABLE_MULTI_PROFA;

	public static boolean ALT_ITEM_AUCTION_ENABLED;
	public static boolean ALT_ITEM_AUCTION_CAN_REBID;
	public static boolean ALT_ITEM_AUCTION_START_ANNOUNCE;
	public static int ALT_ITEM_AUCTION_BID_ITEM_ID;
	public static long ALT_ITEM_AUCTION_MAX_BID;
	public static int ALT_ITEM_AUCTION_MAX_CANCEL_TIME_IN_MILLIS;

	public static boolean ALT_FISH_CHAMPIONSHIP_ENABLED;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_ITEM;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_1;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_2;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_3;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_4;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_5;

	public static boolean ALT_ENABLE_BLOCK_CHECKER_EVENT;
	public static int ALT_MIN_BLOCK_CHECKER_TEAM_MEMBERS;
	public static double ALT_RATE_COINS_REWARD_BLOCK_CHECKER;
	public static boolean ALT_HBCE_FAIR_PLAY;
	public static int ALT_PET_INVENTORY_LIMIT;
	public static int ALT_CLAN_LEVEL_CREATE;

	public static boolean ENABLE_AUCTION_SYSTEM;
	public static long AUCTION_FEE;
	public static int AUCTION_INACTIVITY_DAYS_TO_DELETE;
	public static boolean ALLOW_AUCTION_OUTSIDE_TOWN;
	public static int SECONDS_BETWEEN_ADDING_AUCTIONS;
	public static boolean AUCTION_PRIVATE_STORE_AUTO_ADDED;

	public static int CLAN_LEVEL_6_COST;
	public static int CLAN_LEVEL_7_COST;
	public static int CLAN_LEVEL_8_COST;
	public static int CLAN_LEVEL_9_COST;
	public static int CLAN_LEVEL_10_COST;
	public static int CLAN_LEVEL_11_COST;

	public static int CLAN_LEVEL_6_REQUIREMEN;
	public static int CLAN_LEVEL_7_REQUIREMEN;
	public static int CLAN_LEVEL_8_REQUIREMEN;
	public static int CLAN_LEVEL_9_REQUIREMEN;
	public static int CLAN_LEVEL_10_REQUIREMEN;
	public static int CLAN_LEVEL_11_REQUIREMEN;
	public static int BLOOD_OATHS;
	public static int BLOOD_PLEDGES;
	public static int MIN_ACADEM_POINT;
	public static int MAX_ACADEM_POINT;

	public static boolean ZONE_PVP_COUNT;
	public static boolean SIEGE_PVP_COUNT;
	public static boolean EPIC_EXPERTISE_PENALTY;
	public static boolean EXPERTISE_PENALTY;
	public static int HELLBOUND_LEVEL;
	public static int CLAN_LEAVE_PENALTY;
	public static int ALLY_LEAVE_PENALTY;
	public static int DISSOLVED_ALLY_PENALTY;
	public static int DISSOLVED_CLAN_PENALTY;
	public static int CLAN_DISBAND_TIME;
	public static int ALT_MUSIC_LIMIT;
	public static int ALT_DEBUFF_LIMIT;
	public static int ALT_TRIGGER_LIMIT;
	public static boolean ENABLE_MODIFY_SKILL_DURATION;
	public static boolean ALT_TIME_MODE_SKILL_DURATION;
	public static TIntIntHashMap SKILL_DURATION_LIST;

	/** Ancient Herb */
	public static int ANCIENT_HERB_SPAWN_RADIUS;
	public static int ANCIENT_HERB_SPAWN_CHANCE;
	public static int ANCIENT_HERB_SPAWN_COUNT;
	public static int ANCIENT_HERB_RESPAWN_TIME;
	public static int ANCIENT_HERB_DESPAWN_TIME;
	public static List<Location> HEIN_FIELDS_LOCATIONS = new ArrayList<Location>();

	public static void loadAltSettings()
	{
		final ExProperties altSettings = load(ALT_SETTINGS_FILE);
		Config.ALT_ARENA_EXP = altSettings.getProperty("ArenaExp", true);
		Config.AUTO_SOUL_CRYSTAL_QUEST = altSettings.getProperty("AutoSoulCrystalQuest", true);
		Config.ALT_GAME_DELEVEL = altSettings.getProperty("Delevel", true);
		Config.ALT_MAIL_MIN_LVL = altSettings.getProperty("MinLevelToSendMail", 0);
		Config.VITAMIN_PETS_FOOD_ID = altSettings.getProperty("VitaminPetsFoodID", -1);
		Config.VITAMIN_DESELOT_FOOD_ID = altSettings.getProperty("VitaminDeselotFoodID", -1);
		Config.ALT_TELEPORTS_ONLY_FOR_GIRAN = altSettings.getProperty("AllScrollsSoEToGiran", false);
		Config.VITAMIN_SUPERPET_FOOD_ID = altSettings.getProperty("VitaminSuperPetID", -1);
		Config.ALT_SAVE_UNSAVEABLE = altSettings.getProperty("AltSaveUnsaveable", false);
		Config.SHIELD_SLAM_BLOCK_IS_MUSIC = altSettings.getProperty("ShieldSlamBlockIsMusic", false);
		Config.ALT_SAVE_EFFECTS_REMAINING_TIME = altSettings.getProperty("AltSaveEffectsRemainingTime", 5);
		Config.ALLOW_PET_ATTACK_MASTER = altSettings.getProperty("allowPetAttackMaster", true);
		Config.TELEPORT_PET_TO_MASTER = altSettings.getProperty("TeleportPetToMaster", false);
		Config.ALT_SHOW_REUSE_MSG = altSettings.getProperty("AltShowSkillReuseMessage", true);
		Config.ALT_DELETE_SA_BUFFS = altSettings.getProperty("AltDeleteSABuffs", false);
		Config.AUTO_LOOT = altSettings.getProperty("AutoLoot", false);
		Config.AUTO_LOOT_ONLY_ADENA = altSettings.getProperty("AutoLootOnlyAdena", false);
		Config.AUTO_LOOT_HERBS = altSettings.getProperty("AutoLootHerbs", false);
		Config.AUTO_LOOT_INDIVIDUAL = altSettings.getProperty("AutoLootIndividual", false);
		Config.AUTO_LOOT_FROM_RAIDS = altSettings.getProperty("AutoLootFromRaids", false);
		Config.AUTO_LOOT_PK = altSettings.getProperty("AutoLootPK", false);
		Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP = altSettings.getProperty("AltKarmaPlayerCanShop", false);
		Config.SAVING_SPS = altSettings.getProperty("SavingSpS", false);
		Config.MANAHEAL_SPS_BONUS = altSettings.getProperty("ManahealSpSBonus", false);
		Config.CRAFT_MASTERWORK_CHANCE = altSettings.getProperty("CraftMasterworkChance", 3.);
		Config.CRAFT_DOUBLECRAFT_CHANCE = altSettings.getProperty("CraftDoubleCraftChance", 3.);
		Config.ALT_RAID_RESPAWN_MULTIPLIER = altSettings.getProperty("AltRaidRespawnMultiplier", 1.0);
		Config.ALT_ALLOW_AUGMENT_ALL = altSettings.getProperty("AugmentAll", false);
		Config.ALT_ALLOW_DROP_AUGMENTED = altSettings.getProperty("AlowDropAugmented", false);
		Config.ALT_GAME_UNREGISTER_RECIPE = altSettings.getProperty("AltUnregisterRecipe", true);
		Config.ALT_GAME_SHOW_DROPLIST = altSettings.getProperty("AltShowDroplist", true);
		Config.ALLOW_NPC_SHIFTCLICK = altSettings.getProperty("AllowShiftClick", true);
		Config.ALT_FULL_NPC_STATS_PAGE = altSettings.getProperty("AltFullStatsPage", false);
		Config.ALT_GAME_SUBCLASS_WITHOUT_QUESTS = altSettings.getProperty("AltAllowSubClassWithoutQuest", false);
		Config.ALT_ALLOW_SUBCLASS_WITHOUT_BAIUM = altSettings.getProperty("AltAllowSubClassWithoutBaium", true);
		Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS = altSettings.getProperty("AltLevelToGetSubclass", 75);
		Config.ALT_GAME_START_LEVEL_TO_SUBCLASS = altSettings.getProperty("AltStartLevelToSubclass", 40);
		Config.ALT_GAME_SUB_ADD = altSettings.getProperty("AltSubAdd", 0);
		Config.ALT_GAME_SUB_BOOK = altSettings.getProperty("AltSubBook", false);
		Config.ALT_MAX_LEVEL = Math.min(altSettings.getProperty("AltMaxLevel", 85), Experience.LEVEL.length - 1);
		Config.ALT_MAX_SUB_LEVEL = Math.min(altSettings.getProperty("AltMaxSubLevel", 80), Experience.LEVEL.length - 1);
		Config.ALT_ALLOW_OTHERS_WITHDRAW_FROM_CLAN_WAREHOUSE = altSettings.getProperty("AltAllowOthersWithdrawFromClanWarehouse", false);
		Config.ALT_ALLOW_CLAN_COMMAND_ONLY_FOR_CLAN_LEADER = altSettings.getProperty("AltAllowClanCommandOnlyForClanLeader", true);
		Config.AUGMENTATION_CHANCE_MOD = altSettings.getProperty("AugmentChance", new double[]
		{
			1.0D,
			1.0D
		});
		Config.ALT_GAME_REQUIRE_CLAN_CASTLE = altSettings.getProperty("AltRequireClanCastle", false);
		Config.ALT_GAME_REQUIRE_CASTLE_DAWN = altSettings.getProperty("AltRequireCastleDawn", true);
		Config.ALT_GAME_ALLOW_ADENA_DAWN = altSettings.getProperty("AltAllowAdenaDawn", true);
		Config.RETAIL_SS = altSettings.getProperty("Retail_SevenSigns", true);
		Config.ALT_ADD_RECIPES = altSettings.getProperty("AltAddRecipes", 0);
		Config.SS_ANNOUNCE_PERIOD = altSettings.getProperty("SSAnnouncePeriod", 0);
		Config.PETITIONING_ALLOWED = altSettings.getProperty("PetitioningAllowed", true);
		Config.MAX_PETITIONS_PER_PLAYER = altSettings.getProperty("MaxPetitionsPerPlayer", 5);
		Config.MAX_PETITIONS_PENDING = altSettings.getProperty("MaxPetitionsPending", 25);
		Config.AUTO_LEARN_SKILLS = altSettings.getProperty("AutoLearnSkills", false);
		Config.AUTO_LEARN_FORGOTTEN_SKILLS = altSettings.getProperty("AutoLearnForgottenSkills", false);
		Config.ALT_SOCIAL_ACTION_REUSE = altSettings.getProperty("AltSocialActionReuse", false);
		Config.ALT_DISABLE_SPELLBOOKS = altSettings.getProperty("AltDisableSpellbooks", false);
		Config.ALT_SIMPLE_SIGNS = altSettings.getProperty("PushkinSignsOptions", false);
		Config.ALT_TELE_TO_CATACOMBS = altSettings.getProperty("TeleToCatacombs", false);
		Config.ALT_BS_CRYSTALLIZE = altSettings.getProperty("BSCrystallize", false);
		Config.ALT_MAMMON_UPGRADE = altSettings.getProperty("MammonUpgrade", 6680500);
		Config.ALT_MAMMON_EXCHANGE = altSettings.getProperty("MammonExchange", 10091400);
		Config.ALT_ALLOW_TATTOO = altSettings.getProperty("AllowTattoo", false);
		Config.ALT_BUFF_LIMIT = altSettings.getProperty("BuffLimit", 20);
		Config.ALT_DEATH_PENALTY = altSettings.getProperty("EnableAltDeathPenalty", false);
		Config.ALLOW_DEATH_PENALTY_C5 = altSettings.getProperty("EnableDeathPenaltyC5", true);
		Config.ALT_DEATH_PENALTY_C5_CHANCE = altSettings.getProperty("DeathPenaltyC5Chance", 10);
		Config.ALT_DEATH_PENALTY_C5_CHAOTIC_RECOVERY = altSettings.getProperty("ChaoticCanUseScrollOfRecovery", false);
		Config.ALT_DEATH_PENALTY_C5_EXPERIENCE_PENALTY = altSettings.getProperty("DeathPenaltyC5RateExpPenalty", 1);
		Config.ALT_DEATH_PENALTY_C5_KARMA_PENALTY = altSettings.getProperty("DeathPenaltyC5RateKarma", 1);
		Config.ALT_PK_DEATH_RATE = altSettings.getProperty("AltPKDeathRate", 0.);
		Config.NONOWNER_ITEM_PICKUP_DELAY = altSettings.getProperty("NonOwnerItemPickupDelay", 15L) * 1000L;
		Config.NONOWNER_ITEM_PICKUP_DELAY_RAIDS = altSettings.getProperty("NonOwnerItemPickupDelayRaids", 285L) * 1000L;
		Config.ALT_NO_LASTHIT = altSettings.getProperty("NoLasthitOnRaid", false);
		Config.ALT_DISPEL_MUSIC = altSettings.getProperty("AltDispelDanceSong", false);
		Config.ALT_KAMALOKA_NIGHTMARES_PREMIUM_ONLY = altSettings.getProperty("KamalokaNightmaresPremiumOnly", false);
		Config.ALT_KAMALOKA_NIGHTMARE_REENTER = altSettings.getProperty("SellReenterNightmaresTicket", true);
		Config.ALT_KAMALOKA_ABYSS_REENTER = altSettings.getProperty("SellReenterAbyssTicket", true);
		Config.ALT_KAMALOKA_LAB_REENTER = altSettings.getProperty("SellReenterLabyrinthTicket", true);
		Config.ALT_PET_HEAL_BATTLE_ONLY = altSettings.getProperty("PetsHealOnlyInBattle", true);
		Config.CHAR_TITLE = altSettings.getProperty("CharTitle", false);
		Config.ADD_CHAR_TITLE = altSettings.getProperty("CharAddTitle", "");

		Config.ALT_ALLOW_SELL_COMMON = altSettings.getProperty("AllowSellCommon", true);
		Config.ALT_ALLOW_SHADOW_WEAPONS = altSettings.getProperty("AllowShadowWeapons", true);
		Config.ALT_DISABLED_MULTISELL = altSettings.getProperty("DisabledMultisells", ArrayUtils.EMPTY_INT_ARRAY);
		Config.ALT_SHOP_PRICE_LIMITS = altSettings.getProperty("ShopPriceLimits", ArrayUtils.EMPTY_INT_ARRAY);
		Config.ALT_SHOP_UNALLOWED_ITEMS = altSettings.getProperty("ShopUnallowedItems", ArrayUtils.EMPTY_INT_ARRAY);

		Config.ALT_ALLOWED_PET_POTIONS = altSettings.getProperty("AllowedPetPotions", new int[]
		{
			735,
			1060,
			1061,
			1062,
			1374,
			1375,
			1539,
			1540,
			6035,
			6036
		});

		Config.FESTIVAL_MIN_PARTY_SIZE = altSettings.getProperty("FestivalMinPartySize", 5);
		Config.FESTIVAL_RATE_PRICE = altSettings.getProperty("FestivalRatePrice", 1.0);

		Config.ENABLE_POLL_SYSTEM = altSettings.getProperty("EnablePoll", true);
		Config.ANNOUNCE_POLL_EVERY_X_MIN = altSettings.getProperty("AnnounceToVoteInMin", 10);

		Config.RIFT_MIN_PARTY_SIZE = altSettings.getProperty("RiftMinPartySize", 5);
		Config.RIFT_SPAWN_DELAY = altSettings.getProperty("RiftSpawnDelay", 10000);
		Config.RIFT_MAX_JUMPS = altSettings.getProperty("MaxRiftJumps", 4);
		Config.RIFT_AUTO_JUMPS_TIME = altSettings.getProperty("AutoJumpsDelay", 8);
		Config.RIFT_AUTO_JUMPS_TIME_RAND = altSettings.getProperty("AutoJumpsDelayRandom", 120000);

		Config.RIFT_ENTER_COST_RECRUIT = altSettings.getProperty("RecruitFC", 18);
		Config.RIFT_ENTER_COST_SOLDIER = altSettings.getProperty("SoldierFC", 21);
		Config.RIFT_ENTER_COST_OFFICER = altSettings.getProperty("OfficerFC", 24);
		Config.RIFT_ENTER_COST_CAPTAIN = altSettings.getProperty("CaptainFC", 27);
		Config.RIFT_ENTER_COST_COMMANDER = altSettings.getProperty("CommanderFC", 30);
		Config.RIFT_ENTER_COST_HERO = altSettings.getProperty("HeroFC", 33);
		Config.ALLOW_CLANSKILLS = altSettings.getProperty("AllowClanSkills", true);
		Config.ALLOW_LEARN_TRANS_SKILLS_WO_QUEST = altSettings.getProperty("AllowLearnTransSkillsWOQuest", false);
		Config.PARTY_LEADER_ONLY_CAN_INVITE = altSettings.getProperty("PartyLeaderOnlyCanInvite", true);
		Config.ALLOW_TALK_WHILE_SITTING = altSettings.getProperty("AllowTalkWhileSitting", true);
		Config.ALLOW_NOBLE_TP_TO_ALL = altSettings.getProperty("AllowNobleTPToAll", false);

		Config.CLANHALL_BUFFTIME_MODIFIER = altSettings.getProperty("ClanHallBuffTimeModifier", 1.0);
		Config.SONGDANCETIME_MODIFIER = altSettings.getProperty("SongDanceTimeModifier", 1.0);
		Config.MAXLOAD_MODIFIER = altSettings.getProperty("MaxLoadModifier", 1.0);
		Config.GATEKEEPER_MODIFIER = altSettings.getProperty("GkCostMultiplier", 1.0);
		Config.GATEKEEPER_FREE = altSettings.getProperty("GkFree", 40);
		Config.CRUMA_GATEKEEPER_LVL = altSettings.getProperty("GkCruma", 65);
		Config.ALT_IMPROVED_PETS_LIMITED_USE = altSettings.getProperty("ImprovedPetsLimitedUse", false);

		Config.ALT_CHAMPION_CHANCE1 = altSettings.getProperty("AltChampionChance1", 0.);
		Config.ALT_CHAMPION_CHANCE2 = altSettings.getProperty("AltChampionChance2", 0.);
		Config.ALT_CHAMPION_CAN_BE_AGGRO = altSettings.getProperty("AltChampionAggro", false);
		Config.ALT_CHAMPION_CAN_BE_SOCIAL = altSettings.getProperty("AltChampionSocial", false);
		Config.ALT_CHAMPION_DROP_HERBS = altSettings.getProperty("AltChampionDropHerbs", false);
		Config.ALT_SHOW_MONSTERS_AGRESSION = altSettings.getProperty("AltShowMonstersAgression", false);
		Config.ALT_SHOW_MONSTERS_LVL = altSettings.getProperty("AltShowMonstersLvL", false);
		Config.ALT_CHAMPION_TOP_LEVEL = altSettings.getProperty("AltChampionTopLevel", 75);
		Config.ALT_CHAMPION_MIN_LEVEL = altSettings.getProperty("AltChampionMinLevel", 20);

		Config.ALT_VITALITY_ENABLED = altSettings.getProperty("AltVitalityEnabled", true);
		Config.ALT_VITALITY_RATE = altSettings.getProperty("AltVitalityRate", 1.);
		Config.ALT_VITALITY_CONSUME_RATE = altSettings.getProperty("AltVitalityConsumeRate", 1.);
		Config.ALT_VITALITY_RAID_BONUS = altSettings.getProperty("AltVitalityRaidBonus", 2000);

		Config.ALT_PCBANG_POINTS_ENABLED = altSettings.getProperty("AltPcBangPointsEnabled", false);
		Config.ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE = altSettings.getProperty("AltPcBangPointsDoubleChance", 10.);
		Config.ALT_PCBANG_POINTS_BONUS = altSettings.getProperty("AltPcBangPointsBonus", 0);
		Config.ALT_PCBANG_POINTS_DELAY = altSettings.getProperty("AltPcBangPointsDelay", 20);
		Config.ALT_PCBANG_POINTS_MIN_LVL = altSettings.getProperty("AltPcBangPointsMinLvl", 1);

		Config.ALT_MAX_ALLY_SIZE = altSettings.getProperty("AltMaxAllySize", 3);
		Config.ALT_PARTY_DISTRIBUTION_RANGE = altSettings.getProperty("AltPartyDistributionRange", 1500);
		Config.ALT_PARTY_BONUS = altSettings.getProperty("AltPartyBonus", new double[]
		{
			1.00,
			1.10,
			1.20,
			1.30,
			1.40,
			1.50,
			2.00,
			2.10,
			2.20
		});

		Config.ALT_LEVEL_DIFFERENCE_PROTECTION = altSettings.getProperty("LevelDifferenceProtection", -100);

		Config.ALT_ALL_PHYS_SKILLS_OVERHIT = altSettings.getProperty("AltAllPhysSkillsOverhit", true);
		Config.ALT_REMOVE_SKILLS_ON_DELEVEL = altSettings.getProperty("AltRemoveSkillsOnDelevel", true);
		Config.ALT_USE_BOW_REUSE_MODIFIER = altSettings.getProperty("AltUseBowReuseModifier", true);
		Config.ALLOW_CH_DOOR_OPEN_ON_CLICK = altSettings.getProperty("AllowChDoorOpenOnClick", true);
		Config.ALT_CH_ALL_BUFFS = altSettings.getProperty("AltChAllBuffs", false);
		Config.ALT_CH_ALLOW_1H_BUFFS = altSettings.getProperty("AltChAllowHourBuff", false);
		Config.ALT_CH_SIMPLE_DIALOG = altSettings.getProperty("AltChSimpleDialog", false);

		Config.AUGMENTATION_NG_SKILL_CHANCE = altSettings.getProperty("AugmentationNGSkillChance", 15);
		Config.AUGMENTATION_NG_GLOW_CHANCE = altSettings.getProperty("AugmentationNGGlowChance", 0);
		Config.AUGMENTATION_MID_SKILL_CHANCE = altSettings.getProperty("AugmentationMidSkillChance", 30);
		Config.AUGMENTATION_MID_GLOW_CHANCE = altSettings.getProperty("AugmentationMidGlowChance", 40);
		Config.AUGMENTATION_HIGH_SKILL_CHANCE = altSettings.getProperty("AugmentationHighSkillChance", 45);
		Config.AUGMENTATION_HIGH_GLOW_CHANCE = altSettings.getProperty("AugmentationHighGlowChance", 70);
		Config.AUGMENTATION_TOP_SKILL_CHANCE = altSettings.getProperty("AugmentationTopSkillChance", 60);
		Config.AUGMENTATION_TOP_GLOW_CHANCE = altSettings.getProperty("AugmentationTopGlowChance", 100);
		Config.AUGMENTATION_BASESTAT_CHANCE = altSettings.getProperty("AugmentationBaseStatChance", 1);
		Config.AUGMENTATION_ACC_SKILL_CHANCE = altSettings.getProperty("AugmentationAccSkillChance", 10);

		Config.ALT_OPEN_CLOAK_SLOT = altSettings.getProperty("OpenCloakSlot", false);

		Config.FOLLOW_RANGE = altSettings.getProperty("FollowRange", 100);

		Config.ALT_ENABLE_MULTI_PROFA = altSettings.getProperty("AltEnableMultiProfa", false);

		Config.ALT_ITEM_AUCTION_ENABLED = altSettings.getProperty("AltItemAuctionEnabled", true);
		Config.ALT_ITEM_AUCTION_CAN_REBID = altSettings.getProperty("AltItemAuctionCanRebid", false);
		Config.ALT_ITEM_AUCTION_START_ANNOUNCE = altSettings.getProperty("AltItemAuctionAnnounce", true);
		Config.ALT_ITEM_AUCTION_BID_ITEM_ID = altSettings.getProperty("AltItemAuctionBidItemId", 57);
		Config.ALT_ITEM_AUCTION_MAX_BID = altSettings.getProperty("AltItemAuctionMaxBid", 1000000L);
		Config.ALT_ITEM_AUCTION_MAX_CANCEL_TIME_IN_MILLIS = altSettings.getProperty("AltItemAuctionMaxCancelTimeInMillis", 604800000);

		Config.ENABLE_AUCTION_SYSTEM = altSettings.getProperty("EnableAuctionSystem", true);
		Config.AUCTION_FEE = Integer.parseInt(altSettings.getProperty("AuctionFee", "100000"));
		Config.AUCTION_INACTIVITY_DAYS_TO_DELETE = Integer.parseInt(altSettings.getProperty("AuctionInactivityDaysToDelete", "7"));
		Config.ALLOW_AUCTION_OUTSIDE_TOWN = altSettings.getProperty("AuctionOutsideTown", false);
		Config.SECONDS_BETWEEN_ADDING_AUCTIONS = Integer.parseInt(altSettings.getProperty("AuctionAddDelay", "30"));
		Config.AUCTION_PRIVATE_STORE_AUTO_ADDED = altSettings.getProperty("AuctionPrivateStoreAutoAdded", true);

		Config.ALT_FISH_CHAMPIONSHIP_ENABLED = altSettings.getProperty("AltFishChampionshipEnabled", true);
		Config.ALT_FISH_CHAMPIONSHIP_REWARD_ITEM = altSettings.getProperty("AltFishChampionshipRewardItemId", 57);
		Config.ALT_FISH_CHAMPIONSHIP_REWARD_1 = altSettings.getProperty("AltFishChampionshipReward1", 800000);
		Config.ALT_FISH_CHAMPIONSHIP_REWARD_2 = altSettings.getProperty("AltFishChampionshipReward2", 500000);
		Config.ALT_FISH_CHAMPIONSHIP_REWARD_3 = altSettings.getProperty("AltFishChampionshipReward3", 300000);
		Config.ALT_FISH_CHAMPIONSHIP_REWARD_4 = altSettings.getProperty("AltFishChampionshipReward4", 200000);
		Config.ALT_FISH_CHAMPIONSHIP_REWARD_5 = altSettings.getProperty("AltFishChampionshipReward5", 100000);

		Config.ALT_ENABLE_BLOCK_CHECKER_EVENT = altSettings.getProperty("EnableBlockCheckerEvent", true);
		Config.ALT_MIN_BLOCK_CHECKER_TEAM_MEMBERS = Math.min(Math.max(altSettings.getProperty("BlockCheckerMinTeamMembers", 1), 1), 6);
		Config.ALT_RATE_COINS_REWARD_BLOCK_CHECKER = altSettings.getProperty("BlockCheckerRateCoinReward", 1.);

		Config.ALT_HBCE_FAIR_PLAY = altSettings.getProperty("HBCEFairPlay", false);

		Config.ALT_PET_INVENTORY_LIMIT = altSettings.getProperty("AltPetInventoryLimit", 12);
		Config.ALT_CLAN_LEVEL_CREATE = altSettings.getProperty("ClanLevelCreate", 0);
		Config.CLAN_LEVEL_6_COST = altSettings.getProperty("ClanLevel6Cost", 5000);
		Config.CLAN_LEVEL_7_COST = altSettings.getProperty("ClanLevel7Cost", 10000);
		Config.CLAN_LEVEL_8_COST = altSettings.getProperty("ClanLevel8Cost", 20000);
		Config.CLAN_LEVEL_9_COST = altSettings.getProperty("ClanLevel9Cost", 40000);
		Config.CLAN_LEVEL_10_COST = altSettings.getProperty("ClanLevel10Cost", 40000);
		Config.CLAN_LEVEL_11_COST = altSettings.getProperty("ClanLevel11Cost", 75000);
		Config.CLAN_LEVEL_6_REQUIREMEN = altSettings.getProperty("ClanLevel6Requirement", 30);
		Config.CLAN_LEVEL_7_REQUIREMEN = altSettings.getProperty("ClanLevel7Requirement", 50);
		Config.CLAN_LEVEL_8_REQUIREMEN = altSettings.getProperty("ClanLevel8Requirement", 80);
		Config.CLAN_LEVEL_9_REQUIREMEN = altSettings.getProperty("ClanLevel9Requirement", 120);
		Config.CLAN_LEVEL_10_REQUIREMEN = altSettings.getProperty("ClanLevel10Requirement", 140);
		Config.CLAN_LEVEL_11_REQUIREMEN = altSettings.getProperty("ClanLevel11Requirement", 170);
		Config.BLOOD_OATHS = altSettings.getProperty("BloodOaths", 150);
		Config.BLOOD_PLEDGES = altSettings.getProperty("BloodPledges", 5);
		Config.MIN_ACADEM_POINT = altSettings.getProperty("MinAcademPoint", 190);
		Config.MAX_ACADEM_POINT = altSettings.getProperty("MaxAcademPoint", 650);

		Config.HELLBOUND_LEVEL = altSettings.getProperty("HellboundLevel", 0);

		Config.CLAN_LEAVE_PENALTY = altSettings.getProperty("ClanLeavePenalty", 24);
		Config.ALLY_LEAVE_PENALTY = altSettings.getProperty("AllyLeavePenalty", 24);
		Config.DISSOLVED_ALLY_PENALTY = altSettings.getProperty("DissolveAllyPenalty", 24);
		Config.DISSOLVED_CLAN_PENALTY = altSettings.getProperty("DissolvedClanPenalty", 24);
		Config.CLAN_DISBAND_TIME = altSettings.getProperty("DisbanClanTime", 48);

		Config.SIEGE_PVP_COUNT = altSettings.getProperty("SiegePvpCount", false);
		Config.ZONE_PVP_COUNT = altSettings.getProperty("ZonePvpCount", false);
		Config.EPIC_EXPERTISE_PENALTY = altSettings.getProperty("EpicExpertisePenalty", true);
		Config.EXPERTISE_PENALTY = altSettings.getProperty("ExpertisePenalty", true);
		Config.ALT_MUSIC_LIMIT = altSettings.getProperty("MusicLimit", 12);
		Config.ALT_DEBUFF_LIMIT = altSettings.getProperty("DebuffLimit", 8);
		Config.ALT_TRIGGER_LIMIT = altSettings.getProperty("TriggerLimit", 12);
		Config.ENABLE_MODIFY_SKILL_DURATION = altSettings.getProperty("EnableSkillDuration", false);
		if (Config.ENABLE_MODIFY_SKILL_DURATION)
		{
			String[] propertySplit = altSettings.getProperty("SkillDurationList", "").split(";");
			SKILL_DURATION_LIST = new TIntIntHashMap(propertySplit.length);
			for (String skill : propertySplit)
			{
				String[] skillSplit = skill.split(",");
				if (skillSplit.length != 2)
				{
					_log.warn("[SkillDurationList]: invalid config property -> SkillDurationList \"" + skill + "\"");
				}
				else
				{
					try
					{
						SKILL_DURATION_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
					}
					catch (NumberFormatException nfe)
					{
						if (!skill.isEmpty())
						{
							_log.warn("[SkillDurationList]: invalid config property -> SkillList \"" + skillSplit[0] + "\"" + skillSplit[1]);
						}
					}
				}
			}
		}
		Config.ALT_TIME_MODE_SKILL_DURATION = altSettings.getProperty("AltTimeModeSkillDuration", false);

		Config.ANCIENT_HERB_SPAWN_RADIUS = altSettings.getProperty("AncientHerbSpawnRadius", 600);
		Config.ANCIENT_HERB_SPAWN_CHANCE = altSettings.getProperty("AncientHerbSpawnChance", 3);
		Config.ANCIENT_HERB_SPAWN_COUNT = altSettings.getProperty("AncientHerbSpawnCount", 5);
		Config.ANCIENT_HERB_RESPAWN_TIME = altSettings.getProperty("AncientHerbRespawnTime", 60) * 1000;
		Config.ANCIENT_HERB_DESPAWN_TIME = altSettings.getProperty("AncientHerbDespawnTime", 60) * 1000;
		String[] locs = altSettings.getProperty("AncientHerbSpawnPoints", "").split(";");
		if (locs != null)
		{
			for (String string : locs)
			{
				if (string != null)
				{
					String[] cords = string.split(",");
					int x = Integer.parseInt(cords[0]);
					int y = Integer.parseInt(cords[1]);
					int z = Integer.parseInt(cords[2]);
					Config.HEIN_FIELDS_LOCATIONS.add(new Location(x, y, z));
				}
			}
		}
	}

	public static final String PVP_MOD_CONFIG_FILE = "config/mod/PvPmod.ini";
	public static boolean SPAWN_CHAR;
	public static int SPAWN_X;
	public static int SPAWN_Y;
	public static int SPAWN_Z;

	public static boolean CONSUMABLE_SHOT;
	public static boolean CONSUMABLE_ARROW;

	// -------------------------------------------------------------------------------------------------------
	// PvP MOD
	// -------------------------------------------------------------------------------------------------------
	public static int ATT_MOD_ARMOR;
	public static int ATT_MOD_WEAPON;
	public static int ATT_MOD_WEAPON1;
	public static int ATT_MOD_MAX_ARMOR;
	public static int ATT_MOD_MAX_WEAPON;

	public static int HENNA_STATS;
	public static boolean ENEBLE_TITLE_COLOR_MOD;
	public static String TYPE_TITLE_COLOR_MOD;
	public static int COUNT_TITLE_1;
	public static int TITLE_COLOR_1;
	public static int COUNT_TITLE_2;
	public static int TITLE_COLOR_2;
	public static int COUNT_TITLE_3;
	public static int TITLE_COLOR_3;
	public static int COUNT_TITLE_4;
	public static int TITLE_COLOR_4;
	public static int COUNT_TITLE_5;
	public static int TITLE_COLOR_5;
	public static boolean ENEBLE_NAME_COLOR_MOD;
	public static String TYPE_NAME_COLOR_MOD;
	public static int COUNT_NAME_1;
	public static int NAME_COLOR_1;
	public static int COUNT_NAME_2;
	public static int NAME_COLOR_2;
	public static int COUNT_NAME_3;
	public static int NAME_COLOR_3;
	public static int COUNT_NAME_4;
	public static int NAME_COLOR_4;
	public static int COUNT_NAME_5;
	public static int NAME_COLOR_5;
	// Killing Spree
	public static boolean KILLING_SPREE_ENABLED;
	public static Map<Integer, String> KILLING_SPREE_ANNOUNCEMENTS;
	public static Map<Integer, String> KILLING_SPREE_COLORS;
	// PvP Configs
	public static boolean NEW_CHAR_IS_NOBLE;
	public static boolean NEW_CHAR_IS_HERO;
	public static boolean ANNOUNCE_SPAWN_RB;

	public static boolean SPAWN_CITIES_TREE;
	public static boolean SPAWN_NPC_BUFFER;
	public static int MAX_PARTY_SIZE;
	public static boolean SPAWN_scrubwoman;
	public static boolean ADEPT_ENABLE;

	public static void loadPvPmodConfig()
	{
		final ExProperties PvPmodConfig = load(PVP_MOD_CONFIG_FILE);

		// PVP Server system
		Config.CONSUMABLE_SHOT = PvPmodConfig.getProperty("ConsumableShot", true);
		Config.CONSUMABLE_ARROW = PvPmodConfig.getProperty("ConsumableArrow", true);

		Config.ATT_MOD_ARMOR = PvPmodConfig.getProperty("att_mod_Armor", 6);
		Config.ATT_MOD_WEAPON = PvPmodConfig.getProperty("att_mod_Weapon", 5);
		Config.ATT_MOD_WEAPON1 = PvPmodConfig.getProperty("att_mod_Weapon1", 20);

		Config.ATT_MOD_MAX_ARMOR = PvPmodConfig.getProperty("att_mod_max_armor", 60);
		Config.ATT_MOD_MAX_WEAPON = PvPmodConfig.getProperty("att_mod_max_weapon", 150);

		// by claww
		Config.HENNA_STATS = PvPmodConfig.getProperty("HennaStats", 5);
		Config.ENEBLE_TITLE_COLOR_MOD = PvPmodConfig.getProperty("EnebleTitleColorMod", false);
		Config.TYPE_TITLE_COLOR_MOD = PvPmodConfig.getProperty("TypeTitleColorMod", "PvP");
		Config.COUNT_TITLE_1 = PvPmodConfig.getProperty("CountTitle_1", 50);
		Config.TITLE_COLOR_1 = Integer.decode("0x" + PvPmodConfig.getProperty("TitleColor_1", "FFFFFF"));
		Config.COUNT_TITLE_2 = PvPmodConfig.getProperty("CountTitle_2", 100);
		Config.TITLE_COLOR_2 = Integer.decode("0x" + PvPmodConfig.getProperty("TitleColor_2", "FFFFFF"));
		Config.COUNT_TITLE_3 = PvPmodConfig.getProperty("CountTitle_3", 250);
		Config.TITLE_COLOR_3 = Integer.decode("0x" + PvPmodConfig.getProperty("TitleColor_3", "FFFFFF"));
		Config.COUNT_TITLE_4 = PvPmodConfig.getProperty("CountTitle_4", 500);
		Config.TITLE_COLOR_4 = Integer.decode("0x" + PvPmodConfig.getProperty("TitleColor_4", "FFFFFF"));
		Config.COUNT_TITLE_5 = PvPmodConfig.getProperty("CountTitle_5", 1000);
		Config.TITLE_COLOR_5 = Integer.decode("0x" + PvPmodConfig.getProperty("TitleColor_5", "FFFFFF"));
		Config.ENEBLE_NAME_COLOR_MOD = PvPmodConfig.getProperty("EnebleNameColorMod", false);
		Config.TYPE_NAME_COLOR_MOD = PvPmodConfig.getProperty("TypeNameColorMod", "Pk");
		Config.COUNT_NAME_1 = PvPmodConfig.getProperty("CountName_1", 50);
		Config.NAME_COLOR_1 = Integer.decode("0x" + PvPmodConfig.getProperty("NameColor_1", "FFFFFF"));
		Config.COUNT_NAME_2 = PvPmodConfig.getProperty("CountName_2", 100);
		Config.NAME_COLOR_2 = Integer.decode("0x" + PvPmodConfig.getProperty("NameColor_2", "FFFFFF"));
		Config.COUNT_NAME_3 = PvPmodConfig.getProperty("CountName_3", 250);
		Config.NAME_COLOR_3 = Integer.decode("0x" + PvPmodConfig.getProperty("NameColor_3", "FFFFFF"));
		Config.COUNT_NAME_4 = PvPmodConfig.getProperty("CountName_4", 500);
		Config.NAME_COLOR_4 = Integer.decode("0x" + PvPmodConfig.getProperty("NameColor_4", "FFFFFF"));
		Config.COUNT_NAME_5 = PvPmodConfig.getProperty("CountName_5", 1000);
		Config.NAME_COLOR_5 = Integer.decode("0x" + PvPmodConfig.getProperty("NameColor_5", "FFFFFF"));

		// by claww
		Config.NEW_CHAR_IS_NOBLE = PvPmodConfig.getProperty("NewCharIsNoble", false);
		Config.NEW_CHAR_IS_HERO = PvPmodConfig.getProperty("NewCharIsHero", false);
		Config.ANNOUNCE_SPAWN_RB = PvPmodConfig.getProperty("AnnounceToSpawnRb", false);

		Config.SPAWN_CHAR = PvPmodConfig.getProperty("CustomSpawn", false);
		Config.SPAWN_X = PvPmodConfig.getProperty("SpawnX", 1);
		Config.SPAWN_Y = PvPmodConfig.getProperty("SpawnY", 1);
		Config.SPAWN_Z = PvPmodConfig.getProperty("SpawnZ", 1);

		Config.ADEPT_ENABLE = PvPmodConfig.getProperty("ADEPT_ENABLE", true);

		Config.SPAWN_CITIES_TREE = PvPmodConfig.getProperty("SPAWN_CITIES_TREE", true);
		Config.SPAWN_NPC_BUFFER = PvPmodConfig.getProperty("SPAWN_NPC_BUFFER", true);
		Config.SPAWN_scrubwoman = PvPmodConfig.getProperty("SPAWN_scrubwoman", true);
		Config.MAX_PARTY_SIZE = PvPmodConfig.getProperty("MaxPartySize", 9);

		Config.KILLING_SPREE_ENABLED = PvPmodConfig.getProperty("KillingSpreeEnabled", false);
		Config.KILLING_SPREE_ANNOUNCEMENTS = new HashMap<Integer, String>();
		String[] split = PvPmodConfig.getProperty("KillingSpreeAnnouncements", "").split(";");
		if (!split[0].isEmpty())
		{
			for (String ps : split)
			{
				final String[] pvp = ps.split(",");
				if (pvp.length != 2)
				{
					_log.error("[KillingSpreeAnnouncements]: invalid config property -> KillingSpree \"" + ps + "\"");
				}
				else
				{
					try
					{
						KILLING_SPREE_ANNOUNCEMENTS.put(Integer.parseInt(pvp[0]), pvp[1]);
					}
					catch (final NumberFormatException nfe)
					{
						nfe.printStackTrace();
						if (!ps.equals(""))
						{
							_log.error("[KillingSpreeAnnouncements]: invalid config property -> KillingSpree \"" + Integer.parseInt(pvp[0]) + "\"" + pvp[1]);
						}
					}
				}
			}
		}
		Config.KILLING_SPREE_COLORS = new HashMap<Integer, String>();
		split = PvPmodConfig.getProperty("KillingSpreeColors", "").split(";");
		if (!split[0].isEmpty())
		{
			for (String ps : split)
			{
				final String[] pvp = ps.split(",");
				if (pvp.length != 2)
				{
					_log.error("[KillingSpreeColors]: invalid config property -> KillingSpree \"" + ps + "\"");
				}
				else
				{
					try
					{
						KILLING_SPREE_COLORS.put(Integer.parseInt(pvp[0]), pvp[1]);
					}
					catch (final NumberFormatException nfe)
					{
						nfe.printStackTrace();
						if (!ps.equals(""))
						{
							_log.error("[KillingSpreeColors]: invalid config property -> KillingSpree \"" + Integer.parseInt(pvp[0]) + "\"" + pvp[1]);
						}
					}
				}
			}
		}
	}

	public static final String SERVICES_FILE = "config/services/services.ini";
	public static int _coinID;
	public static boolean ALLOW_UPDATE_ANNOUNCER;
	public static boolean ENTER_WORLD_ANNOUNCEMENTS_HERO_LOGIN;
	public static boolean ENTER_WORLD_ANNOUNCEMENTS_LORD_LOGIN;

	public static boolean SERVICES_DELEVEL_ENABLED;
	public static boolean ALLOW_MAIL_OPTION;
	public static int SERVICES_DELEVEL_ITEM;
	public static int SERVICES_DELEVEL_COUNT;
	public static int SERVICES_DELEVEL_MIN_LEVEL;

	public static boolean SERVICES_CHANGE_NICK_ENABLED2;
	public static boolean SERVICES_CHANGE_NICK_ALLOW_SYMBOL2;
	public static int SERVICES_CHANGE_NICK_PRICE2;
	public static int SERVICES_CHANGE_NICK_ITEM2;

	public static boolean SERVICES_CHANGE_CLAN_NAME_ENABLED2;
	public static int SERVICES_CHANGE_CLAN_NAME_PRICE2;
	public static int SERVICES_CHANGE_CLAN_NAME_ITEM2;

	public static boolean SERVICES_CHANGE_PET_NAME_ENABLED;
	public static int SERVICES_CHANGE_PET_NAME_PRICE;
	public static int SERVICES_CHANGE_PET_NAME_ITEM;

	public static boolean SERVICES_EXCHANGE_BABY_PET_ENABLED;
	public static int SERVICES_EXCHANGE_BABY_PET_PRICE;
	public static int SERVICES_EXCHANGE_BABY_PET_ITEM;

	public static boolean SERVICES_CHANGE_SEX_ENABLED;
	public static int SERVICES_CHANGE_SEX_PRICE;
	public static int SERVICES_CHANGE_SEX_ITEM;

	public static boolean SERVICES_CHANGE_BASE_ENABLED;
	public static int SERVICES_CHANGE_BASE_PRICE;
	public static int SERVICES_CHANGE_BASE_ITEM;

	public static boolean SERVICES_SEPARATE_SUB_ENABLED;
	public static int SERVICES_SEPARATE_SUB_PRICE;
	public static int SERVICES_SEPARATE_SUB_ITEM;

	public static boolean SERVICES_CHANGE_NICK_COLOR_ENABLED;
	public static int SERVICES_CHANGE_NICK_COLOR_PRICE;
	public static int SERVICES_CHANGE_NICK_COLOR_ITEM;
	public static String[] SERVICES_CHANGE_NICK_COLOR_LIST;

	public static boolean SERVICES_BASH_ENABLED;
	public static boolean SERVICES_BASH_SKIP_DOWNLOAD;
	public static int SERVICES_BASH_RELOAD_TIME;

	public static boolean SERVICES_HERO_SELL_ENABLED;
	public static int[] SERVICES_HERO_SELL_DAY;
	public static int[] SERVICES_HERO_SELL_PRICE;
	public static int[] SERVICES_HERO_SELL_ITEM;
	public static boolean SERVICES_HERO_SELL_CHAT;
	public static boolean SERVICES_HERO_SELL_SKILL;
	public static boolean SERVICES_HERO_SELL_ITEMS;

	public static boolean SERVICES_WASH_PK_ENABLED;
	public static int SERVICES_WASH_PK_ITEM;
	public static int SERVICES_WASH_PK_PRICE;
	public static int SERVICES_WASH_KARMA_ITEM;
	public static int SERVICES_WASH_KARMA_PRICE;
	// Service PK Clear from community board.
	public static int SERVICES_CLEAR_PK_PRICE;
	public static int SERVICES_CLEAR_PK_PRICE_ITEM_ID;
	public static int SERVICES_CLEAR_PK_COUNT;

	public static boolean SERVICES_TRANSFER_WEAPON_ENABLED;
	public static int SERVICES_TRANSFER_WEAPON_ITEM;
	public static int SERVICES_TRANSFER_WEAPON_PRICE;

	public static boolean SERVICES_EXPAND_INVENTORY_ENABLED;
	public static int SERVICES_EXPAND_INVENTORY_PRICE;
	public static int SERVICES_EXPAND_INVENTORY_ITEM;
	public static int SERVICES_EXPAND_INVENTORY_MAX;

	public static boolean SERVICES_EXPAND_WAREHOUSE_ENABLED;
	public static int SERVICES_EXPAND_WAREHOUSE_PRICE;
	public static int SERVICES_EXPAND_WAREHOUSE_ITEM;

	public static boolean SERVICES_EXPAND_CWH_ENABLED;
	public static int SERVICES_EXPAND_CWH_PRICE;
	public static int SERVICES_EXPAND_CWH_ITEM;

	public static String SERVICES_SELLPETS;

	public static boolean SERVICES_OFFLINE_TRADE_ALLOW;
	public static boolean SERVICES_OFFLINE_TRADE_ALLOW_OFFSHORE;
	public static int SERVICES_OFFLINE_TRADE_MIN_LEVEL;
	public static int SERVICES_OFFLINE_TRADE_NAME_COLOR;
	public static int SERVICES_OFFLINE_TRADE_PRICE;
	public static int SERVICES_OFFLINE_TRADE_PRICE_ITEM;
	public static long SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK;
	public static boolean SERVICES_OFFLINE_TRADE_RESTORE_AFTER_RESTART;
	public static boolean SERVICES_GIRAN_HARBOR_ENABLED;
	public static boolean SERVICES_PARNASSUS_ENABLED;
	public static boolean SERVICES_PARNASSUS_NOTAX;
	public static long SERVICES_PARNASSUS_PRICE;

	public static boolean SERVICES_ALLOW_LOTTERY;
	public static int SERVICES_LOTTERY_PRIZE;
	public static int SERVICES_ALT_LOTTERY_PRICE;
	public static int SERVICES_LOTTERY_TICKET_PRICE;
	public static double SERVICES_LOTTERY_5_NUMBER_RATE;
	public static double SERVICES_LOTTERY_4_NUMBER_RATE;
	public static double SERVICES_LOTTERY_3_NUMBER_RATE;
	public static int SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE;
	public static boolean SERVICES_RIDE_HIRE_ENABLED;

	public static boolean SERVICES_CHANGE_Title_COLOR_ENABLED;
	public static int SERVICES_CHANGE_Title_COLOR_PRICE;
	public static int SERVICES_CHANGE_Title_COLOR_ITEM;
	public static String[] SERVICES_CHANGE_Title_COLOR_LIST;

	// Clan Promotion npc
	public static boolean SERVICES_CLAN_PROMOTION_ENABLE;
	public static int SERVICES_CLAN_PROMOTION_MAX_LEVEL;
	public static int SERVICES_CLAN_PROMOTION_MIN_ONLINE;
	public static int SERVICES_CLAN_PROMOTION_ITEM;
	public static int SERVICES_CLAN_PROMOTION_ITEM_COUNT;
	public static int SERVICES_CLAN_PROMOTION_SET_LEVEL;
	public static int SERVICES_CLAN_PROMOTION_ADD_REP;
	public static boolean SERVICE_CLAN_PRMOTION_ADD_EGGS;
	public static String[] CLAN_PROMOTION_CLAN_EGGS;

	public static boolean SERVICES_NO_TRADE_ONLY_OFFLINE;
	public static boolean SERVICES_NO_TRADE_BLOCK_ZONE;
	public static double SERVICES_TRADE_TAX;
	public static double SERVICES_OFFSHORE_TRADE_TAX;
	public static boolean SERVICES_OFFSHORE_NO_CASTLE_TAX;
	public static boolean SERVICES_TRADE_TAX_ONLY_OFFLINE;
	public static boolean SERVICES_TRADE_ONLY_FAR;
	public static int SERVICES_TRADE_RADIUS;
	public static int SERVICES_TRADE_MIN_LEVEL;

	public static boolean SERVICES_ALLOW_ROULETTE;
	public static long SERVICES_ROULETTE_MIN_BET;
	public static long SERVICES_ROULETTE_MAX_BET;

	public static boolean SERVICES_ENABLE_NO_CARRIER;
	public static int SERVICES_NO_CARRIER_DEFAULT_TIME;
	public static int SERVICES_NO_CARRIER_MAX_TIME;
	public static int SERVICES_NO_CARRIER_MIN_TIME;

	public static boolean SERVICES_PK_PVP_KILL_ENABLE;
	public static int SERVICES_PVP_KILL_REWARD_ITEM;
	public static long SERVICES_PVP_KILL_REWARD_COUNT;
	public static int SERVICES_PK_KILL_REWARD_ITEM;
	public static long SERVICES_PK_KILL_REWARD_COUNT;
	public static boolean SERVICES_PK_PVP_TIE_IF_SAME_IP;

	// Away Manager
	public static boolean ALLOW_AWAY_STATUS;
	public static boolean AWAY_ONLY_FOR_PREMIUM;
	public static int AWAY_TIMER;
	public static int BACK_TIMER;
	public static int AWAY_TITLE_COLOR;
	public static boolean AWAY_PLAYER_TAKE_AGGRO;
	public static boolean AWAY_PEACE_ZONE;
	// Announce PK/PvP
	public static boolean SERVICES_ANNOUNCE_PK_ENABLED;
	public static boolean SERVICES_ANNOUNCE_PVP_ENABLED;
	public static boolean ITEM_BROKER_ITEM_SEARCH;

	/* Password changer */
	public static boolean SERVICES_CHANGE_PASSWORD;
	public static int PASSWORD_PAY_ID;
	public static long PASSWORD_PAY_COUNT;
	public static String APASSWD_TEMPLATE;
	public static boolean ALLOW_EVENT_GATEKEEPER;
	public static boolean SERVICES_LVL_ENABLED;
	public static int SERVICES_LVL_UP_MAX;
	public static int SERVICES_LVL_UP_PRICE;
	public static int SERVICES_LVL_UP_ITEM;
	public static int SERVICES_LVL_DOWN_MAX;
	public static int SERVICES_LVL_DOWN_PRICE;
	public static int SERVICES_LVL_DOWN_ITEM;

	public static boolean ALLOW_MULTILANG_GATEKEEPER;

	/* Fake List */
	public static boolean ALLOW_FAKE_PLAYERS;
	public static boolean FAKE_PLAYERS_SIT;
	public static int FAKE_PLAYERS_PERCENT;
	public static boolean ALLOW_ONLINE_PARSE;
	public static int FIRST_UPDATE;
	public static int DELAY_UPDATE;

	/* Refferal System */
	public static boolean ALLOW_REFFERAL_SYSTEM;
	public static int REF_SAVE_INTERVAL;
	public static int MAX_REFFERALS_PER_CHAR;
	public static int MIN_ONLINE_TIME;
	public static int MIN_REFF_LEVEL;
	public static double REF_PERCENT_GIVE;
	public static List<Integer> ITEM_LIST = new ArrayList<Integer>();

	public static int SERVICES_HAIR_CHANGE_ITEM_ID;
	public static int SERVICES_HAIR_CHANGE_COUNT;
	public static int SERVICES_CLEAR_INSTANCE_PRICE_ID;
	public static int SERVICES_CLEAR_INSTANCE_PRICE_COUNT;

	public static void loadServicesSettings()
	{
		final ExProperties servicesSettings = load(SERVICES_FILE);

		Config._coinID = servicesSettings.getProperty("Id_Item_Mall", 57);
		Config.ENTER_WORLD_ANNOUNCEMENTS_HERO_LOGIN = servicesSettings.getProperty("AnnounceHero", false);
		Config.ENTER_WORLD_ANNOUNCEMENTS_LORD_LOGIN = servicesSettings.getProperty("AnnounceLord", false);
		Config.SERVICES_DELEVEL_ENABLED = servicesSettings.getProperty("AllowDelevel", false);
		Config.SERVICES_DELEVEL_ITEM = servicesSettings.getProperty("DelevelItem", 57);
		Config.SERVICES_DELEVEL_COUNT = servicesSettings.getProperty("DelevelCount", 1000);
		Config.SERVICES_DELEVEL_MIN_LEVEL = servicesSettings.getProperty("DelevelMinLevel", 1);
		Config.ALLOW_MAIL_OPTION = servicesSettings.getProperty("AllowMailOption", false);

		for (int id : servicesSettings.getProperty("AllowClassMasters", ArrayUtils.EMPTY_INT_ARRAY))
		{
			if (id != 0)
			{
				Config.ALLOW_CLASS_MASTERS_LIST.add(id);
			}
		}

		Config.CLASS_MASTERS_PRICE = servicesSettings.getProperty("ClassMastersPrice", "0,0,0");
		if (Config.CLASS_MASTERS_PRICE.length() >= 5)
		{
			int level = 1;
			for (String id : CLASS_MASTERS_PRICE.split(","))
			{
				Config.CLASS_MASTERS_PRICE_LIST[level] = Integer.parseInt(id);
				level++;
			}
		}
		Config.SERVICES_RIDE_HIRE_ENABLED = servicesSettings.getProperty("RideHireEnabled", false);
		Config.CLASS_MASTERS_PRICE_ITEM = servicesSettings.getProperty("ClassMastersPriceItem", 57);

		Config.SERVICES_CHANGE_NICK_ALLOW_SYMBOL2 = servicesSettings.getProperty("NickChangeAllowSimbol2", false);
		Config.SERVICES_CHANGE_NICK_ENABLED2 = servicesSettings.getProperty("NickChangeEnabled2", false);
		Config.SERVICES_CHANGE_NICK_PRICE2 = servicesSettings.getProperty("NickChangePrice2", 100);
		Config.SERVICES_CHANGE_NICK_ITEM2 = servicesSettings.getProperty("NickChangeItem2", 37000);

		Config.SERVICES_CHANGE_CLAN_NAME_ENABLED2 = servicesSettings.getProperty("ClanNameChangeEnabled2", false);
		Config.SERVICES_CHANGE_CLAN_NAME_PRICE2 = servicesSettings.getProperty("ClanNameChangePrice2", 100);
		Config.SERVICES_CHANGE_CLAN_NAME_ITEM2 = servicesSettings.getProperty("ClanNameChangeItem2", 4037);

		Config.SERVICES_CHANGE_PET_NAME_ENABLED = servicesSettings.getProperty("PetNameChangeEnabled", false);
		Config.SERVICES_CHANGE_PET_NAME_PRICE = servicesSettings.getProperty("PetNameChangePrice", 100);
		Config.SERVICES_CHANGE_PET_NAME_ITEM = servicesSettings.getProperty("PetNameChangeItem", 4037);

		Config.SERVICES_EXCHANGE_BABY_PET_ENABLED = servicesSettings.getProperty("BabyPetExchangeEnabled", false);
		Config.SERVICES_EXCHANGE_BABY_PET_PRICE = servicesSettings.getProperty("BabyPetExchangePrice", 100);
		Config.SERVICES_EXCHANGE_BABY_PET_ITEM = servicesSettings.getProperty("BabyPetExchangeItem", 4037);

		Config.SERVICES_CHANGE_SEX_ENABLED = servicesSettings.getProperty("SexChangeEnabled", false);
		Config.SERVICES_CHANGE_SEX_PRICE = servicesSettings.getProperty("SexChangePrice", 100);
		Config.SERVICES_CHANGE_SEX_ITEM = servicesSettings.getProperty("SexChangeItem", 4037);

		Config.SERVICES_CHANGE_BASE_ENABLED = servicesSettings.getProperty("BaseChangeEnabled", false);
		Config.SERVICES_CHANGE_BASE_PRICE = servicesSettings.getProperty("BaseChangePrice", 100);
		Config.SERVICES_CHANGE_BASE_ITEM = servicesSettings.getProperty("BaseChangeItem", 4037);

		Config.SERVICES_SEPARATE_SUB_ENABLED = servicesSettings.getProperty("SeparateSubEnabled", false);
		Config.SERVICES_SEPARATE_SUB_PRICE = servicesSettings.getProperty("SeparateSubPrice", 100);
		Config.SERVICES_SEPARATE_SUB_ITEM = servicesSettings.getProperty("SeparateSubItem", 4037);

		Config.SERVICES_CHANGE_NICK_COLOR_ENABLED = servicesSettings.getProperty("NickColorChangeEnabled", false);
		Config.SERVICES_CHANGE_NICK_COLOR_PRICE = servicesSettings.getProperty("NickColorChangePrice", 100);
		Config.SERVICES_CHANGE_NICK_COLOR_ITEM = servicesSettings.getProperty("NickColorChangeItem", 4037);
		Config.SERVICES_CHANGE_NICK_COLOR_LIST = servicesSettings.getProperty("NickColorChangeList", new String[]
		{
			"00FF00"
		});

		Config.SERVICES_CHANGE_Title_COLOR_ENABLED = servicesSettings.getProperty("TitleColorChangeEnabled", false);
		Config.SERVICES_CHANGE_Title_COLOR_PRICE = servicesSettings.getProperty("TitleColorChangePrice", 100);
		Config.SERVICES_CHANGE_Title_COLOR_ITEM = servicesSettings.getProperty("TitleColorChangeItem", 4037);
		Config.SERVICES_CHANGE_Title_COLOR_LIST = servicesSettings.getProperty("TitleColorChangeList", new String[]
		{
			"00FF00"
		});

		Config.SERVICES_BASH_ENABLED = servicesSettings.getProperty("BashEnabled", false);
		Config.SERVICES_BASH_SKIP_DOWNLOAD = servicesSettings.getProperty("BashSkipDownload", false);
		Config.SERVICES_BASH_RELOAD_TIME = servicesSettings.getProperty("BashReloadTime", 24);

		Config.SERVICES_HERO_SELL_ENABLED = servicesSettings.getProperty("HeroSellEnabled", false);
		Config.SERVICES_HERO_SELL_DAY = servicesSettings.getProperty("HeroSellDay", new int[]
		{
			30
		});
		Config.SERVICES_HERO_SELL_PRICE = servicesSettings.getProperty("HeroSellPrice", new int[]
		{
			30
		});
		Config.SERVICES_HERO_SELL_ITEM = servicesSettings.getProperty("HeroSellItem", new int[]
		{
			4037
		});
		Config.SERVICES_HERO_SELL_CHAT = servicesSettings.getProperty("HeroChat", false);
		Config.SERVICES_HERO_SELL_SKILL = servicesSettings.getProperty("HeroSkills", false);
		Config.SERVICES_HERO_SELL_ITEMS = servicesSettings.getProperty("HeroItems", false);

		// Clan promotion
		Config.SERVICES_CLAN_PROMOTION_ENABLE = servicesSettings.getProperty("EnableClanPromotion", false);
		Config.SERVICES_CLAN_PROMOTION_MAX_LEVEL = servicesSettings.getProperty("MaxClanLevel", 6);
		Config.SERVICES_CLAN_PROMOTION_MIN_ONLINE = servicesSettings.getProperty("MinOnlineMembers", 10);
		Config.SERVICES_CLAN_PROMOTION_ITEM = servicesSettings.getProperty("ClanPromotionItemId", 57);
		Config.SERVICES_CLAN_PROMOTION_ITEM_COUNT = servicesSettings.getProperty("ClanPromotionItemCOunt", 1000);
		Config.SERVICES_CLAN_PROMOTION_SET_LEVEL = servicesSettings.getProperty("ClanPromotionSetLevel", 5);
		Config.SERVICES_CLAN_PROMOTION_ADD_REP = servicesSettings.getProperty("ClanPromotionAddrep", 0);
		Config.SERVICE_CLAN_PRMOTION_ADD_EGGS = servicesSettings.getProperty("GiveEggsToNewClans", false);
		Config.CLAN_PROMOTION_CLAN_EGGS = servicesSettings.getProperty("ClanEggsToReward", "").replaceAll(" ", "").split(";");

		Config.SERVICES_WASH_PK_ENABLED = servicesSettings.getProperty("WashPkEnabled", false);
		Config.SERVICES_WASH_PK_ITEM = servicesSettings.getProperty("WashPkItem", 4037);
		Config.SERVICES_WASH_PK_PRICE = servicesSettings.getProperty("WashPkPrice", 5);
		Config.SERVICES_WASH_KARMA_ITEM = servicesSettings.getProperty("WashKarmaItem", 4037);
		Config.SERVICES_WASH_KARMA_PRICE = servicesSettings.getProperty("WashKarmaPrice", 5);
		// Service PK Clear from community board
		Config.SERVICES_CLEAR_PK_PRICE = servicesSettings.getProperty("ClearPkPrice", 10000);
		Config.SERVICES_CLEAR_PK_PRICE_ITEM_ID = servicesSettings.getProperty("ClearPkPriceID", 57);
		Config.SERVICES_CLEAR_PK_COUNT = servicesSettings.getProperty("ClearPkCount", 1);

		Config.SERVICES_TRANSFER_WEAPON_ENABLED = servicesSettings.getProperty("TransferWeaponEnabled", false);
		Config.SERVICES_TRANSFER_WEAPON_ITEM = servicesSettings.getProperty("TransferWeaponPriceID", 4037);
		Config.SERVICES_TRANSFER_WEAPON_PRICE = servicesSettings.getProperty("TransferWeaponPriceCount", 5);

		Config.SERVICES_EXPAND_INVENTORY_ENABLED = servicesSettings.getProperty("ExpandInventoryEnabled", false);
		Config.SERVICES_EXPAND_INVENTORY_PRICE = servicesSettings.getProperty("ExpandInventoryPrice", 1000);
		Config.SERVICES_EXPAND_INVENTORY_ITEM = servicesSettings.getProperty("ExpandInventoryItem", 4037);
		Config.SERVICES_EXPAND_INVENTORY_MAX = servicesSettings.getProperty("ExpandInventoryMax", 250);

		Config.SERVICES_EXPAND_WAREHOUSE_ENABLED = servicesSettings.getProperty("ExpandWarehouseEnabled", false);
		Config.SERVICES_EXPAND_WAREHOUSE_PRICE = servicesSettings.getProperty("ExpandWarehousePrice", 1000);
		Config.SERVICES_EXPAND_WAREHOUSE_ITEM = servicesSettings.getProperty("ExpandWarehouseItem", 4037);

		Config.SERVICES_EXPAND_CWH_ENABLED = servicesSettings.getProperty("ExpandCWHEnabled", false);
		Config.SERVICES_EXPAND_CWH_PRICE = servicesSettings.getProperty("ExpandCWHPrice", 1000);
		Config.SERVICES_EXPAND_CWH_ITEM = servicesSettings.getProperty("ExpandCWHItem", 4037);

		Config.SERVICES_SELLPETS = servicesSettings.getProperty("SellPets", "");

		Config.SERVICES_OFFLINE_TRADE_ALLOW = servicesSettings.getProperty("AllowOfflineTrade", false);
		Config.SERVICES_OFFLINE_TRADE_ALLOW_OFFSHORE = servicesSettings.getProperty("AllowOfflineTradeOnlyOffshore", true);
		Config.SERVICES_OFFLINE_TRADE_MIN_LEVEL = servicesSettings.getProperty("OfflineMinLevel", 0);
		Config.SERVICES_OFFLINE_TRADE_NAME_COLOR = Integer.decode("0x" + servicesSettings.getProperty("OfflineTradeNameColor", "B0FFFF"));
		Config.SERVICES_OFFLINE_TRADE_PRICE_ITEM = servicesSettings.getProperty("OfflineTradePriceItem", 0);
		Config.SERVICES_OFFLINE_TRADE_PRICE = servicesSettings.getProperty("OfflineTradePrice", 0);
		Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK = servicesSettings.getProperty("OfflineTradeDaysToKick", 14) * 86400L;
		Config.SERVICES_OFFLINE_TRADE_RESTORE_AFTER_RESTART = servicesSettings.getProperty("OfflineRestoreAfterRestart", true);

		Config.SERVICES_NO_TRADE_ONLY_OFFLINE = servicesSettings.getProperty("NoTradeOnlyOffline", false);
		Config.SERVICES_NO_TRADE_BLOCK_ZONE = servicesSettings.getProperty("NoTradeBlockZone", false);
		Config.SERVICES_TRADE_TAX = servicesSettings.getProperty("TradeTax", 0.0);
		Config.SERVICES_OFFSHORE_TRADE_TAX = servicesSettings.getProperty("OffshoreTradeTax", 0.0);
		Config.SERVICES_TRADE_TAX_ONLY_OFFLINE = servicesSettings.getProperty("TradeTaxOnlyOffline", false);
		Config.SERVICES_OFFSHORE_NO_CASTLE_TAX = servicesSettings.getProperty("NoCastleTaxInOffshore", false);
		Config.SERVICES_TRADE_ONLY_FAR = servicesSettings.getProperty("TradeOnlyFar", false);
		Config.SERVICES_TRADE_MIN_LEVEL = servicesSettings.getProperty("MinLevelForTrade", 0);
		Config.SERVICES_TRADE_RADIUS = servicesSettings.getProperty("TradeRadius", 30);

		Config.SERVICES_GIRAN_HARBOR_ENABLED = servicesSettings.getProperty("GiranHarborZone", false);
		Config.SERVICES_PARNASSUS_ENABLED = servicesSettings.getProperty("ParnassusZone", false);
		Config.SERVICES_PARNASSUS_NOTAX = servicesSettings.getProperty("ParnassusNoTax", false);
		Config.SERVICES_PARNASSUS_PRICE = servicesSettings.getProperty("ParnassusPrice", 500000);

		Config.SERVICES_ALLOW_LOTTERY = servicesSettings.getProperty("AllowLottery", false);
		Config.SERVICES_LOTTERY_PRIZE = servicesSettings.getProperty("LotteryPrize", 50000);
		Config.SERVICES_ALT_LOTTERY_PRICE = servicesSettings.getProperty("AltLotteryPrice", 2000);
		Config.SERVICES_LOTTERY_TICKET_PRICE = servicesSettings.getProperty("LotteryTicketPrice", 2000);
		Config.SERVICES_LOTTERY_5_NUMBER_RATE = servicesSettings.getProperty("Lottery5NumberRate", 0.6);
		Config.SERVICES_LOTTERY_4_NUMBER_RATE = servicesSettings.getProperty("Lottery4NumberRate", 0.4);
		Config.SERVICES_LOTTERY_3_NUMBER_RATE = servicesSettings.getProperty("Lottery3NumberRate", 0.2);
		Config.SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE = servicesSettings.getProperty("Lottery2and1NumberPrize", 200);

		Config.SERVICES_ALLOW_ROULETTE = servicesSettings.getProperty("AllowRoulette", false);
		Config.SERVICES_ROULETTE_MIN_BET = servicesSettings.getProperty("RouletteMinBet", 1L);
		Config.SERVICES_ROULETTE_MAX_BET = servicesSettings.getProperty("RouletteMaxBet", Long.MAX_VALUE);

		Config.SERVICES_ENABLE_NO_CARRIER = servicesSettings.getProperty("EnableNoCarrier", false);
		Config.SERVICES_NO_CARRIER_MIN_TIME = servicesSettings.getProperty("NoCarrierMinTime", 0);
		Config.SERVICES_NO_CARRIER_MAX_TIME = servicesSettings.getProperty("NoCarrierMaxTime", 90);
		Config.SERVICES_NO_CARRIER_DEFAULT_TIME = servicesSettings.getProperty("NoCarrierDefaultTime", 60);

		Config.SERVICES_PK_PVP_KILL_ENABLE = servicesSettings.getProperty("PkPvPKillEnable", false);
		Config.SERVICES_PVP_KILL_REWARD_ITEM = servicesSettings.getProperty("PvPkillRewardItem", 4037);
		Config.SERVICES_PVP_KILL_REWARD_COUNT = servicesSettings.getProperty("PvPKillRewardCount", 1L);
		Config.SERVICES_PK_KILL_REWARD_ITEM = servicesSettings.getProperty("PkkillRewardItem", 4037);
		Config.SERVICES_PK_KILL_REWARD_COUNT = servicesSettings.getProperty("PkKillRewardCount", 1L);
		Config.SERVICES_PK_PVP_TIE_IF_SAME_IP = servicesSettings.getProperty("PkPvPTieifSameIP", true);

		// Away
		Config.ALLOW_AWAY_STATUS = servicesSettings.getProperty("AllowAwayStatus", false);
		Config.AWAY_ONLY_FOR_PREMIUM = servicesSettings.getProperty("AwayOnlyForPremium", true);
		Config.AWAY_PLAYER_TAKE_AGGRO = servicesSettings.getProperty("AwayPlayerTakeAggro", false);
		Config.AWAY_TITLE_COLOR = Integer.decode("0x" + servicesSettings.getProperty("AwayTitleColor", "0000FF")).intValue();
		Config.AWAY_TIMER = servicesSettings.getProperty("AwayTimer", 30);
		Config.BACK_TIMER = servicesSettings.getProperty("BackTimer", 30);
		Config.AWAY_PEACE_ZONE = servicesSettings.getProperty("AwayOnlyInPeaceZone", false);

		// Announce
		Config.SERVICES_ANNOUNCE_PK_ENABLED = servicesSettings.getProperty("AnnouncePK", false);
		Config.SERVICES_ANNOUNCE_PVP_ENABLED = servicesSettings.getProperty("AnnouncePvP", false);

		Config.ITEM_BROKER_ITEM_SEARCH = servicesSettings.getProperty("UseItemBrokerItemSearch", false);

		Config.SERVICES_CHANGE_PASSWORD = servicesSettings.getProperty("ChangePassword", false);
		Config.PASSWORD_PAY_ID = servicesSettings.getProperty("ChangePasswordPayId", 0);
		Config.PASSWORD_PAY_COUNT = servicesSettings.getProperty("ChangePassowrdPayCount", 0);
		Config.APASSWD_TEMPLATE = servicesSettings.getProperty("ApasswdTemplate", "[A-Za-z0-9]{5,16}");

		Config.ALLOW_EVENT_GATEKEEPER = servicesSettings.getProperty("AllowEventGatekeeper", false);
		Config.SERVICES_LVL_ENABLED = servicesSettings.getProperty("LevelChangeEnabled", false);
		Config.SERVICES_LVL_UP_MAX = servicesSettings.getProperty("LevelUPChangeMax", 85);
		Config.SERVICES_LVL_UP_PRICE = servicesSettings.getProperty("LevelUPChangePrice", 1000);
		Config.SERVICES_LVL_UP_ITEM = servicesSettings.getProperty("LevelUPChangeItem", 4037);
		Config.SERVICES_LVL_DOWN_MAX = servicesSettings.getProperty("LevelDownChangeMax", 1);
		Config.SERVICES_LVL_DOWN_PRICE = servicesSettings.getProperty("LevelDownChangePrice", 1000);
		Config.SERVICES_LVL_DOWN_ITEM = servicesSettings.getProperty("LevelDownChangeItem", 4037);

		Config.ALLOW_MULTILANG_GATEKEEPER = servicesSettings.getProperty("AllowMultiLangGatekeeper", false);
		Config.DEFAULT_GK_LANG = servicesSettings.getProperty("DefaultGKLang", "en");
		Config.ALLOW_UPDATE_ANNOUNCER = servicesSettings.getProperty("AllowUpdateAnnouncer", false);
		Config.ALLOW_FAKE_PLAYERS = servicesSettings.getProperty("AllowFakePlayers", false);
		Config.FAKE_PLAYERS_SIT = servicesSettings.getProperty("FakePlayersSit", false);
		Config.FAKE_PLAYERS_PERCENT = servicesSettings.getProperty("FakePlayersPercent", 100);
		Config.ALLOW_ONLINE_PARSE = servicesSettings.getProperty("AllowParsTotalOnline", false);
		Config.FIRST_UPDATE = servicesSettings.getProperty("FirstOnlineUpdate", 1);
		Config.DELAY_UPDATE = servicesSettings.getProperty("OnlineUpdate", 5);

		Config.ALLOW_REFFERAL_SYSTEM = servicesSettings.getProperty("EnableReffSystem", false);
		Config.REF_SAVE_INTERVAL = servicesSettings.getProperty("refferSystemSaveInterval", 1);
		Config.MAX_REFFERALS_PER_CHAR = servicesSettings.getProperty("maxRefferalsPerChar", 1);
		Config.MIN_ONLINE_TIME = servicesSettings.getProperty("MinOnlineTimeForReffering", 30);
		Config.MIN_REFF_LEVEL = servicesSettings.getProperty("MinLevelForReffering", 2);
		Config.REF_PERCENT_GIVE = servicesSettings.getProperty("RefferPercentToGive", 0.1D);

		for (int id : servicesSettings.getProperty("ReffItemsList", ArrayUtils.EMPTY_INT_ARRAY))
		{
			if (id != 0)
			{
				Config.ITEM_LIST.add(Integer.valueOf(id));
			}
		}

		Config.SERVICES_HAIR_CHANGE_ITEM_ID = servicesSettings.getProperty("HairChangeItemID", 4037);
		Config.SERVICES_HAIR_CHANGE_COUNT = servicesSettings.getProperty("HairChangeItemCount", 10);

		Config.SERVICES_CLEAR_INSTANCE_PRICE_ID = servicesSettings.getProperty("ClearInstancePriceID", 57);
		Config.SERVICES_CLEAR_INSTANCE_PRICE_COUNT = servicesSettings.getProperty("ClearInstancePriceCount", 1);

	}

	private static int[][] parseItemsList(final String line)
	{
		final String[] propertySplit = line.split(";");
		if (propertySplit.length == 0)
		{
			return null;
		}
		int i = 0;
		String[] valueSplit;
		final int[][] result = new int[propertySplit.length][];
		for (final String value : propertySplit)
		{
			valueSplit = value.split(",");
			if (valueSplit.length != 2)
			{
				return null;
			}
			result[i] = new int[2];
			try
			{
				result[i][0] = Integer.parseInt(valueSplit[0]);
			}
			catch (final NumberFormatException e)
			{
				e.printStackTrace();
				return null;
			}
			try
			{
				result[i][1] = Integer.parseInt(valueSplit[1]);
			}
			catch (final NumberFormatException e)
			{
				e.printStackTrace();
				return null;
			}
			i++;
		}
		return result;
	}

	public static boolean NOT_USE_USER_VOICED;
	public static boolean ALLOW_TOTAL_ONLINE;
	public static boolean show_rates;

	/* .km-all-to-me */
	public static boolean ENABLE_KM_ALL_TO_ME;

	/* .res */
	public static boolean COMMAND_RES;
	public static int ITEM_ID_RESS;
	public static int PRICE_RESS;
	/* .dressme */
	public static boolean COMMAND_DRESSME_ENABLE;
	/* .pa */
	public static boolean COMMAND_PA;
	/* .loot */
	public static boolean COMMAND_LOOT;
	/* .lock */
	public static boolean SERVICES_LOCK_ACCOUNT_IP;
	/* .farm */
	public static boolean COMMAND_FARM;
	public static int FARM_TELEPORT_ITEM_ID;
	public static int PRICE_FARM;
	public static int FARM_X;
	public static int FARM_Y;
	public static int FARM_Z;
	/* .farm_hard */
	public static boolean COMMAND_FARM_HARD;
	public static int FARM_HARD_TELEPORT_ITEM_ID;
	public static int PRICE_FARM_HARD;
	public static int FARM_HARD_X;
	public static int FARM_HARD_Y;
	public static int FARM_HARD_Z;
	/* .farm_low */
	public static boolean COMMAND_FARM_LOW;
	public static int FARM_LOW_TELEPORT_ITEM_ID;
	public static int PRICE_FARM_LOW;
	public static int FARM_LOW_X;
	public static int FARM_LOW_Y;
	public static int FARM_LOW_Z;
	/* .pvp */
	public static boolean COMMAND_PVP;
	public static int PVP_TELEPORT_ITEM_ID;
	public static int PRICE_PVP;
	public static int PVP_X;
	public static int PVP_Y;
	public static int PVP_Z;
	/* .GoToLeader */
	// public static boolean COMMAND_GoToLeader;
	// public static int PRICE_TELEPORT_CL;
	// public static int GO_TO_CL_ITEM_ID;
	/* .ref : Refference System */
	public static boolean ALLOW_VOICED_COMMANDS;
	// noble
	// public static boolean NOBLE;
	// public static int PRICE_NOBLE;
	// public static int ITEM_NOBLE;
	// CustomSpawnNewChar

	public static boolean ALT_SHOW_SERVER_TIME;
	public static final String COMMANDS_CONFIG_FILE = "config/mod/commands.ini";

	public static void loadCommandssettings()
	{
		final ExProperties CommandsSettings = load(COMMANDS_CONFIG_FILE);

		Config.show_rates = CommandsSettings.getProperty("show_rates", false);
		Config.NOT_USE_USER_VOICED = CommandsSettings.getProperty("NotUsePlayerVoiced", false);

		Config.ENABLE_KM_ALL_TO_ME = CommandsSettings.getProperty("EnableKmAllToMe", false);

		Config.COMMAND_RES = CommandsSettings.getProperty("Command_ress", false);
		Config.ITEM_ID_RESS = CommandsSettings.getProperty("Item_Id_ress", 57);
		Config.PRICE_RESS = CommandsSettings.getProperty("price_ress", 57);

		Config.SERVICES_LOCK_ACCOUNT_IP = CommandsSettings.getProperty("LockAccountIpService", false);
		Config.ALLOW_TOTAL_ONLINE = CommandsSettings.getProperty("AllowVoiceCommandOnline", false);

		Config.COMMAND_FARM = CommandsSettings.getProperty("COMMAND_FARM", false);
		Config.FARM_TELEPORT_ITEM_ID = CommandsSettings.getProperty("FARM_TELEPORT_ITEM_ID", 57);
		Config.PRICE_FARM = CommandsSettings.getProperty("PRICE_FARM", 57);
		Config.FARM_X = CommandsSettings.getProperty("FARM_X", 57);
		Config.FARM_Y = CommandsSettings.getProperty("FARM_Y", 57);
		Config.FARM_Z = CommandsSettings.getProperty("FARM_Z", 57);

		Config.COMMAND_FARM_HARD = CommandsSettings.getProperty("COMMAND_FARM_HARD", false);
		Config.FARM_HARD_TELEPORT_ITEM_ID = CommandsSettings.getProperty("FARM_HARD_TELEPORT_ITEM_ID", 57);
		Config.PRICE_FARM_HARD = CommandsSettings.getProperty("PRICE_FARM_HARD", 57);
		Config.FARM_HARD_X = CommandsSettings.getProperty("FARM_HARD_X", 57);
		Config.FARM_HARD_Y = CommandsSettings.getProperty("FARM_HARD_Y", 57);
		Config.FARM_HARD_Z = CommandsSettings.getProperty("FARM_HARD_Z", 57);

		Config.COMMAND_FARM_LOW = CommandsSettings.getProperty("COMMAND_FARM_LOW", false);
		Config.FARM_LOW_TELEPORT_ITEM_ID = CommandsSettings.getProperty("FARM_LOW_TELEPORT_ITEM_ID", 57);
		Config.PRICE_FARM_LOW = CommandsSettings.getProperty("PRICE_FARM_LOW", 57);
		Config.FARM_LOW_X = CommandsSettings.getProperty("FARM_LOW_X", 57);
		Config.FARM_LOW_Y = CommandsSettings.getProperty("FARM_LOW_Y", 57);
		Config.FARM_LOW_Z = CommandsSettings.getProperty("FARM_LOW_Z", 57);

		Config.COMMAND_PVP = CommandsSettings.getProperty("COMMAND_PVP", false);
		Config.PVP_X = CommandsSettings.getProperty("PVP_X", 0);
		Config.PVP_Y = CommandsSettings.getProperty("PVP_Y", 0);
		Config.PVP_Z = CommandsSettings.getProperty("PVP_Z", 0);
		Config.PVP_TELEPORT_ITEM_ID = CommandsSettings.getProperty("PVP_TELEPORT_ITEM_ID", 57);
		Config.PRICE_PVP = CommandsSettings.getProperty("PRICE_PVP", 57);

		// COMMAND_GoToLeader = CommandsSettings.getProperty("GoToLeader", false);
		// PRICE_TELEPORT_CL = CommandsSettings.getProperty("Price_Teleport", 57);
		// GO_TO_CL_ITEM_ID = CommandsSettings.getProperty("Item_Id_go_to_cl", 57);
		Config.ALLOW_VOICED_COMMANDS = CommandsSettings.getProperty("AllowVoicedCommands", true);

		// NOBLE = CommandsSettings.getProperty("NOBLE", false);
		// ITEM_NOBLE = CommandsSettings.getProperty("Item_Noble", 57);
		// PRICE_NOBLE = CommandsSettings.getProperty("Price_noble", 57);
		Config.ALT_SHOW_SERVER_TIME = CommandsSettings.getProperty("ShowServerTime", false);
		Config.COMMAND_DRESSME_ENABLE = CommandsSettings.getProperty("DressMe", true);
	}

	/** Community Board PVP */
	public static boolean ALLOW_BBS_WAREHOUSE;
	public static boolean BBS_WAREHOUSE_ALLOW_PK;
	public static boolean BBS_PVP_CB_ENABLED;
	public static boolean BBS_PVP_CB_ABNORMAL;
	public static boolean COMMUNITYBOARD_ENABLED;
	public static String BBS_DEFAULT;
	public static String BBS_HOME_DIR;
	public static boolean ALLOW_DROP_CALCULATOR;
	public static int[] DROP_CALCULATOR_DISABLED_TELEPORT;

	public static boolean ALLOW_SENDING_IMAGES;
	public static boolean COMMUNITYBOARD_CLAN_ENABLED;
	public static boolean USE_BBS_PROF_IS_COMBAT;
	public static final String BOARD_MANAGER_CONFIG_FILE = "config/CommunityPvP/board_manager.ini";

	public static void loadCommunityPvPboardsettings()
	{
		final ExProperties CommunityPvPboardSettings = load(BOARD_MANAGER_CONFIG_FILE);

		Config.BBS_PVP_CB_ABNORMAL = CommunityPvPboardSettings.getProperty("BBSPVPAllowAbnormal", false);
		Config.BBS_PVP_CB_ENABLED = CommunityPvPboardSettings.getProperty("BBSPVPEnabled", false);
		Config.COMMUNITYBOARD_ENABLED = CommunityPvPboardSettings.getProperty("AllowCommunityBoard", true);
		Config.BBS_DEFAULT = CommunityPvPboardSettings.getProperty("BBSDefault", "_bbshome");
		Config.BBS_HOME_DIR = CommunityPvPboardSettings.getProperty("BBSHomeDir", "scripts/services/community/");
		Config.ALLOW_BBS_WAREHOUSE = CommunityPvPboardSettings.getProperty("AllowBBSWarehouse", true);
		Config.BBS_WAREHOUSE_ALLOW_PK = CommunityPvPboardSettings.getProperty("BBSWarehouseAllowPK", false);
		Config.ALLOW_DROP_CALCULATOR = CommunityPvPboardSettings.getProperty("AllowDropCalculator", true);
		Config.DROP_CALCULATOR_DISABLED_TELEPORT = CommunityPvPboardSettings.getProperty("DropCalculatorDisabledTeleport", new int[] {});

		Config.ALLOW_SENDING_IMAGES = CommunityPvPboardSettings.getProperty("AllowSendingImages", true);
		Config.COMMUNITYBOARD_CLAN_ENABLED = CommunityPvPboardSettings.getProperty("ClanEnable", false);
		Config.USE_BBS_PROF_IS_COMBAT = CommunityPvPboardSettings.getProperty("NoUserEmCombat", false);
	}

	public static final String BUFFER_MANAGER_CONFIG_FILE = "config/CommunityPvP/buffer_manager.ini";
	/** Settings of CommunityBoard Buffer */
	public static boolean BBS_PVP_BUFFER_ENABLED;
	public static boolean BBS_PVP_BUFER_ONE_BUFF_PET;
	public static long BBS_PVP_BUFFER_ALT_TIME;
	public static int BBS_PVP_BUFFER_PRICE_ITEM;
	public static int BBS_PVP_BUFFER_PRICE_ONE;
	public static int BBS_PVP_BUFFER_BUFFS_PER_PAGE;
	public static int BBS_PVP_BUFFER_BUFFS_PER_SET;
	public static int BBS_PVP_BUFFER_TASK_DELAY;
	public static int BBS_PVP_BUFFER_PRICE_MOD_GRP;
	public static int BBS_PVP_BUFFER_MIN_LVL;
	public static int BBS_PVP_BUFFER_MAX_LVL;
	public static boolean BBS_PVP_BUFFER_ALLOW_SIEGE;
	public static boolean BBS_PVP_BUFFER_ALOWED_INST_BUFF;
	public static boolean BBS_PVP_BUFFER_ALLOW_PVP_FLAG;

	public static double BBS_BUFF_TIME_MOD_SPECIAL;
	public static double BBS_BUFF_TIME_MOD_MUSIC;
	public static double BBS_BUFF_TIME_MOD;
	public static int BBS_BUFF_TIME;
	public static int BBS_BUFF_TIME_SPECIAL;
	public static int BBS_BUFF_TIME_MUSIC;
	public static int BBS_BUFF_ITEM_ID;
	public static int BUFF_PAGE_ROWS;
	public static int MAX_BUFF_PER_SET;
	public static int BBS_BUFF_FREE_LVL;
	public static int BBS_BUFF_ITEM_COUNT;
	public static int MAX_SETS_PER_CHAR;
	public static boolean BUFF_MANUAL_EDIT_SETS;
	public static boolean BBS_BUFF_ALLOW_HEAL;
	public static boolean BBS_BUFF_ALLOW_CANCEL;
	public static int[] BBS_BUFF_IDs;
	public static boolean BBS_BUFF_CURSED;
	public static boolean BBS_BUFF_PK;
	public static boolean BBS_BUFF_LEADER;
	public static boolean BBS_BUFF_NOBLE;
	public static boolean BBS_BUFF_TERITORY;
	public static boolean BBS_BUFF_PEACEZONE_ONLY;
	public static boolean BBS_BUFF_DUEL;
	public static boolean BBS_BUFF_TEMP_ACTION;
	public static boolean BBS_BUFF_CANT_MOVE;
	public static boolean BBS_BUFF_STORE_MODE;
	public static boolean BBS_BUFF_FISHING;
	public static boolean BBS_BUFF_MOUNTED;
	public static boolean BBS_BUFF_VEICHLE;
	public static boolean BBS_BUFF_FLY;
	public static boolean BBS_BUFF_OLY;
	public static boolean BBS_BUFF_ACTION;
	public static boolean BBS_BUFF_DEATH;
	public static boolean BBS_BUFFER_ENABLED;

	public static void loadCommunityPvPbuffersettings()
	{
		final ExProperties CommunityPvPbufferSettings = load(BUFFER_MANAGER_CONFIG_FILE);

		Config.BBS_PVP_BUFFER_ENABLED = CommunityPvPbufferSettings.getProperty("BBSPVPBufferEnabled", false);
		Config.BBS_PVP_BUFFER_ALT_TIME = CommunityPvPbufferSettings.getProperty("BBSPVPBufferTime", 14400) * 1000;
		Config.BBS_PVP_BUFFER_PRICE_ITEM = CommunityPvPbufferSettings.getProperty("BBSPVPBufferPriceItem", 57);
		Config.BBS_PVP_BUFFER_PRICE_ONE = CommunityPvPbufferSettings.getProperty("BBSPVPBufferPriceOne", 1000);
		Config.BBS_PVP_BUFFER_BUFFS_PER_PAGE = CommunityPvPbufferSettings.getProperty("BBSPVPBufferMaxPerPage", 27);
		Config.BBS_PVP_BUFFER_BUFFS_PER_SET = CommunityPvPbufferSettings.getProperty("BBSPVPBufferMaxPerSet", 27);
		Config.BBS_PVP_BUFFER_TASK_DELAY = CommunityPvPbufferSettings.getProperty("BBSPVPBufferTaskDelay", 14400) * 1000;
		Config.BBS_PVP_BUFFER_MIN_LVL = CommunityPvPbufferSettings.getProperty("BBSPVPBufferMinLvl", 1);
		Config.BBS_PVP_BUFFER_MAX_LVL = CommunityPvPbufferSettings.getProperty("BBSPVPBufferMaxLvl", 99);
		Config.BBS_PVP_BUFER_ONE_BUFF_PET = CommunityPvPbufferSettings.getProperty("BBSPVPBufferOneBuffPet", false);
		Config.BBS_PVP_BUFFER_ALLOW_SIEGE = CommunityPvPbufferSettings.getProperty("BBSPVPBufferAllowOnSiege", true);
		Config.BBS_PVP_BUFFER_ALLOW_PVP_FLAG = CommunityPvPbufferSettings.getProperty("BBSPVPBufferAllowOnPvP", true);
		Config.BBS_PVP_BUFFER_ALOWED_INST_BUFF = CommunityPvPbufferSettings.getProperty("BBSPVPBufferAllowInInstance", true);

		Config.BBS_BUFFER_ENABLED = CommunityPvPbufferSettings.getProperty("AllowBBSBuffer", false);
		Config.BBS_BUFF_DEATH = CommunityPvPbufferSettings.getProperty("AllowWhenDead", false);
		Config.BBS_BUFF_ACTION = CommunityPvPbufferSettings.getProperty("AllowWhenInAction", false);
		Config.BBS_BUFF_OLY = CommunityPvPbufferSettings.getProperty("AllowWhenInOlly", false);
		Config.BBS_BUFF_FLY = CommunityPvPbufferSettings.getProperty("AllowWhenInFly", false);
		Config.BBS_BUFF_VEICHLE = CommunityPvPbufferSettings.getProperty("AllowWhenInVeichle", false);
		Config.BBS_BUFF_MOUNTED = CommunityPvPbufferSettings.getProperty("AllowWhenMounted", false);
		Config.BBS_BUFF_CANT_MOVE = CommunityPvPbufferSettings.getProperty("AllowWhenCantMove", false);
		Config.BBS_BUFF_STORE_MODE = CommunityPvPbufferSettings.getProperty("AllowWhenInTrade", false);
		Config.BBS_BUFF_FISHING = CommunityPvPbufferSettings.getProperty("AllowWhenFishing", false);
		Config.BBS_BUFF_TEMP_ACTION = CommunityPvPbufferSettings.getProperty("AllowWhenInTemp", false);
		Config.BBS_BUFF_DUEL = CommunityPvPbufferSettings.getProperty("AllowWhenInDuel", false);
		Config.BBS_BUFF_CURSED = CommunityPvPbufferSettings.getProperty("AllowWhenUseCursed", false);
		Config.BBS_BUFF_PK = CommunityPvPbufferSettings.getProperty("AllowWhenIsPk", false);
		Config.BBS_BUFF_LEADER = CommunityPvPbufferSettings.getProperty("AllowOnlyToClanLeader", false);
		Config.BBS_BUFF_NOBLE = CommunityPvPbufferSettings.getProperty("AllowOnlyToNoble", false);
		Config.BBS_BUFF_TERITORY = CommunityPvPbufferSettings.getProperty("AllowUseInTWPlayer", false);
		Config.BBS_BUFF_PEACEZONE_ONLY = CommunityPvPbufferSettings.getProperty("AllowUseOnlyInPeace", false);
		Config.BBS_BUFF_IDs = CommunityPvPbufferSettings.getProperty("BuffIDs", ArrayUtils.EMPTY_INT_ARRAY);
		Config.BBS_BUFF_ALLOW_CANCEL = CommunityPvPbufferSettings.getProperty("BuffAllowCancel", false);
		Config.BBS_BUFF_ALLOW_HEAL = CommunityPvPbufferSettings.getProperty("BuffAllowHeal", false);
		Config.BUFF_MANUAL_EDIT_SETS = CommunityPvPbufferSettings.getProperty("BuffManualEditSets", false);
		Config.MAX_SETS_PER_CHAR = CommunityPvPbufferSettings.getProperty("MaximumSetsPerChar", 8);
		Config.BBS_BUFF_ITEM_COUNT = CommunityPvPbufferSettings.getProperty("BuffItemCount", 8);
		Config.BBS_BUFF_FREE_LVL = CommunityPvPbufferSettings.getProperty("FreeBuffLevel", 8);
		Config.MAX_BUFF_PER_SET = CommunityPvPbufferSettings.getProperty("MaxBuffsPerSet", 8);
		Config.BUFF_PAGE_ROWS = CommunityPvPbufferSettings.getProperty("BuffsPageRows", 8);
		Config.BBS_BUFF_ITEM_ID = CommunityPvPbufferSettings.getProperty("BuffItemId", 8);
		Config.BBS_BUFF_TIME_MUSIC = CommunityPvPbufferSettings.getProperty("BuffTimeMusic", 8);
		Config.BBS_BUFF_TIME_SPECIAL = CommunityPvPbufferSettings.getProperty("BuffTimeSpecial", 8);
		Config.BBS_BUFF_TIME = CommunityPvPbufferSettings.getProperty("BuffTime", 8);
		Config.BBS_BUFF_TIME_MOD = CommunityPvPbufferSettings.getProperty("BuffTimeMod", 8);
		Config.BBS_BUFF_TIME_MOD_MUSIC = CommunityPvPbufferSettings.getProperty("BuffTimeModMusic", 8);
		Config.BBS_BUFF_TIME_MOD_SPECIAL = CommunityPvPbufferSettings.getProperty("BuffTimeModSpecial", 8);

	}

	public static final String CLASS_MASTER_CONFIG_FILE = "config/CommunityPvP/class_master.ini";
	public static String CLASS_MASTERS_PRICE;
	public static int CLASS_MASTERS_PRICE_ITEM;
	public static List<Integer> ALLOW_CLASS_MASTERS_LIST = new ArrayList<Integer>();
	public static int[] CLASS_MASTERS_PRICE_LIST = new int[4];
	public static boolean BBS_PVP_SUB_MANAGER_ALLOW;
	public static boolean BBS_PVP_SUB_MANAGER_PIACE;

	public static void loadCommunityPvPclasssettings()
	{
		final ExProperties CommunityPvPClassSettings = load(CLASS_MASTER_CONFIG_FILE);

		for (int id : CommunityPvPClassSettings.getProperty("AllowClassMasters", ArrayUtils.EMPTY_INT_ARRAY))
		{
			if (id != 0)
			{
				Config.ALLOW_CLASS_MASTERS_LIST.add(id);
			}
		}

		Config.CLASS_MASTERS_PRICE = CommunityPvPClassSettings.getProperty("ClassMastersPrice", "0,0,0");
		if (Config.CLASS_MASTERS_PRICE.length() >= 5)
		{
			int level = 1;
			for (String id : CLASS_MASTERS_PRICE.split(","))
			{
				Config.CLASS_MASTERS_PRICE_LIST[level] = Integer.parseInt(id);
				level++;
			}
		}
		Config.CLASS_MASTERS_PRICE_ITEM = CommunityPvPClassSettings.getProperty("ClassMastersPriceItem", 57);

		Config.BBS_PVP_SUB_MANAGER_ALLOW = CommunityPvPClassSettings.getProperty("AllowBBSSubManager", false);
		Config.BBS_PVP_SUB_MANAGER_PIACE = CommunityPvPClassSettings.getProperty("AllowBBSSubManagerPiace", false);
	}

	public static final String SHOP_MANAGER_CONFIG_FILE = "config/CommunityPvP/shop_manager.ini";
	public static boolean BBS_PVP_ALLOW_SELL;
	public static boolean BBS_PVP_ALLOW_BUY;
	public static boolean BBS_PVP_ALLOW_AUGMENT;

	public static void loadCommunityPvPshopsettings()
	{
		final ExProperties CommunityPvPshopSettings = load(SHOP_MANAGER_CONFIG_FILE);

		Config.BBS_PVP_ALLOW_BUY = CommunityPvPshopSettings.getProperty("CommunityShopEnable", false);
		Config.BBS_PVP_ALLOW_SELL = CommunityPvPshopSettings.getProperty("CommunitySellEnable", false);
		Config.BBS_PVP_ALLOW_AUGMENT = CommunityPvPshopSettings.getProperty("CommunityAugmentEnable", false);
	}

	public static final String TELEPORT_MANAGER_CONFIG_FILE = "config/CommunityPvP/teleport_manager.ini";
	public static boolean BBS_PVP_TELEPORT_ENABLED;
	public static int BBS_PVP_TELEPORT_POINT_PRICE;
	public static int BBS_PVP_TELEPORT_MAX_POINT_COUNT;

	public static void loadCommunityPvPteleportsettings()
	{
		final ExProperties CommunityPvPteleportsettings = load(TELEPORT_MANAGER_CONFIG_FILE);

		Config.BBS_PVP_TELEPORT_ENABLED = CommunityPvPteleportsettings.getProperty("BBSPVPTeleportEnabled", false);
		Config.BBS_PVP_TELEPORT_POINT_PRICE = CommunityPvPteleportsettings.getProperty("BBSPVPTeleportPointPrice", 200000);
		Config.BBS_PVP_TELEPORT_MAX_POINT_COUNT = CommunityPvPteleportsettings.getProperty("BBSPVPTeleportMaxPointCount", 10);

	}

	public static final String PVP_CONFIG_FILE = "config/pvp.ini";
	/** Karma System Variables */
	public static int KARMA_MIN_KARMA;
	public static int KARMA_SP_DIVIDER;
	public static int KARMA_LOST_BASE;

	/** Player Drop Rate control */
	public static boolean KARMA_DROP_GM;
	public static boolean KARMA_NEEDED_TO_DROP;

	public static int KARMA_DROP_ITEM_LIMIT;

	public static int KARMA_RANDOM_DROP_LOCATION_LIMIT;

	public static double KARMA_DROPCHANCE_BASE;
	public static double KARMA_DROPCHANCE_MOD;
	public static double NORMAL_DROPCHANCE_BASE;
	public static int DROPCHANCE_EQUIPMENT;
	public static int DROPCHANCE_EQUIPPED_WEAPON;
	public static int DROPCHANCE_ITEM;

	public static int MIN_PK_TO_ITEMS_DROP;
	public static boolean DROP_ITEMS_ON_DIE;
	public static boolean DROP_ITEMS_AUGMENTED;

	public static List<Integer> KARMA_LIST_NONDROPPABLE_ITEMS = new ArrayList<Integer>();

	public static int PVP_TIME;

	public static void loadPvPSettings()
	{
		final ExProperties pvpSettings = load(PVP_CONFIG_FILE);

		/* KARMA SYSTEM */
		Config.KARMA_MIN_KARMA = pvpSettings.getProperty("MinKarma", 240);
		Config.KARMA_SP_DIVIDER = pvpSettings.getProperty("SPDivider", 7);
		Config.KARMA_LOST_BASE = pvpSettings.getProperty("BaseKarmaLost", 0);

		Config.KARMA_DROP_GM = pvpSettings.getProperty("CanGMDropEquipment", false);
		Config.KARMA_NEEDED_TO_DROP = pvpSettings.getProperty("KarmaNeededToDrop", true);
		Config.DROP_ITEMS_ON_DIE = pvpSettings.getProperty("DropOnDie", false);
		Config.DROP_ITEMS_AUGMENTED = pvpSettings.getProperty("DropAugmented", false);

		Config.KARMA_DROP_ITEM_LIMIT = pvpSettings.getProperty("MaxItemsDroppable", 10);
		Config.MIN_PK_TO_ITEMS_DROP = pvpSettings.getProperty("MinPKToDropItems", 5);

		Config.KARMA_RANDOM_DROP_LOCATION_LIMIT = pvpSettings.getProperty("MaxDropThrowDistance", 70);

		Config.KARMA_DROPCHANCE_BASE = pvpSettings.getProperty("ChanceOfPKDropBase", 20.);
		Config.KARMA_DROPCHANCE_MOD = pvpSettings.getProperty("ChanceOfPKsDropMod", 1.);
		Config.NORMAL_DROPCHANCE_BASE = pvpSettings.getProperty("ChanceOfNormalDropBase", 1.);
		Config.DROPCHANCE_EQUIPPED_WEAPON = pvpSettings.getProperty("ChanceOfDropWeapon", 3);
		Config.DROPCHANCE_EQUIPMENT = pvpSettings.getProperty("ChanceOfDropEquippment", 17);
		Config.DROPCHANCE_ITEM = pvpSettings.getProperty("ChanceOfDropOther", 80);

		Config.KARMA_LIST_NONDROPPABLE_ITEMS = new ArrayList<Integer>();
		for (int id : pvpSettings.getProperty("ListOfNonDroppableItems", new int[]
		{
			57,
			1147,
			425,
			1146,
			461,
			10,
			2368,
			7,
			6,
			2370,
			2369,
			3500,
			3501,
			3502,
			4422,
			4423,
			4424,
			2375,
			6648,
			6649,
			6650,
			6842,
			6834,
			6835,
			6836,
			6837,
			6838,
			6839,
			6840,
			5575,
			7694,
			6841,
			8181
		}))
		{
			Config.KARMA_LIST_NONDROPPABLE_ITEMS.add(id);
		}

		Config.PVP_TIME = pvpSettings.getProperty("PvPTime", 120000);
	}

	public static final String ZONE_DRAGONVALLEY_FILE = "config/zones/DragonValley.ini";
	public static int NECROMANCER_MS_CHANCE;
	public static double DWARRIOR_MS_CHANCE;
	public static double DHUNTER_MS_CHANCE;
	public static int BDRAKE_MS_CHANCE;
	public static int EDRAKE_MS_CHANCE;

	public static void loadDragonValleyZoneSettings()
	{
		final ExProperties properties = load(ZONE_DRAGONVALLEY_FILE);
		Config.NECROMANCER_MS_CHANCE = properties.getProperty("NecromancerMSChance", 0);
		Config.DWARRIOR_MS_CHANCE = properties.getProperty("DWarriorMSChance", 0.0);
		Config.DHUNTER_MS_CHANCE = properties.getProperty("DHunterMSChance", 0.0);
		Config.BDRAKE_MS_CHANCE = properties.getProperty("BDrakeMSChance", 0);
		Config.EDRAKE_MS_CHANCE = properties.getProperty("EDrakeMSChance", 0);
	}

	public static final String ZONE_LAIROFANTHARAS_FILE = "config/zones/LairOfAntharas.ini";
	public static int BKARIK_D_M_CHANCE;

	public static void loadLairOfAntharasZoneSettings()
	{
		final ExProperties properties = load(ZONE_LAIROFANTHARAS_FILE);
		Config.BKARIK_D_M_CHANCE = properties.getProperty("BKarikDMSChance", 0);
	}

	public static final String AI_CONFIG_FILE = "config/ai.ini";
	/** AI */
	public static boolean ALLOW_NPC_AIS;
	public static int AI_TASK_MANAGER_COUNT;
	public static long AI_TASK_ATTACK_DELAY;
	public static long AI_TASK_ACTIVE_DELAY;
	public static boolean BLOCK_ACTIVE_TASKS;
	public static boolean ALWAYS_TELEPORT_HOME;
	public static boolean RND_WALK;
	public static int RND_WALK_RATE;
	public static int RND_ANIMATION_RATE;

	public static int AGGRO_CHECK_INTERVAL;
	public static long NONAGGRO_TIME_ONTELEPORT;

	/** Maximum range mobs can randomly go from spawn point */
	public static int MAX_DRIFT_RANGE;

	/** Maximum range mobs can pursue agressor from spawn point */
	public static int MAX_PURSUE_RANGE;
	public static int MAX_PURSUE_UNDERGROUND_RANGE;
	public static int MAX_PURSUE_RANGE_RAID;

	public static void loadAISettings()
	{
		final ExProperties aiSettings = load(AI_CONFIG_FILE);

		Config.ALLOW_NPC_AIS = aiSettings.getProperty("AllowNpcAIs", true);
		Config.AI_TASK_MANAGER_COUNT = aiSettings.getProperty("AiTaskManagers", 1);
		Config.AI_TASK_ATTACK_DELAY = aiSettings.getProperty("AiTaskDelay", 1000);
		Config.AI_TASK_ACTIVE_DELAY = aiSettings.getProperty("AiTaskActiveDelay", 1000);
		Config.BLOCK_ACTIVE_TASKS = aiSettings.getProperty("BlockActiveTasks", false);
		Config.ALWAYS_TELEPORT_HOME = aiSettings.getProperty("AlwaysTeleportHome", false);

		Config.RND_WALK = aiSettings.getProperty("RndWalk", true);
		Config.RND_WALK_RATE = aiSettings.getProperty("RndWalkRate", 1);
		Config.RND_ANIMATION_RATE = aiSettings.getProperty("RndAnimationRate", 2);

		Config.AGGRO_CHECK_INTERVAL = aiSettings.getProperty("AggroCheckInterval", 400);
		Config.NONAGGRO_TIME_ONTELEPORT = aiSettings.getProperty("NonAggroTimeOnTeleport", 15000);
		Config.MAX_DRIFT_RANGE = aiSettings.getProperty("MaxDriftRange", 100);
		Config.MAX_PURSUE_RANGE = aiSettings.getProperty("MaxPursueRange", 4000);
		Config.MAX_PURSUE_UNDERGROUND_RANGE = aiSettings.getProperty("MaxPursueUndergoundRange", 2000);
		Config.MAX_PURSUE_RANGE_RAID = aiSettings.getProperty("MaxPursueRangeRaid", 5000);
	}

	/** Geodata config */
	public static int GEO_X_FIRST, GEO_Y_FIRST, GEO_X_LAST, GEO_Y_LAST;
	public static String GEOFILES_PATTERN;
	public static boolean ALLOW_GEODATA;
	public static boolean ALLOW_FALL_FROM_WALLS;
	public static boolean ALLOW_KEYBOARD_MOVE;
	public static boolean COMPACT_GEO;
	public static int CLIENT_Z_SHIFT;
	public static int MAX_Z_DIFF;
	public static int MIN_LAYER_HEIGHT;
	public static int REGION_EDGE_MAX_Z_DIFF;

	/** Geodata (Pathfind) config */
	public static int PATHFIND_BOOST;
	public static int PATHFIND_MAP_MUL;
	public static boolean PATHFIND_DIAGONAL;
	public static boolean PATH_CLEAN;
	public static int PATHFIND_MAX_Z_DIFF;
	public static long PATHFIND_MAX_TIME;
	public static String PATHFIND_BUFFERS;

	public static int GEODATA_SKILL_CHECK_TASK_INTERVAL;
	public static final String GEODATA_CONFIG_FILE = "config/geodata.ini";

	public static void loadGeodataSettings()
	{
		final ExProperties geodataSettings = load(GEODATA_CONFIG_FILE);

		Config.GEO_X_FIRST = geodataSettings.getProperty("GeoFirstX", 11);
		Config.GEO_Y_FIRST = geodataSettings.getProperty("GeoFirstY", 10);
		Config.GEO_X_LAST = geodataSettings.getProperty("GeoLastX", 26);
		Config.GEO_Y_LAST = geodataSettings.getProperty("GeoLastY", 26);

		Config.GEOFILES_PATTERN = geodataSettings.getProperty("GeoFilesPattern", "(\\d{2}_\\d{2})\\.l2j");
		Config.ALLOW_GEODATA = geodataSettings.getProperty("AllowGeodata", true);
		Config.ALLOW_FALL_FROM_WALLS = geodataSettings.getProperty("AllowFallFromWalls", false);
		Config.ALLOW_KEYBOARD_MOVE = geodataSettings.getProperty("AllowMoveWithKeyboard", true);
		Config.COMPACT_GEO = geodataSettings.getProperty("CompactGeoData", false);
		Config.CLIENT_Z_SHIFT = geodataSettings.getProperty("ClientZShift", 16);
		Config.PATHFIND_BOOST = geodataSettings.getProperty("PathFindBoost", 2);
		Config.PATHFIND_DIAGONAL = geodataSettings.getProperty("PathFindDiagonal", true);
		Config.PATHFIND_MAP_MUL = geodataSettings.getProperty("PathFindMapMul", 2);
		Config.PATH_CLEAN = geodataSettings.getProperty("PathClean", true);
		Config.PATHFIND_MAX_Z_DIFF = geodataSettings.getProperty("PathFindMaxZDiff", 32);
		Config.MAX_Z_DIFF = geodataSettings.getProperty("MaxZDiff", 64);
		Config.MIN_LAYER_HEIGHT = geodataSettings.getProperty("MinLayerHeight", 64);
		Config.REGION_EDGE_MAX_Z_DIFF = geodataSettings.getProperty("RegionEdgeMaxZDiff", 128);
		Config.PATHFIND_MAX_TIME = geodataSettings.getProperty("PathFindMaxTime", 10000000);
		Config.PATHFIND_BUFFERS = geodataSettings.getProperty("PathFindBuffers", "8x96;8x128;8x160;8x192;4x224;4x256;4x288;2x320;2x384;2x352;1x512");
		Config.GEODATA_SKILL_CHECK_TASK_INTERVAL = geodataSettings.getProperty("GeodataSkillCheckTaskInterval", 200);
	}

	public static final String EVENTS_CONFIG_FILE = "config/events/events.ini";
	public static double EVENT_CofferOfShadowsPriceRate;
	public static double EVENT_CofferOfShadowsRewardRate;

	public static double EVENT_APIL_FOOLS_DROP_CHANCE;
	public static boolean EVENT_FLOW_OF_HORROR;
	public static boolean EVENT_APRIL_FOOLS_DAY;
	public static boolean EVENT_CHRISTMAS;
	public static boolean EVENT_COFFER_SHADOWS;
	public static boolean EVENT_FREYA;
	public static boolean EVENT_VITALITY_GIFT;
	public static boolean EVENT_GLIT_MEDAL;
	public static boolean EVENT_HEART;
	public static boolean EVENT_LETTER_COLLECTION;
	public static boolean EVENT_MARCH8;
	public static boolean EVENT_MASTER_ENCHANTING;
	public static boolean EVENT_PC_CAFFE_EXCHANGE;
	public static boolean EVENT_SAVING_SNOWMAN;
	public static boolean EVENT_SUMMER_MELEONS;
	public static boolean EVENT_FALL_HARVEST;
	public static boolean EVENT_TRICK_OF_TRANS;
	public static boolean EVENT_VIKTORINA;

	public static int EVENT_LastHeroItemID;
	public static double EVENT_LastHeroItemCOUNT;
	public static int EVENT_LastHeroTime;
	public static boolean EVENT_LastHeroRate;
	public static double EVENT_LastHeroItemCOUNTFinal;
	public static boolean EVENT_LastHeroRateFinal;
	public static int EVENT_LastHeroChanceToStart;

	public static int EVENT_TvTItemID;
	public static double EVENT_TvTItemCOUNT;
	public static int EVENT_TvTChanceToStart;

	// GmHunter event
	public static String[] GM_HUNTER_EVENT_REWARDS;
	public static int GM_HUNTER_EVENT_SET_SPEED;
	public static int GM_HUNTER_EVENT_SET_PDEFENCE;
	public static int GM_HUNTER_EVENT_SET_MDEFENCE;
	public static int GM_HUNTER_EVENT_SET_HP;
	public static int GM_HUNTER_EVENT_SET_CP;
	public static boolean EVENT_GvGDisableEffect;

	public static double EVENT_TFH_POLLEN_CHANCE;
	public static double EVENT_GLITTMEDAL_NORMAL_CHANCE;
	public static double EVENT_GLITTMEDAL_GLIT_CHANCE;
	public static double EVENT_L2DAY_LETTER_CHANCE;
	public static double EVENT_CHANGE_OF_HEART_CHANCE;

	public static double EVENT_TRICK_OF_TRANS_CHANCE;

	public static boolean EVENT_BOUNTY_HUNTERS_ENABLED;

	public static long EVENT_SAVING_SNOWMAN_LOTERY_PRICE;
	public static int EVENT_SAVING_SNOWMAN_REWARDER_CHANCE;

	public static double EVENT_MARCH8_DROP_CHANCE;
	public static double EVENT_MARCH8_PRICE_RATE;

	/** Master Yogi event enchant config */
	public static int ENCHANT_CHANCE_MASTER_YOGI_STAFF;
	public static int ENCHANT_MAX_MASTER_YOGI_STAFF;
	public static int SAFE_ENCHANT_MASTER_YOGI_STAFF;

	public static boolean AllowCustomDropItems;
	public static int[] CDItemsId;
	public static int[] CDItemsCountDropMin;
	public static int[] CDItemsCountDropMax;
	public static double[] CustomDropItemsChance;
	public static boolean CDItemsAllowMinMaxPlayerLvl;
	public static int CDItemsMinPlayerLvl;
	public static int CDItemsMaxPlayerLvl;
	public static boolean CDItemsAllowMinMaxMobLvl;
	public static int CDItemsMinMobLvl;
	public static int CDItemsMaxMobLvl;
	public static boolean CDItemsAllowOnlyRbDrops;

	// RandomBoss Event
	public static boolean RANDOM_BOSS_ENABLE;
	public static int RANDOM_BOSS_ID;
	public static int RANDOM_BOSS_TIME;
	public static int RANDOM_BOSS_X;
	public static int RANDOM_BOSS_Y;
	public static int RANDOM_BOSS_Z;

	// Fight Club
	public static boolean ALLOW_FIGHT_CLUB;
	public static boolean FIGHT_CLUB_HWID_CHECK;
	public static int FIGHT_CLUB_DISALLOW_EVENT;
	public static boolean FIGHT_CLUB_EQUALIZE_ROOMS;

	// Santa Event
	public static boolean EVENT_SANTA_ALLOW;
	public static double EVENT_SANTA_CHANCE_MULT;

	public static void loadEventsSettings()
	{
		final ExProperties eventSettings = load(EVENTS_CONFIG_FILE);

		Config.EVENT_CofferOfShadowsPriceRate = eventSettings.getProperty("CofferOfShadowsPriceRate", 1.);
		Config.EVENT_CofferOfShadowsRewardRate = eventSettings.getProperty("CofferOfShadowsRewardRate", 1.);

		Config.EVENT_LastHeroItemID = eventSettings.getProperty("LastHero_bonus_id", 57);
		Config.EVENT_LastHeroItemCOUNT = eventSettings.getProperty("LastHero_bonus_count", 1.);
		Config.EVENT_LastHeroTime = eventSettings.getProperty("LastHero_time", 3);
		Config.EVENT_LastHeroRate = eventSettings.getProperty("LastHero_rate", true);
		Config.EVENT_LastHeroChanceToStart = eventSettings.getProperty("LastHero_ChanceToStart", 5);
		Config.EVENT_LastHeroItemCOUNTFinal = eventSettings.getProperty("LastHero_final_bonus_count", 12.);
		Config.EVENT_LastHeroRateFinal = eventSettings.getProperty("LastHero_rate_final", true);

		Config.GM_HUNTER_EVENT_REWARDS = eventSettings.getProperty("GmHunterRewards", "57,1,100;").replaceAll(" ", "").split(";");
		Config.GM_HUNTER_EVENT_SET_SPEED = eventSettings.getProperty("GmHunterSetSpeed", 200);
		Config.GM_HUNTER_EVENT_SET_PDEFENCE = eventSettings.getProperty("GmHunterSetPdefence", 2000);
		Config.GM_HUNTER_EVENT_SET_MDEFENCE = eventSettings.getProperty("GmHunterSetMdefence", 2000);
		Config.GM_HUNTER_EVENT_SET_HP = eventSettings.getProperty("GmHunterSetHP", 500000);
		Config.GM_HUNTER_EVENT_SET_CP = eventSettings.getProperty("GmHunterSetCP", 500000);

		Config.EVENT_TvTItemID = eventSettings.getProperty("TvT_bonus_id", 57);
		Config.EVENT_TvTItemCOUNT = eventSettings.getProperty("TvT_bonus_count", 5000.);
		Config.EVENT_TvTTime = eventSettings.getProperty("TvT_time", 3);
		Config.EVENT_TvT_rate = eventSettings.getProperty("TvT_rate", true);
		Config.EVENT_TvTChanceToStart = eventSettings.getProperty("TvT_ChanceToStart", 5);

		Config.EVENT_GvGDisableEffect = eventSettings.getProperty("GvGDisableEffect", false);

		Config.EVENT_TFH_POLLEN_CHANCE = eventSettings.getProperty("TFH_POLLEN_CHANCE", 5.);

		Config.EVENT_GLITTMEDAL_NORMAL_CHANCE = eventSettings.getProperty("MEDAL_CHANCE", 10.);
		Config.EVENT_GLITTMEDAL_GLIT_CHANCE = eventSettings.getProperty("GLITTMEDAL_CHANCE", 0.1);

		Config.EVENT_L2DAY_LETTER_CHANCE = eventSettings.getProperty("L2DAY_LETTER_CHANCE", 1.);
		Config.EVENT_CHANGE_OF_HEART_CHANCE = eventSettings.getProperty("EVENT_CHANGE_OF_HEART_CHANCE", 5.);

		Config.EVENT_APIL_FOOLS_DROP_CHANCE = eventSettings.getProperty("AprilFollsDropChance", 50.);

		Config.EVENT_BOUNTY_HUNTERS_ENABLED = eventSettings.getProperty("BountyHuntersEnabled", true);

		Config.EVENT_SAVING_SNOWMAN_LOTERY_PRICE = eventSettings.getProperty("SavingSnowmanLoteryPrice", 50000);
		Config.EVENT_SAVING_SNOWMAN_REWARDER_CHANCE = eventSettings.getProperty("SavingSnowmanRewarderChance", 2);

		Config.EVENT_TRICK_OF_TRANS_CHANCE = eventSettings.getProperty("TRICK_OF_TRANS_CHANCE", 10.);

		Config.EVENT_MARCH8_DROP_CHANCE = eventSettings.getProperty("March8DropChance", 10.);
		Config.EVENT_MARCH8_PRICE_RATE = eventSettings.getProperty("March8PriceRate", 1.);

		Config.ENCHANT_CHANCE_MASTER_YOGI_STAFF = eventSettings.getProperty("MasterYogiEnchantChance", 66);
		Config.ENCHANT_MAX_MASTER_YOGI_STAFF = eventSettings.getProperty("MasterYogiEnchantMaxWeapon", 28);
		Config.SAFE_ENCHANT_MASTER_YOGI_STAFF = eventSettings.getProperty("MasterYogiSafeEnchant", 3);

		Config.AllowCustomDropItems = eventSettings.getProperty("AllowCustomDropItems", true);
		Config.CDItemsAllowMinMaxPlayerLvl = eventSettings.getProperty("CDItemsAllowMinMaxPlayerLvl", false);
		Config.CDItemsAllowMinMaxMobLvl = eventSettings.getProperty("CDItemsAllowMinMaxMobLvl", false);
		Config.CDItemsAllowOnlyRbDrops = eventSettings.getProperty("CDItemsAllowOnlyRbDrops", false);
		Config.CDItemsId = eventSettings.getProperty("CDItemsId", new int[]
		{
			57
		});
		Config.CDItemsCountDropMin = eventSettings.getProperty("CDItemsCountDropMin", new int[]
		{
			1
		});
		Config.CDItemsCountDropMax = eventSettings.getProperty("CDItemsCountDropMax", new int[]
		{
			1
		});
		Config.CustomDropItemsChance = eventSettings.getProperty("CustomDropItemsChance", new double[]
		{
			1.
		});
		Config.CDItemsMinPlayerLvl = eventSettings.getProperty("CDItemsMinPlayerLvl", 20);
		Config.CDItemsMaxPlayerLvl = eventSettings.getProperty("CDItemsMaxPlayerLvl", 85);
		Config.CDItemsMinMobLvl = eventSettings.getProperty("CDItemsMinMobLvl", 20);
		Config.CDItemsMaxMobLvl = eventSettings.getProperty("CDItemsMaxMobLvl", 80);
		Config.EVENT_FLOW_OF_HORROR = eventSettings.getProperty("EnableFlowHorrorEvent", false);
		Config.EVENT_APRIL_FOOLS_DAY = eventSettings.getProperty("EnableAprilFoolsDayEvent", false);
		Config.EVENT_CHRISTMAS = eventSettings.getProperty("EnableChrismasEvent", false);
		Config.EVENT_COFFER_SHADOWS = eventSettings.getProperty("EnableCofferShadowEvent", false);
		Config.EVENT_FREYA = eventSettings.getProperty("EnableFreyaEvent", false);
		Config.EVENT_VITALITY_GIFT = eventSettings.getProperty("EnableVitalityGiftEvent", false);
		Config.EVENT_GLIT_MEDAL = eventSettings.getProperty("EnableGlitteringMedalEvent", false);
		Config.EVENT_HEART = eventSettings.getProperty("EnableHearthEvent", false);
		Config.EVENT_LETTER_COLLECTION = eventSettings.getProperty("EnableLetterCollectionEvent", false);
		Config.EVENT_MARCH8 = eventSettings.getProperty("EnableMarch8Event", false);
		Config.EVENT_MASTER_ENCHANTING = eventSettings.getProperty("EnableMasterEnchantingEvent", false);
		Config.EVENT_PC_CAFFE_EXCHANGE = eventSettings.getProperty("EnablePCCafeExchangeEvent", false);
		Config.EVENT_SAVING_SNOWMAN = eventSettings.getProperty("EnableSavingSnowmanEvent", false);
		Config.EVENT_SUMMER_MELEONS = eventSettings.getProperty("EnableSummerMeleonsEvent", false);
		Config.EVENT_FALL_HARVEST = eventSettings.getProperty("EnableFallHarvestEvent", false);
		Config.EVENT_TRICK_OF_TRANS = eventSettings.getProperty("EnableTrickOfTransEvent", false);
		Config.RANDOM_BOSS_ENABLE = eventSettings.getProperty("EnableRandomBossEvent", false);
		Config.RANDOM_BOSS_ID = eventSettings.getProperty("RandomBossID", 37000);
		Config.RANDOM_BOSS_TIME = eventSettings.getProperty("RandomBossTime", 60);
		Config.RANDOM_BOSS_X = eventSettings.getProperty("RandomBossSpawnX", 20168);
		Config.RANDOM_BOSS_Y = eventSettings.getProperty("RandomBossSpawnY", -15336);
		Config.RANDOM_BOSS_Z = eventSettings.getProperty("RandomBossSpawnZ", -3109);

		Config.ALLOW_FIGHT_CLUB = eventSettings.getProperty("AllowFightClub", true);
		Config.FIGHT_CLUB_HWID_CHECK = eventSettings.getProperty("FightClubHwidCheck", true);
		Config.FIGHT_CLUB_DISALLOW_EVENT = eventSettings.getProperty("FightClubNotAllowedEvent", -1);
		Config.FIGHT_CLUB_EQUALIZE_ROOMS = eventSettings.getProperty("FightClubEqualizeRooms", false);

		Config.EVENT_SANTA_ALLOW = eventSettings.getProperty("AllowSantaEvent", false);
		Config.EVENT_SANTA_CHANCE_MULT = eventSettings.getProperty("SantaItemsChanceMult", 1.0);
	}

	public static final String OLYMPIAD = "config/olympiad.ini";
	public static boolean ENABLE_OLYMPIAD;
	public static boolean ENABLE_OLYMPIAD_SPECTATING;

	public static int CLASS_GAME_MIN;
	public static int NONCLASS_GAME_MIN;
	public static int TEAM_GAME_MIN;

	public static int GAME_MAX_LIMIT;
	public static int GAME_CLASSES_COUNT_LIMIT;
	public static int GAME_NOCLASSES_COUNT_LIMIT;
	public static int GAME_TEAM_COUNT_LIMIT;

	public static int ALT_OLY_REG_DISPLAY;
	public static int ALT_OLY_BATTLE_REWARD_ITEM;
	public static int ALT_OLY_CLASSED_RITEM_C;
	public static int ALT_OLY_NONCLASSED_RITEM_C;
	public static int ALT_OLY_TEAM_RITEM_C;
	public static int ALT_OLY_COMP_RITEM;
	public static int ALT_OLY_GP_PER_POINT;
	public static int ALT_OLY_HERO_POINTS;
	public static int ALT_OLY_RANK1_POINTS;
	public static int ALT_OLY_RANK2_POINTS;
	public static int ALT_OLY_RANK3_POINTS;
	public static int ALT_OLY_RANK4_POINTS;
	public static int ALT_OLY_RANK5_POINTS;
	public static int OLYMPIAD_STADIAS_COUNT;
	public static int OLYMPIAD_BATTLES_FOR_REWARD;
	public static int OLYMPIAD_POINTS_DEFAULT;
	public static int OLYMPIAD_POINTS_WEEKLY;
	public static boolean OLYMPIAD_OLDSTYLE_STAT;
	public static int ALT_OLY_WAIT_TIME;
	public static int ALT_OLY_PORT_BACK_TIME;
	public static boolean OLYMPIAD_PLAYER_IP;
	public static int OLYMPIAD_BEGIN_TIME;
	public static boolean OLYMPIAD_BAD_ENCHANT_ITEMS_ALLOW;
	public static boolean OLY_SHOW_OPPONENT_PERSONALITY;

	public static boolean OLY_ENCH_LIMIT_ENABLE;
	public static int OLY_ENCHANT_LIMIT_WEAPON;
	public static int OLY_ENCHANT_LIMIT_ARMOR;
	public static int OLY_ENCHANT_LIMIT_JEWEL;

	/** Olympiad Compitition Starting time */
	public static int ALT_OLY_START_TIME;
	/** Olympiad Compition Min */
	public static int ALT_OLY_MIN;
	/** Olympaid Comptetition Period */
	public static long ALT_OLY_CPERIOD;
	/** Olympiad Manager Shout Just One Time CUSTOM MESSAGE */
	public static boolean OLYMPIAD_SHOUT_ONCE_PER_START;
	/** Olympaid Weekly Period */
	public static long ALT_OLY_WPERIOD;
	/** Olympaid Validation Period */
	public static long ALT_OLY_VPERIOD;
	// new
	public static boolean ALT_OLYMP_PERIOD;
	public static List<Integer> ALT_OLY_DATE_END_MONTHLY = new ArrayList<>();
	public static int ALT_OLY_DATE_END_WEEKLY;

	public static void loadOlympiadSettings()
	{
		final ExProperties olympSettings = load(OLYMPIAD);

		Config.ENABLE_OLYMPIAD = olympSettings.getProperty("EnableOlympiad", true);
		Config.ALT_OLYMP_PERIOD = olympSettings.getProperty("AltTwoWeeksOlyPeriod", false);
		Config.ENABLE_OLYMPIAD_SPECTATING = olympSettings.getProperty("EnableOlympiadSpectating", true);
		Config.ALT_OLY_START_TIME = olympSettings.getProperty("AltOlyStartTime", 18);
		Config.ALT_OLY_MIN = olympSettings.getProperty("AltOlyMin", 0);
		Config.ALT_OLY_CPERIOD = olympSettings.getProperty("AltOlyCPeriod", 21600000);
		Config.OLYMPIAD_SHOUT_ONCE_PER_START = olympSettings.getProperty("OlyManagerShoutJustOneMessage", false);
		Config.ALT_OLY_WPERIOD = olympSettings.getProperty("AltOlyWPeriod", 604800000);
		Config.ALT_OLY_VPERIOD = olympSettings.getProperty("AltOlyVPeriod", 43200000);
		for (String prop : olympSettings.getProperty("AltOlyDateEndMonthly", "1").split(","))
		{
			Config.ALT_OLY_DATE_END_MONTHLY.add(Integer.parseInt(prop));
		}
		Config.ALT_OLY_DATE_END_WEEKLY = olympSettings.getProperty("AltOlyDateEndWeekly", 0);
		Config.CLASS_GAME_MIN = olympSettings.getProperty("ClassGameMin", 5);
		Config.NONCLASS_GAME_MIN = olympSettings.getProperty("NonClassGameMin", 9);
		Config.TEAM_GAME_MIN = olympSettings.getProperty("TeamGameMin", 4);

		Config.GAME_MAX_LIMIT = olympSettings.getProperty("GameMaxLimit", 70);
		Config.GAME_CLASSES_COUNT_LIMIT = olympSettings.getProperty("GameClassesCountLimit", 30);
		Config.GAME_NOCLASSES_COUNT_LIMIT = olympSettings.getProperty("GameNoClassesCountLimit", 60);
		Config.GAME_TEAM_COUNT_LIMIT = olympSettings.getProperty("GameTeamCountLimit", 10);

		Config.ALT_OLY_REG_DISPLAY = olympSettings.getProperty("AltOlyRegistrationDisplayNumber", 100);
		Config.ALT_OLY_BATTLE_REWARD_ITEM = olympSettings.getProperty("AltOlyBattleRewItem", 13722);
		Config.ALT_OLY_CLASSED_RITEM_C = olympSettings.getProperty("AltOlyClassedRewItemCount", 50);
		Config.ALT_OLY_NONCLASSED_RITEM_C = olympSettings.getProperty("AltOlyNonClassedRewItemCount", 40);
		Config.ALT_OLY_TEAM_RITEM_C = olympSettings.getProperty("AltOlyTeamRewItemCount", 50);
		Config.ALT_OLY_COMP_RITEM = olympSettings.getProperty("AltOlyCompRewItem", 13722);
		Config.ALT_OLY_GP_PER_POINT = olympSettings.getProperty("AltOlyGPPerPoint", 1000);
		Config.ALT_OLY_HERO_POINTS = olympSettings.getProperty("AltOlyHeroPoints", 180);
		Config.ALT_OLY_RANK1_POINTS = olympSettings.getProperty("AltOlyRank1Points", 120);
		Config.ALT_OLY_RANK2_POINTS = olympSettings.getProperty("AltOlyRank2Points", 80);
		Config.ALT_OLY_RANK3_POINTS = olympSettings.getProperty("AltOlyRank3Points", 55);
		Config.ALT_OLY_RANK4_POINTS = olympSettings.getProperty("AltOlyRank4Points", 35);
		Config.ALT_OLY_RANK5_POINTS = olympSettings.getProperty("AltOlyRank5Points", 20);
		Config.OLYMPIAD_STADIAS_COUNT = olympSettings.getProperty("OlympiadStadiasCount", 160);
		Config.OLYMPIAD_BEGIN_TIME = olympSettings.getProperty("OlympiadBeginTime", 120);
		Config.OLYMPIAD_BATTLES_FOR_REWARD = olympSettings.getProperty("OlympiadBattlesForReward", 15);
		Config.OLYMPIAD_POINTS_DEFAULT = olympSettings.getProperty("OlympiadPointsDefault", 50);
		Config.OLYMPIAD_POINTS_WEEKLY = olympSettings.getProperty("OlympiadPointsWeekly", 10);
		Config.OLYMPIAD_OLDSTYLE_STAT = olympSettings.getProperty("OlympiadOldStyleStat", false);
		Config.ALT_OLY_WAIT_TIME = olympSettings.getProperty("AltOlyWaitTime", 120);
		Config.ALT_OLY_PORT_BACK_TIME = olympSettings.getProperty("AltOlyPortBackTime", 20);
		Config.OLYMPIAD_PLAYER_IP = olympSettings.getProperty("OlympiadPlayerIp", false);
		Config.OLYMPIAD_BAD_ENCHANT_ITEMS_ALLOW = olympSettings.getProperty("OlympiadUnEquipBadEnchantItem", false);

		Config.OLY_ENCH_LIMIT_ENABLE = olympSettings.getProperty("OlyEnchantLimit", false);
		Config.OLY_ENCHANT_LIMIT_WEAPON = olympSettings.getProperty("OlyEnchantLimitWeapon", 0);
		Config.OLY_ENCHANT_LIMIT_ARMOR = olympSettings.getProperty("OlyEnchantLimitArmor", 0);
		Config.OLY_ENCHANT_LIMIT_JEWEL = olympSettings.getProperty("OlyEnchantLimitJewel", 0);

		Config.OLY_SHOW_OPPONENT_PERSONALITY = olympSettings.getProperty("OlympiadShowOpponentPersonality", false);
	}

	public static final String ENCHANT_CB_CONFIG_FILE = "config/CommunityPvP/EnchantCB.ini";
	public static boolean ENCHANT_ENABLED;
	public static int ENCHANTER_ITEM_ID;
	public static int MAX_ENCHANT;
	public static int[] ENCHANT_LEVELS;
	public static int[] ENCHANT_PRICE_WPN;
	public static int[] ENCHANT_PRICE_ARM;
	public static int[] ENCHANT_ATTRIBUTE_LEVELS;
	public static int[] ENCHANT_ATTRIBUTE_LEVELS_ARM;
	public static int[] ATTRIBUTE_PRICE_WPN;
	public static int[] ATTRIBUTE_PRICE_ARM;
	public static boolean ENCHANT_ATT_PVP;

	public static void loadEnchantCBConfig()
	{
		final ExProperties EnchantCBSetting = load(ENCHANT_CB_CONFIG_FILE);

		Config.ENCHANT_ENABLED = EnchantCBSetting.getProperty("Enchant_enabled", false);
		Config.ENCHANTER_ITEM_ID = EnchantCBSetting.getProperty("CBEnchantItem", 4037);
		Config.MAX_ENCHANT = EnchantCBSetting.getProperty("CBEnchantItem", 20);
		Config.ENCHANT_LEVELS = EnchantCBSetting.getProperty("CBEnchantLvl", new int[]
		{
			1
		});
		Config.ENCHANT_PRICE_WPN = EnchantCBSetting.getProperty("CBEnchantPriceWeapon", new int[]
		{
			1
		});
		Config.ENCHANT_PRICE_ARM = EnchantCBSetting.getProperty("CBEnchantPriceArmor", new int[]
		{
			1
		});
		Config.ENCHANT_ATTRIBUTE_LEVELS = EnchantCBSetting.getProperty("CBEnchantAtributeLvlWeapon", new int[]
		{
			1
		});
		Config.ENCHANT_ATTRIBUTE_LEVELS_ARM = EnchantCBSetting.getProperty("CBEnchantAtributeLvlArmor", new int[]
		{
			1
		});
		Config.ATTRIBUTE_PRICE_WPN = EnchantCBSetting.getProperty("CBEnchantAtributePriceWeapon", new int[]
		{
			1
		});
		Config.ATTRIBUTE_PRICE_ARM = EnchantCBSetting.getProperty("CBEnchantAtributePriceArmor", new int[]
		{
			1
		});
		Config.ENCHANT_ATT_PVP = EnchantCBSetting.getProperty("CBEnchantAtributePvP", false);

	}

	public static final String RANKING_CB_CONFIG_FILE = "config/CommunityPvP/ranking.ini";
	public static int SERVER_RANKING_REWARD_ITEM_ID;
	public static int[] SERVER_RANKING_REWARD_ITEM_COUNT;

	public static void loadRankingCBConfig()
	{
		final ExProperties rankingCBSetting = load(RANKING_CB_CONFIG_FILE);

		Config.SERVER_RANKING_REWARD_ITEM_ID = rankingCBSetting.getProperty("ServerRankingRewardItemId", 57);
		Config.SERVER_RANKING_REWARD_ITEM_COUNT = rankingCBSetting.getProperty("ServerRankingRewardItemCount", new int[]
		{
			10,
			3,
			1
		});
	}

	public static final String AUGMENT_CB_CONFIG_FILE = "config/CommunityPvP/augment.ini";
	public static boolean ALLOW_CB_AUGMENTATION;
	public static int COMMUNITY_AUGMENTATION_MIN_LEVEL;
	public static boolean COMMUNITY_AUGMENTATION_ALLOW_JEWELRY;

	public static void loadAugmentCBConfig()
	{
		final ExProperties augmentCBSetting = load(AUGMENT_CB_CONFIG_FILE);

		Config.ALLOW_CB_AUGMENTATION = augmentCBSetting.getProperty("EnableCommunityAugmentation", false);
		Config.COMMUNITY_AUGMENTATION_MIN_LEVEL = augmentCBSetting.getProperty("MinLevelToAugment", 46);
		Config.COMMUNITY_AUGMENTATION_ALLOW_JEWELRY = augmentCBSetting.getProperty("AllowJewelryyAugmentation", false);
	}

	public static final String ACADEMY_CB_CONFIG_FILE = "config/CommunityPvP/academy.ini";
	// Community Academy
	public static boolean ENABLE_COMMUNITY_ACADEMY;
	public static String SERVICES_ACADEMY_REWARD;
	public static long ACADEMY_MIN_ADENA_AMOUNT;
	public static long ACADEMY_MAX_ADENA_AMOUNT;
	public static long MAX_TIME_IN_ACADEMY;
	public static int ACADEMY_INVITE_DELAY;

	public static void loadAcademyCBConfig()
	{
		final ExProperties academyCBSetting = load(ACADEMY_CB_CONFIG_FILE);

		Config.ENABLE_COMMUNITY_ACADEMY = academyCBSetting.getProperty("EnableAcademyBoard", false);
		Config.SERVICES_ACADEMY_REWARD = academyCBSetting.getProperty("AcademyRewards", "57");
		Config.ACADEMY_MIN_ADENA_AMOUNT = academyCBSetting.getProperty("MinAcademyPrice", 1);
		Config.ACADEMY_MAX_ADENA_AMOUNT = academyCBSetting.getProperty("MaxAcademyPrice", 1000000000);
		Config.MAX_TIME_IN_ACADEMY = academyCBSetting.getProperty("KickAcademyAfter", 259200000);
		Config.ACADEMY_INVITE_DELAY = academyCBSetting.getProperty("InviteDelay", 5);
	}

	public static final String PREMIUM_FILE = "config/services/premium.ini";
	public static int PREMIUM_ACCOUNT_TYPE;
	public static int PREMIUM_ACCOUNT_PARTY_GIFT_ID;
	public static boolean ENTER_WORLD_SHOW_HTML_PREMIUM_BUY;
	public static boolean ENTER_WORLD_SHOW_HTML_PREMIUM_DONE;
	public static boolean ENTER_WORLD_SHOW_HTML_PREMIUM_ACTIVE;

	public static int SERVICES_RATE_TYPE;
	public static int SERVICES_RATE_CREATE_PA;
	public static int[] SERVICES_RATE_BONUS_PRICE;
	public static int[] SERVICES_RATE_BONUS_ITEM;
	public static double[] SERVICES_RATE_BONUS_VALUE;
	public static int[] SERVICES_RATE_BONUS_DAYS;
	public static int ENCHANT_CHANCE_WEAPON_PA;
	public static int ENCHANT_CHANCE_ARMOR_PA;
	public static int ENCHANT_CHANCE_ACCESSORY_PA;
	public static int ENCHANT_CHANCE_WEAPON_BLESS_PA;
	public static int ENCHANT_CHANCE_ARMOR_BLESS_PA;
	public static int ENCHANT_CHANCE_ACCESSORY_BLESS_PA;
	public static int ENCHANT_CHANCE_CRYSTAL_WEAPON_PA;
	public static int ENCHANT_CHANCE_CRYSTAL_ARMOR_PA;
	public static int ENCHANT_CHANCE_CRYSTAL_ACCESSORY_PA;

	public static double SERVICES_BONUS_XP;
	public static double SERVICES_BONUS_SP;
	public static double SERVICES_BONUS_ADENA;
	public static double SERVICES_BONUS_ITEMS;
	public static double SERVICES_BONUS_SPOIL;
	public static boolean AUTO_LOOT_PA;
	public static int ALT_NEW_CHAR_PREMIUM_ID;
	public static boolean USE_ALT_ENCHANT_PA;
	public static ArrayList<Integer> ENCHANT_WEAPON_FIGHT_PA = new ArrayList<Integer>();
	public static ArrayList<Integer> ENCHANT_WEAPON_FIGHT_BLESSED_PA = new ArrayList<Integer>();
	public static ArrayList<Integer> ENCHANT_WEAPON_FIGHT_CRYSTAL_PA = new ArrayList<Integer>();
	public static ArrayList<Integer> ENCHANT_ARMOR_PA = new ArrayList<Integer>();
	public static ArrayList<Integer> ENCHANT_ARMOR_CRYSTAL_PA = new ArrayList<Integer>();
	public static ArrayList<Integer> ENCHANT_ARMOR_BLESSED_PA = new ArrayList<Integer>();
	public static ArrayList<Integer> ENCHANT_ARMOR_JEWELRY_PA = new ArrayList<Integer>();
	public static ArrayList<Integer> ENCHANT_ARMOR_JEWELRY_CRYSTAL_PA = new ArrayList<Integer>();
	public static ArrayList<Integer> ENCHANT_ARMOR_JEWELRY_BLESSED_PA = new ArrayList<Integer>();

	public static void loadPremiumConfig()
	{
		final ExProperties premiumConf = load(PREMIUM_FILE);

		Config.PREMIUM_ACCOUNT_TYPE = premiumConf.getProperty("RateBonusType", 0);
		Config.PREMIUM_ACCOUNT_PARTY_GIFT_ID = premiumConf.getProperty("PartyGift", 1);

		Config.ENTER_WORLD_SHOW_HTML_PREMIUM_BUY = premiumConf.getProperty("PremiumHTML", false);
		// ENTER_WORLD_SHOW_HTML_LOCK = premiumConf.getProperty("LockHTML", false);
		Config.ENTER_WORLD_SHOW_HTML_PREMIUM_DONE = premiumConf.getProperty("PremiumDone", false);
		Config.ENTER_WORLD_SHOW_HTML_PREMIUM_ACTIVE = premiumConf.getProperty("PremiumInfo", false);

		Config.SERVICES_RATE_TYPE = premiumConf.getProperty("RateBonusType", Bonus.NO_BONUS);
		Config.SERVICES_RATE_CREATE_PA = premiumConf.getProperty("RateBonusCreateChar", 0);
		Config.SERVICES_RATE_BONUS_PRICE = premiumConf.getProperty("RateBonusPrice", new int[]
		{
			1500
		});
		Config.SERVICES_RATE_BONUS_ITEM = premiumConf.getProperty("RateBonusItem", new int[]
		{
			4037
		});
		Config.SERVICES_RATE_BONUS_VALUE = premiumConf.getProperty("RateBonusValue", new double[]
		{
			2.
		});
		Config.SERVICES_RATE_BONUS_DAYS = premiumConf.getProperty("RateBonusTime", new int[]
		{
			30
		});
		Config.AUTO_LOOT_PA = premiumConf.getProperty("AutoLootPA", false);
		Config.ENCHANT_CHANCE_WEAPON_PA = premiumConf.getProperty("EnchantChancePA", 66);
		Config.ENCHANT_CHANCE_ARMOR_PA = premiumConf.getProperty("EnchantChanceArmorPA", ENCHANT_CHANCE_WEAPON);
		Config.ENCHANT_CHANCE_ACCESSORY_PA = premiumConf.getProperty("EnchantChanceAccessoryPA", ENCHANT_CHANCE_ARMOR);
		Config.ENCHANT_CHANCE_WEAPON_BLESS_PA = premiumConf.getProperty("EnchantChanceBlessPA", 66);
		Config.ENCHANT_CHANCE_ARMOR_BLESS_PA = premiumConf.getProperty("EnchantChanceArmorBlessPA", ENCHANT_CHANCE_WEAPON);
		Config.ENCHANT_CHANCE_ACCESSORY_BLESS_PA = premiumConf.getProperty("EnchantChanceAccessoryBlessPA", ENCHANT_CHANCE_ARMOR);
		Config.ENCHANT_CHANCE_CRYSTAL_WEAPON_PA = premiumConf.getProperty("EnchantChanceCrystalPA", 66);
		Config.ENCHANT_CHANCE_CRYSTAL_ARMOR_PA = premiumConf.getProperty("EnchantChanceCrystalArmorPA", ENCHANT_CHANCE_CRYSTAL_WEAPON);
		Config.ENCHANT_CHANCE_CRYSTAL_ACCESSORY_PA = premiumConf.getProperty("EnchantChanceCrystalAccessory", ENCHANT_CHANCE_CRYSTAL_ARMOR);

		Config.SERVICES_BONUS_XP = premiumConf.getProperty("RateBonusXp", 1.);
		Config.SERVICES_BONUS_SP = premiumConf.getProperty("RateBonusSp", 1.);
		Config.SERVICES_BONUS_ADENA = premiumConf.getProperty("RateBonusAdena", 1.);
		Config.SERVICES_BONUS_ITEMS = premiumConf.getProperty("RateBonusItems", 1.);
		Config.SERVICES_BONUS_SPOIL = premiumConf.getProperty("RateBonusSpoil", 1.);

		Config.USE_ALT_ENCHANT_PA = Boolean.parseBoolean(premiumConf.getProperty("UseAltEnchantPA", "False"));
		for (String prop : premiumConf.getProperty("EnchantWeaponFighterPA", "100,100,100,70,70,70,70,70,70,70,70,70,70,70,70,35,35,35,35,35").split(","))
		{
			Config.ENCHANT_WEAPON_FIGHT_PA.add(Integer.parseInt(prop));
		}
		for (String prop : premiumConf.getProperty("EnchantWeaponFighterCrystalPA", "100,100,100,70,70,70,70,70,70,70,70,70,70,70,70,35,35,35,35,35").split(","))
		{
			Config.ENCHANT_WEAPON_FIGHT_BLESSED_PA.add(Integer.parseInt(prop));
		}
		for (String prop : premiumConf.getProperty("EnchantWeaponFighterBlessedPA", "100,100,100,70,70,70,70,70,70,70,70,70,70,70,70,35,35,35,35,35").split(","))
		{
			Config.ENCHANT_WEAPON_FIGHT_CRYSTAL_PA.add(Integer.parseInt(prop));
		}
		for (String prop : premiumConf.getProperty("EnchantArmorPA", "100,100,100,66,33,25,20,16,14,12,11,10,9,8,8,7,7,6,6,6").split(","))
		{
			Config.ENCHANT_ARMOR_PA.add(Integer.parseInt(prop));
		}
		for (String prop : premiumConf.getProperty("EnchantArmorCrystalPA", "100,100,100,66,33,25,20,16,14,12,11,10,9,8,8,7,7,6,6,6").split(","))
		{
			Config.ENCHANT_ARMOR_CRYSTAL_PA.add(Integer.parseInt(prop));
		}
		for (String prop : premiumConf.getProperty("EnchantArmorBlessedPA", "100,100,100,66,33,25,20,16,14,12,11,10,9,8,8,7,7,6,6,6").split(","))
		{
			Config.ENCHANT_ARMOR_BLESSED_PA.add(Integer.parseInt(prop));
		}
		for (String prop : premiumConf.getProperty("EnchantJewelryPA", "100,100,100,66,33,25,20,16,14,12,11,10,9,8,8,7,7,6,6,6").split(","))
		{
			Config.ENCHANT_ARMOR_JEWELRY_PA.add(Integer.parseInt(prop));
		}
		for (String prop : premiumConf.getProperty("EnchantJewelryCrystalPA", "100,100,100,66,33,25,20,16,14,12,11,10,9,8,8,7,7,6,6,6").split(","))
		{
			Config.ENCHANT_ARMOR_JEWELRY_CRYSTAL_PA.add(Integer.parseInt(prop));
		}
		for (String prop : premiumConf.getProperty("EnchantJewelryBlessedPA", "100,100,100,66,33,25,20,16,14,12,11,10,9,8,8,7,7,6,6,6").split(","))
		{
			Config.ENCHANT_ARMOR_JEWELRY_BLESSED_PA.add(Integer.parseInt(prop));
		}

		Config.ALT_NEW_CHAR_PREMIUM_ID = premiumConf.getProperty("AltNewCharPremiumId", 0);
	}

	public static final String TALKING_GUARD_CONFIG_FILE = "config/mod/TalkingGuard.ini";
	public static int TalkGuardChance;
	public static int TalkNormalChance = 0;
	public static int TalkNormalPeriod = 0;
	public static int TalkAggroPeriod = 0;

	public static void loadTalkGuardConfig()
	{
		final ExProperties TalkGuardSetting = load(TALKING_GUARD_CONFIG_FILE);

		Config.TalkGuardChance = TalkGuardSetting.getProperty("TalkGuardChance", 4037);
		Config.TalkNormalChance = TalkGuardSetting.getProperty("TalkNormalChance", 4037);
		Config.TalkNormalPeriod = TalkGuardSetting.getProperty("TalkNormalPeriod", 4037);
		Config.TalkAggroPeriod = TalkGuardSetting.getProperty("TalkAggroPeriod", 4037);
	}

	public static final String BUFFER_CONFIG_FILE = "config/services/Buffer.ini";
	// Buffer
	public static boolean BUFFER_ON;
	public static int ITEM_ID;
	public static boolean BUFFER_PET_ENABLED;
	public static int BUFFER_PRICE;
	public static int BUFFER_MIN_LVL;
	public static int BUFFER_MAX_LVL;
	public static boolean BUFFER_ALLOW_IN_INSTANCE;

	public static void loadBufferConfig()
	{
		final ExProperties BufferConfig = load(BUFFER_CONFIG_FILE);

		Config.BUFFER_ON = BufferConfig.getProperty("Buffer", false);
		Config.ITEM_ID = BufferConfig.getProperty("Item_id", 57);
		Config.BUFFER_PET_ENABLED = BufferConfig.getProperty("Buffer_pet", false);
		Config.BUFFER_PRICE = BufferConfig.getProperty("Buffer_price", 20);
		Config.BUFFER_MIN_LVL = BufferConfig.getProperty("Buffer_min_lvl", 1);
		Config.BUFFER_MAX_LVL = BufferConfig.getProperty("Buffer_max_lvl", 99);
		Config.BUFFER_ALLOW_IN_INSTANCE = BufferConfig.getProperty("BufferAllowInInstance", false);

	}

	public static final String ACC_MOVE_FILE = "config/services/CharMove.ini";
	// Acc move
	public static boolean ACC_MOVE_ENABLED;
	public static int ACC_MOVE_ITEM;
	public static int ACC_MOVE_PRICE;

	// Transferring characters between accounts
	public static void loadAcc_moveConfig()
	{
		final ExProperties Acc_moveConfig = load(ACC_MOVE_FILE);

		Config.ACC_MOVE_ENABLED = Acc_moveConfig.getProperty("Acc_move_enabled", false);
		Config.ACC_MOVE_ITEM = Acc_moveConfig.getProperty("Acc_move_item", 57);
		Config.ACC_MOVE_PRICE = Acc_moveConfig.getProperty("Acc_move_price", 57);

	}

	public static final String EVENT_TEAM_VS_TEAM_CONFIG_FILE = "config/events/TeamVSTeam.ini";
	public static int EVENT_TvTTime;
	public static String[] EVENT_TvTRewards;
	public static boolean EVENT_TvT_rate;
	public static String[] EVENT_TvTStartTime;
	public static boolean EVENT_TvTCategories;
	public static int EVENT_TvTMaxPlayerInTeam;
	public static int EVENT_TvTMinPlayerInTeam;
	public static boolean EVENT_TvTAllowSummons;
	public static boolean EVENT_TvTAllowBuffs;
	public static boolean EVENT_TvTAllowMultiReg;
	public static String EVENT_TvTCheckWindowMethod;
	public static int EVENT_TvTEventRunningTime;
	public static String[] EVENT_TvTFighterBuffs;
	public static String[] EVENT_TvTMageBuffs;
	public static boolean EVENT_TvTBuffPlayers;
	public static boolean EVENT_TvTrate;
	public static int[] EVENT_TvTOpenCloseDoors;
	public static String[] EVENT_TvT_DISALLOWED_SKILLS;

	public static void loadTeamVSTeamSettings()
	{
		final ExProperties eventTeamVSTeamSettings = load(EVENT_TEAM_VS_TEAM_CONFIG_FILE);

		Config.EVENT_TvTRewards = eventTeamVSTeamSettings.getProperty("TvT_Rewards", "").trim().replaceAll(" ", "").split(";");
		Config.EVENT_TvTTime = eventTeamVSTeamSettings.getProperty("TvT_time", 3);
		Config.EVENT_TvTStartTime = eventTeamVSTeamSettings.getProperty("TvT_StartTime", "20:00").trim().replaceAll(" ", "").split(",");
		Config.EVENT_TvTCategories = eventTeamVSTeamSettings.getProperty("TvT_Categories", false);
		Config.EVENT_TvTMaxPlayerInTeam = eventTeamVSTeamSettings.getProperty("TvT_MaxPlayerInTeam", 20);
		Config.EVENT_TvTMinPlayerInTeam = eventTeamVSTeamSettings.getProperty("TvT_MinPlayerInTeam", 2);
		Config.EVENT_TvTAllowSummons = eventTeamVSTeamSettings.getProperty("TvT_AllowSummons", false);
		Config.EVENT_TvTAllowBuffs = eventTeamVSTeamSettings.getProperty("TvT_AllowBuffs", false);
		Config.EVENT_TvTAllowMultiReg = eventTeamVSTeamSettings.getProperty("TvT_AllowMultiReg", false);
		Config.EVENT_TvTCheckWindowMethod = eventTeamVSTeamSettings.getProperty("TvT_CheckWindowMethod", "IP");
		Config.EVENT_TvTEventRunningTime = eventTeamVSTeamSettings.getProperty("TvT_EventRunningTime", 20);
		Config.EVENT_TvTFighterBuffs = eventTeamVSTeamSettings.getProperty("TvT_FighterBuffs", "").trim().replaceAll(" ", "").split(";");
		Config.EVENT_TvTMageBuffs = eventTeamVSTeamSettings.getProperty("TvT_MageBuffs", "").trim().replaceAll(" ", "").split(";");
		Config.EVENT_TvTBuffPlayers = eventTeamVSTeamSettings.getProperty("TvT_BuffPlayers", false);
		Config.EVENT_TvTrate = eventTeamVSTeamSettings.getProperty("TvT_rate", true);
		Config.EVENT_TvTOpenCloseDoors = eventTeamVSTeamSettings.getProperty("TvT_OpenCloseDoors", new int[]
		{
			24190001,
			24190002,
			24190003,
			24190004
		});
		Config.EVENT_TvT_DISALLOWED_SKILLS = eventTeamVSTeamSettings.getProperty("TvT_DisallowedSkills", "").trim().replaceAll(" ", "").split(";");

	}

	public static final String EVENT_KOREAN_STYLE_CONFIG_FILE = "config/events/KoreanStyle.ini";
	public static String[] EVENT_KOREAN_WINNER_REWARDS;
	public static String[] EVENT_KOREAN_KILL_REWARD;
	public static String[] EVENT_KOREANStartTime;
	public static String[] EVENT_KOREAN_REFLECTIONS;
	public static String[] EVENT_KOREAN_FIGHTER_BUFFS;
	public static String[] EVENT_KOREAN_MAGE_BUFFS;
	public static String[] EVENT_KOREAN_DISALLOWED_SKILLS;
	public static String EVENT_KOREAN_CHECK_WINDOW_METHOD;
	public static int EVENT_KOREAN_TIME_TO_TP;
	public static int EVENT_KOREAN_PLAYERS_IN_TEAM;
	public static int EVENT_KOREAN_MIN_LEVEL;
	public static int EVENT_KOREAN_MAX_LEVEL;
	public static boolean EVENT_KOREAN_ALLOW_BUFFS;
	public static boolean EVENT_KOREAN_BUFF_PLAYERS;
	public static boolean EVENT_KOREAN_RESET_REUSE;
	public static int EVENT_KOREAN_SEC_UNTIL_KILL;

	public static void loadKoreanStyleSettings()
	{
		final ExProperties eventKoreanStyleSettings = load(EVENT_KOREAN_STYLE_CONFIG_FILE);

		Config.EVENT_KOREAN_WINNER_REWARDS = eventKoreanStyleSettings.getProperty("Korean_Winner_Reward", "").trim().replaceAll(" ", "").split(";");
		Config.EVENT_KOREAN_KILL_REWARD = eventKoreanStyleSettings.getProperty("Korean_Kill_Reward", "").trim().replaceAll(" ", "").split(";");
		Config.EVENT_KOREAN_TIME_TO_TP = eventKoreanStyleSettings.getProperty("Korean_time", 5);
		Config.EVENT_KOREANStartTime = eventKoreanStyleSettings.getProperty("Korean_StartTime", "21:00").trim().replaceAll(" ", "").split(",");
		Config.EVENT_KOREAN_PLAYERS_IN_TEAM = eventKoreanStyleSettings.getProperty("Korean_PlayersInTeam", 50);
		Config.EVENT_KOREAN_MIN_LEVEL = eventKoreanStyleSettings.getProperty("Korean_MinLevel", 80);
		Config.EVENT_KOREAN_MAX_LEVEL = eventKoreanStyleSettings.getProperty("Korean_MaxLevel", 85);
		Config.EVENT_KOREAN_REFLECTIONS = eventKoreanStyleSettings.getProperty("Korean_Reflections", "").trim().replaceAll(" ", "").split(";");
		Config.EVENT_KOREAN_ALLOW_BUFFS = eventKoreanStyleSettings.getProperty("Korean_AllowBuffs", false);
		Config.EVENT_KOREAN_CHECK_WINDOW_METHOD = eventKoreanStyleSettings.getProperty("Korean_CheckWindowMethod", "IP");
		Config.EVENT_KOREAN_BUFF_PLAYERS = eventKoreanStyleSettings.getProperty("Korean_BuffPlayers", false);
		Config.EVENT_KOREAN_RESET_REUSE = eventKoreanStyleSettings.getProperty("Korean_ResetReuse", false);
		Config.EVENT_KOREAN_FIGHTER_BUFFS = eventKoreanStyleSettings.getProperty("Korean_FighterBuffs", "").trim().replaceAll(" ", "").split(";");
		Config.EVENT_KOREAN_MAGE_BUFFS = eventKoreanStyleSettings.getProperty("Korean_MageBuffs", "").trim().replaceAll(" ", "").split(";");
		Config.EVENT_KOREAN_SEC_UNTIL_KILL = eventKoreanStyleSettings.getProperty("Korean_Sec_Until_Kill", 60);
		// Config.EVENT_KOREAN_DISALLOWED_SKILLS = eventKoreanStyleSettings.getProperty("Korean_DisallowedSkills", "").trim().replaceAll(" ", "").split(";");
	}

	public static final String EVENT_CAPTURE_THE_FLAG_CONFIG_FILE = "config/events/CaptureTheFlag.ini";
	public static int EVENT_CtfTime;
	public static boolean EVENT_CtFrate;
	public static String[] EVENT_CtFStartTime;
	public static boolean EVENT_CtFCategories;
	public static int EVENT_CtFMaxPlayerInTeam;
	public static int EVENT_CtFMinPlayerInTeam;
	public static boolean EVENT_CtFAllowSummons;
	public static boolean EVENT_CtFAllowBuffs;
	public static boolean EVENT_CtFAllowMultiReg;
	public static String EVENT_CtFCheckWindowMethod;
	public static String[] EVENT_CtFFighterBuffs;
	public static String[] EVENT_CtFMageBuffs;
	public static boolean EVENT_CtFBuffPlayers;
	public static String[] EVENT_CtFRewards;
	public static int[] EVENT_CtFOpenCloseDoors;
	public static String[] EVENT_CtF_DISALLOWED_SKILLS;

	public static void loadCaptureTheFlagSettings()
	{
		final ExProperties eventCaptureTheFlagSettings = load(EVENT_CAPTURE_THE_FLAG_CONFIG_FILE);

		Config.EVENT_CtFRewards = eventCaptureTheFlagSettings.getProperty("CtF_Rewards", "").trim().replaceAll(" ", "").split(";");
		Config.EVENT_CtfTime = eventCaptureTheFlagSettings.getProperty("CtF_time", 3);
		Config.EVENT_CtFrate = eventCaptureTheFlagSettings.getProperty("CtF_rate", true);
		Config.EVENT_CtFStartTime = eventCaptureTheFlagSettings.getProperty("CtF_StartTime", "20:00").trim().replaceAll(" ", "").split(",");
		Config.EVENT_CtFCategories = eventCaptureTheFlagSettings.getProperty("CtF_Categories", false);
		Config.EVENT_CtFMaxPlayerInTeam = eventCaptureTheFlagSettings.getProperty("CtF_MaxPlayerInTeam", 20);
		Config.EVENT_CtFMinPlayerInTeam = eventCaptureTheFlagSettings.getProperty("CtF_MinPlayerInTeam", 2);
		Config.EVENT_CtFAllowSummons = eventCaptureTheFlagSettings.getProperty("CtF_AllowSummons", false);
		Config.EVENT_CtFAllowBuffs = eventCaptureTheFlagSettings.getProperty("CtF_AllowBuffs", false);
		Config.EVENT_CtFAllowMultiReg = eventCaptureTheFlagSettings.getProperty("CtF_AllowMultiReg", false);
		Config.EVENT_CtFCheckWindowMethod = eventCaptureTheFlagSettings.getProperty("CtF_CheckWindowMethod", "IP");
		Config.EVENT_CtFFighterBuffs = eventCaptureTheFlagSettings.getProperty("CtF_FighterBuffs", "").trim().replaceAll(" ", "").split(";");
		Config.EVENT_CtFMageBuffs = eventCaptureTheFlagSettings.getProperty("CtF_MageBuffs", "").trim().replaceAll(" ", "").split(";");
		Config.EVENT_CtFBuffPlayers = eventCaptureTheFlagSettings.getProperty("CtF_BuffPlayers", false);
		Config.EVENT_CtFOpenCloseDoors = eventCaptureTheFlagSettings.getProperty("CtF_OpenCloseDoors", new int[]
		{
			24190001,
			24190002,
			24190003,
			24190004
		});
		Config.EVENT_CtF_DISALLOWED_SKILLS = eventCaptureTheFlagSettings.getProperty("CtF_DisallowedSkills", "").trim().replaceAll(" ", "").split(";");

	}

	public static int RAID_EVENT_RAID_ID;
	public static int RAID_EVENT_DURATION;
	public static int RAID_EVENT_NOTIFY_DELAY;
	public static int RAID_EVENT_TIME_HOUR;
	public static int RAID_EVENT_TIME_MINUTE;
	public static final String RAID_EVENT_CONFIG_FILE = "config/events/RaidEvent.ini";

	public static void loadRaidEventConfig()
	{
		final ExProperties raidEventConfig = load(RAID_EVENT_CONFIG_FILE);

		// Raid Event
		Config.RAID_EVENT_RAID_ID = raidEventConfig.getProperty("RaidNpcId", 60000);
		Config.RAID_EVENT_DURATION = raidEventConfig.getProperty("EventDuration", 60) * 60 * 1000;
		Config.RAID_EVENT_NOTIFY_DELAY = raidEventConfig.getProperty("EventNotifyDelay", 2) * 60 * 1000;
		final String[] time = raidEventConfig.getProperty("EventTime", "20:00").split(":");
		Config.RAID_EVENT_TIME_HOUR = Integer.parseInt(time[0]);
		Config.RAID_EVENT_TIME_MINUTE = Integer.parseInt(time[1]);
	}

	public static final String BUFF_STORE_CONFIG_FILE = "config/mod/OfflineBuffer.ini";

	public static boolean BUFF_STORE_ENABLED;
	public static boolean BUFF_STORE_MP_ENABLED;
	public static double BUFF_STORE_MP_CONSUME_MULTIPLIER;
	public static boolean BUFF_STORE_ITEM_CONSUME_ENABLED;
	public static int BUFF_STORE_NAME_COLOR;
	public static int BUFF_STORE_TITLE_COLOR;
	public static int BUFF_STORE_OFFLINE_NAME_COLOR;
	public static List<Integer> BUFF_STORE_ALLOWED_CLASS_LIST;
	public static List<Integer> BUFF_STORE_FORBIDDEN_SKILL_LIST;

	public static void loadBuffStoreConfig()
	{
		final ExProperties buffStoreConfig = load(BUFF_STORE_CONFIG_FILE);

		// Buff Store
		Config.BUFF_STORE_ENABLED = buffStoreConfig.getProperty("BuffStoreEnabled", false);
		Config.BUFF_STORE_MP_ENABLED = buffStoreConfig.getProperty("BuffStoreMpEnabled", true);
		Config.BUFF_STORE_MP_CONSUME_MULTIPLIER = buffStoreConfig.getProperty("BuffStoreMpConsumeMultiplier", 1.0f);
		Config.BUFF_STORE_ITEM_CONSUME_ENABLED = buffStoreConfig.getProperty("BuffStoreItemConsumeEnabled", true);

		Config.BUFF_STORE_NAME_COLOR = Integer.decode("0x" + buffStoreConfig.getProperty("BuffStoreNameColor", "808080"));
		Config.BUFF_STORE_TITLE_COLOR = Integer.decode("0x" + buffStoreConfig.getProperty("BuffStoreTitleColor", "808080"));
		Config.BUFF_STORE_OFFLINE_NAME_COLOR = Integer.decode("0x" + buffStoreConfig.getProperty("BuffStoreOfflineNameColor", "808080"));

		final String[] classes = buffStoreConfig.getProperty("BuffStoreAllowedClassList", "").split(",");
		Config.BUFF_STORE_ALLOWED_CLASS_LIST = new ArrayList<>();
		if (classes.length > 0)
		{
			for (String classId : classes)
			{
				Config.BUFF_STORE_ALLOWED_CLASS_LIST.add(Integer.parseInt(classId));
			}
		}

		final String[] skills = buffStoreConfig.getProperty("BuffStoreForbiddenSkillList", "").split(",");
		Config.BUFF_STORE_FORBIDDEN_SKILL_LIST = new ArrayList<>();
		if (skills.length > 0)
		{
			for (String skillId : skills)
			{
				Config.BUFF_STORE_FORBIDDEN_SKILL_LIST.add(Integer.parseInt(skillId));
			}
		}
	}

	public static final String FORGE_CONFIG_FILE = "config/services/forge.ini";
	public static boolean BBS_FORGE_ENABLED;
	public static int BBS_FORGE_ENCHANT_ITEM;
	public static int BBS_FORGE_FOUNDATION_ITEM;
	public static int[] BBS_FORGE_FOUNDATION_PRICE_ARMOR;
	public static int[] BBS_FORGE_FOUNDATION_PRICE_WEAPON;
	public static int[] BBS_FORGE_FOUNDATION_PRICE_JEWEL;
	public static int[] BBS_FORGE_ENCHANT_MAX;
	public static int[] BBS_FORGE_WEAPON_ENCHANT_LVL;
	public static int[] BBS_FORGE_ARMOR_ENCHANT_LVL;
	public static int[] BBS_FORGE_JEWELS_ENCHANT_LVL;
	public static int[] BBS_FORGE_ENCHANT_PRICE_WEAPON;
	public static int[] BBS_FORGE_ENCHANT_PRICE_ARMOR;
	public static int[] BBS_FORGE_ENCHANT_PRICE_JEWELS;
	public static int[] BBS_FORGE_AUGMENT_ITEMS_LIST;
	public static long[] BBS_FORGE_AUGMENT_COUNT_LIST;
	public static int BBS_FORGE_WEAPON_ATTRIBUTE_MAX;
	public static int BBS_FORGE_ARMOR_ATTRIBUTE_MAX;
	public static int[] BBS_FORGE_ATRIBUTE_LVL_WEAPON;
	public static int[] BBS_FORGE_ATRIBUTE_LVL_ARMOR;
	public static int[] BBS_FORGE_ATRIBUTE_PRICE_ARMOR;
	public static int[] BBS_FORGE_ATRIBUTE_PRICE_WEAPON;
	public static boolean BBS_FORGE_ATRIBUTE_PVP;
	public static String[] BBS_FORGE_GRADE_ATTRIBUTE;

	public static void loadForgeSettings()
	{
		final ExProperties forge = load(FORGE_CONFIG_FILE);
		Config.BBS_FORGE_ENABLED = forge.getProperty("Allow", false);
		Config.BBS_FORGE_ENCHANT_ITEM = forge.getProperty("Item", 4356);
		Config.BBS_FORGE_FOUNDATION_ITEM = forge.getProperty("FoundationItem", 37000);
		Config.BBS_FORGE_FOUNDATION_PRICE_ARMOR = forge.getProperty("FoundationPriceArmor", new int[]
		{
			1,
			1,
			1,
			1,
			1,
			2,
			5,
			10
		});
		Config.BBS_FORGE_FOUNDATION_PRICE_WEAPON = forge.getProperty("FoundationPriceWeapon", new int[]
		{
			1,
			1,
			1,
			1,
			1,
			2,
			5,
			10
		});
		Config.BBS_FORGE_FOUNDATION_PRICE_JEWEL = forge.getProperty("FoundationPriceJewel", new int[]
		{
			1,
			1,
			1,
			1,
			1,
			2,
			5,
			10
		});
		Config.BBS_FORGE_ENCHANT_MAX = forge.getProperty("MaxEnchant", new int[]
		{
			25
		});
		Config.BBS_FORGE_WEAPON_ENCHANT_LVL = forge.getProperty("WValue", new int[]
		{
			5
		});
		Config.BBS_FORGE_ARMOR_ENCHANT_LVL = forge.getProperty("AValue", new int[]
		{
			5
		});
		Config.BBS_FORGE_JEWELS_ENCHANT_LVL = forge.getProperty("JValue", new int[]
		{
			5
		});
		Config.BBS_FORGE_ENCHANT_PRICE_WEAPON = forge.getProperty("WPrice", new int[]
		{
			5
		});
		Config.BBS_FORGE_ENCHANT_PRICE_ARMOR = forge.getProperty("APrice", new int[]
		{
			5
		});
		Config.BBS_FORGE_ENCHANT_PRICE_JEWELS = forge.getProperty("JPrice", new int[]
		{
			5
		});

		Config.BBS_FORGE_AUGMENT_ITEMS_LIST = forge.getProperty("AugmentItems", new int[]
		{
			4037,
			4037,
			4037,
			4037
		});
		Config.BBS_FORGE_AUGMENT_COUNT_LIST = forge.getProperty("AugmentCount", new long[]
		{
			1L,
			3L,
			6L,
			10L
		});

		Config.BBS_FORGE_ATRIBUTE_LVL_WEAPON = forge.getProperty("AtributeWeaponValue", new int[]
		{
			25
		});
		Config.BBS_FORGE_ATRIBUTE_PRICE_WEAPON = forge.getProperty("PriceForAtributeWeapon", new int[]
		{
			25
		});
		Config.BBS_FORGE_ATRIBUTE_LVL_ARMOR = forge.getProperty("AtributeArmorValue", new int[]
		{
			25
		});
		Config.BBS_FORGE_ATRIBUTE_PRICE_ARMOR = forge.getProperty("PriceForAtributeArmor", new int[]
		{
			25
		});
		Config.BBS_FORGE_ATRIBUTE_PVP = forge.getProperty("AtributePvP", true);
		Config.BBS_FORGE_WEAPON_ATTRIBUTE_MAX = forge.getProperty("MaxWAttribute", 25);
		Config.BBS_FORGE_ARMOR_ATTRIBUTE_MAX = forge.getProperty("MaxAAttribute", 25);
		Config.BBS_FORGE_GRADE_ATTRIBUTE = forge.getProperty("AtributeGrade", "NG:NO;D:NO;C:NO;B:NO;A:ON;S:ON;S80:ON;S84:ON").trim().replaceAll(" ", "").split(";");
	}

	public static final String ANTI_FEED_CONFIG_FILE = "config/mod/AntiFeed.ini";
	public static boolean ANTIFEED_ENABLE;
	public static boolean ANTIFEED_DUALBOX;
	public static boolean ANTIFEED_DISCONNECTED_AS_DUALBOX;
	public static int ANTIFEED_INTERVAL;
	public static int ANTIFEED_MAX_LVL_DIFFERENCE;

	public static void loadAntiFeedSettings()
	{
		final ExProperties antifeed = load(ANTI_FEED_CONFIG_FILE);

		Config.ANTIFEED_ENABLE = antifeed.getProperty("AntiFeedEnable", false);
		Config.ANTIFEED_DUALBOX = antifeed.getProperty("AntiFeedDualbox", true);
		Config.ANTIFEED_DISCONNECTED_AS_DUALBOX = antifeed.getProperty("AntiFeedDisconnectedAsDualbox", true);
		Config.ANTIFEED_INTERVAL = antifeed.getProperty("AntiFeedInterval", 120) * 1000;
		Config.ANTIFEED_MAX_LVL_DIFFERENCE = antifeed.getProperty("AntiFeedMaxLvlDifference", 0);
	}

	public static final String DONATE_REWARD_CONFIG_FILE = "config/services/DonateReward.ini";

	public static int DONATION_REWARD_ITEM_ID;
	public static int DONATION_REWARD_MULTIPLIER_PER_EURO;
	public static Map<Integer, Integer> DONATION_REWARD_BONUSES = new LinkedHashMap<>();

	public static void loadDonateRewardSettings()
	{
		final ExProperties donate = load(DONATE_REWARD_CONFIG_FILE);

		Config.DONATION_REWARD_ITEM_ID = donate.getProperty("DonationRewardItemId", 57);
		Config.DONATION_REWARD_MULTIPLIER_PER_EURO = donate.getProperty("DonationMultiplierPerEuro", 1);
		final String donationBonus = donate.getProperty("DonationBonusRewards", "300,35;200,25;100,20;25,15;10,10;0,1");
		for (String bonus : donationBonus.split(";"))
		{
			final String[] split = bonus.split(",");
			Config.DONATION_REWARD_BONUSES.put(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
		}
	}

	public static final String RU_VOTE_CONFIG_FILE = "config/mod/RuVotes.properties";

	public static boolean ENABLE_RU_VOTE_SYSTEM;
	public static String RU_VOTE_LINK;
	public static long RU_VOTE_THREAD_DELAY;
	public static long RU_VOTE_APPEAR_DELAY;
	public static int RU_VOTE_WIPE_HOUR;
	public static SchedulingPattern RU_VOTE_PATTERN;
	public static long RU_VOTE_ANNOUCEMENT_DELAY;
	public static String RU_VOTE_SUCCESS_MSG;
	public static String RU_VOTE_FAILED_MSG;
	public static String RU_VOTE_PANEL_COMMAND;
	public static String RU_VOTE_PANEL_MSG;

	public static void loadRuVotesConfig()
	{
		final ExProperties ruVotesConfig = load(RU_VOTE_CONFIG_FILE);
		Config.ENABLE_RU_VOTE_SYSTEM = ruVotesConfig.getProperty("EnableRuVoteSystem", true);
		Config.RU_VOTE_LINK = ruVotesConfig.getProperty("RuVoteLink", "https://rf.mmotop.ru/votes/1b21089af10ea20786d638fe4a53d29151341fb8.txt?856161463b24d3c57cf9f1471b5f9a7e");
		Config.RU_VOTE_THREAD_DELAY = ruVotesConfig.getProperty("RuVoteThreadDelay", 2100000L);
		Config.RU_VOTE_APPEAR_DELAY = ruVotesConfig.getProperty("RuVoteAppearDelay", 7200000L);
		Config.RU_VOTE_WIPE_HOUR = ruVotesConfig.getProperty("RuVoteWipeHour", 21);
		Config.RU_VOTE_PATTERN = new SchedulingPattern("* " + Config.RU_VOTE_WIPE_HOUR + " * * *");
		Config.RU_VOTE_ANNOUCEMENT_DELAY = ruVotesConfig.getProperty("RuVoteAnnouncementDelay", 600000L);
		Config.RU_VOTE_SUCCESS_MSG = ruVotesConfig.getProperty("RuVoteSuccessMsg", "");
		Config.RU_VOTE_FAILED_MSG = ruVotesConfig.getProperty("RuVoteFailedMsg", "");
		Config.RU_VOTE_PANEL_COMMAND = ruVotesConfig.getProperty("RuVotePanelCommand", "vote2");
		Config.RU_VOTE_PANEL_MSG = ruVotesConfig.getProperty("RuVotePanelMsg", "");
	}

	public static final String ACHIEVEMENTS_CONFIG_FILE = "config/mod/Achievements.ini";
	public static boolean ENABLE_ACHIEVEMENTS;
	public static boolean ENABLE_PLAYER_COUNTERS;

	public static void loadAchievementsConfig()
	{
		final ExProperties achieveConfig = load(ACHIEVEMENTS_CONFIG_FILE);
		Config.ENABLE_ACHIEVEMENTS = achieveConfig.getProperty("EnableAchievements", false);
		Config.ENABLE_PLAYER_COUNTERS = achieveConfig.getProperty("EnablePlayerCounters", true);
	}

	/** Phantom players */
	public static final String PHANTOM_FILE = "config/phantom/Phantoms.ini";
	// Phantom players
	public static boolean PHANTOM_PLAYERS_ENABLED;
	public static String PHANTOM_PLAYERS_ACCOUNT;
	public static int PHANTOM_MAX_PLAYERS;
	public static int[] PHANTOM_BANNED_CLASSID;
	public static int[] PHANTOM_BANNED_SETID;
	public static int PHANTOM_MAX_WEAPON_GRADE;
	public static int PHANTOM_MAX_ARMOR_GRADE;
	public static int PHANTOM_MAX_JEWEL_GRADE;
	public static int PHANTOM_SPAWN_MAX;
	public static int PHANTOM_SPAWN_DELAY;
	public static int PHANTOM_MAX_LIFETIME;
	public static int CHANCE_TO_ENCHANT_WEAP;
	public static int MAX_ENCH_PHANTOM_WEAP;
	public static int PHANTOM_MAX_DRIFT_RANGE;
	public static boolean ALLOW_PHANTOM_CUSTOM_TITLES;
	public static int PHANTOM_CHANCE_SET_NOBLE_TITLE;
	public static boolean DISABLE_PHANTOM_ACTIONS;
	public static int[] PHANTOM_ALLOWED_NPC_TO_WALK;
	public static int PHANTOM_ROAMING_MAX_WH_CHECKS;
	public static int PHANTOM_ROAMING_MAX_WH_CHECKS_DWARF;
	public static int PHANTOM_ROAMING_MAX_SHOP_CHECKS;
	public static int PHANTOM_ROAMING_MAX_SHOP_CHECKS_DWARF;
	public static int PHANTOM_ROAMING_MAX_NPC_CHECKS;
	public static int PHANTOM_ROAMING_MIN_WH_DELAY;
	public static int PHANTOM_ROAMING_MAX_WH_DELAY;
	public static int PHANTOM_ROAMING_MIN_SHOP_DELAY;
	public static int PHANTOM_ROAMING_MAX_SHOP_DELAY;
	public static int PHANTOM_ROAMING_MIN_NPC_DELAY;
	public static int PHANTOM_ROAMING_MAX_NPC_DELAY;
	public static int PHANTOM_ROAMING_MIN_PRIVATESTORE_DELAY;
	public static int PHANTOM_ROAMING_MAX_PRIVATESTORE_DELAY;
	public static int PHANTOM_ROAMING_MIN_FREEROAM_DELAY;
	public static int PHANTOM_ROAMING_MAX_FREEROAM_DELAY;
	public static boolean DISABLE_PHANTOM_RESPAWN;
	public static boolean DEBUG_PHANTOMS;
	public static int[] PHANTOM_CLANS;

	public static void loadPhantomsConfig()
	{
		final ExProperties settings = load(PHANTOM_FILE);

		Config.PHANTOM_PLAYERS_ENABLED = settings.getProperty("PhantomPlayersEnabled", false);
		Config.PHANTOM_PLAYERS_ACCOUNT = settings.getProperty("PhantomPlayersAccount", "PhantomPlayerAI");
		Config.PHANTOM_MAX_PLAYERS = settings.getProperty("PhantomMaxPlayers", 1);
		Config.PHANTOM_BANNED_CLASSID = settings.getProperty("PhantomBannedClassIds", new int[] {});
		Config.PHANTOM_BANNED_SETID = settings.getProperty("PhantomBannedSetIds", new int[] {});
		Config.PHANTOM_MAX_WEAPON_GRADE = settings.getProperty("PhantomMaxWeaponGrade", 5);
		Config.PHANTOM_MAX_ARMOR_GRADE = settings.getProperty("PhantomMaxArmorGrade", 5);
		Config.PHANTOM_MAX_JEWEL_GRADE = settings.getProperty("PhantomMaxJewelGrade", 5);
		Config.PHANTOM_SPAWN_MAX = settings.getProperty("PhantomSpawnMax", 1);
		Config.PHANTOM_SPAWN_DELAY = settings.getProperty("PhantomSpawnDelay", 60);
		Config.PHANTOM_MAX_LIFETIME = settings.getProperty("PhantomMaxLifetime", 120);
		Config.CHANCE_TO_ENCHANT_WEAP = settings.getProperty("PhantomChanceEnchantWeap", 0);
		Config.MAX_ENCH_PHANTOM_WEAP = settings.getProperty("PhantomMaxEnchantWeap", 4);
		Config.PHANTOM_MAX_DRIFT_RANGE = settings.getProperty("MaxDriftRangeForNpc", 1000);
		Config.ALLOW_PHANTOM_CUSTOM_TITLES = settings.getProperty("AllowSetupCustomTitles", false);
		Config.PHANTOM_CHANCE_SET_NOBLE_TITLE = settings.getProperty("ChanceToSetTitle", 30);
		Config.DISABLE_PHANTOM_ACTIONS = settings.getProperty("DisablePhantomActions", false);
		Config.PHANTOM_ALLOWED_NPC_TO_WALK = settings.getProperty("PhantomRoamingNpcs", new int[] {});
		Config.PHANTOM_ROAMING_MAX_WH_CHECKS = settings.getProperty("PhantomRoamingMaxWhChecks", 2);
		Config.PHANTOM_ROAMING_MAX_WH_CHECKS_DWARF = settings.getProperty("PhantomRoamingMaxWhChecksDwarf", 8);
		Config.PHANTOM_ROAMING_MAX_SHOP_CHECKS = settings.getProperty("PhantomRoamingMaxShopChecks", 2);
		Config.PHANTOM_ROAMING_MAX_SHOP_CHECKS_DWARF = settings.getProperty("PhantomRoamingMaxShopChecksDwarf", 5);
		Config.PHANTOM_ROAMING_MAX_NPC_CHECKS = settings.getProperty("PhantomRoamingMaxNpcChecks", 6);
		Config.PHANTOM_ROAMING_MIN_WH_DELAY = settings.getProperty("PhantomRoamingMinWhDelay", 60);
		Config.PHANTOM_ROAMING_MAX_WH_DELAY = settings.getProperty("PhantomRoamingMaxWhDelay", 300);
		Config.PHANTOM_ROAMING_MIN_SHOP_DELAY = settings.getProperty("PhantomRoamingMinShopDelay", 30);
		Config.PHANTOM_ROAMING_MAX_SHOP_DELAY = settings.getProperty("PhantomRoamingMaxShopDelay", 120);
		Config.PHANTOM_ROAMING_MIN_NPC_DELAY = settings.getProperty("PhantomRoamingMinNpcDelay", 45);
		Config.PHANTOM_ROAMING_MAX_NPC_DELAY = settings.getProperty("PhantomRoamingMaxNpcDelay", 120);
		Config.PHANTOM_ROAMING_MIN_PRIVATESTORE_DELAY = settings.getProperty("PhantomRoamingMinPrivatestoreDelay", 2);
		Config.PHANTOM_ROAMING_MAX_PRIVATESTORE_DELAY = settings.getProperty("PhantomRoamingMaxPrivatestoreDelay", 7);
		Config.PHANTOM_ROAMING_MIN_FREEROAM_DELAY = settings.getProperty("PhantomRoamingMinFreeroamDelay", 10);
		Config.PHANTOM_ROAMING_MAX_FREEROAM_DELAY = settings.getProperty("PhantomRoamingMaxFreeroamDelay", 60);
		Config.DISABLE_PHANTOM_RESPAWN = settings.getProperty("DisablePhantomRespawn", false);
		Config.DEBUG_PHANTOMS = settings.getProperty("DebugPhantoms", false);
		Config.PHANTOM_CLANS = settings.getProperty("PhantomClans", new int[] {});
	}

	public static final String CAPTCHA_CONFIG_FILE = "config/mod/Captcha.ini";
	// Captcha system
	public static boolean ENABLE_CAPTCHA;
	public static boolean CAPTCHA_UNEQUIP;
	public static int CAPTCHA_MIN_MONSTERS;
	public static int CAPTCHA_MAX_MONSTERS;
	public static int CAPTCHA_ATTEMPTS;
	public static int CAPTCHA_SAME_LOCATION_DELAY;
	public static int CAPTCHA_SAME_LOCATION_MIN_KILLS;
	public static String CAPTCHA_FAILED_CAPTCHA_PUNISHMENT_TYPE;
	public static int CAPTCHA_FAILED_CAPTCHA_PUNISHMENT_TIME;
	public static int CAPTCHA_TIME_BETWEEN_TESTED_SECONDS;
	public static int CAPTCHA_TIME_BETWEEN_REPORTS_SECONDS;
	public static int CAPTCHA_MIN_LEVEL;

	public static void loadCaptchaConfig()
	{
		final ExProperties settings = load(CAPTCHA_CONFIG_FILE);

		Config.ENABLE_CAPTCHA = settings.getProperty("EnableCaptchaSystem", false);
		Config.CAPTCHA_UNEQUIP = settings.getProperty("CaptchaUnequipWeapon", false);
		Config.CAPTCHA_MIN_MONSTERS = settings.getProperty("CaptchaMinMonstertokill", 1000);
		Config.CAPTCHA_MAX_MONSTERS = settings.getProperty("CaptchaMaxMonstertokill", 2000);
		Config.CAPTCHA_ATTEMPTS = settings.getProperty("CaptchaAttempts", 3);
		Config.CAPTCHA_SAME_LOCATION_DELAY = settings.getProperty("CaptchaSameLocationDelay", 60);
		Config.CAPTCHA_SAME_LOCATION_MIN_KILLS = settings.getProperty("CaptchaSameLocationMinKills", 5);
		Config.CAPTCHA_FAILED_CAPTCHA_PUNISHMENT_TYPE = settings.getProperty("CaptchaPunishmentType", "BANCHAR");
		Config.CAPTCHA_FAILED_CAPTCHA_PUNISHMENT_TIME = settings.getProperty("CaptchaPunishmentTime", -1);
		Config.CAPTCHA_TIME_BETWEEN_TESTED_SECONDS = settings.getProperty("CaptchaDelayBetweenTests", 1800);
		Config.CAPTCHA_TIME_BETWEEN_REPORTS_SECONDS = settings.getProperty("CaptchaReportDelay", 7200);
		Config.CAPTCHA_MIN_LEVEL = settings.getProperty("CaptchaMinLevel", 40);
	}

	public static final String TRIVIA_CONFIG_FILE = "config/events/Trivia.properties";
	// Trivia event
	public static boolean TRIVIA_ENABLED;
	public static boolean TRIVIA_REMOVE_QUESTION;
	public static boolean TRIVIA_REMOVE_QUESTION_NO_ANSWER;
	public static int TRIVIA_START_TIME_HOUR;
	public static int TRIVIA_START_TIME_MIN;
	public static int TRIVIA_WORK_TIME;
	public static int TRIVIA_TIME_ANSER;
	public static int TRIVIA_TIME_PAUSE;
	public static String TRIVIA_REWARD_FIRST;
	public static String TRIVIA_REWARD_REST;

	public static void loadTriviaSettings()
	{
		final ExProperties TriviaSettings = load(TRIVIA_CONFIG_FILE);

		Config.TRIVIA_ENABLED = TriviaSettings.getProperty("Trivia_Enabled", false);
		Config.TRIVIA_REMOVE_QUESTION = TriviaSettings.getProperty("Trivia_Remove_Question", false);
		Config.TRIVIA_REMOVE_QUESTION_NO_ANSWER = TriviaSettings.getProperty("Trivia_Remove_Question_No_Answer", false);
		Config.TRIVIA_START_TIME_HOUR = TriviaSettings.getProperty("Trivia_Start_Time_Hour", 16);
		Config.TRIVIA_START_TIME_MIN = TriviaSettings.getProperty("Trivia_Start_Time_Minute", 16);
		Config.TRIVIA_WORK_TIME = TriviaSettings.getProperty("Trivia_Work_Time", 2);
		Config.TRIVIA_TIME_ANSER = TriviaSettings.getProperty("Trivia_Time_Answer", 1);
		Config.TRIVIA_TIME_PAUSE = TriviaSettings.getProperty("Trivia_Time_Pause", 1);
		Config.TRIVIA_REWARD_FIRST = TriviaSettings.getProperty("Trivia_Reward_First", "57,1,100;57,2,100;");
		Config.TRIVIA_REWARD_REST = TriviaSettings.getProperty("Trivia_Reward_Other", "57,1,100;57,2,100;");
	}

	public static final String UNDERGROUND_COLISEUM_CONFIG_FILE = "config/events/UndergroundColiseum.ini";
	public static boolean DEV_UNDERGROUND_COLISEUM;
	public static int UNDERGROUND_COLISEUM_MEMBER_COUNT;

	public static void loadUndergroundColiseumSettings()
	{
		final ExProperties coliseumSettings = load(UNDERGROUND_COLISEUM_CONFIG_FILE);

		Config.DEV_UNDERGROUND_COLISEUM = coliseumSettings.getProperty("DebugUndergroundColiseum", false);
		Config.UNDERGROUND_COLISEUM_MEMBER_COUNT = coliseumSettings.getProperty("UndergroundColiseumMemberCount", 7);
	}

	public static final String DAILY_QUESTS_CONFIG_FILE = "config/mod/DailyQuests.ini";

	public static boolean ENABLE_DAILY_QUESTS;
	public static boolean ENABLE_CLAN_REWARD;
	public static int CLAN_REWARD_MIN_LEVEL_FOR_REWARD;
	public static int CLAN_REWARD_MAX_LEVEL_FOR_REWARD;
	public static int CLAN_REWARD_MIN_ONLINE_FOR_REWARD;
	public static int CLAN_REWARD_LEVEL;
	public static int CLAN_REWARD_REPUTATION;
	public static boolean CLAN_REWARD_SKILLS;
	public static boolean ALLOW_DUALBOX_EPIC;

	public static void loadDailyQuestsSettings()
	{
		final ExProperties settings = load(DAILY_QUESTS_CONFIG_FILE);

		Config.ENABLE_DAILY_QUESTS = settings.getProperty("EnableDailyQuests", false);
		Config.ENABLE_CLAN_REWARD = settings.getProperty("EnableClanReward", false);
		Config.CLAN_REWARD_MIN_LEVEL_FOR_REWARD = settings.getProperty("ClanRewardMinLevelForReward", 0);
		Config.CLAN_REWARD_MAX_LEVEL_FOR_REWARD = settings.getProperty("ClanRewardMaxLevelForReward", 7);
		Config.CLAN_REWARD_MIN_ONLINE_FOR_REWARD = settings.getProperty("ClanRewardMinOnlineForReward", 0);
		Config.CLAN_REWARD_LEVEL = settings.getProperty("ClanRewardLevel", 7);
		Config.CLAN_REWARD_REPUTATION = settings.getProperty("ClanRewardReputation", 45000);
		Config.CLAN_REWARD_SKILLS = settings.getProperty("ClanRewardSkills", true);
	}

	public static String TWITCH_VOICE;
	public static String TWITCH_TOKEN;
	public static int TWITCH_MIN_MINUTE;
	public static int TWITCH_REWARD_EVERY;
	public static int TWITCH_MIN_VIEWERS;
	public static int[][] TWITCH_REWARD;
	public static String TWITCH_GAME_NAME;
	public static String TWITCH_SERVER_NAME;

	public static void loadTwitchSettings()
	{
		final ExProperties Twitchsettings = load(DAILY_QUESTS_CONFIG_FILE);

		Config.TWITCH_VOICE = Twitchsettings.getProperty("TwitchVoice", "twitch");
		Config.TWITCH_GAME_NAME = Twitchsettings.getProperty("TwitchGame", "L2Fierce");
		Config.TWITCH_SERVER_NAME = Twitchsettings.getProperty("TwitchServer", "L2Fierce");
		Config.TWITCH_TOKEN = Twitchsettings.getProperty("TwitchToken", "fwkef293gk23g23gs");
		Config.TWITCH_MIN_MINUTE = Twitchsettings.getProperty("TwitchMinStream", 60);
		Config.TWITCH_REWARD_EVERY = Twitchsettings.getProperty("TwitchRewardEvery", 60);
		Config.TWITCH_MIN_VIEWERS = Twitchsettings.getProperty("TwitchMinViewer", 0);
		Config.TWITCH_REWARD = parseItemsList(Twitchsettings.getProperty("TwitchReward", "6393,10"));
	}

	public static void load()
	{
		// Twitch fix
		loadTwitchSettings();

		loadServerConfig();
		loadTelnetConfig();
		loadResidenceConfig();
		loadOtherConfig();
		loadSpoilConfig();
		loadFormulasConfig();
		loadAltSettings();
		loadServicesSettings();
		loadPvPSettings();
		loadAISettings();
		loadGeodataSettings();
		loadEventsSettings();
		loadOlympiadSettings();
		loadDevelopSettings();
		loadExtSettings();
		loadTopSettings();
		loadRatesConfig();
		loadFightClubSettings();
		loadItemsUseConfig();
		loadSchemeBuffer();
		loadChatConfig();
		loadDonationStore();
		loadNpcConfig();
		loadBossConfig();
		loadEpicBossConfig();
		loadWeddingConfig();
		loadInstancesConfig();
		loadItemsSettings();
		abuseLoad();
		loadGMAccess();
		loadPremiumConfig();
		loadForgeSettings();
		loadPvPmodConfig();
		loadHitmanSettings();
		loadVIKTORINAsettings();
		if (ADVIPSYSTEM)
		{
			ipsLoad();
		}
		if (ALLOW_ADDONS_CONFIG)
		{
			AddonsConfig.load();
		}
		// Load Community Board
		loadCommunityPvPboardsettings();
		loadCommunityPvPbuffersettings();
		loadCommunityPvPclasssettings();
		loadCommunityPvPshopsettings();
		loadCommunityPvPteleportsettings();
		loadEnchantCBConfig();
		loadCommandssettings();
		loadBufferConfig();
		loadDonateConfig();
		loadTalkGuardConfig();
		loadAcc_moveConfig();
		loadTeamVSTeamSettings();
		loadKoreanStyleSettings();
		loadCaptureTheFlagSettings();

		// Synerge
		loadRaidEventConfig();
		loadBuffStoreConfig();
		loadRankingCBConfig();
		loadAntiFeedSettings();
		loadDonateRewardSettings();
		loadRuVotesConfig();
		loadAchievementsConfig();
		loadPhantomsConfig();
		loadCaptchaConfig();
		loadAugmentCBConfig();
		loadAcademyCBConfig();
		loadTriviaSettings();
		loadUndergroundColiseumSettings();
		loadDailyQuestsSettings();
	}

	private Config()
	{

	}

	public static void abuseLoad()
	{
		List<Pattern> tmp = new ArrayList<Pattern>();
		LineNumberReader lnr = null;
		try
		{
			String line;

			lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(ANUSEWORDS_CONFIG_FILE), "UTF-8"));

			while ((line = lnr.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(line, "\n\r");
				if (st.hasMoreTokens())
				{
					tmp.add(Pattern.compile(".*" + st.nextToken() + ".*", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
				}
			}

			ABUSEWORD_LIST = tmp.toArray(new Pattern[tmp.size()]);
			tmp.clear();
			_log.info("Abuse: Loaded " + ABUSEWORD_LIST.length + " abuse words.");
		}
		catch (IOException e1)
		{
			_log.warn("Error reading abuse: " + e1);
		}
		finally
		{
			try
			{
				if (lnr != null)
				{
					lnr.close();
				}
			}
			catch (Exception e2)
			{
				// nothing
			}
		}
	}

	public static void loadGMAccess()
	{
		gmlist.clear();
		loadGMAccess(new File(GM_PERSONAL_ACCESS_FILE));
		File dir = new File(GM_ACCESS_FILES_DIR);
		if (!dir.exists() || !dir.isDirectory())
		{
			_log.info("Dir " + dir.getAbsolutePath() + " not exists.");
			return;
		}
		for (File f : dir.listFiles())
		{
			// hidden Ñ„Ð°Ð¹Ð»Ñ‹ Ð�Ð• Ð¸Ð³Ð½Ð¾Ñ€Ð¸Ñ€ÑƒÐµÐ¼
			if (!f.isDirectory() && f.getName().endsWith(".xml"))
			{
				loadGMAccess(f);
			}
		}
	}

	public static void loadGMAccess(File file)
	{
		try
		{
			Field fld;
			// File file = new File(filename);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			Document doc = factory.newDocumentBuilder().parse(file);

			for (Node z = doc.getFirstChild(); z != null; z = z.getNextSibling())
			{
				for (Node n = z.getFirstChild(); n != null; n = n.getNextSibling())
				{
					if (!n.getNodeName().equalsIgnoreCase("char"))
					{
						continue;
					}

					PlayerAccess pa = new PlayerAccess();
					for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						Class<?> cls = pa.getClass();
						String node = d.getNodeName();

						if (node.equalsIgnoreCase("#text"))
						{
							continue;
						}

						// Synerge - Support to allow only the commands listed here
						if (node.equalsIgnoreCase("AllowedCommands"))
						{
							final String[] commands = d.getAttributes().getNamedItem("set").getNodeValue().split(";");
							for (String command : commands)
							{
								if (command.trim().isEmpty())
								{
									continue;
								}

								pa.AllowedCommands.add(command.trim());
							}
							continue;
						}

						try
						{
							fld = cls.getField(node);
						}
						catch (NoSuchFieldException e)
						{
							_log.info("Not found desclarate ACCESS name: " + node + " in XML Player access Object");
							continue;
						}

						if (fld.getType().getName().equalsIgnoreCase("boolean"))
						{
							fld.setBoolean(pa, Boolean.parseBoolean(d.getAttributes().getNamedItem("set").getNodeValue()));
						}
						else if (fld.getType().getName().equalsIgnoreCase("int"))
						{
							fld.setInt(pa, Integer.valueOf(d.getAttributes().getNamedItem("set").getNodeValue()));
						}
					}
					gmlist.put(pa.PlayerID, pa);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static String getField(String fieldName)
	{
		Field field = FieldUtils.getField(Config.class, fieldName);

		if (field == null)
		{
			return null;
		}

		try
		{
			return String.valueOf(field.get(null));
		}
		catch (IllegalArgumentException e)
		{

		}
		catch (IllegalAccessException e)
		{

		}

		return null;
	}

	public static boolean setField(String fieldName, String value)
	{
		Field field = FieldUtils.getField(Config.class, fieldName);

		if (field == null)
		{
			return false;
		}

		try
		{
			if (field.getType() == boolean.class)
			{
				field.setBoolean(null, BooleanUtils.toBoolean(value));
			}
			else if (field.getType() == int.class)
			{
				field.setInt(null, NumberUtils.toInt(value));
			}
			else if (field.getType() == long.class)
			{
				field.setLong(null, NumberUtils.toLong(value));
			}
			else if (field.getType() == double.class)
			{
				field.setDouble(null, NumberUtils.toDouble(value));
			}
			else if (field.getType() == String.class)
			{
				field.set(null, value);
			}
			else
			{
				return false;
			}
		}
		catch (IllegalArgumentException e)
		{
			return false;
		}
		catch (IllegalAccessException e)
		{
			return false;
		}

		return true;
	}

	public static ExProperties load(String filename)
	{
		return load(new File(filename));
	}

	public static ExProperties load(File file)
	{
		ExProperties result = new ExProperties();

		try
		{
			result.load(file);
		}
		catch (IOException e)
		{
			_log.error("Error loading config : " + file.getName() + "!", e);
		}

		return result;
	}

	public static boolean containsAbuseWord(String s)
	{
		for (Pattern pattern : ABUSEWORD_LIST)
		{
			if (pattern.matcher(s).matches())
			{
				return true;
			}
		}
		return false;
	}

	private static void ipsLoad()
	{
		ExProperties ipsSettings = load(ADV_IP_FILE);

		for (int i = 0; i < (ipsSettings.size()); i++)
		{
			int channelId = (i + 2);

			String channels = ipsSettings.getProperty("Channel" + channelId, "-1");
			if (channels.equals("-1"))
			{
				continue;
			}

			AdvIP advip = new AdvIP();
			advip.channelId = channelId;
			advip.channelAdress = channels.split(";")[0];
			advip.channelPort = Integer.parseInt(channels.split(";")[1]);
			GAMEIPS.add(advip);

			_log.info("Added Proxy Channel: " + advip.channelId + " - " + advip.channelAdress + ":" + advip.channelPort);
		}
	}

	public static final File findResource(final String path)
	{
		return findNonCustomResource(path);
	}

	public static final File findNonCustomResource(final String path)
	{
		File file = new File(DATAPACK_ROOT, path);
		if (!file.exists())
		{
			file = new File(path);
		}
		return file;
	}
}