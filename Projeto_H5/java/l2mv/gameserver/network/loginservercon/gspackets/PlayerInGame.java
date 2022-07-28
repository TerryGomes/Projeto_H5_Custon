package l2mv.gameserver.network.loginservercon.gspackets;

import l2mv.gameserver.network.loginservercon.SendablePacket;

public class PlayerInGame extends SendablePacket
{
	private final String account;

	public PlayerInGame(String account)
	{
		this.account = account;
	}

	@Override
	protected void writeImpl()
	{
		this.writeC(0x03);
		this.writeS(this.account);
	}
}
