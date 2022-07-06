package l2mv.commons.net.nio.impl;

import java.nio.ByteBuffer;

@SuppressWarnings("rawtypes")
public abstract class SendablePacket<T extends MMOClient> extends l2mv.commons.net.nio.SendablePacket<T>
{
	@Override
	protected ByteBuffer getByteBuffer()
	{
		return ((SelectorThread) Thread.currentThread()).getWriteBuffer();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getClient()
	{
		return (T) ((SelectorThread) Thread.currentThread()).getWriteClient();
	}

	@Override
	protected abstract boolean write();
}