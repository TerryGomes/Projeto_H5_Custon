package l2mv.gameserver.network.serverpackets;

public class ExNotifyBirthDay extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ExNotifyBirthDay();

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x8F);
	}
}