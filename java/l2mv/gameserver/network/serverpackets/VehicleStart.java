package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.entity.boat.Boat;

public class VehicleStart extends L2GameServerPacket
{
	private int _objectId, _state;

	public VehicleStart(Boat boat)
	{
		this._objectId = boat.getObjectId();
		this._state = boat.getRunState();
	}

	@Override
	protected void writeImpl()
	{
		this.writeC(0xC0);
		this.writeD(this._objectId);
		this.writeD(this._state);
	}
}