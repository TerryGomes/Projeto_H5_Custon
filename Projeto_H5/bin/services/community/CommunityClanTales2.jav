package services.community;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import l2f.commons.dbutils.DbUtils;
import l2f.gameserver.Config;
import l2f.gameserver.data.ClanRequest;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.data.xml.holder.EventHolder;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.handler.bbs.CommunityBoardManager;
import l2f.gameserver.handler.bbs.ICommunityBoardHandler;
import l2f.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.actor.listener.CharListenerList;
import l2f.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.model.pledge.SubUnit;
import l2f.gameserver.model.pledge.UnitMember;
import l2f.gameserver.network.serverpackets.JoinPledge;
import l2f.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import l2f.gameserver.network.serverpackets.PledgeShowMemberListAdd;
import l2f.gameserver.network.serverpackets.PledgeSkillList;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.ShowBoard;
import l2f.gameserver.network.serverpackets.SkillList;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.tables.ClanTable;
import l2f.gameserver.taskmanager.AutoImageSenderManager;
import l2f.gameserver.utils.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommunityClan extends Functions implements ScriptFile, ICommunityBoardHandler
{
	private final Listener _listener = new Listener();
	public static final Logger _log = LoggerFactory.getLogger(CommunityClan.class);

	@Override
	public String[] getBypassCommands()
	{
		return new String[] {
				"_bbsclan",
				"_clbbsclan_",
				"_clbbslist_",
				"_clsearch",
				"_clbbsadmi",
				"_mailwritepledgeform",
				"_announcepledgewriteform",
				"_announcepledgeswitchshowflag",
				"_announcepledgewrite",
				"_clwriteintro",
				"_clwritemail" };
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		if (!Config.COMMUNITYBOARD_CLAN_ENABLED)
		{
			String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "off.htm", player);
			ShowBoard.separateAndSend(html, player);
			return;
		}

		//Checking if all required images were sent to the player, if not - not allowing to pass
		if (!AutoImageSenderManager.wereAllImagesSent(player))
		{
			player.sendPacket(new Say2(player.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "CB", "Community wasn't loaded yet, try again in few seconds."));
			return;
		}

		bypass = bypass.replace(" ", ":");

		String content = "";
		if (bypass.equals("_bbsclan:invitelist"))
			content = inviteList(player);
		else if (bypass.startsWith("_bbsclan:invite:sub"))
		{
			String[] data = bypass.split(":");
			int clanId = player.getClanId();
			int obj = Integer.parseInt(data[3]);
			int unity = Integer.parseInt(data[4]);
			if (ClanData.getInstance().checkClanInvite(player, clanId, obj, unity))
				doInvite(clanId, obj, unity);

			onBypassCommand(player, "_bbsclan:clan:id:" + player.getClanId());
			return;
		}
		else if (bypass.startsWith("_bbsclan:invite:page"))
		{
			String[] data = bypass.split(":");
			content = invitePlayer(player, data[3]);
		}
		else if (bypass.startsWith("_bbsclan:invite:remove"))
		{
			String[] data = bypass.split(":");
			ClanData.getInstance().inviteRemove(player, data[3]);
			if (player.getClan().getInviteList().size() > 0)
				onBypassCommand(player, "_bbsclan:invitelist");
			else
				onBypassCommand(player, "_bbsclan:clan:id:" + player.getClanId());
			return;
		}
		else if (bypass.equals("_bbsclan") || bypass.startsWith("_clbbsclan_") || bypass.startsWith("_clbbslist_"))
			content = buildClanList("1", player);
		else if (bypass.startsWith("_bbsclan:list:clan"))
		{
			String[] data = bypass.split(":");
			content = buildClanList(data[3], player);
		}
		else if (bypass.startsWith("_bbsclan:clan:id"))
			content = buildClanPage(bypass.split(":")[3], player);
		else if (bypass.startsWith("_bbsclan:invite"))
		{
			String[] data = bypass.split(":");
			String clanId = data[2];
			String note = Util.ArrayToString(data, 3);
			ClanData.getInstance().sendInviteTask(player, clanId, note, true);
			onBypassCommand(player, "_bbsclan:clan:id:" + clanId);
			return;
		}
		else if (bypass.startsWith("_bbsclan:warclan"))
		{
			String[] data = bypass.split(":");
			int war = Integer.parseInt(data[2]);
			ClanData.getInstance().checkAndStartWar(player, war);
			onBypassCommand(player, "_bbsclan:clan:id:" + war);
			return;
		}
		else if (bypass.startsWith("_bbsclan:unwarclan"))
		{
			String[] data = bypass.split(":");
			int war = Integer.parseInt(data[2]);
			ClanData.getInstance().checkAndStopWar(player, war);
			onBypassCommand(player, "_bbsclan:clan:id:" + war);
			return;
		}
		else if (bypass.startsWith("_bbsclan:removeinvite"))
		{
			String clanId = bypass.split(":")[2];
			ClanData.getInstance().sendInviteTask(player, clanId, null, false);
			onBypassCommand(player, "_bbsclan:clan:id:" + clanId);
			return;
		}

		ShowBoard.separateAndSend(content, player);
	}

	private String invitePlayer(Player player, String string)
	{
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan/invite-player-info.htm", player);

		Clan clan = player.getClan();
		if (clan == null)
			return null;

		int obj = Integer.parseInt(string);

		ClanRequest request = ClanRequest.getClanInvitePlayer(clan.getClanId(), obj);
		if (request == null)
			return null;

		Player invited = request.getPlayer();

		if (!invited.isOnline())
		{
			Player restore = ClanData.getInstance().restore(obj);
			if (restore != null)
				invited = restore;
		}

		html = html.replace("<?name?>", invited.getName());
		html = html.replace("<?online?>", String.valueOf(invited.isOnline() ? "<font color=\"18FF00\">Yes</font>" : "<font color=\"FF0000\">No</font>"));
		html = html.replace("<?noblesse?>", String.valueOf(invited.isNoble() ? "<font color=\"18FF00\">Yes</font>" : "<font color=\"FF0000\">No</font>"));
		html = html.replace("<?hero?>", String.valueOf(invited.isHero() ? "<font color=\"18FF00\">Yes</font>" : "<font color=\"FF0000\">No</font>"));
		html = html.replace("<?level?>", String.valueOf(invited.getLevel()));
		html = html.replace("<?time?>", Util.time(request.getTime()));
		html = html.replace("<?class?>", Util.getFullClassName(player.getBaseClassId()));
		html = html.replace("<?remove?>", "bypass _bbsclan:invite:remove:" + invited.getObjectId());
		html = html.replace("<?note?>", request.getNote());
		if (!invited.isOnline())
			html = html.replace("<?button?>", HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan/invite-player-info-button-off.htm", player));
		else
		{
			String button = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan/invite-player-info-button.htm", player);
			String block = "";
			String list = "";

			int i = 1;
			final int[] unity = new int[] { 0, 100, 200, 1001, 1002, 2001, 2002 };
			for (int id : unity)
			{
				SubUnit sub = clan.getSubUnit(id);
				if (sub != null && sub.size() < clan.getSubPledgeLimit(id))
				{
					block = button;
					block = block.replace("<?color?>", i % 2 == 1 ? "99CC33" : "669933");
					block = block.replace("<?action?>", "bypass _bbsclan:invite:sub:" + invited.getObjectId() + ":" + id);
					block = block.replace("<?unity?>", takeFullSubName(id, sub.getName()));
					list += block;
					i++;
				}
			}

			if (list.isEmpty())
				list = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan/invite-player-info-button-limit.htm", player);

			html = html.replace("<?button?>", list);
		}

		return html;
	}

	public static final int SUBUNIT_MAIN_CLAN = 0;
	public static final int SUBUNIT_ROYAL1 = 100;
	public static final int SUBUNIT_ROYAL2 = 200;
	public static final int SUBUNIT_KNIGHT1 = 1001;
	public static final int SUBUNIT_KNIGHT2 = 1002;
	public static final int SUBUNIT_KNIGHT3 = 2001;
	public static final int SUBUNIT_KNIGHT4 = 2002;

	private String takeFullSubName(int id, String name)
	{
		String type = null;
		switch(id)
		{
			case 0:
				type = "Main Clan";
				break;
			case 100:
				type = "1st Royal Guard";
				break;
			case 200:
				type = "2nd Royal Guard";
				break;
			case 1001:
				type = "1st Order of Knights";
				break;
			case 1002:
				type = "2nd Order of Knights";
				break;
			case 2001:
				type = "3rd Order of Knights";
				break;
			case 2002:
				type = "4th Order of Knights";
				break;
		}

		type += ": " + name;

		return type;
	}

	private String inviteList(Player player)
	{
		Clan clan = player.getClan();

		if (clan == null)
			return null;

		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan/invite-list.htm", player);
		String template = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan/invite-list-template.htm", player);
		String block = "";
		String list = "";

		int i = 1;
		for (ClanRequest request : clan.getInviteList())
		{
			Player invited = request.getPlayer();

			if (!invited.isOnline())
			{
				Player restore = ClanData.getInstance().restore(invited.getObjectId());
				if (restore != null)
					invited = restore;
			}

			long time = request.getTime();

			block = template;
			block = block.replace("<?color?>", i % 2 == 1 ? "333333" : "A7A19A");
			block = block.replace("<?name?>", invited.getName());
			block = block.replace("<?online?>", String.valueOf(invited.isOnline() ? "<font color=\"18FF00\">Yes</font>" : "<font color=\"FF0000\">No</font>"));
			block = block.replace("<?noblesse?>", String.valueOf(invited.isNoble() ? "<font color=\"18FF00\">Yes</font>" : "<font color=\"FF0000\">No</font>"));
			block = block.replace("<?level?>", String.valueOf(invited.getLevel()));
			block = block.replace("<?time?>", Util.time(time));
			block = block.replace("<?class?>", Util.getFullClassName(player.getBaseClassId()));
			block = block.replace("<?action?>", "bypass _bbsclan:invite:page:" + invited.getObjectId());
			block = block.replace("<?remove?>", "bypass _bbsclan:invite:remove:" + invited.getObjectId());
			list += block;
			i++;
		}
		html = html.replace("<?list?>", list);
		html = html.replace("<?action?>", "bypass _bbsclan:clan:id:" + player.getClanId());
		return html;
	}

	private String buildClanList(String page_num, Player player)
	{
		if (page_num == null)
			return null;

		int page = Integer.parseInt(page_num);
		String content = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan/clan-list.htm", player);
		int start = (page - 1) * 14;
		int end = Math.min(page * 14, ClanRequest.getClanList().size());

		String template = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan/clan-list-template.htm", player);
		String body = "";
		for (int i = start; i < end; i++)
		{
			Clan clan = ClanRequest.getClanList().get(i);
			String clantpl = template;
			clantpl = clantpl.replace("<?action?>", "bypass _bbsclan:clan:id:" + clan.getClanId());
			clantpl = clantpl.replace("<?color?>", i % 2 == 1 ? "151515" : "212121");
			clantpl = clantpl.replace("<?level_color?>", clan.getLevel() == 11 ? "FFCC33" : (clan.getLevel() <= 10 && clan.getLevel() >= 8 ? "66CCCC" : "339933"));
			clantpl = clantpl.replace("<?position?>", String.valueOf(i + 1));
			clantpl = clantpl.replace("<?clan_crest?>", clan.hasCrest() ? "Crest.crest_" + Config.REQUEST_ID + "_" + clan.getCrestId() : "L2UI_CH3.ssq_bar1back");
			clantpl = clantpl.replace("<?ally_crest?>", clan.getAlliance() != null && clan.getAlliance().getAllyCrestId() > 0 ? "Crest.crest_" + Config.REQUEST_ID + "_" + clan.getAlliance().getAllyCrestId() : "L2UI_CH3.ssq_bar2back");
			clantpl = clantpl.replace("<?clan_name?>", clan.getName());
			clantpl = clantpl.replace("<?clan_owner?>", clan.getLeaderName());
			clantpl = clantpl.replace("<?clan_level?>", String.valueOf(clan.getLevel()));
			clantpl = clantpl.replace("<?member_count?>", String.valueOf(clan.getAllSize()));
			body += clantpl;
		}

		Clan my_clan = player.getClan();
		if (my_clan != null && my_clan.getLevel() > 0)
		{
			String clantpl = template;
			clantpl = clantpl.replace("<?action?>", "bypass _bbsclan:clan:id:" + my_clan.getClanId());
			clantpl = clantpl.replace("<?color?>", "669933");
			clantpl = clantpl.replace("<?level_color?>", my_clan.getLevel() == 11 ? "FFCC33" : (my_clan.getLevel() <= 10 && my_clan.getLevel() >= 8 ? "66CCCC" : "339933"));
			clantpl = clantpl.replace("<?position?>", "MY");
			clantpl = clantpl.replace("<?clan_crest?>", my_clan.hasCrest() ? "Crest.crest_" + Config.REQUEST_ID + "_" + my_clan.getCrestId() : "L2UI_CH3.ssq_bar1back");
			clantpl = clantpl.replace("<?ally_crest?>", my_clan.getAlliance() != null && my_clan.getAlliance().getAllyCrestId() > 0 ? "Crest.crest_" + Config.REQUEST_ID + "_" + my_clan.getAlliance().getAllyCrestId() : "L2UI_CH3.ssq_bar2back");
			clantpl = clantpl.replace("<?clan_name?>", my_clan.getName());
			clantpl = clantpl.replace("<?clan_owner?>", my_clan.getLeaderName());
			clantpl = clantpl.replace("<?clan_level?>", String.valueOf(my_clan.getLevel()));
			clantpl = clantpl.replace("<?member_count?>", String.valueOf(my_clan.getAllSize()));
			body += clantpl;
		}

		content = content.replace("<?navigate?>", parseNavigate(page));
		content = content.replace("<?body?>", body);

		return content;
	}

	private String parseNavigate(int page)
	{
		StringBuilder pg = new StringBuilder();
		
		int size = ClanRequest.getClanList().size();
		int inPage = 14;
		
		pg.append("<center><table width=25 border=0><tr>");
		if (page > 1)
		{
			pg.append("<td width=20 align=center><button value=\"\" action=\"bypass _bbsclan:list:clan:").append(page - 1).append("\" width=16 height=16 back=\"L2UI_ct1.Button_DF_Left_down\" fore=\"L2UI_ct1.Button_DF_Left\"></td>");
		}
		if (size > inPage)
		{
			pg.append("<td align=center width=250>").append(page).append("</td>");
		}
		if (size > page * inPage)
		{
			pg.append("<td width=50><button value=\"\" action=\"bypass _bbsclan:list:clan:").append(page + 1).append("\" width=16 height=16 back=\"L2UI_ct1.Button_DF_Right_down\" fore=\"L2UI_ct1.Button_DF_Right\"></td>");
		}
		pg.append("</tr></table></center>");
		
		return pg.toString();
	}

	private String buildClanPage(String string, Player player)
	{
		if (string == null)
			return null;

		int clanId = Integer.parseInt(string);

		Clan clan = ClanTable.getInstance().getClan(clanId);

		if (clan == null)
			return null;

		String content = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan/clan-page.htm", player);
		content = content.replace("<?clan_name?>", clan.getName());
		content = content.replace("<?clan_ally?>", clan.getAlliance() != null ? clan.getAlliance().getAllyName() : "No");
		content = content.replace("<?clan_crest?>", clan.hasCrest() ? "Crest.crest_" + Config.REQUEST_ID + "_" + clan.getCrestId() : "L2UI_CH3.ssq_bar1back");
		content = content.replace("<?ally_crest?>", clan.getAlliance() != null && clan.getAlliance().getAllyCrestId() > 0 ? "Crest.crest_" + Config.REQUEST_ID + "_" + clan.getAlliance().getAllyCrestId() : "L2UI_CH3.ssq_bar2back");
		content = content.replace("<?clan_owner?>", clan.getLeaderName());
		content = content.replace("<?clan_level?>", String.valueOf(clan.getLevel()));
		content = content.replace("<?clan_base?>", String.valueOf(clan.getCastle() != 0 ? ClanData.getInstance().name(clan.getCastle()) : clan.getHasFortress() != 0 ? ClanData.getInstance().name(clan.getHasFortress()) : "No"));
		content = content.replace("<?clan_hall?>", clan.getHasHideout() != 0 ? ClanData.getInstance().name(clan.getHasHideout()) : "No");
		content = content.replace("<?clan_members?>", String.valueOf(clan.getAllSize()));
		content = content.replace("<?clan_point?>", Util.formatAdena(clan.getReputationScore()));
		content = content.replace("<?clan_id?>", String.valueOf(clan.getClanId()));
		content = content.replace("<?clan_avarage_level?>", Util.formatAdena(clan.getAverageLevel()));
		content = content.replace("<?clan_online?>", Util.formatAdena(clan.getOnlineMembers(0).size()));

		Clan myclan = player.getClan();
		if (myclan == null)
		{
			String page = null;
			if (clan.checkInviteList(player.getObjectId()))
			{
				page = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan/clan-page-remove_invite.htm", player);
				page = page.replace("<?bypass?>", "bypass _bbsclan:removeinvite:" + clan.getClanId());
			}
			else
			{
				page = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan/clan-page-invite.htm", player);
				page = page.replace("<?bypass?>", "bypass -h scripts_services.ClanPanel:addRequest " + clan.getClanId());
			}

			content = content.replace("<?container?>", page);
		}
		else if (myclan == clan && clan.getLeader().getPlayer() == player && clan.getInviteList().size() > 0)
		{
			String page = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan/clan-page-inviteinfo.htm", player);
			page = page.replace("<?bypass?>", "bypass _bbsclan:invitelist");
			page = page.replace("<?invite_count?>", String.valueOf(clan.getInviteList().size()));
			content = content.replace("<?container?>", page);
		}
		else
			content = content.replace("<?container?>", "");

		if (ClanData.getInstance().checkClanWar(clan, myclan, player, false))
		{
			if (myclan.isAtWarWith(clan.getClanId()))
			{
				String page = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan/clan-page-unwar.htm", player);
				page = page.replace("<?bypass?>", "bypass _bbsclan:unwarclan:" + clan.getClanId());
				content = content.replace("<?war?>", page);
			}
			else
			{
				String page = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan/clan-page-war.htm", player);
				page = page.replace("<?bypass?>", "bypass _bbsclan:warclan:" + clan.getClanId());
				content = content.replace("<?war?>", page);
			}
		}
		else
			content = content.replace("<?war?>", "");

		if (ClanData.getInstance().haveWars(clan))
		{
			String war_body = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan/clan-page-war-list.htm", player);
			String war_temp = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan/clan-page-war-list-template.htm", player);
			String block = "";
			String list = "";
			int num = 0;
			for (Clan war : clan.getEnemyClans())
			{
				num++;
				if (num <= 6 && war.isAtWarWith(clan.getClanId()) && clan.isAtWarWith(war.getClanId()))
				{
					block = war_temp;
					block = block.replace("<?color?>", num % 2 == 1 ? "333333" : "A7A19A");
					block = block.replace("<?clan_name?>", war.getName());
					block = block.replace("<?clan_leader?>", war.getLeaderName());
					block = block.replace("<?clan_level?>", String.valueOf(war.getLevel()));
					block = block.replace("<?clan_member?>", String.valueOf(war.getAllSize()));
					block = block.replace("<?war_link?>", "bypass _bbsclan:clan:id:" + war.getClanId());
					list += block;
				}
				else
					num--;
			}

			war_body = war_body.replace("<?war_list?>", list);
			war_body = war_body.replace("<?war_count?>", String.valueOf(num));
			content = content.replace("<?clan_warlist?>", war_body);
		}
		else
			content = content.replace("<?clan_warlist?>", HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan/clan-page-war-list-empty.htm", player));

		if (clan.getSkills().size() > 0)
		{
			String skill_body = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan/clan-page-skills.htm", player);
			String skill_temp = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan/clan-page-skills-template.htm", player);
			String block = "";
			String list = "";
			int count = 1;
			for (Skill skill : clan.getSkills())
			{
				block = skill_temp;
				block = block.replace("<?skill_level?>", String.valueOf(skill.getLevel()));
				block = block.replace("<?skill_icon?>", skill.getIcon());
				list += block;

				if (count % 9 == 0)
					list += "</tr><tr>";

				count++;
			}

			final int[] unity = new int[] { 0, 100, 200, 1001, 1002, 2001, 2002 };
			ArrayList<Skill> unity_list = new ArrayList<Skill>();
			for (int id : unity)
			{
				SubUnit sub = clan.getSubUnit(id);
				if (sub != null)
				{
					for (Skill skill : sub.getSkills())
					{
						unity_list.add(skill);
					}
				}
			}

			for (Skill skill : unity_list)
			{
				block = skill_temp;
				block = block.replace("<?skill_level?>", String.valueOf(skill.getLevel()));
				block = block.replace("<?skill_icon?>", skill.getIcon());
				list += block;

				if (count % 9 == 0)
					list += "</tr><tr>";

				count++;
			}

			skill_body = skill_body.replace("<?skill_list?>", list);
			content = content.replace("<?clan_skill?>", skill_body);
		}
		else
			content = content.replace("<?clan_skill?>", HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan/clan-page-skills-empty.htm", player));

		String description = clan.getDescription();
		final boolean isnull = description == null || description.isEmpty();
		content = content.replace("<?clan_notice?>", isnull ? "<center><font color=\"FF0000\">Empty</font></center>" : description);
		content = content.replace("<?edit_notice?>", clan.getLeader().getPlayer() == player ? ("<button " + (isnull ? "action=\"bypass -h scripts_services.ClanPanel:addDescription\" value=\"Add\"" : "action=\"bypass -h scripts_services.ClanPanel:editDescription\" value=\"Edit\"") + " width=120 height=24 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"/>") : "");

		return content;
	}

	@Override
	public void onLoad()
	{
		CommunityBoardManager.getInstance().registerHandler(this);
		if (Config.COMMUNITYBOARD_ENABLED)
		{
			CharListenerList.addGlobal(_listener);
			ClanRequest.updateList();
			_log.info("CommunityBoard: Clan service loaded.");
		}
	}

	private class Listener implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			Clan clan = player.getClan();
			if (clan == null || clan.getLevel() < 2)
				return;

			if (clan.getDescription() == null)
			{
				Connection con = null;
				PreparedStatement statement = null;
				ResultSet rset = null;
				String description = null;
				try
				{
					con = DatabaseFactory.getInstance().getConnection();
					statement = con.prepareStatement("SELECT description FROM `clan_description` WHERE `clan_id` = ?");
					statement.setInt(1, clan.getClanId());
					rset = statement.executeQuery();
					if (rset.next())
						description = rset.getString("description");
				}
				catch (Exception e)
				{
					_log.error("CommunityBoard -> Clan.onPlayerEnter(): " + e);
				}
				finally
				{
					DbUtils.closeQuietly(con, statement, rset);
				}

				if (description != null)
				{
					description = description.replace("\n", "<br1>\n");
					clan.setDescription(description);
				}
			}
		}
	}

	private void doInvite(int clanId, int obj, int unity)
	{
		Player player = GameObjectsStorage.getPlayer(obj);

		if (player == null || !player.isOnline())
			return;

		Clan clan = ClanTable.getInstance().getClan(clanId);

		player.sendPacket(new JoinPledge(clan.getClanId()));
		SubUnit subUnit = clan.getSubUnit(unity);
		if (subUnit == null)
			return;

		UnitMember member = new UnitMember(clan, player.getName(), player.getTitle(), player.getLevel(), player.getClassId().getId(), player.getObjectId(), unity, player.getPowerGrade(), player.getApprentice(), player.getSex(), Clan.SUBUNIT_NONE);
		subUnit.addUnitMember(member);

		player.setPledgeType(unity);
		player.setClan(clan);

		member.setPlayerInstance(player, false);

		member.setPowerGrade(clan.getAffiliationRank(player.getPledgeType()));

		clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListAdd(member), player);
		clan.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.S1_HAS_JOINED_THE_CLAN).addString(player.getName()), new PledgeShowInfoUpdate(clan));

		player.sendPacket(SystemMsg.ENTERED_THE_CLAN);
		player.sendPacket(player.getClan().listAll());
		player.updatePledgeClass();

		clan.addSkillsQuietly(player);
		player.sendPacket(new PledgeSkillList(clan));
		player.sendPacket(new SkillList(player));

		EventHolder.getInstance().findEvent(player);
		if (clan.getWarDominion() > 0)
		{
			DominionSiegeEvent siegeEvent = player.getEvent(DominionSiegeEvent.class);

			siegeEvent.updatePlayer(player, true);
		}
		else
			player.broadcastCharInfo();

		player.store(false);

		ClanRequest.removeClanInvitePlayer(clan.getClanId(), player.getObjectId());

	}

	@Override
	public void onReload()
	{
		
	}

	@Override
	public void onShutdown()
	{
		
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		
	}
}
