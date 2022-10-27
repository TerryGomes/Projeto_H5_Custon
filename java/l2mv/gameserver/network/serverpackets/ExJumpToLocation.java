package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.utils.Location;

public class ExJumpToLocation extends L2GameServerPacket
{
	private int _objectId;
	private Location _current;
	private Location _destination;

	public ExJumpToLocation(int objectId, Location from, Location to)
	{
		this._objectId = objectId;
		this._current = from;
		this._destination = to;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x88);

		this.writeD(this._objectId);

		this.writeD(this._destination.x);
		this.writeD(this._destination.y);
		this.writeD(this._destination.z);

		this.writeD(this._current.x);
		this.writeD(this._current.y);
		this.writeD(this._current.z);
	}
}