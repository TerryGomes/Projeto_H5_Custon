package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.SevenSigns;

public class ShowMiniMap extends L2GameServerPacket
{
	private int _mapId, _period;

	public ShowMiniMap(Player player, int mapId)
	{
		_mapId = mapId;
		_period = SevenSigns.getInstance().getCurrentPeriod();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xa3);
		writeD(_mapId);
		writeC(_period);
	}
}