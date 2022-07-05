package l2f.gameserver.handler.voicecommands.impl;

import l2f.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.CCPHelpers.CCPSmallCommands;

public class AntiGrief implements IVoicedCommandHandler
{
	private static final String[] COMMANDS =
	{
		"buffshield",
		"buffShield"
	};

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		CCPSmallCommands.setAntiGrief(activeChar);
		return false;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}

}
