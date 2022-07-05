package l2f.gameserver.network.loginservercon.gspackets;

import l2f.gameserver.network.loginservercon.SendablePacket;

public class GameServerProxyResponse extends SendablePacket
{
	private final String accountName;
	private final String proxyIp;

	public GameServerProxyResponse(String accountName, String proxyIp)
	{
		super();
		this.accountName = accountName;
		this.proxyIp = proxyIp;
	}

	@Override
	protected void writeImpl()
	{
		writeC(21);
		writeS(accountName);
		writeS(proxyIp);
	}
}
