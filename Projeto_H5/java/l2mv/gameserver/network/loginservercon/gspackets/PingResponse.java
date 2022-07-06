package l2mv.gameserver.network.loginservercon.gspackets;

import l2mv.gameserver.network.loginservercon.SendablePacket;

public class PingResponse extends SendablePacket
{
	@Override
	protected void writeImpl()
	{
		writeC(0xff);
		writeQ(System.currentTimeMillis());
	}
}