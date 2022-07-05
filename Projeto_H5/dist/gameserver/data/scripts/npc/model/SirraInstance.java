package npc.model;

import org.apache.commons.lang3.ArrayUtils;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.DoorInstance;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.ExChangeClientEffectInfo;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.Location;

/**
 * @author pchayka
 */
public class SirraInstance extends NpcInstance
{

	private static final int[] questInstances =
	{
		140,
		138,
		141
	};
	private static final int[] warInstances =
	{
		139,
		144
	};

	public SirraInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String htmlpath = null;
		if (ArrayUtils.contains(questInstances, getReflection().getInstancedZoneId()))
		{
			htmlpath = "default/32762.htm";
		}
		else if (ArrayUtils.contains(warInstances, getReflection().getInstancedZoneId()))
		{
			DoorInstance door = getReflection().getDoor(23140101);
			if (door.isOpen())
			{
				htmlpath = "default/32762_opened.htm";
			}
			else
			{
				htmlpath = "default/32762_closed.htm";
			}
		}
		else
		{
			htmlpath = "default/32762.htm";
		}
		return htmlpath;
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		if (command.equalsIgnoreCase("teleport_in"))
		{
			for (NpcInstance n : getReflection().getNpcs())
			{
				if (n.getNpcId() == 29179 || n.getNpcId() == 29180)
				{
					player.sendPacket(new ExChangeClientEffectInfo(2));
				}
			}
			player.teleToLocation(new Location(114712, -113544, -11225));
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}