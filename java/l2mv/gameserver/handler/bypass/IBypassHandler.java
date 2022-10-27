package l2mv.gameserver.handler.bypass;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;

public interface IBypassHandler
{
	String[] getBypasses();

	void onBypassFeedback(NpcInstance npc, Player player, String command);
}
