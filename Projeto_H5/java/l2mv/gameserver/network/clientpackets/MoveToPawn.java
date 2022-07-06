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
		_charObjId = cha.getObjectId();
		_targetId = target.getObjectId();
		_distance = distance;
		_x = cha.getX();
		_y = cha.getY();
		_z = cha.getZ();
		_tx = target.getX();
		_ty = target.getY();
		_tz = target.getZ();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x72);

		writeD(_charObjId);
		writeD(_targetId);
		writeD(_distance);

		writeD(_x);
		writeD(_y);
		writeD(_z);
		writeD(_tx);
		writeD(_ty);
		writeD(_tz);
	}
}
