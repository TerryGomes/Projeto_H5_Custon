package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.boat.AirShip;
import l2mv.gameserver.model.entity.boat.ClanAirShip;
import l2mv.gameserver.utils.Location;

public class ExAirShipInfo extends L2GameServerPacket
{
	private int _objId, _speed1, _speed2, _fuel, _maxFuel, _driverObjId, _controlKey;
	private Location _loc;

	public ExAirShipInfo(AirShip ship)
	{
		this._objId = ship.getObjectId();
		this._loc = ship.getLoc();
		this._speed1 = ship.getRunSpeed();
		this._speed2 = ship.getRotationSpeed();
		if (ship.isClanAirShip())
		{
			this._fuel = ((ClanAirShip) ship).getCurrentFuel();
			this._maxFuel = ((ClanAirShip) ship).getMaxFuel();
			Player driver = ((ClanAirShip) ship).getDriver();
			this._driverObjId = driver == null ? 0 : driver.getObjectId();
			this._controlKey = ((ClanAirShip) ship).getControlKey().getObjectId();
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x60);

		this.writeD(this._objId);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
		this.writeD(this._loc.h);
		this.writeD(this._driverObjId); // object id of player who control ship
		this.writeD(this._speed1);
		this.writeD(this._speed2);
		this.writeD(this._controlKey);

		if (this._controlKey != 0)
		{
			this.writeD(0x16e); // Controller X
			this.writeD(0x00); // Controller Y
			this.writeD(0x6b); // Controller Z
			this.writeD(0x15c); // Captain X
			this.writeD(0x00); // Captain Y
			this.writeD(0x69); // Captain Z
		}
		else
		{
			this.writeD(0x00);
			this.writeD(0x00);
			this.writeD(0x00);
			this.writeD(0x00);
			this.writeD(0x00);
			this.writeD(0x00);
		}

		this.writeD(this._fuel); // current fuel
		this.writeD(this._maxFuel); // max fuel
	}
}