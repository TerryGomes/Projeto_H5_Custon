package l2f.gameserver.network.serverpackets;

public class ExPledgeCrestLarge extends L2GameServerPacket
{
	private int _crestId;
	private byte[] _data;

	public ExPledgeCrestLarge(int crestId, byte[] data)
	{
		_crestId = crestId;
		_data = data;
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0x1b);

		writeD(0x00);
		writeD(_crestId);
		writeD(_data.length);
		writeB(_data);
	}
}