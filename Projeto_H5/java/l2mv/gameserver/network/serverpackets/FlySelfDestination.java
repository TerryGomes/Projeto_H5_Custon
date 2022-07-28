package l2mv.gameserver.network.serverpackets;

public class FlySelfDestination extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeEx(0x43);
		// TODO dddd
	}
}