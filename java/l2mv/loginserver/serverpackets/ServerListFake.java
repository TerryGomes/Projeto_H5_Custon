package l2mv.loginserver.serverpackets;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import l2mv.commons.net.utils.NetUtils;
import l2mv.loginserver.GameServerManager;
import l2mv.loginserver.accounts.Account;
import l2mv.loginserver.gameservercon.GameServer;

public final class ServerListFake extends L2LoginServerPacket
{
	private List<ServerData> _servers = new ArrayList<ServerData>();
	@SuppressWarnings("unused")
	private int _lastServer;

	private static class ServerData
	{
		int serverId;
		InetAddress ip;
		int port;
		@SuppressWarnings("unused")
		int online;
		@SuppressWarnings("unused")
		int maxPlayers;
		@SuppressWarnings("unused")
		boolean status;
		boolean pvp;
		boolean brackets;
		int type;
		int ageLimit;
		@SuppressWarnings("unused")
		int playerSize;
		int[] deleteChars;

		ServerData(int serverId, InetAddress ip, int port, boolean pvp, boolean brackets, int type, int online, int maxPlayers, boolean status, int size, int ageLimit, int[] d)
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
			playerSize = size;
			this.ageLimit = ageLimit;
			deleteChars = d;
		}
	}

	public ServerListFake(Account account)
	{
		_lastServer = account.getLastServer();

		for (GameServer gs : GameServerManager.getInstance().getGameServers())
		{
			InetAddress ip;
			try
			{
				ip = NetUtils.isInternalIP(account.getLastIP()) ? gs.getInternalHost() : gs.getExternalHost();
			}
			catch (UnknownHostException e)
			{
				continue;
			}

			Pair<Integer, int[]> entry = account.getAccountInfo(gs.getId());

			_servers.add(new ServerData(gs.getId(), ip, gs.getPort(), gs.isPvp(), gs.isShowingBrackets(), gs.getServerType(), gs.getOnline(), gs.getMaxPlayers(), gs.isOnline(), entry == null ? 0 : entry.getKey(), gs.getAgeLimit(), entry == null ? ArrayUtils.EMPTY_INT_ARRAY : entry.getValue()));
		}
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x04);
		writeC(_servers.size());
		writeC(0);
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
			writeH(0);
			writeH(0);
			writeC(0x00);
			writeD(server.type);
			writeC(server.brackets ? 0x01 : 0x00);
		}

		writeH(0x00); // -??
		writeC(_servers.size());
		for (ServerData server : _servers)
		{
			writeC(server.serverId);
			writeC(0); // acc player size
			writeC(0);
			for (int t : server.deleteChars)
			{
				writeD((int) (t - System.currentTimeMillis() / 1000L));
			}
		}
	}
}