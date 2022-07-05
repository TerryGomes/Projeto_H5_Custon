package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.entity.boat.Boat;

public class VehicleStart extends L2GameServerPacket
{
	private int _objectId, _state;

	public VehicleStart(Boat boat)
	{
		_objectId = boat.getObjectId();
		_state = boat.getRunState();
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xC0);
		writeD(_objectId);
		writeD(_state);
	}
}