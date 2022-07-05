package l2f.gameserver.network.serverpackets;

import l2f.gameserver.utils.Location;

public class ExJumpToLocation extends L2GameServerPacket
{
	private int _objectId;
	private Location _current;
	private Location _destination;

	public ExJumpToLocation(int objectId, Location from, Location to)
	{
		_objectId = objectId;
		_current = from;
		_destination = to;
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0x88);

		writeD(_objectId);

		writeD(_destination.x);
		writeD(_destination.y);
		writeD(_destination.z);

		writeD(_current.x);
		writeD(_current.y);
		writeD(_current.z);
	}
}