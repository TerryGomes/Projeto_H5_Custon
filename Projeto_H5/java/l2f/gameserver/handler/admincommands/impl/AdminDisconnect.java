package l2f.gameserver.handler.admincommands.impl;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.handler.admincommands.IAdminCommandHandler;
import l2f.gameserver.model.GameObject;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.network.serverpackets.components.SystemMsg;

public class AdminDisconnect implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_disconnect, admin_kick, admin_kick_count
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.getPlayerAccess().CanKick)
		{
			return false;
		}

		switch (command)
		{
		case admin_disconnect:
		case admin_kick:
			final Player player;
			if (wordList.length == 1)
			{
				// Обработка по таргету
				GameObject target = activeChar.getTarget();
				if (target == null)
				{
					activeChar.sendMessage("Select character or specify player name.");
					break;
				}
				if (!target.isPlayer())
				{
					activeChar.sendPacket(SystemMsg.INVALID_TARGET);
					break;
				}
				player = (Player) target;
			}
			else
			{
				// Обработка по нику
				player = World.getPlayer(wordList[1]);
				if (player == null)
				{
					activeChar.sendMessage("Character " + wordList[1] + " not found in game.");
					break;
				}
			}

			activeChar.sendMessage("Character " + player.getName() + " disconnected from server.");

			if (player.isInOfflineMode())
			{
				player.setOfflineMode(false);
				player.kick();
				return true;
			}

			player.sendMessage(new CustomMessage("admincommandhandlers.AdminDisconnect.YoureKickedByGM", player));
			player.sendPacket(SystemMsg.YOU_HAVE_BEEN_DISCONNECTED_FROM_THE_SERVER_);
			ThreadPoolManager.getInstance().schedule(new RunnableImpl()
			{
				@Override
				public void runImpl() throws Exception
				{
					player.kick();
				}
			}, 500);
			break;
		case admin_kick_count:
			int toKickCount = Integer.parseInt(wordList[1]);
			int kickedCount = 0;
			for (Player playerToKick : GameObjectsStorage.getAllPlayersForIterate())
			{
				if (playerToKick.isOnline() && playerToKick.getNetConnection() != null && !playerToKick.equals(activeChar))
				{
					ThreadPoolManager.getInstance().schedule(new RunnableImpl()
					{
						@Override
						public void runImpl() throws Exception
						{
							playerToKick.kick();
						}
					}, 500);
					kickedCount++;
					if (toKickCount <= kickedCount)
					{
						break;
					}
				}
			}
			activeChar.sendMessage("Kicked " + kickedCount + " players!");
			break;
		}
		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}