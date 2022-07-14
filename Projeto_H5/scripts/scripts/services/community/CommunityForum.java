package services.community;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javolution.util.FastList;
import l2mv.gameserver.Config;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.cache.ImagesCache;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.handler.bbs.CommunityBoardManager;
import l2mv.gameserver.handler.bbs.ICommunityBoardHandler;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.forum.ForumBoard;
import l2mv.gameserver.model.entity.forum.ForumBoardType;
import l2mv.gameserver.model.entity.forum.ForumHandler;
import l2mv.gameserver.model.entity.forum.ForumMember;
import l2mv.gameserver.model.entity.forum.ForumMemberGroup;
import l2mv.gameserver.model.entity.forum.ForumMembersHolder;
import l2mv.gameserver.model.entity.forum.ForumPost;
import l2mv.gameserver.model.entity.forum.ForumTopic;
import l2mv.gameserver.network.serverpackets.Say2;
import l2mv.gameserver.network.serverpackets.ShowBoard;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.Util;

public class CommunityForum implements ScriptFile, ICommunityBoardHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(CommunityForum.class);

	private static final SimpleDateFormat POST_TIME = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	@Override
	public void onLoad()
	{
		if (Config.COMMUNITYBOARD_ENABLED && !ConfigHolder.getBool("EnableMergeCommunity") && ConfigHolder.getBool("AllowForum"))
		{
			LOG.info("CommunityBoard: Forum loaded.");
			CommunityBoardManager.getInstance().registerHandler(this);
		}
	}

	@Override
	public void onReload()
	{
		if (Config.COMMUNITYBOARD_ENABLED && !ConfigHolder.getBool("EnableMergeCommunity") && ConfigHolder.getBool("AllowForum"))
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
			"_bbsforum"
		};
	}

	private void useForumBypass(Player player, String bypass, Object... params)
	{
		onBypassCommand(player, "_bbsforum_" + bypass + (params.length > 0 ? "_" : "") + Util.joinArrayWithCharacter(params, "_"));
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		final StringTokenizer st = new StringTokenizer(bypass, "_");
		st.nextToken();

		if (st.hasMoreTokens())
		{
			switch (st.nextToken())
			{
			case "main":
			{
				showMainPage(player);
				break;
			}
			case "connectAccount":
			{
				showConnectAccountPage(player);
				break;
			}
			case "finalizeConnectAccount":
			{
				final String nickName = st.nextToken().trim();
				final String password = st.nextToken().trim();
				onAccountConnectFinalize(player, nickName, password);
				break;
			}
			case "connectAccountError":
			{
				final String errorMsg = st.nextToken().trim();
				showConnectAccountErrorPage(player, errorMsg);
				break;
			}
			case "board":
			{
				final ForumBoard board = ForumHandler.getInstance().getBoardByIndex(Integer.parseInt(st.nextToken().trim()));
				final int page = (st.hasMoreTokens() ? Integer.parseInt(st.nextToken().trim()) : 0);
				showBoardPage(player, board, page);
				break;
			}
			case "post":
			{
				final ForumPost post = ForumHandler.getInstance().getPostById(Integer.parseInt(st.nextToken()));
				if (ArrayUtils.contains(ConfigHolder.getIntArray("ForumOnlyForumTopics"), post.getTopic().getTopicId()))
				{
					player.sendMessage(ConfigHolder.getString("ForumOnlyForumTopicMsg"));
					final ForumBoardType boardType = post.getTopic().getBoard().getType();
					useForumBypass(player, "board", boardType.getBoardIndex());
					break;
				}
				post.getTopic().incViews();
				showPostPage(player, post);
				break;
			}
			case "startNewTopic":
			{
				if (player.getForumMember() == null)
				{
					sendErrorMsg(player, "You need to Log In to .forum first!");
					player.sendActionFailed();
					break;
				}
				final ForumBoard board = ForumHandler.getInstance().getBoardByIndex(Integer.parseInt(st.nextToken().trim()));
				showNewTopicPage(player, board);
				break;
			}
			case "startNewReply":
			{
				if (player.getForumMember() == null)
				{
					sendErrorMsg(player, "You need to Log In to .forum first!");
					player.sendActionFailed();
					break;
				}
				final ForumTopic topic = ForumHandler.getInstance().getTopicById(Integer.parseInt(st.nextToken()));
				final String communityArgsValue = Util.getAllTokens(st);
				showNewReplyPage(player, topic, communityArgsValue);
				break;
			}
			case "finalizeNewTopic":
			{
				final ForumBoard board = ForumHandler.getInstance().getBoardByIndex(Integer.parseInt(st.nextToken().trim()));
				final String subject = st.nextToken();
				final String message = Util.getAllTokens(st);
				onNewTopicFinalize(player, board, subject, message);
				break;
			}
			case "finalizeNewReply":
			{
				final ForumTopic topic = ForumHandler.getInstance().getTopicById(Integer.parseInt(st.nextToken()));
				final String message = Util.getAllTokens(st);
				onNewReplyFinalize(player, topic, message);
				break;
			}
			}
		}
	}

	private void showMainPage(Player player)
	{
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "forum/main.htm", player);

		// Announcements board
		final ForumBoard announcementsBoard = ForumHandler.getInstance().getBoardByIndex(ForumBoardType.ANNOUNCEMENTS.getBoardIndex());
		html = html.replace("%announceBoardIndex%", String.valueOf(announcementsBoard.getType().getBoardIndex()));
		html = html.replace("%announcePostsCount%", String.valueOf(announcementsBoard.getPostsCount()));
		html = html.replace("%announceTopicsCount%", String.valueOf(announcementsBoard.getTopicsCount()));

		if (announcementsBoard.getLastPost() != null)
		{
			html = html.replace("%announceLastPost%", "<font color=" + announcementsBoard.getLastPost().getWriter().getMemberGroup().getColor() + ">" + announcementsBoard.getLastPost().getWriter().getMemberName() + "</font>");
			html = html.replace("%announceTime%", "<font color=63bea6>" + POST_TIME.format(announcementsBoard.getLastPost().getDate()) + "</font>");
		}
		else
		{
			html = html.replace("%announceLastPost%", "");
			html = html.replace("%announceTime%", "");
		}

		// Events board
		final ForumBoard eventsBoard = ForumHandler.getInstance().getBoardByIndex(ForumBoardType.EVENTS.getBoardIndex());
		html = html.replace("%eventBoardIndex%", String.valueOf(eventsBoard.getType().getBoardIndex()));
		html = html.replace("%eventPostsCount%", String.valueOf(eventsBoard.getPostsCount()));
		html = html.replace("%eventTopicsCount%", String.valueOf(eventsBoard.getTopicsCount()));

		if (eventsBoard.getLastPost() != null)
		{
			html = html.replace("%eventLastPost%", "<font color=" + eventsBoard.getLastPost().getWriter().getMemberGroup().getColor() + ">" + eventsBoard.getLastPost().getWriter().getMemberName() + "</font>");
			html = html.replace("%eventTime%", "<font color=63bea6>" + POST_TIME.format(eventsBoard.getLastPost().getDate()) + "</font>");
		}
		else
		{
			html = html.replace("%eventLastPost%", "");
			html = html.replace("%eventTime%", "");
		}

		// Changelog board
		final ForumBoard changelogBoard = ForumHandler.getInstance().getBoardByIndex(ForumBoardType.RECRUITMENT.getBoardIndex());
		html = html.replace("%changelogBoardIndex%", String.valueOf(changelogBoard.getType().getBoardIndex()));
		html = html.replace("%changelogPostsCount%", String.valueOf(changelogBoard.getPostsCount()));
		html = html.replace("%changelogTopicsCount%", String.valueOf(changelogBoard.getTopicsCount()));

		if (changelogBoard.getLastPost() != null)
		{
			html = html.replace("%changelogLastPost%", "<font color=" + changelogBoard.getLastPost().getWriter().getMemberGroup().getColor() + ">" + changelogBoard.getLastPost().getWriter().getMemberName() + "</font>");
			html = html.replace("%changelogTime%", "<font color=63bea6>" + POST_TIME.format(changelogBoard.getLastPost().getDate()) + "</font>");
		}
		else
		{
			html = html.replace("%changelogLastPost%", "");
			html = html.replace("%changelogTime%", "");
		}

		// General board
		final ForumBoard generalBoard = ForumHandler.getInstance().getBoardByIndex(ForumBoardType.GENERAL_DISCUSSION.getBoardIndex());
		html = html.replace("%generalBoardIndex%", String.valueOf(generalBoard.getType().getBoardIndex()));
		html = html.replace("%generalPostsCount%", String.valueOf(generalBoard.getPostsCount()));
		html = html.replace("%generalTopicsCount%", String.valueOf(generalBoard.getTopicsCount()));

		if (generalBoard.getLastPost() != null)
		{
			html = html.replace("%generalLastPost%", "<font color=" + generalBoard.getLastPost().getWriter().getMemberGroup().getColor() + ">" + generalBoard.getLastPost().getWriter().getMemberName() + "</font>");
			html = html.replace("%generalTime%", "<font color=63bea6>" + POST_TIME.format(generalBoard.getLastPost().getDate()) + "</font>");
		}
		else
		{
			html = html.replace("%generalLastPost%", "");
			html = html.replace("%generalTime%", "");
		}

		// Clans board
		final ForumBoard clansBoard = ForumHandler.getInstance().getBoardByIndex(ForumBoardType.SUGGESTIONS.getBoardIndex());
		html = html.replace("%clanBoardIndex%", String.valueOf(clansBoard.getType().getBoardIndex()));
		html = html.replace("%clanPostsCount%", String.valueOf(clansBoard.getPostsCount()));
		html = html.replace("%clanTopicsCount%", String.valueOf(clansBoard.getTopicsCount()));

		if (clansBoard.getLastPost() != null)
		{
			html = html.replace("%clanLastPost%", "<font color=" + clansBoard.getLastPost().getWriter().getMemberGroup().getColor() + ">" + clansBoard.getLastPost().getWriter().getMemberName() + "</font>");
			html = html.replace("%clanTime%", "<font color=63bea6>" + POST_TIME.format(clansBoard.getLastPost().getDate()) + "</font>");
		}
		else
		{
			html = html.replace("%clanLastPost%", "");
			html = html.replace("%clanTime%", "");
		}

		/*
		 * // Marketplace board
		 * final ForumBoard marketBoard = ForumHandler.getInstance().getBoardByIndex(ForumBoardType.BUG_TRACKER.getBoardIndex());
		 * html = html.replace("%marketBoardIndex%", String.valueOf(marketBoard.getType().getBoardIndex()));
		 * html = html.replace("%marketPostsCount%", String.valueOf(marketBoard.getPostsCount()));
		 * html = html.replace("%marketTopicsCount%", String.valueOf(marketBoard.getTopicsCount()));
		 * if (marketBoard.getLastPost() != null)
		 * {
		 * html = html.replace("%marketLastPost%", "<font color=" + marketBoard.getLastPost().getWriter().getMemberGroup().getColor() + ">" +
		 * marketBoard.getLastPost().getWriter().getMemberName() + "</font>");
		 * html = html.replace("%marketTime%", "<font color=63bea6>" + POST_TIME.format(marketBoard.getLastPost().getDate()) + "</font>");
		 * }
		 * else
		 * {
		 * html = html.replace("%marketLastPost%", "");
		 * html = html.replace("%marketTime%", "");
		 * }
		 */
		// Bug Tracker board
		final ForumBoard bugTrackerBoard = ForumHandler.getInstance().getBoardByIndex(ForumBoardType.BUG_TRACKER.getBoardIndex());
		html = html.replace("%bugBoardIndex%", String.valueOf(bugTrackerBoard.getType().getBoardIndex()));
		html = html.replace("%bugPostsCount%", String.valueOf(bugTrackerBoard.getPostsCount()));
		html = html.replace("%bugTopicsCount%", String.valueOf(bugTrackerBoard.getTopicsCount()));

		if (bugTrackerBoard.getLastPost() != null)
		{
			html = html.replace("%bugLastPost%", "<font color=" + bugTrackerBoard.getLastPost().getWriter().getMemberGroup().getColor() + ">" + bugTrackerBoard.getLastPost().getWriter().getMemberName() + "</font>");
			html = html.replace("%bugTime%", "<font color=63bea6>" + POST_TIME.format(bugTrackerBoard.getLastPost().getDate()) + "</font>");
		}
		else
		{
			html = html.replace("%bugLastPost%", "");
			html = html.replace("%bugTime%", "");
		}

		/*
		 * // Suggestions board
		 * final ForumBoard suggestionsBoard = ForumHandler.getInstance().getBoardByIndex(ForumBoardType.SUGGESTIONS.getBoardIndex());
		 * html = html.replace("%bugBoardIndex%", String.valueOf(suggestionsBoard.getType().getBoardIndex()));
		 * html = html.replace("%bugPostsCount%", String.valueOf(suggestionsBoard.getPostsCount()));
		 * html = html.replace("%bugTopicsCount%", String.valueOf(suggestionsBoard.getTopicsCount()));
		 * if (suggestionsBoard.getLastPost() != null)
		 * {
		 * html = html.replace("%bugLastPost%", "<font color=" + suggestionsBoard.getLastPost().getWriter().getMemberGroup().getColor() + ">" +
		 * suggestionsBoard.getLastPost().getWriter().getMemberName() + "</font>");
		 * html = html.replace("%bugTime%", "<font color=63bea6>" + POST_TIME.format(suggestionsBoard.getLastPost().getDate()) + "</font>");
		 * }
		 * else
		 * {
		 * html = html.replace("%bugLastPost%", "");
		 * html = html.replace("%bugTime%", "");
		 * }
		 */

		// Replacements
		html = html.replace("%header%", HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "forum/header.htm", player));
		html = html.replace("%shortServerName%", Config.SHORT_SERVER_NAME);
		html = html.replace("%refreshBypass%", "bypass _bbsforum_main");

		// Header
		final StringBuilder header = new StringBuilder();
		if (player.getForumMember() != null)
		{
			header.append("<center>");
			header.append("<font color=565368>You are logged in as </font><font color=" + player.getForumMember().getMemberGroup().getColor() + ">" + player.getForumMember().getMemberName() + "</font>");
			header.append("</center>");
		}
		else
		{
			header.append("<button action=\"bypass _bbsforum_connectAccount\" value=\"Log in!\" width=150 height=22 back=Btns.btn_simple_red_150x22_Down fore=Btns.btn_simple_red_150x22>");
		}
		html = html.replace("%header%", header.toString());

		ImagesCache.getInstance().sendUsedImages(html, player);
		ShowBoard.separateAndSend(html, player);
	}

	private void showConnectAccountPage(Player player)
	{
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "forum/connectAccount.htm", player);

		// Min Posts
		final StringBuilder minPosts = new StringBuilder();
		if (ConfigHolder.getInt("ForumAccountMinPosts") > 0)
		{
			minPosts.append("<font name=hs12 color=565368>Requirements:</font>");
			minPosts.append("<br1>");
			minPosts.append("<font color=63bea6>Account MUST have at least " + ConfigHolder.getString("ForumAccountMinPosts") + " Post" + (ConfigHolder.getInt("ForumAccountMinPosts") > 1 ? "s" : "") + "!</font>");
		}
		else
		{
			html = html.replace("%minPostsSpace%", "<br><br><br><br><br>");
		}

		// Account price
		final StringBuilder accountPrice = new StringBuilder();
		if (ConfigHolder.getLong("ForumConnectAccountPrice") > 0)
		{
			accountPrice.append("<font name=hs12 color=565368>Price:</font>");
			accountPrice.append("<br1>");
			accountPrice.append("<font color=63bea6>" + ConfigHolder.getLong("ForumConnectAccountPrice") + " " + ItemHolder.getInstance().getTemplate(ConfigHolder.getInt("ForumConnectAccountPriceId")).getName() + (ConfigHolder.getLong("ForumConnectAccountPrice") > 1 ? "s" : "") + "</font>");
			accountPrice.append("<br>");
		}
		else
		{
			html = html.replace("%accountPriceSpace%", "<br><br><br><br><br>");
		}

		// Replacements
		html = html.replace("%header%", HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "forum/header.htm", player));
		html = html.replace("%shortServerName%", Config.SHORT_SERVER_NAME);
		html = html.replace("%refreshBypass%", "bypass _bbsforum_connectAccount");
		html = html.replace("%minPosts%", minPosts.toString());
		html = html.replace("%accountPrice%", accountPrice.toString());

		// Header
		final StringBuilder header = new StringBuilder();
		if (player.getForumMember() != null)
		{
			header.append("<center>");
			header.append("<font color=565368>You are logged in as </font><font color=" + player.getForumMember().getMemberGroup().getColor() + ">" + player.getForumMember().getMemberName() + "</font>");
			header.append("</center>");
		}
		else
		{
			header.append("<button action=\"bypass _bbsforum_connectAccount\" value=\"Log in!\" width=150 height=22 back=Btns.btn_simple_red_150x22_Down fore=Btns.btn_simple_red_150x22>");
		}
		html = html.replace("%header%", header.toString());

		ImagesCache.getInstance().sendUsedImages(html, player);
		ShowBoard.separateAndSend(html, player);
	}

	private void showConnectAccountErrorPage(Player player, String errorMsg)
	{
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "forum/connectAccountError.htm", player);

		// Replacements
		html = html.replace("%header%", HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "forum/header.htm", player));
		html = html.replace("%shortServerName%", Config.SHORT_SERVER_NAME);
		html = html.replace("%refreshBypass%", "bypass _bbsforum_connectAccount");
		html = html.replace("%errorMsg%", errorMsg);

		// Header
		final StringBuilder header = new StringBuilder();
		if (player.getForumMember() != null)
		{
			header.append("<center>");
			header.append("<font color=565368>You are logged in as </font><font color=" + player.getForumMember().getMemberGroup().getColor() + ">" + player.getForumMember().getMemberName() + "</font>");
			header.append("</center>");
		}
		else
		{
			header.append("<button action=\"bypass _bbsforum_connectAccount\" value=\"Log in!\" width=150 height=22 back=Btns.btn_simple_red_150x22_Down fore=Btns.btn_simple_red_150x22>");
		}
		html = html.replace("%header%", header.toString());

		ImagesCache.getInstance().sendUsedImages(html, player);
		ShowBoard.separateAndSend(html, player);
	}

	private void showBoardPage(Player player, ForumBoard board, int page)
	{
		final int topicsPerPage = 7;
		final int boardIndex = board.getType().getBoardIndex();

		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "forum/board.htm", player);

		// Topics
		final StringBuilder topics = new StringBuilder();
		final List<ForumTopic> boardTopics = board.getTopics();
		int index = 0;
		for (int x = (page * topicsPerPage); x < (((page * topicsPerPage) + topicsPerPage) - 1); x++)
		{
			if (index > 0)
			{
				topics.append("<tr>");
				topics.append("<td width=773 height=6>");
				topics.append("<table width=773 height=6>");
				topics.append("<tr>");
				topics.append("<td width=9 height=6>");
				topics.append("<br1>");
				topics.append("</td>");
				topics.append("<td width=753 height=6>");
				topics.append("<table cellspacing=0 border=0 width=743 height=1 bgcolor=1d4482>");
				topics.append("<tr>");
				topics.append("<td width=743 height=1>");
				topics.append("<br1>");
				topics.append("</td>");
				topics.append("</tr>");
				topics.append("</table>");
				topics.append("</td>");
				topics.append("<td width=10 height=6>");
				topics.append("<br1>");
				topics.append("</td>");
				topics.append("</tr>");
				topics.append("</table>");
				topics.append("</td>");
				topics.append("</tr>");
			}

			if (boardTopics.size() > x)
			{
				final ForumTopic topic = boardTopics.get(x);
				final ForumPost firstPost = topic.getFirstPost();
				final ForumPost lastPost = topic.getLastPost();

				topics.append("<tr>");
				topics.append("<td width=773 height=50>");
				topics.append("<table width=773 height=50>");
				topics.append("<tr>");
				topics.append("<td width=9 height=50>");
				topics.append("<br>");
				topics.append("</td>");
				topics.append("<td width=753 height=50>");
				topics.append("<table cellspacing=0 border=0 width=743 height=46 bgcolor=101320>");
				topics.append("<tr>");
				topics.append("<td width=5 height=8>");
				topics.append("<br1>");
				topics.append("</td>");
				topics.append("<td width=38 height=8>");
				topics.append("<br1>");
				topics.append("</td>");
				topics.append("<td width=400 height=8>");
				topics.append("<br1>");
				topics.append("</td>");
				topics.append("<td width=120 height=8>");
				topics.append("<br1>");
				topics.append("</td>");
				topics.append("<td width=180 height=8>");
				topics.append("<br1>");
				topics.append("</td>");
				topics.append("</tr>");
				topics.append("<tr>");
				topics.append("<td width=5 height=41>");
				topics.append("<br1>");
				topics.append("</td>");
				topics.append("<td width=38 height=41>");
				topics.append("<table border=0 cellspacing=0 cellpadding=0 width=32 height=32 background=");
				if (topic.isForumOnlyTopic())
				{
					topics.append("L2UI_CT1.SystemMenuWnd_df_Homepage>");
				}
				else if (topic.isSticky())
				{
					topics.append("L2UI_CT1.ICON_DF_Exclamation>");
				}
				else if (topic.isLocked())
				{
					topics.append("Crest.crest_1_1009>");
				}
				else
				{
					topics.append("L2UI_CT1.Icon_DF_MenuWnd_Character>");
				}
				topics.append("<tr>");
				topics.append("<td width=32 height=32 align=center valign=top>");
				topics.append("<button action=\"bypass _bbsforum_post_" + firstPost.getPostId() + "\" width=34 height=34 back=L2UI_CT1.ItemWindow_DF_Frame_Down fore=L2UI_CT1.ItemWindow_DF_Frame />");
				topics.append("</td>");
				topics.append("</tr>");
				topics.append("</table>");
				topics.append("</td>");
				topics.append("<td fixwidth=400 height=41>");
				topics.append("<font name=hs12 color=63bea6>");
				if (firstPost.getSubject().length() <= 45)
				{
					topics.append(firstPost.getSubject());
				}
				else
				{
					topics.append(firstPost.getSubject().substring(0, 45));
				}
				topics.append("</font><br1>");
				topics.append("<font color=565368>Started by:</font> <font color=" + firstPost.getWriter().getMemberGroup().getColor() + ">" + firstPost.getWriter().getMemberName() + "</font>");
				topics.append("</td>");
				topics.append("<td width=120 height=41>");
				topics.append("<table cellspacing=0 border=0 width=120 height=41>");
				topics.append("<tr>");
				topics.append("<td width=120 height=16>");
				topics.append("<font color=565368>Replies:</font> <font color=63bea6>" + (topic.getPostsCount() - 1) + "</font>");
				topics.append("</td>");
				topics.append("</tr>");
				topics.append("<tr>");
				topics.append("<td width=120 height=15>");
				topics.append("<font color=9fa0a2>Views:</font> <font color=63bea6>" + topic.getViews() + "</font>");
				topics.append("</td>");
				topics.append("</tr>");
				topics.append("<tr>");
				topics.append("<td width=120 height=10>");
				topics.append("<br1>");
				topics.append("</td>");
				topics.append("</tr>");
				topics.append("</table>");
				topics.append("</td>");
				topics.append("<td width=180 height=41>");
				topics.append("<table cellspacing=0 border=0 width=180 height=41>");
				topics.append("<tr>");
				topics.append("<td width=120 height=16>");
				topics.append("<font color=565368>Last Post by:</font> <font color=" + lastPost.getWriter().getMemberGroup().getColor() + ">" + lastPost.getWriter().getMemberName() + "</font>");
				topics.append("</td>");
				topics.append("</tr>");
				topics.append("<tr>");
				topics.append("<td width=120 height=15>");
				topics.append("<font color=9fa0a2>Time:</font> <font color=63bea6>" + POST_TIME.format(lastPost.getDate()) + "</font>");
				topics.append("</td>");
				topics.append("</tr>");
				topics.append("<tr>");
				topics.append("<td width=120 height=10>");
				topics.append("<br1>");
				topics.append("</td>");
				topics.append("</tr>");
				topics.append("</table>");
				topics.append("</td>");
				topics.append("</tr>");
				topics.append("</table>");
				topics.append("</td>");
				topics.append("<td width=10 height=50>");
				topics.append("<br>");
				topics.append("</td>");
				topics.append("</tr>");
				topics.append("</table>");
				topics.append("</td>");
				topics.append("</tr>");
			}
			else
			{
				topics.append("<tr>");
				topics.append("<td width=773 height=50>");
				topics.append("<table width=773 height=50>");
				topics.append("<tr>");
				topics.append("<td width=9 height=50>");
				topics.append("<br>");
				topics.append("</td>");
				topics.append("<td width=753 height=50>");
				topics.append("<table cellspacing=0 border=0 width=746 height=46 bgcolor=101320>");
				topics.append("<tr>");
				topics.append("<td width=5 height=8>");
				topics.append("<br1>");
				topics.append("</td>");
				topics.append("<td width=38 height=8>");
				topics.append("<br1>");
				topics.append("</td>");
				topics.append("<td width=400 height=8>");
				topics.append("<br1>");
				topics.append("</td>");
				topics.append("<td width=120 height=8>");
				topics.append("<br1>");
				topics.append("</td>");
				topics.append("<td width=180 height=8>");
				topics.append("<br1>");
				topics.append("</td>");
				topics.append("</tr>");
				topics.append("<tr>");
				topics.append("<td width=5 height=41>");
				topics.append("<br1>");
				topics.append("</td>");
				topics.append("<td width=38 height=41>");
				topics.append("<br>");
				topics.append("</td>");
				topics.append("<td width=400 height=41>");
				topics.append("<br>");
				topics.append("</td>");
				topics.append("<td width=120 height=41>");
				topics.append("<br>");
				topics.append("</td>");
				topics.append("<td width=180 height=41>");
				topics.append("<br>");
				topics.append("</td>");
				topics.append("</tr>");
				topics.append("</table>");
				topics.append("</td>");
				topics.append("<td width=10 height=50>");
				topics.append("<br>");
				topics.append("</td>");
				topics.append("</tr>");
				topics.append("</table>");
				topics.append("</td>");
				topics.append("</tr>");
			}

			index++;
		}

		// First Pages
		final StringBuilder firstPages = new StringBuilder();
		if (page > 0)
		{
			firstPages.append("<td fixwidth=45 height=25>");
			firstPages.append("<font color=565368>Go to: </font>");
			firstPages.append("</td>");
			firstPages.append("<td fixwidth=60 height=25>");
			firstPages.append("<font color=565368><a action=\"bypass _bbsforum_board_" + boardIndex + "\">First Page</a></font>");
			firstPages.append("</td>");
			firstPages.append("<td fixwidth=25 height=25>");
			firstPages.append("<font color=565368><<</font>");
			firstPages.append("</td>");
			firstPages.append("<td fixwidth=115 height=25>");
			firstPages.append("<font color=565368><a action=\"bypass _bbsforum_board_" + boardIndex + "_" + (page - 1) + "\">Previous Page</a></font>");
			firstPages.append("</td>");
		}
		else
		{
			firstPages.append("<td fixwidth=45 height=25>");
			firstPages.append("<br>");
			firstPages.append("</td>");
			firstPages.append("<td fixwidth=60 height=25>");
			firstPages.append("<br>");
			firstPages.append("</td>");
			firstPages.append("<td fixwidth=25 height=25>");
			firstPages.append("<br>");
			firstPages.append("</td>");
			firstPages.append("<td fixwidth=115 height=25>");
			firstPages.append("<br>");
			firstPages.append("</td>");
		}

		// New Topic
		if (board.canWriteNewTopic(player.getForumMember()))
		{
			html = html.replace("%newTopic%", "<button action=\"bypass _bbsforum_startNewTopic_" + boardIndex + "\" value=\"   Create New Topic\" width=200 height=32 back=Btns.btn_ornaments_confirmed_red_200x32_down fore=Btns.btn_ornaments_confirmed_red_200x32>");
		}
		else
		{
			html = html.replace("%newTopic%", "<br>");
		}

		// Last Pages
		final StringBuilder lastPages = new StringBuilder();
		if (boardTopics.size() > ((page + 1) * topicsPerPage))
		{
			lastPages.append("<td fixwidth=105 height=25 align=right>");
			lastPages.append("<font color=565368>Go to: </font>");
			lastPages.append("</td>");
			lastPages.append("<td fixwidth=60 height=25>");
			lastPages.append("<font color=565368><a action=\"bypass _bbsforum_board_" + boardIndex + "_" + (page + 1) + "\">Next Page</a></font>");
			lastPages.append("</td>");
			lastPages.append("<td fixwidth=25 height=25>");
			lastPages.append("<font color=565368>>></font>");
			lastPages.append("</td>");
			lastPages.append("<td fixwidth=55 height=25>");
			lastPages.append("<font color=565368><a action=\"bypass _bbsforum_board_" + boardIndex + "_" + ((int) Math.ceil(boardTopics.size() / topicsPerPage) - 1) + "\">Last Page</a></font>");
			lastPages.append("</td>");
		}
		else
		{
			lastPages.append("<td fixwidth=105 height=25 align=right>");
			lastPages.append("<br>");
			lastPages.append("</td>");
			lastPages.append("<td fixwidth=60 height=25>");
			lastPages.append("<br>");
			lastPages.append("</td>");
			lastPages.append("<td fixwidth=25 height=25>");
			lastPages.append("<br>");
			lastPages.append("</td>");
			lastPages.append("<td fixwidth=55 height=25>");
			lastPages.append("<br>");
			lastPages.append("</td>");
		}

		// Replacements
		html = html.replace("%header%", HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "forum/header.htm", player));
		html = html.replace("%shortServerName%", Config.SHORT_SERVER_NAME);
		html = html.replace("%refreshBypass%", "bypass _bbsforum_board_" + boardIndex + "_" + page);
		html = html.replace("%boardName%", board.getType().getNiceName());
		html = html.replace("%topics%", topics.toString());
		html = html.replace("%firstPages%", firstPages.toString());
		html = html.replace("%lastPages%", lastPages.toString());

		// Header
		final StringBuilder header = new StringBuilder();
		if (player.getForumMember() != null)
		{
			header.append("<center>");
			header.append("<font color=565368>You are logged in as </font><font color=" + player.getForumMember().getMemberGroup().getColor() + ">" + player.getForumMember().getMemberName() + "</font>");
			header.append("</center>");
		}
		else
		{
			header.append("<button action=\"bypass _bbsforum_connectAccount\" value=\"Log in!\" width=150 height=22 back=Btns.btn_simple_red_150x22_Down fore=Btns.btn_simple_red_150x22>");
		}
		html = html.replace("%header%", header.toString());

		ImagesCache.getInstance().sendUsedImages(html, player);
		ShowBoard.separateAndSend(html, player);
	}

	private void showPostPage(Player player, ForumPost post)
	{
		final ForumTopic topic = post.getTopic();
		final ForumBoard board = topic.getBoard();
		final int boardIndex = board.getType().getBoardIndex();
		final int topicIndex = board.getTopics().indexOf(topic);
		final int topicsPerPage = 7;
		final int page = (int) Math.floor((topicIndex / topicsPerPage));
		final int indexOfPost = topic.getIndexOfPost(post);
		final List<ForumPost> allPosts = topic.getPosts();

		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "forum/post.htm", player);

		// Posts
		final StringBuilder posts = new StringBuilder();
		posts.append("<font name=hs12 color=" + post.getWriter().getMemberGroup().getColor() + ">" + post.getWriter().getMemberName() + "</font><br1>");
		posts.append("<font color=565368>Posts: " + post.getWriter().getPostCount() + "<br>");
		if (post.getWriter().getOnlineOwners().size() > 0)
		{
			posts.append("<font color=565368>Online Account Owners:<br1></font>");
			for (Player owner : post.getWriter().getOnlineOwners())
			{
				posts.append("- <font color=6dd507>" + owner.getName() + "</font><br1>");
			}
		}
		posts.append("</font>");

		// Message
		html = html.replace("%message%", CommunityForum.convertQuotes(post.getMessage(), "<table cellspacing=8><tr><td fixwidth=568><table width=568 cellspacing=3 bgcolor=16595a><tr><td fixwidth=568><font color=63bea6>Quote By: ", "</font><br1><font color=919191>", "</font></td></tr></table></td></tr></table><br1>"));

		// Quote
		if (topic.checkCanAddPost(player.getForumMember()))
		{
			html = html.replace("%quote%", "<button action=\"bypass _bbsforum_startNewReply_" + topic.getTopicId() + "_" + CommunityForum.getQuotePost(post) + "\" value=\"Quote\" width=110 height=28 back=Btns.btn_simple_red_110x28_down fore=Btns.btn_simple_red_110x28>");
		}
		else
		{
			html = html.replace("%quote%", "<br>");
		}

		// First Pages
		final StringBuilder firstPages = new StringBuilder();
		if (indexOfPost > 0)
		{
			firstPages.append("<td fixwidth=35 height=25>");
			firstPages.append("<font color=565368>Go to: </font>");
			firstPages.append("</td>");
			firstPages.append("<td fixwidth=45 height=25>");
			firstPages.append("<font color=565368><a action=\"bypass _bbsforum_post_" + allPosts.get(0).getPostId() + "\">First Post</a></font>");
			firstPages.append("</td>");
			firstPages.append("<td fixwidth=20 height=25>");
			firstPages.append("<font color=565368><<</font>");
			firstPages.append("</td>");
			firstPages.append("<td fixwidth=85 height=25>");
			firstPages.append("<font color=565368><a action=\"bypass _bbsforum_post_" + allPosts.get(indexOfPost - 1).getPostId() + "\">Previous Post</a></font>");
			firstPages.append("</td>");
		}
		else
		{
			firstPages.append("<td fixwidth=35 height=25>");
			firstPages.append("<br>");
			firstPages.append("</td>");
			firstPages.append("<td fixwidth=45 height=25>");
			firstPages.append("<br>");
			firstPages.append("</td>");
			firstPages.append("<td fixwidth=20 height=25>");
			firstPages.append("<br>");
			firstPages.append("</td>");
			firstPages.append("<td fixwidth=85 height=25>");
			firstPages.append("<br>");
			firstPages.append("</td>");
		}

		// New post
		if (topic.checkCanAddPost(player.getForumMember()))
		{
			html = html.replace("%newPost%", "<button action=\"bypass _bbsforum_startNewReply_" + topic.getTopicId() + "\" value=\"Reply\" width=200 height=32 back=Btns.btn_ornaments_confirmed_red_200x32_Down fore=Btns.btn_ornaments_confirmed_red_200x32>");
		}
		else
		{
			html = html.replace("%newPost%", "<br>");
		}

		// Last Pages
		final StringBuilder lastPages = new StringBuilder();
		if (indexOfPost + 1 < topic.getPostsCount())
		{
			lastPages.append("<td fixwidth=79 height=25 align=right>");
			lastPages.append("<font color=565368>Go to: </font>");
			lastPages.append("</td>");
			lastPages.append("<td fixwidth=45 height=25>");
			lastPages.append("<font color=565368><a action=\"bypass _bbsforum_post_" + allPosts.get(indexOfPost + 1).getPostId() + "\">Next Post</a></font>");
			lastPages.append("</td>");
			lastPages.append("<td fixwidth=20 height=25>");
			lastPages.append("<font color=565368>>></font>");
			lastPages.append("</td>");
			lastPages.append("<td fixwidth=45 height=25>");
			lastPages.append("<font color=565368><a action=\"bypass _bbsforum_post_" + allPosts.get(allPosts.size() - 1).getPostId() + "\">Last Post</a></font>");
			lastPages.append("</td>");
		}
		else
		{
			lastPages.append("<td fixwidth=79 height=25 align=right>");
			lastPages.append("<br>");
			lastPages.append("</td>");
			lastPages.append("<td fixwidth=45 height=25>");
			lastPages.append("<br>");
			lastPages.append("</td>");
			lastPages.append("<td fixwidth=20 height=25>");
			lastPages.append("<br>");
			lastPages.append("</td>");
			lastPages.append("<td fixwidth=45 height=25>");
			lastPages.append("<br>");
			lastPages.append("</td>");
		}

		// Replacements
		html = html.replace("%header%", HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "forum/header.htm", player));
		html = html.replace("%shortServerName%", Config.SHORT_SERVER_NAME);
		html = html.replace("%refreshBypass%", "bypass _bbsforum_post_" + post.getPostId());
		html = html.replace("%boardBypass%", "<font color=565368><a action=\"bypass _bbsforum_board_" + boardIndex + "_" + page + "\">" + board.getType().getNiceName() + "</a></font>");
		html = html.replace("%firstSubject%", topic.getFirstPost().getSubject());
		html = html.replace("%posts%", posts.toString());
		html = html.replace("%firstPages%", firstPages.toString());
		html = html.replace("%lastPages%", lastPages.toString());

		// Header
		final StringBuilder header = new StringBuilder();
		if (player.getForumMember() != null)
		{
			header.append("<center>");
			header.append("<font color=565368>You are logged in as </font><font color=" + player.getForumMember().getMemberGroup().getColor() + ">" + player.getForumMember().getMemberName() + "</font>");
			header.append("</center>");
		}
		else
		{
			header.append("<button action=\"bypass _bbsforum_connectAccount\" value=\"Log in!\" width=150 height=22 back=Btns.btn_simple_red_150x22_Down fore=Btns.btn_simple_red_150x22>");
		}
		html = html.replace("%header%", header.toString());

		ImagesCache.getInstance().sendUsedImages(html, player);
		ShowBoard.separateAndSend(html, player);
	}

	private void showNewTopicPage(Player player, ForumBoard board)
	{
		final int boardIndex = board.getType().getBoardIndex();

		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "forum/startNewTopic.htm", player);

		// Posts
		html = html.replace("%posterInfo%", "<font name=hs12 color=" + player.getForumMember().getMemberGroup().getColor() + ">" + player.getForumMember().getMemberName() + "</font>");
		html = html.replace("%postCount%", String.valueOf(player.getForumMember().getPostCount()));

		// Replacements
		html = html.replace("%header%", HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "forum/header.htm", player));
		html = html.replace("%shortServerName%", Config.SHORT_SERVER_NAME);
		html = html.replace("%refreshBypass%", "bypass _bbsforum_startNewTopic_" + board.getType().getBoardIndex());
		html = html.replace("%boardIndex%", String.valueOf(boardIndex));
		html = html.replace("%boardName%", board.getType().getNiceName());

		// Header
		final StringBuilder header = new StringBuilder();
		if (player.getForumMember() != null)
		{
			header.append("<center>");
			header.append("<font color=565368>You are logged in as </font><font color=" + player.getForumMember().getMemberGroup().getColor() + ">" + player.getForumMember().getMemberName() + "</font>");
			header.append("</center>");
		}
		else
		{
			header.append("<button action=\"bypass _bbsforum_connectAccount\" value=\"Log in!\" width=150 height=22 back=Btns.btn_simple_red_150x22_Down fore=Btns.btn_simple_red_150x22>");
		}
		html = html.replace("%header%", header.toString());

		ImagesCache.getInstance().sendUsedImages(html, player);
		ShowBoard.separateAndSend(html, player);
	}

	private void showNewReplyPage(Player player, ForumTopic topic, String communityArgsValue)
	{
		final ForumBoardType boardType = topic.getBoard().getType();
		final int topicIndex = topic.getBoard().getTopics().indexOf(topic);
		final int topicsPerPage = 7;
		final int page = (int) Math.floor(topicIndex / topicsPerPage);

		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "forum/startNewReply.htm", player);

		// Posts
		html = html.replace("%posterInfo%", "<font name=hs12 color=" + player.getForumMember().getMemberGroup().getColor() + ">" + player.getForumMember().getMemberName() + "</font><br1>");
		html = html.replace("%postCount%", String.valueOf(player.getForumMember().getPostCount()));

		// Replacements
		html = html.replace("%header%", HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "forum/header.htm", player));
		html = html.replace("%shortServerName%", Config.SHORT_SERVER_NAME);
		html = html.replace("%refreshBypass%", "bypass _bbsforum_startNewReply_" + topic.getTopicId() + "_" + communityArgsValue);
		html = html.replace("%boardIndex%", String.valueOf(boardType.getBoardIndex()));
		html = html.replace("%boardName%", boardType.getNiceName());
		html = html.replace("%page%", String.valueOf(page));
		html = html.replace("%lastPostId%", String.valueOf(topic.getLastPost().getPostId()));
		html = html.replace("%topicId%", String.valueOf(topic.getTopicId()));
		html = html.replace("%firstSubject%", String.valueOf(topic.getFirstPost().getSubject()));

		// Header
		final StringBuilder header = new StringBuilder();
		if (player.getForumMember() != null)
		{
			header.append("<center>");
			header.append("<font color=565368>You are logged in as </font><font color=" + player.getForumMember().getMemberGroup().getColor() + ">" + player.getForumMember().getMemberName() + "</font>");
			header.append("</center>");
		}
		else
		{
			header.append("<button action=\"bypass _bbsforum_connectAccount\" value=\"Log in!\" width=150 height=22 back=Btns.btn_simple_red_150x22_Down fore=Btns.btn_simple_red_150x22>");
		}
		html = html.replace("%header%", header.toString());

		ImagesCache.getInstance().sendUsedImages(html, player);
		ShowBoard.separateAndSend(html, player);
	}

	private void onAccountConnectFinalize(Player player, String nickName, String password)
	{
		final ForumMember member = ForumMembersHolder.getInstance().getMemberByName(nickName, null);
		if (member == null)
		{
			useForumBypass(player, "connectAccountError", "This Account doesn't exist or doesn't have enough Posts!");
			return;
		}
		if (member.getPostCount() < ConfigHolder.getInt("ForumAccountMinPosts"))
		{
			useForumBypass(player, "connectAccountError", "This Account doesn't have enough Posts!");
			return;
		}
		if (member.getMemberGroup() != ForumMemberGroup.NORMAL)
		{
			useForumBypass(player, "connectAccountError", "This Forum Account cannot be connected to the game!");
			return;
		}
		if (!ForumMembersHolder.getInstance().checkCorrectPassword(member, password))
		{
			useForumBypass(player, "connectAccountError", "Password is incorrect!");
			return;
		}
		if (ConfigHolder.getInt("ForumConnectAccountPriceId") > 0 && ConfigHolder.getLong("ForumConnectAccountPrice") > 0L && !player.getInventory().destroyItemByItemId(ConfigHolder.getInt("ForumConnectAccountPriceId"), ConfigHolder.getLong("ForumConnectAccountPrice"), "Connecting Forum Account"))
		{
			useForumBypass(player, "connectAccountError", "You don't have enough of required items!");
			return;
		}
		connectForumAccount(player, member);
		useForumBypass(player, "main");
	}

	private void onNewTopicFinalize(Player player, ForumBoard board, String subject, String message)
	{
		final ForumBoardType boardType = board.getType();
		if (player.getForumMember() == null)
		{
			player.sendMessage("You need to register Forum Account First!");
			useForumBypass(player, "board");
		}
		else if (!board.canWriteNewTopic(player.getForumMember()))
		{
			player.sendMessage("Topics cannot be created here!");
			useForumBypass(player, "board");
		}
		else
		{
			final ForumPost firstPost = createForumTopic(player, boardType, subject, message);
			if (firstPost != null)
			{
				firstPost.getTopic().incViews();
				useForumBypass(player, "post", firstPost.getPostId());
			}
		}
	}

	private static ForumPost createForumTopic(Player player, ForumBoardType boardType, String subject, String message)
	{
		if (!ConfigHolder.getPattern("ForumSubjectPattern").matcher(subject).matches())
		{
			sendErrorMsg(player, "Subject doesn't meet criteria!");
			return null;
		}
		if (subject.length() > ConfigHolder.getInt("ForumMaxSubjectLength"))
		{
			sendErrorMsg(player, "Subject is too long!");
			return null;
		}
		if (!ConfigHolder.getPattern("ForumMessagePattern").matcher(message).matches())
		{
			sendErrorMsg(player, "Message doesn't meet criteria!");
			return null;
		}
		if (message.length() > ConfigHolder.getInt("ForumMaxPostMessageLength"))
		{
			sendErrorMsg(player, "Message is too long!");
			return null;
		}
		final String convertedMessage = ForumHandler.convertMessageFromTextBox(message);
		final ForumBoard board = ForumHandler.getInstance().getBoardByIndex(boardType.getBoardIndex());
		final ForumPost firstPost = board.createNewTopic(player, subject, convertedMessage);
		player.sendMessage("Topic has been created!");
		return firstPost;
	}

	private void onNewReplyFinalize(Player player, ForumTopic topic, String message)
	{
		if (player.getForumMember() == null)
		{
			sendErrorMsg(player, "You need to register Forum Account First!");
			useForumBypass(player, "post", topic.getLastPost().getPostId());
		}
		else if (topic.isLocked() || !topic.getBoard().canWriteNewTopic(player.getForumMember()))
		{
			sendErrorMsg(player, "Topic is locked!");
			useForumBypass(player, "post", topic.getLastPost().getPostId());
		}
		else
		{
			final ForumPost newPost = createForumPost(player, topic, message);
			if (newPost != null)
			{
				useForumBypass(player, "post", newPost.getPostId());
			}
		}
	}

	private static void connectForumAccount(Player player, ForumMember member)
	{
		player.setForumLogin(member.getMemberName());
		player.setForumMember(member);
		player.sendMessage("Account has been successfully connected!!");
	}

	private static void sendErrorMsg(Player player, String msg)
	{
		player.sendPacket(new Say2(0, ChatType.COMMANDCHANNEL_ALL, "Error", msg));
	}

	private static ForumPost createForumPost(Player player, ForumTopic topic, String message)
	{
		if (!ConfigHolder.getPattern("ForumMessagePattern").matcher(message).matches())
		{
			sendErrorMsg(player, "Message doesn't meet criteria!");
			return null;
		}
		if (message.length() > ConfigHolder.getInt("ForumMaxPostMessageLength"))
		{
			sendErrorMsg(player, "Message is too long!");
			return null;
		}
		final String convertedMsg = ForumHandler.convertMessageFromTextBox(message);
		final ForumPost post = topic.createNewPost(player, convertedMsg);
		player.sendMessage("Post has been added!");
		return post;
	}

	public static String getQuotePost(ForumPost postToQuote)
	{
		if (postToQuote == null)
		{
			return "";
		}

		final StringBuilder quote = new StringBuilder();
		quote.append("[quote author=");
		quote.append(postToQuote.getWriter().getMemberName());
		quote.append(" link=topic=");
		quote.append(postToQuote.getTopic().getTopicId());
		quote.append(".msg");
		quote.append(postToQuote.getPostId());
		quote.append("#msg");
		quote.append(postToQuote.getPostId());
		quote.append(" date=");
		quote.append(postToQuote.getDate() / 1000L);
		quote.append("]<br1>");
		final String messageWithoutQuote = clearQuotes(postToQuote.getMessage());
		quote.append(messageWithoutQuote);
		quote.append("<br1>[/quote]<br1>");
		return quote.toString();
	}

	public static String convertQuotes(String originalMessage, String startTagReplacement, String afterAuthor, String endTagReplacement)
	{
		String newMessage = originalMessage;
		final int startTags = StringUtils.countMatches(newMessage, "[quote");
		final int endTags = StringUtils.countMatches(newMessage, "[/quote]");
		if (startTags > 0 && startTags == endTags)
		{
			while (newMessage.contains("[quote"))
			{
				final int startTagIndex = newMessage.indexOf("[quote");
				final int endTagIndex = newMessage.indexOf("]", startTagIndex);
				if (endTagIndex <= startTagIndex)
				{
					return originalMessage;
				}
				final String justQuote = newMessage.substring(startTagIndex, endTagIndex + 1);
				final String[] split = justQuote.split(" ");
				if (split.length != 4)
				{
					return originalMessage;
				}
				String author = "";
				if (split[1].startsWith("author="))
				{
					author = split[1].substring("author=".length());
				}
				final String newText = startTagReplacement + author + afterAuthor;
				newMessage = newMessage.replace(justQuote, newText);
			}
			newMessage = newMessage.replace("[/quote]", endTagReplacement);
		}
		return newMessage;
	}

	private static String clearQuotes(String originalMessage)
	{
		String newMessage;
		int endIndex;
		for (newMessage = originalMessage; newMessage.contains("[/quote]"); newMessage = newMessage.substring(endIndex + "[/quote]".length(), newMessage.length() - 1))
		{
			endIndex = newMessage.indexOf("[/quote]");
			if (endIndex < 0)
			{
				break;
			}
		}
		while (newMessage.startsWith("<br1>"))
		{
			newMessage = newMessage.substring("<br1>".length(), newMessage.length() - 1);
		}
		return newMessage;
	}

	protected static List<String> getReplyArguments(String initMessage)
	{
		final List<String> args = new FastList<>();
		args.add("0");
		args.add("0");
		args.add("0");
		args.add("0");
		args.add("0");
		args.add("0");
		args.add("0");
		args.add("0");
		args.add("0");
		args.add("9");
		args.add("0");
		args.add("0");
		args.add(initMessage);
		args.add("0");
		args.add("0");
		args.add("0");
		args.add("0");
		return args;
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
	}
}
