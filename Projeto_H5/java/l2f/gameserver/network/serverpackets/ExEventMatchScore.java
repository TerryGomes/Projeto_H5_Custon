package l2f.gameserver.network.serverpackets;

public class ExEventMatchScore extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeEx(0x10);
		// TODO ddd
	}
}