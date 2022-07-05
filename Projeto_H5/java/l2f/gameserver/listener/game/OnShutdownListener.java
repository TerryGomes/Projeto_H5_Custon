package l2f.gameserver.listener.game;

import l2f.gameserver.Shutdown;
import l2f.gameserver.listener.GameListener;

public interface OnShutdownListener extends GameListener
{
	void onShutdown(Shutdown.ShutdownMode p0);
}
