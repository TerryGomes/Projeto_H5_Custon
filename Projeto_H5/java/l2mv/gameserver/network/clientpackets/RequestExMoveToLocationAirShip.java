package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.boat.ClanAirShip;

/**
 * Format: d d|dd
 */
public class RequestExMoveToLocationAirShip extends L2GameClientPacket
{
	private int _moveType;
	private int _param1, _param2;

	@Override
	protected void readImpl()
	{
		_moveType = readD();
		switch (_moveType)
		{
		case 4: // AirShipTeleport
			_param1 = readD() + 1;
			break;
		case 0: // Free move
			_param1 = readD();
			_param2 = readD();
			break;
		case 2: // Up
			readD(); // ?
			readD(); // ?
			break;
		case 3: // Down
			readD(); // ?
			readD(); // ?
			break;
		}
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null || player.getBoat() == null || !player.getBoat().isClanAirShip())
		{
			return;
		}

		ClanAirShip airship = (ClanAirShip) player.getBoat();
		if (airship.getDriver() == player)
		{
			switch (_moveType)
			{
			case 4: // AirShipTeleport
				airship.addTeleportPoint(player, _param1);
				break;
			case 0: // Free move
				if (!airship.isCustomMove())
				{
					break;
				}
				airship.moveToLocation(airship.getLoc().setX(_param1).setY(_param2), 0, false);
				break;
			case 2: // Up
				if (!airship.isCustomMove())
				{
					break;
				}
				airship.moveToLocation(airship.getLoc().changeZ(100), 0, false);
				break;
			case 3: // Down
				if (!airship.isCustomMove())
				{
					break;
				}
				airship.moveToLocation(airship.getLoc().changeZ(-100), 0, false);
				break;
			}
		}
	}
}