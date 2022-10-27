package services;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.scripts.Functions;

public class TakeBeastHandler extends Functions
{
	private final int BEAST_WHIP = 15473;

	public void show()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();

		if (player == null || npc == null || !npc.isInRange(player, 1000L))
		{
			return;
		}

		String htmltext;
		if (player.getLevel() < 82)
		{
			htmltext = npc.getNpcId() + "-1.htm";
		}
		else if (Functions.getItemCount(player, BEAST_WHIP) > 0)
		{
			htmltext = npc.getNpcId() + "-2.htm";
		}
		else
		{
			Functions.addItem(player, BEAST_WHIP, 1, "TakeBeastHandler");
			htmltext = npc.getNpcId() + "-3.htm";
		}

		npc.showChatWindow(player, "default/" + htmltext);
	}
}
