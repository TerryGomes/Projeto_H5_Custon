package l2f.gameserver.network.serverpackets;

public class ExEventMatchCreate extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeEx(0x1D);
		// TODO d
	}
}