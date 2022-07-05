package l2f.commons.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL
 * @date 19:13/04.04.2011
 */
public abstract class RunnableImpl implements Runnable
{
	public static final Logger _log = LoggerFactory.getLogger(RunnableImpl.class);

	public abstract void runImpl() throws Exception;

	@Override
	public final void run()
	{
		try
		{
			runImpl();
		}
		catch (Exception e)
		{
			_log.error("Exception: RunnableImpl.run(): " + e, e);
		}
	}
}
