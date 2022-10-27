package services.community;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.trove.map.hash.TIntObjectHashMap;
import javolution.util.FastMap;
import l2mv.gameserver.Config;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.fandc.academy.AcademyList;
import l2mv.gameserver.fandc.academy.AcademyRewards;
import l2mv.gameserver.handler.bbs.CommunityBoardManager;
import l2mv.gameserver.handler.bbs.ICommunityBoardHandler;
import l2mv.gameserver.listener.actor.player.impl.AcademyAnswerListener;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Request;
import l2mv.gameserver.model.Request.L2RequestType;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.Zone.ZoneType;
import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.network.serverpackets.ConfirmDlg;
import l2mv.gameserver.network.serverpackets.ShowBoard;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.Util;
import l2mv.gameserver.utils.ValueSortMap;

/**
 * @author Infern0
 */
public class CommunityBoardAcademy implements ScriptFile, ICommunityBoardHandler
{
	private static final Logger _log = LoggerFactory.getLogger(CommunityBoardAcademy.class);
	private static TIntObjectHashMap<SortBy> _playerSortBy = new TIntObjectHashMap<>();
	private static TIntObjectHashMap<String> _playerSearch = new TIntObjectHashMap<>();

	@Override
	public void onLoad()
	{
		if (Config.COMMUNITYBOARD_ENABLED && Config.ENABLE_COMMUNITY_ACADEMY)
		{
			_log.info("CommunityBoard: Academy service loaded.");
			CommunityBoardManager.getInstance().registerHandler(this);
		}
	}

