package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Creature;

public class StartRotating extends L2GameServerPacket
{
	private int _charId, _degree, _side, _speed;

	public StartRotating(Creature cha, int degree, int side, int speed)
	{
		this._charId = cha.getObjectId();
		this._degree = degree;
		this._side = side;
		this._speed = speed;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x7a);
		this.writeD(this._charId);
		this.writeD(this._degree);
		this.writeD(this._side);
		this.writeD(this._speed);
	}
}