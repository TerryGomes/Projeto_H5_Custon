package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.SevenSigns;

public class ShowMiniMap extends L2GameServerPacket
{
	private int _mapId, _period;

	public ShowMiniMap(Player player, int mapId)
	{
		this._mapId = mapId;
		this._period = SevenSigns.getInstance().getCurrentPeriod();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xa3);
		this.writeD(this._mapId);
		this.writeC(this._period);
	}
}