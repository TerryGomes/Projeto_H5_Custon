package l2mv.gameserver.network.serverpackets;

public class ShowRadar extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		this.writeC(0xAA);
		// TODO ddddd
	}
}