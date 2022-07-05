package l2f.gameserver.network.serverpackets;

public class ServerClose extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ServerClose();

	@Override
	protected void writeImpl()
	{
		writeC(0x20);
	}
}