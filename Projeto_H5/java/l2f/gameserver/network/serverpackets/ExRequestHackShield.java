package l2f.gameserver.network.serverpackets;

public class ExRequestHackShield extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeEx(0x49);
	}
}