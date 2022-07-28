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
		this._moveType = this.readD();
		switch (this._moveType)
		{
		case 4: // AirShipTeleport
			this._param1 = this.readD() + 1;
			break;
		case 0: // Free move
			this._param1 = this.readD();
			this._param2 = this.readD();
			break;
		case 2: // Up
			this.readD(); // ?
			this.readD(); // ?
			break;
		case 3: // Down
			this.readD(); // ?
			this.readD(); // ?
			break;
		}
	}

	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null || player.getBoat() == null || !player.getBoat().isClanAirShip())
		{
			return;
		}

		ClanAirShip airship = (ClanAirShip) player.getBoat();
		if (airship.getDriver() == player)
		{
			switch (this._moveType)
			{
			case 4: // AirShipTeleport
				airship.addTeleportPoint(player, this._param1);
				break;
			case 0: // Free move
				if (!airship.isCustomMove())
				{
					break;
				}
				airship.moveToLocation(airship.getLoc().setX(this._param1).setY(this._param2), 0, false);
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