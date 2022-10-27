package npc.model;

import instances.CrystalCaverns;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.ReflectionUtils;

public class CoralGardenGateInstance extends NpcInstance
{
	private static final long serialVersionUID = -1L;

	public CoralGardenGateInstance(int objectId, NpcTemplate template)
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

		if (command.equalsIgnoreCase("request_coralg"))
		{
			Reflection r = player.getActiveReflection();
			if (r != null)
			{
				if (player.canReenterInstance(10))
				{
					player.teleToLocation(r.getTeleportLoc(), r);
				}
			}
			else if (player.canEnterInstance(10))
			{
				ReflectionUtils.enterReflection(player, new CrystalCaverns(), 10);
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}
