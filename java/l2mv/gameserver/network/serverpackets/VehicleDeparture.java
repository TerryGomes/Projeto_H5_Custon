package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.entity.boat.Boat;
import l2mv.gameserver.utils.Location;

public class VehicleDeparture extends L2GameServerPacket
{
	private int _moveSpeed, _rotationSpeed;
	private int _boatObjId;
	private Location _loc;

	public VehicleDeparture(Boat boat)
	{
		this._boatObjId = boat.getObjectId();
		this._moveSpeed = boat.getMoveSpeed();
		this._rotationSpeed = boat.getRotationSpeed();
		this._loc = boat.getDestination();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x6c);
		this.writeD(this._boatObjId);
		this.writeD(this._moveSpeed);
		this.writeD(this._rotationSpeed);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
	}
}