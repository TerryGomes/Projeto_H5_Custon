package l2f.gameserver.network.clientpackets;

import l2f.gameserver.data.BoatHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.boat.Boat;
import l2f.gameserver.utils.Location;

public class RequestGetOffVehicle extends L2GameClientPacket
{
	// Format: cdddd
	private int _objectId;
	private Location _location = new Location();

	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_location.x = readD();
		_location.y = readD();
		_location.z = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		Boat boat = BoatHolder.getInstance().getBoat(_objectId);
		if (boat == null || boat.isMoving)
		{
			player.sendActionFailed();
			return;
		}

		boat.oustPlayer(player, _location, false);
	}
}