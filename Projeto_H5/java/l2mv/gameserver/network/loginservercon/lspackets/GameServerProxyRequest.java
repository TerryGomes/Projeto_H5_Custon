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
		accountName = readS();
		ipName = readS();
	}

	@Override
	protected void runImpl()
	{
		try
		{
			final InetAddress clientIp = InetAddress.getByName(ipName);
			final InetAddress proxy = chooseProxy(clientIp);
			AuthServerCommunication.getInstance().sendPacket(new GameServerProxyResponse(accountName, proxy.getHostAddress()));
		}
		catch (UnknownHostException e)
		{
			GameServerProxyRequest.LOG.error("Error while getting Proxy for " + ipName + " " + accountName, e);
		}
	}

	public InetAddress chooseProxy(InetAddress playerIPAddress) throws UnknownHostException
	{
		final Map<ProxyRequirement, InetAddress> proxies = ProxiesHolder.getInstance().getAllProxies();
		if (proxies != null && !proxies.isEmpty())
		{
			for (Map.Entry<ProxyRequirement, InetAddress> proxy : proxies.entrySet())
			{
				if (proxy.getKey().matches(accountName, playerIPAddress))
				{
					return proxy.getValue();
				}
			}
			throw new UnknownHostException("Proxy has not been found for IP Address: " + playerIPAddress.toString());
		}
		return InetAddress.getByName(Config.EXTERNAL_HOSTNAME);
	}
}
