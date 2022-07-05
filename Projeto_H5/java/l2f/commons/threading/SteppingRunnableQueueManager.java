package l2f.commons.threading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Delayed;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.mutable.MutableLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mobius
 * @version $Revision: 1.0 $
 */
public abstract class SteppingRunnableQueueManager implements Runnable
{
	/**
	 * Field _log.
	 */
	static final Logger _log = LoggerFactory.getLogger(SteppingRunnableQueueManager.class);
	/**
	 * Field tickPerStepInMillis.
	 */
	protected final long tickPerStepInMillis;
	/**
	 * Field queue.
	 */
	private final List<SteppingScheduledFuture<?>> queue = new CopyOnWriteArrayList<>();
	/**
	 * Field isRunning.
	 */
	private final AtomicBoolean isRunning = new AtomicBoolean();

	/**
	 * Constructor for SteppingRunnableQueueManager.
	 * @param tickPerStepInMillis long
	 */
	public SteppingRunnableQueueManager(long tickPerStepInMillis)
	{
		this.tickPerStepInMillis = tickPerStepInMillis;
	}

	/**
	 * @author Mobius
	 */
	public class SteppingScheduledFuture<V> implements RunnableScheduledFuture<V>
	{
		/**
		 * Field r.
		 */
		final Runnable r;
		/**
		 * Field stepping.
		 */
		private final long stepping;
		/**
		 * Field isPeriodic.
		 */
		private final boolean isPeriodic;
		/**
		 * Field step.
		 */
		private long step;
		/**
		 * Field isCancelled.
		 */
		private boolean isCancelled;

		/**
		 * Constructor for SteppingScheduledFuture.
		 * @param r Runnable
		 * @param initial long
		 * @param stepping long
		 * @param isPeriodic boolean
		 */
		public SteppingScheduledFuture(Runnable r, long initial, long stepping, boolean isPeriodic)
		{
			this.r = r;
			this.step = initial;
			this.stepping = stepping;
			this.isPeriodic = isPeriodic;
		}

		/**
		 * Method run.
		 * @see java.util.concurrent.RunnableFuture#run()
		 */
		@Override
		public void run()
		{
			if (--step == 0)
			{
				try
				{
					r.run();
				}
				catch (Exception e)
				{
					_log.error("Exception in a Runnable execution:", e);
				}
				finally
				{
					if (isPeriodic)
					{
						step = stepping;
					}
				}
			}
		}

		/**
		 * Method isDone.
		 * @return boolean * @see java.util.concurrent.Future#isDone()
		 */
		@Override
		public boolean isDone()
		{
			return isCancelled || (!isPeriodic && (step == 0));
		}

		/**
		 * Method isCancelled.
		 * @return boolean * @see java.util.concurrent.Future#isCancelled()
		 */
		@Override
		public boolean isCancelled()
		{
			return isCancelled;
		}

		/**
		 * Method cancel.
		 * @param mayInterruptIfRunning boolean
		 * @return boolean * @see java.util.concurrent.Future#cancel(boolean)
		 */
		@Override
		public boolean cancel(boolean mayInterruptIfRunning)
		{
			return isCancelled = true;
		}

		/**
		 * Method get.
		 * @return V * @see java.util.concurrent.Future#get()
		 */
		@Override
		public V get()
		{
			return null;
		}

		/**
		 * Method get.
		 * @param timeout long
		 * @param unit TimeUnit
		 * @return V * @see java.util.concurrent.Future#get(long, TimeUnit)
		 */
		@Override
		public V get(long timeout, TimeUnit unit)
		{
			return null;
		}

		/**
		 * Method getDelay.
		 * @param unit TimeUnit
		 * @return long * @see java.util.concurrent.Delayed#getDelay(TimeUnit)
		 */
		@Override
		public long getDelay(TimeUnit unit)
		{
			return unit.convert(step * tickPerStepInMillis, TimeUnit.MILLISECONDS);
		}

