package l2f.gameserver.network.serverpackets;

public class ExPutItemResultForVariationMake extends L2GameServerPacket
{
	private int _itemObjId;
	private int _unk1;
	private int _unk2;

	public ExPutItemResultForVariationMake(int itemObjId)
	{
		_itemObjId = itemObjId;
		_unk1 = 1;
		_unk2 = 1;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0x53);
		writeD(_itemObjId);
		writeD(_unk1);
		writeD(_unk2);
	}
}