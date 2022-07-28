package l2mv.gameserver.network.loginservercon;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SendablePacket extends l2mv.commons.net.nio.SendablePacket<AuthServerCommunication>
{
	private static final Logger _log = LoggerFactory.getLogger(SendablePacket.class);

	@Override
	public AuthServerCommunication getClient()
	{
		return AuthServerCommunication.getInstance();
	}

	@Override
	protected ByteBuffer getByteBuffer()
	{
		return this.getClient().getWriteBuffer();
	}

	@Override
	public boolean write()
	{
		try
		{
			this.writeImpl();
		}
		catch (RuntimeException e)
		{
			_log.error("Loginserver SendablePacket ", e);
		}
		return true;
	}

	protected abstract void writeImpl();
}
