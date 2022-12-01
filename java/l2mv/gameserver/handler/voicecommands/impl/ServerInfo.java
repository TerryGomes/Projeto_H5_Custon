package l2mv.gameserver.handler.voicecommands.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import l2mv.gameserver.GameServer;
import l2mv.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.scripts.Functions;

public class ServerInfo extends Functions implements IVoicedCommandHandler
{
	private static final String[] _commandList =
	{
		"revisao"
	};

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (command.equals("revisao"))
		{
			activeChar.sendMessage("Revision: Final Revision");
			activeChar.sendMessage("Build date: " + GameServer.getInstance().getVersion().getBuildDate());
			activeChar.sendMessage("Build Versao: " + GameServer.getInstance().getVersion().getVersionNumber());
			activeChar.sendMessage("Build Revisao: " + GameServer.getInstance().getVersion().getRevisionNumber());
			activeChar.sendMessage(DATE_FORMAT.format(new Date(System.currentTimeMillis())));
		}
		return false;
	}
}
