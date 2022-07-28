package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.boat.Boat;
import l2mv.gameserver.utils.Location;

public class ExMoveToLocationInAirShip extends L2GameServerPacket
{
	private int char_id, boat_id;
	private Location _origin, _destination;

	public ExMoveToLocationInAirShip(Player cha, Boat boat, Location origin, Location destination)
	{
		this.char_id = cha.getObjectId();
		this.boat_id = boat.getObjectId();
		this._origin = origin;
		this._destination = destination;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x6D);
		this.writeD(this.char_id);
		this.writeD(this.boat_id);

		this.writeD(this._destination.x);
		this.writeD(this._destination.y);
		this.writeD(this._destination.z);
		this.writeD(this._origin.x);
		this.writeD(this._origin.y);
		this.writeD(this._origin.z);
	}
}