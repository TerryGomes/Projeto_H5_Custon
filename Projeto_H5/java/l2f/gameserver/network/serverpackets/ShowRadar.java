package l2f.gameserver.network.serverpackets;

public class ShowRadar extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(0xAA);
		// TODO ddddd
	}
}