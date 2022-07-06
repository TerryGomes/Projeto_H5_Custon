package l2mv.gameserver.listener.actor.player;

import l2mv.gameserver.listener.PlayerListener;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.Reflection;

public interface OnTeleportListener extends PlayerListener
{
	public void onTeleport(Player player, int x, int y, int z, Reflection reflection);
}
