package l2mv.gameserver.network.serverpackets;

public class ExShowLines extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeEx(0xA5);
		// TODO hdcc cx[ddd]
	}
}