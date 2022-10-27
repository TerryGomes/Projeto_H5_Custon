package l2mv.gameserver.network.serverpackets;

public class PledgeCrest extends L2GameServerPacket
{
	private int _crestId;
	private int _crestSize;
	private byte[] _data;

	public PledgeCrest(int crestId, byte[] data)
	{
		this._crestId = crestId;
		this._data = data;
		this._crestSize = this._data.length;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x6a);
		this.writeD(this._crestId);
		this.writeD(this._crestSize);
		this.writeB(this._data);
	}
}