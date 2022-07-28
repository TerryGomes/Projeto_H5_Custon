package l2mv.gameserver.network.serverpackets;

public class ExChangeNicknameNColor extends L2GameServerPacket
{
	private int _itemObjId;

	public ExChangeNicknameNColor(int itemObjId)
	{
		this._itemObjId = itemObjId;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x83);
		this.writeD(this._itemObjId);
	}
}