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
		_objectId = player.getObjectId();
		DominionSiegeEvent siegeEvent = player.getEvent(DominionSiegeEvent.class);
		_territoryId = siegeEvent.getId();
		_isDisguised = siegeEvent.getObjects(DominionSiegeEvent.DISGUISE_PLAYERS).contains(_objectId);
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0xA3);
		writeD(_objectId);
		writeD(1);
		writeD(_territoryId); // territory Id
		writeD(_isDisguised ? 1 : 0);
		writeD(_isDisguised ? _territoryId : 0); // territory Id
	}
}
