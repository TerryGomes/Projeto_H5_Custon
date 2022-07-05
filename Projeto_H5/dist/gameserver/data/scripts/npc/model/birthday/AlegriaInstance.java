package npc.model.birthday;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.ItemFunctions;

/**
 * @author claww
 * @date 21.01.2013
 */
@SuppressWarnings("serial")
public class AlegriaInstance extends NpcInstance
{
	private static final int EXPLORERHAT = 10250;
	private static final int HAT = 13488; // Birthday Hat

	public AlegriaInstance(int objectId, NpcTemplate template)
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

		if (command.equalsIgnoreCase("exchangeHat"))
		{
			if (ItemFunctions.getItemCount(player, EXPLORERHAT) < 1)
			{
				showChatWindow(player, "default/32600-nohat.htm");
				return;
			}

			ItemFunctions.removeItem(player, EXPLORERHAT, 1, true, "AlegriaInstance");
			ItemFunctions.addItem(player, HAT, 1, true, "AlegriaInstance");

			showChatWindow(player, "default/32600-successful.htm");

			deleteMe();
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}
