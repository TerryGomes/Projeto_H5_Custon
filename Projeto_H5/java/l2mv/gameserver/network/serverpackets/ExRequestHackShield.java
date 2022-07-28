package l2mv.gameserver.network.serverpackets;

public class ExRequestHackShield extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x49);
	}
}