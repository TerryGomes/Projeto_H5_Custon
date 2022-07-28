package l2mv.gameserver.network.serverpackets;

public class ActionFail extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ActionFail();

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x1f);
	}
}