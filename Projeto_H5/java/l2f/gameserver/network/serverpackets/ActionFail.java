package l2f.gameserver.network.serverpackets;

public class ActionFail extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ActionFail();

	@Override
	protected final void writeImpl()
	{
		writeC(0x1f);
	}
}