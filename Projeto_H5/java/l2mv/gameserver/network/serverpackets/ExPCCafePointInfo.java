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
		_pcBangPoints = player.getPcBangPoints();
		_mAddPoint = mAddPoint;
		_mPeriodType = mPeriodType;
		_pointType = pointType;
		_remainTime = remainTime;
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0x32);
		writeD(_pcBangPoints);
		writeD(_mAddPoint);
		writeC(_mPeriodType);
		writeD(_remainTime);
		writeC(_pointType);
	}
}