package l2mv.gameserver.listener.actor.player;

import l2mv.gameserver.listener.PlayerListener;
import l2mv.gameserver.model.Player;

public interface OnFishDieListener extends PlayerListener
{
	void onFishDied(Player player, int fishId, boolean isMonster);
}
