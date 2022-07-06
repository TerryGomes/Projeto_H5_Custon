package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.entity.boat.Boat;
import l2mv.gameserver.utils.Location;

public class VehicleCheckLocation extends L2GameServerPacket
{
	private int _boatObjectId;
	private Location _loc;

	public VehicleCheckLocation(Boat instance)
	{
		_boatObjectId = instance.getObjectId();
		_loc = instance.getLoc();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x6d);
		writeD(_boatObjectId);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(_loc.h);
	}
}