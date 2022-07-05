package l2f.gameserver.network.loginservercon;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.network.loginservercon.lspackets.AuthResponse;
import l2f.gameserver.network.loginservercon.lspackets.ChangePasswordResponse;
import l2f.gameserver.network.loginservercon.lspackets.GameServerProxyRequest;
import l2f.gameserver.network.loginservercon.lspackets.GetAccountInfo;
import l2f.gameserver.network.loginservercon.lspackets.KickPlayer;
import l2f.gameserver.network.loginservercon.lspackets.LoginServerFail;
import l2f.gameserver.network.loginservercon.lspackets.OnWrongAccountPassword;
import l2f.gameserver.network.loginservercon.lspackets.PingRequest;
import l2f.gameserver.network.loginservercon.lspackets.PlayerAuthResponse;

public class PacketHandler
{
	private static final Logger _log = LoggerFactory.getLogger(PacketHandler.class);

	public static ReceivablePacket handlePacket(ByteBuffer buf)
	{
		ReceivablePacket packet = null;

		int id = buf.get() & 0xff;

		switch (id)
		{
		case 0x00:
			packet = new AuthResponse();
			break;
		case 0x01:
			packet = new LoginServerFail();
			break;
		case 0x02:
			packet = new PlayerAuthResponse();
			break;
		case 0x03:
			packet = new KickPlayer();
			break;
		case 0x04:
			packet = new GetAccountInfo();
			break;
		case 0x06:
			packet = new ChangePasswordResponse();
			break;
		case 7:
		{
			packet = new OnWrongAccountPassword();
			break;
		}
		case 9:
		{
			packet = new GameServerProxyRequest();
			break;
		}
		case 0xff:
			packet = new PingRequest();
			break;
		default:
			_log.error("Received unknown packet: " + Integer.toHexString(id));
		}

		return packet;
	}
}
