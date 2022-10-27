package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.network.serverpackets.L2GameServerPacket;

/**
 * @author Mikheil
 */
public class MoveToPawn extends L2GameServerPacket
{
	private final int _charObjId;
	private final int _targetId;
	private final int _distance;
	private final int _x, _y, _z, _tx, _ty, _tz;

	public MoveToPawn(GameObject cha, GameObject target, int distance)
	{
		this._charObjId = cha.getObjectId();
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

		this.writeD(this._charObjId);
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
