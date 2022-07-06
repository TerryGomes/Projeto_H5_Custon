package l2mv.gameserver.handler.admincommands.impl;

import l2mv.gameserver.handler.admincommands.IAdminCommandHandler;
import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Zone;
import l2mv.gameserver.taskmanager.GlobalPvPZoneTaskManager;

public class AdminGlobalPvpEvent implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_start_global_pvp_event, admin_stop_global_pvp_event
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		switch (command)
		{
		case admin_start_global_pvp_event:
		{
			for (Zone zone : ReflectionManager.DEFAULT.getZones())
			{
				if (zone.getType() == Zone.ZoneType.global_pvp_zone)
				{
					zone.setActive(true);
				}
			}

			for (Player player : GameObjectsStorage.getAllPlayersCopy())
			{
				GlobalPvPZoneTaskManager.getInstance().sendMainHtmlToPlayer(player);
			}
			GlobalPvPZoneTaskManager.getInstance().setGlobalPvpOn(true);
			GlobalPvPZoneTaskManager.getInstance().startThread();
			break;
		}
		case admin_stop_global_pvp_event:
		{
			for (Zone zone : ReflectionManager.DEFAULT.getZones())
			{
				if (zone.getType() == Zone.ZoneType.global_pvp_zone)
				{
					zone.setActive(false);
				}
			}
			GlobalPvPZoneTaskManager.getInstance().setGlobalPvpOn(false);
			GlobalPvPZoneTaskManager.getInstance().stopThread();
			break;
		}
		default:
		{
			return false;
		}
		}

		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
