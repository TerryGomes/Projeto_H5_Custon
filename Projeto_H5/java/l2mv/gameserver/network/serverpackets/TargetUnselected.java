package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.utils.Location;

/**
 * format  ddddd
 */
public class TargetUnselected extends L2GameServerPacket
{
	private int _targetId;
	private Location _loc;

	public TargetUnselected(GameObject obj)
	{
		_targetId = obj.getObjectId();
		_loc = obj.getLoc();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x24);
		writeD(_targetId);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(0x00);
	}
}