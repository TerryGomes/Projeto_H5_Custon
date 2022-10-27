package l2mv.gameserver.network.serverpackets;

public class ExEventMatchObserver extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeEx(0x0E);
		// TODO dccSS
	}
}