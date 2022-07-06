package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

public class ExVoteSystemInfo extends L2GameServerPacket
{
	private int _receivedRec, _givingRec, _time, _bonusPercent;
	private boolean _showTimer;

	public ExVoteSystemInfo(Player player)
	{
		_receivedRec = player.getRecomLeft();
		_givingRec = player.getRecomHave();
		_time = player.getRecomBonusTime();
		_bonusPercent = player.getRecomBonus();
		_showTimer = !player.isRecomTimerActive() || player.isHourglassEffected();
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0xC9);
		writeD(_receivedRec); // полученые реки
		writeD(_givingRec); // отданые реки
		writeD(_time); // таймер скок секунд осталось
		writeD(_bonusPercent); // процент бонуса
		writeD(_showTimer ? 0x01 : 0x00); // если ноль то таймера нету 1 - пишет чтоли "Работает"
	}
}