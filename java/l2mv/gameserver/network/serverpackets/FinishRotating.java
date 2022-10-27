package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Creature;

public class FinishRotating extends L2GameServerPacket
{
	private int _charId, _degree, _speed;

	public FinishRotating(Creature player, int degree, int speed)
	{
		this._charId = player.getObjectId();
		this._degree = degree;
		this._speed = speed;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x61);
		this.writeD(this._charId);
		this.writeD(this._degree);
		this.writeD(this._speed);
		this.writeD(0x00); // ??
	}
}