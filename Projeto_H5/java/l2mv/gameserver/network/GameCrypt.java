package l2mv.gameserver.network;

public class GameCrypt
{
	private final byte[] _inKey = new byte[16], _outKey = new byte[16];
	private boolean _isEnabled = false;

	public void setKey(byte[] key)
	{
		System.arraycopy(key, 0, this._inKey, 0, 16);
		System.arraycopy(key, 0, this._outKey, 0, 16);
	}

	public void setKey(byte[] key, boolean value)
	{
		this.setKey(key);
	}

	public boolean decrypt(byte[] raw, int offset, int size)
	{
		if (!this._isEnabled)
		{
			return true;
		}

		int temp = 0;
		for (int i = 0; i < size; i++)
		{
			int temp2 = raw[offset + i] & 0xFF;
			raw[offset + i] = (byte) (temp2 ^ this._inKey[i & 15] ^ temp);
			temp = temp2;
		}

		int old = this._inKey[8] & 0xff;
		old |= this._inKey[9] << 8 & 0xff00;
		old |= this._inKey[10] << 0x10 & 0xff0000;
		old |= this._inKey[11] << 0x18 & 0xff000000;

		old += size;

		this._inKey[8] = (byte) (old & 0xff);
		this._inKey[9] = (byte) (old >> 0x08 & 0xff);
		this._inKey[10] = (byte) (old >> 0x10 & 0xff);
		this._inKey[11] = (byte) (old >> 0x18 & 0xff);

		return true;
	}

	public void encrypt(byte[] raw, int offset, int size)
	{
		if (!this._isEnabled)
		{
			this._isEnabled = true;
			return;
		}

		int temp = 0;
		for (int i = 0; i < size; i++)
		{
			int temp2 = raw[offset + i] & 0xFF;
			temp = temp2 ^ this._outKey[i & 15] ^ temp;
			raw[offset + i] = (byte) temp;
		}

		int old = this._outKey[8] & 0xff;
		old |= this._outKey[9] << 8 & 0xff00;
		old |= this._outKey[10] << 0x10 & 0xff0000;
		old |= this._outKey[11] << 0x18 & 0xff000000;
		old += size;

		this._outKey[8] = (byte) (old & 0xff);
		this._outKey[9] = (byte) (old >> 0x08 & 0xff);
		this._outKey[10] = (byte) (old >> 0x10 & 0xff);
		this._outKey[11] = (byte) (old >> 0x18 & 0xff);
	}
}