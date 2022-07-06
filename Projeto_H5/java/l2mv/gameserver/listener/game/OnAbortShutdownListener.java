package l2mv.gameserver.listener.game;

import l2mv.gameserver.Shutdown;
import l2mv.gameserver.listener.GameListener;

public interface OnAbortShutdownListener extends GameListener
{
	void onAbortShutdown(Shutdown.ShutdownMode p0, int p1);
}
