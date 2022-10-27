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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import l2mv.gameserver.com.l2jserver.gameserver.masteriopack.imageconverter.ServerSideImage;
import l2mv.gameserver.kara.vote.StringUtil;

//import com.l2jserver.gameserver.l2mv.gameserver.masteriopack.imageconverter.ServerSideImage;

//import l2mv.gameserver.kara.vote.StringUtil;

/**
 * This class initializes all global variables for configuration.<br>
 * If the key doesn't appear in properties file, a default value is set by this class.<br>
 * @author Masterio
 */
public final class RPSConfig
{
	private static final Logger log = Logger.getLogger(RPSConfig.class.getName());

	// Rank PvP System:
	public static boolean RANK_PVP_SYSTEM_ENABLED;
	public static int LEGAL_KILL_MIN_LVL;
	public static boolean PVP_COUNTER_FOR_ALTT_ENABLED;
	public static boolean PVP_COUNTER_FOR_ALTT_LEGAL_KILLS_ONLY;
	public static boolean LEGAL_KILL_FOR_PK_KILLER_ENABLED;
	public static boolean LEGAL_KILL_FOR_INNOCENT_KILL_ENABLED;
	public static int PROTECTION_TIME_RESET;
	public static int LEGAL_KILL_PROTECTION;
	public static int DAILY_LEGAL_KILL_PROTECTION;

	public static boolean GM_IGNORE_ENABLED;

	// PvP + RPC Reward:
	public static boolean RPC_REWARD_ENABLED;
	public static long RPC_REWARD_AMOUNT;
	public static int RPC_REWARD_MIN_LVL;

	public static boolean PVP_REWARD_ENABLED;
	public static int PVP_REWARD_ID;
	public static long PVP_REWARD_AMOUNT;
	public static int PVP_REWARD_MIN_LVL;

	public static boolean REWARD_FOR_PK_KILLER_ENABLED;
	public static boolean REWARD_FOR_INNOCENT_KILL_ENABLED;

	public static boolean RANK_PVP_REWARD_ENABLED;
	public static int RANK_PVP_REWARD_MIN_LVL;
	public static boolean REWARD_LEGAL_KILL_ENABLED;
	public static boolean RANK_LEVEL_REWARD_ENABLED;
	public static boolean RANK_SKILL_REWARD_ENABLED;

	// Ranks:
	public static boolean RANKS_ENABLED;
	public static int RANK_POINTS_MIN_LVL;
	public static boolean RANK_POINTS_CUT_ENABLED;
	public static boolean RANK_RPC_ENABLED;
	public static boolean RANK_RPC_CUT_ENABLED;

	public static boolean RANK_POINTS_DOWN_COUNT_ENABLED;
	public static List<Integer> RANK_POINTS_DOWN_AMOUNTS = new ArrayList<>();

	public static boolean RANK_SHOUT_INFO_ON_KILL_ENABLED;
	public static boolean RANK_SHOUT_BONUS_INFO_ON_KILL_ENABLED;

	public static boolean RPC_EXCHANGE_ENABLED;
	public static boolean RPC_EXCHANGE_CONFIRM_BOX_ENABLED;

	public static boolean PVP_EXP_DECREASE_ENABLED;
	public static int PVP_EXP_DECREASE_METHOD;
	public static int PVP_EXP_DECREASE_CONSTANT;
	public static double PVP_EXP_DECREASE_FRACTION;
	public static boolean PVP_EXP_DECREASE_ON_LEGAL_KILL_ENABLED;

	// War Kills:
	public static boolean WAR_KILLS_ENABLED;
	public static double WAR_RANK_POINTS_RATIO;

	// Combo Kill:
	public static boolean COMBO_KILL_ENABLED;
	public static boolean COMBO_KILL_PROTECTION_WITH_LEGAL_KILL_ENABLED;
	public static boolean COMBO_KILL_PROTECTION_NO_REPEAT_ENABLED;

	public static Map<Integer, String> COMBO_KILL_LOCAL_AREA_MESSAGES = new HashMap<>();
	public static Map<Integer, String> COMBO_KILL_GLOBAL_AREA_MESSAGES = new HashMap<>();

