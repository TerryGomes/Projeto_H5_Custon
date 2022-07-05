package l2f.gameserver.listener.actor.player;

import l2f.gameserver.listener.PlayerListener;
import l2f.gameserver.model.Player;

public interface OnPlayerPartyInviteListener extends PlayerListener
{
	public void onPartyInvite(Player player);
}
