package l2mv.gameserver.network.serverpackets;

public class AllianceCrest extends L2GameServerPacket
{
	private int _crestId;
	private byte[] _data;

	public AllianceCrest(int crestId, byte[] data)
	{
		this._crestId = crestId;
		this._data = data;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xaf);
		this.writeD(this._crestId);
		this.writeD(this._data.length);
		this.writeB(this._data);
	}
}