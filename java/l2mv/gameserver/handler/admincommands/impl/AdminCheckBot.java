package l2mv.gameserver.handler.admincommands.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dbutils.DbUtils;
import l2mv.gameserver.Config;
import l2mv.gameserver.dao.CharacterDAO;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.handler.admincommands.IAdminCommandHandler;
import l2mv.gameserver.instancemanager.AutoHuntingManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.network.GameClient;
import l2mv.gameserver.network.loginservercon.AuthServerCommunication;
import l2mv.gameserver.network.loginservercon.gspackets.ChangeAccessLevel;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.utils.AutoBan;
import l2mv.gameserver.utils.Log;
import l2mv.gameserver.utils.TimeUtils;

public class AdminCheckBot implements IAdminCommandHandler
{
	private static final Logger _log = LoggerFactory.getLogger(AdminCheckBot.class);

	private static enum Commands
	{
		admin_checkbots,
		admin_readbot,
		admin_markbotreaded,
		admin_punish_bot,
		admin_set_report_points
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		if (!Config.ENABLE_AUTO_HUNTING_REPORT)
		{
			activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.admincheckbot.notenabled", activeChar));
			return false;
		}
		final Commands command = (Commands) comm;
		final String[] ids = fullString.split(" ");
		switch (command)
		{
		case admin_set_report_points:
			if (activeChar.getTarget() != null && activeChar.getTarget().isPlayer() && ids.length != 1)
			{
				activeChar.getTarget().getPlayer().getReportedAccount().setBotReportPoints(Integer.parseInt(ids[1]));
				activeChar.getTarget().getPlayer().getReportedAccount().updatePoints(activeChar.getTarget().getPlayer().getAccountName());
				activeChar.sendMessage("You have set bot report points to " + Integer.parseInt(ids[1]) + " for player: " + activeChar.getTarget().getName());
			}
			else
			{
				activeChar.sendMessage("Failed to set bot report points...");
			}
			break;
		case admin_checkbots:
			sendBotPage(activeChar);
			break;
		case admin_readbot:
			sendBotInfoPage(activeChar, Integer.parseInt(ids[1]));
			break;
		case admin_markbotreaded:
		{
			try
			{
				AutoHuntingManager.getInstance().markAsRead(Integer.parseInt(wordList[1]));
				sendBotPage(activeChar);
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
			break;
		}
		case admin_punish_bot:
		{
			if (wordList != null)
			{
				final String characterName = wordList[1];
				if (characterName != null)
				{
					if (wordList[2].equalsIgnoreCase("CHATBAN"))
					{
						if (wordList.length != 4)
						{
							activeChar.sendMessage("Invalid Format for punishment... please fill all boxes.");
							return false;
						}
						final int punishTime = Integer.valueOf(wordList[3]);
						if (punishTime != 0)
						{
							Log.bots("AdminCheckBot: " + characterName + " has been marked as bot and CHAT-BANNED by GM " + activeChar.getName() + " for " + punishTime + " minutes.");
							AutoBan.ChatBan(characterName, punishTime, "Handled by bot report system.", activeChar.getName());
							activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.admincheckbot.you_chatbanned", activeChar, characterName, punishTime));
							introduceNewPunishedBotAndClear(characterName);
						}
						else
						{
							activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.admincheckbot.error", activeChar));
						}
					}
					else if (wordList[2].equalsIgnoreCase("KICK"))
					{
						Log.bots("AdminCheckBot: " + characterName + " has been marked as bot and KICKED by GM " + activeChar.getName());
						final Player plr = World.getPlayer(characterName);
						if (plr != null)
						{
							plr.kick();
						}
						activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.admincheckbot.kicked", activeChar, characterName));
						introduceNewPunishedBotAndClear(characterName);
					}
					else if (wordList[2].equalsIgnoreCase("BANCHAR"))
					{
						if (wordList.length != 4)
						{
							activeChar.sendMessage("Invalid Format for punishment... please fill all boxes.");
							return false;
						}
						final int punishTime = Integer.valueOf(wordList[3]);
						if (punishTime != 0)
						{
							Log.bots("AdminCheckBot: " + characterName + " has been marked as bot and BAN-CHAR by GM " + activeChar.getName() + " for " + punishTime + " days.");
							AutoBan.Banned(characterName, -100, punishTime, "Handled by botreport system.", activeChar.getName());
							activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.admincheckbot.banned", activeChar, characterName, punishTime));
							final Player plr = World.getPlayer(characterName);
							if (plr != null)
							{
								plr.kick();
							}
							introduceNewPunishedBotAndClear(characterName);
						}
						else
						{
							activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.admincheckbot.error", activeChar));
						}
					}
					else if (wordList[2].equalsIgnoreCase("BANACC"))
					{
						final String accountName = CharacterDAO.getInstance().getAccountName(characterName);
						if (accountName != null)
						{
							Log.bots("AdminCheckBot: " + characterName + " has been marked as bot and BAN-ACCOUNT by GM " + activeChar.getName() + " forever.");
							AuthServerCommunication.getInstance().sendPacket(new ChangeAccessLevel(accountName, -100, 0));
							final GameClient client = AuthServerCommunication.getInstance().getAuthedClient(accountName);
							if (client != null)
							{
								final Player player = client.getActiveChar();
								if (player != null)
								{
									player.kick();
									activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.admincheckbot.banned1", activeChar, characterName, accountName));
									introduceNewPunishedBotAndClear(characterName);
								}
							}
						}
						else
						{
							activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.admincheckbot.error", activeChar));
						}
					}
				}
				else
				{
					activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.admincheckbot.doesnotexist", activeChar));
				}
			}
		}
		}
		return true;
	}

