package services.community;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.cache.ImagesCache;
import l2mv.gameserver.dao.CharacterDAO;
import l2mv.gameserver.data.StringHolder;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.handler.bbs.CommunityBoardManager;
import l2mv.gameserver.handler.bbs.ICommunityBoardHandler;
import l2mv.gameserver.instancemanager.ServerVariables;
import l2mv.gameserver.listener.actor.player.OnAnswerListener;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.ClassId;
import l2mv.gameserver.model.entity.tournament.BattleObservationManager;
import l2mv.gameserver.model.entity.tournament.BattleRecord;
import l2mv.gameserver.model.entity.tournament.BattleScheduleManager;
import l2mv.gameserver.model.entity.tournament.Team;
import l2mv.gameserver.model.entity.tournament.TournamentStatus;
import l2mv.gameserver.model.entity.tournament.TournamentTeamsManager;
import l2mv.gameserver.network.serverpackets.ConfirmDlg;
import l2mv.gameserver.network.serverpackets.HideBoard;
import l2mv.gameserver.network.serverpackets.ShowBoard;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.ChatUtil;
import l2mv.gameserver.utils.Debug;
import l2mv.gameserver.utils.Language;
import l2mv.gameserver.utils.TimeUtils;
import l2mv.gameserver.utils.Util;

/**
 * Grand Bosses Community Manager
 *
 * @author Synerge
 */
public class CommunityTournament implements ScriptFile, ICommunityBoardHandler
{
	private static final Logger _log = LoggerFactory.getLogger(CommunityTournament.class);

	private static final SimpleDateFormat BATTLE_DATE_FORMAT = new SimpleDateFormat("HH:mm dd.MM.yyyy");

	@Override
	public void onLoad()
	{
		if (Config.COMMUNITYBOARD_ENABLED && !ConfigHolder.getBool("EnableMergeCommunity"))
		{
			_log.info("CommunityBoard: Tournament loaded.");
			CommunityBoardManager.getInstance().registerHandler(this);
		}
	}

	@Override
	public void onReload()
	{
		if (Config.COMMUNITYBOARD_ENABLED && !ConfigHolder.getBool("EnableMergeCommunity"))
		{
			CommunityBoardManager.getInstance().removeHandler(this);
		}
	}

	@Override
	public void onShutdown()
	{
	}

	@Override
	public String[] getBypassCommands()
	{
		return new String[]
		{
			"_bbstournament"
		};
	}

