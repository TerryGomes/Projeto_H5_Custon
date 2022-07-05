package l2f.gameserver.network.serverpackets;

public class SunRise extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(0x12);
	}
}