package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2mv.gameserver.model.entity.residence.Fortress;

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
		this._fortressId = fortress.getId();

		FortressSiegeEvent siegeEvent = fortress.getSiegeEvent();
		this._commandersMax = siegeEvent.getBarrackStatus().length;
		if (fortress.getSiegeEvent().isInProgress())
		{
			for (int i = 0; i < this._commandersMax; i++)
			{
				if (siegeEvent.getBarrackStatus()[i])
				{
					this._commandersCurrent++;
				}
			}
		}
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x17);
		this.writeD(this._fortressId);
		this.writeD(this._commandersMax);
		this.writeD(this._commandersCurrent);
	}
}