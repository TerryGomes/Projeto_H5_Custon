package l2f.commons.net.nio.impl;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("rawtypes")
public class MMOExecutableQueue<T extends MMOClient> implements Queue<ReceivablePacket<T>>, Runnable
{
	private static final int NONE = 0;
	private static final int QUEUED = 1;
	private static final int RUNNING = 2;

	private final IMMOExecutor<T> _executor;
	private final Queue<ReceivablePacket<T>> _queue;

	private AtomicInteger _state = new AtomicInteger(NONE);

	public MMOExecutableQueue(IMMOExecutor<T> executor)
	{
		_executor = executor;
		_queue = new ArrayDeque<ReceivablePacket<T>>();
	}

	@Override
	public void run()
	{
		while (_state.compareAndSet(QUEUED, RUNNING))
		{
			try
			{
				for (;;)
				{
					final Runnable t = poll();
					if (t == null)
					{
						break;
					}

					t.run();
				}
			}
			finally
			{
				_state.compareAndSet(RUNNING, NONE);
			}
		}
	}

	@Override
	public int size()
	{
		return _queue.size();
	}

	@Override
	public boolean isEmpty()
	{
		return _queue.isEmpty();
	}

	@Override
	public boolean contains(Object o)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<ReceivablePacket<T>> iterator()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <E> E[] toArray(E[] a)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends ReceivablePacket<T>> c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear()
	{
		synchronized (_queue)
		{
			_queue.clear();
		}
	}

	@Override
	public boolean add(ReceivablePacket<T> e)
	{
		synchronized (_queue)
		{
			if (!_queue.add(e))
			{
				return false;
			}
		}

		if (_state.getAndSet(QUEUED) == NONE)
		{
			_executor.execute(this);
		}

		return true;
	}

	@Override
	public boolean offer(ReceivablePacket<T> e)
	{
		synchronized (_queue)
		{
			return _queue.offer(e);
		}
	}

	@Override
	public ReceivablePacket<T> remove()
	{
		synchronized (_queue)
		{
			return _queue.remove();
		}
	}

	@Override
	public ReceivablePacket<T> poll()
	{
		synchronized (_queue)
		{
			return _queue.poll();
		}
	}

	@Override
	public ReceivablePacket<T> element()
	{
		synchronized (_queue)
		{
			return _queue.element();
		}
	}

	@Override
	public ReceivablePacket<T> peek()
	{
		synchronized (_queue)
		{
			return _queue.peek();
		}
	}
}
