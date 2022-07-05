package l2f.gameserver.network.serverpackets;

public class ExBR_RecentProductListPacket extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeEx(0xDC);
		// TODO dx[dhddddcccccdd]
	}
}