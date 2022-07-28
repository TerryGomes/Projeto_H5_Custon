package l2mv.gameserver.network.serverpackets;

public class ExAutoSoulShot extends L2GameServerPacket
{
	private final int _itemId;
	private final boolean _type;

	public ExAutoSoulShot(int itemId, boolean type)
	{
		this._itemId = itemId;
		this._type = type;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x0c);

		this.writeD(this._itemId);
		this.writeD(this._type ? 1 : 0);
	}
}