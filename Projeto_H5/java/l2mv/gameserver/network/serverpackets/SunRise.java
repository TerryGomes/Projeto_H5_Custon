package l2mv.gameserver.network.serverpackets;

public class SunRise extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		this.writeC(0x12);
	}
}