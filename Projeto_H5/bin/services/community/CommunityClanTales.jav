package services.community;

import gnu.trove.map.hash.TIntObjectHashMap;
import javolution.util.FastMap;
import l2f.commons.dbutils.DbUtils;
import l2f.gameserver.Config;
import l2f.gameserver.cache.Msg;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.data.xml.holder.ResidenceHolder;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.handler.bbs.CommunityBoardManager;
import l2f.gameserver.handler.bbs.ICommunityBoardHandler;
import l2f.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.SubClass;
import l2f.gameserver.model.actor.listener.CharListenerList;
import l2f.gameserver.model.base.ClassId;
import l2f.gameserver.model.entity.SevenSigns;
import l2f.gameserver.model.entity.residence.Residence;
import l2f.gameserver.model.items.Inventory;
import l2f.gameserver.model.items.PcInventory;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.model.pledge.Clan.SinglePetition;
import l2f.gameserver.model.pledge.SubUnit;
import l2f.gameserver.model.pledge.UnitMember;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.network.serverpackets.ShowBoard;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.tables.ClanTable;
import l2f.gameserver.templates.item.ItemTemplate;
import l2f.gameserver.utils.BbsUtil;
import l2f.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class CommunityClan extends Functions implements ScriptFile, ICommunityBoardHandler
{
	private static final Logger _log = LoggerFactory.getLogger(CommunityClan.class);
	private static final int CLANS_PER_PAGE = 7;
	private static final int MEMBERS_PER_PAGE = 15;
	private static final String[] ALL_CLASSES = {"Duelist","Dreadnought","PhoenixKnight","HellKnight","Adventurer","Saggitarius","Archmage","SoulTaker","ArcanaLord","Cardinal","Hierophant","EvaTemplar","SwordMuse","WindRider","MoonlightSentine","MysticMuse","ElementalMaster","EvaSaint","ShillienTemplar","SpectralDancer","GhostHunter","GhostSentinel","StormScreamer","SpectralMaster","ShillienSaint","Titan","GrandKhauatari","Dominator","Doomcryer","FortuneSeeker","Maestro"};
	private static final int[] SLOTS = {Inventory.PAPERDOLL_RHAND, Inventory.PAPERDOLL_LHAND, Inventory.PAPERDOLL_HEAD, Inventory.PAPERDOLL_CHEST, Inventory.PAPERDOLL_LEGS,
		Inventory.PAPERDOLL_GLOVES, Inventory.PAPERDOLL_FEET, Inventory.PAPERDOLL_BACK, Inventory.PAPERDOLL_UNDER, Inventory.PAPERDOLL_BELT,
		Inventory.PAPERDOLL_LFINGER, Inventory.PAPERDOLL_RFINGER, Inventory.PAPERDOLL_LEAR, Inventory.PAPERDOLL_REAR, Inventory.PAPERDOLL_NECK, Inventory.PAPERDOLL_LBRACELET};
	private static final String[] NAMES = {"Weapon", "Shield", "Helmet", "Chest", "Legs", "Gloves", "Boots", "Cloak", "Shirt", "Belt", "Ring"," Ring", "Earring", "Earring", "Necklace", "Bracelet"};

	private static TIntObjectHashMap<String[]> _clanSkillDecriptions = new TIntObjectHashMap<String[]>();
	private final Listener _listener = new Listener();

	@Override
	public void onLoad()
	{
		CharListenerList.addGlobal(_listener);
		if (Config.COMMUNITYBOARD_ENABLED)
		{
			_log.info("CommunityBoard: Clan Community service loaded.");
			CommunityBoardManager.getInstance().registerHandler(this);
		}
	}

	@Override
	public void onReload()
	{
		if (Config.COMMUNITYBOARD_ENABLED)
			CommunityBoardManager.getInstance().removeHandler(this);
	}

	@Override
	public void onShutdown()
	{}

	@Override
	public String[] getBypassCommands()
	{
		return new String[] {
				"_bbsclan",
				"_clbbsclan_",
				"_clbbslist_",
				"_clbbsmanage",
				"_bbsclanjoin",
				"_clbbspetitions",
				"_clbbsplayerpetition",
				"_clbbsplayerinventory",
				"_bbsclanmembers",
				"_clbbssinglemember",
				"_clbbsskills",
				"_mailwritepledgeform",
				"_announcepledgewriteform",
				"_announcepledgeswitchshowflag",
				"_announcepledgewrite", };
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		player.setSessionVar("add_fav", null);
		String html = null;
		
		if ("bbsclan".equals(cmd))
		{
			onBypassCommand(player, "_clbbslist_0");
			return;
		}
		else if ("clbbslist".equals(cmd))
		{
			int page = Integer.parseInt(st.nextToken());
			html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_clanlist.htm", player);
			html = getAllClansRank(player, html, page);
			html = html.replace("%myClan%", (player.getClan() != null ? "_clbbsclan_"+player.getClanId() : "_clbbslist_0"));
		}
		else if ("clbbsclan".equals(cmd))
		{
			int clanId = Integer.parseInt(st.nextToken());
			if (clanId == 0)
			{
				player.sendPacket(new SystemMessage(SystemMessage.NOT_JOINED_IN_ANY_CLAN));
				onBypassCommand(player, "_clbbslist_0");
				return;
			}

			Clan clan = ClanTable.getInstance().getClan(clanId);
			if (clan == null)
			{
				onBypassCommand(player, "_clbbslist_0");
				return;
			}

			html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_clan.htm", player);
			html = getMainClanPage(player, clan, html);
		}
		else if ("clbbsmanage".equals(cmd))//_clbbsmanage_btn
		{
			String actionToken = st.nextToken();
			int action = Integer.parseInt(actionToken.substring(0, 1));
			
			if (action != 0)
			{
				boolean shouldReturn = manageRecrutationWindow(player, action, actionToken);
				if (shouldReturn)
					return;
			}
			html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_clanrecruit.htm", player);
			html = getClanRecruitmentManagePage(player, html);
		}
		else if ("bbsclanjoin".equals(cmd))
		{
			int clanId = Integer.parseInt(st.nextToken());

			Clan clan = ClanTable.getInstance().getClan(clanId);
			if (clan == null)
			{
				sendErrorMessage(player, "Such Clan cannot be found!", "_clbbslist_0");
				return;
			}
			String next = st.nextToken();
			if (Integer.parseInt(next.substring(0, 1)) == 1)
			{
				try
				{
					if (!manageClanJoinWindow(player, clan, next.substring(2)))
					{
						sendInfoMessage(player, "You have already sent petition to this clan!", "_clbbsclan_"+clan.getClanId(), true);
						return;
					}
				}
				catch (Exception e)
				{
					sendErrorMessage(player, "Petition you have sent have tried to send is incorrect!", "_bbsclanjoin_"+clan.getClanId()+"_0");
					return;
				}
				sendInfoMessage(player, "Your petition has been submited!", "_clbbsclan_"+clan.getClanId(), false);
				return;
			}
			html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_clanjoin.htm", player);
			html = getClanJoinPage(player, clan, html);
		}
		else if ("clbbspetitions".equals(cmd))
		{
			int clanId = Integer.parseInt(st.nextToken());

			Clan clan = ClanTable.getInstance().getClan(clanId);
			if (clan == null)
			{
				sendErrorMessage(player, "Such Clan cannot be found!", "_clbbslist_0");
				return;
			}
			
			html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_clanpetitions.htm", player);
			html = getClanPetitionsPage(player, clan, html);
		}
		else if ("clbbsplayerpetition".equals(cmd))
		{
			int senderId = Integer.parseInt(st.nextToken());
			if (st.hasMoreTokens())
			{
				int action = Integer.parseInt(st.nextToken());
				managePlayerPetition(player, senderId, action);
				return;
			}
			html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_clanplayerpetition.htm", player);
			
			Player sender = GameObjectsStorage.getPlayer(senderId);
			if (sender != null)
				html = getClanSinglePetitionPage(player, sender, html);
			else
				html = getClanSinglePetitionPage(player, senderId, html);
		}
		else if ("clbbsplayerinventory".equals(cmd))
		{
			int senderId = Integer.parseInt(st.nextToken());
			Player sender = GameObjectsStorage.getPlayer(senderId);
			
			html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_clanplayerinventory.htm", player);

			if (sender != null)
				html = getPlayerInventoryPage(sender, html);
			else
				html = getPlayerInventoryPage(senderId, html);
		}
		else if ("bbsclanmembers".equals(cmd))
		{
			int clanId = Integer.parseInt(st.nextToken());
			if (clanId == 0)
			{
				sendErrorMessage(player, "Such Clan cannot be found!", "_clbbslist_0");
				return;
			}
			int page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 0;

			Clan clan = ClanTable.getInstance().getClan(clanId);
			if (clan == null)
			{
				sendErrorMessage(player, "Such Clan cannot be found!", "_clbbslist_0");
				return;
			}
			
			html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_clanmembers.htm", player);
			html = getClanMembersPage(player, clan, html, page);
		}
		else if ("clbbssinglemember".equals(cmd))
		{
			int playerId = Integer.parseInt(st.nextToken());
			
			html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_clansinglemember.htm", player);

			Player member = GameObjectsStorage.getPlayer(playerId);
			if (member != null)
				html = getClanSingleMemberPage(member, html);
			else
				html = getClanSingleMemberPage(playerId, html);
		}
		else if ("clbbsskills".equals(cmd))
		{
			int clanId = Integer.parseInt(st.nextToken());
			if (clanId == 0)
			{
				sendErrorMessage(player, "Such Clan cannot be found!", "_clbbslist_0");
				return;
			}

			Clan clan = ClanTable.getInstance().getClan(clanId);
			if (clan == null)
			{
				sendErrorMessage(player, "Such Clan cannot be found!", "_clbbslist_0");
				return;
			}
			
			
			html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_clanskills.htm", player);
			html = getClanSkills(clan, html);
		}
		else if ("mailwritepledgeform".equals(cmd))
		{
			Clan clan = player.getClan();
			if (clan == null || clan.getLevel() < 2 || !player.isClanLeader())
			{
				onBypassCommand(player, "_clbbsclan_" + player.getClanId());
				return;
			}

			html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_pledge_mail_write.htm", player);

			html = html.replace("%PLEDGE_ID%", String.valueOf(clan.getClanId()));
			html = html.replace("%pledge_id%", String.valueOf(clan.getClanId()));
			html = html.replace("%pledge_name%", clan.getName());

			html = BbsUtil.htmlBuff(html, player);
		}
		else if ("announcepledgewriteform".equals(cmd))
		{
			Clan clan = player.getClan();
			if (clan == null || clan.getLevel() < 2 || !player.isClanLeader())
			{
				onBypassCommand(player, "_clbbsclan_" + player.getClanId());
				return;
			}

			HashMap<Integer, String> tpls = Util.parseTemplate(HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR +
					"bbs_clanannounce.htm", player));
			html = tpls.get(0);

			String notice = "";
			int type = 0;
			
			Connection con = null;
			PreparedStatement statement = null;
			ResultSet rset = null;
			
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("SELECT * FROM `bbs_clannotice` WHERE `clan_id` = ? and type != 2");
				statement.setInt(1, clan.getClanId());
				rset = statement.executeQuery();
				
				if (rset.next())
				{
					notice = rset.getString("notice");
					type = rset.getInt("type");
				}
			}
			catch (Exception e)
			{
				_log.error("While selecting bbs_clannotice:", e);
			}
			finally
			{
				DbUtils.closeQuietly(con, statement, rset);
			}
			
			if (type == 0)
			{
				html = html.replace("%off%", "off");
				html = html.replace("%on%", "<a action=\"bypass _announcepledgeswitchshowflag_1\">on</a>");
			}
			else
			{
				html = html.replace("%off%", "<a action=\"bypass _announcepledgeswitchshowflag_0\">off</a>");
				html = html.replace("%on%", "on");
				
			}
			html = html.replace("%flag%", String.valueOf(type));

			List<String> args = new ArrayList<String>();
			args.add("0");
			args.add("0");
			args.add("0");
			args.add("0");
			args.add("0");
			args.add("0"); // account data ?
			args.add("");
			args.add("0"); // account data ?
			args.add("");
			args.add("0"); // account data ?
			args.add("");
			args.add("");
			args.add(notice);
			args.add("");
			args.add("");
			args.add("0");
			args.add("0");
			args.add("");

			player.sendPacket(new ShowBoard(html, "1001", player));
			player.sendPacket(new ShowBoard(args));
			return;
		}
		else if ("announcepledgeswitchshowflag".equals(cmd))
		{
			Clan clan = player.getClan();
			if (clan == null || clan.getLevel() < 2 || !player.isClanLeader())
			{
				onBypassCommand(player, "_clbbsclan_" + player.getClanId());
				return;
			}

			int type = Integer.parseInt(st.nextToken());

			Connection con = null;
			PreparedStatement statement = null;
			
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("UPDATE `bbs_clannotice` SET type = ? WHERE `clan_id` = ? and type = ?");
				statement.setInt(1, type);
				statement.setInt(2, clan.getClanId());
				statement.setInt(3, type == 1 ? 0 : 1);
				statement.execute();
			}
			catch (Exception e)
			{
				_log.error("While updating bbs_clannotice:", e);
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}

			clan.setNotice(type == 0 ? "" : null);
			onBypassCommand(player, "_announcepledgewriteform");
			return;
		}
		ShowBoard.separateAndSend(html, player);
	}

	private String getMainClanPage(Player player, Clan clan, String html)
	{
		html = html.replace("%clanName%", clan.getName());
		html = html.replace("%clanLeader%", clan.getLeaderName());
		html = html.replace("%allyName%", (clan.getAlliance() != null ? clan.getAlliance().getAllyName() : "No"));
		html = html.replace("%membersCount%", String.valueOf(clan.getAllMembers().size()));
		html = html.replace("%membersOnline%", String.valueOf(clan.getOnlineMembers().size()));
		html = html.replace("%clanLevel%", String.valueOf(getLevelIcon(clan.getLevel())));
		
		Residence clanHall = ResidenceHolder.getInstance().getResidence(clan.getHasHideout());
		html = html.replace("%clanHall%", (clanHall != null ? getResidenceName(clanHall) : "None"));
		Residence castle = ResidenceHolder.getInstance().getResidence(clan.getCastle());
		html = html.replace("%castle%", (castle != null ? castle.getName() : "None"));
		Residence fortress = ResidenceHolder.getInstance().getResidence(clan.getHasFortress());
		html = html.replace("%fortress%", (fortress != null ? getResidenceName(fortress) : "None"));
		
		int[] data = getMainClanPageData(clan);
		
		html = html.replace("%pvps%", String.valueOf(data[0]));
		html = html.replace("%pks%", String.valueOf(data[1]));
		
		for (int i = 0;i<3;i++)
		{
			Clan allyMember = clan.getAlliance() != null && clan.getAlliance().getMembersCount() > i ? clan.getAlliance().getMembers()[i] : null;
			
			html = html.replace("%allyMember"+i+"%", allyMember == null ? "<br>" : "<button value=\""+allyMember.getName()+"\" action=\"bypass _clbbsclan_"+allyMember.getClanId()+"\" width=200 height=32 back=\"L2UI_CT1.OlympiadWnd_DF_Reward_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Reward\">");
		}

		StringBuilder builder = new StringBuilder();
		
		builder.append("<table width=700 height=45>");
		int index = 0;
		for (Clan warClan : clan.getEnemyClans())
		{
			if (index % 3 == 0)
			{
				if (index > 0)
					builder.append("</tr>");
				builder.append("<tr>");
				index = 0;
			}
			builder.append("<td><center>");
			builder.append("<button action=\"bypass _clbbsclan_").append(warClan.getClanId()).append("\" value=\"").append(warClan.getName()).append("\" width=200 height=32 back=\"L2UI_CT1.OlympiadWnd_DF_Reward_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Reward\">");
			builder.append("</center></td>");
			index++;
		}
		if (builder.length() < 50)//No wars at all
			builder.append("<tr><td><br></td></tr>");
		else if (!builder.substring(builder.length()-5).equals("</tr>"))//If <tr> wasn't closed
			builder.append("</tr>");
		builder.append("</table>");
		
		html = html.replace("%wars%", builder.toString());
		
		boolean isLeader = player.getClan() != null && player.getClan().equals(clan) && player.getClan().getLeaderId() == player.getObjectId();
		
		html = html.replace("%manageNotice%", isLeader ? "<button action=\"bypass _announcepledgewriteform\" value=\"Manage Notice\" width=200 height=30 back=\"L2UI_CT1.OlympiadWnd_DF_Info_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Info\">" : "<br>");
		
		return html;
	}

	private String getLevelIcon(int level)
	{
		if (level == 0 || level == 1)
			return "Icon.skill4290";
		if (level >= 2 && level <= 9)
			return "Icon.skill"+(4301 + level);
		return "Icon.skill4408_x"+level;
	}

	private String getClanMembersPage(Player player, Clan clan, String html, int page)
	{
		html = html.replace("%clanName%", clan.getName());
		List<UnitMember> members = clan.getAllMembers();
		
		StringBuilder builder = new StringBuilder();
		int index = 0;
		int max = Math.min(MEMBERS_PER_PAGE+MEMBERS_PER_PAGE*page, members.size());
		for (int i = MEMBERS_PER_PAGE*page;i<max;i++)
		{
			UnitMember member = members.get(i);
			builder.append("<tr>");
			builder.append("<td width=50><font color=\"f1b45d\">").append(index + 1).append(".</font></td>");
			builder.append("<td width=150>");
			builder.append("<button action=\"bypass _clbbssinglemember_").append(member.getObjectId()).append("\" value=\"").append(member.getName()).append("\" width=150 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.button_df\">");
			builder.append("</td>");
			builder.append("<td width=100><center>").append(member.getPlayer() != null
					? "<font color=6a9b54>True</font>"
					: "<font color=9b5454>False</font>").append("</center></td>");


			builder.append("<td width=100><font color=\"bc7420\"><center>").append(member.isSubLeader() != 0 || member
					.isClanLeader()
					? "True"
					: "False").append("</center></font></td>");
			builder.append("<td width=75><font color=\"bc7420\"><center>").append(getUnitName(member.getSubUnit().getType())).append("</center></font></td>");
			builder.append("<td width=75></td>");
			builder.append("</tr>");
			index++;
		}
		
		html = html.replace("%members%", builder.toString());

		//Restarting Builder
		builder = new StringBuilder();

		builder.append("<table width=700><tr><td width=350>");
		if (page > 0)
			builder.append("<center><button action=\"bypass _bbsclanmembers_").append(clan.getClanId()).append("_")
					.append(page - 1).append("\" value=\"Previous\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.button_df\"></center>");
		builder.append("</td><td width=350>");
		if (members.size() > MEMBERS_PER_PAGE+MEMBERS_PER_PAGE*page)
			builder.append("<center><button action=\"bypass _bbsclanmembers_").append(clan.getClanId()+"_"+(page+1)).append("\" value=\"Next\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.button_df\"></center>");
		builder.append("</td></tr></table>");
		
		html = html.replace("%nextPages%", builder.toString());
		
		return html;
	}
	
	private String getClanSingleMemberPage(Player member, String html)
	{
		html = html.replace("%playerName%", member.getName());
		html = html.replace("%playerId%", String.valueOf(member.getObjectId()));
		html = html.replace("%clanName%", member.getClan() != null ? member.getClan().getName() : "");
		html = html.replace("%online%", "<font color=6a9b54>True</font>");
		html = html.replace("%title%", member.getTitle());
		html = html.replace("%pvpPoints%", String.valueOf(member.getPvpKills()));
		html = html.replace("%pkPoints%", String.valueOf(member.getPkKills()));
		html = html.replace("%rank%", "Level " + (member.getClan() != null ? member.getClan().getAnyMember(member.getObjectId()).getPowerGrade() : 0));
		html = html.replace("%onlineTime%", getConvertedTime(member.getOnlineTime()));
		html = html.replace("%leader%", member.getSubUnit() != null ? (member.getSubUnit().getLeaderObjectId() == member.getObjectId() ? "True" : "False") : "False");
		html = html.replace("%subpledge%", getUnitName(member.getSubUnit().getType()));
		html = html.replace("%nobless%", member.isNoble() ? "True" : "False");
		html = html.replace("%hero%", member.isHero() ? "True" : "False");
		html = html.replace("%adena%", getConvertedAdena(member.getAdena()));
		html = html.replace("%recs%", String.valueOf(member.getRecomHave()));
		html = html.replace("%sevenSigns%", SevenSigns.getCabalShortName(SevenSigns.getInstance().getPlayerCabal(member)));
		html = html.replace("%fame%", String.valueOf(member.getFame()));
		
		Collection<SubClass> classes = member.getSubClasses().values();
		int subIndex = 0;
		for (SubClass sub : classes)
		{
			String replacement = "";
			if (sub.isBase())
			{
				replacement = "mainClass";
			}
			else
			{
				if (subIndex == 0)
					replacement = "firstSub";
				else if (subIndex == 1)
					replacement = "secondSub";
				else
					replacement = "thirdSub";
				subIndex++;
			}
			
			html = html.replace("%"+replacement+"%", getFullClassName(ClassId.values()[sub.getClassId()])+"("+sub.getLevel()+")");
		}
		html = html.replace("%firstSub%", "");
		html = html.replace("%secondSub%", "");
		html = html.replace("%thirdSub%", "");
		
		html = html.replace("%clanId%", String.valueOf(member.getClanId()));
		
		return html;
	}

	private String getClanSingleMemberPage(int playerId, String html)
	{
		OfflineSinglePlayerData data = getSinglePlayerData(playerId);
		
		html = html.replace("%playerName%", data.char_name);
		html = html.replace("%playerId%", String.valueOf(playerId));
		html = html.replace("%clanName%", data.clan_name);
		html = html.replace("%online%", "<font color=9b5454>False</font>");
		html = html.replace("%title%", data.title);
		html = html.replace("%pvpPoints%", ""+data.pvpKills);
		html = html.replace("%pkPoints%", ""+data.pkKills);
		html = html.replace("%onlineTime%", getConvertedTime(data.onlineTime));
		html = html.replace("%leader%", Util.boolToString(data.isClanLeader));
		html = html.replace("%subpledge%", getUnitName(data.pledge_type));
		html = html.replace("%nobless%", Util.boolToString(data.isNoble));
		html = html.replace("%hero%", Util.boolToString(data.isHero));
		html = html.replace("%adena%", getConvertedAdena(data.adenaCount));
		html = html.replace("%recs%", ""+data.rec_have);
		html = html.replace("%sevenSigns%", SevenSigns.getCabalShortName(data.sevenSignsSide));
		html = html.replace("%fame%", ""+data.fame);
		html = html.replace("%clanId%", ""+data.clanId);
		
		String[] otherSubs = {"%firstSub%", "%secondSub%", "%thirdSub%"};
		int index = 0;
		for (int[] sub : data.subClassIdLvlBase)
		{
			if (sub[2] == 1)
				html = html.replace("%mainClass%", getFullClassName(ClassId.values()[sub[0]])+"("+sub[1]+")");
			else
				html = html.replace(otherSubs[index], getFullClassName(ClassId.values()[sub[0]])+"("+sub[1]+")");
		}
		//In case player doesn't have all subclasses
		for (String sub : otherSubs)
			html = html.replace(sub, "<br>");
		
		return html;
	}

	private String getClanSkills(Clan clan, String html)
	{
		html = html.replace("%clanName%", clan.getName());
		html = html.replace("%clanId%", String.valueOf(clan.getClanId()));
		
		StringBuilder builder = new StringBuilder();
		for (Skill clanSkill : clan.getSkills())
		{
			builder.append("<tr><td width=50></td>");
			builder.append("<td width=55><br>");
			builder.append("<img src=\"").append(clanSkill.getIcon()).append("\" height=30 width=30>");
			builder.append("</td><td width=445><br><table width=400><tr><td><font name=\"hs12\" color=\"b32c25\">");
			builder.append(clanSkill.getName()).append(" Lv ").append(clanSkill.getLevel());
			builder.append("</font></td></tr><tr><td>");
			String[] descriptions = _clanSkillDecriptions.get(clanSkill.getId());
			if (descriptions == null || descriptions.length < clanSkill.getLevel()-1)
			{
				_log.warn("cannot find skill id:"+clanSkill.getId()+" in Clan Community Skills descriptions!");
			}
			else
			{
				builder.append("<font color=\"bc7420\">").append(descriptions[clanSkill.getLevel()-1]).append("</font>");
			}
			builder.append("</td></tr></table></td></tr>");
		}
		
		html = html.replace("%skills%", builder.toString());
		
		return html;
	}
	
	private String getClanSinglePetitionPage(Player leader, Player member, String html)
	{
		html = html.replace("%clanId%", String.valueOf(leader.getClan().getClanId()));
		html = html.replace("%playerId%", String.valueOf(member.getObjectId()));
		html = html.replace("%playerName%", member.getName());
		html = html.replace("%online%", "<font color=6a9b54>True</font>");
		html = html.replace("%onlineTime%", getConvertedTime(member.getOnlineTime()));
		html = html.replace("%pvpPoints%", String.valueOf(member.getPvpKills()));
		html = html.replace("%pkPoints%", String.valueOf(member.getPkKills()));
		html = html.replace("%fame%", String.valueOf(member.getFame()));
		html = html.replace("%adena%", getConvertedAdena(member.getAdena()));
		
		Collection<SubClass> classes = member.getSubClasses().values();
		int subIndex = 0;
		for (SubClass sub : classes)
		{
			String replacement = "";
			if (sub.isBase())
			{
				replacement = "mainClass";
			}
			else
			{
				if (subIndex == 0)
					replacement = "firstSub";
				else if (subIndex == 1)
					replacement = "secondSub";
				else
					replacement = "thirdSub";
				subIndex++;
			}
			
			html = html.replace("%"+replacement+"%", getFullClassName(ClassId.values()[sub.getClassId()])+"("+sub.getLevel()+")");
		}
		html = html.replace("%firstSub%", "");
		html = html.replace("%secondSub%", "");
		html = html.replace("%thirdSub%", "");
		
		int index = 1;
		for (String question : leader.getClan().getQuestions())
		{
			html = html.replace("%question"+index+"%", question != null && question.length() > 2 ? question+":" : "");
			index++;
		}
		
		SinglePetition petition = leader.getClan().getPetition(member.getObjectId());
		index = 1;
		for (String answer : petition.getAnswers())
		{
			html = html.replace("%answer"+index+"%", answer != null && answer.length() > 2 ? answer : "");
			index++;
		}
		
		html = html.replace("%comment%", petition.getComment());
		
		return html;
	}
	
	private String getClanSinglePetitionPage(Player leader, int playerId, String html)
	{
		PetitionPlayerData data = getSinglePetitionPlayerData(playerId);

		html = html.replace("%clanId%", String.valueOf(leader.getClanId()));
		html = html.replace("%playerId%", String.valueOf(playerId));
		html = html.replace("%online%", "<font color=9b5454>False</font>");
		html = html.replace("%playerName%", data.char_name);
		html = html.replace("%onlineTime%", getConvertedTime(data.onlineTime));
		html = html.replace("%pvpPoints%", ""+data.pvpKills);
		html = html.replace("%pkPoints%", ""+data.pkKills);
		html = html.replace("%fame%", ""+data.fame);
		html = html.replace("%adena%", getConvertedAdena(data.adenaCount));
		//Subclasses
		String[] otherSubs = {"%firstSub%", "%secondSub%", "%thirdSub%"};
		int index = 0;
		for (int[] sub : data.subClassIdLvlBase)
		{
			if (sub[2] == 1)
				html = html.replace("%mainClass%", getFullClassName(ClassId.values()[sub[0]])+"("+sub[1]+")");
			else
				html = html.replace(otherSubs[index], getFullClassName(ClassId.values()[sub[0]])+"("+sub[1]+")");
		}
		//In case player doesn't have all subclasses
		for (String sub : otherSubs)
			html = html.replace(sub, "<br>");
		
		index = 1;
		for (String question : leader.getClan().getQuestions())
		{
			html = html.replace("%question"+index+"%", question != null && question.length() > 2 ? question : "");
			index++;
		}
		
		SinglePetition petition = leader.getClan().getPetition(playerId);
		index = 1;
		for (String answer : petition.getAnswers())
		{
			html = html.replace("%answer"+index+"%", answer != null && answer.length() > 2 ? answer : "");
			index++;
		}
		
		html = html.replace("%comment%", petition.getComment());
		
		return html;
	}
	
	private String getClanRecruitmentManagePage(Player player, String html)
	{
		Clan clan = player.getClan();
		if (clan == null)
			return html;
		
		html = html.replace("%clanName%", clan.getName());
		boolean firstChecked = clan.getClassesNeeded().size() == ALL_CLASSES.length;
		html = html.replace("%checked1%", firstChecked ? "_checked" : "");
		html = html.replace("%checked2%", firstChecked ? "" : "_checked");
		
		String[] notChoosenClasses = getNotChosenClasses(clan);
		html = html.replace("%firstClassGroup%", notChoosenClasses[0]);
		html = html.replace("%secondClassGroup%", notChoosenClasses[1]);
		
		StringBuilder builder = new StringBuilder();
		builder.append("<tr>");
		int index = -1;
		for (Integer clas : clan.getClassesNeeded())
		{
			if (index%4 == 3)
				builder.append("</tr><tr>");
			index++;

			builder.append("<td width=100><button value=\"").append(ALL_CLASSES[clas-88]).append("\" action=\"bypass  _clbbsmanage_5 ").append(ALL_CLASSES[clas-88]).append("\" back=\"l2ui_ct1.button.button_df_small_down\" width=105 height=20 fore=\"l2ui_ct1.button.button_df_small\"></td>");
		}
		builder.append("</tr>");
		
		html = html.replace("%choosenClasses%", builder.toString());
		
		for (int i = 0;i<8;i++)
		{
			String clanQuestion = clan.getQuestions()[i];
			html = html.replace("%question"+(i+1)+"%", clanQuestion != null && clanQuestion.length() > 0 ? clanQuestion : "Question "+(i+1)+":");
		}
		
		html = html.replace("%recrutation%", clan.isRecruting() ? "Stop" : "Start");
		return html;
	}

	private String getClanJoinPage(Player player, Clan clan, String html)
	{
		html = html.replace("%clanId%", String.valueOf(clan.getClanId()));
		html = html.replace("%clanName%", clan.getName());
		for (int i = 0;i<8;i++)
		{
			String question = clan.getQuestions()[i];
			if (question != null && question.length() > 2)
			{
				html = html.replace("%question"+(i+1)+"%", question);
				html = html.replace("%answer"+(i+1)+"%", "<edit var=\"answer"+(i+1)+"\" width=275 height=15>");
			}
			else
			{
				html = html.replace("%question"+(i+1)+"%", "");
				html = html.replace("%answer"+(i+1)+"%", "");
				html = html.replace("$answer"+(i+1), " ");
			}
		}
		
		boolean canJoin = false;
		StringBuilder builder = new StringBuilder();
		int index = -1;

		builder.append("<tr>");
		for (int classNeeded : clan.getClassesNeeded())
		{
			index++;
			if (index == 6)
			{
				builder.append("</tr><tr>");
				index = 0;
			}
			boolean goodClass = player.getSubClasses().keySet().contains(classNeeded);
			
			if (goodClass)
				canJoin = true;

			builder.append("<td width=130><font color=\"").append(goodClass ? "6a9b54" : "9b5454").append("\">");
			builder.append(getFullClassName(ClassId.values()[classNeeded]));
			builder.append("</font></td>");
		}
		builder.append("</tr>");
		
		html = html.replace("%classes%", builder.toString());
		
		if (canJoin)
			html = html.replace("%joinClanButton%", "<br><center><button action=\"bypass _bbsclanjoin_"+clan.getClanId()+"_1 | $answer1 | $answer2 | $answer3 | $answer4 | $answer5 | $answer6 | $answer7 | $answer8 | $comment |\" value=\"Send\" width=130 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.button_df\"></center>");
		else
			html = html.replace("%joinClanButton%", "");
		
		return html;
	}
	
	private String getClanPetitionsPage(Player player, Clan clan, String html)
	{
		html = html.replace("%clanName%", clan.getName());
		
		int index = 1;
		List<SinglePetition> _petitionsToRemove = new ArrayList<SinglePetition>();
		StringBuilder builder = new StringBuilder();
		
		for (SinglePetition petition : clan.getPetitions())
		{
			ClanPetitionData data = getClanPetitionsData(petition.getSenderId());
			if (data == null)
			{
				_petitionsToRemove.add(petition);
				continue;
			}
			builder.append("<tr><td width=30><font name=\"hs12\" color=\"f1b45d\">");
			builder.append(index);
			builder.append(".</font></font></td><td width=150>");
			builder.append("<button action=\"bypass _clbbsplayerpetition_").append(petition.getSenderId()).append("\" value=\"").append(data.char_name).append("\" width=150 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.button_df\">");
			builder.append("</td><td width=100><center>");
			builder.append(data.online);
			builder.append("</td><td width=95><font color=\"f1b45d\"><center>");
			builder.append(data.pvpKills);
			builder.append("</center></font></td><td width=100><font color=\"f1b45d\"><center>");
			builder.append(getConvertedTime(data.onlineTime));
			builder.append("</center></font></td><td width=75><font color=\"f1b45d\"><center>");
			builder.append(Util.boolToString(data.isNoble));
			builder.append("</center></font></td></tr>");
			index ++;
		}
		
		for (SinglePetition petitionToRemove : _petitionsToRemove)
			clan.deletePetition(petitionToRemove);
		
		html = html.replace("%petitions%", builder.toString());
		
		return html;
	}
	
	public String getPlayerInventoryPage(Player player, String html)
	{
		html = html.replace("%playerName%", player.getName());
		html = html.replace("%back%", (player.getClan() != null ? "_clbbssinglemember_"+player.getObjectId() : "_clbbsplayerpetition_"+player.getObjectId()));
		
		PcInventory pcInv = player.getInventory();
		StringBuilder builder = new StringBuilder();
		
		builder.append("<tr>");
		for (int i = 0;i<SLOTS.length;i++)
		{
			if (i % 2 == 0)
				builder.append("</tr><tr>");
			builder.append("<td><table><tr><td height=40>");
			builder.append(pcInv.getPaperdollItem(SLOTS[i]) != null ? "<img src="+pcInv.getPaperdollItem(SLOTS[i]).getTemplate().getIcon() + " width=32 height=32>" : "<img src=\"Icon.low_tab\" width=32 height=32>");
			builder.append("</td><td width=150><font color=\"bc7420\">");
			builder.append(pcInv.getPaperdollItem(SLOTS[i]) != null ? pcInv.getPaperdollItem(SLOTS[i]).getTemplate().getName() +" +"+pcInv.getPaperdollItem(SLOTS[i]).getEnchantLevel() : "No "+NAMES[i]);
			builder.append("</font></td></tr></table></td>");
		}
		builder.append("</tr>");
		
		html = html.replace("%inventory%", builder.toString());
		
		return html;
	}
	
	public String getPlayerInventoryPage(int playerId, String html)
	{
		OfflinePlayerInventoryData data = getPlayerInventoryData(playerId);
		html = html.replace("%playerName%", data.char_name);
		html = html.replace("%back%", (data.clanId != 0 ? "_clbbssinglemember_"+playerId : "_clbbsplayerpetition_"+playerId));
		
		StringBuilder builder = new StringBuilder();

		builder.append("<tr>");
		for (int i = 0;i<SLOTS.length;i++)
		{
			if (i % 2 == 0)
				builder.append("</tr><tr>");
			int[] item = data.itemIdAndEnchantForSlot.get(i);
			ItemTemplate template = null;
			if (item != null && item[0] > 0)
				template = ItemHolder.getInstance().getTemplate(item[0]);
			builder.append("<td><table><tr><td height=40>");
			builder.append(template != null ? ("<img src="+template.getIcon() + " width=32 height=32>") : ("<img src=\"Icon.low_tab\" width=32 height=32>"));
			builder.append("</td><td width=150><font color=\"bc7420\">");
			builder.append(template != null ? (template.getName() +" +"+item[1]) : ("No "+NAMES[i]));
			builder.append("</font></td></tr></table></td>");
		}
		builder.append("</tr>");
		
		html = html.replace("%inventory%", builder.toString());
		
		return html;
	}
	
	private class OfflinePlayerInventoryData
	{
		String char_name;
		int clanId;
		Map<Integer, int[]> itemIdAndEnchantForSlot = new FastMap<Integer, int[]>();
	}

	private OfflinePlayerInventoryData getPlayerInventoryData(int playerId)
	{
		OfflinePlayerInventoryData data = new OfflinePlayerInventoryData();
		
		Connection con = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			
			PreparedStatement statement = null;
			ResultSet rset = null;
			
			try
			{
				statement = con.prepareStatement("SELECT char_name,clanid FROM characters WHERE obj_Id = '"+playerId+"'");
				rset = statement.executeQuery();
				
				if (rset.next())
				{
					data.char_name = rset.getString("char_name");
					data.clanId = rset.getInt("clanid");
				}
			}
			finally
			{
				DbUtils.closeQuietly(statement, rset);
			}
			
			try
			{
				statement = con.prepareStatement("SELECT item_id, loc_data, enchant_level FROM items WHERE owner_id = '"+playerId+"' AND loc='PAPERDOLL'");
				rset = statement.executeQuery();
				while (rset.next())
				{
					int loc = rset.getInt("loc_data");
					for (int i = 0 ; i < SLOTS.length ; i++)
						if (loc == SLOTS[i])
						{
							int[] itemData = {rset.getInt("item_id"), rset.getInt("enchant_level")};
							data.itemIdAndEnchantForSlot.put(i, itemData);
						}
				}
			}
			finally
			{
				DbUtils.closeQuietly(statement, rset);
			}
		}
		catch (Exception e)
		{
			_log.error("Error in getPlayerInventoryData:", e);
		}
		finally
		{
			DbUtils.closeQuietly(con);
		}
			
		return data;
	}

	private int[] getMainClanPageData(Clan clan)
	{
		int[] data = new int[3];
		
		Connection con = null;
		
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			
			PreparedStatement statement = null;
			ResultSet rset = null;
			
			try
			{
				statement = con.prepareStatement("SELECT SUM(pvpkills), SUM(pkkills) FROM characters WHERE characters.clanid = '"+clan.getClanId()+"'");
				rset = statement.executeQuery();
				
				if (rset.next())
				{
					data[0] = rset.getInt("SUM(pvpkills)");
					data[1] = rset.getInt("SUM(pkkills)");
				}
			}
			finally
			{
				DbUtils.close(statement, rset);
			}
		}
		catch (Exception e)
		{
			_log.error("Error in getMainClanPageData:", e);
		}
		finally
		{
			DbUtils.closeQuietly(con);
		}
		
		return data;
	}
	
	private class OfflineSinglePlayerData
	{
		String char_name;
		String title = "";
		int pvpKills;
		int pkKills;
		long onlineTime;
		int rec_have;
		int sevenSignsSide = 0;
		int fame;
		int clanId;
		String clan_name = "";
		int pledge_type = 0;
		boolean isClanLeader = false;
		boolean isNoble = false;
		boolean isHero = false;
		long adenaCount = 0L;
		List<int[]> subClassIdLvlBase = new ArrayList<int[]>();
	}
	
	private OfflineSinglePlayerData getSinglePlayerData(int playerId)
	{
		OfflineSinglePlayerData data = new OfflineSinglePlayerData();
		
		Connection con = null;
		
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = null;
			ResultSet rset = null;
			
			try
			{
				statement = con.prepareStatement("SELECT char_name,title,pvpkills,pkkills,onlinetime,rec_have,fame,clanid FROM characters WHERE obj_Id = '"+playerId+"'");
				rset = statement.executeQuery();
				
				if (rset.next())
				{
					data.char_name = rset.getString("char_name");
					data.title = rset.getString("title");
					data.pvpKills = rset.getInt("pvpkills");
					data.pkKills = rset.getInt("pkkills");
					data.onlineTime = rset.getLong("onlinetime");
					data.rec_have = rset.getInt("rec_have");
					data.fame = rset.getInt("fame");
					data.clanId = rset.getInt("clanid");
				}
			}
			finally
			{
				DbUtils.closeQuietly(statement, rset);
			}
			
			try
			{
				statement = con.prepareStatement("SELECT cabal FROM seven_signs WHERE char_obj_id='"+playerId+"'");
				rset = statement.executeQuery();
				
				if (rset.next())
				{
					data.sevenSignsSide = SevenSigns.getCabalNumber(rset.getString("cabal"));
				}
			}
			finally
			{
				DbUtils.closeQuietly(statement, rset);
			}
			
			//If player have clan
			if (data.clanId > 0)
			{
				
				try
				{
					statement = con.prepareStatement("select type,name,leader_id from `clan_subpledges` where `clan_id` = '"+data.clanId+"'");
					rset = statement.executeQuery();
					
					if (rset.next())
					{
						data.clan_name = rset.getString("name");
						data.pledge_type = rset.getInt("type");
						data.isClanLeader = rset.getInt("leader_id") == playerId;
					}
				}
				finally
				{
					DbUtils.closeQuietly(statement, rset);
				}
			}
			
			try
			{
				statement = con.prepareStatement("select olympiad_points from `olympiad_nobles` where `char_id` = '"+playerId+"'");
				rset = statement.executeQuery();
				
				if (rset.next())
				{
					data.isNoble = true;
				}
			}
			finally
			{
				DbUtils.closeQuietly(statement, rset);
			}
			
			try
			{
				statement = con.prepareStatement("select count from `heroes` where `char_id` = '"+playerId+"'");;
				rset = statement.executeQuery();
				
				if (rset.next())
					data.isHero = true;
			}
			finally
			{
				DbUtils.closeQuietly(statement, rset);
			}

			try
			{
				statement = con.prepareStatement("select count from `items` where `owner_id` = '"+playerId+"' AND item_id=57");
				rset = statement.executeQuery();
				if (rset.next())
					data.adenaCount = rset.getLong("count");
			}
			finally
			{
				DbUtils.closeQuietly(statement, rset);
			}
			
			try
			{
				statement = con.prepareStatement("select class_id,level,isBase from `character_subclasses` where `char_obj_id` = '"+playerId+"'");
				rset = statement.executeQuery();
				
				while (rset.next())
				{
					int[] sub = new int[3];
					sub[0] = rset.getInt("class_id");
					sub[1] = rset.getInt("level");
					sub[2] = rset.getInt("isBase");
					data.subClassIdLvlBase.add(sub);
				}
			}
			finally
			{
				DbUtils.closeQuietly(statement, rset);
			}
			
		}
		catch (Exception e)
		{
			_log.error("Error in getSinglePlayerData:", e);
		}
		finally
		{
			DbUtils.closeQuietly(con);
		}
		
		return data;
	}
	
	private class PetitionPlayerData
	{
		String char_name;
		long onlineTime;
		int pvpKills;
		int pkKills;
		int fame;
		long adenaCount = 0L;
		List<int[]> subClassIdLvlBase = new ArrayList<int[]>();
	}
	
	private PetitionPlayerData getSinglePetitionPlayerData(int playerId)
	{
		PetitionPlayerData data = new PetitionPlayerData();
		
		Connection con = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = null;
			ResultSet rset = null;
			try
			{
				statement = con.prepareStatement("SELECT char_name,onlinetime,pvpkills,pkkills,fame FROM characters WHERE obj_Id = '"+playerId+"'");
				rset = statement.executeQuery();
				if (rset.next())
				{
					data.char_name = rset.getString("char_name");
					data.onlineTime = rset.getLong("onlinetime");
					data.pvpKills = rset.getInt("pvpkills");
					data.pkKills = rset.getInt("pkkills");
					data.fame = rset.getInt("fame");
				}
			}
			finally
			{
				DbUtils.closeQuietly(statement, rset);
			}

			try
			{
				statement = con.prepareStatement("select count from `items` where `owner_id` = '"+playerId+"' AND item_id=57");
				rset = statement.executeQuery();
				if (rset.next())
					data.adenaCount = rset.getLong("count");
			}
			finally
			{
				DbUtils.closeQuietly(statement, rset);
			}
			
			try
			{
				statement = con.prepareStatement("select class_id,level,isBase from `character_subclasses` where `char_obj_id` = '"+playerId+"'");
				rset = statement.executeQuery();
				while (rset.next())
				{
					int[] sub = new int[3];
					sub[0] = rset.getInt("class_id");
					sub[1] = rset.getInt("level");
					sub[2] = rset.getInt("isBase");
					data.subClassIdLvlBase.add(sub);
				}
			}
			finally
			{
				DbUtils.closeQuietly(statement, rset);
			}
			
		}
		catch (Exception e)
		{
			_log.error("Error in getSinglePetitionPlayerData:", e);
		}
		finally
		{
			DbUtils.closeQuietly(con);
		}
		
		return data;
	}
	
	private class ClanPetitionData
	{
		String char_name;
		String online;
		int pvpKills;
		long onlineTime;
		boolean isNoble;
	}
	
	private ClanPetitionData getClanPetitionsData(int senderId)
	{
		ClanPetitionData data = new ClanPetitionData();
		Player sender = GameObjectsStorage.getPlayer(senderId);
		boolean haveclan = false;
		if (sender != null)
		{
			data.char_name = sender.getName();
			data.online = "<font color=6a9b54>True</font>";
			data.pvpKills = sender.getPvpKills();
			data.onlineTime = sender.getOnlineTime();
			data.isNoble = sender.isNoble();
		}
		else
		{
			Connection con = null;
			
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				PreparedStatement statement = null;
				ResultSet rset = null;
				try
				{
					statement = con.prepareStatement("SELECT char_name,pvpkills,onlinetime,clanid FROM characters WHERE obj_Id = '"+senderId+"'");
					rset = statement.executeQuery();
					if (rset.next())
					{
						data.char_name = rset.getString("char_name");
						data.online = "<font color=9b5454>False</font>";
						data.pvpKills = rset.getInt("pvpkills");
						data.onlineTime = rset.getLong("onlinetime");
						if (rset.getInt("clanid") > 0)
							haveclan = true;
					}
				}
				finally
				{
					DbUtils.closeQuietly(statement, rset);
				}
				
				try
				{
					statement = con.prepareStatement("SELECT char_id FROM olympiad_nobles WHERE char_id = '"+senderId+"'");
					rset = statement.executeQuery();
					if (rset.next())
					{
						data.isNoble = true;
					}
				}
				finally
				{
					DbUtils.closeQuietly(statement, rset);
				}
			}
			catch (Exception e)
			{
				_log.error("Error in getClanPetitionsData:", e);
			}
			finally
			{
				DbUtils.closeQuietly(con);
			}
		}
		
		if (haveclan)
			return null;
		else
			return data;
	}

	private String getConvertedTime(long seconds)
	{
		int days = (int) (seconds/86400);
		seconds -= days*86400;
		int hours = (int) (seconds/3600);
		seconds -= hours*3600;
		int minutes = (int) (seconds/60);
		
		boolean includeNext = true;
		String time = "";
		if (days > 0)
		{
			time = days + " Days ";
			if (days > 5)
				includeNext = false;
		}
		if (hours > 0 && includeNext)
		{
			if (time.length() > 0)
				includeNext = false;
			time += hours + " Hours ";
			if (hours > 10)
				includeNext = false;
		}
		if (minutes > 0 && includeNext)
		{
			time += minutes + " Mins";
		}
		return time;
	}

	private String getConvertedAdena(long adena)
	{
		String text = "";
		String convertedAdena = String.valueOf(adena);
		int ks = (convertedAdena.length()-1)/3;
		long firstValue = adena/(long)(Math.pow(1000, ks));
		text = firstValue+getKs(ks);
		if ((convertedAdena.length()-2)/3 < ks)
		{
			adena -= firstValue*(long)(Math.pow(1000, ks));
			if (adena/(long)(Math.pow(1000, (ks-1))) > 0)
				text += " "+adena/(int)(Math.pow(1000, (ks-1)))+getKs(ks-1);
		}
		return text;
	}
	
	private String getKs(int howMany)
	{
		String x = "";
		for (int i = 0;i<howMany;i++)
			x += "k";
		return x;
	}
	
	public String getUnitName(int type)
	{
		String subUnitName = "";
		switch (type)
		{
		case Clan.SUBUNIT_MAIN_CLAN:
			subUnitName = "Main Clan";
			break;
		case Clan.SUBUNIT_ROYAL1:
		case Clan.SUBUNIT_ROYAL2:
			subUnitName = "Royal Guard";
			break;
			default:
				subUnitName = "Order of Knight";
		}
		return subUnitName;
	}
	
	private void sendErrorMessage(Player player, String message, String backPage)
	{
		sendInfoMessage(player, message, backPage, true);
	}
	
	private void sendInfoMessage(Player player, String message, String backPage, boolean error)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<html><head><title>Clan Recruitment</title></head><body>");
		builder.append("<table border=0 cellpadding=0 cellspacing=0 width=700><tr><td><br><br>");
		builder.append("<center><font color = \"").append(error ? "9b5454" : "6a9b54").append("\">");
		builder.append(message);
		builder.append("</font><br><br><br>");
		builder.append("<button action=\"bypass ").append(backPage).append("\" value=\"Back\" width=130 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.button_df\">");
		builder.append("</center></td></tr></table></body></html>");
		
		ShowBoard.separateAndSend(builder.toString(), player);
	}
	
	//TODO zrobic online total w klanie
	
	private String getMainStatsTableColor(int index)
	{
		return index % 2 == 0 ? "222320" : "191919";
	}
	private String getAllClansRank(Player player, String html, int page)
	{
		String newHtml = html;
		Clan[] clans = ClanTable.getInstance().getClans();
		Arrays.sort(clans, _clansComparator);
		
		for (int i = 0 ; i < 10;i++)
		{
			int clanIndex = i + (page*9);
			if (page > 0)
				clanIndex++;
			Clan clan = clans.length <= clanIndex ? null : clans[clanIndex];
			String crest = getCrestHtml(clan != null ? clan.getCrestId() : 0, clan != null && clan.getAlliance() != null ? clan.getAlliance().getAllyCrestId() : 0);
			
			newHtml = newHtml.replace("%level"+(i+1)+"%", clan != null ? "<img src="+getLevelIcon(clan.getLevel())+" width=32 height=32>" : "<br>");
			newHtml = newHtml.replace("%crest"+(i+1)+"%", crest);
			newHtml = newHtml.replace("%clan"+(i+1)+"%", clan != null ? "<button value=\""+clan.getName()+"\" action=\"bypass _clbbsclan_"+clan.getClanId()+"\" width=200 height=32 back=\"L2UI_CT1.OlympiadWnd_DF_Reward_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Reward\">" : "<br>");
			newHtml = newHtml.replace("%alliance"+(i+1)+"%", clan != null && clan.getAlliance() != null ? clan.getAlliance().getAllyName() : "<br>");
			newHtml = newHtml.replace("%leader"+(i+1)+"%", clan != null ? clan.getLeaderName() : "<br>");
			newHtml = newHtml.replace("%members"+(i+1)+"%", clan != null ? String.valueOf(clan.getAllMembers().size()) : "<br>");
		}
		
		newHtml = newHtml.replace("%myClanButton%", player.getClan() != null ? "<center><button action=\"bypass _clbbsclan_"+player.getClanId()+"\" value=\""+player.getClan().getName()+"\" width=200 height=30 back=\"L2UI_CT1.OlympiadWnd_DF_Info_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Info\"></center>" : "<br>");

		StringBuilder builder = new StringBuilder();
		builder.append("<table width=700><tr><td width=350>");
		if (page > 0)
			builder.append("<center><button action=\"bypass _clbbslist_").append(page-1).append("\" value=\"Previous\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.button_df\"></center>");
		builder.append("</td><td width=350>");
		if (clans.length > CLANS_PER_PAGE+CLANS_PER_PAGE*page)
			builder.append("<center><button action=\"bypass _clbbslist_").append(page+1).append("\" value=\"Next\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.button_df\"></center>");
		builder.append("</td></tr></table>");

		newHtml = newHtml.replace("%pages%", builder.toString());
		
		return newHtml;
	}
	
	private String getCrestHtml(int clanCrestId, int allyCrestId)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<table fixwidth=24 border=0 cellpadding=0 cellspacing=0>");
		builder.append("   <tr><td height=10><br></td></tr><tr><td height=10><br></td></tr>");//Just adding some space
		builder.append("   <tr>");
		builder.append("        <td fixwidth=8>");
		if (allyCrestId > 0)
		{
			builder.append("            <table fixwidth=8 border=0 cellpadding=0 cellspacing=0 background=\"Crest.crest_1_"+allyCrestId+"\"");
			builder.append("                <tr>");
			builder.append("                <tr>");
			builder.append("                    <td fixwidth=8><img height=4 width=8 src=\"L2UI.SquareBlack\"><br1>&nbsp;</td>");
			builder.append("                </tr>");
			builder.append("            </table>");
		}
		else
			builder.append("<br>");
		builder.append("        </td>");
		builder.append("        <td fixwidth=16>");
		if (clanCrestId > 0)
		{
			builder.append("            <table fixwidth=16 border=0 cellpadding=0 cellspacing=0 background=\"Crest.crest_1_"+clanCrestId+"\">");
			builder.append("                <tr>");
			builder.append("                    <td fixwidth=16><img height=4 width=16 src=\"L2UI.SquareBlack\"><br1>&nbsp;</td>");
			builder.append("                </tr>");
			builder.append("            </table>");
		}
		else
			builder.append("<br>");
		builder.append("        </td>");
		builder.append("    </tr>");
		builder.append("</table>");
		
		return builder.toString();
	}

	private boolean manageRecrutationWindow(Player player, int actionToken, String wholeText)
	{
		Clan clan = player.getClan();
		
		switch (actionToken)
		{
		case 1:
			clan.getClassesNeeded().clear();
			for (int i = 88;i<=118;i++)
				clan.addClassNeeded(i);
			break;
		case 2:
			clan.getClassesNeeded().clear();
			break;
		case 3:
			if (wholeText.length() > 2)
			{
				String clazz = wholeText.substring(2);
				for (int i = 0;i<ALL_CLASSES.length;i++)
					if (ALL_CLASSES[i].equals(clazz))
					{
						clan.addClassNeeded(88+i);
						break;
					}
			}
			break;
		case 5:
			String clazz = wholeText.substring(2);
			for (int i = 0;i<ALL_CLASSES.length;i++)
				if (ALL_CLASSES[i].equals(clazz))
				{
					clan.deleteClassNeeded(88+i);
					break;
				}
			break;
		case 6:
			String[] questions = clan.getQuestions();
			StringTokenizer st = new StringTokenizer(wholeText.substring(2), "|");
			for (int i = 0;i<8;i++)
			{
				String question = st.nextToken();
				if (question.length() > 3)
					questions[i] = question;
				clan.setQuestions(questions);
			}
			break;
		case 7:
			clan.setRecrutating(!clan.isRecruting());
			break;
		}
		return false;
	}

	private boolean manageClanJoinWindow(Player player, Clan clan, String text)
	{
		StringTokenizer st = new StringTokenizer(text, "|");
		String[] answers = new String[8];
		for (int i = 0;i<8;i++)
		{
			String answer = st.nextToken();
			answers[i] = answer;
		}
		String comment = st.nextToken();
		return clan.addPetition(player.getObjectId(), answers, comment);
	}
	
	private void managePlayerPetition(Player player, int senderId, int action)
	{
		Player sender = GameObjectsStorage.getPlayer(senderId);
		Clan clan = player.getClan();
		switch (action)
		{
		case 1:
			
			int type = -1;
			for (SubUnit unit : clan.getAllSubUnits())
				if (clan.getUnitMembersSize(unit.getType()) < clan.getSubPledgeLimit(unit.getType()))
					type = unit.getType();
			
			if (type == -1)
			{
				sendErrorMessage(player, "Clan is full!", "_clbbsplayerpetition_"+senderId);
				return;
			}
			if (sender != null)
			{
				player.getClan().addMember(sender, type);
			}
			else
			{
				Connection con = null;
				PreparedStatement statement = null;
				try
				{
					con = DatabaseFactory.getInstance().getConnection();
					statement = con.prepareStatement("UPDATE characters SET clanid="+clan.getClanId()+", pledge_type="+type+" WHERE obj_Id="+senderId+" AND clanid=0");
					statement.execute();
				}
				catch (Exception e)
				{
					_log.error("Error in managePlayerPetition:", e);
				}
				finally
				{
					DbUtils.closeQuietly(con, statement);
				}
				player.getClan().getSubUnit(type).addUnitMember(getSubUnitMember(clan, type, senderId));
			}
			sendInfoMessage(player, "Member has been added!", "_clbbspetitions_"+clan.getClanId(), false);
		case 2:
			clan.deletePetition(senderId);
			if (action == 2)
				sendInfoMessage(player, "Petition has been deleted!", "_clbbspetitions_"+clan.getClanId(), false);
			break;
		}
		
	}
	
	private UnitMember getSubUnitMember(Clan clan, int type, int memberId)
	{
		UnitMember member = null;
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(//
					"SELECT `c`.`char_name` AS `char_name`," + //
					"`s`.`level` AS `level`," + //
					"`s`.`class_id` AS `class_id`," + //
					"`c`.`title` AS `title`," + //
					"`c`.`pledge_rank` AS `pledge_rank`," + //
					"`c`.`sex` AS `sex` " + //
					"FROM `characters` `c` " + //
					"LEFT JOIN `character_subclasses` `s` ON (`s`.`char_obj_id` = `c`.`charId` AND `s`.`isBase` = '1') " + //
			"WHERE `c`.`obj_Id`=?");
			statement.setInt(1, memberId);
			rset = statement.executeQuery();
			if (rset.next())
			{
				member = new UnitMember(clan, rset.getString("char_name"), rset.getString("title"), rset.getInt("level"), rset.getInt("class_id"), memberId, type, rset.getInt("pledge_rank"), 0, rset.getInt("sex"), Clan.SUBUNIT_NONE);
			}
			
		}
		catch (Exception e)
		{
			_log.error("Error in managePlayerPetition:", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		
		return member;
	}
	
	private ClanComparator _clansComparator = new ClanComparator();
	
	private static class ClanComparator implements Comparator<Clan>, Serializable
	{
		private static final long serialVersionUID = 3433237824902505293L;

		@Override
		public int compare(Clan o1, Clan o2)
		{
			if (o1.getLevel() > o2.getLevel())
				return -1;
			if (o2.getLevel() > o1.getLevel())
				return 1;
			if (o1.getReputationScore() > o2.getReputationScore())
				return -1;
			if (o2.getReputationScore() > o1.getReputationScore())
				return 1;
			return 0;
		}
	}
	
	private String[] getNotChosenClasses(Clan clan)
	{
		String[] splited = {"",""};
		
		ArrayList<Integer> classes = clan.getClassesNeeded();
		
		for (int i = 0;i<ALL_CLASSES.length;i++)
			if (!classes.contains(i+88))
			{
				int x = 1;
				if (i%2 == 0)
					x = 0;
				if (!splited[x].equals(""))
					splited[x] += ";";
				splited[x] += ALL_CLASSES[i];
			}
		return splited;
	}
	
	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		if ("announcepledgewrite".equals(cmd))
		{
			Clan clan = player.getClan();
			if (clan == null || clan.getLevel() < 2 || !player.isClanLeader())
			{
				onBypassCommand(player, "_clbbsclan_" + player.getClanId());
				return;
			}

			if (arg3 == null || arg3.isEmpty())
			{
				onBypassCommand(player, "_announcepledgewriteform");
				return;
			}
			
			//arg3 = removeIllegalText(arg3);
			arg3 = arg3.replace("<", "");
			arg3 = arg3.replace(">", "");
			arg3 = arg3.replace("&", "");
			arg3 = arg3.replace("$", "");

			if (arg3.isEmpty())
			{
				onBypassCommand(player, "_announcepledgewriteform");
				return;
			}

			if (arg3.length() > 3000)
				arg3 = arg3.substring(0, 3000);

			int type = Integer.parseInt(st.nextToken());

			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("REPLACE INTO `bbs_clannotice`(clan_id, type, notice) VALUES(?, ?, ?)");
				
				statement.setInt(1, clan.getClanId());
				statement.setInt(2, type);
				statement.setString(3, arg3);
				statement.execute();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				onBypassCommand(player, "_announcepledgewriteform");
				return;
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}

			if (type == 1)
				clan.setNotice(arg3.replace("\n", "<br1>"));
			else
				clan.setNotice("");

			player.sendPacket(Msg.NOTICE_HAS_BEEN_SAVED);
			onBypassCommand(player, "_announcepledgewriteform");
		}
	}

	private String removeIllegalText(String text)//TODO
	{
		char[] array = text.toCharArray();
		for (int i = 0;i<text.length();i++)
		{
			char c = array[i];
			if (i + 1 < text.length())
				if (c == '<')
				{
					char nextChar = array[i+1];
					if (nextChar != 'b' && nextChar != 'f')
						array[i] = ' ';
				}
				
		}
		return null;
	}

	private class Listener implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			Clan clan = player.getClan();
			if (clan == null || clan.getLevel() < 2)
				return;

			if (clan.getNotice() == null)
			{
				String notice = "";
				int type = 0;
				Connection con = null;
				PreparedStatement statement = null;
				ResultSet rset = null;
				try
				{
					con = DatabaseFactory.getInstance().getConnection();
					statement = con.prepareStatement("SELECT * FROM `bbs_clannotice` WHERE `clan_id` = ? and type != 2");
					statement.setInt(1, clan.getClanId());
					rset = statement.executeQuery();
					
					if (rset.next())
					{
						notice = rset.getString("notice");
						type = rset.getInt("type");
					}
				}
				catch (Exception e)
				{
					_log.error("While updating bbs_clannotice:", e);
				}
				finally
				{
					DbUtils.closeQuietly(con, statement, rset);
				}

				clan.setNotice(type == 1 ? notice.replace("\n", "<br1>\n") : "");
			}

			if (!clan.getNotice().isEmpty())
			{
				String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "clan_popup.htm", player);
				html = html.replace("%pledge_name%", clan.getName());
				html = html.replace("%content%", clan.getNotice());

				player.sendPacket(new NpcHtmlMessage(0).setHtml(html));
			}
		}
	}
	

	
	private static String getFullClassName(ClassId classIndex)
	{
		switch (classIndex)
		{
		case phoenixKnight:
			return "Phoenix Knight";
		case hellKnight:
			return "Hell Knight";
		case arcanaLord:
			return "Arcana Lord";

		case evaTemplar:
			return "Eva's Templar";
		case swordMuse:
			return "Sword Muse";
		case windRider:
			return "Wind Rider";
		case moonlightSentinel:
			return "Moonlight Sentinel";
		case mysticMuse:
			return "Mystic Muse";
		case elementalMaster:
			return "Elemental Master";
		case evaSaint:
			return "Eva's Saint";

		case shillienTemplar:
			return "ShillenTemplar";
		case spectralDancer:
			return "Spectral Dancer";
		case ghostHunter:
			return "Ghost Hunter";
		case ghostSentinel:
			return "Ghost Sentinel";
		case stormScreamer:
			return "Storm Screamer";
		case spectralMaster:
			return "Spectral Master";
		case shillienSaint:
			return "Shillien Saint";

		case grandKhauatari:
			return "Grand Khauatari";

		case fortuneSeeker:
			return "Fortune Seeker";
			
			default:
				return classIndex.name().substring(0,1).toUpperCase()+classIndex.name().substring(1);
		}
	}
	
	private static String getResidenceName(Residence r)
	{
		int id = r.getId();
		StringTokenizer st = new StringTokenizer(r.getName());
		if (id >= 101 && id <= 121 || id >= 22 && id <= 30 || id == 30 || id == 34 || id==35 ||
				id >= 47 && id <= 58 || id == 61 || id == 62)
			return st.nextToken();
		
		switch (id)
		{
		case 31://second
		case 32:
		case 33:
		case 36:
		case 37:
		case 38:
		case 41:
		case 42:
		case 43:
		case 44:
		case 45:
			st.nextToken();
			return st.nextToken();
		case 21://third
			st.nextToken();
			st.nextToken();
			return st.nextToken();
			
		case 59://first & second
		case 60:
		case 63:
			return st.nextToken()+" "+st.nextToken();
			default:
				return r.getName();
		}
	}
	
	{
		_clanSkillDecriptions.put(370, new String[] {"Increases clan members' Max HP by 3%. It only affects those who are of an Heir rank or higher.",
				"Increases clan members' Max HP by 5%. It only affects those who are of an Heir rank or higher.",
				"Increases clan members' Max HP by 6%. It only affects those who are of an Heir rank or higher."});
		_clanSkillDecriptions.put(371, new String[] {"Increases clan members' Max CP by 6%. It only affects those who are of a Baron rank or higher.",
				"Increases clan members' Max CP by 10%. It only affects those who are of a Baron rank or higher.",
				"Increases clan members' Max CP by 12%. It only affects those who are of a Baron rank or higher."});
		_clanSkillDecriptions.put(372, new String[] {"Increases clan members' Max MP by 3%. It only affects those who are of a Viscount rank or higher.",
				"Increases clan members' Max MP by 5%. It only affects those who are of a Viscount rank or higher.",
				"Increases clan members' Max MP by 6%. It only affects those who are of a Viscount rank or higher."});
		_clanSkillDecriptions.put(373, new String[] {"Increases clan members' HP regeneration by 3%. It only affects those who are of an Heir rank or higher.",
				"Increases clan members' HP regeneration by 5%. It only affects those who are of an Heir rank or higher.",
				"Increases clan members' HP regeneration by 6%. It only affects those who are of an Heir rank or higher."});
		_clanSkillDecriptions.put(374, new String[] {"Increases clan members' CP regeneration by 6%. It only affects those who are of an Elder rank or higher.",
				"Increases clan members' CP regeneration by 10%. It only affects those who are of an Elder rank or higher.",
				"Increases clan members' CP regeneration by 12%. It only affects those who are of an Elder rank or higher."});
		_clanSkillDecriptions.put(375, new String[] {"Increases clan members' MP regeneration by 3%. It only affects those who are of a Viscount rank or higher.",
				"Increases clan members' MP regeneration by 5%. It only affects those who are of a Viscount rank or higher.",
				"Increases clan members' MP regeneration by 6%. It only affects those who are of a Viscount rank or higher."});
		_clanSkillDecriptions.put(376, new String[] {"Increases clan members' P. Atk. by 3%. It only affects those who are of a Knight rank or higher.",
				"Increases clan members' P. Atk. by 5%. It only affects those who are of a Knight rank or higher.",
				"Increases clan members' P. Atk. by 6%. It only affects those who are of a Knight rank or higher."});
		_clanSkillDecriptions.put(377, new String[] {"Increases clan members' P. Def. by 3%. It only affects those who are of a Knight rank or higher.",
				"Increases clan members' P. Def. by 5%. It only affects those who are of a Knight rank or higher.",
				"Increases clan members' P. Def. by 6%. It only affects those who are of a Knight rank or higher."});
		_clanSkillDecriptions.put(378, new String[] {"Increases clan members' M. Atk by 6%. It only affects those who are of a Viscount rank or higher.",
				"Increases clan members' M. Atk by 10%. It only affects those who are of a Viscount rank or higher.",
				"Increases clan members' M. Atk by 12%. It only affects those who are of a Viscount rank or higher."});
		_clanSkillDecriptions.put(379, new String[] {"Increases clan members' M. Def by 6%. It only affects those who are of an Heir rank or higher.",
				"Increases clan members' M. Def by 10%. It only affects those who are of an Heir rank or higher.",
				"Increases clan members' M. Def by 12%. It only affects those who are of an Heir rank or higher."});
		_clanSkillDecriptions.put(380, new String[] {"Increases clan members' Accuracy by 1. It only affects those who are of a Baron rank or higher.",
				"Increases clan members' Accuracy by 2. It only affects those who are of a Baron rank or higher.",
				"Increases clan members' Accuracy by 3. It only affects those who are of a Baron rank or higher."});
		_clanSkillDecriptions.put(381, new String[] {"Increases clan members' Evasion by 1. It only affects those who are of a Baron rank or higher.",
				"Increases clan members' Evasion by 2. It only affects those who are of a Baron rank or higher.",
				"Increases clan members' Evasion by 3. It only affects those who are of a Baron rank or higher."});
		_clanSkillDecriptions.put(382, new String[] {"Increases clan members' Shield Defense by 12%. It only affects those who are of a Viscount rank or higher.",
				"Increases clan members' Shield Defense by 20%. It only affects those who are of a Viscount rank or higher.",
				"Increases clan members' Shield Defense by 24%. It only affects those who are of a Viscount rank or higher."});
		_clanSkillDecriptions.put(383, new String[] {"Increases clan members' Shield Defense by 24%. It only affects those who are of a Baron rank or higher.",
				"Increases clan members' Shield Defense by 40%. It only affects those who are of a Baron rank or higher.",
				"Increases clan members' Shield Defense by 48%. It only affects those who are of a Baron rank or higher."});
		_clanSkillDecriptions.put(384, new String[] {"Increases clan members' Resistance to Water/Wind attacks by 3. It only affects those who are of a Viscount rank or higher.",
				"Increases clan members' Resistance to Water/Wind attacks by 5. It only affects those who are of a Viscount rank or higher.",
				"Increases clan members' Resistance to Water/Wind attacks by 6. It only affects those who are of a Viscount rank or higher."});
		_clanSkillDecriptions.put(385, new String[] {"ncreases clan members' Resistance to Fire/Earth attacks by 3. It only affects those who are of a Viscount rank or higher.",
				"Increases clan members' Resistance to Fire/Earth attacks by 5. It only affects those who are of a Viscount rank or higher.",
				"Increases clan members' Resistance to Fire/Earth attacks by 6. It only affects those who are of a Viscount rank or higher."});
		_clanSkillDecriptions.put(386, new String[] {"Increases clan members' Resistance to Stun attacks by 12. It only affects those who are of a Viscount rank or higher.",
				"Increases clan members' Resistance to Stun attacks by 20. It only affects those who are of a Viscount rank or higher.",
				"Increases clan members' Resistance to Stun attacks by 24. It only affects those who are of a Viscount rank or higher."});
		_clanSkillDecriptions.put(387, new String[] {"Increases clan members' Resistance to Hold attacks by 12. It only affects those who are of a Viscount rank or higher.",
				"Increases clan members' Resistance to Hold attacks by 20. It only affects those who are of a Viscount rank or higher.",
				"Increases clan members' Resistance to Hold attacks by 24. It only affects those who are of a Viscount rank or higher."});
		_clanSkillDecriptions.put(388, new String[] {"Increases clan members' Resistance to Sleep attacks by 12. It only affects those who are of a Viscount rank or higher.",
				"Increases clan members' Resistance to Sleep attacks by 20. It only affects those who are of a Viscount rank or higher.",
				"Increases clan members' Resistance to Sleep attacks by 24. It only affects those who are of a Viscount rank or higher."});
		_clanSkillDecriptions.put(389, new String[] {"Increases clan members' Speed by 3. It only affects those who are of a Count rank or higher.",
				"Increases clan members' Speed by 5. It only affects those who are of a Count rank or higher.",
				"Increases clan members' Speed by 6. It only affects those who are of a Count rank or higher."});
		_clanSkillDecriptions.put(390, new String[] {"Decreases clan members' experience loss and the chance of other death penalties when killed by a monster or player. It only affects those who are of an Heir rank or higher.",
				"Decreases clan members' experience loss and the chance of other death penalties when killed by a monster or player. It only affects those who are of an Heir rank or higher.",
				"Decreases clan members' experience loss and the chance of other death penalties when killed by a monster or player. It only affects those who are of an Heir rank or higher."});
		_clanSkillDecriptions.put(391, new String[] {"Grants the privilege of Command Channel formation. It only effects Sage / Elder class and above."});
		_clanSkillDecriptions.put(590, new String[] {"The Max HP of clan members in residence increases by 222."});
		_clanSkillDecriptions.put(591, new String[] {"The Max CP of clan members in residence increases by 444."});
		_clanSkillDecriptions.put(592, new String[] {"The Max MP of clan members in residence increases by 168."});
		_clanSkillDecriptions.put(593, new String[] {"The HP Recovery Bonus of clan members in residence increases by 1.09."});
		_clanSkillDecriptions.put(594, new String[] {"CP recovery bonus of clan members in residence increases by 1.09."});
		_clanSkillDecriptions.put(595, new String[] {"The MP Recovery Bonus of clan members in residence increases by 0.47."});
		_clanSkillDecriptions.put(596, new String[] {"P. Atk. of clan members in residence increases by 34.6."});
		_clanSkillDecriptions.put(597, new String[] {"P. Def. of clan members in residence increases by 54.7."});
		_clanSkillDecriptions.put(598, new String[] {"M. Atk. of clan members in residence increases by 40.4."});
		_clanSkillDecriptions.put(599, new String[] {"The M. Def. of clan members in residence increases by 44."});
		_clanSkillDecriptions.put(600, new String[] {"Accuracy of clan members in residence increases by 4."});
		_clanSkillDecriptions.put(601, new String[] {"Evasion of clan members in residence increases by 4."});
		_clanSkillDecriptions.put(602, new String[] {"Shield Defense of clan members in residence increases by 54.7."});
		_clanSkillDecriptions.put(603, new String[] {"Shield Defense. of clan members in residence increases by 225."});
		_clanSkillDecriptions.put(604, new String[] {"Resistance to Water and Wind attacks of clan members in residence increases by 10."});
		_clanSkillDecriptions.put(605, new String[] {"Resistance to Fire and Earth attacks of clan members in residence increases by 10."});
		_clanSkillDecriptions.put(606, new String[] {"Resistance to Stun attacks of clan members in residence increases by 10."});
		_clanSkillDecriptions.put(607, new String[] {"Resistance to Hold attacks of clan members in residence increases by 10."});
		_clanSkillDecriptions.put(608, new String[] {"Resistance to Sleep attacks of clan members in residence increases by 10."});
		_clanSkillDecriptions.put(609, new String[] {"The Speed of clan members in residence increases by 6."});
		_clanSkillDecriptions.put(610, new String[] {"When a clan member within the residence is killed by PK/ordinary monster, the Exp. points consumption rate and the probability of incurring a death after-effect are decreased."});
		_clanSkillDecriptions.put(611, new String[] {"The corresponding troops' P. Atk. increase by 17.3.",
				"The corresponding troops' P. Atk. increase by 17.3 and Critical Rate increase by 15.",
				"The corresponding troops' P. Atk. increase by 17.3, Critical Rate increase by 15, and Critical Damage increase by 100."});
		_clanSkillDecriptions.put(612, new String[] {"The corresponding troops' P. Def. increase by 27.3.",
				"The corresponding troops' P. Def. increase by 27.3 and M. Def. increase by 17.6.",
				"The corresponding troops' P. Def. increase by 27.3, M. Def. increase by 17.6, and Shield Defense. increase by 6%."});
		_clanSkillDecriptions.put(613, new String[] {"The corresponding troops' Accuracy increase by 2.",
				"The corresponding troops' Accuracy increase by 2 and Evasion increase by 2.",
				"The corresponding troops' Accuracy increase by 2, Evasion increase by 2, and Speed increase by 3."});
		_clanSkillDecriptions.put(614, new String[] {"The corresponding troops' M. Def. increase by 17.",
				"The corresponding troops' M. Def. increase by 31.1.",
				"The corresponding troops' M. Def. increase by 44."});
		_clanSkillDecriptions.put(615, new String[] {"The corresponding troops' heal power increase by 20.",
				"The corresponding troops' heal power increase by 20 and Max MP increase by 30%.",
				"The corresponding troops' heal power increase by 20, Max MP increase by 30%, and MP consumption decreases by 5%."});
		_clanSkillDecriptions.put(616, new String[] {"The corresponding troops' M. Atk. increase by 7.17.",
				"The corresponding troops' M. Atk. increase by 19.32.",
				"The corresponding troops' M. Atk. increase by 19.32 and magic Critical Damage rate increases by 1%."});
		_clanSkillDecriptions.put(848, new String[] {"STR+1 / INT+1"});
		_clanSkillDecriptions.put(849, new String[] {"DEX+1 / WIT+1"});
		_clanSkillDecriptions.put(850, new String[] {"STR+1 / MEN+1"});
		_clanSkillDecriptions.put(851, new String[] {"CON+1 / MEN+1"});
		_clanSkillDecriptions.put(852, new String[] {"DEX+1 / MEN+1"});
		_clanSkillDecriptions.put(853, new String[] {"CON+1 / INT+1"});
		_clanSkillDecriptions.put(854, new String[] {"DEX+1 / INT+1"});
		_clanSkillDecriptions.put(855, new String[] {"STR+1 / WIT+1"});
		_clanSkillDecriptions.put(856, new String[] {"CON+1 / WIT+1"});
	}
}
