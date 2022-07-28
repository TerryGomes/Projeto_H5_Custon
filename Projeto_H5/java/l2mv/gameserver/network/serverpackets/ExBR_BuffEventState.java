package l2mv.gameserver.network.serverpackets;

public class ExBR_BuffEventState extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeEx(0xDB);
		// TODO dddd
	}
}