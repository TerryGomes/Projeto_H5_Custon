package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Summon;

public class SetSummonRemainTime extends L2GameServerPacket
{
	private final int _maxFed;
	private final int _curFed;

	public SetSummonRemainTime(Summon summon)
	{
		_curFed = summon.getCurrentFed();
		_maxFed = summon.getMaxFed();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xD1);
		writeD(_maxFed);
		writeD(_curFed);
	}
}