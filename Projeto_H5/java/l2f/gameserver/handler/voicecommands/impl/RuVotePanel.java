package l2f.gameserver.handler.voicecommands.impl;

import java.util.concurrent.TimeUnit;

import l2f.gameserver.Config;
import l2f.gameserver.ConfigHolder;
import l2f.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2f.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.vote.RuVoteAnnounceTask;
import l2f.gameserver.vote.RuVotesHolder;

public class RuVotePanel implements IVoicedCommandHandler, ScriptFile
{
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String params)
	{
		if (!Config.ENABLE_RU_VOTE_SYSTEM)
		{
			return false;
		}

		final boolean submittingVote = RuVoteAnnounceTask.getInstance().onPanelOpened(activeChar);
		if (submittingVote && !Config.RU_VOTE_PANEL_MSG.isEmpty())
		{
			activeChar.sendPacket(new Say2(0, ChatType.TELL, ConfigHolder.getString("ServerName"), Config.RU_VOTE_PANEL_MSG));
		}
		showPanel(activeChar);
		return true;
	}

	private static void showPanel(Player activeChar)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		if (RuVotesHolder.getInstance().checkHavePenalty(activeChar, true))
		{
			html.setFile("command/ruVotePanelPenalty.htm");
			html.replace("${timeToWipe}", getTimeToWipe());
		}
		else
		{
			html.setFile("command/ruVotePanel.htm");
			html.replace("${RuVoteLinkToVote}", ConfigHolder.getString("RuVoteLinkToVote"));
			html.replace("${RuVoteLinkToGuide}", ConfigHolder.getString("RuVoteLinkToGuide"));
		}

		html.replace("${ServerName}", Config.SERVER_NAME);
		activeChar.sendPacket(html);
	}

	private static String getTimeToWipe()
	{
		final long timeToWipe = Config.RU_VOTE_PATTERN.next(System.currentTimeMillis()) - System.currentTimeMillis();
		if (timeToWipe > TimeUnit.HOURS.toMillis(1L))
		{
			return timeToWipe / TimeUnit.HOURS.toMillis(1L) + " Hours";
		}
		if (timeToWipe > TimeUnit.MINUTES.toMillis(1L))
		{
			return timeToWipe / TimeUnit.MINUTES.toMillis(1L) + " Minutes";
		}
		if (timeToWipe > TimeUnit.SECONDS.toMillis(1L))
		{
			return timeToWipe / TimeUnit.SECONDS.toMillis(1L) + " Seconds";
		}
		return "";
	}

	@Override
	public void onLoad()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
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
	public String[] getVoicedCommandList()
	{
		if (Config.RU_VOTE_PANEL_COMMAND.isEmpty())
		{
			return new String[0];
		}
		return new String[]
		{
			Config.RU_VOTE_PANEL_COMMAND
		};
	}
}
