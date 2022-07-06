package l2mv.commons.net.nio.impl;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("rawtypes")
public class MMOConnection<T extends MMOClient>
{
	private final SelectorThread<T> _selectorThread;

	private final SelectionKey _selectionKey;
	private final Socket _socket;
	private final WritableByteChannel _writableByteChannel;
	private final ReadableByteChannel _readableByteChannel;

	private final Queue<SendablePacket<T>> _sendQueue;
	private final Queue<ReceivablePacket<T>> _recvQueue;

	private T _client;
	private ByteBuffer _readBuffer, _primaryWriteBuffer, _secondaryWriteBuffer;

	private boolean _pendingClose;
	private long _pendingCloseTime;
	private boolean _closed;

	private long _pendingWriteTime;
	private AtomicBoolean _isPengingWrite = new AtomicBoolean();

	public MMOConnection(SelectorThread<T> selectorThread, Socket socket, SelectionKey key)
	{
		_selectorThread = selectorThread;
		_selectionKey = key;
		_socket = socket;
		_writableByteChannel = socket.getChannel();
		_readableByteChannel = socket.getChannel();
		_sendQueue = new ArrayDeque<SendablePacket<T>>();
		_recvQueue = new MMOExecutableQueue<T>(selectorThread.getExecutor());
	}

	protected void setClient(T client)
	{
		_client = client;
	}

	public T getClient()
	{
		return _client;
	}

	public void recvPacket(ReceivablePacket<T> rp)
	{
		if ((rp == null) || isClosed())
		{
			return;
		}

		_recvQueue.add(rp);
	}

	public void sendPacket(SendablePacket<T> sp)
	{
		if (sp == null)
		{
			return;
		}

		synchronized (this)
		{
			if (isClosed())
			{
				return;
			}

			_sendQueue.add(sp);
		}

		scheduleWriteInterest();
	}

	@SuppressWarnings("unchecked")
	public void sendPacket(SendablePacket<T>... args)
	{
		if (args == null || args.length == 0)
		{
			return;
		}

		synchronized (this)
		{
			if (isClosed())
			{
				return;
			}

			for (SendablePacket<T> sp : args)
			{
				if (sp != null)
				{
					_sendQueue.add(sp);
				}
			}
		}

		scheduleWriteInterest();
	}

	public void sendPackets(List<? extends SendablePacket<T>> args)
	{
		if (args == null || args.isEmpty())
		{
			return;
		}

		SendablePacket<T> sp;

		synchronized (this)
		{
			if (isClosed())
			{
				return;
			}

			for (int i = 0; i < args.size(); i++)
			{
				if ((sp = args.get(i)) != null)
				{
					_sendQueue.add(sp);
				}
			}
		}

		scheduleWriteInterest();
	}

	protected SelectionKey getSelectionKey()
	{
		return _selectionKey;
	}

	/**
	 * ?????????? ????????? ???????????? ???????? OP_READ
	 */
	protected void disableReadInterest()
	{
		try
		{
			_selectionKey.interestOps(_selectionKey.interestOps() & ~SelectionKey.OP_READ);
		}
		catch (CancelledKeyException e)
		{
			// ignore
		}
	}

	/**
	 * ????????? ???????????? ???????? OP_WRITE
	 */
	protected void scheduleWriteInterest()
	{
		try
		{
			if (_isPengingWrite.compareAndSet(false, true))
			{
				_pendingWriteTime = System.currentTimeMillis();
			}
		}
		catch (CancelledKeyException e)
		{
			// ignore
		}
	}

