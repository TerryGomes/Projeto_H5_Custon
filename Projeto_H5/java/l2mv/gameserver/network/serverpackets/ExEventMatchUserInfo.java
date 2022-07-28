package l2mv.gameserver.network.serverpackets;

public class ExEventMatchUserInfo extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeEx(0x02);
		// TODO dSdddddddd
	}
}