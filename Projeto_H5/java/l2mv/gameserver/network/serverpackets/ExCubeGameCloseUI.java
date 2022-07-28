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
		this.writeEx(0x97);
		this.writeD(0xffffffff);
	}
}