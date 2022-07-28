package l2mv.gameserver.network.serverpackets;

public class ExEventMatchCreate extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeEx(0x1D);
		// TODO d
	}
}