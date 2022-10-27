package l2mv.gameserver.listener.actor.player;

import l2mv.gameserver.listener.PlayerListener;
import l2mv.gameserver.model.Player;

public interface OnTeleportedListener extends PlayerListener
{
	void onTeleported(Player p0);
}
