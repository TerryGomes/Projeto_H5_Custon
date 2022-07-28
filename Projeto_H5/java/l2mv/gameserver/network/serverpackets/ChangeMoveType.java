package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Creature;

/**
 * 0000: 3e 2a 89 00 4c 01 00 00 00                         .|...
 *
 * format   dd
 */
public class ChangeMoveType extends L2GameServerPacket
{
	public static int WALK = 0;
	public static int RUN = 1;

	private int _chaId;
	private boolean _running;

	public ChangeMoveType(Creature cha)
	{
		this._chaId = cha.getObjectId();
		this._running = cha.isRunning();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x28);
		this.writeD(this._chaId);
		this.writeD(this._running ? 1 : 0);
		this.writeD(0); // c2
	}
}