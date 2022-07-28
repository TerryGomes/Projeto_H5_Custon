package l2mv.gameserver.network.serverpackets;

/**
 * Format: (chd) ddd
 * d: winner team
 */
public class ExCubeGameEnd extends L2GameServerPacket
{
	boolean _isRedTeamWin;

	public ExCubeGameEnd(boolean isRedTeamWin)
	{
		this._isRedTeamWin = isRedTeamWin;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x98);
		this.writeD(0x01);

		this.writeD(this._isRedTeamWin ? 0x01 : 0x00);
	}
}