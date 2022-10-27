package l2mv.gameserver.listener.game;

import l2mv.gameserver.Shutdown;
import l2mv.gameserver.listener.GameListener;

public interface OnShutdownListener extends GameListener
{
	void onShutdown(Shutdown.ShutdownMode p0);
}
