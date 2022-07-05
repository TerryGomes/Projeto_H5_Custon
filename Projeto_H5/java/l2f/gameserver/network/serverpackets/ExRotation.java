package l2f.gameserver.network.serverpackets;

public class ExRotation extends L2GameServerPacket
{
	private int _charObjId, _degree;

	public ExRotation(int charId, int degree)
	{
		_charObjId = charId;
		_degree = degree;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0xC1);
		writeD(_charObjId);
		writeD(_degree);
	}
}
