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
		this._targetId = obj.getObjectId();
		this._loc = obj.getLoc();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x24);
		this.writeD(this._targetId);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
		this.writeD(0x00);
	}
}