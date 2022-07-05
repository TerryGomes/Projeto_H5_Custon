package l2f.gameserver.network.serverpackets;

public class LeaveWorld extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new LeaveWorld();

	@Override
	protected final void writeImpl()
	{
		writeC(0x84);
	}
}