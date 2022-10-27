package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Creature;

public class MoveToPawn extends L2GameServerPacket
{
	private int _chaId, _targetId, _distance;
	private int _x, _y, _z, _tx, _ty, _tz;

	public MoveToPawn(Creature cha, Creature target, int distance)
	{
		this._chaId = cha.getObjectId();
		this._targetId = target.getObjectId();
		this._distance = distance;
		this._x = cha.getX();
		this._y = cha.getY();
		this._z = cha.getZ();
		this._tx = target.getX();
		this._ty = target.getY();
		this._tz = target.getZ();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x72);

		this.writeD(this._chaId);
		this.writeD(this._targetId);
		this.writeD(this._distance);

		this.writeD(this._x);
		this.writeD(this._y);
		this.writeD(this._z);

		this.writeD(this._tx);
		this.writeD(this._ty);
		this.writeD(this._tz);
	}
}