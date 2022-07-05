package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Creature;

public class StartRotating extends L2GameServerPacket
{
	private int _charId, _degree, _side, _speed;

	public StartRotating(Creature cha, int degree, int side, int speed)
	{
		_charId = cha.getObjectId();
		_degree = degree;
		_side = side;
		_speed = speed;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x7a);
		writeD(_charId);
		writeD(_degree);
		writeD(_side);
		writeD(_speed);
	}
}