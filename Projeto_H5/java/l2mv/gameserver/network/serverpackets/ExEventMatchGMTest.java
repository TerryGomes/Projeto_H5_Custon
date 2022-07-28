package l2mv.gameserver.network.serverpackets;

public class ExEventMatchGMTest extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeEx(0x07);
		// just trigger
	}
}