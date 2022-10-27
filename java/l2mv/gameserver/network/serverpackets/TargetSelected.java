package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.utils.Location;

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
		this._objectId = objectId;
		this._targetId = targetId;
		this._loc = loc;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x23);
		this.writeD(this._objectId);
		this.writeD(this._targetId);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
		this.writeD(0x00);
	}
}