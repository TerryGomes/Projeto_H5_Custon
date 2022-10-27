package l2mv.gameserver.network.serverpackets;

/**
 * Format: (chd)
 */
public class ExCubeGameRequestReady extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeEx(0x97);
		this.writeD(0x04);
	}
}