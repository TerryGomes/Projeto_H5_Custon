package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Creature;

/**
 * format   ddddd
 */
public class StopMove extends L2GameServerPacket
{
	private final int _objectId;
	private final int _x;
	private final int _y;
	private final int _z;
	private final int _heading;

	public StopMove(Creature cha)
	{
		this._objectId = cha.getObjectId();
		this._x = cha.getX();
		this._y = cha.getY();
		this._z = cha.getZ();
		this._heading = cha.getHeading();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x47);
		this.writeD(this._objectId);
		this.writeD(this._x);
		this.writeD(this._y);
		this.writeD(this._z);
		this.writeD(this._heading);
	}
}