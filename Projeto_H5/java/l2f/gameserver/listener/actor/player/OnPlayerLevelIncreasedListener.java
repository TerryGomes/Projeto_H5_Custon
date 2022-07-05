package l2f.gameserver.listener.actor.player;

import l2f.gameserver.listener.PlayerListener;
import l2f.gameserver.model.Player;

public interface OnPlayerLevelIncreasedListener extends PlayerListener
{
	void onLevelIncreased(Player p0);
}
