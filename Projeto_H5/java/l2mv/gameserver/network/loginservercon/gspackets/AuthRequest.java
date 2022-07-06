package l2mv.gameserver.network.loginservercon.gspackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.GameServer;
import l2mv.gameserver.network.loginservercon.SendablePacket;

public class AuthRequest extends SendablePacket
{
	@Override
	protected void writeImpl()
	{
		writeC(0x00);
		writeD(GameServer.AUTH_SERVER_PROTOCOL);
		writeC(Config.REQUEST_ID);
		writeC(Config.ACCEPT_ALTERNATE_ID ? 0x01 : 0x00);
		writeD(Config.AUTH_SERVER_SERVER_TYPE);
		writeD(Config.AUTH_SERVER_AGE_LIMIT);
		writeC(Config.AUTH_SERVER_GM_ONLY ? 0x01 : 0x00);
		writeC(Config.AUTH_SERVER_BRACKETS ? 0x01 : 0x00);
		writeC(Config.AUTH_SERVER_IS_PVP ? 0x01 : 0x00);
		writeS(Config.EXTERNAL_HOSTNAME);
		writeS(Config.INTERNAL_HOSTNAME);

		// ports
		writeH(Config.PORTS_GAME.length);
		for (int PORT_GAME : Config.PORTS_GAME)
		{
			writeH(PORT_GAME);
		}

		writeD(Config.MAXIMUM_ONLINE_USERS);
	}
}
