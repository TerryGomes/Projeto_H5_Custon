package l2mv.gameserver.network.serverpackets;

public class ExSetMpccRouting extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeEx(0x37);
		// TODO d
	}
}