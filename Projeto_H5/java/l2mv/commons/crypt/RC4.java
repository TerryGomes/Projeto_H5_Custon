package l2mv.commons.crypt;

public class RC4
{
	private byte state[] = new byte[256];
	private int x;
	private int y;
	private byte[] _key;
	private boolean _block = false;

	public RC4(String key, boolean block) throws NullPointerException
	{
		this(key.getBytes(), block);
	}

	public RC4(byte[] key, boolean block) throws NullPointerException
	{
		_key = key;
		_block = block;
		setKey();
	}

	private void setKey()
	{
		for (int i = 0; i < 256; i++)
		{
			state[i] = (byte) i;
		}

		x = 0;
		y = 0;

		int index1 = 0;
		int index2 = 0;

		byte tmp;

		if (_key == null || _key.length == 0)
		{
			throw new NullPointerException();
		}
		for (int i = 0; i < 256; i++)
		{

			index2 = ((_key[index1] & 0xff) + (state[i] & 0xff) + index2) & 0xff;

			tmp = state[i];
			state[i] = state[index2];
			state[index2] = tmp;

			index1 = (index1 + 1) % _key.length;
		}
	}

	public byte[] rc4(String data)
	{

		if (data == null || data.length() == 0)
		{
			return null;
		}

		return rc4(data.getBytes());

	}

	public byte[] rc4(byte[] buf)
	{
		int xorIndex;
		byte tmp;

		if (buf == null)
		{
			return null;
		}

		byte[] result = new byte[buf.length];

		for (int i = 0; i < buf.length; i++)
		{

			x = (x + 1) & 0xff;
			y = ((state[x] & 0xff) + y) & 0xff;

			tmp = state[x];
			state[x] = state[y];
			state[y] = tmp;

			xorIndex = ((state[x] & 0xff) + (state[y] & 0xff)) & 0xff;
			result[i] = (byte) ((buf[i] ^ state[xorIndex]) & 0xff);
		}
		if (_block)
		{
			x = 0;
			y = 0;
		}
		else if (x > 5000 || y > 5000)
		{
			x = 0;
			y = 0;
		}

		return result;
	}

}