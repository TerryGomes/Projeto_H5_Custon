package l2mv.gameserver.network.serverpackets;

public class ExRotation extends L2GameServerPacket
{
	private int _charObjId, _degree;

	public ExRotation(int charId, int degree)
	{
		this._charObjId = charId;
		this._degree = degree;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xC1);
		this.writeD(this._charObjId);
		this.writeD(this._degree);
	}
}
