//package services.community;
//
//import java.io.Serializable;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Comparator;
//import java.util.List;
//import java.util.StringTokenizer;
//
//import l2mv.commons.annotations.Nullable;
//import l2mv.gameserver.Config;
//import l2mv.gameserver.ConfigHolder;
//import l2mv.gameserver.cache.CrestCache;
//import l2mv.gameserver.cache.ImagesCache;
//import l2mv.gameserver.cache.Msg;
//import l2mv.gameserver.data.htm.HtmCache;
//import l2mv.gameserver.data.xml.holder.ResidenceHolder;
//import l2mv.gameserver.database.DatabaseFactory;
//import l2mv.gameserver.handler.bbs.CommunityBoardManager;
//import l2mv.gameserver.handler.bbs.ICommunityBoardHandler;
//import l2mv.gameserver.listener.actor.player.OnPlayerEnterListener;
//import l2mv.gameserver.model.Player;
//import l2mv.gameserver.model.Skill;
//import l2mv.gameserver.model.actor.listener.CharListenerList;
//import l2mv.gameserver.model.pledge.Clan;
//import l2mv.gameserver.model.pledge.UnitMember;
//import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
//import l2mv.gameserver.network.serverpackets.PledgeCrest;
//import l2mv.gameserver.network.serverpackets.Say2;
//import l2mv.gameserver.network.serverpackets.ShowBoard;
//import l2mv.gameserver.network.serverpackets.components.ChatType;
//import l2mv.gameserver.scripts.Functions;
//import l2mv.gameserver.scripts.ScriptFile;
//import l2mv.gameserver.tables.ClanTable;
//import l2mv.gameserver.utils.Util;
//
//import org.apache.commons.lang3.ArrayUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class CommunityClanTales extends Functions implements ScriptFile, ICommunityBoardHandler
//{
//	private static final Logger _log = LoggerFactory.getLogger(CommunityClanTales.class);
//
//	private static final SortType DEFAULT_SORT_TYPE = SortType.RANK;
//	private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#,###");
//
//	private final Listener _listener = new Listener();
//
//	private Clan[] _clansSortedByRank;
//	private final Object _sortedClansLock = new Object();
//	private long _lastSortDate;
//
//	@Override
//	public void onLoad()
//	{
//		if (Config.COMMUNITYBOARD_ENABLED && !ConfigHolder.getBool("EnableMergeCommunity") && ConfigHolder.getBool("AllowClanListPage"))
//		{
//			CommunityBoardManager.getInstance().registerHandler(this);
//
//			CharListenerList.addGlobal(_listener);
//			_log.info("CommunityBoard: Clan service loaded.");
//		}
//	}
//
//	@Override
//	public void onReload()
//	{
//		if (Config.COMMUNITYBOARD_ENABLED && !ConfigHolder.getBool("EnableMergeCommunity") && ConfigHolder.getBool("AllowClanListPage"))
//			CommunityBoardManager.getInstance().removeHandler(this);
//	}
//
//	@Override
//	public void onShutdown()
//	{
//	}
//
//	@Override
//	public String[] getBypassCommands()
//	{
//		return new String[]
//		{
//			"_bbsclan"
//		};
//	}
//
//	private void useClanBypass(Player player, String bypass, Object... params)
//	{
//		onBypassCommand(player, "_bbsclan_" + bypass + (params.length > 0 ? "_" : "") + Util.joinArrayWithCharacter(params, "_"));
//	}
//
//	@Override
//	public void onBypassCommand(Player player, String bypass)
//	{
//		final long currentDate = System.currentTimeMillis();
//		synchronized (_sortedClansLock)
//		{
//			if (_clansSortedByRank == null || _lastSortDate + ConfigHolder.getLong("ClanListSortDelay") < currentDate)
//			{
//				_clansSortedByRank = getSortedClans(SortType.RANK);
//				_lastSortDate = currentDate;
//			}
//		}
//
//		// Clan list should be the main page
//		if (bypass.equals("_bbsclan"))
//		{
//			useClanBypass(player, "clanList", "", "", "");
//			return;
//		}
//
//		// We add to the _ a space after, because some params come empty and the tokenizer doesnt recognize them and they should be there
//		bypass = bypass.replace("_", "_ ");
//
//		final StringTokenizer st = new StringTokenizer(bypass, "_");
//		st.nextToken();
//
//		if (st.hasMoreTokens())
//		{
//			switch (st.nextToken().trim())
//			{
//				case "clanList":
//				{
//					final String searchClanName = st.nextToken().trim();
//					final String searchPlayerName = st.nextToken().trim();
//					final String searchAllianceName = st.nextToken().trim();
//					final int page = (st.hasMoreTokens() ? Integer.parseInt(st.nextToken().trim()) : 0);
//					showClanListPage(player, searchClanName, searchPlayerName, searchAllianceName, page);
//					break;
//				}
//				case "clanDetails":
//				{
//					final String searchClanName = st.nextToken().trim();
//					final String searchPlayerName = st.nextToken().trim();
//					final String searchAllianceName = st.nextToken().trim();
//					final int page = Integer.parseInt(st.nextToken().trim());
//					final int clanId = Integer.parseInt(st.nextToken().trim());
//					showClanDetailsPage(player, searchClanName, searchPlayerName, searchAllianceName, page, clanId);
//					break;
//				}
//				case "changeSort":
//				{
//					final String searchClanName = st.nextToken().trim();
//					final String searchPlayerName = st.nextToken().trim();
//					final String searchAllianceName = st.nextToken().trim();
//					final String sortName = Util.getAllTokens(st).replace(" ", "_").trim(); // Some sorts have _ in their name
//
//					SortType.saveSortVar(player, sortName);
//					useClanBypass(player, "clanList", searchClanName, searchPlayerName, searchAllianceName);
//					break;
//				}
//				case "editClanNotice":
//				{
//					final String communityArgsValue = Util.getAllTokens(st).trim();
//					showEditClanNoticePage(player, communityArgsValue);
//					break;
//				}
//				case "confirmEditClanNotice":
//				{
//					final String newNotice = Util.getAllTokens(st).trim();
//					if (isNoticeAlright(player, newNotice, true))
//					{
//						editNotice(player, newNotice);
//						useClanBypass(player, "editClanNotice", convertBackTags(player.getClan().getNotice()));
//						player.sendPacket(Msg.NOTICE_HAS_BEEN_SAVED);
//					}
//					break;
//				}
//			}
//		}
//	}
//
//	private void showClanListPage(Player player, String searchClanName, String searchPlayerName, String searchAllianceName, int page)
//	{
//		final int clansPerPage = 8;
//		final Clan[] sortedClans = getSortedClans(player, searchClanName, searchPlayerName, searchAllianceName);
//		final SortType sortType = SortType.getQuickVarValue(player);
//
//		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clanList/clanList.htm", player);
//
//		// Sort Rank
//		if (sortType == SortType.RANK)
//			html = html.replace("%sortRank%", "<font color=FFFFFF>#</font>");
//		else
//			html = html.replace("%sortRank%", "<font color=FFFFFF><button value=\"#\" action=\"bypass _bbsclan_changeSort_" + searchClanName + "_" + searchPlayerName + "_" + searchAllianceName + "_RANK\" width=25 height=15 back=\"L2UI_CT1.ListCTRL_DF_Title_Down\" fore=\"L2UI_CT1.ListCTRL_DF_Title\">");
//
//		// Sort Name
//		if (sortType == SortType.NAME)
//			html = html.replace("%sortName%", "<font color=FFFFFF>Clan name</font>");
//		else
//			html = html.replace("%sortName%", "<font color=FFFFFF><button value=\"Clan Name\" action=\"bypass _bbsclan_changeSort_" + searchClanName + "_" + searchPlayerName + "_" + searchAllianceName + "_NAME\" width=128 height=15 back=\"L2UI_CT1.ListCTRL_DF_Title_Down\" fore=\"L2UI_CT1.ListCTRL_DF_Title\">");
//
//		// Sort Base
//		if (sortType == SortType.BASE)
//			html = html.replace("%sortBase%", "<font color=FFFFFF>Base</font>");
//		else
//			html = html.replace("%sortBase%", "<font color=FFFFFF><button value=\"Clan Base\" action=\"bypass _bbsclan_changeSort_" + searchClanName + "_" + searchPlayerName + "_" + searchAllianceName + "_BASE\" width=128 height=15 back=\"L2UI_CT1.ListCTRL_DF_Title_Down\" fore=\"L2UI_CT1.ListCTRL_DF_Title\">");
//
//		// Player Alliance Header
//		if (searchPlayerName.length()> 0)
//			html = html.replace("%playerHeader%", "<td background=\"L2UI_CT1.ListCTRL_DF_Title\" width=128 align=center><font color=FFFFFF>Player Name</font></td>");
//		else if (searchAllianceName.length() > 0)
//			html = html.replace("%playerHeader%", "<td background=\"L2UI_CT1.ListCTRL_DF_Title\" width=128 align=center><font color=FFFFFF>Alliance</font></td>");
//		else
//			html = html.replace("%playerHeader%", "");
//
//		// Sort Leader Name
//		if (sortType == SortType.LEADER_NAME)
//			html = html.replace("%sortLeader%", "<font color=FFFFFF>Leader</font>");
//		else
//			html = html.replace("%sortLeader%", "<font color=FFFFFF><a action=\"bypass _bbsclan_changeSort_" + searchClanName + "_" + searchPlayerName + "_" + searchAllianceName + "_LEADER_NAME\">Leader</a></font>");
//
//		// Sort Level
//		if (sortType == SortType.LEVEL)
//			html = html.replace("%sortLevel%", "<font color=ff8e3b>Level</font>");
//		else
//			html = html.replace("%sortLevel%", "<font color=ff8e3b><a action=\"bypass _bbsclan_changeSort_" + searchClanName + "_" + searchPlayerName + "_" + searchAllianceName + "_LEVEL\">Level</a></font>");
//
//		// Sort Members Count
//		if (sortType == SortType.MEMBERS_COUNT)
//			html = html.replace("%sortMembers%", "<font color=ff8e3b>Members</font>");
//		else
//			html = html.replace("%sortMembers%", "<font color=ff8e3b><a action=\"bypass _bbsclan_changeSort_" + searchClanName + "_" + searchPlayerName + "_" + searchAllianceName + "_MEMBERS_COUNT\">Members</a></font>");
//
//		// Clan List
//		StringBuilder clanList = new StringBuilder();
//
//		for (int i = page * clansPerPage; i < ((page * clansPerPage) + clansPerPage) - 1; i++)
//		{
//			if (sortedClans.length > i)
//			{
//				makeClanListTable(clanList, player, sortedClans[i], i, searchClanName, searchPlayerName, searchAllianceName, page);
//			}
//			else
//			{
//				makeEmptyClanListTable(clanList, player, i, searchClanName, searchPlayerName, searchAllianceName, page);
//			}
//		}
//		if (player.getClan() != null)
//		{
//			makeClanListTable(clanList, player, player.getClan(), -1, searchClanName, searchPlayerName, searchAllianceName, page);
//		}
//
//		// Previous Page
//		if (page > 0)
//			html = html.replace("%previousPage%", "<button action=\"bypass _bbsclan_clanList_" + searchClanName + "_" + searchPlayerName + "_" + searchAllianceName + "_" + (page - 1) + "\" width=14 height=14 back=Btns.left_blue_down fore=Btns.left_blue>");
//		else
//			html = html.replace("%previousPage%", "<br>");
//
//		// Next Page
//		if (sortedClans.length > (clansPerPage * page) + clansPerPage)
//			html = html.replace("%nextPage%", "<button action=\"bypass _bbsclan_clanList_" + searchClanName + "_" + searchPlayerName + "_" + searchAllianceName + "_" + (page + 1) + "\" width=14 height=14 back=Btns.right_blue_down fore=Btns.right_blue>");
//		else
//			html = html.replace("%nextPage%", "<br>");
//
//		// Search Boxes
//		StringBuilder searchBoxes = new StringBuilder();
//
//		if (searchClanName.length() == 0 && searchPlayerName.length() == 0 && searchAllianceName.length() == 0)
//		{
//			searchBoxes.append("<td width=180 align=center height=25>");
//			searchBoxes.append("<edit var=\"findClanName\" width=118 height=14>");
//			searchBoxes.append("</td>");
//			searchBoxes.append("<td width=180 align=center>");
//			searchBoxes.append("<edit var=\"findMemberName\" width=118 height=14>");
//			searchBoxes.append("</td>");
//			searchBoxes.append("<td width=180 align=center>");
//			searchBoxes.append("<edit var=\"findAllyName\" width=118 height=14>");
//			searchBoxes.append("</td>");
//			searchBoxes.append("</tr>");
//			searchBoxes.append("<tr>");
//			searchBoxes.append("<td width=180 align=center>");
//			searchBoxes.append("<button action=\"bypass _bbsclan_clanList_ $findClanName __\" value=\"Find\" width=60 height=16 back=Btns.btn_simple_red_60x16_Down fore=Btns.btn_simple_red_60x16 />");
//			searchBoxes.append("</td>");
//			searchBoxes.append("<td width=180 align=center>");
//			searchBoxes.append("<button action=\"bypass _bbsclan_clanList__ $findMemberName _\" value=\"Find\" width=60 height=16 back=Btns.btn_simple_red_60x16_Down fore=Btns.btn_simple_red_60x16 />");
//			searchBoxes.append("</td>");
//			searchBoxes.append("<td width=180 align=center>");
//			searchBoxes.append("<button action=\"bypass _bbsclan_clanList___ $findAllyName\" value=\"Find\" width=60 height=16 back=Btns.btn_simple_red_60x16_Down fore=Btns.btn_simple_red_60x16 />");
//			searchBoxes.append("</td>");
//		}
//		else if (searchClanName.length() > 0)
//		{
//			searchBoxes.append("<td width=180 align=center height=25>");
//			makeFilledEditBox(searchBoxes, searchClanName);
//			searchBoxes.append("</td>");
//			searchBoxes.append("<td width=180 align=center>");
//			makeFilledEditBox(searchBoxes, "");
//			searchBoxes.append("</td>");
//			searchBoxes.append("<td width=180 align=center>");
//			makeFilledEditBox(searchBoxes, "");
//			searchBoxes.append("</td>");
//			searchBoxes.append("</tr>");
//			searchBoxes.append("<tr>");
//			searchBoxes.append("<td width=180 align=center>");
//			searchBoxes.append("<button action=\"bypass _bbsclan_clanList___\" value=\"Clear\" width=60 height=16 back=Btns.btn_simple_red_60x16_Down fore=Btns.btn_simple_red_60x16 />");
//			searchBoxes.append("</td>");
//			searchBoxes.append("<td width=180 align=center>");
//			searchBoxes.append("<br>");
//			searchBoxes.append("</td>");
//			searchBoxes.append("<td width=180 align=center>");
//			searchBoxes.append("<br>");
//			searchBoxes.append("</td>");
//		}
//		else if (searchPlayerName.length() > 0)
//		{
//			searchBoxes.append("<td width=180 align=center height=25>");
//			makeFilledEditBox(searchBoxes, "");
//			searchBoxes.append("</td>");
//			searchBoxes.append("<td width=180 align=center>");
//			makeFilledEditBox(searchBoxes, searchPlayerName);
//			searchBoxes.append("</td>");
//			searchBoxes.append("<td width=180 align=center>");
//			makeFilledEditBox(searchBoxes, "");
//			searchBoxes.append("</td>");
//			searchBoxes.append("</tr>");
//			searchBoxes.append("<tr>");
//			searchBoxes.append("<td width=180 align=center>");
//			searchBoxes.append("<br>");
//			searchBoxes.append("</td>");
//			searchBoxes.append("<td width=180 align=center>");
//			searchBoxes.append("<button action=\"bypass _bbsclan_clanList___\" value=\"Clear\" width=60 height=16 back=Btns.btn_simple_red_60x16_Down fore=Btns.btn_simple_red_60x16 />");
//			searchBoxes.append("</td>");
//			searchBoxes.append("<td width=180 align=center>");
//			searchBoxes.append("<br>");
//			searchBoxes.append("</td>");
//		}
//		else
//		{
//			searchBoxes.append("<td width=180 align=center height=25>");
//			makeFilledEditBox(searchBoxes, "");
//			searchBoxes.append("</td>");
//			searchBoxes.append("<td width=180 align=center>");
//			makeFilledEditBox(searchBoxes, "");
//			searchBoxes.append("</td>");
//			searchBoxes.append("<td width=180 align=center>");
//			makeFilledEditBox(searchBoxes, searchAllianceName);
//			searchBoxes.append("</td>");
//			searchBoxes.append("</tr>");
//			searchBoxes.append("<tr>");
//			searchBoxes.append("<td width=180 align=center>");
//			searchBoxes.append("<br>");
//			searchBoxes.append("</td>");
//			searchBoxes.append("<td width=180 align=center>");
//			searchBoxes.append("<br>");
//			searchBoxes.append("</td>");
//			searchBoxes.append("<td width=180 align=center>");
//			searchBoxes.append("<button action=\"bypass _bbsclan_clanList___\" value=\"Clear\" width=60 height=16 back=Btns.btn_simple_red_60x16_Down fore=Btns.btn_simple_red_60x16 />");
//			searchBoxes.append("</td>");
//		}
//
//		// Replacements
//		html = html.replace("%clanList%", clanList.toString());
//		html = html.replace("%currentPage%", String.valueOf(page + 1));
//		html = html.replace("%searchBoxes%", searchBoxes.toString());
//
//		ImagesCache.getInstance().sendUsedImages(html, player);
//		ShowBoard.separateAndSend(html, player);
//	}
//
//	private void showClanDetailsPage(Player player, String searchClanName, String searchPlayerName, String searchAllianceName, int page, int clanId)
//	{
//		final Clan clan = ClanTable.getInstance().getClan(clanId);
//		final int maxSkillsOnLine = 10;
//
//		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clanList/clanDetails.htm", player);
//
//		// Alliance Crest
//		StringBuilder allianceCrest = new StringBuilder();
//		if (clan.hasAllianceCrest())
//		{
//			allianceCrest.append("<table cellpadding=0 cellspacing=0 width=8 height=16 background=Crest.crest_1_" + clan.getAlliance().getAllyCrestId() + ">");
//			allianceCrest.append("<tr>");
//			allianceCrest.append("<td width=8 height=4>");
//			allianceCrest.append("<img src=L2.NonEdistingImage width=8 height=5>");
//			allianceCrest.append("</td>");
//			allianceCrest.append("</tr>");
//			allianceCrest.append("<tr>");
//			allianceCrest.append("<td width=8 height=12>");
//			allianceCrest.append("<br>");
//			allianceCrest.append("</td>");
//			allianceCrest.append("</tr>");
//			allianceCrest.append("</table>");
//		}
//		else
//			allianceCrest.append("<br>");
//
//		// Clan Crest
//		StringBuilder clanCrest = new StringBuilder();
//		if (clan.hasCrest())
//		{
//			clanCrest.append("<table cellpadding=0 cellspacing=0 width=16 height=16 background=Crest.crest_1_" + clan.getCrestId() + ">");
//			clanCrest.append("<tr>");
//			clanCrest.append("<td width=16 height=4>");
//			clanCrest.append("<img src=L2.NonEdistingImage width=16 height=5>");
//			clanCrest.append("</td>");
//			clanCrest.append("</tr>");
//			clanCrest.append("<tr>");
//			clanCrest.append("<td width=16 height=12>");
//			clanCrest.append("<br>");
//			clanCrest.append("</td>");
//			clanCrest.append("</tr>");
//			clanCrest.append("</table>");
//		}
//		else
//			clanCrest.append("<br>");
//
//		// Base
//		if (clan.getCastle() > 0)
//			html = html.replace("%base%", ResidenceHolder.getInstance().getResidence(clan.getCastle()).getName() + " Castle");
//		else if (clan.getHasFortress() > 0)
//			html = html.replace("%base%", ResidenceHolder.getInstance().getResidence(clan.getHasFortress()).getName());
//		else
//			html = html.replace("%base%", "No Base");
//
//		// Clan Hall
//		if (clan.getHasHideout() > 0)
//			html = html.replace("%clanHall%", ResidenceHolder.getInstance().getResidence(clan.getHasHideout()).getName());
//		else
//			html = html.replace("%clanHall%", "No Clan Hall");
//
//		// Clan Notice
//		StringBuilder clanNotice = new StringBuilder();
//		if (clan.canEditNotice(player))
//		{
//			clanNotice.append("<table width=300 cellspacing=2 cellpadding=4 height=20 bgcolor=011118>");
//			clanNotice.append("<tr>");
//			clanNotice.append("<td width=300 align=center valign=top>");
//			clanNotice.append("<font color=ff8e3b name=hs9>Clan Login Notice</font>");
//			clanNotice.append("</td>");
//			clanNotice.append("</tr>");
//			clanNotice.append("</table>");
//			clanNotice.append("<table width=300 cellspacing=2 cellpadding=4 bgcolor=00080b>");
//			clanNotice.append("<tr>");
//			clanNotice.append("<td FIXWIDTH=300 align=center valign=top>");
//			if (clan.getNotice() != null && clan.getNotice().length() > 0)
//				clanNotice.append("<button action=\"bypass _bbsclan_editClanNotice_" + convertBackTags(clan.getNotice()) + "\" value=\"Edit\" width=150 height=22 back=Btns.btn_simple_blue_150x22_Down fore=Btns.btn_simple_blue_150x22>");
//			else
//				clanNotice.append("<button action=\"bypass _bbsclan_editClanNotice_\" value=\"Edit\" width=150 height=22 back=Btns.btn_simple_blue_150x22_Down fore=Btns.btn_simple_blue_150x22>");
//			clanNotice.append("</td>");
//			clanNotice.append("</tr>");
//			clanNotice.append("</table>");
//		}
//
//		// Clan Skills
//		StringBuilder clanSkills = new StringBuilder();
//		if (clan.getSkills().size() > 0)
//		{
//			clanSkills.append("<table border=0 cellspacing=2 cellpadding=12 width=450 height=45 bgcolor=011118>");
//			clanSkills.append("<tr>");
//			clanSkills.append("<td width=450 valign=top>");
//			clanSkills.append("<table cellspacing=0 cellpadding=0>");
//			clanSkills.append("<tr>");
//			int lastIndex = 0;
//			int index = 0;
//			for (Skill skill : clan.getSkills())
//			{
//				makeSkillTr(clanSkills, skill);
//				if (index + 1 >= clan.getSkills().size())
//					lastIndex = index;
//				if (index % maxSkillsOnLine == maxSkillsOnLine - 1 && index + 1 < clan.getSkills().size())
//				{
//					clanSkills.append("</tr>");
//					clanSkills.append("<tr>");
//				}
//				index++;
//			}
//			index = 0;
//			final List<Skill> unitSkills = clan.getUnitTopSkills();
//			for (Skill skill : unitSkills)
//			{
//				makeSkillTr(clanSkills, skill);
//				if ((lastIndex + index + 1) % maxSkillsOnLine == maxSkillsOnLine - 1 && index + 1 < unitSkills.size())
//				{
//					clanSkills.append("</tr>");
//					clanSkills.append("<tr>");
//				}
//				index++;
//			}
//			clanSkills.append("</tr>");
//			clanSkills.append("</table>");
//			clanSkills.append("</td>");
//			clanSkills.append("</tr>");
//			clanSkills.append("</table>");
//		}
//		else
//		{
//			clanSkills.append("<table border=0 cellspacing=2 cellpadding=4 width=450 height=25 bgcolor=011118>");
//			clanSkills.append("<tr>");
//			clanSkills.append("<td width=450 align=center valign=top>");
//			clanSkills.append("<font color=e7a689>Clan has no skills!</font>");
//			clanSkills.append("</td>");
//			clanSkills.append("</tr>");
//		}
//
//		// Clan Wars
//		StringBuilder clanWars = new StringBuilder();
//		if (clan.getWarClans().size() > 0)
//		{
//			clanWars.append("<table border=0 cellspacing=2 cellpadding=4 width=456 height=25 bgcolor=011118>");
//			clanWars.append("<tr>");
//			clanWars.append("<td width=130 valign=top>");
//			clanWars.append("<font color=ff8e3b>Clan</font>");
//			clanWars.append("</td>");
//			clanWars.append("<td width=120 align=center valign=top>");
//			clanWars.append("<font color=ff8e3b>Leader</font>");
//			clanWars.append("</td>");
//			clanWars.append("<td width=62 align=center valign=top>");
//			clanWars.append("<font color=ff8e3b>Level</font>");
//			clanWars.append("</td>");
//			clanWars.append("<td width=60 align=center valign=top>");
//			clanWars.append("<font color=ff8e3b>Members</font>");
//			clanWars.append("</td>");
//			clanWars.append("<td width=40 align=center valign=top>");
//			clanWars.append("<font color=ff8e3b>info</font>");
//			clanWars.append("</td>");
//			clanWars.append("</tr>");
//			clanWars.append("</table>");
//			int index = 0;
//			for (Clan warClan : clan.getWarClans())
//			{
//				if (index == 6)
//					break;
//
//				clanWars.append("<table border=0 cellspacing=2 cellpadding=4 width=456 height=25 bgcolor=" + (index % 2 == 0  ? "2f2f2f" : "171717") + ">");
//				clanWars.append("<tr>");
//				clanWars.append("<td width=130 valign=top>");
//				clanWars.append("<font color=e7a689>" + warClan.getName() + "</font>");
//				clanWars.append("</td>");
//				clanWars.append("<td width=120 align=center valign=top>");
//				clanWars.append("<font color=e7a689>" + warClan.getLeaderName() + "</font>");
//				clanWars.append("</td>");
//				clanWars.append("<td width=62 align=center valign=top>");
//				clanWars.append("<font color=e7a689>" + warClan.getLevel() + "</font>");
//				clanWars.append("</td>");
//				clanWars.append("<td width=60 align=center valign=top>");
//				clanWars.append("<font color=e7a689>" + warClan.getAllSize() + "</font>");
//				clanWars.append("</td>");
//				clanWars.append("<td width=40 align=center valign=top>");
//				clanWars.append("<button action=\"bypass _bbsclan_clanDetails_" + searchClanName + "_" + searchPlayerName + "_" + searchAllianceName + "_" + page + "_" + warClan.getClanId() + "\" width=14 height=14 back=L2UI_CT1.Button_DF_Input_Down fore=Btns.info_blue>");
//				clanWars.append("</td>");
//				clanWars.append("</tr>");
//				clanWars.append("</table>");
//				index++;
//			}
//			clanWars.append("<table border=0 cellspacing=2 cellpadding=4 width=457 height=24 bgcolor=00080b>");
//			clanWars.append("<tr>");
//			clanWars.append("<td width=300 align=center valign=top>");
//			clanWars.append("<font color=ff8e3b>Wars count:</font> <font color=e7a689>" + clan.getWarClans().size() + "</font>");
//			clanWars.append("</td>");
//			clanWars.append("</tr>");
//			clanWars.append("</table>");
//		}
//		else
//		{
//			clanWars.append("<table border=0 cellspacing=2 cellpadding=4 width=457 height=25 bgcolor=011118>");
//			clanWars.append("<tr>");
//			clanWars.append("<td FIXWIDTH=300 align=center valign=top>");
//			clanWars.append("<font color=e7a689>Clan is at peace and has no clan wars</font>");
//			clanWars.append("</td>");
//			clanWars.append("</tr>");
//			clanWars.append("</table>");
//		}
//
//		// Replacements
//		html = html.replace("%allianceCrest%", allianceCrest.toString());
//		html = html.replace("%clanCrest%", clanCrest.toString());
//		html = html.replace("%clanNotice%", clanNotice.toString());
//		html = html.replace("%clanSkills%", clanSkills.toString());
//		html = html.replace("%clanWars%", clanWars.toString());
//		html = html.replace("%clanName%", clan.getName());
//		html = html.replace("%clanId%", String.valueOf(clan.getClanId()));
//		html = html.replace("%clanLeaderName%", clan.getLeaderName());
//		html = html.replace("%clanAlliance%", (clan.getAllyId() > 0 ? clan.getAlliance().getAllyName() : "No Alliance"));
//		html = html.replace("%clanLvl%", String.valueOf(clan.getLevel()));
//		html = html.replace("%clanAllSize%", String.valueOf(clan.getAllSize()));
//		html = html.replace("%clanReputation%", DECIMAL_FORMATTER.format(clan.getReputationScore()));
//		html = html.replace("%clanAverageLvl%", String.valueOf(clan.getAverageLevel()));
//		html = html.replace("%clanOnlineMembers%", String.valueOf(clan.getOnlineMembers().size()));
//		html = html.replace("%searchClanName%", searchClanName);
//		html = html.replace("%searchPlayerName%", searchPlayerName);
//		html = html.replace("%searchAllianceName%", searchAllianceName);
//		html = html.replace("%page%", String.valueOf(page));
//
//		ImagesCache.getInstance().sendUsedImages(html, player);
//		ShowBoard.separateAndSend(html, player);
//	}
//
//	private void showEditClanNoticePage(Player player, String communityArgsValue)
//	{
//		final Clan clan = player.getClan();
//
//		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clanList/editClanNotice.htm", player);
//
//		// Clan Notice
//		if (clan.getNotice() != null && clan.getNotice().length() > 0)
//			html = html.replace("%clanNotice%", clan.getNotice());
//		else
//			html = html.replace("%clanNotice%", "Notice doesn't exist...");
//
//		// Replacements
//		html = html.replace("%clanNoticeMaxLen%", String.valueOf(ConfigHolder.getInt("ClanListNoticeMaxLength")));
//		html = html.replace("%clanNoticeMaxLineBreaks%", String.valueOf(ConfigHolder.getInt("ClanListNoticeMaxLineBreaks")));
//		html = html.replace("%clanId%", String.valueOf(clan.getClanId()));
//
//		ImagesCache.getInstance().sendUsedImages(html, player);
//		ShowBoard.separateAndSend(html, player);
//	}
//
//	private void makeSkillTr(StringBuilder st, Skill skill)
//	{
//		st.append("<td width=42 height=46 align=center valign=top>");
//		st.append("<table border=0 cellspacing=0 cellpadding=0 width=32 height=32 background=" + skill.getIcon() + ">");
//		st.append("<tr>");
//		st.append("<td width=32 align=right>");
//		st.append("<table border=0 width=16 height=16 background=L2UI_CT1.Windows_DF_TooltipBG>");
//		st.append("<tr>");
//		st.append("<td width=16 align=center valign=center>");
//		st.append("<font color=FFFFFF>" + skill.getLevel() + "</font>");
//		st.append("</td>");
//		st.append("</tr>");
//		st.append("</table>");
//		st.append("</td>");
//		st.append("</tr>");
//		st.append("</table>");
//		st.append("</td>");
//	}
//
//	private void makeClanListTable(StringBuilder st, Player player, Clan clan, int index, String searchClanName, String searchPlayerName, String searchAllianceName, int page)
//	{
//		final boolean myClan = player.getClan() != null && player.getClanId() == clan.getClanId();
//		trySendCrest(player, clan);
//
//		st.append("<table>");
//		st.append("<tr>");
//		st.append("<td align=center>");
//		st.append("<table height=25 bgcolor=" + (myClan ? "00080b" : (index % 2 == 0 ? "011118" : "031f28")) + ">");
//		st.append("<tr>");
//		st.append("<td>");
//		st.append("<table>");
//		st.append("<tr>");
//		st.append("<td width=30 align=center valign=top>");
//		st.append("<font color=ff8e3b>" + getRank(clan) + ".</font>");
//		st.append("</td>");
//		st.append("<td width=155 valign=top>");
//		st.append("<table cellspacing=0 cellpadding=0>");
//		st.append("<tr>");
//		st.append("<td width=8 align=right valign=center>");
//		if (clan.hasAllianceCrest())
//		{
//			st.append("<table cellpadding=0 cellspacing=0 width=8 height=16 background=Crest.crest_1_" + clan.getAlliance().getAllyCrestId() + ">");
//			st.append("<tr>");
//			st.append("<td width=8 height=4>");
//			st.append("<img src=L2.NonEdistingImage width=8 height=5>");
//			st.append("</td>");
//			st.append("</tr>");
//			st.append("<tr>");
//			st.append("<td width=8 height=12>");
//			st.append("<br>");
//			st.append("</td>");
//			st.append("</tr>");
//			st.append("</table>");
//		}
//		else
//		{
//			st.append("<br>");
//		}
//		st.append("</td>");
//		st.append("<td width=22 align=left valign=center>");
//		if (clan.hasCrest())
//		{
//			st.append("<table cellpadding=0 cellspacing=0 width=16 height=16 background=Crest.crest_1_" + clan.getCrestId() + ">");
//			st.append("<tr>");
//			st.append("<td width=16 height=4>");
//			st.append("<img src=L2.NonEdistingImage width=16 height=5>");
//			st.append("</td>");
//			st.append("</tr>");
//			st.append("<tr>");
//			st.append("<td width=16 height=12>");
//			st.append("<br>");
//			st.append("</td>");
//			st.append("</tr>");
//			st.append("</table>");
//		}
//		else
//		{
//			st.append("<br>");
//		}
//		st.append("</td>");
//		st.append("<td width=125 align=left valign=top>");
//		st.append("<font color=bc2b0e><a action=\"bypass _bbsclan_clanDetails_" + searchClanName + "_" + searchPlayerName + "_" + searchAllianceName + "_" + page + "_" + clan.getClanId() + "\">" + clan.getName() + "</a></font>");
//		st.append("</td>");
//		st.append("</tr>");
//		st.append("</table>");
//		st.append("</td>");
//		st.append("<td width=128 align=center valign=top>");
//		if (clan.getCastle() > 0)
//			st.append("<font color=e7a689>" + ResidenceHolder.getInstance().getResidence(clan.getCastle()).getName() + " Castle</font>");
//		else if (clan.getHasFortress() > 0)
//			st.append("<font color=e7a689>" + ResidenceHolder.getInstance().getResidence(clan.getHasFortress()).getName() + "</font>");
//		else
//			st.append("<br>");
//		st.append("</td>");
//		if (searchPlayerName.length() > 0)
//		{
//			st.append("<td width=128 align=center>");
//			if (getFirstMemberByName(clan,searchPlayerName) != null)
//				st.append("<font color=e7a689>" + getFirstMemberByName(clan,searchPlayerName).getName() + "</font>");
//			else
//				st.append("<br>");
//			st.append("</td>");
//		}
//		else if (searchAllianceName.length() > 0)
//		{
//			st.append("<td width=128 align=center>");
//			if (clan.getAlliance() != null)
//				st.append("<font color=e7a689>" + clan.getAlliance().getAllyName() + "</font>");
//			else
//				st.append("<br>");
//			st.append("</td>");
//		}
//		st.append("<td width=140 align=center valign=top>");
//		st.append("<font color=e7a689>" + clan.getLeaderName() + "</font>");
//		st.append("</td>");
//		st.append("<td width=80 align=center valign=top>");
//		st.append("<font color=");
//		switch (clan.getLevel())
//		{
//			case 11:
//				st.append("FFCC33");
//				break;
//			case 10:
//			case 9:
//			case 8:
//				st.append("66CCCC");
//				break;
//			default:
//				st.append("339933");
//		}
//		st.append(clan.getLevel());
//		st.append("</font>");
//		st.append("</td>");
//		st.append("<td width=50 align=center valign=top>");
//		st.append("<font color=e7a689>" + clan.getAllSize() + "</font>");
//		st.append("</td>");
//		st.append("<td width=40 align=center valign=top>");
//		st.append("<button action=\"bypass _bbsclan_clanDetails_" + searchClanName + "_" + searchPlayerName + "_" + searchAllianceName + "_" + page + "_" + clan.getClanId() + "\" width=14 height=14 back=L2UI_CT1.Button_DF_Input_Down fore=Btns.info_blue>");
//		st.append("</td>");
//		st.append("</tr>");
//		st.append("</table>");
//		st.append("</td>");
//		st.append("</tr>");
//		st.append("</table>");
//		st.append("</td>");
//		st.append("</tr>");
//		st.append("</table>");
//	}
//
//	private void makeEmptyClanListTable(StringBuilder st, Player player, int index, String searchClanName, String searchPlayerName, String searchAllianceName, int page)
//	{
//		st.append("<table>");
//		st.append("<tr>");
//		st.append("<td align=center>");
//		st.append("<table height=25 bgcolor=" + (index % 2 == 0 ? "011118" : "031f28") + ">");
//		st.append("<tr>");
//		st.append("<td>");
//		st.append("<table>");
//		st.append("<tr>");
//		st.append("<td width=30 align=center valign=top>");
//		st.append("<br>");
//		st.append("</td>");
//		st.append("<td width=155 valign=top>");
//		st.append("<br>");
//		st.append("</td>");
//		st.append("<td width=128>");
//		st.append("<br>");
//		st.append("</td>");
//		if (searchPlayerName.length() > 0 || searchAllianceName.length() > 0)
//		{
//			st.append("<td width=128>");
//			st.append("<br>");
//			st.append("</td>");
//		}
//		st.append("<td width=140 align=center valign=top>");
//		st.append("<br>");
//		st.append("</td>");
//		st.append("<td width=80 align=center valign=top>");
//		st.append("<br>");
//		st.append("</td>");
//		st.append("<td width=50 align=center valign=top>");
//		st.append("<br>");
//		st.append("</td>");
//		st.append("<td width=40 align=center valign=top>");
//		st.append("<br>");
//		st.append("</td>");
//		st.append("</tr>");
//		st.append("</table>");
//		st.append("</td>");
//		st.append("</tr>");
//		st.append("</table>");
//		st.append("</td>");
//		st.append("</tr>");
//		st.append("</table>");
//	}
//
//	private void makeFilledEditBox(StringBuilder st, String content)
//	{
//		st.append("<table cellspacing=0 cellpadding=0 width=125 height=21 background=L2UI_CT1.CharacterPassword_DF_EditBox>");
//		st.append("<tr>");
//		st.append("<td fixwidth=121 align=left>");
//		st.append("<font color=LEVEL> " + content + "</font>");
//		st.append("</td>");
//		st.append("<td width=4>");
//		st.append("<br1>");
//		st.append("</td>");
//		st.append("</tr>");
//		st.append("</table>");
//	}
//
//	private static boolean isNoticeAlright(Player player, String notice, boolean sendErrorMsg)
//	{
//		if (player.getClan() == null || !player.getClan().canEditNotice(player))
//		{
//			if (sendErrorMsg)
//				sendErrorMsg(player, "You are not authorized to edit Clan Notice!");
//			return false;
//		}
//		if (!notice.isEmpty() && !ConfigHolder.getBool("AllowClanListNewNotice"))
//		{
//			if (sendErrorMsg)
//				sendErrorMsg(player, "This feature is currently Disabled!");
//			return false;
//		}
//		if (notice.isEmpty() && !ConfigHolder.getBool("AllowClanListDeleteNotice"))
//		{
//			if (sendErrorMsg)
//				sendErrorMsg(player, "This feature is currently Disabled!");
//			return false;
//		}
//		if (!ConfigHolder.getPattern("ClanListNoticePattern").matcher(notice).matches())
//		{
//			if (sendErrorMsg)
//				sendErrorMsg(player, "Notice contains not allowed letters!");
//			return false;
//		}
//		if (notice.length() > ConfigHolder.getInt("ClanListNoticeMaxLength"))
//		{
//			if (sendErrorMsg)
//				sendErrorMsg(player, "New Notice is too long(" + notice.length() + " chars)!");
//			return false;
//		}
//		int lines = StringUtils.countMatches(notice, "<br");
//		lines += StringUtils.countMatches(notice, "\n");
//		if (lines > ConfigHolder.getInt("ClanListNoticeMaxLineBreaks"))
//		{
//			if (sendErrorMsg)
//				sendErrorMsg(player, "New Notice has too many lines(" + lines + ")!");
//			return false;
//		}
//		return true;
//	}
//
//	private static void editNotice(Player player, String notice)
//	{
//		final Clan clan = player.getClan();
//		notice = notice.replace("<", "");
//		notice = notice.replace(">", "");
//		notice = notice.replace("&", "");
//		notice = notice.replace("$", "");
//		notice = replaceTags(notice);
//
//		if (notice.isEmpty())
//		{
//			try (Connection con = DatabaseFactory.getInstance().getConnection();
//			     PreparedStatement statement = con.prepareStatement("DELETE FROM `bbs_clannotice` WHERE clan_id=? AND type=?"))
//			{
//				statement.setInt(1, clan.getClanId());
//				statement.setInt(2, 1);
//				statement.execute();
//			}
//			catch (SQLException e)
//			{
//				_log.error("Error while saving Clan Notice: " + notice + " For Player: " + player.toString(), e);
//				return;
//			}
//			clan.setNotice("");
//		}
//		else
//		{
//			try (Connection con = DatabaseFactory.getInstance().getConnection();
//			     PreparedStatement statement = con.prepareStatement("REPLACE INTO `bbs_clannotice`(clan_id, type, notice) VALUES(?, ?, ?)"))
//			{
//				statement.setInt(1, clan.getClanId());
//				statement.setInt(2, 1);
//				statement.setString(3, notice);
//				statement.execute();
//			}
//			catch (SQLException e)
//			{
//				_log.error("Error while saving Clan Notice: " + notice + " For Player: " + player.toString(), e);
//				return;
//			}
//			clan.setNotice(notice.replace("\n", "<br1>"));
//		}
//	}
//
//	private static String replaceTags(String notice)
//	{
//		for (String[] tag : ConfigHolder.getMultiStringArray("ClanListNoticeTags"))
//			notice = notice.replaceAll("(?i)\\[" + tag[0] + "\\]", " <font color=" + tag[1] + "> ");
//		notice = notice.replace("[/color]", "</font>");
//		return notice;
//	}
//
//	public static String convertBackTags(String notice)
//	{
//		for (String[] tag : ConfigHolder.getMultiStringArray("ClanListNoticeTags"))
//			notice = notice.replace(" <font color=" + tag[1] + "> ", "[" + tag[0] + "]");
//		notice = notice.replace("</font>", "[/color]");
//		return notice;
//	}
//
//	public static void trySendCrest(Player player, Clan clan)
//	{
//		final int crestId = CrestCache.getInstance().getPledgeCrestId(clan.getClanId());
//		if (crestId > 0)
//			player.sendPacket(new PledgeCrest(crestId, CrestCache.getInstance().getPledgeCrest(crestId)));
//		if (clan.getAlliance() != null)
//		{
//			final int allyCrest = CrestCache.getInstance().getAllyCrestId(clan.getAllyId());
//			if (allyCrest > 0)
//				player.sendPacket(new PledgeCrest(crestId, CrestCache.getInstance().getAllyCrest(allyCrest)));
//		}
//	}
//
//	public int getRank(Clan clan)
//	{
//		synchronized (_sortedClansLock)
//		{
//			final int index = ArrayUtils.indexOf(_clansSortedByRank, clan);
//			if (index < 0)
//				return 0;
//			return index + 1;
//		}
//	}
//
//	public static Clan[] getSortedClans(Player player, String searchClanName, String searchPlayerName, String searchAllianceName)
//	{
//		final SortType sortType = SortType.getQuickVarValue(player);
//		final Clan[] sortedClans = getSortedClans(sortType);
//		if (searchClanName.isEmpty() && searchPlayerName.isEmpty() && searchAllianceName.isEmpty())
//			return sortedClans;
//		final List<Clan> searchedClans = new ArrayList<Clan>(sortedClans.length);
//		for (Clan clan : sortedClans)
//			if (isSearchedClan(clan, searchClanName, searchPlayerName, searchAllianceName))
//				searchedClans.add(clan);
//		return searchedClans.toArray(new Clan[searchedClans.size()]);
//	}
//
//	private static boolean isSearchedClan(Clan clan, String searchClanName, String searchPlayerName, String searchAllianceName)
//	{
//		return (searchClanName.isEmpty() || StringUtils.containsIgnoreCase(clan.getName(), searchClanName)) && (searchPlayerName.isEmpty() || getFirstMemberByName(clan, searchPlayerName) != null) && (searchAllianceName.isEmpty() || clan.getAlliance() != null && StringUtils.containsIgnoreCase(clan.getAlliance()
//		                                                                                                                                                                                                                                                                                                 .getAllyName(),
//		                                                                                                                                                                                                                                                                                             searchAllianceName));
//	}
//
//	@Nullable
//	public static UnitMember getFirstMemberByName(Clan clan, String searchPlayerName)
//	{
//		for (UnitMember member : clan.getAllMembers())
//			if (StringUtils.containsIgnoreCase(member.getName(), searchPlayerName))
//				return member;
//		return null;
//	}
//
//	private static Clan[] getSortedClans(SortType sortType)
//	{
//		final Clan[] clans = ClanTable.getInstance().getClans();
//		Arrays.sort(clans, new ClanComparator(sortType));
//		return clans;
//	}
//
//	private static void sendErrorMsg(Player player, String msg)
//	{
//		player.sendPacket(new Say2(0, ChatType.COMMANDCHANNEL_ALL, "Error", msg));
//	}
//
//	@Override
//	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
//	{
//	}
//
//	private class Listener implements OnPlayerEnterListener
//	{
//		@Override
//		public void onPlayerEnter(Player player)
//		{
//			final Clan clan = player.getClan();
//			if (clan == null || clan.getLevel() < 2)
//				return;
//
//			if (clan.getNotice() == null)
//			{
//				String notice = "";
//				int type = 0;
//				try (Connection con = DatabaseFactory.getInstance().getConnection();
//				     PreparedStatement statement = con.prepareStatement("SELECT * FROM `bbs_clannotice` WHERE `clan_id` = ? and type != 2"))
//				{
//					statement.setInt(1, clan.getClanId());
//					try (ResultSet rset = statement.executeQuery())
//					{
//						if (rset.next())
//						{
//							notice = rset.getString("notice");
//							type = rset.getInt("type");
//						}
//					}
//				}
//				catch (SQLException e)
//				{
//					_log.error("While loading bbs_clannotice:", e);
//				}
//				clan.setNotice(type == 1 ? notice.replace("\n", "<br1>\n") : "");
//			}
//			if (!clan.getNotice().isEmpty())
//			{
//				String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan_popup.htm", player);
//				html = html.replace("%pledge_name%", clan.getName());
//				html = html.replace("%content%", clan.getNotice());
//				player.sendPacket(new NpcHtmlMessage(0).setHtml(html));
//			}
//		}
//	}
//
//	private static class ClanComparator implements Comparator<Clan>, Serializable
//	{
//		private static final long serialVersionUID = -3385635365249147356L;
//		private final SortType _sortType;
//
//		private ClanComparator(SortType sortType)
//		{
//			_sortType = sortType;
//		}
//
//		@Override
//		public int compare(Clan o1, Clan o2)
//		{
//			if (o1.equals(o2))
//				return 0;
//
//			final int result = _sortType.compare(o1, o2);
//			if (result != 0)
//				return result;
//
//			for (SortType type : SortType.values())
//			{
//				if (type != _sortType)
//				{
//					final int differentSortResult = type.compare(o1, o2);
//					if (differentSortResult != 0)
//						return differentSortResult;
//				}
//			}
//			return Integer.compare(o2.getClanId(), o1.getClanId());
//		}
//	}
//
//	public enum SortType
//	{
//		RANK,
//		NAME,
//		BASE,
//		LEADER_NAME,
//		LEVEL,
//		MEMBERS_COUNT;
//		public static String getQuickVarName()
//		{
//			return "ClanListSort";
//		}
//
//		public static SortType getQuickVarValue(Player player)
//		{
//			return valueOf(player.getQuickVarS(getQuickVarName(), CommunityClanTales.DEFAULT_SORT_TYPE.name()));
//		}
//
//		public static boolean saveSortVar(Player player, String sortName)
//		{
//			if (sortName == null)
//				return false;
//
//			try
//			{
//				final SortType type = valueOf(sortName);
//				player.addQuickVar(getQuickVarName(), type.name());
//				return true;
//			}
//			catch (IllegalArgumentException e)
//			{
//				_log.error("Error while trying to add " + SortType.class.getSimpleName() + " Sort Name: " + sortName + " By Player: " + player.toString());
//				return false;
//			}
//		}
//
//		public int compare(Clan o1, Clan o2)
//		{
//			switch (this)
//			{
//				case RANK:
//				{
//					final int membersCompare = Integer.compare(o2.getOnlineMembers().size(), o1.getOnlineMembers().size());
//					if (membersCompare != 0)
//						return membersCompare;
//
//					final int castleCompare = Boolean.compare(o2.getCastle() > 0, o1.getCastle() > 0);
//					if (castleCompare != 0)
//						return castleCompare;
//
//					final int fortressCompare = Boolean.compare(o2.getHasFortress() > 0, o1.getHasFortress() > 0);
//					if (fortressCompare != 0)
//						return fortressCompare;
//
//					return Integer.compare(o1.getClanId(), o2.getClanId());
//				}
//				case NAME:
//				{
//					return o1.getName().compareTo(o2.getName());
//				}
//				case BASE:
//				{
//					final int castleCompare = Boolean.compare(o2.getCastle() > 0, o1.getCastle() > 0);
//					if (castleCompare != 0)
//						return castleCompare;
//					return Boolean.compare(o2.getHasFortress() > 0, o1.getHasFortress() > 0);
//				}
//				case LEADER_NAME:
//				{
//					if (o1.getLeaderName().equals(o2.getLeaderName()))
//						return 0;
//					if (o2.getLeaderName().isEmpty())
//						return -1;
//					if (o1.getLeaderName().isEmpty())
//						return 1;
//					return o1.getLeaderName().compareTo(o2.getLeaderName());
//				}
//				case LEVEL:
//				{
//					return Integer.compare(o2.getLevel(), o1.getLevel());
//				}
//				case MEMBERS_COUNT:
//				{
//					return Integer.compare(o2.getAllSize(), o1.getAllSize());
//				}
//				default:
//				{
//					return 0;
//				}
//			}
//		}
//	}
//}
