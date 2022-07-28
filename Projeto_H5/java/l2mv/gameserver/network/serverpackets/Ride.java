package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.utils.Location;

public class Ride extends L2GameServerPacket
{
	private int _mountType, _id, _rideClassID;
	private Location _loc;

	public Ride(Player cha)
	{
		this._id = cha.getObjectId();
		this._mountType = cha.getMountType();
		this._rideClassID = cha.getMountNpcId() + 1000000;
		this._loc = cha.getLoc();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x8c);
		this.writeD(this._id);
		this.writeD(this._mountType == 0 ? 0 : 1);
		this.writeD(this._mountType);
		this.writeD(this._rideClassID);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
	}
}