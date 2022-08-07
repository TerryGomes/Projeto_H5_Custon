package services.community;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.data.StringHolder;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.fandc.facebook.ActiveTask;
import l2mv.gameserver.fandc.facebook.ActiveTasksHandler;
import l2mv.gameserver.fandc.facebook.CompletedTasksHistory;
import l2mv.gameserver.fandc.facebook.FacebookActionType;
import l2mv.gameserver.fandc.facebook.FacebookIdentityType;
import l2mv.gameserver.fandc.facebook.FacebookProfile;
import l2mv.gameserver.fandc.facebook.FacebookProfilesHolder;
import l2mv.gameserver.fandc.facebook.TaskNoAvailableException;
import l2mv.gameserver.handler.bbs.CommunityBoardManager;
import l2mv.gameserver.handler.bbs.ICommunityBoardHandler;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SubClass;
import l2mv.gameserver.network.serverpackets.HideBoard;
import l2mv.gameserver.network.serverpackets.Say2;
import l2mv.gameserver.network.serverpackets.ShowBoard;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.utils.Log;
import l2mv.gameserver.utils.Util;

/**
 * Facebook Community Manager
 *
 * @author Synerge
 */
public class CommunityFacebook implements ScriptFile, ICommunityBoardHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(CommunityFacebook.class);

	private static final SimpleDateFormat NEXT_CHALLENGE_FORMAT = new SimpleDateFormat("HH:mm dd/MM/yyyy");
	private static final SimpleDateFormat CURRENT_TIME_FORMAT = new SimpleDateFormat("HH:mm");

	@Override
	public void onLoad()
	{
		if (Config.COMMUNITYBOARD_ENABLED && !ConfigHolder.getBool("EnableMergeCommunity"))
		{
			LOG.info("CommunityBoard: Facebook Rewards page loaded.");
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
			"_bbsfacebook"
		};
	}

	private void useFacebookBypass(Player player, String bypass, Object... params)
	{
		onBypassCommand(player, "_bbsfacebook_" + bypass + (params.length > 0 ? "_" : "") + Util.joinArrayWithCharacter(params, "_"));
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		final ActiveTask task = ActiveTasksHandler.getInstance().getActiveTaskByPlayer(player);

		final StringTokenizer st = new StringTokenizer(bypass, "_");
		st.nextToken();

		if (st.hasMoreTokens())
		{
			switch (st.nextToken())
			{
			case "main":
			{
				if (task == null)
				{
					if (player.hasFacebookProfile())
					{
						if (player.getFacebookProfile().hasNegativePoints())
						{
							if (CompletedTasksHistory.getInstance().getAvailableNegativeBalanceTypes(player.getFacebookProfile()).isEmpty())
							{
								useFacebookBypass(player, "noTasksToTake");
							}
							else
							{
								useFacebookBypass(player, "tasksList");
							}
						}
						else if (player.getFacebookProfile().hasTaskDelay())
						{
							useFacebookBypass(player, "taskDelay");
						}
						else if (CompletedTasksHistory.getInstance().getAvailableActionTypes(player.getFacebookProfile()).isEmpty())
						{
							useFacebookBypass(player, "noTasksToTake");
						}
						else
						{
							useFacebookBypass(player, "tasksList");
						}
					}
					else
					{
						useFacebookBypass(player, "confirmIdentityStartInfo");
					}
				}
				else
				{
					useFacebookBypass(player, "activeTaskDetails");
				}
				break;
			}
			case "confirmIdentityStartInfo":
			{
				showConfirmIndentityStartInfoPage(player);
				break;
			}
			case "confirmIdentityNonEnglishChars":
			{
				startNewTask(player, FacebookActionType.COMMENT, FacebookIdentityType.NAME_IN_COMMENT, player.getName());
				break;
			}
			case "confirmIdentityEnglishChars":
			{
				final String facebookName = st.nextToken().trim();
				startNewTask(player, FacebookActionType.COMMENT, FacebookIdentityType.NAME, facebookName);
				break;
			}
			case "tasksList":
			{
				showTaskListPage(player);
				break;
			}
			case "startTask":
			{
				if (player.getFacebookProfile() == null)
				{
					useFacebookBypass(player, "main");
					return;
				}

				final String action = st.nextToken();
				final FacebookActionType actionType = FacebookActionType.valueOf(action);
				startNewTask(player, actionType, FacebookIdentityType.ID, player.getFacebookProfile().getId());
				break;
			}
			case "taskDelay":
			{
				showTaskDelayPage(player);
				break;
			}
			case "activeTaskDetails":
			{
				showActiveTaskDetailsPage(player);
				break;
			}
			case "noTasksToTake":
			{
				String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "facebookRewards/noTasksToTake.htm", player);
				ShowBoard.separateAndSend(html, player);
				break;
			}
			}
		}
	}

	private void showConfirmIndentityStartInfoPage(Player player)
	{
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "facebookRewards/confirmIdentityStartInfo.htm", player);

		// Rewards
		StringBuilder rewards = new StringBuilder();

		for (Map.Entry<Integer, Long> rewardEntry : FacebookActionType.COMMENT.getRewardForTask().entrySet())
		{
			ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(rewardEntry.getKey());
			if (itemTemplate == null)
			{
				continue;
			}

			rewards.append("<td width=40>");
			rewards.append("<table cellspacing=0 cellpadding=0 width=32 height=32 background=" + itemTemplate.getIcon() + ">");
			rewards.append("<tr>");
			rewards.append("<td width=32 height=32 align=center valign=top>");
			// rewards.append("<img src=Btns.nice_frame width=32 height=32>");
			rewards.append("</td>");
			rewards.append("</tr>");
			rewards.append("</table>");
			rewards.append("</td>");
			rewards.append("<td width=120>");
			rewards.append("<font color=ff8e3b>" + rewardEntry.getValue() + "x " + itemTemplate.getName() + "</font>");
			rewards.append("</td>");
		}

		// Replacements
		html = html.replace("%rewards%", rewards.toString());

		ShowBoard.separateAndSend(html, player);
	}

	private void showTaskListPage(Player player)
	{
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "facebookRewards/tasksList.htm", player);

		// Balance
		if (player.getFacebookProfile().hasNegativePoints())
		{
			html = html.replace("%balance%", "Clear Negative Balance");
		}
		else
		{
			html = html.replace("%balance%", "Choose your new Task");
		}

		// Actions
		StringBuilder actions = new StringBuilder();
		boolean nextColor = false;
		if (player.getFacebookProfile().hasNegativePoints())
		{
			actions.append("<table cellspacing=8></table>");
			actions.append("<table cellspacing=0 cellpadding=2 width=740 height=69 background=l2ui_ct1.Windows_DF_TooltipBG>");
			actions.append("<tr>");
			actions.append("<td>");
			actions.append("<table cellspacing=0 cellpadding=0 width=736 height=65 bgcolor=011118>");
			actions.append("<tr>");
			actions.append("<td align=center>");
			actions.append("<font color=bc2b0e name=hs12>");
			actions.append("Negative Points!");
			actions.append("</font>");
			actions.append("<br>");
			actions.append("<font color=ff8e3b>");
			actions.append("It looks like you have negative points for deleting post/like/comment/share that was rewarded.<br1>");
			actions.append("You need to complete similar tasks before being able to get the rewards again.");
			actions.append("</font>");
			actions.append("</td>");
			actions.append("</tr>");
			actions.append("</table>");
			actions.append("</td>");
			actions.append("</tr>");
			actions.append("</table>");

			for (FacebookActionType negativePointsType : CompletedTasksHistory.getInstance().getAvailableNegativeBalanceTypes(player.getFacebookProfile()))
			{
				makeActionTypeTable(actions, negativePointsType, nextColor, false);
				nextColor = !nextColor;
			}
		}
		else
		{
			for (FacebookActionType availableActionType : CompletedTasksHistory.getInstance().getAvailableActionTypes(player.getFacebookProfile()))
			{
				makeActionTypeTable(actions, availableActionType, nextColor, true);
				nextColor = !nextColor;
			}
		}

		// Replacements
		html = html.replace("%actions%", actions.toString());

		ShowBoard.separateAndSend(html, player);
	}

	private void makeActionTypeTable(StringBuilder sb, FacebookActionType actionType, boolean nextColor, boolean rewarded)
	{
		sb.append("<table cellspacing=8></table>");
		sb.append("<table cellspacing=0 cellpadding=2 width=740 height=79 background=l2ui_ct1.Windows_DF_TooltipBG>");
		sb.append("<tr>");
		sb.append("<td>");
		sb.append("<table cellspacing=0 cellpadding=0 width=736 height=75 bgcolor=" + (nextColor ? "011118" : "00080b") + ">");
		sb.append("<tr>");
		sb.append("<td align=center>");
		sb.append("<font color=bc2b0e name=hs12>");
		switch (actionType)
		{
		case LIKE:
			sb.append("Like our post on Facebook");
			break;
		case POST:
			sb.append("Post comment on our Facebook Wall");
			break;
		case COMMENT:
			sb.append("Write comment under one of our posts");
			break;
		case SHARE:
			sb.append("Share one of our posts");
			break;
		}
		sb.append("</font>");
		sb.append("<br>");
		sb.append("<table cellspacing=0 cellpadding=0 width=736>");
		sb.append("<tr>");
		sb.append("<td width=568>");
		if (rewarded)
		{
			sb.append("<table cellspacing=0 cellpadding=0>");
			sb.append("<tr>");
			sb.append("<td width=70 align=center>");
			sb.append("<font color=bc2b0e name=hs12>Reward:</font>");
			sb.append("</td>");
			for (Map.Entry<Integer, Long> rewardEntry : actionType.getRewardForTask().entrySet())
			{
				final ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(rewardEntry.getKey());
				sb.append("<td width=40>");
				sb.append("<table cellspacing=0 cellpadding=0 width=32 height=32 background=" + itemTemplate.getIcon() + ">");
				sb.append("<tr>");
				sb.append("<td width=32 height=32 align=center valign=top>");
				sb.append("<img src=Btns.nice_frame width=32 height=32>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("<td width=120>");
				sb.append("<font color=ff8e3b>" + rewardEntry.getValue() + "x " + itemTemplate.getName() + "</font>");
				sb.append("</td>");
			}
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("<#else>");
			sb.append("<br>");
		}
		sb.append("</td>");
		sb.append("<td width=168>");
		sb.append("<br>");
		sb.append("<button value=\"Challenge Accepted\" action=\"bypass _bbsfacebook_startTask_" + actionType.toString() + "\" width=150 height=22 back=Btns.btn_simple_red_150x22_down fore=Btns.btn_simple_red_150x22 />");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
	}

	private void showTaskDelayPage(Player player)
	{
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "facebookRewards/taskDelay.htm", player);

		// Next Challenge
		html = html.replace("%nextChallenge%", NEXT_CHALLENGE_FORMAT.format(player.getFacebookProfile().getDelayEndDate()));

		// Next Challenge
		html = html.replace("%currentTime%", CURRENT_TIME_FORMAT.format(Calendar.getInstance().getTimeInMillis()));

		ShowBoard.separateAndSend(html, player);
	}

	private void showActiveTaskDetailsPage(Player player)
	{
		final ActiveTask activeTask = ActiveTasksHandler.getInstance().getActiveTaskByPlayer(player);

		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "facebookRewards/activeTaskDetails.htm", player);

		// Action Comment
		switch (activeTask.getActionType())
		{
		case LIKE:
			html = html.replace("%actionComment%", "Like our post on Facebook");
			break;
		case POST:
			html = html.replace("%actionComment%", "Post comment on our Facebook Wall");
			break;
		case COMMENT:
			html = html.replace("%actionComment%", "Write comment under one of our posts");
			break;
		case SHARE:
			html = html.replace("%actionComment%", "Share one of our posts");
			break;
		}

		// Messages
		final StringBuilder messages = new StringBuilder();
		if (activeTask.getActionType().haveCommentMessage())
		{
			messages.append("<tr>");
			messages.append("<td align=center height=50>");
			messages.append("<font color=bc2b0e name=hs12>Comment to write " + (activeTask.getIdentityType() == FacebookIdentityType.ID || !ConfigHolder.getBool("FacebookRegistrationOnlyExactComment") ? "different comment will have to be approved" : "<font color=ff0000 name=hs12>(COMMENT MUST BE 100% THE SAME!!!)</font>") + ":");
			messages.append("</font><br1>");
			messages.append("<font color=ff8e3b>");
			messages.append(activeTask.getRequestedMessage());
			messages.append("</font>");
			messages.append("</td>");
			messages.append("</tr>");
			messages.append("<tr>");
			messages.append("<td align=center height=100>");
			messages.append("<font color=bc2b0e name=hs12>Under official post:</font><br1>");
			messages.append("<font color=ff8e3b>");
			messages.append(activeTask.getFather().getMessage().replace("\n", "<br1>"));
			messages.append("</font>");
			messages.append("</td>");
			messages.append("</tr>");
		}
		else
		{
			messages.append("<tr>");
			messages.append("<td align=center height=150>");
			messages.append("<font color=bc2b0e name=hs12>Post Message:</font><br1>");
			messages.append("<font color=ff8e3b>");
			messages.append(activeTask.getFather().getMessage().replace("\n", "<br1>"));
			messages.append("</font>");
			messages.append("</td>");
			messages.append("</tr>");
		}

		// Rewards
		final StringBuilder rewards = new StringBuilder();
		if (player.getFacebookProfile() != null && player.getFacebookProfile().hasNegativePoints())
		{
			rewards.append("<tr>");
			rewards.append("<td align=center height=40>");
			rewards.append("<font color=ff8e3b>");
			rewards.append("Task is not rewarded because You have Negative Points.");
			rewards.append("</font>");
			rewards.append("</td>");
			rewards.append("</tr>");
		}
		else
		{
			rewards.append("<tr>");
			rewards.append("<td align=center height=40>");
			rewards.append("<table cellspacing=0 cellpadding=0>");
			rewards.append("<tr>");
			for (Map.Entry<Integer, Long> rewardEntry : activeTask.getActionType().getRewardForTask().entrySet())
			{
				final ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(rewardEntry.getKey());
				rewards.append("<td width=40>");
				rewards.append("<table cellspacing=0 cellpadding=0 width=32 height=32 background=" + itemTemplate.getIcon() + ">");
				rewards.append("<tr>");
				rewards.append("<td width=32 height=32 align=center valign=top>");
				rewards.append("<img src=Btns.nice_frame width=32 height=32>");
				rewards.append("</td>");
				rewards.append("</tr>");
				rewards.append("</table>");
				rewards.append("</td>");
				rewards.append("<td width=120>");
				rewards.append("<font color=ff8e3b>" + rewardEntry.getValue() + "x " + itemTemplate.getName() + "</font>");
				rewards.append("</td>");
			}
			rewards.append("</tr>");
			rewards.append("</table>");
			rewards.append("</td>");
			rewards.append("</tr>");
		}

		// Replacements
		html = html.replace("%messages%", messages.toString());
		html = html.replace("%rewards%", rewards.toString());
		html = html.replace("%timeLimit%", CURRENT_TIME_FORMAT.format(activeTask.getTimeLimitDate()));
		html = html.replace("%currentTime%", CURRENT_TIME_FORMAT.format(Calendar.getInstance().getTimeInMillis()));
		html = html.replace("%linkToAction%", activeTask.getLinkToAction());

		ShowBoard.separateAndSend(html, player);
	}

	public boolean isDisabled(Player player)
	{
		return !ConfigHolder.getBool("AllowFacebookRewardSystem");
	}

	private void startNewTask(Player player, FacebookActionType actionType, FacebookIdentityType identityType, String identityValue)
	{
		if (getBiggestLevel(player) < ConfigHolder.getInt("FacebookConnectMinLevel"))
		{
			sendErrorMessage(player, StringHolder.getNotNull(player, "Facebook.StartNewTask.Fail.TooLowLevel", ConfigHolder.getInt("FacebookConnectMinLevel")), true);
			return;
		}

		FacebookProfile profile = null;
		if (identityType == FacebookIdentityType.ID)
		{
			profile = player.getFacebookProfile();
		}
		if (identityType == FacebookIdentityType.NAME)
		{
			profile = FacebookProfilesHolder.getInstance().getProfileByName(identityValue, true, true);
		}

		if (profile != null && !profile.hasNegativePoints())
		{
			if (!CompletedTasksHistory.getInstance().isActionTypeAvailable(profile, actionType))
			{
				sendErrorMessage(player, StringHolder.getNotNull(player, "Facebook.StartNewTask.Fail.TaskNotAvailable", new Object[0]), true);
				Log.logFacebook("Couldn't start new Task by " + player + ", " + profile + ". Reason: " + actionType + " not available!");
				return;
			}

			if (profile.hasTaskDelay())
			{
				sendErrorMessage(player, StringHolder.getNotNull(player, "Facebook.StartNewTask.Fail.TaskNotAvailable", new Object[0]), true);
				Log.logFacebook("Couldn't start new Task by " + player + ", " + profile + ". Reason: Delay!");
				return;
			}
		}

		if (ActiveTasksHandler.getInstance().getActiveTask(identityType, identityValue) != null)
		{
			sendErrorMessage(player, StringHolder.getNotNull(player, "Facebook.StartNewTask.Fail.TaskAlreadyActive", new Object[0]), true);
			Log.logFacebook("Couldn't start new Task by " + player + ", " + identityType + ", " + identityValue + ". Reason: Already Active!");
			return;
		}

		try
		{
			final ActiveTask taskToComplete = ActiveTasksHandler.getInstance().createActiveTask(player, identityType, identityValue, actionType);
			if (taskToComplete == null)
			{
				player.sendPacket(HideBoard.PACKET);
			}
			else
			{
				useFacebookBypass(player, "activeTaskDetails");
			}
		}
		catch (TaskNoAvailableException e)
		{
			Log.logFacebook("Task No Available for " + player.toString() + ", " + actionType + ", " + identityType + " , " + identityValue + ", " + e.toString());
			useFacebookBypass(player, "noTasksToTake");
		}
	}

	private int getBiggestLevel(Player player)
	{
		int biggest = Integer.MIN_VALUE;
		for (SubClass sub : player.getSubClasses().values())
		{
			if (sub.getLevel() > biggest)
			{
				biggest = sub.getLevel();
			}
		}
		return biggest;
	}

	private static void sendErrorMessage(Player player, String msg, boolean closeBoard)
	{
		player.sendPacket(new Say2(0, ChatType.COMMANDCHANNEL_ALL, "Error", msg));
		if (closeBoard)
		{
			player.sendPacket(HideBoard.PACKET);
		}
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
	}
}
