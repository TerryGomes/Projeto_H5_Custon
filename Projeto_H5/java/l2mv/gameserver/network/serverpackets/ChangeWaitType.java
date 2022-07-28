package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Creature;

/**
 * 0000: 3f 2a 89 00 4c 01 00 00 00 0a 15 00 00 66 fe 00    ?*..L........f..
 * 0010: 00 7c f1 ff ff                                     .|...
 *
 * format   dd ddd
 */
public class ChangeWaitType extends L2GameServerPacket
{
	private int _objectId;
	private int _moveType;
	private int _x, _y, _z;

	public static final int WT_SITTING = 0;
	public static final int WT_STANDING = 1;
	public static final int WT_START_FAKEDEATH = 2;
	public static final int WT_STOP_FAKEDEATH = 3;

	public ChangeWaitType(Creature cha, int newMoveType)
	{
		this._objectId = cha.getObjectId();
		this._moveType = newMoveType;
		this._x = cha.getX();
		this._y = cha.getY();
		this._z = cha.getZ();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x29);
		this.writeD(this._objectId);
		this.writeD(this._moveType);
		this.writeD(this._x);
		this.writeD(this._y);
		this.writeD(this._z);
	}
}