package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.data.BoatHolder;
import l2mv.gameserver.geodata.GeoEngine;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.boat.Boat;
import l2mv.gameserver.utils.Location;

public class ValidatePosition extends L2GameClientPacket
{
	private final Location _loc = new Location();

	private int _boatObjectId;
	private Location _lastClientPosition;
	private Location _lastServerPosition;

	/**
	 * packet type id 0x48
	 * format:		cddddd
	 */
	@Override
	protected void readImpl()
	{
		this._loc.x = this.readD();
		this._loc.y = this.readD();
		this._loc.z = this.readD();
		this._loc.h = this.readD();
		this._boatObjectId = this.readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if ((activeChar == null) || activeChar.isTeleporting() || activeChar.isInObserverMode())
		{
			return;
		}

		this._lastClientPosition = activeChar.getLastClientPosition();
		this._lastServerPosition = activeChar.getLastServerPosition();

		if (this._lastClientPosition == null)
		{
			this._lastClientPosition = activeChar.getLoc();
		}
		if (this._lastServerPosition == null)
		{
			this._lastServerPosition = activeChar.getLoc();
		}

		if (activeChar.getX() == 0 && activeChar.getY() == 0 && activeChar.getZ() == 0)
		{
			this.correctPosition(activeChar);
			return;
		}

		if (activeChar.isInFlyingTransform())
		{
			// В летающей трансформе нельзя находиться на территории Aden
			if (this._loc.x > -166168)
			{
				activeChar.setTransformation(0);
				return;
			}

			// В летающей трансформе нельзя летать ниже, чем 0, и выше, чем 6000
			if (this._loc.z <= 0 || this._loc.z >= 6000)
			{
				activeChar.teleToLocation(activeChar.getLoc().setZ(Math.min(5950, Math.max(50, this._loc.z))));
				return;
			}
		}

		double diff = activeChar.getDistance(this._loc.x, this._loc.y);
		int dz = Math.abs(this._loc.z - activeChar.getZ());
		int h = this._lastServerPosition.z - activeChar.getZ();

		if (this._boatObjectId > 0)
		{
			Boat boat = BoatHolder.getInstance().getBoat(this._boatObjectId);
			if (boat != null && activeChar.getBoat() == boat)
			{
				activeChar.setHeading(this._loc.h);
				boat.validateLocationPacket(activeChar);
			}
			activeChar.setLastClientPosition(this._loc.setH(activeChar.getHeading()));
			activeChar.setLastServerPosition(activeChar.getLoc());
			return;
		}

		// Если мы уже падаем, то отключаем все валидейты
		if (activeChar.isFalling())
		{
			diff = 0;
			dz = 0;
			h = 0;
		}

		if (h >= 256) // Пока падаем, высоту не корректируем
		{
			activeChar.falling(h);
		}
		else if (dz >= (activeChar.isFlying() ? 1024 : 512))
		{
			if (activeChar.getIncorrectValidateCount() >= 3)
			{
				activeChar.teleToClosestTown();
			}
			else
			{
				activeChar.teleToLocation(activeChar.getLoc());
				activeChar.setIncorrectValidateCount(activeChar.getIncorrectValidateCount() + 1);
			}
		}
		else if (dz >= 256)
		{
			activeChar.validateLocation(0);
		}
		else if (this._loc.z < -30000 || this._loc.z > 30000)
		{
			if (activeChar.getIncorrectValidateCount() >= 3)
			{
				activeChar.teleToClosestTown();
			}
			else
			{
				this.correctPosition(activeChar);
				activeChar.setIncorrectValidateCount(activeChar.getIncorrectValidateCount() + 1);
			}
		}
		else if (diff > 1024)
		{
			if (activeChar.getIncorrectValidateCount() >= 3)
			{
				activeChar.teleToClosestTown();
			}
			else
			{
				activeChar.teleToLocation(activeChar.getLoc());
				activeChar.setIncorrectValidateCount(activeChar.getIncorrectValidateCount() + 1);
			}
		}
		else if (diff > 256)
		{
			// TODO реализовать NetPing и вычислять предельное отклонение исходя из пинга по формуле: 16 + (ping * activeChar.getMoveSpeed()) / 1000
			activeChar.validateLocation(1);
		}
		else
		{
			activeChar.setIncorrectValidateCount(0);
		}

		activeChar.setLastClientPosition(this._loc.setH(activeChar.getHeading()));
		activeChar.setLastServerPosition(activeChar.getLoc());
	}

	private void correctPosition(Player activeChar)
	{
		if (activeChar.isGM())
		{
			activeChar.sendMessage("Server loc: " + activeChar.getLoc());
			activeChar.sendMessage("Correcting position...");
		}
		if (this._lastServerPosition.x != 0 && this._lastServerPosition.y != 0 && this._lastServerPosition.z != 0)
		{
			if (GeoEngine.getNSWE(this._lastServerPosition.x, this._lastServerPosition.y, this._lastServerPosition.z, activeChar.getGeoIndex()) == GeoEngine.NSWE_ALL)
			{
				activeChar.teleToLocation(this._lastServerPosition);
			}
			else
			{
				activeChar.teleToClosestTown();
			}
		}
		else if ((this._lastClientPosition.x != 0 && this._lastClientPosition.y != 0 && this._lastClientPosition.z != 0) && (GeoEngine.getNSWE(this._lastClientPosition.x, this._lastClientPosition.y, this._lastClientPosition.z, activeChar.getGeoIndex()) == GeoEngine.NSWE_ALL))
		{
			activeChar.teleToLocation(this._lastClientPosition);
		}
		else
		{
			activeChar.teleToClosestTown();
		}
	}

	// Synerge - This packet can be used while the character is blocked
	@Override
	public boolean canBeUsedWhileBlocked()
	{
		return true;
	}
}