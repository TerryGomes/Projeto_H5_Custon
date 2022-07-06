package l2mv.gameserver.handler.voicecommands.impl;

import l2mv.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.CCPHelpers.CCPOffline;
import l2mv.gameserver.scripts.Functions;

public class Offline extends Functions implements IVoicedCommandHandler
{
	private static final String[] COMMANDS = new String[]
	{
		"offline"
	};

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		CCPOffline.setOfflineStore(activeChar);
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}