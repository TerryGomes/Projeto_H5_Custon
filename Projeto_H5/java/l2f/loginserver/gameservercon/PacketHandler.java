package l2f.loginserver.gameservercon;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.loginserver.gameservercon.gspackets.AuthRequest;
import l2f.loginserver.gameservercon.gspackets.BonusRequest;
import l2f.loginserver.gameservercon.gspackets.ChangeAccessLevel;
import l2f.loginserver.gameservercon.gspackets.ChangeAllowedHwid;
import l2f.loginserver.gameservercon.gspackets.ChangeAllowedIp;
import l2f.loginserver.gameservercon.gspackets.ChangePassword;
import l2f.loginserver.gameservercon.gspackets.GameServerProxyResponse;
import l2f.loginserver.gameservercon.gspackets.IdsRequest;
import l2f.loginserver.gameservercon.gspackets.OnlineStatus;
import l2f.loginserver.gameservercon.gspackets.PingResponse;
import l2f.loginserver.gameservercon.gspackets.PlayerAuthRequest;
import l2f.loginserver.gameservercon.gspackets.PlayerInGame;
import l2f.loginserver.gameservercon.gspackets.PlayerLogout;
import l2f.loginserver.gameservercon.gspackets.SetAccountInfo;

public class PacketHandler
{
	private static Logger _log = LoggerFactory.getLogger(PacketHandler.class);

	public static ReceivablePacket handlePacket(GameServer gs, ByteBuffer buf)
	{
		ReceivablePacket packet = null;

		int id = buf.get() & 0xff;

		if (!gs.isAuthed())
		{
			switch (id)
			{
			case 0x00:
				packet = new AuthRequest();
				break;
			default:
				_log.error("Received unknown packet: " + Integer.toHexString(id));
			}
		}
		else
		{
			switch (id)
			{
			case 0x01:
				packet = new OnlineStatus();
				break;
			case 0x02:
				packet = new PlayerAuthRequest();
				break;
			case 0x03:
				packet = new PlayerInGame();
				break;
			case 0x04:
				packet = new PlayerLogout();
				break;
			case 0x05:
				packet = new SetAccountInfo();
				break;
			case 0x07:
				packet = new ChangeAllowedIp();
				break;
			case 0x08:
				packet = new ChangePassword();
				break;
			case 0x09:
				packet = new ChangeAllowedHwid();
				break;
			case 0x10:
				packet = new BonusRequest();
				break;
			case 0x11:
				packet = new ChangeAccessLevel();
				break;
			case 0x12:
				packet = new IdsRequest();
				break;
			case 21:
				packet = new GameServerProxyResponse();
				break;
			case 0xff:
				packet = new PingResponse();
				break;
			default:
				_log.error("Received unknown packet: " + Integer.toHexString(id));
			}
		}

		return packet;
	}
}