package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.entity.boat.Boat;
import l2mv.gameserver.utils.Location;

public class ExMoveToLocationAirShip extends L2GameServerPacket
{
	private int _objectId;
	private Location _origin, _destination;

	public ExMoveToLocationAirShip(Boat boat)
	{
		this._objectId = boat.getObjectId();
		this._origin = boat.getLoc();
		this._destination = boat.getDestination();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x65);
		this.writeD(this._objectId);

		this.writeD(this._destination.x);
		this.writeD(this._destination.y);
		this.writeD(this._destination.z);
		this.writeD(this._origin.x);
		this.writeD(this._origin.y);
		this.writeD(this._origin.z);
	}
}