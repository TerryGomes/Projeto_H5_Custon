package l2mv.gameserver.network.loginservercon.gspackets;

import l2mv.gameserver.network.loginservercon.SendablePacket;

public class SetAccountInfo extends SendablePacket
{
	private final String _account;
	private final int _size;
	private final int[] _deleteChars;

	public SetAccountInfo(String account, int size, int[] deleteChars)
	{
		this._account = account;
		this._size = size;
		this._deleteChars = deleteChars;
	}

	@Override
	protected void writeImpl()
	{
		this.writeC(0x05);
		this.writeS(this._account);
		this.writeC(this._size);
		this.writeD(this._deleteChars.length);
		for (int i : this._deleteChars)
		{
			this.writeD(i);
		}
	}
}
