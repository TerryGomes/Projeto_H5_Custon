package l2f.gameserver.network.serverpackets;

public class ExRaidCharSelected extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeEx(0xB5);
		// just a trigger
	}
}