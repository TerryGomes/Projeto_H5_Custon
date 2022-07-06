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
		_protocol = readD();
	}

	@Override
	protected void runImpl()
	{
		if (_protocol == -2)
		{
			_client.closeNow(false);
			return;
		}
		else if (_protocol == -3)
		{
			_log.info("Status request from IP : " + getClient().getIpAddr());
			getClient().close(new SendStatus());
			return;
		}
		else if (_protocol < Config.MIN_PROTOCOL_REVISION || _protocol > Config.MAX_PROTOCOL_REVISION)
		{
			_log.warn("Unknown protocol revision : " + _protocol + ", client : " + _client);
			getClient().close(new KeyPacket(null));
			return;
		}

		_client.setRevision(_protocol);

		Log.reachedProtocolVersion(_client, _client.getHWID());
		sendPacket(new KeyPacket(_client.enableCrypt()));
	}

	@Override
	public String getType()
	{
		return getClass().getSimpleName();
	}
}
