package l2f.loginserver.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.net.nio.impl.ReceivablePacket;
import l2f.loginserver.L2LoginClient;

public abstract class L2LoginClientPacket extends ReceivablePacket<L2LoginClient>
{
	protected static Logger _log = LoggerFactory.getLogger(L2LoginClientPacket.class);

	@Override
	protected final boolean read()
	{
		try
		{
			readImpl();
			return true;
		}
		catch (Exception e)
		{
			_log.error("", e);
			return false;
		}
	}

	@Override
	public void run()
	{
		try
		{
			runImpl();
		}
		catch (Exception e)
		{
			_log.error("", e);
		}
	}

	protected abstract void readImpl();

	protected abstract void runImpl() throws Exception;

	/**
	 * Synerge
	 * @return Returns if this clientpacket can be used while the character is blocked, overriden if it can
	 */
	@Override
	public boolean canBeUsedWhileBlocked()
	{
		return true;
	}
}