		/**
		 * Method compareTo.
		 * @param o Delayed
		 * @return int
		 */
		@Override
		public int compareTo(Delayed o)
		{
			return 0;
		}

		/**
		 * Method isPeriodic.
		 * @return boolean * @see java.util.concurrent.RunnableScheduledFuture#isPeriodic()
		 */
		@Override
		public boolean isPeriodic()
		{
			return isPeriodic;
		}
	}

	/**
	 * Method schedule.
	 * @param r Runnable
	 * @param delay long
	 * @return SteppingScheduledFuture<?>
	 */
	public SteppingScheduledFuture<?> schedule(Runnable r, long delay)
	{
		return schedule(r, delay, delay, false);
	}

	/**
	 * Method scheduleAtFixedRate.
	 * @param r Runnable
	 * @param initial long
	 * @param delay long
	 * @return SteppingScheduledFuture<?>
	 */
	public SteppingScheduledFuture<?> scheduleAtFixedRate(Runnable r, long initial, long delay)
	{
		return schedule(r, initial, delay, true);
	}

	/**
	 * Method schedule.
	 * @param r Runnable
	 * @param initial long
	 * @param delay long
	 * @param isPeriodic boolean
	 * @return SteppingScheduledFuture<?>
	 */
	private SteppingScheduledFuture<?> schedule(Runnable r, long initial, long delay, boolean isPeriodic)
	{
		SteppingScheduledFuture<?> sr;
		long initialStepping = getStepping(initial);
		long stepping = getStepping(delay);
		queue.add(sr = new SteppingScheduledFuture<Boolean>(r, initialStepping, stepping, isPeriodic));
		return sr;
	}

	/**
	 * Method getStepping.
	 * @param delay long
	 * @return long
	 */
	private long getStepping(long delay)
	{
		delay = Math.max(0, delay);
		return (delay % tickPerStepInMillis) > (tickPerStepInMillis / 2) ? (delay / tickPerStepInMillis) + 1 : delay < tickPerStepInMillis ? 1 : delay / tickPerStepInMillis;
	}

	/**
	 * Method run.
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		if (!isRunning.compareAndSet(false, true))
		{
			_log.warn("Slow running queue, managed by " + this + ", queue size : " + queue.size() + "!");
			return;
		}
		try
		{
			if (queue.isEmpty())
			{
				return;
			}
			for (SteppingScheduledFuture<?> sr : queue)
			{
				if (!sr.isDone())
				{
					sr.run();
				}
			}
		}
		finally
		{
			isRunning.set(false);
		}
	}

	/**
	 * Method purge.
	 */
	public void purge()
	{
		final List<SteppingScheduledFuture<?>> purge = new ArrayList<>();
		for (SteppingScheduledFuture<?> sr : queue)
		{
			if (sr != null && sr.isDone())
			{
				purge.add(sr);
			}
		}
		queue.removeAll(purge);
	}

	/**
	 * Method getStats.
	 * @return CharSequence
	 */
	public CharSequence getStats()
	{
		StringBuilder list = new StringBuilder();
		Map<String, MutableLong> stats = new TreeMap<>();
		int total = 0;
		int done = 0;
		for (SteppingScheduledFuture<?> sr : queue)
		{
			if (sr.isDone())
			{
				done++;
				continue;
			}
			total++;
			MutableLong count = stats.get(sr.r.getClass().getName());
			if (count == null)
			{
				stats.put(sr.r.getClass().getName(), count = new MutableLong(1L));
			}
			else
			{
				count.increment();
			}
		}
		for (Map.Entry<String, MutableLong> e : stats.entrySet())
		{
			list.append('\t').append(e.getKey()).append(" : ").append(e.getValue().longValue()).append('\n');
		}
		list.append("Scheduled: ....... ").append(total).append('\n');
		list.append("Done/Cancelled: .. ").append(done).append('\n');
		return list;
	}
}