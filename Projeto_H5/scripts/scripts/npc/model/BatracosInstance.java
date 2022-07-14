package npc.model;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public final class BatracosInstance extends NpcInstance
{
	private static final int urogosIzId = 505;

	public BatracosInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if (val == 0)
		{
			String htmlpath = null;
			if (getReflection().isDefault())
			{
				htmlpath = "default/32740.htm";
			}
			else
			{
				htmlpath = "default/32740-4.htm";
			}
			player.sendPacket(new NpcHtmlMessage(player, this, htmlpath, val));
		}
		else
		{
			super.showChatWindow(player, val);
		}
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		if (command.equalsIgnoreCase("request_seer"))
		{
			Reflection r = player.getActiveReflection();
			if (r != null)
			{
				if (player.canReenterInstance(urogosIzId))
				{
					player.teleToLocation(r.getTeleportLoc(), r);
				}
			}
			else if (player.canEnterInstance(urogosIzId))
			{
				ReflectionUtils.enterReflection(player, urogosIzId);
			}
		}
		else if (command.equalsIgnoreCase("leave"))
		{
			if (!getReflection().isDefault())
			{
				getReflection().collapse();
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}