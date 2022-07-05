package l2f.gameserver.listener.actor.player;

import l2f.gameserver.listener.PlayerListener;
import l2f.gameserver.model.Player;

public interface OnFishDieListener extends PlayerListener
{
	void onFishDied(Player player, int fishId, boolean isMonster);
}
