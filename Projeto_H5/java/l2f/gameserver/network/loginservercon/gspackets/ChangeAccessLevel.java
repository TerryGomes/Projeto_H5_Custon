package l2f.gameserver.network.loginservercon.gspackets;

import l2f.gameserver.network.loginservercon.SendablePacket;

public class ChangeAccessLevel extends SendablePacket
{
	private final String account;
	private final int level;
	private final int banExpire;

	public ChangeAccessLevel(String account, int level, int banExpire)
	{
		this.account = account;
		this.level = level;
		this.banExpire = banExpire;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x11);
		writeS(account);
		writeD(level);
		writeD(banExpire);
	}
}
