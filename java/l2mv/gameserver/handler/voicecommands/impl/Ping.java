package l2mv.gameserver.handler.voicecommands.impl;

import l2mv.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.CCPHelpers.CCPSmallCommands;
import l2mv.gameserver.scripts.Functions;

/**
 * @author claww
 */
public class Ping extends Functions implements IVoicedCommandHandler
{
	private static final String[] COMMANDS =
	{
		"ping"
	};

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		CCPSmallCommands.getPing(activeChar);
		return false;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}