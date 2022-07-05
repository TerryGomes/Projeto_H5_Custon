package l2f.gameserver.listener.game;

import l2f.gameserver.Shutdown;
import l2f.gameserver.listener.GameListener;

public interface OnAbortShutdownListener extends GameListener
{
	void onAbortShutdown(Shutdown.ShutdownMode p0, int p1);
}
