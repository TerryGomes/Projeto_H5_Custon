package l2mv.gameserver.network.serverpackets;

public class DismissAlliance extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeC(0xAD);
		// TODO d
	}
}