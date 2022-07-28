package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.boat.Boat;
import l2mv.gameserver.utils.Location;

public class MoveToLocationInVehicle extends L2GameServerPacket
{
	private int _playerObjectId, _boatObjectId;
	private Location _origin, _destination;

	public MoveToLocationInVehicle(Player cha, Boat boat, Location origin, Location destination)
	{
		this._playerObjectId = cha.getObjectId();
		this._boatObjectId = boat.getObjectId();
		this._origin = origin;
		this._destination = destination;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x7e);
		this.writeD(this._playerObjectId);
		this.writeD(this._boatObjectId);
		this.writeD(this._destination.x);
		this.writeD(this._destination.y);
		this.writeD(this._destination.z);
		this.writeD(this._origin.x);
		this.writeD(this._origin.y);
		this.writeD(this._origin.z);
	}
}