package npc.model;

import bosses.AntharasManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.templates.npc.NpcTemplate;

/**
 * @author pchayka
 */

public final class HeartOfWardingInstance extends NpcInstance
{
	public HeartOfWardingInstance(int objectId, NpcTemplate template)
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

		if (command.equalsIgnoreCase("enter_lair"))
		{
			AntharasManager.enterTheLair(player);
			return;
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}