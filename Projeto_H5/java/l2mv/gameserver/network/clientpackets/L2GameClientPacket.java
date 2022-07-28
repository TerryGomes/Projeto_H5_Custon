package l2mv.gameserver.network.clientpackets;

import java.nio.BufferUnderflowException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.net.nio.impl.ReceivablePacket;
import l2mv.gameserver.GameServer;
import l2mv.gameserver.network.GameClient;
import l2mv.gameserver.network.serverpackets.L2GameServerPacket;

/**
 * Packets received by the game server from clients
 */
public abstract class L2GameClientPacket extends ReceivablePacket<GameClient>
{
	private static final Logger _log = LoggerFactory.getLogger(L2GameClientPacket.class);

	@Override
	public final boolean read()
	{
		try
		{
			this.readImpl();
			return true;
		}
		catch (BufferUnderflowException e)
		{
			this._client.onPacketReadFail();
			_log.error("Client: " + this._client + " - Failed reading: " + this.getType() + " - Server Version: " + GameServer.getInstance().getVersion().getRevisionNumber() + " - " + e.getMessage());
		}
		catch (RuntimeException e)
		{
			_log.error("Client: " + this._client + " - Failed reading: " + this.getType() + " - Server Version: " + GameServer.getInstance().getVersion().getRevisionNumber(), e);
		}

		return false;
	}

	protected abstract void readImpl();

	@Override
	public final void run()
	{
		GameClient client = this.getClient();
		try
		{
			this.runImpl();
		}
		catch (RuntimeException e)
		{
			_log.error("Client: " + client + " - Failed running: " + this.getType() + " - Server Version: " + GameServer.getInstance().getVersion().getRevisionNumber(), e);
		}
	}

	protected abstract void runImpl();

	protected String readS(int len)
	{
		String ret = this.readS();
		return ret.length() > len ? ret.substring(0, len) : ret;
	}

	protected void sendPacket(L2GameServerPacket packet)
	{
		this.getClient().sendPacket(packet);
	}

	protected void sendPacket(L2GameServerPacket... packets)
	{
		this.getClient().sendPacket(packets);
	}

	protected void sendPackets(List<L2GameServerPacket> packets)
	{
		this.getClient().sendPackets(packets);
	}

	public String getType()
	{
		return "[C] " + this.getClass().getSimpleName();
	}

	/**
	 * Synerge
	 * @return Returns if this clientpacket can be used while the character is blocked, overriden if it can
	 */
	@Override
	public boolean canBeUsedWhileBlocked()
	{
		return false;
	}
}