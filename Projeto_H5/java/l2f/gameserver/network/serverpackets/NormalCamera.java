package l2f.gameserver.network.serverpackets;

public class NormalCamera extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(0xd7);
	}
}