	public static boolean COMBO_KILL_ALT_MESSAGES_ENABLED;
	public static String COMBO_KILL_ALT_MESSAGE;
	public static int COMBO_KILL_ALT_GLOBAL_MESSAGE_LVL;

	public static boolean COMBO_KILL_DEFEAT_MESSAGE_ENABLED;
	public static int COMBO_KILL_DEFEAT_MESSAGE_MIN_LVL;
	public static String COMBO_KILL_DEFEAT_MESSAGE;

	public static int COMBO_KILL_RESETER;
	public static boolean COMBO_KILL_RANK_POINTS_RATIO_ENABLED;
	public static Map<Integer, Double> COMBO_KILL_RANK_POINTS_RATIO = new HashMap<>();

	// Title & Nick Color:
	public static boolean NICK_COLOR_ENABLED;
	public static boolean TITLE_COLOR_ENABLED;

	// Zones:
	public static List<Integer> ALLOWED_ZONES_IDS = new ArrayList<>();
	public static List<Integer> RESTRICTED_ZONES_IDS = new ArrayList<>();
	public static List<Integer> DEATH_MANAGER_RESTRICTED_ZONES_IDS = new ArrayList<>();
	public static Map<Integer, Double> RANK_POINTS_BONUS_ZONES_IDS = new HashMap<>();

	// pvpinfo command, pvp status window, death manager:
	public static boolean PVP_INFO_COMMAND_ENABLED;
	public static boolean PVP_INFO_USER_COMMAND_ENABLED;
	public static int PVP_INFO_USER_COMMAND_ID;

	public static boolean PVP_INFO_COMMAND_ON_DEATH_ENABLED;
	public static boolean DEATH_MANAGER_DETAILS_ENABLED;
	public static boolean DEATH_MANAGER_SHOW_ITEMS_ENABLED;

	public static boolean TOTAL_KILLS_IN_SHOUT_ENABLED;
	public static boolean TOTAL_KILLS_IN_PVPINFO_ENABLED;
	public static boolean TOTAL_KILLS_ON_ME_IN_PVPINFO_ENABLED;
	public static boolean SHOW_PLAYER_LEVEL_IN_PVPINFO_ENABLED;

	// Anti-Farm:
	public static boolean ANTI_FARM_CLAN_ALLY_ENABLED;
	public static boolean ANTI_FARM_PARTY_ENABLED;
	public static boolean ANTI_FARM_IP_ENABLED;

	// Top List (Community Board):
	public static boolean TOP_LIST_ENABLED;
	public static long TOP_LIST_IGNORE_TIME_LIMIT;

	// Database:
	public static long PVP_TABLE_UPDATE_INTERVAL;
	public static List<Long> TOP_TABLE_UPDATE_TIMES = new ArrayList<>();
	public static boolean RPC_TABLE_FORCE_UPDATE_ENABLED;

	public static boolean DATABASE_CLEANER_ENABLED;
	public static long DATABASE_CLEANER_REPEAT_TIME;

	// Image:
	public static boolean SERVER_SIDE_IMAGES_ENABLED;
	public static int IMAGE_PREFIX;

	// Button style:
	public static String BUTTON_UP;
	public static String BUTTON_DOWN;
	public static String BUTTON_W;
	public static String BUTTON_H;
	public static String BUTTON_BIG_W;
	public static String BUTTON_BIG_H;

