package l2f.gameserver.network.serverpackets;

public class ExShowVariationCancelWindow extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ExShowVariationCancelWindow();

	@Override
	protected final void writeImpl()
	{
		writeEx(0x52);
	}
}