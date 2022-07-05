package l2f.commons.threading;

import java.util.Queue;

public abstract class FIFORunnableQueue<T extends Runnable> implements Runnable
{
	private static final int NONE = 0;
	private static final int QUEUED = 1;
	private static final int RUNNING = 2;
	private int _state = 0;
	private final Queue<T> _queue;

	public FIFORunnableQueue(Queue<T> queue)
	{
		_queue = queue;
	}

	public void execute(T t)
	{
		_queue.add(t);

		synchronized (this)
		{
			if (_state != NONE)
			{
				return;
			}
			_state = QUEUED;
		}

		execute();
	}

	protected abstract void execute();

	public void clear()
	{
		_queue.clear();
	}

	@Override
	public void run()
	{
		synchronized (this)
		{
			if (_state == RUNNING)
			{
				return;
			}
			_state = RUNNING;
		}

		try
		{
			while (true)
			{
				Runnable t = _queue.poll();
				if (t == null)
				{
					break;
				}
				t.run();
			}
		}
		finally
		{
			synchronized (this)
			{
				_state = NONE;
			}
		}
	}
}