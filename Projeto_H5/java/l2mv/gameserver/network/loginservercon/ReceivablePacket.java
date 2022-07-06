package l2mv.gameserver.network.loginservercon;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ReceivablePacket extends l2mv.commons.net.nio.ReceivablePacket<AuthServerCommunication>
{
	private static final Logger _log = LoggerFactory.getLogger(ReceivablePacket.class);

	@Override
	public AuthServerCommunication getClient()
	{
		return AuthServerCommunication.getInstance();
	}

	@Override
	protected ByteBuffer getByteBuffer()
	{
		return getClient().getReadBuffer();
	}

	@Override
	public final boolean read()
	{
		try
		{
			readImpl();
		}
		catch (RuntimeException e)
		{
			_log.error("Loginserver ReceivablePacket", e);
		}
		return true;
	}

	@Override
	public final void run()
	{
		try
		{
			runImpl();
		}
		catch (RuntimeException e)
		{
			_log.error("Loginserver ReceivablePacket ", e);
		}
	}

	protected abstract void readImpl();

	protected abstract void runImpl();

	protected void sendPacket(SendablePacket sp)
	{
		getClient().sendPacket(sp);
	}
}
