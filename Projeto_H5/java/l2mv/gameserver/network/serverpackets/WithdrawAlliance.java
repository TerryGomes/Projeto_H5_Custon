package l2mv.gameserver.network.serverpackets;

public class WithdrawAlliance extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(0xAB);
		// TODO d
	}
}