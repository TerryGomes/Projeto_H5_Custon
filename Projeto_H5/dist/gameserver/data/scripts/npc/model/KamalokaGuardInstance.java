package npc.model;

import l2f.gameserver.instancemanager.ReflectionManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public final class KamalokaGuardInstance extends NpcInstance
{
	public KamalokaGuardInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this) || checkForDominionWard(player))
		{
			return;
		}

		if (command.startsWith("kamaloka"))
		{
			int val = Integer.parseInt(command.substring(9));
			Reflection r = player.getActiveReflection();
			if (r != null)
			{
				if (player.canReenterInstance(val))
				{
					player.teleToLocation(r.getTeleportLoc(), r);
				}
			}
			else if (player.canEnterInstance(val))
			{
				ReflectionUtils.enterReflection(player, val);
			}
		}
		else if (command.startsWith("escape"))
		{
			if (player.getParty() == null || !player.getParty().isLeader(player))
			{
				showChatWindow(player, "not_party_leader.htm");
				return;
			}
			player.getReflection().collapse();
		}
		else if (command.startsWith("return"))
		{
			Reflection r = player.getReflection();
			if (r.getReturnLoc() != null)
			{
				player.teleToLocation(r.getReturnLoc(), ReflectionManager.DEFAULT);
			}
			else
			{
				player.setReflection(ReflectionManager.DEFAULT);
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if (checkForDominionWard(player))
		{
			return;
		}

		super.showChatWindow(player, val);
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String pom;
		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}

		return "instance/kamaloka/" + pom + ".htm";
	}
}