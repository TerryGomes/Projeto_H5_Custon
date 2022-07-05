package l2f.gameserver.network.serverpackets;

import l2f.gameserver.utils.Location;

/**
 * format   dddddd
 */
public class TargetSelected extends L2GameServerPacket
{
	private int _objectId;
	private int _targetId;
	private Location _loc;

	public TargetSelected(int objectId, int targetId, Location loc)
	{
		_objectId = objectId;
		_targetId = targetId;
		_loc = loc;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x23);
		writeD(_objectId);
		writeD(_targetId);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(0x00);
	}
}