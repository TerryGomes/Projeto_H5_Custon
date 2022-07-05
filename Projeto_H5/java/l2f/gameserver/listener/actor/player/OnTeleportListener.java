package l2f.gameserver.listener.actor.player;

import l2f.gameserver.listener.PlayerListener;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.Reflection;

public interface OnTeleportListener extends PlayerListener
{
	public void onTeleport(Player player, int x, int y, int z, Reflection reflection);
}
