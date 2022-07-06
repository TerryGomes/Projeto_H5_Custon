package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.entity.events.impl.DuelEvent;

public class ExDuelEnd extends L2GameServerPacket
{
	private int _duelType;

	public ExDuelEnd(DuelEvent e)
	{
		_duelType = e.getDuelType();
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0x4f);
		writeD(_duelType);
	}
}