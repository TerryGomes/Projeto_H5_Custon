package l2f.gameserver.network.serverpackets;

public class PartySmallWindowDeleteAll extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new PartySmallWindowDeleteAll();

	@Override
	protected final void writeImpl()
	{
		writeC(0x50);
	}
}