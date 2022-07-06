package l2mv.gameserver.listener.actor.player;

import l2mv.gameserver.listener.PlayerListener;

public interface OnAnswerListener extends PlayerListener
{
	void sayYes();

	void sayNo();
}
