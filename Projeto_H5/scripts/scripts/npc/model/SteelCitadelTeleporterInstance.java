package npc.model;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */
public class SteelCitadelTeleporterInstance extends NpcInstance
{
	public SteelCitadelTeleporterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		if (!player.isInParty())
		{
			showChatWindow(player, "default/32745-1.htm");
			return;
		}
		if ((player.getParty().getLeader() != player) || !rangeCheck(player))
		{
			showChatWindow(player, "default/32745-2.htm");
			return;
		}

		if (command.equalsIgnoreCase("01_up"))
		{
			player.getParty().Teleport(new Location(-22208, 277122, -13376));
			return;
		}
		else if (command.equalsIgnoreCase("02_up"))
		{
			player.getParty().Teleport(new Location(-22208, 277106, -11648));
			return;
		}
		else if (command.equalsIgnoreCase("02_down"))
		{
			player.getParty().Teleport(new Location(-22208, 277074, -15040));
			return;
		}
		else if (command.equalsIgnoreCase("03_up"))
		{
			player.getParty().Teleport(new Location(-22208, 277120, -9920));
			return;
		}
		else if (command.equalsIgnoreCase("03_down"))
		{
			player.getParty().Teleport(new Location(-22208, 277120, -13376));
			return;
		}
		else if (command.equalsIgnoreCase("04_up"))
		{
			player.getParty().Teleport(new Location(-19024, 277126, -8256));
			return;
		}
		else if (command.equalsIgnoreCase("04_down"))
		{
			player.getParty().Teleport(new Location(-22208, 277106, -11648));
			return;
		}
		else if (command.equalsIgnoreCase("06_up"))
		{
			player.getParty().Teleport(new Location(-19024, 277106, -9920));
			return;
		}
		else if (command.equalsIgnoreCase("06_down"))
		{
			player.getParty().Teleport(new Location(-22208, 277122, -9920));
			return;
		}
		else if (command.equalsIgnoreCase("07_up"))
		{
			player.getParty().Teleport(new Location(-19008, 277100, -11648));
			return;
		}
		else if (command.equalsIgnoreCase("07_down"))
		{
			player.getParty().Teleport(new Location(-19024, 277122, -8256));
			return;
		}
		else if (command.equalsIgnoreCase("08_up"))
		{
			player.getParty().Teleport(new Location(-19008, 277100, -13376));
			return;
		}
		else if (command.equalsIgnoreCase("08_down"))
		{
			player.getParty().Teleport(new Location(-19008, 277106, -9920));
			return;
		}
		else if (command.equalsIgnoreCase("09_up"))
		{
			player.getParty().Teleport(new Location(14602, 283179, -7500));
			return;
		}
		else if (command.equalsIgnoreCase("09_down"))
		{
			player.getParty().Teleport(new Location(-19008, 277100, -11648));
			return;
		}
		else if (command.equalsIgnoreCase("facedemon"))
		{
			enterInstance(player, 5);
			return;
		}
		else if (command.equalsIgnoreCase("faceranku"))
		{
			enterInstance(player, 6);
			return;
		}
		else if (command.equalsIgnoreCase("leave"))
		{
			player.getReflection().collapse();
			return;
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	private boolean rangeCheck(Player pl)
	{
		for (Player m : pl.getParty().getMembers())
		{
			if (!pl.isInRange(m, 400))
			{
				return false;
			}
		}
		return true;
	}

	private int getIz(int floor)
	{
		if (floor == 5)
		{
			return 3;
		}
		else
		{
			return 4;
		}
	}

	private void enterInstance(Player player, int floor)
	{
		Reflection r = player.getActiveReflection();
		if (r != null)
		{
			if (player.canReenterInstance(getIz(floor)))
			{
				player.teleToLocation(r.getTeleportLoc(), r);
			}
		}
		else if (player.canEnterInstance(getIz(floor)))
		{
			ReflectionUtils.enterReflection(player, getIz(floor));
		}
	}
}