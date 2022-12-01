package l2mv.gameserver.handler.admincommands.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.handler.admincommands.AdminCommandHandler;
import l2mv.gameserver.handler.admincommands.IAdminCommandHandler;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.multverso.facebook.ActionsExtractingManager;
import l2mv.gameserver.multverso.facebook.ActiveTask;
import l2mv.gameserver.multverso.facebook.ActiveTasksHandler;
import l2mv.gameserver.multverso.facebook.CompletedTask;
import l2mv.gameserver.multverso.facebook.CompletedTasksHistory;
import l2mv.gameserver.multverso.facebook.FacebookActionType;
import l2mv.gameserver.multverso.facebook.FacebookDatabaseHandler;
import l2mv.gameserver.multverso.facebook.OfficialPost;
import l2mv.gameserver.multverso.facebook.OfficialPostsHolder;
import l2mv.gameserver.network.serverpackets.ShowBoard;

public class AdminFacebook implements IAdminCommandHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(AdminFacebook.class);

	private static enum Commands
	{
		admin_facebook,
		admin_fb_set_message_approval,
		admin_fb_official_posts,
		admin_fb_official_post_edit_panel,
		admin_fb_add_rewarded_action,
		admin_fb_remove_rewarded_action,
		admin_reset_facebook_delay,
		admin_recheck_task_completed,
		admin_has_fb_task,
		admin_expire_fb_task,
		admin_clear_negative_balance,
		admin_reload_fb_posts
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		switch (command)
		{
		case admin_facebook:
		{
			String html = HtmCache.getInstance().getNotNull("admin/facebook/messagesToApprove.htm", activeChar);

			final StringBuilder sb = new StringBuilder();
			int index = 0;
			boolean nextColor = false;
			for (CompletedTask task : CompletedTasksHistory.getInstance().getTasksThatNeedsApproval())
			{
				index++;
				if (index > 5)
				{
					break;
				}

				sb.append("<table cellspacing=5></table>");
				sb.append("<table cellspacing=0 cellpadding=2 fixwidth=740 height=79 background=l2ui_ct1.Windows_DF_TooltipBG>");
				sb.append("<tr>");
				sb.append("<td>");
				sb.append("<table cellspacing=0 cellpadding=0 fixwidth=736 height=75 bgcolor=" + (nextColor ? "011118" : "00080b") + ">");
				sb.append("<tr>");
				sb.append("<td fixwidth=568>");
				sb.append("<table cellspacing=0 cellpadding=4 fixwidth=568>");
				sb.append("<tr>");
				sb.append("<td align=center width=60>");
				sb.append("<font color=bc2b0e>Message:</font>");
				sb.append("</td>");
				sb.append("<td fixwidth=508>");
				sb.append("<font color=ff8e3b>" + task.getMessage().replace("\n", "<br1>") + "</font");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("<td width=168>");
				sb.append("<br>");
				sb.append("<button value=\"Approve\" action=\"bypass -h admin_fb_set_message_approval " + task.getId() + " True\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\" />");
				sb.append("<br>");
				sb.append("<button value=\"NOT Approve\" action=\"bypass -h admin_fb_set_message_approval " + task.getId() + " False\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\" />");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");

				nextColor = !nextColor;
			}

			html = html.replace("%list%", sb.toString());
			ShowBoard.separateAndSend(html, activeChar);
			break;
		}
		case admin_fb_set_message_approval:
		{
			final String actionId = wordList[1];
			final boolean approved = wordList[2].equalsIgnoreCase("true");
			final CompletedTask task = CompletedTasksHistory.getInstance().getCompletedTask(actionId);
			ActiveTasksHandler.manageMessageApproval(task, approved);
			break;
		}
		case admin_fb_official_posts:
		{
			String html = HtmCache.getInstance().getNotNull("admin/facebook/officialPosts.htm", activeChar);

			final StringBuilder sb = new StringBuilder();
			int index = 0;
			boolean nextColor = false;
			for (OfficialPost officialPost : OfficialPostsHolder.getInstance().getRecentOfficialPostsForIterate())
			{
				index++;

				sb.append("<table cellspacing=5></table>");
				sb.append("<table cellspacing=0 cellpadding=2 fixwidth=740 height=79>");
				sb.append("<tr>");
				sb.append("<td>");
				sb.append("<table cellspacing=0 cellpadding=0 fixwidth=736 height=75 bgcolor=" + (nextColor ? "011118" : "00080b") + ">");
				sb.append("<tr>");
				sb.append("<td fixwidth=568>");
				sb.append("<table cellspacing=0 cellpadding=4 fixwidth=636>");
				sb.append("<tr>");
				sb.append("<td align=center width=60>");
				sb.append("<font color=bc2b0e>");
				if (officialPost.getRewardedActionsForIterate().isEmpty())
				{
					sb.append("NOT Active");
				}
				else
				{
					sb.append("Active");
				}
				sb.append("</font>");
				sb.append("</td>");
				sb.append("<td fixwidth=576>");
				sb.append("<font color=ff8e3b>" + officialPost.getMessage().replace("\n", "<br1>") + "</font>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("<td width=100>");
				sb.append("<br><br><br>");
				sb.append("<button value=\"Setup\" action=\"bypass -h admin_fb_official_post_edit_panel " + officialPost.getId() + "\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\" />");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");

				nextColor = !nextColor;
			}

			html = html.replace("%list%", sb.toString());
			ShowBoard.separateAndSend(html, activeChar);
			break;
		}
		case admin_fb_official_post_edit_panel:
		{
			String html = HtmCache.getInstance().getNotNull("admin/facebook/editOfficialPost.htm", activeChar);

			final String postId = wordList[1];
			final OfficialPost officialPost = OfficialPostsHolder.getInstance().getOfficialPost(postId);

			final StringBuilder sb = new StringBuilder();
			for (FacebookActionType actionType : FacebookActionType.values())
			{
				if (!actionType.isRewarded())
				{
					continue;
				}

				if (officialPost.isActionTypeRewarded(actionType))
				{
					sb.append("<button value=\"" + actionType.toString() + "\" action=\"bypass -h admin_fb_remove_rewarded_action " + officialPost.getId() + " " + actionType.toString() + "\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\" />");
				}
				else
				{
					sb.append("<button value=\"" + actionType.toString() + "\" action=\"bypass -h admin_fb_add_rewarded_action " + officialPost.getId() + " " + actionType.toString() + "\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\" />");
				}
			}

			html = html.replace("%message%", officialPost.getMessage().replace("\n", "<br1>"));
			html = html.replace("%list%", sb.toString());
			ShowBoard.separateAndSend(html, activeChar);
			break;
		}
		case admin_fb_add_rewarded_action:
		{
			final String postId = wordList[1];
			final String actionName = wordList[2];
			OfficialPostsHolder.getInstance().addNewRewardedAction(postId, FacebookActionType.valueOf(actionName));
			AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, "admin_fb_official_post_edit_panel " + postId);
			break;
		}
		case admin_fb_remove_rewarded_action:
		{
			final String postId = wordList[1];
			final String actionName = wordList[2];
			OfficialPostsHolder.getInstance().removeNewRewardedAction(postId, FacebookActionType.valueOf(actionName));
			AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, "admin_fb_official_post_edit_panel " + postId);
			break;
		}
		case admin_reset_facebook_delay:
		{
			final String targetName = wordList[1];
			final Player target = GameObjectsStorage.getPlayer(targetName);
			if (target == null || target.getFacebookProfile() == null)
			{
				activeChar.sendMessage(target.toString() + " doesn't have Facebook Profile.");
				return false;
			}
			target.getFacebookProfile().setLastCompletedTaskDate(-1L);
			FacebookDatabaseHandler.replaceFacebookProfile(target.getFacebookProfile());
			break;
		}
		case admin_recheck_task_completed:
		{
			final String targetName = wordList[1];
			final Player target = GameObjectsStorage.getPlayer(targetName);
			if (target == null)
			{
				return false;
			}

			final ActiveTask task = ActiveTasksHandler.getInstance().getActiveTaskByPlayer(target);
			if (task == null)
			{
				activeChar.sendMessage(target.getName() + " has no active task!");
				return false;
			}
			final boolean completed = ActiveTasksHandler.getInstance().checkTaskCompleted(task);
			activeChar.sendMessage("Result: " + completed);
			break;
		}
		case admin_has_fb_task:
		{
			final String targetName = wordList[1];
			final Player target = GameObjectsStorage.getPlayer(targetName);
			if (target == null)
			{
				return false;
			}

			final ActiveTask task = ActiveTasksHandler.getInstance().getActiveTaskByPlayer(target);
			if (task == null)
			{
				activeChar.sendMessage(target.getName() + " has NO active task!");
			}
			else
			{
				activeChar.sendMessage(target.getName() + " HAS active task!");
			}
			break;
		}
		case admin_expire_fb_task:
		{
			final String targetName = wordList[1];
			final Player target = GameObjectsStorage.getPlayer(targetName);
			if (target == null)
			{
				return false;
			}

			final ActiveTask task = ActiveTasksHandler.getInstance().getActiveTaskByPlayer(target);
			if (task == null)
			{
				activeChar.sendMessage(target.getName() + " has no active task!");
				return false;
			}
			ActiveTasksHandler.getInstance().forceExpireTask(task);
			activeChar.sendMessage("Task has expired!");
			break;
		}
		case admin_clear_negative_balance:
		{
			final String targetName = wordList[1];
			final Player target = GameObjectsStorage.getPlayer(targetName);
			if (target == null)
			{
				return false;
			}

			if (target.getFacebookProfile() == null)
			{
				activeChar.sendMessage(target.getName() + " has no Facebook Profile attached!");
				return false;
			}
			if (target.getFacebookProfile().getNegativePointTypesForIterate().isEmpty())
			{
				activeChar.sendMessage(target.getName() + " doesnt have Negative Balance!");
				return false;
			}
			target.getFacebookProfile().getNegativePointTypesForIterate().clear();
			FacebookDatabaseHandler.replaceFacebookProfile(target.getFacebookProfile());
			break;
		}
		case admin_reload_fb_posts:
		{
			OfficialPostsHolder.getInstance().getRecentOfficialPostsForIterate().clear();
			OfficialPostsHolder.getInstance().getActivePostsForIterate().clear();
			try
			{
				ActionsExtractingManager.getInstance().getExtractor("ExtractOfficialPosts").extractData(ConfigHolder.getString("FacebookToken"));
			}
			catch (IOException e)
			{
				LOG.error("Error while extracting Official Posts!", e);
				return false;
			}
			break;
		}
		default:
		{
			return false;
		}
		}

		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
