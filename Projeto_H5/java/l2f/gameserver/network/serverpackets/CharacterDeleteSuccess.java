package l2f.gameserver.network.serverpackets;

public class CharacterDeleteSuccess extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(0x1d);
	}
}