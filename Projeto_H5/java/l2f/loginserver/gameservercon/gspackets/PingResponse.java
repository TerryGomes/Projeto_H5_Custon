package l2f.loginserver.gameservercon.gspackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.loginserver.gameservercon.GameServer;
import l2f.loginserver.gameservercon.ReceivablePacket;

public class PingResponse extends ReceivablePacket
{
	private static final Logger _log = LoggerFactory.getLogger(PingResponse.class);

	private long _serverTime;

	@Override
	protected void readImpl()
	{
		_serverTime = readQ();
	}

	@Override
	protected void runImpl()
	{
		final GameServer gameServer = getGameServer();
		if (!gameServer.isAuthed())
		{
			return;
		}

		gameServer.getConnection().onPingResponse();

		final long diff = System.currentTimeMillis() - _serverTime;
		if (Math.abs(diff) > 999)
		{
			_log.warn("Gameserver " + gameServer.getId() + " [" + gameServer.getName() + "] : time offset " + diff + " ms.");
		}
	}
}