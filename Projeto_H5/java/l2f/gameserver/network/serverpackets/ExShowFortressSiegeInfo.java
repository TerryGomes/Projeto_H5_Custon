package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2f.gameserver.model.entity.residence.Fortress;

/**
 * @author VISTALL
 */
public class ExShowFortressSiegeInfo extends L2GameServerPacket
{
	private int _fortressId;
	private int _commandersMax;
	private int _commandersCurrent;

	public ExShowFortressSiegeInfo(Fortress fortress)
	{
		_fortressId = fortress.getId();

		FortressSiegeEvent siegeEvent = fortress.getSiegeEvent();
		_commandersMax = siegeEvent.getBarrackStatus().length;
		if (fortress.getSiegeEvent().isInProgress())
		{
			for (int i = 0; i < _commandersMax; i++)
			{
				if (siegeEvent.getBarrackStatus()[i])
				{
					_commandersCurrent++;
				}
			}
		}
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0x17);
		writeD(_fortressId);
		writeD(_commandersMax);
		writeD(_commandersCurrent);
	}
}