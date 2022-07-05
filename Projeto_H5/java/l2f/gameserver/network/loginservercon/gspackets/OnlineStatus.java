package l2f.gameserver.network.loginservercon.gspackets;

import l2f.gameserver.network.loginservercon.SendablePacket;

public class OnlineStatus extends SendablePacket
{
	private final boolean _online;

	public OnlineStatus(boolean online)
	{
		_online = online;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x01);
		writeC(_online ? 1 : 0);
	}
}