	/**
	 * ?????????? ????????? ???????????? ???????? OP_WRITE
	 */
	protected void disableWriteInterest()
	{
		try
		{
			if (_isPengingWrite.compareAndSet(true, false))
			{
				_selectionKey.interestOps(_selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
			}
		}
		catch (CancelledKeyException e)
		{
			// ignore
		}
	}

	protected void enableWriteInterest()
	{
		if (_isPengingWrite.compareAndSet(true, false))
		{
			_selectionKey.interestOps(_selectionKey.interestOps() | SelectionKey.OP_WRITE);
		}
	}

	protected boolean isPendingWrite()
	{
		return _isPengingWrite.get();
	}

	public long getPendingWriteTime()
	{
		return _pendingWriteTime;
	}

	public Socket getSocket()
	{
		return _socket;
	}

	public WritableByteChannel getWritableChannel()
	{
		return _writableByteChannel;
	}

	public ReadableByteChannel getReadableByteChannel()
	{
		return _readableByteChannel;
	}

	protected Queue<SendablePacket<T>> getSendQueue()
	{
		return _sendQueue;
	}

	protected Queue<ReceivablePacket<T>> getRecvQueue()
	{
		return _recvQueue;
	}

	protected void createWriteBuffer(ByteBuffer buf)
	{
		if (_primaryWriteBuffer == null)
		{
			_primaryWriteBuffer = _selectorThread.getPooledBuffer();
			_primaryWriteBuffer.put(buf);
		}
		else
		{
			ByteBuffer temp = _selectorThread.getPooledBuffer();
			temp.put(buf);

			int remaining = temp.remaining();
			_primaryWriteBuffer.flip();
			int limit = _primaryWriteBuffer.limit();

			if (remaining >= _primaryWriteBuffer.remaining())
			{
				temp.put(_primaryWriteBuffer);
				_selectorThread.recycleBuffer(_primaryWriteBuffer);
				_primaryWriteBuffer = temp;
			}
			else
			{
				_primaryWriteBuffer.limit(remaining);
				temp.put(_primaryWriteBuffer);
				_primaryWriteBuffer.limit(limit);
				_primaryWriteBuffer.compact();
				_secondaryWriteBuffer = _primaryWriteBuffer;
				_primaryWriteBuffer = temp;
			}
		}
	}

	protected boolean hasPendingWriteBuffer()
	{
		return _primaryWriteBuffer != null;
	}

	protected void movePendingWriteBufferTo(ByteBuffer dest)
	{
		_primaryWriteBuffer.flip();
		dest.put(_primaryWriteBuffer);
		_selectorThread.recycleBuffer(_primaryWriteBuffer);
		_primaryWriteBuffer = _secondaryWriteBuffer;
		_secondaryWriteBuffer = null;
	}

	protected void setReadBuffer(ByteBuffer buf)
	{
		_readBuffer = buf;
	}

	public ByteBuffer getReadBuffer()
	{
		return _readBuffer;
	}

	public boolean isClosed()
	{
		return _pendingClose || _closed;
	}

	public boolean isPengingClose()
	{
		return _pendingClose;
	}

	public long getPendingCloseTime()
	{
		return _pendingCloseTime;
	}

	protected void close() throws IOException
	{
		_closed = true;
		_socket.close();
	}

	protected void closeNow()
	{
		synchronized (this)
		{
			if (isClosed())
			{
				return;
			}

			_sendQueue.clear();

			_pendingClose = true;
			_pendingCloseTime = System.currentTimeMillis();
		}

		disableReadInterest();
		disableWriteInterest();
	}

	public void close(SendablePacket<T> sp)
	{
		synchronized (this)
		{
			if (isClosed())
			{
				return;
			}

			_sendQueue.clear();

			sendPacket(sp);

			_pendingClose = true;
			_pendingCloseTime = System.currentTimeMillis();
		}

		disableReadInterest();
	}

	protected void closeLater()
	{
		synchronized (this)
		{
			if (isClosed())
			{
				return;
			}

			_pendingClose = true;
			_pendingCloseTime = System.currentTimeMillis();
		}
	}

	protected void releaseBuffers()
	{
		if (_primaryWriteBuffer != null)
		{
			_selectorThread.recycleBuffer(_primaryWriteBuffer);
			_primaryWriteBuffer = null;
			if (_secondaryWriteBuffer != null)
			{
				_selectorThread.recycleBuffer(_secondaryWriteBuffer);
				_secondaryWriteBuffer = null;
			}
		}
		if (_readBuffer != null)
		{
			_selectorThread.recycleBuffer(_readBuffer);
			_readBuffer = null;
		}
	}

	protected void clearQueues()
	{
		_sendQueue.clear();
		_recvQueue.clear();
	}

	protected void onDisconnection()
	{
		getClient().onDisconnection();
	}

	protected void onForcedDisconnection()
	{
		getClient().onForcedDisconnection();
	}

	@Override
	public String toString()
	{
		return "MMOConnection: selector=" + _selectorThread + "; client=" + getClient();
	}
}