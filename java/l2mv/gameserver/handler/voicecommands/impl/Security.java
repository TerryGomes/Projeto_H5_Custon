package l2mv.gameserver.handler.voicecommands.impl;

import l2mv.gameserver.Config;
import l2mv.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.loginservercon.AuthServerCommunication;
import l2mv.gameserver.network.loginservercon.gspackets.ChangeAllowedIp;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;

public class Security implements IVoicedCommandHandler
{

	private static final String[] _commandList = {};

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{

		if (command.equalsIgnoreCase("lock"))
		{
			NpcHtmlMessage html = new NpcHtmlMessage(activeChar.getObjectId());
			html.setFile("command/lock/lock.htm");
			html.replace("%ip_block%", IpBlockStatus());
			html.replace("%hwid_block%", HwidBlockStatus());
			html.replace("%hwid_val%", "CPU");
			html.replace("%curIP%", activeChar.getIP());
			activeChar.sendPacket(html);
			return true;
		}

		else if (command.equalsIgnoreCase("lockIp"))
		{

			if (!Config.ALLOW_IP_LOCK)
			{
				return true;
			}

			AuthServerCommunication.getInstance().sendPacket(new ChangeAllowedIp(activeChar.getAccountName(), activeChar.getIP()));

			NpcHtmlMessage html = new NpcHtmlMessage(activeChar.getObjectId());
			html.setFile("command/lock/lock_ip.htm");
			html.replace("%curIP%", activeChar.getIP());
			activeChar.sendPacket(html);
			return true;
		}

		else if (command.equalsIgnoreCase("lockHwid"))
		{

			if (!Config.ALLOW_HWID_LOCK)
			{
				return true;
			}

			if (!activeChar.getInventory().destroyItemByItemId(4037, 1, "Security"))
			{
				activeChar.sendMessage("In order to secure your account you should pay 5 Security Coin");
				return false;
			}

			activeChar.setHwidLock(activeChar.getHWID());
			NpcHtmlMessage html = new NpcHtmlMessage(activeChar.getObjectId());
			html.setFile("command/lock/lock_hwid.htm");
			activeChar.sendPacket(html);

			return true;
		}

		else if (command.equalsIgnoreCase("unlockIp"))
		{

			AuthServerCommunication.getInstance().sendPacket(new ChangeAllowedIp(activeChar.getAccountName(), ""));

			NpcHtmlMessage html = new NpcHtmlMessage(activeChar.getObjectId());
			html.setFile("command/lock/unlock_ip.htm");
			html.replace("%curIP", activeChar.getIP());
			activeChar.sendPacket(html);
			return true;
		}

		else if (command.equalsIgnoreCase("unlockHwid"))
		{

			activeChar.setHwidLock(null);

			NpcHtmlMessage html = new NpcHtmlMessage(activeChar.getObjectId());
			html.setFile("command/lock/unlock_hwid.htm");
			activeChar.sendPacket(html);

			return true;
		}

		return true;
	}

	private String IpBlockStatus()
	{
		if (Config.ALLOW_IP_LOCK)
		{
			return "Allowed";
		}
		return "Prohibited";
	}

	private String HwidBlockStatus()
	{
		if (Config.ALLOW_HWID_LOCK)
		{
			return "Allowed";
		}
		return "Prohibited";
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}