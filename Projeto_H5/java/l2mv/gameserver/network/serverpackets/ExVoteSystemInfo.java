package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

public class ExVoteSystemInfo extends L2GameServerPacket
{
	private int _receivedRec, _givingRec, _time, _bonusPercent;
	private boolean _showTimer;

	public ExVoteSystemInfo(Player player)
	{
		this._receivedRec = player.getRecomLeft();
		this._givingRec = player.getRecomHave();
		this._time = player.getRecomBonusTime();
		this._bonusPercent = player.getRecomBonus();
		this._showTimer = !player.isRecomTimerActive() || player.isHourglassEffected();
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xC9);
		this.writeD(this._receivedRec); // полученые реки
		this.writeD(this._givingRec); // отданые реки
		this.writeD(this._time); // таймер скок секунд осталось
		this.writeD(this._bonusPercent); // процент бонуса
		this.writeD(this._showTimer ? 0x01 : 0x00); // если ноль то таймера нету 1 - пишет чтоли "Работает"
	}
}