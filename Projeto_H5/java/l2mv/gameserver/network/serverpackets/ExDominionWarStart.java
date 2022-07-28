package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.events.impl.DominionSiegeEvent;

/**
 * @author VISTALL
 * @date 12:08/05.03.2011
 */
public class ExDominionWarStart extends L2GameServerPacket
{
	private int _objectId;
	private int _territoryId;
	private boolean _isDisguised;

	public ExDominionWarStart(Player player)
	{
		this._objectId = player.getObjectId();
		DominionSiegeEvent siegeEvent = player.getEvent(DominionSiegeEvent.class);
		this._territoryId = siegeEvent.getId();
		this._isDisguised = siegeEvent.getObjects(DominionSiegeEvent.DISGUISE_PLAYERS).contains(this._objectId);
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xA3);
		this.writeD(this._objectId);
		this.writeD(1);
		this.writeD(this._territoryId); // territory Id
		this.writeD(this._isDisguised ? 1 : 0);
		this.writeD(this._isDisguised ? this._territoryId : 0); // territory Id
	}
}
