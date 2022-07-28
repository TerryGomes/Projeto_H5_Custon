package l2mv.gameserver.network.serverpackets;

/**
 * Format: (chd) d
 * d: seconds left
 */
public class ExCubeGameChangeTimeToStart extends L2GameServerPacket
{
	int _seconds;

	public ExCubeGameChangeTimeToStart(int seconds)
	{
		this._seconds = seconds;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x97);
		this.writeD(0x03);

		this.writeD(this._seconds);
	}
}