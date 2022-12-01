package l2mv.gameserver.handler.voicecommands.impl.BotReport;

import l2mv.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2mv.gameserver.model.Player;

/**
 * Class Handling Bot Reporting
 * It is used, when player is typing .report
 * It also handles Answer of the Captcha that is sent to the Targeted Player.
 */
public class ReportCommand implements IVoicedCommandHandler
{
	private static final String[] COMMANDS =
	{
		"report"
	};

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (target.isEmpty() || !target.startsWith("answer"))
		{
			if (activeChar.getTarget() == null || !activeChar.getTarget().isPlayer())
			{
				activeChar.sendMessage("Target player that might be Bot and write .report");
				return false;
			}

			CaptchaHandler.tryReportPlayer(activeChar, activeChar.getTarget().getPlayer());
		}
		else if (target.startsWith("answer "))// target syntax should be "target ASDSA"
		{
			String answer = target.substring("answer ".length());
			answer = answer.replace(" ", "");
			CaptchaHandler.onAnswerCaptcha(activeChar, answer);
		}
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}
