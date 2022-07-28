package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

/**
 * Format: (chd) dddddd
 * d: time left
 * d: blue points
 * d: red points
 * d: team
 * d: player object id
 * d: player points
 */
public class ExCubeGameExtendedChangePoints extends L2GameServerPacket
{
	private int _timeLeft;
	private int _bluePoints;
	private int _redPoints;
	private boolean _isRedTeam;
	private int _objectId;
	private int _playerPoints;

	public ExCubeGameExtendedChangePoints(int timeLeft, int bluePoints, int redPoints, boolean isRedTeam, Player player, int playerPoints)
	{
		this._timeLeft = timeLeft;
		this._bluePoints = bluePoints;
		this._redPoints = redPoints;
		this._isRedTeam = isRedTeam;
		this._objectId = player.getObjectId();
		this._playerPoints = playerPoints;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x98);
		this.writeD(0x00);

		this.writeD(this._timeLeft);
		this.writeD(this._bluePoints);
		this.writeD(this._redPoints);

		this.writeD(this._isRedTeam ? 0x01 : 0x00);
		this.writeD(this._objectId);
		this.writeD(this._playerPoints);
	}
}