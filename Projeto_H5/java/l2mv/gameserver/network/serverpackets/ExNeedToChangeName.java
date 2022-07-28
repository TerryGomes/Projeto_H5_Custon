package l2mv.gameserver.network.serverpackets;

public class ExNeedToChangeName extends L2GameServerPacket
{
	private int _type, _reason;
	private String _origName;

	public ExNeedToChangeName(int type, int reason, String origName)
	{
		this._type = type;
		this._reason = reason;
		this._origName = origName;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x69);
		this.writeD(this._type);
		this.writeD(this._reason);
		this.writeS(this._origName);
	}
}