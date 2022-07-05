package l2f.gameserver.network.serverpackets;

public class DeleteRadar extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(0xB8);
		// TODO ddd
	}
}