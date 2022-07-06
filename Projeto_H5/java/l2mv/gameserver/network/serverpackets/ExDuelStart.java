package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.entity.events.impl.DuelEvent;

public class ExDuelStart extends L2GameServerPacket
{
	private int _duelType;

	public ExDuelStart(DuelEvent e)
	{
		_duelType = e.getDuelType();
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0x4e);
		writeD(_duelType);
	}
}