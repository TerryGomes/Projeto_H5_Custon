package l2mv.gameserver.network.loginservercon.gspackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.GameServer;
import l2mv.gameserver.network.loginservercon.SendablePacket;

public class AuthRequest extends SendablePacket
{
	@Override
	protected void writeImpl()
	{
		this.writeC(0x00);
		this.writeD(GameServer.AUTH_SERVER_PROTOCOL);
		this.writeC(Config.REQUEST_ID);
		this.writeC(Config.ACCEPT_ALTERNATE_ID ? 0x01 : 0x00);
		this.writeD(Config.AUTH_SERVER_SERVER_TYPE);
		this.writeD(Config.AUTH_SERVER_AGE_LIMIT);
		this.writeC(Config.AUTH_SERVER_GM_ONLY ? 0x01 : 0x00);
		this.writeC(Config.AUTH_SERVER_BRACKETS ? 0x01 : 0x00);
		this.writeC(Config.AUTH_SERVER_IS_PVP ? 0x01 : 0x00);
		this.writeS(Config.EXTERNAL_HOSTNAME);
		this.writeS(Config.INTERNAL_HOSTNAME);

		// ports
		this.writeH(Config.PORTS_GAME.length);
		for (int PORT_GAME : Config.PORTS_GAME)
		{
			this.writeH(PORT_GAME);
		}

		this.writeD(Config.MAXIMUM_ONLINE_USERS);
	}
}
