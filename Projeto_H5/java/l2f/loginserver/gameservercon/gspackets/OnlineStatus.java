package l2f.loginserver.gameservercon.gspackets;

import l2f.loginserver.gameservercon.GameServer;
import l2f.loginserver.gameservercon.ReceivablePacket;

/**
 * @author VISTALL
 * @date 21:40/28.06.2011
 */
public class OnlineStatus extends ReceivablePacket
{
	private boolean _online;

	@Override
	protected void readImpl()
	{
		_online = readC() == 1;
	}

	@Override
	protected void runImpl()
	{
		final GameServer gameServer = getGameServer();
		if (!gameServer.isAuthed())
		{
			return;
		}

		gameServer.setOnline(_online);
	}
}
