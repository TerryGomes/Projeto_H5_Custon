package l2f.gameserver.network.serverpackets;

public class CharacterCreateFail extends L2GameServerPacket
{
	public static final L2GameServerPacket REASON_TOO_MANY_CHARACTERS = new CharacterCreateFail(0x01);
	public static final L2GameServerPacket REASON_NAME_ALREADY_EXISTS = new CharacterCreateFail(0x02);
	public static final L2GameServerPacket REASON_16_ENG_CHARS = new CharacterCreateFail(0x03);

	private int _error;

	private CharacterCreateFail(int errorCode)
	{
		_error = errorCode;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x10);
		writeD(_error);
	}
}