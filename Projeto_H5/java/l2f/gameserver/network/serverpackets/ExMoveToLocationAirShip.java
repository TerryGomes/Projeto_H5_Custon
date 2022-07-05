package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.entity.boat.Boat;
import l2f.gameserver.utils.Location;

public class ExMoveToLocationAirShip extends L2GameServerPacket
{
	private int _objectId;
	private Location _origin, _destination;

	public ExMoveToLocationAirShip(Boat boat)
	{
		_objectId = boat.getObjectId();
		_origin = boat.getLoc();
		_destination = boat.getDestination();
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0x65);
		writeD(_objectId);

		writeD(_destination.x);
		writeD(_destination.y);
		writeD(_destination.z);
		writeD(_origin.x);
		writeD(_origin.y);
		writeD(_origin.z);
	}
}