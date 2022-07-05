package l2f.gameserver.network.serverpackets;

public class SunSet extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(0x13);
	}
}