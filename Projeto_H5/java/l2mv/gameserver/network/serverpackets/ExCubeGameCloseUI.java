package l2mv.gameserver.network.serverpackets;

/**
 * Format: (chd)
 */
public class ExCubeGameCloseUI extends L2GameServerPacket
{
	int _seconds;

	public ExCubeGameCloseUI()
	{
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0x97);
		writeD(0xffffffff);
	}
}