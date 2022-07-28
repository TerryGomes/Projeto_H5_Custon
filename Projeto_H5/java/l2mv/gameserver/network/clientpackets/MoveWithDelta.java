package l2mv.gameserver.network.clientpackets;

/**
 * Format: (c) ddd
 * d: dx
 * d: dy
 * d: dz
 */
public class MoveWithDelta extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _dx, _dy, _dz;

	@Override
	protected void readImpl()
	{
		this._dx = this.readD();
		this._dy = this.readD();
		this._dz = this.readD();
	}

	@Override
	protected void runImpl()
	{
		// TODO this
	}
}