	private void useTournamentBypass(Player player, String bypass, Object... params)
	{
		onBypassCommand(player, "_bbstournament_" + bypass + (params.length > 0 ? "_" : "") + Util.joinArrayWithCharacter(params, "_"));
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		if (!ConfigHolder.getBool("TournamentAllowVoicedCommand"))
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(this, "onBypassCommand", player, bypass);
			}
			return;
		}

		// Friendlist tab shows the main tournament window
		if (bypass.startsWith("_friendlist_"))
		{
			useTournamentBypass(player, "main");
			return;
		}

		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(this, "onBypassCommand", player, bypass, TournamentStatus.getCurrentStatus(), TournamentTeamsManager.getInstance().isRegistered(player));
		}

		final StringTokenizer st = new StringTokenizer(bypass, "_");
		st.nextToken();

		if (st.hasMoreTokens())
		{
			switch (st.nextToken())
			{
			case "main":
			{
				final TournamentStatus status = TournamentStatus.getCurrentStatus();
				if (status == TournamentStatus.OVER)
				{
					useTournamentBypass(player, "showPodium");
				}
				else if (status == TournamentStatus.BATTLES)
				{
					final BattleScheduleManager scheduleManager = BattleScheduleManager.getInstance();
					final int roundIndex = scheduleManager.getCurrentRoundIndex();
					final BattleRecord nextBattle = scheduleManager.getNextBattle();
					final int battleIndex = nextBattle == null ? 0 : BattleScheduleManager.getInstance().getBattlesSortedByDate(roundIndex).indexOf(nextBattle);
					useTournamentBypass(player, "battleInfo", roundIndex, battleIndex);
				}
				else if (TournamentTeamsManager.getInstance().isRegistered(player))
				{
					useTournamentBypass(player, "registeredInfo");
				}
				else
				{
					useTournamentBypass(player, "registration");
				}
				break;
			}
			case "registration":
			{
				showRegistrationPage(player);
				break;
			}
			case "registeredInfo":
			{
				showRegistrationInfoPage(player);
				break;
			}
			case "tryToRegisterTeam":
			{
				tryToRegisterTeam(player);
				break;
			}
			case "unregister":
			{
				final TournamentStatus status = TournamentStatus.getCurrentStatus();
				if (status == TournamentStatus.REGISTRATION)
				{
					askUnregister(player);
				}
				else
				{
					useTournamentBypass(player, "main");
				}
				break;
			}
			case "battleInfo":
			{
				final int roundIndex = Integer.parseInt(st.nextToken());
				final int battleInRoundIndex = Integer.parseInt(st.nextToken());
				showBattleInfoPage(player, roundIndex, battleInRoundIndex);
				break;
			}
			case "battleRules":
			{
				showBattleRulesPage(player);
				break;
			}
			case "startObserve":
			{
				final int battleId = Integer.parseInt(st.nextToken());
				final BattleRecord record = BattleScheduleManager.getInstance().getBattle(battleId);
				BattleObservationManager.tryObserveBattle(record, player);
				player.sendPacket(new HideBoard());
				break;
			}
			case "showPodium":
			{
				showPodiumPage(player);
				break;
			}
			}
		}
	}

	private void showRegistrationPage(Player player)
	{
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "tournament/registration.htm", player);

		// Registering time
		if (ServerVariables.getLong("TournamentBattleTime", -1) > Calendar.getInstance().getTimeInMillis())
		{
			html = html.replace("%registerTime%", "Registration ends in " + TimeUtils.timeLeftToEpoch(ServerVariables.getLong("TournamentBattleTime", -1)) + ".");
		}
		else
		{
			html = html.replace("%registerTime%", "Registration for %tournamentName% is now opened!");
		}

		// Disabled classes
		StringBuilder disabledClasses = new StringBuilder();

		if (ConfigHolder.getIntArray("TournamentDisabledClasses").length > 0)
		{
			disabledClasses.append("Classes that cannot take part: ");

			int i = 0;
			for (int classId : ConfigHolder.getIntArray("TournamentDisabledClasses"))
			{
				ClassId clas = ClassId.getById(classId);
				if (clas == null)
				{
					continue;
				}

				if (i > 0)
				{
					disabledClasses.append(", ");
				}

				disabledClasses.append(clas.toPrettyString());

				i++;
			}

			disabledClasses.append("<br1>");
		}

		// Loser Reward
		if (!ConfigHolder.checkIsEmpty("TournamentExtraLoserReward"))
		{
			html = html.replace("%loserReward%", "<font color=f6e41f>Every player who will fight the battle and lose it, will gain " + ConfigHolder.getMapEntry("TournamentExtraLoserReward", Integer.class, Long.class).getValue() + " " + ItemHolder.getInstance().getItemName(ConfigHolder.getMapEntry("TournamentExtraLoserReward", Integer.class, Long.class).getKey()) + "!</font><br1>");
			html = html.replace("%loserReward2%", "<br1>");
		}
		else
		{
			html = html.replace("%loserReward%", "");
			html = html.replace("%loserReward2%", "<br>");
		}

		// Requirements
		StringBuilder requirements = new StringBuilder();
		if (ConfigHolder.getInt("TournamentMinLevel") > 1)
		{
			requirements.append("Level " + ConfigHolder.getInt("TournamentMinLevel") + " or higher<br1>");
		}

		switch (ConfigHolder.getInt("TournamentPlayersInTeam"))
		{
		case 1:
			break;
		case 2:
			requirements.append("Be in Party with your Partner<br1>");
			break;
		default:
			requirements.append("Be in Party with " + (ConfigHolder.getInt("TournamentPlayersInTeam") - 1) + " of your Partners<br1>");
			break;
		}
		if (!ConfigHolder.checkIsEmpty("TournamentRequiredItem"))
		{
			requirements.append(ConfigHolder.getMapEntry("TournamentRequiredItem", Integer.class, Long.class).getValue() + " " + ItemHolder.getInstance().getItemName(ConfigHolder.getMapEntry("TournamentRequiredItem", Integer.class, Long.class).getKey()) + "s in all members inventory<br1>");
		}

		// Replacements
		html = html.replace("%header%", HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "tournament/header.htm", player));
		html = html.replace("%disabledClasses%", disabledClasses.toString());
		html = html.replace("%requirements%", requirements.toString());
		html = html.replace("%serverName%", Config.SERVER_NAME);
		html = html.replace("%tournamentName%", ConfigHolder.getString("TournamentName"));
		html = html.replace("%tournamentPlayersInTeam%", String.valueOf(ConfigHolder.getInt("TournamentPlayersInTeam")));
		html = html.replace("%tournamentTeamsCount%", String.valueOf(TournamentTeamsManager.getInstance().getTeamsCount()));

		ImagesCache.getInstance().sendUsedImages(html, player);
		ShowBoard.separateAndSend(html, player);
	}

	private void showRegistrationErrorPage(Player player, String errorMsg)
	{
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "tournament/registerError.htm", player);

		// Replacements
		html = html.replace("%header%", HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "tournament/header.htm", player));
		html = html.replace("%serverName%", Config.SERVER_NAME);
		html = html.replace("%tournamentName%", ConfigHolder.getString("TournamentName"));
		html = html.replace("%errorMsg%", errorMsg);

		ImagesCache.getInstance().sendUsedImages(html, player);
		ShowBoard.separateAndSend(html, player);
	}

	private void showRegistrationInfoPage(Player player)
	{
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "tournament/registeredInfo.htm", player);

		// Disabled classes
		StringBuilder disabledClasses = new StringBuilder();

		if (ConfigHolder.getIntArray("TournamentDisabledClasses").length > 0)
		{
			disabledClasses.append("Classes that cannot take part: ");

			int i = 0;
			for (int classId : ConfigHolder.getIntArray("TournamentDisabledClasses"))
			{
				ClassId clas = ClassId.getById(classId);
				if (clas == null)
				{
					continue;
				}

				if (i > 0)
				{
					disabledClasses.append(", ");
				}

				disabledClasses.append(clas.toPrettyString());

				i++;
			}

			disabledClasses.append("<br1>");
		}

		// Loser Reward
		if (!ConfigHolder.checkIsEmpty("TournamentExtraLoserReward"))
		{
			html = html.replace("%loserReward%", "<font color=f6e41f>Every player who will fight the battle and lose it, will gain " + ConfigHolder.getMapEntry("TournamentExtraLoserReward", Integer.class, Long.class).getValue() + " " + ItemHolder.getInstance().getItemName(ConfigHolder.getMapEntry("TournamentExtraLoserReward", Integer.class, Long.class).getKey()) + "!</font><br1>");
			html = html.replace("%loserReward2%", "");
		}
		else
		{
			html = html.replace("%loserReward%", "");
			html = html.replace("%loserReward2%", "<br>");
		}

		// Replacements
		html = html.replace("%header%", HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "tournament/header.htm", player));
		html = html.replace("%disabledClasses%", disabledClasses.toString());
		html = html.replace("%serverName%", Config.SERVER_NAME);
		html = html.replace("%tournamentName%", ConfigHolder.getString("TournamentName"));
		html = html.replace("%tournamentPlayersInTeam%", String.valueOf(ConfigHolder.getInt("TournamentPlayersInTeam")));
		html = html.replace("%tournamentTeamsCount%", String.valueOf(TournamentTeamsManager.getInstance().getTeamsCount()));

		ImagesCache.getInstance().sendUsedImages(html, player);
		ShowBoard.separateAndSend(html, player);
	}

	private void showBattleInfoPage(Player player, int roundIndex, int battleInRoundIndex)
	{
		// Variables
		final List<BattleRecord> sortedBattles = BattleScheduleManager.getInstance().getBattlesSortedByDate(roundIndex);
		final BattleRecord bypassBattle = sortedBattles.get(battleInRoundIndex);
		Team myTeam = TournamentTeamsManager.getInstance().getMyTeam(player);
		BattleRecord myBattle = null;
		final BattleRecord leftBattle;
		BattleRecord rightBattle = null;
		if (myTeam != null)
		{
			myBattle = BattleScheduleManager.getInstance().getMyBattle(myTeam);
		}
		if (sortedBattles.size() > battleInRoundIndex + 1)
		{
			leftBattle = bypassBattle;
			rightBattle = sortedBattles.get(battleInRoundIndex + 1);
		}
		else if (battleInRoundIndex > 0 && sortedBattles.size() > battleInRoundIndex - 1)
		{
			leftBattle = sortedBattles.get(battleInRoundIndex - 1);
			rightBattle = bypassBattle;
		}
		else
		{
			leftBattle = bypassBattle;
		}

		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "tournament/battleInfo.htm", player);

		// Fight time
		if (myBattle != null && !myBattle.isPastBattle())
		{
			html = html.replace("%fightTime%", "You fight in " + TimeUtils.timeLeftToEpoch(myBattle.getBattleDate()));
		}
		else
		{
			html = html.replace("%fightTime%", "Good luck to all fighters!");
		}

		// Round
		if (BattleScheduleManager.getInstance().isFinalRound(roundIndex))
		{
			html = html.replace("%round%", "Final Round!");
		}
		else
		{
			html = html.replace("%round%", "Round " + (roundIndex + 1));
		}

		// Left and Right Battle Contents
		int index = 0;
		for (BattleRecord battle : new BattleRecord[]
		{
			leftBattle,
			rightBattle
		})
		{
			index++;

			if (battle == null)
			{
				html = html.replace((index == 1 ? "%leftBattle%" : "%rightBattle%"), "");
				continue;
			}

			String battleHtml = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "tournament/battleInfoTeam.htm", player);

			// Battle Round
			battleHtml = battleHtml.replace("%battleRound%", "Round " + (sortedBattles.indexOf(battle) + 1));

			// Content Winner Team
			if (battle.getWinnerTeam() != null)
			{
				battleHtml = battleHtml.replace("%winnerTeam%", makeTeamContent(myTeam, battle.getWinnerTeam()));
			}
			else
			{
				battleHtml = battleHtml.replace("%winnerTeam%", makeEmptyTeamContent());
			}

			// Battle Start Date
			battleHtml = battleHtml.replace("%startDate%", makeBattleStartDateContent(battle));

			// Content Team 1
			if (battle.getTeam1() != null)
			{
				battleHtml = battleHtml.replace("%team1%", makeTeamContent(myTeam, battle.getTeam1()));
			}
			else
			{
				battleHtml = battleHtml.replace("%team1%", makeEmptyTeamContent());
			}

			// Content Team 2
			if (battle.getTeam2() != null)
			{
				battleHtml = battleHtml.replace("%team2%", makeTeamContent(myTeam, battle.getTeam2()));
			}
			else
			{
				battleHtml = battleHtml.replace("%team2%", makeEmptyTeamContent());
			}

			html = html.replace((index == 1 ? "%leftBattle%" : "%rightBattle%"), battleHtml.toString());
		}

		// Previous Bypass
		if (sortedBattles.indexOf(leftBattle) > 0)
		{
			html = html.replace("%previousBypass%", "<button value=\"     Previous Battles\" action=\"bypass _bbstournament_battleInfo_" + roundIndex + "_" + (battleInRoundIndex - 1) + "\" width=150 height=26 back=Btns.btn_ornaments_back_blue_150x26_Down fore=Btns.btn_ornaments_back_blue_150x26>");
		}
		else
		{
			html = html.replace("%previousBypass%", "<br>");
		}

		// Next Bypass
		if (rightBattle != null && sortedBattles.indexOf(rightBattle) < sortedBattles.size() - 1)
		{
			html = html.replace("%nextBypass%", "<button value=\"     Next Battles\" action=\"bypass _bbstournament_battleInfo_" + roundIndex + "_" + (battleInRoundIndex + 1) + "\" width=150 height=26 back=Btns.btn_ornaments_right_blue_150x26_Down fore=Btns.btn_ornaments_right_blue_150x26>");
		}
		else
		{
			html = html.replace("%nextBypass%", "<br>");
		}

		// Replacements
		html = html.replace("%header%", HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "tournament/header.htm", player));
		html = html.replace("%serverName%", Config.SERVER_NAME);
		html = html.replace("%tournamentName%", ConfigHolder.getString("TournamentName"));
		html = html.replace("%tournamentPlayersInTeam%", String.valueOf(ConfigHolder.getInt("TournamentPlayersInTeam")));
		html = html.replace("%tournamentTeamsCount%", String.valueOf(TournamentTeamsManager.getInstance().getTeamsCount()));

		ImagesCache.getInstance().sendUsedImages(html, player);
		ShowBoard.separateAndSend(html, player);
	}

	private void showBattleRulesPage(Player player)
	{
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "tournament/battleRules.htm", player);

		// Disabled classes
		StringBuilder disabledClasses = new StringBuilder();

		if (ConfigHolder.getIntArray("TournamentDisabledClasses").length > 0)
		{
			int i = 0;
			for (int classId : ConfigHolder.getIntArray("TournamentDisabledClasses"))
			{
				ClassId clas = ClassId.getById(classId);
				if (clas == null)
				{
					continue;
				}

				if (i > 0)
				{
					disabledClasses.append(", ");
				}

				disabledClasses.append(clas.toPrettyString());

				i++;
			}
		}

		// Replacements
		html = html.replace("%header%", HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "tournament/header.htm", player));
		html = html.replace("%disabledClasses%", disabledClasses.toString());
		html = html.replace("%serverName%", Config.SERVER_NAME);
		html = html.replace("%tournamentName%", ConfigHolder.getString("TournamentName"));
		html = html.replace("%tournamentMinLevel%", String.valueOf(ConfigHolder.getInt("TournamentMinLevel")));
		html = html.replace("%tournamentFirstFightPreparation%", String.valueOf(ConfigHolder.getInt("TournamentFirstFightPreparation")));
		html = html.replace("%tournamentNextFightPreparation%", String.valueOf(ConfigHolder.getInt("TournamentNextFightsPreparation")));
		html = html.replace("%tournamentFightsToResult%", String.valueOf(ConfigHolder.getInt("TournamentFightsToResult")));
		html = html.replace("%tournamentMaxFightTimeForResult%", String.valueOf(ConfigHolder.getInt("TournamentMaxFightTimeForResult")));

		ImagesCache.getInstance().sendUsedImages(html, player);
		ShowBoard.separateAndSend(html, player);
	}

	private void showPodiumPage(Player player)
	{
		Team firstPlace = null, secondPlace = null, thirdPlace = null, myTeam = null;
		if (TournamentTeamsManager.getInstance().getTeamByFinalPosition(1) != null)
		{
			firstPlace = TournamentTeamsManager.getInstance().getTeamByFinalPosition(1);
		}
		if (TournamentTeamsManager.getInstance().getTeamByFinalPosition(2) != null)
		{
			secondPlace = TournamentTeamsManager.getInstance().getTeamByFinalPosition(2);
		}
		if (TournamentTeamsManager.getInstance().getTeamByFinalPosition(3) != null)
		{
			thirdPlace = TournamentTeamsManager.getInstance().getTeamByFinalPosition(3);
		}
		if (TournamentTeamsManager.getInstance().getMyTeam(player) != null)
		{
			myTeam = TournamentTeamsManager.getInstance().getMyTeam(player);
		}

		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "tournament/podium.htm", player);

		// Content First Place
		if (firstPlace != null)
		{
			html = html.replace("%winnerTeam%", makeTeamContent(myTeam, firstPlace));
		}
		else
		{
			html = html.replace("%winnerTeam%", makeEmptyTeamContent());
		}

		// Content Second Place
		if (secondPlace != null)
		{
			html = html.replace("%secondTeam%", makeTeamContent(myTeam, secondPlace));
		}
		else
		{
			html = html.replace("%secondTeam%", makeEmptyTeamContent());
		}

		// Content Third Place
		if (thirdPlace != null)
		{
			html = html.replace("%thirdTeam%", makeTeamContent(myTeam, thirdPlace));
		}
		else
		{
			html = html.replace("%thirdTeam%", makeEmptyTeamContent());
		}

		// Replacements
		html = html.replace("%header%", HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "tournament/header.htm", player));
		html = html.replace("%serverName%", Config.SERVER_NAME);
		html = html.replace("%tournamentName%", ConfigHolder.getString("TournamentName"));

		ImagesCache.getInstance().sendUsedImages(html, player);
		ShowBoard.separateAndSend(html, player);
	}

	private String makeTeamContent(Team myTeam, Team team)
	{
		final StringBuilder builder = new StringBuilder();
		boolean nextColor = false;
		for (int playerId : team.getPlayerIdsForIterate())
		{
			final String playerName = CharacterDAO.getNameByObjectId(playerId);
			if (playerName.length() > 0)
			{
				builder.append("<table width=147 height=20 bgcolor=" + (nextColor ? "061236" : "030a22") + " cellspacing=0 cellpadding=0>");
				builder.append("<tr>");
				builder.append("<td width=147 align=center>");
				if (myTeam != null && myTeam.getId() == team.getId())
				{
					builder.append("<font color=ffe828>");
				}
				else
				{
					builder.append("<font color=c6c39e>");
				}

				builder.append(playerName);
				builder.append("</font>");
				builder.append("</td>");
				builder.append("</tr>");
				builder.append("</table>");
			}
			nextColor = !nextColor;
		}
		return builder.toString();
	}

	private String makeEmptyTeamContent()
	{
		final StringBuilder builder = new StringBuilder();
		boolean nextColor = false;
		for (int i = 1; i <= ConfigHolder.getInt("TournamentPlayersInTeam"); i++)
		{
			builder.append("<table width=147 height=20 bgcolor=" + (nextColor ? "061236" : "030a22") + " cellspacing=0 cellpadding=0>");
			builder.append("<tr>");
			builder.append("<td width=147 align=center>");
			builder.append("<br>");
			builder.append("</td>");
			builder.append("</tr>");
			builder.append("</table>");

			nextColor = !nextColor;
		}
		return builder.toString();
	}

	private String makeBattleStartDateContent(BattleRecord battle)
	{
		final StringBuilder builder = new StringBuilder();

		if (battle.getTeam2Id() < 0)
		{
			builder.append("<br><br>");
			builder.append("<font color=c6c39e name=hs12>Finished without Battle</font><br>");
			builder.append("<table cellspacing=12></table>");
		}
		else if (battle.isPastBattle())
		{
			builder.append("<font color=c6c39e>Battle finished at:</font><br1>");
			builder.append("<font color=D97F10 name=hs12>" + BATTLE_DATE_FORMAT.format(battle.getBattleDate()) + "</font><br1>");
			builder.append("<table cellspacing=19></table>");
		}
		else if (battle.isNowLive())
		{
			builder.append("<br>");
			builder.append("<button value=\"       Watch Match Live NOW!\" action=\"bypass _bbstournament_startObserve_" + battle.getId() + "\" width=200 height=32 back=Btns.btn_ornaments_info_red_200x32_Down fore=Btns.btn_ornaments_confirmed_red_200x32>");
			builder.append("<table cellspacing=9></table>");
		}
		else
		{
			builder.append("<font color=c6c39e>Battle starts in:</font><br1>");
			builder.append("<font color=D97F10 name=hs12>" + TimeUtils.timeLeftToEpoch(battle.getBattleDate()) + "</font><br1>");
			builder.append("<font color=c6c39e>(at " + BATTLE_DATE_FORMAT.format(battle.getBattleDate()) + " CEST)</font>");
		}

		return builder.toString();
	}

	private void tryToRegisterTeam(Player player)
	{
		if (!ConfigHolder.getBool("AllowTournamentRegistering"))
		{
			showRegistrationErrorPage(player, StringHolder.getInstance().getNotNull(player, "Tournament.CannotRegisterSelf.Disabled"));
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(CommunityTournament.class, "tryToRegisterTeam", "ErrorDisabled", player);
			}
			return;
		}

		if (ConfigHolder.getInt("TournamentMaxTeams") > 0 && TournamentTeamsManager.getInstance().getTeamsCount() >= ConfigHolder.getInt("TournamentMaxTeams"))
		{
			showRegistrationErrorPage(player, StringHolder.getInstance().getNotNull(player, "Tournament.CannotRegisterSelf.TooManyTeams"));
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(CommunityTournament.class, "tryToRegisterTeam", "ErrorMaxTeams", player, ConfigHolder.getInt("TournamentMaxTeams"), TournamentTeamsManager.getInstance().getTeamsCount());
			}
			return;
		}

		if (ConfigHolder.getInt("TournamentPlayersInTeam") == 1)
		{
			if (player.getParty() != null)
			{
				showRegistrationErrorPage(player, StringHolder.getInstance().getNotNull(player, "Tournament.CannotRegisterSelf.SingleTournamentHasParty"));
				if (Debug.TOURNAMENT.isActive())
				{
					Debug.TOURNAMENT.debug(CommunityTournament.class, "tryToRegisterTeam", "ErrorHasParty", player, player.getParty());
				}
				return;
			}

			if (!checkCanRegister(player, player, true))
			{
				return;
			}
		}
		else
		{
			if (player.getParty() == null)
			{
				showRegistrationErrorPage(player, StringHolder.getInstance().getNotNull(player, "Tournament.CannotRegisterSelf.NoParty"));
				if (Debug.TOURNAMENT.isActive())
				{
					Debug.TOURNAMENT.debug(CommunityTournament.class, "tryToRegisterTeam", "ErrorNoParty", player);
				}
				return;
			}

			if (player.getParty().getMemberCount() != ConfigHolder.getInt("TournamentPlayersInTeam"))
			{
				showRegistrationErrorPage(player, StringHolder.getNotNull(player, "Tournament.CannotRegisterSelf.WrongMembersCount", ConfigHolder.getInt("TournamentPlayersInTeam")));
				if (Debug.TOURNAMENT.isActive())
				{
					Debug.TOURNAMENT.debug(CommunityTournament.class, "tryToRegisterTeam", "ErrorMemberCount", player, player.getParty().getMemberCount(), ConfigHolder.getInt("TournamentPlayersInTeam"));
				}
				return;
			}

			for (Player member : player.getParty().getMembers())
			{
				if (!checkCanRegister(member, player, true))
				{
					return;
				}
			}
		}

		final Map.Entry<Integer, Long> requiredItem = ConfigHolder.checkIsEmpty("TournamentRequiredItem") ? null : ConfigHolder.getMapEntry("TournamentRequiredItem", Integer.class, Long.class);
		if (player.getParty() == null)
		{
			if (requiredItem != null)
			{
				player.getInventory().destroyItemByItemId(requiredItem.getKey(), requiredItem.getValue(), "RegisteringToTournament");
			}

			final List<Player> members = new ArrayList<Player>(1);
			members.add(player);
			final Team team = TournamentTeamsManager.getInstance().createNewTeam(members);
			useTournamentBypass(player, "registeredInfo");
			player.sendCustomMessage("Tournament.RegisterSuccess");

			if (ConfigHolder.getBool("TournamentAnnounceRegister"))
			{
				ChatUtil.sendStringToAll(ChatUtil.getMessagePerLang("Tournament.RegisterSuccessAnnounceSender"), ChatUtil.getMessagePerLang("Tournament.RegisterSuccessAnnounce1", player.getName()), ConfigHolder.getChatType("TournamentAnnounceRegisterChatType"));
			}

			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(CommunityTournament.class, "tryToRegisterTeam", "Success", player, team, ConfigHolder.getBool("TournamentAnnounceRegister"));
			}
		}
		else
		{
			final List<Player> partyMembers = player.getParty().getMembers();
			final Team team = TournamentTeamsManager.getInstance().createNewTeam(partyMembers);
			for (Player member2 : partyMembers)
			{
				if (requiredItem != null)
				{
					member2.getInventory().destroyItemByItemId(requiredItem.getKey(), requiredItem.getValue(), "RegisteringToTournament");
				}

				useTournamentBypass(member2, "registeredInfo");
				member2.sendCustomMessage("Tournament.RegisterSuccess");
			}
			if (ConfigHolder.getBool("TournamentAnnounceRegister"))
			{
				ChatUtil.sendStringToAll(ChatUtil.getMessagePerLang("Tournament.RegisterSuccessAnnounceSender"), getRegisterAnnounceBody(partyMembers), ConfigHolder.getChatType("TournamentAnnounceRegisterChatType"));
			}

			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(CommunityTournament.class, "tryToRegisterTeam", "Success", player, player.getParty(), team);
			}
		}
	}

	private static Map<Language, String> getRegisterAnnounceBody(List<Player> players)
	{
		final String[] names = new String[players.size()];
		for (int i = 0; i < players.size(); ++i)
		{
			names[i] = players.get(i).getName();
		}

		switch (players.size())
		{
		case 1:
		{
			return ChatUtil.getMessagePerLang("Tournament.RegisterSuccessAnnounce1", (Object[]) names);
		}
		case 2:
		{
			return ChatUtil.getMessagePerLang("Tournament.RegisterSuccessAnnounce2", (Object[]) names);
		}
		case 3:
		{
			return ChatUtil.getMessagePerLang("Tournament.RegisterSuccessAnnounce3", (Object[]) names);
		}
		case 4:
		{
			return ChatUtil.getMessagePerLang("Tournament.RegisterSuccessAnnounce4", (Object[]) names);
		}
		case 5:
		{
			return ChatUtil.getMessagePerLang("Tournament.RegisterSuccessAnnounce5", (Object[]) names);
		}
		case 6:
		{
			return ChatUtil.getMessagePerLang("Tournament.RegisterSuccessAnnounce6", (Object[]) names);
		}
		case 7:
		{
			return ChatUtil.getMessagePerLang("Tournament.RegisterSuccessAnnounce7", (Object[]) names);
		}
		case 8:
		{
			return ChatUtil.getMessagePerLang("Tournament.RegisterSuccessAnnounce8", (Object[]) names);
		}
		case 9:
		{
			return ChatUtil.getMessagePerLang("Tournament.RegisterSuccessAnnounce9", (Object[]) names);
		}
		default:
		{
			throw new AssertionError("Size of list in getRegisterAnnounceBody cannot be " + players.size());
		}
		}
	}

	private boolean checkCanRegister(Player player, Player leader, boolean sendErrorMessage)
	{
		if (player.isBlocked())
		{
			if (sendErrorMessage)
			{
				if (player.equals(leader))
				{
					showRegistrationErrorPage(leader, StringHolder.getInstance().getNotNull(leader, "Tournament.CannotRegisterSelf.Blocked"));
				}
				else
				{
					showRegistrationErrorPage(leader, StringHolder.getNotNull(leader, "Tournament.CannotRegisterFriend.Blocked", player.getName()));
					player.sendCustomMessage("Tournament.CannotRegisterSelf.Blocked");
				}
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(CommunityTournament.class, "checkCanRegister", "ErrorBlocked", player, leader, sendErrorMessage);
			}
			return false;
		}
		if (player.isInJail())
		{
			if (sendErrorMessage)
			{
				if (player.equals(leader))
				{
					showRegistrationErrorPage(leader, StringHolder.getInstance().getNotNull(leader, "Tournament.CannotRegisterSelf.Jailed"));
				}
				else
				{
					showRegistrationErrorPage(leader, StringHolder.getNotNull(leader, "Tournament.CannotRegisterFriend.Jailed", player.getName()));
					player.sendCustomMessage("Tournament.CannotRegisterSelf.Jailed");
				}
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(CommunityTournament.class, "checkCanRegister", "ErrorJail", player, leader, sendErrorMessage);
			}
			return false;
		}
		if (player.isInOfflineMode())
		{
			if (sendErrorMessage)
			{
				if (player.equals(leader))
				{
					showRegistrationErrorPage(leader, StringHolder.getInstance().getNotNull(leader, "Tournament.CannotRegisterSelf.OfflineMode"));
				}
				else
				{
					showRegistrationErrorPage(leader, StringHolder.getNotNull(leader, "Tournament.CannotRegisterFriend.OfflineMode", player.getName()));
					player.sendCustomMessage("Tournament.CannotRegisterSelf.OfflineMode");
				}
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(CommunityTournament.class, "checkCanRegister", "ErrorOffline", player, leader, sendErrorMessage);
			}
			return false;
		}
		if (player.getLevel() < ConfigHolder.getInt("TournamentMinLevel"))
		{
			if (sendErrorMessage)
			{
				if (player.equals(leader))
				{
					showRegistrationErrorPage(leader, StringHolder.getInstance().getNotNull(leader, "Tournament.CannotRegisterSelf.TooLowLevel"));
				}
				else
				{
					showRegistrationErrorPage(leader, StringHolder.getNotNull(leader, "Tournament.CannotRegisterFriend.TooLowLevel", player.getName()));
					player.sendCustomMessage("Tournament.CannotRegisterSelf.TooLowLevel");
				}
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(CommunityTournament.class, "checkCanRegister", "ErrorLevel", player, leader, sendErrorMessage, player.getLevel(), ConfigHolder.getInt("TournamentMinLevel"));
			}
			return false;
		}
		if (player.getClassId().getLevel() < ConfigHolder.getInt("TournamentMinimumClassLevel"))
		{
			if (sendErrorMessage)
			{
				if (player.equals(leader))
				{
					showRegistrationErrorPage(leader, StringHolder.getInstance().getNotNull(leader, "Tournament.CannotRegisterSelf.TooLowClassLevel"));
				}
				else
				{
					showRegistrationErrorPage(leader, StringHolder.getNotNull(leader, "Tournament.CannotRegisterFriend.TooLowClassLevel", player.getName()));
					player.sendCustomMessage("Tournament.CannotRegisterSelf.TooLowClassLevel");
				}
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(CommunityTournament.class, "checkCanRegister", "ErrorClassLevel", player, leader, sendErrorMessage, player.getClassId().getLevel(), ConfigHolder.getInt("TournamentMinimumClassLevel"));
			}
			return false;
		}
		if (TournamentTeamsManager.getInstance().isRegistered(player))
		{
			if (sendErrorMessage)
			{
				if (player.equals(leader))
				{
					showRegistrationErrorPage(leader, StringHolder.getInstance().getNotNull(leader, "Tournament.CannotRegisterSelf.AlreadyRegistered"));
				}
				else
				{
					showRegistrationErrorPage(leader, StringHolder.getNotNull(leader, "Tournament.CannotRegisterFriend.AlreadyRegistered", player.getName()));
					player.sendCustomMessage("Tournament.CannotRegisterSelf.AlreadyRegistered");
				}
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(CommunityTournament.class, "checkCanRegister", "ErrorRegistered", player, leader, sendErrorMessage);
			}
			return false;
		}
		if (ArrayUtils.contains(ConfigHolder.getIntArray("TournamentDisabledClasses"), player.getClassId().getId()))
		{
			if (sendErrorMessage)
			{
				if (player.equals(leader))
				{
					showRegistrationErrorPage(leader, StringHolder.getInstance().getNotNull(leader, "Tournament.CannotRegisterSelf.ClassDisabled"));
				}
				else
				{
					showRegistrationErrorPage(leader, StringHolder.getNotNull(leader, "Tournament.CannotRegisterFriend.ClassDisabled", player.getName()));
					player.sendCustomMessage("Tournament.CannotRegisterSelf.ClassDisabled");
				}
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(CommunityTournament.class, "checkCanRegister", "ErrorDisabledClass", player, leader, sendErrorMessage, Arrays.toString(ConfigHolder.getIntArray("TournamentDisabledClasses")), player.getClassId().getId());
			}
			return false;
		}
		if (!ConfigHolder.checkIsEmpty("TournamentRequiredItem"))
		{
			final Map.Entry<Integer, Long> requiredItem = ConfigHolder.getMapEntry("TournamentRequiredItem", Integer.class, Long.class);
			if (player.getInventory().getCountOf(requiredItem.getKey()) < requiredItem.getValue())
			{
				if (sendErrorMessage)
				{
					if (player.equals(leader))
					{
						showRegistrationErrorPage(leader, StringHolder.getInstance().getNotNull(leader, "Tournament.CannotRegisterSelf.NoRequiredItems"));
					}
					else
					{
						showRegistrationErrorPage(leader, StringHolder.getNotNull(leader, "Tournament.CannotRegisterFriend.NoRequiredItems", player.getName()));
						player.sendCustomMessage("Tournament.CannotRegisterSelf.NoRequiredItems");
					}
				}
				if (Debug.TOURNAMENT.isActive())
				{
					Debug.TOURNAMENT.debug(CommunityTournament.class, "checkCanRegister", "ErrorNoRequiredItems", player, leader, sendErrorMessage, player.getInventory().getCountOf(requiredItem.getKey()), requiredItem.getValue());
				}
				return false;
			}
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(CommunityTournament.class, "checkCanRegister", "Success", player, leader, sendErrorMessage);
		}
		return true;
	}

	private void askUnregister(Player player)
	{
		final ConfirmDlg packet = new ConfirmDlg(SystemMsg.S1, 60000).addString(StringHolder.getInstance().getNotNull(player, "Tournament.AskUnregister"));
		player.ask(packet, new BtnUnregister(player));
	}

	private class BtnUnregister implements OnAnswerListener
	{
		private final Player _player;

		public BtnUnregister(Player player)
		{
			_player = player;
		}

		@Override
		public void sayYes()
		{
			if (TournamentStatus.getCurrentStatus() == TournamentStatus.REGISTRATION)
			{
				final Team team = TournamentTeamsManager.getInstance().getMyTeam(_player);
				if (team != null)
				{
					TournamentTeamsManager.getInstance().removeTeam(team);
					for (Player member : team.getOnlinePlayers())
					{
						member.sendCustomMessage("Tournament.UnregisterSuccess");
					}
				}
			}
			useTournamentBypass(_player, "main");
		}

		@Override
		public void sayNo()
		{
		}
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
	}
}
