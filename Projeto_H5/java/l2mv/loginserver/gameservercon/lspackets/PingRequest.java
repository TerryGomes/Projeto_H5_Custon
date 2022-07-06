package l2mv.loginserver.gameservercon.lspackets;

import l2mv.loginserver.gameservercon.SendablePacket;

public class PingRequest extends SendablePacket
{
	@Override
	protected void writeImpl()
	{
		writeC(0xff);
	}
}