package l2f.gameserver.network.serverpackets;

public class CharacterCreateSuccess extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new CharacterCreateSuccess();

	@Override
	protected final void writeImpl()
	{
		writeC(0x0f);
		writeD(0x01);
	}
}