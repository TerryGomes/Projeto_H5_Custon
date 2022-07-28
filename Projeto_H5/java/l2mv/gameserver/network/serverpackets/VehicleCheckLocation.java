package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.entity.boat.Boat;
import l2mv.gameserver.utils.Location;

public class VehicleCheckLocation extends L2GameServerPacket
{
	private int _boatObjectId;
	private Location _loc;

	public VehicleCheckLocation(Boat instance)
	{
		this._boatObjectId = instance.getObjectId();
		this._loc = instance.getLoc();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x6d);
		this.writeD(this._boatObjectId);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
		this.writeD(this._loc.h);
	}
}