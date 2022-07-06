package l2mv.gameserver.network.serverpackets;

public class CharacterDeleteSuccess extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(0x1d);
	}
}