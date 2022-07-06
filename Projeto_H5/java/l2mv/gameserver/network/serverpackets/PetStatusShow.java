package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Summon;

public class PetStatusShow extends L2GameServerPacket
{
	private int _summonType;

	public PetStatusShow(Summon summon)
	{
		_summonType = summon.getSummonType();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xb1);
		writeD(_summonType);
	}
}