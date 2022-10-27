package l2mv.gameserver.network.serverpackets;

public class ExPutIntensiveResultForVariationMake extends L2GameServerPacket
{
	private int _refinerItemObjId, _lifestoneItemId, _gemstoneItemId, _unk;
	private long _gemstoneCount;

	public ExPutIntensiveResultForVariationMake(int refinerItemObjId, int lifeStoneId, int gemstoneItemId, long gemstoneCount)
	{
		this._refinerItemObjId = refinerItemObjId;
		this._lifestoneItemId = lifeStoneId;
		this._gemstoneItemId = gemstoneItemId;
		this._gemstoneCount = gemstoneCount;
		this._unk = 1;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x54);
		this.writeD(this._refinerItemObjId);
		this.writeD(this._lifestoneItemId);
		this.writeD(this._gemstoneItemId);
		this.writeQ(this._gemstoneCount);
		this.writeD(this._unk);
	}
}