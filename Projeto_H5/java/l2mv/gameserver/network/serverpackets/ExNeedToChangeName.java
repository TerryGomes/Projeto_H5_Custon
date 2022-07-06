package l2mv.gameserver.network.serverpackets;

public class ExNeedToChangeName extends L2GameServerPacket
{
	private int _type, _reason;
	private String _origName;

	public ExNeedToChangeName(int type, int reason, String origName)
	{
		_type = type;
		_reason = reason;
		_origName = origName;
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0x69);
		writeD(_type);
		writeD(_reason);
		writeS(_origName);
	}
}