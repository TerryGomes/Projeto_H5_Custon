package l2f.gameserver.handler.petition;

import l2f.gameserver.model.Player;

public interface IPetitionHandler
{
	void handle(Player player, int id, String txt);
}
