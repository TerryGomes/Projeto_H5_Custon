package l2mv.gameserver.listener.actor.player;

import l2mv.gameserver.listener.PlayerListener;
import l2mv.gameserver.model.Player;

public interface OnQuestionMarkListener extends PlayerListener
{
	public void onQuestionMarkClicked(Player player, int questionMarkId);
}
