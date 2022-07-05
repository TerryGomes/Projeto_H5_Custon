package l2f.commons.net.nio.impl;

import java.nio.ByteBuffer;

@SuppressWarnings("rawtypes")
public abstract class MMOClient<T extends MMOConnection>
{
	public static boolean SESSION_OK = false;
	private T _connection;
	private boolean isAuthed;

	public MMOClient(T con)
	{
		_connection = con;
	}

	protected void setConnection(T con)
	{
		_connection = con;
	}

	public T getConnection()
	{
		return _connection;
	}

	public boolean isAuthed()
	{
		return isAuthed;
	}

	public void setAuthed(boolean isAuthed)
	{
		this.isAuthed = isAuthed;
	}

	public void closeNow(boolean error)
	{
		if (isConnected())
		{
			_connection.closeNow();
		}
	}

	public void closeLater()
	{
		if (isConnected())
		{
			_connection.closeLater();
		}
	}

	public boolean isConnected()
	{
		return _connection != null && !_connection.isClosed();
	}

	public abstract boolean decrypt(ByteBuffer buf, int size);

	public abstract boolean encrypt(ByteBuffer buf, int size);

	protected void onDisconnection()
	{
	}

	protected void onForcedDisconnection()
	{
	}
}