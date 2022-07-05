package l2f.gameserver.network.serverpackets;

public class ExPlayAnimation extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeEx(0x5A);
		// TODO dcdS
	}
}