	@Override
	public void onReload()
	{
		if (Config.COMMUNITYBOARD_ENABLED && Config.ENABLE_COMMUNITY_ACADEMY)
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
			"_bbsShowAcademyList",
			"_bbsRegAcademyChar",
			"_bbsShowInvitePage",
			"_bbsInviteToAcademy",
			"_bbsUnregisterFromAcademy",
			"_bbsAcademySearch",
			"_bbsAcademySort",
			"_bbsAcademyReset"
		};
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		final StringTokenizer str = new StringTokenizer(bypass, " ");
		final String cmd = str.nextToken();
		if (!checkConditions(player))
		{
			return;
		}
		if (cmd.equalsIgnoreCase("_bbsRegAcademyChar"))
		{
			academyButton(player, false);
		}
		else if (cmd.equalsIgnoreCase("_bbsShowAcademyList"))
		{
			final StringTokenizer st = new StringTokenizer(bypass, " ");
			st.nextToken();
			final int page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
			showAcademList(player, page);
		}
		else if (cmd.equalsIgnoreCase("_bbsShowInvitePage"))
		{
			final StringTokenizer st = new StringTokenizer(bypass, " ");
			st.nextToken();
			final String name = st.nextToken();
			final int page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
			showAcademyChar(player, name, page);
		}
		else if (cmd.equalsIgnoreCase("_bbsAcademySort"))
		{
			final StringTokenizer st = new StringTokenizer(bypass, " ");
			st.nextToken();
			final String sorname = st.hasMoreTokens() ? st.nextToken() : "Level";
			_playerSortBy.put(player.getObjectId(), SortBy.getEnum(sorname));
			showAcademList(player, 1);
		}
		else if (cmd.equalsIgnoreCase("_bbsAcademySearch"))
		{
			final StringTokenizer st = new StringTokenizer(bypass, " ");
			st.nextToken();
			final String search = st.hasMoreTokens() ? st.nextToken() : "";
			_playerSearch.put(player.getObjectId(), search);
			showAcademList(player, 1);
		}
		else if (cmd.equalsIgnoreCase("_bbsAcademyReset"))
		{
			_playerSearch.remove(player.getObjectId());
			showAcademList(player, 1);
		}
		else if (cmd.equalsIgnoreCase("_bbsInviteToAcademy"))
		{
			final StringTokenizer st = new StringTokenizer(bypass, " ");
			st.nextToken();
			if (st.countTokens() != 3)
			{
				return;
			}
			final String charName = st.nextToken();
			final String item = st.nextToken();
			final long price = Long.valueOf(st.nextToken());
			final int itemId = AcademyRewards.getInstance().getItemId(item);
			if (itemId == -1)
			{
				player.sendChatMessage(player.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", "Error, cannot find such item... please contact the server Administrator");
				return;
			}
			final Player acadChar = World.getPlayer(charName);
			if (acadChar == null || !acadChar.isConnected())
			{
				player.sendChatMessage(player.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", "It seems the character you wish to invite is offline.");
				showAcademList(player, 1);
				return;
			}
			if (!player.antiFlood.canInviteInAcademy(acadChar.getObjectId()))
			{
				player.sendChatMessage(player.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", "You can send invite to same academy char every 5 minutes.");
				showAcademList(player, 1);
				return;
			}
			inviteToAcademy(player, acadChar, itemId, price);
		}
		else if (cmd.equalsIgnoreCase("_bbsUnregisterFromAcademy"))
		{
			academyButton(player, true);
		}
	}

	private void academyButton(Player activeChar, boolean unregisterToAcademy)
	{
		if (!activeChar.antiFlood.canRegisterForAcademy())
		{
			activeChar.sendMessageS("Do not spam the button, please wait 5 seconds and try again.", 5);
			activeChar.sendChatMessage(activeChar.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", "Do not spam the button, please wait 5 seconds and try again.");
			return;
		}
		if (unregisterToAcademy)
		{
			AcademyList.deleteFromAcdemyList(activeChar);
			activeChar.sendMessageS("You have unregistered from Academy Search Board.", 5);
			activeChar.sendChatMessage(activeChar.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", "You have unregistered from Academy Search Board.");
			onBypassCommand(activeChar, "_bbsShowAcademyList 1");
			return;
		}
		if (activeChar.getLevel() < 5 || activeChar.getLevel() > 39)
		{
			activeChar.sendChatMessage(activeChar.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", "Level requirements are not met!");
			return;
		}
		if (activeChar.getClan() != null)
		{
			activeChar.sendChatMessage(activeChar.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", "You are in a clan already, cannot procide to academy register...");
			return;
		}
		if (!activeChar.canJoinClan())
		{
			activeChar.sendChatMessage(activeChar.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", "You have clan leave penalty. Wait it to finish and try again.");
			return;
		}
		for (final Player plr : AcademyList.getAcademyList())
		{
			if (plr == null)
			{
				continue;
			}
			if (plr == activeChar)
			{
				activeChar.sendChatMessage(activeChar.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", "You are already listed into Acaemy Search Board.");
				return;
			}
		}
		activeChar.setSearchforAcademy(true);
		AcademyList.addToAcademy(activeChar);
		activeChar.sendChatMessage(activeChar.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", "Successfuly registered into Academy Search Board.");
		activeChar.sendMessageS("Successfuly registered into Academy Search Board.", 5);
		onBypassCommand(activeChar, "_bbsShowAcademyList 1");
	}

	private static List<Player> getFilteredAcademy(String filter)
	{
		if (filter == null)
		{
			filter = "";
		}
		final List<Player> filteredList = new ArrayList<Player>();
		for (final Player plr : AcademyList.getAcademyList())
		{
			if (plr.getName().toLowerCase().contains(filter.toLowerCase()) || plr.getClassId().getName().toLowerCase().contains(filter.toLowerCase()))
			{
				filteredList.add(plr);
			}
		}
		return filteredList;
	}

	private void showAcademList(Player player, int page)
	{
		String htmltosend = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/academy/academyList.htm", player);
		final List<Player> academy = getFilteredAcademy(_playerSearch.get(player.getObjectId()));
		final List<Player> _academyList = AcademyList.getAcademyList();
		String searchfor = _playerSearch.get(player.getObjectId());
		if (searchfor == null)
		{
			searchfor = "";
		}
		final StringBuilder sb = new StringBuilder();
		SortBy sortBy = _playerSortBy.get(player.getObjectId());
		if (sortBy == null)
		{
			sortBy = SortBy.LEVEL;
		}
		final String nameOfCurSortBy = sortBy.toString() + ";";
		sb.append(nameOfCurSortBy);
		for (final SortBy s : SortBy.values())
		{
			final String str = s + ";";
			if (!str.toString().equalsIgnoreCase(nameOfCurSortBy))
			{
				sb.append(str);
			}
		}
		htmltosend = htmltosend.replaceAll("%sortbylist%", sb.toString());
		if (!_academyList.contains(player) && player.getLevel() < 40 && player.getLevel() > 4 && player.getClan() == null)
		{
			htmltosend = htmltosend.replace("%mainbutton%", "<button value=\"Register\" action=\"bypass -h _bbsRegAcademyChar\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\"><br>");
		}
		if (_academyList.contains(player))
		{
			htmltosend = htmltosend.replace("%mainbutton%", "<button value=\"Unregister\" action=\"bypass -h _bbsUnregisterFromAcademy\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\"><br>");
		}
		else
		{
			htmltosend = htmltosend.replace("%mainbutton%", "<button width=197 height=0>");
		}
		int all = 0;
		int clansvisual = 0;
		final boolean pagereached = false;
		final int totalpages = _academyList.size() / 16 + 1;
		if (page == 1)
		{
			if (totalpages == 1)
			{
				htmltosend = htmltosend.replaceAll("%more%", "&nbsp;");
			}
			else
			{
				htmltosend = htmltosend.replaceAll("%more%", "<button value=\"\" action=\"bypass _bbsShowAcademyList " + (page + 1) + " \" width=40 height=20 back=\"L2UI_CT1.Inventory_DF_Btn_RotateLeft\" fore=\"L2UI_CT1.Inventory_DF_Btn_RotateLeft\">");
			}
			htmltosend = htmltosend.replaceAll("%back%", "&nbsp;");
		}
		else if (page > 1)
		{
			if (totalpages <= page)
			{
				htmltosend = htmltosend.replaceAll("%back%", "<button value=\"\" action=\"bypass _bbsShowAcademyList " + (page - 1) + "\" width=40 height=20 back=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\" fore=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\">");
				htmltosend = htmltosend.replaceAll("%more%", "&nbsp;");
			}
			else
			{
				htmltosend = htmltosend.replaceAll("%more%", "<button value=\"\" action=\"bypass _bbsShowAcademyList " + (page + 1) + "\" width=40 height=20 back=\"L2UI_CT1.Inventory_DF_Btn_RotateLeft\" fore=\"L2UI_CT1.Inventory_DF_Btn_RotateLeft\">");
				htmltosend = htmltosend.replaceAll("%back%", "<button value=\"\" action=\"bypass _bbsShowAcademyList " + (page - 1) + "\" width=40 height=20 back=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\" fore=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\">");
			}
		}
		for (final Player plr : getSorttedacademys(_playerSortBy.get(player.getObjectId()), academy))
		{
			all++;
			if ((page == 1 && clansvisual > 16) || (!pagereached && all > page * 16))
			{
				continue;
			}
			if (!pagereached && all <= (page - 1) * 16)
			{
				continue;
			}
			clansvisual++;
			if (plr.isInOfflineMode() || !plr.isOnline())
			{
				AcademyList.deleteFromAcdemyList(plr);
				continue;
			}
			if (!plr.canJoinClan() || plr.getLevel() > 40 || plr.getClassId().getLevel() > 2)
			{
				AcademyList.deleteFromAcdemyList(plr);
				continue;
			}
			htmltosend = htmltosend.replaceAll("%icon" + clansvisual + "%", getIconByRace(plr.getRace()));
			htmltosend = htmltosend.replaceAll("%classname" + clansvisual + "%", "Class: <font color=01A9DB>" + Util.getFullClassName(plr.getClassId().getId()) + "</font>");
			htmltosend = htmltosend.replaceAll("%name" + clansvisual + "%", "Name: <font color=\"CB4646\">" + plr.getName() + "</font>");
			htmltosend = htmltosend.replaceAll("%level" + clansvisual + "%", "<font color=\"ad9d46\">[" + plr.getLevel() + "]</font>");
			htmltosend = htmltosend.replaceAll("%onlinetime" + clansvisual + "%", "Online: <font color=\"848484\">" + Util.formatTime((int) plr.getOnlineTime(), 1) + "</font>");
			if (player.getClan() != null && player.getClan().getLevel() > 4 && (player.getClanPrivileges() & Clan.CP_CL_INVITE_CLAN) == Clan.CP_CL_INVITE_CLAN && !plr.getBlockList().contains(player.getName()))
			{
				htmltosend = htmltosend.replaceAll("%request" + clansvisual + "%", "<button value=\"\" action=\"bypass _bbsShowInvitePage " + plr.getName() + " 1\" width=32 height=32 back=\"L2UI_CT1.MiniMap_DF_PlusBtn_Red\" fore=\"L2UI_CT1.MiniMap_DF_PlusBtn_Red\">");
			}
			else
			{
				htmltosend = htmltosend.replaceAll("%request" + clansvisual + "%", "<button width=32 height=0>");
			}
			htmltosend = htmltosend.replaceAll("%width" + clansvisual + "%", "180");
		}
		if (clansvisual < 16)
		{
			for (int d = clansvisual + 1; d != 17; d++)
			{
				htmltosend = htmltosend.replaceAll("%icon" + d + "%", "L2UI_CT1.Inventory_DF_CloakSlot_Disable");
				htmltosend = htmltosend.replaceAll("%classname" + d + "%", "&nbsp;");
				htmltosend = htmltosend.replaceAll("%name" + d + "%", "&nbsp;");
				htmltosend = htmltosend.replaceAll("%level" + d + "%", "&nbsp;");
				htmltosend = htmltosend.replaceAll("%onlinetime" + d + "%", "&nbsp;");
				htmltosend = htmltosend.replaceAll("%request" + d + "%", "&nbsp;");
				htmltosend = htmltosend.replaceAll("%width" + d + "%", "395");
			}
		}
		htmltosend = htmltosend.replaceAll("%searchfor%", searchfor == "" ? "&nbsp;" : "Search result for: <font color=LEVEL>" + searchfor + "</font>");
		htmltosend = htmltosend.replaceAll("%totalresults%", "" + _academyList.size());
		ShowBoard.separateAndSend(htmltosend, player);
	}

	private void showAcademyChar(Player player, String academyChar, int pageNum)
	{
		final Player plr = World.getPlayer(academyChar);
		if (plr == null || !plr.isConnected())
		{
			player.sendChatMessage(player.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", "It seems the character is offline.");
			showAcademList(player, 1);
			return;
		}
		String htmltosend = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/academy/academyRequest.htm", player);
		final StringBuilder sb = new StringBuilder();
		for (final Player academy : AcademyList.getAcademyList())
		{
			if (!academy.getName().equalsIgnoreCase(academyChar))
			{
				continue;
			}
			sb.append("<center>");
			sb.append("Select Item:");
			sb.append("<combobox width=180 height=20 var=\"items\" list=\"" + AcademyRewards.getInstance().toList() + "\"><br>");
			sb.append("Amount: " + "<edit var=\"price\" type=\"number\"  width=180 height=15 length=\"15\"><br><br>");
			sb.append("Minimum: " + Util.formatAdena(Config.ACADEMY_MIN_ADENA_AMOUNT));
			sb.append("<br1>");
			sb.append("Maximum: " + Util.formatAdena(Config.ACADEMY_MAX_ADENA_AMOUNT));
			sb.append("<button value=\"Invite To Academy\" action=\"bypass -h _bbsInviteToAcademy " + academy.getName() + " $items $price\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\"><br>");
			sb.append("<button value=\"Back\" action=\"bypass -h _bbsShowAcademyList " + pageNum + "\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\"><br>");
			sb.append("</center>");
			break;
		}
		htmltosend = htmltosend.replace("%playerName%", plr.getName() + " [" + plr.getLevel() + "]");
		htmltosend = htmltosend.replace("%body%", sb.toString());
		ShowBoard.separateAndSend(htmltosend, player);
	}

	private void inviteToAcademy(Player activeChar, Player academyChar, int itemId, long price)
	{
		if (activeChar == null || academyChar == null || !checkConditions(activeChar))
		{
			return;
		}
		price = Math.max(price, 0);
		if (price < Config.ACADEMY_MIN_ADENA_AMOUNT || price > Config.ACADEMY_MAX_ADENA_AMOUNT)
		{
			activeChar.sendChatMessage(activeChar.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", "Invalid input! Min: " + Util.formatAdena(Config.ACADEMY_MIN_ADENA_AMOUNT) + " | Max: " + Util.formatAdena(Config.ACADEMY_MAX_ADENA_AMOUNT) + " Adena.");
			showAcademyChar(activeChar, academyChar.getName(), 1);
			return;
		}
		final Clan clan = activeChar.getClan();
		if (clan == null || !clan.canInvite())
		{
			activeChar.sendPacket(SystemMsg.AFTER_A_CLAN_MEMBER_IS_DISMISSED_FROM_A_CLAN_THE_CLAN_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_ACCEPTING_A_NEW_MEMBER);
			return;
		}
		// is the activeChar have privilege to invite players
		if ((activeChar.getClanPrivileges() & Clan.CP_CL_INVITE_CLAN) != Clan.CP_CL_INVITE_CLAN)
		{
			activeChar.sendPacket(SystemMsg.ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS);
			return;
		}
		if (academyChar.getClan() == activeChar.getClan())
		{
			activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return;
		}
		if (!academyChar.canJoinClan())
		{
			activeChar.sendPacket(new SystemMessage2(SystemMsg.C1_CANNOT_JOIN_THE_CLAN_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_THEY_LEFT_ANOTHER_CLAN).addName(academyChar));
			return;
		}
		if (academyChar.getClan() != null)
		{
			activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_IS_ALREADY_A_MEMBER_OF_ANOTHER_CLAN).addName(academyChar));
			return;
		}
		if (academyChar.isBusy())
		{
			activeChar.sendPacket(new SystemMessage2(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(academyChar));
			return;
		}
		if (academyChar.getLevel() > 40 || academyChar.getClassId().getLevel() > 2)
		{
			activeChar.sendPacket(SystemMsg.TO_JOIN_A_CLAN_ACADEMY_CHARACTERS_MUST_BE_LEVEL_40_OR_BELOW_NOT_BELONG_ANOTHER_CLAN_AND_NOT_YET_COMPLETED_THEIR_2ND_CLASS_TRANSFER);
			return;
		}
		if (clan.getUnitMembersSize(Clan.SUBUNIT_ACADEMY) >= clan.getSubPledgeLimit(Clan.SUBUNIT_ACADEMY))
		{
			activeChar.sendPacket(SystemMsg.THE_ACADEMYROYAL_GUARDORDER_OF_KNIGHTS_IS_FULL_AND_CANNOT_ACCEPT_NEW_MEMBERS_AT_THIS_TIME);
			return;
		}
		if (!(Functions.getItemCount(activeChar, itemId) >= price))
		{
			activeChar.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			return;
		}
		final Request request = new Request(L2RequestType.CLAN, activeChar, academyChar).setTimeout(15000L);
		request.set("pledgeType", Clan.SUBUNIT_ACADEMY);
		academyChar.setPledgeItemId(itemId);
		academyChar.setPledgePrice(price);
		final ConfirmDlg packet = new ConfirmDlg(SystemMsg.S1, 15000).addString(activeChar.getName() + " leader of " + activeChar.getClan().getName() + " clan wish to invite you to academy! You will recive " + Util.formatAdena(price) + " " + ItemHolder.getInstance().getTemplate(itemId).getName() + " as payment!");
		academyChar.ask(packet, new AcademyAnswerListener(activeChar, academyChar));
		onBypassCommand(activeChar, "_bbsShowAcademyList 1");
		activeChar.sendMessageS("Invation has been send to " + academyChar.getName(), 5);
		activeChar.sendChatMessage(activeChar.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", "Invation has been send to " + academyChar.getName());
	}

	private boolean checkConditions(Player player)
	{
		if (player == null || player.isDead())
		{
			return false;
		}
		if (player.isCursedWeaponEquipped() || player.isInJail() || player.isDead() || player.isAlikeDead() || player.isCastingNow() || player.isInCombat() || player.isAttackingNow() || player.isInOlympiadMode() || player.isFlying() || player.isTerritoryFlagEquipped())
		{
			player.sendChatMessage(player.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", player.isLangRus() ? "Невозможно использовать в данный момент!" : "You can not use it at this moment!");
			return false;
		}
		if (player.getReflectionId() != 0 || player.isInZone(ZoneType.epic))
		{
			player.sendChatMessage(player.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", player.isLangRus() ? "Невозможно использовать в данных зонах!" : "Can not be used in these areas!");
			return false;
		}
		if (player.isInZone(ZoneType.SIEGE))
		{
			player.sendChatMessage(player.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", player.isLangRus() ? "Невозможно использовать во время осад!" : "Can not be used during the siege!");
			return false;
		}
		if (player.isTerritoryFlagEquipped())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
			return false;
		}
		return true;
	}

	private static String getIconByRace(Race race)
	{
		switch (race)
		{
		case human:
			return "icon.skill4416_human";
		case elf:
			return "icon.skill4416_elf";
		case darkelf:
			return "icon.skill4416_darkelf";
		case orc:
			return "icon.skill4416_orc";
		case dwarf:
			return "icon.skill4416_dwarf";
		case kamael:
			return "icon.skill4416_kamael";
		}
		return "icon.skill4416_etc";
	}

	private enum SortBy
	{
		LEVEL("Level"), NAME_ASC("Name(Ascending)"), NAME_DSC("Name(Descending)"), ONLINE_ASC("Online(Ascending)"), ONLINE_DSC("Online(Descending)"), CLASS_ASC("Class(Ascending)"), CLASS_DSC("Class(Descending)");

		private final String _sortName;

		private SortBy(String sortName)
		{
			_sortName = sortName;
		}

		@Override
		public String toString()
		{
			return _sortName;
		}

		public static SortBy getEnum(String sortName)
		{
			for (final SortBy sb : values())
			{
				if (sb.toString().equals(sortName))
				{
					return sb;
				}
			}
			return LEVEL;
		}
	}

	@SuppressWarnings("unchecked")
	public static List<Player> getSorttedacademys(SortBy sort, List<Player> academys)
	{
		if (sort == null)
		{
			sort = SortBy.LEVEL;
		}
		final List<Player> sorted = new ArrayList<>();
		switch (sort)
		{
		default:
		case LEVEL:
			final List<Player> notSortedValues = new ArrayList<>();
			notSortedValues.addAll(academys);
			Player storedid = null;
			int lastpoints = 0;
			while (notSortedValues.size() > 0)
			{
				if (sorted.size() == academys.size())
				{
					break;
				}
				for (final Player cplayer : notSortedValues)
				{
					if (cplayer.getLevel() >= lastpoints)
					{
						storedid = cplayer;
						lastpoints = cplayer.getLevel();
					}
				}
				if (storedid != null)
				{
					notSortedValues.remove(storedid);
					sorted.add(storedid);
					storedid = null;
					lastpoints = 0;
				}
			}
			return sorted;
		case NAME_ASC:
			final Map<Player, String> tmp = new FastMap<>();
			for (final Player academy : academys)
			{
				tmp.put(academy, academy.getName());
			}
			sorted.addAll(ValueSortMap.sortMapByValue(tmp, true).keySet());
			return sorted;
		case NAME_DSC:
			final Map<Player, String> tmp2 = new FastMap<>();
			for (final Player academy : academys)
			{
				tmp2.put(academy, academy.getName());
			}
			sorted.addAll(ValueSortMap.sortMapByValue(tmp2, false).keySet());
			return sorted;
		case ONLINE_ASC:
			final Map<Player, Long> tmp3 = new FastMap<>();
			for (final Player academy : academys)
			{
				tmp3.put(academy, academy.getOnlineTime());
			}
			sorted.addAll(ValueSortMap.sortMapByValue(tmp3, true).keySet());
			return sorted;
		case ONLINE_DSC:
			final Map<Player, Long> tmp4 = new FastMap<>();
			for (final Player academy : academys)
			{
				tmp4.put(academy, academy.getOnlineTime());
			}
			sorted.addAll(ValueSortMap.sortMapByValue(tmp4, false).keySet());
			return sorted;
		case CLASS_ASC:
			final Map<Player, String> tmp5 = new FastMap<>();
			for (final Player academy : academys)
			{
				tmp5.put(academy, academy.getClassId().getName());
			}
			sorted.addAll(ValueSortMap.sortMapByValue(tmp5, true).keySet());
			return sorted;
		case CLASS_DSC:
			final Map<Player, String> tmp6 = new FastMap<>();
			for (final Player academy : academys)
			{
				tmp6.put(academy, academy.getClassId().getName());
			}
			sorted.addAll(ValueSortMap.sortMapByValue(tmp6, false).keySet());
			return sorted;
		}
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
	}
}
