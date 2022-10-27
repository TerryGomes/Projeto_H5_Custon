package l2mv.gameserver.network.serverpackets;

public class CharacterCreateSuccess extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new CharacterCreateSuccess();

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x0f);
		this.writeD(0x01);
	}
}