package l2f.gameserver.listener.actor.player;

import l2f.gameserver.listener.PlayerListener;

public interface OnAnswerListener extends PlayerListener
{
	void sayYes();

	void sayNo();
}