	private static void sendBotPage(Player activeChar)
	{
		final String html = HtmCache.getInstance().getNotNull("admin/reports.htm", activeChar);
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		final StringBuilder tb = new StringBuilder();
		tb.append("<html><body><table width=260>");
		tb.append("<tr>");
		tb.append("<td width=40>");
		tb.append("<button value=Main action=bypass -h admin_admin width=40 height=21 back=L2UI_ct1.button_df fore=L2UI_ct1.button_df>");
		tb.append("</td>");
		tb.append("<td width=180>");
		tb.append("<font name=hs12><center>Bot Reports</center></font>");
		tb.append("</td>");
		tb.append("<td width=40>");
		tb.append("<button value=Back action=bypass -h admin_admin width=40 height=21 back=L2UI_ct1.button_df fore=L2UI_ct1.button_df>");
		tb.append("</td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("<center>");
		for (int i : AutoHuntingManager.getInstance().getUnread().keySet())
		{
			tb.append("<a action=\"bypass -h admin_readbot " + i + "\">Ticket #" + i + "</a><br1>");
		}
		tb.append("</center></body></html>");
		adminReply.setHtml(html);
		adminReply.replace("%tickets%", tb.toString());
		activeChar.sendPacket(adminReply);
	}

	private static void sendBotInfoPage(Player activeChar, int botId)
	{
		final String[] report = AutoHuntingManager.getInstance().getUnread().get(botId);
		final String html = HtmCache.getInstance().getNotNull("admin/report_page.htm", activeChar);
		final Player reportedTarget = World.getPlayer(report[0]);
		final int punishTimes = AutoHuntingManager.getInstance().getPlayerReportsCount(reportedTarget);
		final String captchaFails = CharacterDAO.getInstance().getUserVar(report[0], "FailedOnCaptchaTest");
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		adminReply.setHtml(html);
		if (reportedTarget == null)
		{
			adminReply.replace("%reported%", "" + report[0]);
		}
		else
		{
			adminReply.replace("%reported%", "<a action=\"bypass -h admin_goto_char_menu " + reportedTarget.getName() + "\">" + reportedTarget.getName() + "</a>");
		}
		adminReply.replace("%captchaFails%", captchaFails == null ? 0 : Integer.parseInt(captchaFails));
		adminReply.replace("%ticket%", "" + botId);
		adminReply.replace("%reportedplayer%", "" + report[0]);
		adminReply.replace("%reporter%", "" + report[1]);
		adminReply.replace("%reportedTimes%", "" + punishTimes);
		adminReply.replace("%date%", "" + TimeUtils.convertDateToString(Long.parseLong(report[2])));
		adminReply.replace("%type%", "" + report[3]);
		adminReply.replace("%info%", "" + report[4]);
		activeChar.sendPacket(adminReply);
	}

	private static void introduceNewPunishedBotAndClear(String charName)
	{
		final int charObjId = CharacterDAO.getInstance().getObjectIdByName(charName);
		if (charObjId == 0)
		{
			return;
		}
		Connection con = null;
		PreparedStatement delStatement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			delStatement = con.prepareStatement("DELETE FROM bot_report WHERE reported_objectId = ?");
			delStatement.setInt(1, charObjId);
			delStatement.execute();
		}
		catch (final Exception e)
		{
			_log.info("AdminCheckBot.introduceNewPunishedBotAndClear(target): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, delStatement);
		}
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
