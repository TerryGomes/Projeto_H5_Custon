package l2f.gameserver.listener.actor.player;

import l2f.gameserver.listener.PlayerListener;
import l2f.gameserver.model.Player;

public interface OnPlayerExitListener extends PlayerListener
{
	public void onPlayerExit(Player player);
}
