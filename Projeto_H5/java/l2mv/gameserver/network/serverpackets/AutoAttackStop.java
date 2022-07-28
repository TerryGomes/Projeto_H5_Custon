package l2mv.gameserver.network.serverpackets;

public class AutoAttackStop extends L2GameServerPacket
{
	// dh
	private int _targetId;

	/**
	 * @param _characters
	 */
	public AutoAttackStop(int targetId)
	{
		this._targetId = targetId;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x26);
		this.writeD(this._targetId);
	}
}