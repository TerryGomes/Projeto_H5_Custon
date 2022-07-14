package npc.model;

import bosses.SailrenManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.ItemFunctions;

/**
 * @author pchayka
 */

public final class SairlenGatekeeperInstance extends NpcInstance
{
	private static final int GAZKH = 8784;

	public SairlenGatekeeperInstance(int objectId, NpcTemplate template)
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

		if (command.startsWith("request_entrance"))
		{
			if (player.getLevel() < 75)
			{
				showChatWindow(player, "default/32109-3.htm");
			}
			else if (ItemFunctions.getItemCount(player, GAZKH) > 0)
			{
				int check = SailrenManager.canIntoSailrenLair(player);
				switch (check)
				{
				case 1:
				case 2:
					showChatWindow(player, "default/32109-5.htm");
					break;
				case 3:
					showChatWindow(player, "default/32109-4.htm");
					break;
				case 4:
					showChatWindow(player, "default/32109-1.htm");
					break;
				case 0:
					ItemFunctions.removeItem(player, GAZKH, 1, true, "SairlenGatekeeperInstance");
					SailrenManager.setSailrenSpawnTask();
					SailrenManager.entryToSailrenLair(player);
					break;
				default:
					break;
				}
			}
			else
			{
				showChatWindow(player, "default/32109-2.htm");
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}