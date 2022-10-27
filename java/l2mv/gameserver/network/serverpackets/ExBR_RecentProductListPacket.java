package l2mv.gameserver.network.serverpackets;

public class ExBR_RecentProductListPacket extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeEx(0xDC);
		// TODO dx[dhddddcccccdd]
	}
}