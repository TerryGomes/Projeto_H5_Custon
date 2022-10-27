package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2mv.gameserver.model.entity.residence.Fortress;

/**
 * @author VISTALL
 */
public class ExShowFortressMapInfo extends L2GameServerPacket
{
	private int _fortressId;
	private boolean _fortressStatus;
	private boolean[] _commanders;

	public ExShowFortressMapInfo(Fortress fortress)
	{
		this._fortressId = fortress.getId();
		this._fortressStatus = fortress.getSiegeEvent().isInProgress();

		FortressSiegeEvent siegeEvent = fortress.getSiegeEvent();
		this._commanders = siegeEvent.getBarrackStatus();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x7d);

		this.writeD(this._fortressId);
		this.writeD(this._fortressStatus);
		this.writeD(this._commanders.length);
		for (boolean b : this._commanders)
		{
			this.writeD(b);
		}
	}
}