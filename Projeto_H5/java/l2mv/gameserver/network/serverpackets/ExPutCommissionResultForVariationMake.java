package l2mv.gameserver.network.serverpackets;

public class ExPutCommissionResultForVariationMake extends L2GameServerPacket
{
	private int _gemstoneObjId, _unk1, _unk3;
	private long _gemstoneCount, _unk2;

	public ExPutCommissionResultForVariationMake(int gemstoneObjId, long count)
	{
		this._gemstoneObjId = gemstoneObjId;
		this._unk1 = 1;
		this._gemstoneCount = count;
		this._unk2 = 1;
		this._unk3 = 1;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x55);
		this.writeD(this._gemstoneObjId);
		this.writeD(this._unk1);
		this.writeQ(this._gemstoneCount);
		this.writeQ(this._unk2);
		this.writeD(this._unk3);
	}
}