	/**
	 * All required tables are initialized here.
	 */
	public static void load()
	{
		log.info("> Initializing Rank PvP System (" + RANK_PVP_SYSTEM_VERSION + "):");

		try
		{
			File cc = new File(RANK_PVP_SYSTEM_CONFIG_FILE);
			InputStream is = new FileInputStream(cc);
			Properties ccSettings = new Properties();
			ccSettings.load(is);

			RANK_PVP_SYSTEM_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RankPvpSystemEnabled", "false"));
			if (!RANK_PVP_SYSTEM_ENABLED)
			{
				log.info(" - Rank PvP System: Disabled");
				return;
			}

			PVP_COUNTER_FOR_ALTT_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("PvpCounterForAltTEnabled", "false"));
			PVP_COUNTER_FOR_ALTT_LEGAL_KILLS_ONLY = Boolean.parseBoolean(ccSettings.getProperty("PvpCounterForAltTLegalKillsOnly", "false"));

			DATABASE_CLEANER_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("DatabaseCleanerEnabled", "false"));
			DATABASE_CLEANER_REPEAT_TIME = Integer.parseInt(ccSettings.getProperty("DatabaseCleanerRepeatTime", "0"));
			if (DATABASE_CLEANER_REPEAT_TIME <= 0)
			{
				DATABASE_CLEANER_ENABLED = false;
				log.warning("[DatabaseCleanerRepeatTime]: invalid config property -> \"" + DATABASE_CLEANER_REPEAT_TIME + "\"");
			}
			else
			{
				DATABASE_CLEANER_REPEAT_TIME *= 86400000;
			}

			RPC_REWARD_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RpcRewardEnabled", "false"));
			RPC_REWARD_AMOUNT = Integer.parseInt(ccSettings.getProperty("RpcRewardAmmount", "1"));
			RPC_REWARD_MIN_LVL = Integer.parseInt(ccSettings.getProperty("RpcRewardMinLvl", "76"));

