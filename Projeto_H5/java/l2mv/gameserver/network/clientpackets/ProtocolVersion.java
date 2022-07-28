package l2mv.gameserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.network.serverpackets.KeyPacket;
import l2mv.gameserver.network.serverpackets.SendStatus;
import l2mv.gameserver.utils.Log;

public class ProtocolVersion extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(ProtocolVersion.class);

	private int _protocol;

	@Override
	protected void readImpl()
	{
		this._protocol = this.readD();
	}

	@Override
	protected void runImpl()
	{
		if (this._protocol == -2)
		{
			this._client.closeNow(false);
			return;
		}
		else if (this._protocol == -3)
		{
			_log.info("Status request from IP : " + this.getClient().getIpAddr());
			this.getClient().close(new SendStatus());
			return;
		}
		else if (this._protocol < Config.MIN_PROTOCOL_REVISION || this._protocol > Config.MAX_PROTOCOL_REVISION)
		{
			_log.warn("Unknown protocol revision : " + this._protocol + ", client : " + this._client);
			this.getClient().close(new KeyPacket(null));
			return;
		}

		this._client.setRevision(this._protocol);

		Log.reachedProtocolVersion(this._client, this._client.getHWID());
		this.sendPacket(new KeyPacket(this._client.enableCrypt()));
	}

	@Override
	public String getType()
	{
		return this.getClass().getSimpleName();
	}
}
