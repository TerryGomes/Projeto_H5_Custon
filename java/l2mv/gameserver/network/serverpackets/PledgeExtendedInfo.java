package l2mv.gameserver.network.serverpackets;

public class PledgeExtendedInfo extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		this.writeC(0x8A);
		// TODO SddSddddddddSd
	}
}