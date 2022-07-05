package l2f.loginserver.gameservercon.lspackets;

import l2f.loginserver.gameservercon.GameServer;
import l2f.loginserver.gameservercon.SendablePacket;

public class AuthResponse extends SendablePacket
{
	private int serverId;
	private String name;

	public AuthResponse(GameServer gs)
	{
		serverId = gs.getId();
		name = gs.getName();
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x00);
		writeC(serverId);
		writeS(name);
	}
}