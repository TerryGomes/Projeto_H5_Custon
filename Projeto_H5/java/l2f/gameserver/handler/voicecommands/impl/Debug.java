package l2f.gameserver.handler.voicecommands.impl;

import l2f.gameserver.Config;
import l2f.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.components.CustomMessage;

public class Debug implements IVoicedCommandHandler
{
	private static final String[] _commandList = {};

	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}

	@Override
	public boolean useVoicedCommand(String command, Player player, String args)
	{
		if (!Config.ALT_DEBUG_ENABLED)
		{
			return false;
		}

		if (player.isDebug())
		{
			player.setDebug(false);
			player.sendMessage(new CustomMessage("voicedcommandhandlers.Debug.Disabled", player));
		}
		else
		{
			player.setDebug(true);
			player.sendMessage(new CustomMessage("voicedcommandhandlers.Debug.Enabled", player));
		}
		return true;
	}
}