			PVP_REWARD_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("PvpRewardEnabled", "false"));
			PVP_REWARD_ID = Integer.parseInt(ccSettings.getProperty("PvpRewardId", "57"));
			PVP_REWARD_AMOUNT = Integer.parseInt(ccSettings.getProperty("PvpRewardAmount", "1"));
			PVP_REWARD_MIN_LVL = Integer.parseInt(ccSettings.getProperty("PvpRewardMinLvl", "76"));

			REWARD_FOR_PK_KILLER_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RewardForPkKillerEnabled", "true"));
			RANK_PVP_REWARD_MIN_LVL = Integer.parseInt(ccSettings.getProperty("RankPvpRewardMinLvl", "76"));
			REWARD_FOR_INNOCENT_KILL_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RewardForInnocentKillEnabled", "false"));

			RANK_PVP_REWARD_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RankPvpRewardEnabled", "true"));
			REWARD_LEGAL_KILL_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RewardLegalKillEnabled", "true"));
			RANK_LEVEL_REWARD_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RankLevelRewardEnabled", "true"));
			RANK_SKILL_REWARD_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RankSkillRewardEnabled", "true"));

			RANKS_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RanksEnabled", "false"));
			RANK_POINTS_MIN_LVL = Integer.parseInt(ccSettings.getProperty("RankPointsMinLvl", "76"));
			RANK_POINTS_CUT_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RankPointsCutEnabled", "true"));

			RANK_RPC_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RankRpcEnabled", "false"));
			RANK_RPC_CUT_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RankRpcCutEnabled", "true"));

			// set rank's:
			String id1[] = ccSettings.getProperty("RankNames", "").split(",");
			String id2[] = ccSettings.getProperty("RankMinPoints", "").split(",");
			String id3[] = ccSettings.getProperty("RankPointsForKill", "").split(",");
			String id5[] = ccSettings.getProperty("RankRpcAmount", "").split(",");
			String id6[] = ccSettings.getProperty("NickColors", "").split(",");
			String id7[] = ccSettings.getProperty("TitleColors", "").split(",");

			if (RANK_PVP_SYSTEM_ENABLED || RANK_RPC_ENABLED || RANKS_ENABLED)
			{
				if (id1.length != id2.length || id1.length != id3.length || id1.length != id5.length || id1.length != id6.length || id1.length != id7.length)
				{
					log.info("[RankPvpSystemConfig]: Arrays sizes should be the same!");

					log.info("RANK_NAMES         		 :" + id1.length);
					log.info("RANK_MIN_POINTS    		 :" + id2.length);
					log.info("RANK_POINTS_FOR_KILL		 :" + id3.length);
					log.info("RANK_RPC_AMOUNT		 	 :" + id5.length);
					log.info("RANK_NICK_COLORS    		 :" + id6.length);
					log.info("RANK_TITLE_COLORS  		 :" + id7.length);
				}
				else if (id1.length == 0 || id2.length == 0 || id3.length == 0 || id5.length == 0 || id6.length == 0 || id7.length == 0)
				{
					log.info("[RankPvpSystemConfig]: Arrays sizes must be greater than 0!");

					log.info("RANK_NAMES         		 :" + id1.length);
					log.info("RANK_MIN_POINTS    		 :" + id2.length);
					log.info("RANK_POINTS_FOR_KILL		 :" + id3.length);
					log.info("RANK_RPC_AMOUNT		 	 :" + id5.length);
					log.info("RANK_NICK_COLORS    		 :" + id6.length);
					log.info("RANK_TITLE_COLORS  		 :" + id7.length);
				}
				else if (id2.length > 0 && Integer.parseInt(id2[id2.length - 1]) != 0)
				{
					log.info("[RankMinPoints]: Last value must equal 0! Example: ...,6,5,4,3,2,1,0");
				}
				else
				{
					for (int i = 1; i <= id1.length; i++)
					{
						Rank rank = new Rank();

						rank.setId(i);
						rank.setName(id1[id1.length - i]);
						rank.setMinPoints(Long.parseLong(id2[id1.length - i]));
						rank.setPointsForKill(Integer.parseInt(id3[id1.length - i]));

						rank.setRpc(Integer.parseInt(id5[id1.length - i]));
						rank.setNickColor(Integer.decode("0x" + id6[id1.length - i]));
						rank.setTitleColor(Integer.decode("0x" + id7[id1.length - i]));

						RankTable.getInstance().getRankList().put(i, rank);
					}
				}
			}

			NICK_COLOR_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("NickColorEnabled", "false"));
			TITLE_COLOR_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("TitleColorEnabled", "false"));

			RANK_POINTS_DOWN_COUNT_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RankPointsDownCountEnabled", "false"));
			RANK_POINTS_DOWN_AMOUNTS = new ArrayList<>();
			for (String id : ccSettings.getProperty("RankPointsDownAmounts", "").split(","))
			{
				RANK_POINTS_DOWN_AMOUNTS.add(Integer.parseInt(id));
			}

			RANK_SHOUT_INFO_ON_KILL_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RankShoutInfoOnKillEnabled", "false"));
			RANK_SHOUT_BONUS_INFO_ON_KILL_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RankShoutBonusInfoOnKillEnabled", "false"));

			WAR_KILLS_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("WarKillsEnabled", "false"));
			if (WAR_KILLS_ENABLED)
			{
				WAR_RANK_POINTS_RATIO = Double.parseDouble(ccSettings.getProperty("WarRankPointsRatio", "1.0"));
			}
			else
			{
				WAR_RANK_POINTS_RATIO = 1.0;
			}

			COMBO_KILL_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("ComboKillEnabled", "false"));
			COMBO_KILL_PROTECTION_WITH_LEGAL_KILL_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("ComboKillProtectionWithLegalKillEnabled", "false"));
			COMBO_KILL_PROTECTION_NO_REPEAT_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("ComboKillProtectionNoRepeatEnabled", "false"));

			PVP_EXP_DECREASE_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("PvpExpDecreaseEnabled", "false"));
			PVP_EXP_DECREASE_METHOD = Integer.parseInt(ccSettings.getProperty("PvpExpDecreaseMethod", "1"));
			if (PVP_EXP_DECREASE_METHOD <= 0 || PVP_EXP_DECREASE_METHOD > 3)
			{
				PVP_EXP_DECREASE_METHOD = 1;
			}

			PVP_EXP_DECREASE_CONSTANT = Integer.parseInt(ccSettings.getProperty("PvpExpDecreaseConstant", "1"));
			if (PVP_EXP_DECREASE_CONSTANT <= 0)
			{
				PVP_EXP_DECREASE_CONSTANT = 1;
			}

			PVP_EXP_DECREASE_FRACTION = Double.parseDouble(ccSettings.getProperty("PvpExpDecreaseFraction", "1.0"));
			if (PVP_EXP_DECREASE_FRACTION <= 0.0 || PVP_EXP_DECREASE_FRACTION > 1.0)
			{
				PVP_EXP_DECREASE_FRACTION = 1.0;
			}

			PVP_EXP_DECREASE_ON_LEGAL_KILL_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("PvpExpDecreaseOnLegalKillEnabled", "true"));

			String propertyValue = ccSettings.getProperty("ComboKillLocalAreaMessages");
			if ((propertyValue != null) && (propertyValue.length() > 0))
			{
				String[] propertySplit = propertyValue.split(";");
				if (propertySplit.length > 0)
				{
					for (String value : propertySplit)
					{
						String[] valueSplit = value.split(",");
						if (valueSplit.length != 2)
						{
							log.warning(StringUtil.concat("[ComboKillLocalAreaMessages]: invalid config property -> \"", value, "\""));
						}
						else
						{
							try
							{
								COMBO_KILL_LOCAL_AREA_MESSAGES.put(Integer.parseInt(valueSplit[0]), valueSplit[1]);
							}
							catch (NumberFormatException nfe)
							{
								if (!value.isEmpty())
								{
									log.warning(StringUtil.concat("[ComboKillLocalAreaMessages]: invalid config property -> \"", valueSplit[0], "\"", valueSplit[1]));
								}
							}
						}
					}
				}
			}

			propertyValue = ccSettings.getProperty("ComboKillGlobalAreaMessages", "");
			if ((propertyValue != null) && (propertyValue.length() > 0))
			{
				String[] propertySplit = ccSettings.getProperty("ComboKillGlobalAreaMessages").split(";");
				if (propertySplit.length > 0)
				{
					for (String value : propertySplit)
					{
						String[] valueSplit = value.split(",");
						if (valueSplit.length != 2)
						{
							log.warning(StringUtil.concat("[ComboKillGlobalAreaMessages]: invalid config property -> \"", value, "\""));
						}
						else
						{
							try
							{
								COMBO_KILL_GLOBAL_AREA_MESSAGES.put(Integer.parseInt(valueSplit[0]), valueSplit[1]);
							}
							catch (NumberFormatException nfe)
							{
								if (!value.isEmpty())
								{
									log.warning(StringUtil.concat("[ComboKillGlobalAreaMessages]: invalid config property -> \"", valueSplit[0], "\"", valueSplit[1]));
								}
							}
						}
					}
				}
			}

			COMBO_KILL_ALT_MESSAGES_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("ComboKillAltMessagesEnabled", "false"));
			COMBO_KILL_ALT_MESSAGE = ccSettings.getProperty("ComboKillAltMessage", "%killer% have %combo_level% Combo kills!");
			COMBO_KILL_ALT_GLOBAL_MESSAGE_LVL = Integer.parseInt(ccSettings.getProperty("ComboKillAltGlobalMessageMinLvl", "0"));

			COMBO_KILL_DEFEAT_MESSAGE_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("ComboKillDefeatMessageEnabled", "true"));
			COMBO_KILL_DEFEAT_MESSAGE_MIN_LVL = Integer.parseInt(ccSettings.getProperty("ComboKillDefeatMessageMinComboLvl", "0"));
			COMBO_KILL_DEFEAT_MESSAGE = ccSettings.getProperty("ComboKillDefeatMessage", "%killer% is defeated with %combo_level% combo lvl!!!");

			COMBO_KILL_RESETER = Integer.parseInt(ccSettings.getProperty("ComboKillReseter", "0"));
			COMBO_KILL_RANK_POINTS_RATIO_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("ComboKillRankPointsRatioEnabled", "false"));

			propertyValue = ccSettings.getProperty("ComboKillRankPointsRatio", "");
			if ((propertyValue != null) && (propertyValue.length() > 0))
			{
				String[] propertySplit = ccSettings.getProperty("ComboKillRankPointsRatio").split(";");
				if (propertySplit.length > 0)
				{
					for (String value : propertySplit)
					{
						String[] valueSplit = value.split(",");
						if (valueSplit.length != 2)
						{
							log.warning(StringUtil.concat("[ComboKillRankPointsRatio]: invalid config property -> \"", value, "\""));
						}
						else
						{
							try
							{
								COMBO_KILL_RANK_POINTS_RATIO.put(Integer.parseInt(valueSplit[0]), Double.parseDouble(valueSplit[1]));
							}
							catch (NumberFormatException nfe)
							{
								if (!value.isEmpty())
								{
									log.warning(StringUtil.concat("[ComboKillRankPointsRatio]: invalid config property -> \"", valueSplit[0], "\"", valueSplit[1]));
								}
							}
						}
					}
				}
			}

			// additional security for combo kill system:
			if ((COMBO_KILL_LOCAL_AREA_MESSAGES.size() == 0) && (COMBO_KILL_GLOBAL_AREA_MESSAGES.size() == 0))
			{
				COMBO_KILL_ENABLED = false;
			}

			int i = 0;
			String tempStr = ccSettings.getProperty("AllowedZonesIds");
			if ((tempStr != null) && (tempStr.length() > 0))
			{
				for (String rZoneId : tempStr.split(","))
				{
					try
					{
						ALLOWED_ZONES_IDS.add(i, Integer.parseInt(rZoneId));
					}
					catch (Exception e)
					{
						log.info(e.getMessage());
					}
					i++;
				}
			}

			i = 0;
			tempStr = ccSettings.getProperty("RestrictedZonesIds");
			if ((tempStr != null) && (tempStr.length() > 0))
			{
				for (String rZoneId : tempStr.split(","))
				{
					try
					{
						RESTRICTED_ZONES_IDS.add(i, Integer.parseInt(rZoneId));
					}
					catch (Exception e)
					{
						log.info(e.getMessage());
					}
					i++;
				}
			}
			LEGAL_KILL_MIN_LVL = Integer.parseInt(ccSettings.getProperty("LegalKillMinLvl", "1"));
			LEGAL_KILL_FOR_PK_KILLER_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("LegalKillForPkKillerEnabled", "true"));
			LEGAL_KILL_FOR_INNOCENT_KILL_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("LegalKillForInnocentKillEnabled", "false"));
			PROTECTION_TIME_RESET = Integer.parseInt(ccSettings.getProperty("ProtectionTimeReset", "0"));
			if (PROTECTION_TIME_RESET > 0)
			{
				PROTECTION_TIME_RESET = PROTECTION_TIME_RESET * 60000;
			}
			else if (PROTECTION_TIME_RESET < 0)
			{
				PROTECTION_TIME_RESET = 0;
			}

			LEGAL_KILL_PROTECTION = Integer.parseInt(ccSettings.getProperty("LegalKillProtection", "0"));
			DAILY_LEGAL_KILL_PROTECTION = Integer.parseInt(ccSettings.getProperty("DailyLegalKillProtection", "0"));

			GM_IGNORE_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("GMIgnoreEnabled", "true"));

			PVP_INFO_COMMAND_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("PvpInfoCommandEnabled", "true"));
			PVP_INFO_USER_COMMAND_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("PvpInfoUserCommandEnabled", "false"));
			PVP_INFO_USER_COMMAND_ID = Integer.parseInt(ccSettings.getProperty("PvpInfoUserCommandId", "114"));

			PVP_INFO_COMMAND_ON_DEATH_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("PvpInfoCommandShowOnDeathEnabled", "true"));
			DEATH_MANAGER_DETAILS_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("DeathManagerDetailsEnabled", "true"));
			DEATH_MANAGER_SHOW_ITEMS_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("DeathManagerShowItemsEnabled", "true"));

			i = 0;
			tempStr = ccSettings.getProperty("DeathManagerRestrictedZonesIds");
			if ((tempStr != null) && (tempStr.length() > 0))
			{
				for (String rZoneId : tempStr.split(","))
				{
					try
					{
						DEATH_MANAGER_RESTRICTED_ZONES_IDS.add(i, Integer.parseInt(rZoneId));
					}
					catch (Exception e)
					{
						log.info(e.getMessage());
					}
					i++;
				}
			}

			propertyValue = ccSettings.getProperty("RankPointsBonusZonesIds", "");
			if ((propertyValue != null) && (propertyValue.length() > 0))
			{
				String[] propertySplit = ccSettings.getProperty("RankPointsBonusZonesIds", "").split(";");
				if (propertySplit.length > 0)
				{
					for (String value : propertySplit)
					{
						String[] valueSplit = value.split(",");
						if (valueSplit.length != 2)
						{
							log.warning(StringUtil.concat("[RankPointsBonusZonesIds]: invalid config property -> \"", value, "\""));
						}
						else
						{
							try
							{
								RANK_POINTS_BONUS_ZONES_IDS.put(Integer.parseInt(valueSplit[0]), Double.parseDouble(valueSplit[1]));
							}
							catch (NumberFormatException nfe)
							{
								if (!value.isEmpty())
								{
									log.warning(StringUtil.concat("[RankPointsBonusZonesIds]: invalid config property -> \"", valueSplit[0], "\"", valueSplit[1]));
								}
							}
						}
					}
				}
			}

			TOTAL_KILLS_IN_SHOUT_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("TotalKillsInShoutEnabled", "true"));
			TOTAL_KILLS_IN_PVPINFO_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("TotalKillsInPvpInfoEnabled", "true"));
			TOTAL_KILLS_ON_ME_IN_PVPINFO_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("TotalKillsOnMeInPvpInfoEnabled", "true"));
			SHOW_PLAYER_LEVEL_IN_PVPINFO_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("ShowPlayerLevelInPvpInfoEnabled", "true"));

			RPC_EXCHANGE_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RpcExchangeEnabled", "true"));
			RPC_EXCHANGE_CONFIRM_BOX_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RpcExchangeConfirmBoxEnabled", "true"));

			ANTI_FARM_CLAN_ALLY_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("AntiFarmClanAllyEnabled", "true"));
			ANTI_FARM_PARTY_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("AntiFarmPartyEnabled", "true"));
			ANTI_FARM_IP_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("AntiFarmIpEnabled", "true"));

			PVP_TABLE_UPDATE_INTERVAL = (Integer.parseInt(ccSettings.getProperty("PvpTableUpdateInterval", "1")) * 60000);
			if (PVP_TABLE_UPDATE_INTERVAL < 1)
			{
				log.warning(StringUtil.concat("[PvpTableUpdateInterval]: invalid config property -> \"", Long.toString(PVP_TABLE_UPDATE_INTERVAL), "\""));
				PVP_TABLE_UPDATE_INTERVAL = 60000;
			}
			RPC_TABLE_FORCE_UPDATE_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RpcTableForceUpdate", "true"));

			// top table update times:
			propertyValue = ccSettings.getProperty("TopTableUpdateTimes", "3:00");
			List<Long> temp_time_list = new ArrayList<>();
			if ((propertyValue != null) && (propertyValue.length() > 0))
			{
				String[] propertySplit = ccSettings.getProperty("TopTableUpdateTimes", "").split(",");
				if (propertySplit.length > 0)
				{
					for (String value : propertySplit)
					{
						String[] hm = value.split(":");
						if (hm.length != 2) // hm table length
						{
							log.warning(StringUtil.concat("[TopTableUpdateTimes]: invalid config property -> \"", value, "\""));
						}
						else
						{
							try
							{
								// prepare h & m:
								String h_s = hm[0];
								String m_s = hm[1];

								if (m_s.length() != 2)
								{
									log.warning(StringUtil.concat("[TopTableUpdateTimes]: invalid config property -> \"", value, "\" minutes format incorrect [hh:mm]"));
									break;
								}

								// check times like: [0]3:15, 3:[0]5.
								if (h_s.startsWith("0") && h_s.length() == 2)
								{
									h_s = h_s.substring(1);
								}
								if (m_s.startsWith("0") && m_s.length() == 2)
								{
									m_s = m_s.substring(1);
								}
								if (h_s.equals("0"))
								{
									h_s = "24";
								}

								// calculate all times for times in ms from time 0:00.
								long h = Long.parseLong(h_s);
								long m = Long.parseLong(m_s);

								if (h < 0 || m < 0 || h > 24 || m > 59)
								{
									log.warning(StringUtil.concat("[TopTableUpdateTimes]: invalid config property -> \"", value, "\" minutes format incorrect [hh:mm]"));
									break;
								}

								long mili = (h * 60 + m) * 60000 - (3600000); // - 1h

								if (mili >= 0 && !temp_time_list.contains(mili))
								{
									temp_time_list.add(mili);
								}
							}
							catch (NumberFormatException nfe)
							{
							}
						}
					}
				}
			}

			class LongComparator implements Comparator<Long>
			{
				@Override
				public int compare(Long v1, Long v2)
				{
					if (v1 > v2)
					{
						return 1;
					}
					else if (v1 == v2)
					{
						return 0;
					}
					else
					{
						return -1;
					}
				}
			}

			Comparator<Long> comp = new LongComparator();
			Long[] aol = new Long[temp_time_list.size()];
			Arrays.sort(temp_time_list.toArray(aol), comp);
			TOP_TABLE_UPDATE_TIMES = Arrays.asList(aol);

			TOP_LIST_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("TopListEnabled", "true"));
			TOP_LIST_IGNORE_TIME_LIMIT = Integer.parseInt(ccSettings.getProperty("TopListIgnoreTimeLimit", "0"));
			if (TOP_LIST_IGNORE_TIME_LIMIT > 0)
			{
				TOP_LIST_IGNORE_TIME_LIMIT *= 86400000; // in milliseconds
			}

			SERVER_SIDE_IMAGES_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("ServerSideImagesEnabled", "true"));
			IMAGE_PREFIX = Integer.parseInt(ccSettings.getProperty("ImagePrefix", "1"));

			// Buttons style:
			BUTTON_UP = ccSettings.getProperty("ButtonFore", "L2UI_ch3.BigButton3_over");
			BUTTON_DOWN = ccSettings.getProperty("ButtonBack", "L2UI_ch3.BigButton3");
			BUTTON_W = ccSettings.getProperty("ButtonWidth", "134");
			BUTTON_H = ccSettings.getProperty("ButtonHeight", "21");
			BUTTON_BIG_W = ccSettings.getProperty("ButtonBigWidth", "180");
			BUTTON_BIG_H = ccSettings.getProperty("ButtonBigHeight", "24");

			log.warning(" - Rank Pvp System Config initialization complete.");
		}
		catch (Exception e)
		{
			log.warning("Config: " + e.getMessage());
			throw new Error("Failed to Load " + RANK_PVP_SYSTEM_CONFIG_FILE + " File.");
		}

		// initializing system
		PvpTable.getInstance();

		if (RPC_REWARD_ENABLED || RANK_RPC_ENABLED || RPC_TABLE_FORCE_UPDATE_ENABLED)
		{
			RPCTable.getInstance();
		}
		else
		{
			log.info(" - RPCTable: Disabled, players RPC will be not updated!");
		}

		if (RPC_REWARD_ENABLED || RPC_EXCHANGE_ENABLED)
		{
			RPCRewardTable.getInstance();
		}
		else
		{
			log.info(" - RPCRewardTable: Disabled.");
		}

		if (PVP_REWARD_ENABLED || RANK_PVP_REWARD_ENABLED)
		{
			RewardTable.getInstance();
		}
		else
		{
			log.info(" - RewardTable: Disabled.");
		}

		if (TOP_LIST_ENABLED)
		{
			TopTable.getInstance();
		}
		else
		{
			log.info(" - TopTable: Disabled.");
		}

		ServerSideImage.getInstance();
	}

	// --------------------------------------------------
	// Constants - not placed in configuration file
	// --------------------------------------------------
	public static final String RANK_PVP_SYSTEM_CONFIG_FILE = "./config/masterio/RankPvpSystemConfig.properties";

	public static final String RANK_PVP_SYSTEM_VERSION = "3.0";

	// CHAR_ID_COLUMN_NAME is not in configuration file.
	public static final String CHAR_ID_COLUMN_NAME = "charId"; // H5
	// public static final String CHAR_ID_COLUMN_NAME = "obj_Id"; // IL

	public class DecreaseMethod
	{
		public static final byte FULL = 1;
		public static final byte BASIC = 2;
		public static final byte CONSTANT = 3;
		public static final byte FRACTION = 4;
	}
}
