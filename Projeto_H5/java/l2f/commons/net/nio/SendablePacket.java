package l2f.commons.net.nio;

public abstract class SendablePacket<T> extends AbstractPacket<T>
{
	protected void writeC(int data)
	{
		getByteBuffer().put((byte) data);
	}

	protected void writeF(double value)
	{
		getByteBuffer().putDouble(value);
	}

	protected void writeH(int value)
	{
		getByteBuffer().putShort((short) value);
	}

	protected void writeD(int value)
	{
		getByteBuffer().putInt(value);
	}

	protected void writeQ(long value)
	{
		getByteBuffer().putLong(value);
	}

	protected void writeB(byte[] data)
	{
		getByteBuffer().put(data);
	}

	protected void writeS(CharSequence charSequence)
	{
		if (charSequence != null)
		{
			int length = charSequence.length();
			for (int i = 0; i < length; i++)
			{
				getByteBuffer().putChar(charSequence.charAt(i));
			}
		}
		getByteBuffer().putChar('\000');
	}

	protected abstract boolean write();
}
