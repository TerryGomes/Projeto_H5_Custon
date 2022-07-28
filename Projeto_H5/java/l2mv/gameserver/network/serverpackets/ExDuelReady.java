package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.entity.events.impl.DuelEvent;

public class ExDuelReady extends L2GameServerPacket
{
	private int _duelType;

	public ExDuelReady(DuelEvent event)
	{
		this._duelType = event.getDuelType();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x4d);
		this.writeD(this._duelType);
	}
}