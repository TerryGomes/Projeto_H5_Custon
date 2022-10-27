package l2mv.gameserver.network.serverpackets;

public class ExPledgeCrestLarge extends L2GameServerPacket
{
	private int _crestId;
	private byte[] _data;

	public ExPledgeCrestLarge(int crestId, byte[] data)
	{
		this._crestId = crestId;
		this._data = data;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x1b);

		this.writeD(0x00);
		this.writeD(this._crestId);
		this.writeD(this._data.length);
		this.writeB(this._data);
	}
}