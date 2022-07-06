package l2mv.commons.util.concurrent.atomic;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * ?????????, ??????????????? ???? ?????????.
 *
 * @author G1ta0
 */
public class AtomicState
{
	private static final AtomicIntegerFieldUpdater<AtomicState> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(AtomicState.class, "value");

	private volatile int value;

	public AtomicState(boolean initialValue)
	{
		value = initialValue ? 1 : 0;
	}

	public AtomicState()
	{
	}

	public final boolean get()
	{
		return value != 0;
	}

	private boolean getBool(int value)
	{
		if (value < 0)
		{
			throw new IllegalStateException();
		}
		return value > 0;
	}

	public final boolean setAndGet(boolean newValue)
	{
		if (newValue)
		{
			return getBool(stateUpdater.incrementAndGet(this));
		}
		else
		{
			return getBool(stateUpdater.decrementAndGet(this));
		}
	}

	public final boolean getAndSet(boolean newValue)
	{
		if (newValue)
		{
			return getBool(stateUpdater.getAndIncrement(this));
		}
		else
		{
			return getBool(stateUpdater.getAndDecrement(this));
		}
	}

}
