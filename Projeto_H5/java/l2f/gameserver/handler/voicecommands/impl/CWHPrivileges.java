package l2f.gameserver.handler.voicecommands.impl;

import l2f.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.CCPHelpers.CCPCWHPrivilages;
import l2f.gameserver.scripts.Functions;

public class CWHPrivileges extends Functions implements IVoicedCommandHandler
{
	private static final String[] _commandList = new String[]
	{
		"clan"
	};

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		CCPCWHPrivilages.clanMain(activeChar, args);
		return false;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}