package l2mv.gameserver.network.serverpackets;

public class ExResponseFreeServer extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeEx(0x77);
		// just trigger
	}
}