package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Creature;

public class MoveToPawn extends L2GameServerPacket
{
	private int _chaId, _targetId, _distance;
	private int _x, _y, _z, _tx, _ty, _tz;

	public MoveToPawn(Creature cha, Creature target, int distance)
	{
		_chaId = cha.getObjectId();
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

		writeD(_chaId);
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