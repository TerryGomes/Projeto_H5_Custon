package l2f.gameserver.network.serverpackets;

public class FlySelfDestination extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeEx(0x43);
		// TODO dddd
	}
}