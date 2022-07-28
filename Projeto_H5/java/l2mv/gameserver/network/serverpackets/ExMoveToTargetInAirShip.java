package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.boat.Boat;
import l2mv.gameserver.utils.Location;

public class ExMoveToTargetInAirShip extends L2GameServerPacket
{
	private int char_id, boat_id, target_id, _dist;
	private Location _loc;

	public ExMoveToTargetInAirShip(Player cha, Boat boat, int targetId, int dist, Location origin)
	{
		this.char_id = cha.getObjectId();
		this.boat_id = boat.getObjectId();
		this.target_id = targetId;
		this._dist = dist;
		this._loc = origin;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x71);

		this.writeD(this.char_id); // ID:%d
		this.writeD(this.target_id); // TargetID:%d
		this.writeD(this._dist); // Dist:%d
		this.writeD(this._loc.y); // OriginX:%d
		this.writeD(this._loc.z); // OriginY:%d
		this.writeD(this._loc.h); // OriginZ:%d
		this.writeD(this.boat_id); // AirShipID:%d
	}
}