package l2mv.gameserver.handler.admincommands.impl;

import l2mv.gameserver.handler.admincommands.IAdminCommandHandler;
import l2mv.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.Say2;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.tables.GmListTable;

public class AdminGmChat implements IAdminCommandHandler, OnPlayerEnterListener
{
	private static enum Commands
	{
		admin_gmchat,
		admin_snoop,
		admin_unsnoop,
		admin_global_on,
		admin_global_off
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.getPlayerAccess().CanAnnounce)
		{
			return false;
		}

		switch (command)
		{
		case admin_gmchat:
			try
			{
				String text = fullString.replaceFirst(Commands.admin_gmchat.name(), "");
				Say2 cs = new Say2(0, ChatType.ALLIANCE, activeChar.getName(), text);
				GmListTable.broadcastToGMs(cs);
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
			break;
		case admin_snoop:
		{
			GameObject target = activeChar.getTarget();
			if (target == null)
			{
				activeChar.sendMessage("You must select a target.");
				return false;
			}
			if (!target.isPlayer())
			{
				activeChar.sendMessage("Target must be a player.");
				return false;
			}
			Player player = (Player) target;
			player.addSnooper(activeChar);
			activeChar.addSnooped(player);
			break;
		}
		case admin_unsnoop:
		{
			GameObject target = activeChar.getTarget();
			if (target == null)
			{
				activeChar.sendMessage("You must select a target.");
				return false;
			}
			if (!target.isPlayer())
			{
				activeChar.sendMessage("Target must be a player.");
				return false;
			}
			Player player = (Player) target;
			activeChar.removeSnooped(player);
			activeChar.sendMessage("stoped snooping player: " + target.getName());
			break;
		}
		case admin_global_on:
		{
			activeChar.sendMessage("You will now receive Shout & Trade from all zones!");
			activeChar.setVar("GlobalOn", "true", -1L);
			break;
		}
		case admin_global_off:
		{
			activeChar.sendMessage("You will no longer receive Shout & Trade from all zones!");
			activeChar.unsetVar("GlobalOn");
			break;
		}
		}
		return true;
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		if (player.getVarB("GlobalOn", false))
		{
			player.sendMessage("You will now receive Shout & Trade from all zones!");
		}
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}