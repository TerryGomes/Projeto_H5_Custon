package l2mv.gameserver.handler.petition;

import l2mv.gameserver.model.Player;

public interface IPetitionHandler
{
	void handle(Player player, int id, String txt);
}
