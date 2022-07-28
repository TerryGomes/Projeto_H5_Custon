package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.utils.Location;

public class ExStopMoveInAirShip extends L2GameServerPacket
{
	private int char_id, boat_id, char_heading;
	private Location _loc;

	public ExStopMoveInAirShip(Player cha)
	{
		this.char_id = cha.getObjectId();
		this.boat_id = cha.getBoat().getObjectId();
		this._loc = cha.getInBoatPosition();
		this.char_heading = cha.getHeading();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x6E);

		this.writeD(this.char_id);
		this.writeD(this.boat_id);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
		this.writeD(this.char_heading);
	}
}