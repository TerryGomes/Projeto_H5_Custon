package npc.model;

import instances.FreyaHard;
import instances.FreyaNormal;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public final class JiniaNpcInstance extends NpcInstance
{
	private static final int normalFreyaIzId = 139;
	private static final int extremeFreyaIzId = 144;

	public JiniaNpcInstance(int objectId, NpcTemplate template)
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

		if (command.equalsIgnoreCase("request_normalfreya"))
		{
			Reflection r = player.getActiveReflection();
			if (r != null)
			{
				if (player.canReenterInstance(normalFreyaIzId))
				{
					player.teleToLocation(r.getTeleportLoc(), r);
				}
			}
			else if (player.canEnterInstance(normalFreyaIzId))
			{
				ReflectionUtils.enterReflection(player, new FreyaNormal(), normalFreyaIzId);
			}
		}
		else if (command.equalsIgnoreCase("request_extremefreya"))
		{
			Reflection r = player.getActiveReflection();
			if (r != null)
			{
				if (player.canReenterInstance(extremeFreyaIzId))
				{
					player.teleToLocation(r.getTeleportLoc(), r);
				}
			}
			else if (player.canEnterInstance(extremeFreyaIzId))
			{
				ReflectionUtils.enterReflection(player, new FreyaHard(), extremeFreyaIzId);
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}