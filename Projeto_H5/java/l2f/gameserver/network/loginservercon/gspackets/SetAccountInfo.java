package l2f.gameserver.network.loginservercon.gspackets;

import l2f.gameserver.network.loginservercon.SendablePacket;

public class SetAccountInfo extends SendablePacket
{
	private final String _account;
	private final int _size;
	private final int[] _deleteChars;

	public SetAccountInfo(String account, int size, int[] deleteChars)
	{
		_account = account;
		_size = size;
		_deleteChars = deleteChars;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x05);
		writeS(_account);
		writeC(_size);
		writeD(_deleteChars.length);
		for (int i : _deleteChars)
		{
			writeD(i);
		}
	}
}
