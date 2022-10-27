package l2mv.gameserver.handler.voicecommands.impl;

import l2mv.gameserver.Config;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2mv.gameserver.instancemanager.AutoHuntingManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;

public class ReportBot implements IVoicedCommandHandler
{
	private static final String[] COMMANDS = new String[]
	{
		/* "report" */
	};

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		final String htmlreport = HtmCache.getInstance().getNotNull("command/report.htm", activeChar);
		if (args == null || args == "" || args.isEmpty())
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setHtml(htmlreport);
			html.replace("%reported%", activeChar.getTarget() == null ? "Please select target to report or type his name." : activeChar.getTarget().isPlayer() ? activeChar.getTarget().getName() : "You can report only players.");
			activeChar.sendPacket(html);
			return false;
		}
		final String[] paramSplit = args.split(" ");
		if (paramSplit[0].equalsIgnoreCase("Bot") && paramSplit.length != 1)
		{
			activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.voicecommands.impl.randomcommands.message4", activeChar));
			return false;
		}
		final StringBuilder sb = new StringBuilder();
		for (String other : paramSplit)
		{
			other = other.replace("Bot", "");
			other = other.replace("Abuse", "");
			other = other.replace("FakeShop", "");
			other = other.replace("\n", " ");
			sb.append(other + " ");
		}
		final String fullMsg = sb.toString();
		if (fullMsg.length() > 150)
		{
			activeChar.sendMessage("You have exceeded maximum allowed characters for report. Maximum lenght: 150 characters.");
			return false;
		}
		botReportcommand(activeChar, paramSplit[0], sb.toString());
		return true;
	}

	private void botReportcommand(Player activeChar, String typeofreport, String moreinfo)
	{
		if (Config.ENABLE_AUTO_HUNTING_REPORT)
		{
			if (activeChar.getTarget() != null && activeChar.getTarget().isPlayer() && !typeofreport.isEmpty() && typeofreport != null)
			{
				final Player reported = activeChar.getTarget().getPlayer();
				if ((reported == null) || !AutoHuntingManager.getInstance().validateBot(reported, activeChar) || !AutoHuntingManager.getInstance().validateReport(activeChar))
				{
					return;
				}
				try
				{
					AutoHuntingManager.getInstance().reportBot(reported, activeChar, typeofreport, moreinfo);
				}
				catch (final Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.voicecommands.impl.randomcommands.message15", activeChar));
			}
		}
		else
		{
			activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.voicecommands.impl.randomcommands.message16", activeChar));
		}
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}