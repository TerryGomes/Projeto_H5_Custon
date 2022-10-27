package l2mv.commons.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunnableStatsWrapper implements Runnable
{
	private static final Logger _log = LoggerFactory.getLogger(RunnableStatsWrapper.class);
	private final Runnable _runnable;

	RunnableStatsWrapper(Runnable runnable)
	{
		_runnable = runnable;
	}

	public static Runnable wrap(Runnable runnable)
	{
		return new RunnableStatsWrapper(runnable);
	}

	@Override
	public void run()
	{
		execute(_runnable);
	}

	public static void execute(Runnable runnable)
	{
		long begin = System.nanoTime();
		try
		{
			runnable.run();

			RunnableStatsManager.getInstance().handleStats(runnable.getClass(), System.nanoTime() - begin);
		}
		catch (RuntimeException e)
		{
			_log.error("Exception in a Runnable execution:", e);
		}
	}
}