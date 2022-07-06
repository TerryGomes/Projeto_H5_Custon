package l2mv.gameserver.network.loginservercon.gspackets;

import l2mv.gameserver.network.loginservercon.SendablePacket;

public class ChangeAllowedIp extends SendablePacket
{
	private final String account;
	private final String ip;

	public ChangeAllowedIp(String account, String ip)
	{
		this.account = account;
		this.ip = ip;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x07);
		writeS(account);
		writeS(ip);
	}
}