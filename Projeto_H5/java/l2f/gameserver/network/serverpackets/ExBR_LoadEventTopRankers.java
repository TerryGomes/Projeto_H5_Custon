package l2f.gameserver.network.serverpackets;

public class ExBR_LoadEventTopRankers extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeEx(0xBD);
		// TODO ddddd
	}
}