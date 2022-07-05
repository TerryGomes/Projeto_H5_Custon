package l2f.gameserver.listener.game;

import l2f.gameserver.listener.GameListener;

public interface OnDayNightChangeListener extends GameListener
{
	public void onDay();

	public void onNight();
}
