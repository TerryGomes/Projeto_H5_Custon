package l2mv.gameserver.network.serverpackets;

public class RequestTimeCheck extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(0xC1);
		// TODO d
	}
}