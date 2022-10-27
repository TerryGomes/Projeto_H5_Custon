package l2mv.gameserver.network.serverpackets;

import java.util.Collections;
import java.util.List;

import l2mv.gameserver.model.entity.boat.ClanAirShip;
import l2mv.gameserver.model.entity.events.objects.BoatPoint;

public class ExAirShipTeleportList extends L2GameServerPacket
{
	private int _fuel;
	private List<BoatPoint> _airports = Collections.emptyList();

	public ExAirShipTeleportList(ClanAirShip ship)
	{
		this._fuel = ship.getCurrentFuel();
		this._airports = ship.getDock().getTeleportList();
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x9A);
		this.writeD(this._fuel); // current fuel
		this.writeD(this._airports.size());

		for (int i = 0; i < this._airports.size(); i++)
		{
			BoatPoint point = this._airports.get(i);
			this.writeD(i - 1); // AirportID
			this.writeD(point.getFuel()); // need fuel
			this.writeD(point.x); // Airport x
			this.writeD(point.y); // Airport y
			this.writeD(point.z); // Airport z
		}
	}
}