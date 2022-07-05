package l2f.gameserver.network.serverpackets;

public class ExPVPMatchCCRetire extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ExPVPMatchCCRetire();

	@Override
	public void writeImpl()
	{
		writeEx(0x8B);
	}
}