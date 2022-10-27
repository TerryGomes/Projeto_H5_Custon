package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Creature;

public class SetupGauge extends L2GameServerPacket
{
	public static final int BLUE = 0;
	public static final int RED = 1;
	public static final int CYAN = 2;

	private int _charId;
	private int _dat1;
	private int _time;

	public SetupGauge(Creature character, int dat1, int time)
	{
		this._charId = character.getObjectId();
		this._dat1 = dat1;// color 0-blue 1-red 2-cyan 3-
		this._time = time;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x6b);
		this.writeD(this._charId);
		this.writeD(this._dat1);
		this.writeD(this._time);

		this.writeD(this._time); // c2
	}
}