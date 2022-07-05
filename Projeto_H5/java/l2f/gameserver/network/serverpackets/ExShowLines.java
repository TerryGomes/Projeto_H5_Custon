package l2f.gameserver.network.serverpackets;

public class ExShowLines extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeEx(0xA5);
		// TODO hdcc cx[ddd]
	}
}