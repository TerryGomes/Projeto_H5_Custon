package l2f.gameserver.network.loginservercon.gspackets;

import l2f.gameserver.network.loginservercon.SendablePacket;

public class ChangeAllowedHwid extends SendablePacket
{
	private final String account;
	private final String hwid;

	public ChangeAllowedHwid(String account, String hwid)
	{
		this.account = account;
		this.hwid = hwid;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x09);
		writeS(account);
		writeS(hwid);
	}
}