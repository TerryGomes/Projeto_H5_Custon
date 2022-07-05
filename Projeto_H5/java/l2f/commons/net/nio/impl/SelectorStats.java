package l2f.commons.net.nio.impl;

import java.util.concurrent.atomic.AtomicLong;

public class SelectorStats
{
	private AtomicLong _connectionsTotal = new AtomicLong();
	private AtomicLong _connectionsCurrent = new AtomicLong();
	private AtomicLong _connectionsMax = new AtomicLong();
	private AtomicLong _incomingBytesTotal = new AtomicLong();
	private AtomicLong _outgoingBytesTotal = new AtomicLong();
	private AtomicLong _incomingPacketsTotal = new AtomicLong();
	private AtomicLong _outgoingPacketsTotal = new AtomicLong();
	private AtomicLong _bytesMaxPerRead = new AtomicLong();
	private AtomicLong _bytesMaxPerWrite = new AtomicLong();

	public void increaseOpenedConnections()
	{
		if (_connectionsCurrent.incrementAndGet() > _connectionsMax.get())
		{
			_connectionsMax.incrementAndGet();
		}
		_connectionsTotal.incrementAndGet();
	}

	public void decreseOpenedConnections()
	{
		_connectionsCurrent.decrementAndGet();
	}

	public void increaseIncomingBytes(int size)
	{
		if (size > _bytesMaxPerRead.get())
		{
			_bytesMaxPerRead.set(size);
		}
		_incomingBytesTotal.addAndGet(size);
	}

	public void increaseOutgoingBytes(int size)
	{
		if (size > _bytesMaxPerWrite.get())
		{
			_bytesMaxPerWrite.set(size);
		}
		_outgoingBytesTotal.addAndGet(size);
	}

	public void increaseIncomingPacketsCount()
	{
		_incomingPacketsTotal.incrementAndGet();
	}

	public void increaseOutgoingPacketsCount()
	{
		_outgoingPacketsTotal.incrementAndGet();
	}

	public long getTotalConnections()
	{
		return _connectionsTotal.get();
	}

	public long getCurrentConnections()
	{
		return _connectionsCurrent.get();
	}

	public long getMaximumConnections()
	{
		return _connectionsMax.get();
	}

	public long getIncomingBytesTotal()
	{
		return _incomingBytesTotal.get();
	}

	public long getOutgoingBytesTotal()
	{
		return _outgoingBytesTotal.get();
	}

	public long getIncomingPacketsTotal()
	{
		return _incomingPacketsTotal.get();
	}

	public long getOutgoingPacketsTotal()
	{
		return _outgoingPacketsTotal.get();
	}

	public long getMaxBytesPerRead()
	{
		return _bytesMaxPerRead.get();
	}

	public long getMaxBytesPerWrite()
	{
		return _bytesMaxPerWrite.get();
	}
}
