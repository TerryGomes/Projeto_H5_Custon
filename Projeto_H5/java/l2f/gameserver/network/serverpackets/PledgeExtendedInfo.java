package l2f.gameserver.network.serverpackets;

public class PledgeExtendedInfo extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(0x8A);
		// TODO SddSddddddddSd
	}
}