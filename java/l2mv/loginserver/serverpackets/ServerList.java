package l2mv.loginserver.serverpackets;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.loginserver.GameServerManager;
import l2mv.loginserver.L2LoginClient;
import l2mv.loginserver.accounts.Account;
import l2mv.loginserver.gameservercon.GameServer;

public final class ServerList extends L2LoginServerPacket
{
	private static final Logger LOG = LoggerFactory.getLogger(ServerList.class);

	private final List<ServerData> _servers = new ArrayList<ServerData>();
	private final int _lastServer;

	public ServerList(L2LoginClient client, Map<Integer, String> proxies)
	{
		final Account account = client.getAccount();
		_lastServer = account.getLastServer();
		if (client.getConnection() == null || client.getConnection().getSocket() == null)
		{
			return;
		}

		final InetAddress clientIp = client.getConnection().getSocket().getInetAddress();
		for (GameServer gs : GameServerManager.getInstance().getGameServers())
		{
			try
			{
				final Pair<Integer, int[]> entry = account.getAccountInfo(gs.getId());
				if (proxies.containsKey(gs.getId()))
				{
					final String proxy = proxies.get(gs.getId());
					final InetAddress proxyIp = InetAddress.getByName(proxy);
					LOG.info("IP: " + clientIp.getHostAddress() + " Login: " + account.getLogin() + " Assigned to Proxy: " + proxy);
					_servers.add(new ServerData(gs.getId(), proxyIp, gs.getPort(), gs.isPvp(), gs.isShowingBrackets(), gs.getServerType(), gs.getOnline(), gs.getMaxPlayers(), gs.isOnline(), entry == null ? 0 : (int) entry.getKey(), gs.getAgeLimit(), entry == null ? ArrayUtils.EMPTY_INT_ARRAY : (int[]) entry.getValue()));
				}
				else
				{
					_servers.add(new ServerData(gs.getId(), gs.getExternalHost(), gs.getPort(), gs.isPvp(), gs.isShowingBrackets(), gs.getServerType(), gs.getOnline(), gs.getMaxPlayers(), gs.isOnline(), entry == null ? 0 : (int) entry.getKey(), gs.getAgeLimit(), entry == null ? ArrayUtils.EMPTY_INT_ARRAY : (int[]) entry.getValue()));
				}
			}
			catch (UnknownHostException e)
			{
				LOG.error("Error while getting Proxy IP! Proxies: " + proxies + " GS: " + gs.getId(), e);
			}
		}
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x04);
		writeC(_servers.size());
		writeC(_lastServer);

		for (ServerData server : _servers)
		{
			writeC(server.serverId);

			InetAddress i4 = server.ip;
			byte[] raw = i4.getAddress();
			writeC(raw[0] & 0xff);
			writeC(raw[1] & 0xff);
			writeC(raw[2] & 0xff);
			writeC(raw[3] & 0xff);

			writeD(server.port);
			writeC(server.ageLimit); // age limit
			writeC(server.pvp ? 0x01 : 0x00);
			writeH(server.online);
			writeH(server.maxPlayers);
			writeC(server.status ? 0x01 : 0x00);
			writeD(server.type);
			writeC(server.brackets ? 0x01 : 0x00);
		}

		writeH(0x00); // -??
		writeC(_servers.size());

		for (ServerData server : _servers)
		{
			writeC(server.serverId);
			writeC(server.playerSize); // acc player size
			writeC(server.deleteChars.length);
			for (int t : server.deleteChars)
			{
				writeD((int) (t - System.currentTimeMillis() / 1000L));
			}
		}
	}

	private static class ServerData
	{
		int serverId;
		InetAddress ip;
		int port;
		int online;
		int maxPlayers;
		boolean status;
		boolean pvp;
		boolean brackets;
		int type;
		int ageLimit;
		int playerSize;
		int[] deleteChars;

		ServerData(int serverId, InetAddress ip, int port, boolean pvp, boolean brackets, int type, int online, int maxPlayers, boolean status, int playerSize, int ageLimit, int[] deleteChars)
		{
			this.serverId = serverId;
			this.ip = ip;
			this.port = port;
			this.pvp = pvp;
			this.brackets = brackets;
			this.type = type;
			this.online = online;
			this.maxPlayers = maxPlayers;
			this.status = status;
			this.playerSize = playerSize;
			this.ageLimit = ageLimit;
			this.deleteChars = deleteChars;
		}
	}
}