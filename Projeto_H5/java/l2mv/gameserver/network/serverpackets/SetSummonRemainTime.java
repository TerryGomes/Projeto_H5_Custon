package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Summon;

public class SetSummonRemainTime extends L2GameServerPacket
{
	private final int _maxFed;
	private final int _curFed;

	public SetSummonRemainTime(Summon summon)
	{
		this._curFed = summon.getCurrentFed();
		this._maxFed = summon.getMaxFed();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xD1);
		this.writeD(this._maxFed);
		this.writeD(this._curFed);
	}
}