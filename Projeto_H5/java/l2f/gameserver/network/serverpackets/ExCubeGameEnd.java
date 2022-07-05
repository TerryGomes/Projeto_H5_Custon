package l2f.gameserver.network.serverpackets;

/**
 * Format: (chd) ddd
 * d: winner team
 */
public class ExCubeGameEnd extends L2GameServerPacket
{
	boolean _isRedTeamWin;

	public ExCubeGameEnd(boolean isRedTeamWin)
	{
		_isRedTeamWin = isRedTeamWin;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0x98);
		writeD(0x01);

		writeD(_isRedTeamWin ? 0x01 : 0x00);
	}
}