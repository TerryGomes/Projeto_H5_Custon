package l2mv.gameserver.network.loginservercon.gspackets;

import l2mv.gameserver.network.loginservercon.SendablePacket;

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
		this.writeC(21);
		this.writeS(this.accountName);
		this.writeS(this.proxyIp);
	}
}
