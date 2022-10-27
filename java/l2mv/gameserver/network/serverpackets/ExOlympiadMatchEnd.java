package l2mv.gameserver.network.serverpackets;

public class ExOlympiadMatchEnd extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ExOlympiadMatchEnd();

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x2D);
	}
}