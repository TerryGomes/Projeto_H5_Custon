package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.data.BoatHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.boat.Boat;
import l2mv.gameserver.utils.Location;

public class RequestGetOnVehicle extends L2GameClientPacket
{
	private int _objectId;
	private Location _loc = new Location();

	/**
	 * packet type id 0x53
	 * format:      cdddd
	 */
	@Override
	protected void readImpl()
	{
		this._objectId = this.readD();
		this._loc.x = this.readD();
		this._loc.y = this.readD();
		this._loc.z = this.readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		Boat boat = BoatHolder.getInstance().getBoat(this._objectId);
		if (boat == null)
		{
			return;
		}

		player._stablePoint = boat.getCurrentWay().getReturnLoc();
		boat.addPlayer(player, this._loc);
	}
}