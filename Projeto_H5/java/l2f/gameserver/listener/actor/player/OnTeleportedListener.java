package l2f.gameserver.listener.actor.player;

import l2f.gameserver.listener.PlayerListener;
import l2f.gameserver.model.Player;

public interface OnTeleportedListener extends PlayerListener
{
	void onTeleported(Player p0);
}
