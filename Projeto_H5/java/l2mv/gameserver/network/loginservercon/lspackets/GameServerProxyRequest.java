package l2mv.gameserver.network.loginservercon.lspackets;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.ProxiesHolder;
import l2mv.gameserver.network.loginservercon.AuthServerCommunication;
import l2mv.gameserver.network.loginservercon.ReceivablePacket;
import l2mv.gameserver.network.loginservercon.gspackets.GameServerProxyResponse;
import l2mv.gameserver.utils.ProxyRequirement;

public class GameServerProxyRequest extends ReceivablePacket
{
	private static final Logger LOG = LoggerFactory.getLogger(GameServerProxyRequest.class);

	private String accountName;
	private String ipName;

	@Override
	public void readImpl()
	{
		this.accountName = this.readS();
		this.ipName = this.readS();
	}

	@Override
	protected void runImpl()
	{
		try
		{
			final InetAddress clientIp = InetAddress.getByName(this.ipName);
			final InetAddress proxy = this.chooseProxy(clientIp);
			AuthServerCommunication.getInstance().sendPacket(new GameServerProxyResponse(this.accountName, proxy.getHostAddress()));
		}
		catch (UnknownHostException e)
		{
			GameServerProxyRequest.LOG.error("Error while getting Proxy for " + this.ipName + " " + this.accountName, e);
		}
	}

	public InetAddress chooseProxy(InetAddress playerIPAddress) throws UnknownHostException
	{
		final Map<ProxyRequirement, InetAddress> proxies = ProxiesHolder.getInstance().getAllProxies();
		if (proxies != null && !proxies.isEmpty())
		{
			for (Map.Entry<ProxyRequirement, InetAddress> proxy : proxies.entrySet())
			{
				if (proxy.getKey().matches(this.accountName, playerIPAddress))
				{
					return proxy.getValue();
				}
			}
			throw new UnknownHostException("Proxy has not been found for IP Address: " + playerIPAddress.toString());
		}
		return InetAddress.getByName(Config.EXTERNAL_HOSTNAME);
	}
}
