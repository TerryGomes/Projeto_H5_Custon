package l2mv.loginserver.utils;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2mv.commons.annotations.Nullable;
import l2mv.loginserver.Config;
import l2mv.loginserver.GameServerManager;
import l2mv.loginserver.L2LoginClient;
import l2mv.loginserver.gameservercon.GameServer;
import l2mv.loginserver.gameservercon.lspackets.GameServerProxyRequest;
import l2mv.loginserver.serverpackets.ServerList;

public class ProxyWaitingList
{
	private static final String NO_PROXY_VALUE = "";

	private final Map<L2LoginClient, Map<Integer, String>> proxyPerAccount;

	private ProxyWaitingList()
	{
		proxyPerAccount = new ConcurrentHashMap<L2LoginClient, Map<Integer, String>>(10);
	}

	public void receiveProxy(int gameServerId, String accountName, String proxy)
	{
		final L2LoginClient client = getWaitingLoginClient(accountName);
		if (client != null)
		{
			final Map<Integer, String> proxies = proxyPerAccount.get(client);
			proxies.put(gameServerId, proxy);
			if (!proxies.values().contains(""))
			{
				client.sendPacket(new ServerList(client, proxies));
				proxyPerAccount.remove(client);
			}
		}
	}

	public void requestProxies(L2LoginClient client)
	{
		final InetAddress ip = client.getConnection().getSocket().getInetAddress();
		final GameServerProxyRequest requestProxy = new GameServerProxyRequest(client.getLogin(), ip.getHostAddress());
		final GameServer[] gameServers = GameServerManager.getInstance().getGameServers();
		final Map<Integer, String> proxies = new ConcurrentHashMap<Integer, String>(gameServers.length);
		final String specialProxy = getSpecialProxy(client);
		for (GameServer gs : GameServerManager.getInstance().getGameServers())
		{
			if (gs.isAuthed())
			{
				if (specialProxy == null)
				{
					proxies.put(gs.getId(), NO_PROXY_VALUE);
					gs.sendPacket(requestProxy);
				}
				else
				{
					proxies.put(gs.getId(), specialProxy);
				}
			}
		}
		if (proxies.values().contains(""))
		{
			proxyPerAccount.put(client, proxies);
		}
		else
		{
			client.sendPacket(new ServerList(client, proxies));
		}
	}

	private static String getSpecialProxy(L2LoginClient client)
	{
		final String clientLogin = client.getLogin();
		for (String specialLogin : Config.DIFFERENT_GAME_SERVER_IP_ACCOUNTS)
		{
			if (specialLogin.equalsIgnoreCase(clientLogin))
			{
				return Config.DIFFERENT_GAME_SERVER_IP;
			}
		}
		return null;
	}

	@Nullable
	private L2LoginClient getWaitingLoginClient(String accountName)
	{
		for (L2LoginClient client : proxyPerAccount.keySet())
		{
			if (client.getLogin().equals(accountName))
			{
				return client;
			}
		}
		return null;
	}

	public static ProxyWaitingList getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final ProxyWaitingList instance = new ProxyWaitingList();
	}
}
