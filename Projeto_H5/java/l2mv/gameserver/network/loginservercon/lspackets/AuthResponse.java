package l2mv.gameserver.network.loginservercon.lspackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.network.loginservercon.AuthServerCommunication;
import l2mv.gameserver.network.loginservercon.ReceivablePacket;
import l2mv.gameserver.network.loginservercon.gspackets.OnlineStatus;
import l2mv.gameserver.network.loginservercon.gspackets.PlayerInGame;

public class AuthResponse extends ReceivablePacket
{
	private static final Logger _log = LoggerFactory.getLogger(AuthResponse.class);

	private int _serverId;
	private String _serverName;

	@Override
	protected void readImpl()
	{
		_serverId = readC();
		_serverName = readS();
	}

	@Override
	protected void runImpl()
	{
		_log.info("Registered on authserver as " + _serverId + " [" + _serverName + "]");

		sendPacket(new OnlineStatus(true));

		String[] accounts = AuthServerCommunication.getInstance().getAccounts();
		for (String account : accounts)
		{
			sendPacket(new PlayerInGame(account));
		}
	}
}
