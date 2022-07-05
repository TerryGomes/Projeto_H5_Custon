package l2f.gameserver.handler.bypass;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;

public interface IBypassHandler
{
	String[] getBypasses();

	void onBypassFeedback(NpcInstance npc, Player player, String command);
}
