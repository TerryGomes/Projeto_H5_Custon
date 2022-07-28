package l2mv.gameserver.network.loginservercon.gspackets;

import l2mv.gameserver.network.loginservercon.SendablePacket;

public class PlayerLogout extends SendablePacket
{
	private final String account;

	public PlayerLogout(String account)
	{
		this.account = account;
	}

	@Override
	protected void writeImpl()
	{
		this.writeC(0x04);
		this.writeS(this.account);
	}
}
