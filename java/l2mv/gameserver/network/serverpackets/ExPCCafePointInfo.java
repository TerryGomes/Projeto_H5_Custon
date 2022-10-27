package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

/**
 * Format: ch ddcdc
 *
 * Args: player, points to add, type of period (default 1), type of points (1-double, 2-integer), time left to the end of period
 */
public class ExPCCafePointInfo extends L2GameServerPacket
{
	private int _mAddPoint, _mPeriodType, _pointType, _pcBangPoints, _remainTime;

	public ExPCCafePointInfo(Player player, int mAddPoint, int mPeriodType, int pointType, int remainTime)
	{
		this._pcBangPoints = player.getPcBangPoints();
		this._mAddPoint = mAddPoint;
		this._mPeriodType = mPeriodType;
		this._pointType = pointType;
		this._remainTime = remainTime;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x32);
		this.writeD(this._pcBangPoints);
		this.writeD(this._mAddPoint);
		this.writeC(this._mPeriodType);
		this.writeD(this._remainTime);
		this.writeC(this._pointType);
	}
}