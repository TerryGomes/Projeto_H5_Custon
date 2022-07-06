package npc.model;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.Location;

@SuppressWarnings("serial")
public class TullyWorkShopTeleporterInstance extends NpcInstance
{
	public TullyWorkShopTeleporterInstance(int objectId, NpcTemplate template)
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
			showChatWindow(player, "default/32753-1.htm");
			return;
		}
		if ((player.getParty().getLeader() != player) || !rangeCheck(player))
		{
			showChatWindow(player, "default/32753-2.htm");
			return;
		}

		if (command.equalsIgnoreCase("01_up"))
		{
			player.getParty().Teleport(new Location(-12700, 273340, -13600));
			return;
		}
		else if (command.equalsIgnoreCase("02_up"))
		{
			player.getParty().Teleport(new Location(-13246, 275740, -11936));
			return;
		}
		else if (command.equalsIgnoreCase("02_down"))
		{
			player.getParty().Teleport(new Location(-12894, 273900, -15296));
			return;
		}
		else if (command.equalsIgnoreCase("03_up"))
		{
			player.getParty().Teleport(new Location(-12798, 273458, -10496));
			return;
		}
		else if (command.equalsIgnoreCase("03_down"))
		{
			player.getParty().Teleport(new Location(-12718, 273490, -13600));
			return;
		}
		else if (command.equalsIgnoreCase("04_up"))
		{
			player.getParty().Teleport(new Location(-13500, 275912, -9032));
			return;
		}
		else if (command.equalsIgnoreCase("04_down"))
		{
			player.getParty().Teleport(new Location(-13246, 275740, -11936));
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
}