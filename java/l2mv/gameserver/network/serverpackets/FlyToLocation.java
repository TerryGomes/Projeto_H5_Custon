package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.utils.Location;

public class FlyToLocation extends L2GameServerPacket
{
	private int _chaObjId;
	private final FlyType _type;
	private Location _loc;
	private Location _destLoc;

	public enum FlyType
	{
		THROW_UP, THROW_HORIZONTAL, DUMMY, CHARGE, NONE
	}

	public FlyToLocation(Creature cha, Location destLoc, FlyType type)
	{
		this._destLoc = destLoc;
		this._type = type;
		this._chaObjId = cha.getObjectId();
		this._loc = cha.getLoc();
	}

	@Override
	protected void writeImpl()
	{
		this.writeC(0xd4);
		this.writeD(this._chaObjId);
		this.writeD(this._destLoc.x);
		this.writeD(this._destLoc.y);
		this.writeD(this._destLoc.z);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
		this.writeD(this._type.ordinal());
	}
}