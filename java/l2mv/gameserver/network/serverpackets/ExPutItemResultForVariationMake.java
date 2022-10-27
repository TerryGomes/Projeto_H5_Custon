package l2mv.gameserver.network.serverpackets;

public class ExPutItemResultForVariationMake extends L2GameServerPacket
{
	private int _itemObjId;
	private int _unk1;
	private int _unk2;

	public ExPutItemResultForVariationMake(int itemObjId)
	{
		this._itemObjId = itemObjId;
		this._unk1 = 1;
		this._unk2 = 1;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x53);
		this.writeD(this._itemObjId);
		this.writeD(this._unk1);
		this.writeD(this._unk2);
	}
}