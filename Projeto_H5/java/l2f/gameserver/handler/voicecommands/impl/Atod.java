package l2f.gameserver.handler.voicecommands.impl;

import l2f.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.CCPHelpers.CCPSmallCommands;
import l2f.gameserver.scripts.Functions;

public class Atod extends Functions implements IVoicedCommandHandler
{
	private static final String[] COMMANDS = {};

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String params)
	{
		if (command.equalsIgnoreCase("openatod"))
		{
			if (params == null)
			{
				activeChar.sendMessage("Usage: .openatod <num>");
			}
			else
			{
				int num = 0;
				try
				{
					num = Integer.parseInt(params);
				}
				catch (NumberFormatException nfe)
				{
					activeChar.sendMessage("You must enter a number. Usage: .openatod <num>");
					return false;
				}

				CCPSmallCommands.openToad(activeChar, num);
			}

		}
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}

}