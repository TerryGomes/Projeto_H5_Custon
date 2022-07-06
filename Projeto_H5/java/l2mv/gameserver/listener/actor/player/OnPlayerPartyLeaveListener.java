package l2mv.gameserver.listener.actor.player;

import l2mv.gameserver.listener.PlayerListener;
import l2mv.gameserver.model.Player;

public interface OnPlayerPartyLeaveListener extends PlayerListener
{
	public void onPartyLeave(Player player);
}
