package l2mv.gameserver.network.serverpackets;

/**
 * Format: (chd) ddd
 * d: time left
 * d: blue points
 * d: red points
 */
public class ExCubeGameChangePoints extends L2GameServerPacket
{
	int _timeLeft;
	int _bluePoints;
	int _redPoints;

	public ExCubeGameChangePoints(int timeLeft, int bluePoints, int redPoints)
	{
		this._timeLeft = timeLeft;
		this._bluePoints = bluePoints;
		this._redPoints = redPoints;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x98);
		this.writeD(0x02);

		this.writeD(this._timeLeft);
		this.writeD(this._bluePoints);
		this.writeD(this._redPoints);
	}
}