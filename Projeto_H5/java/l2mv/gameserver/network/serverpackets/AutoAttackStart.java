package l2mv.gameserver.network.serverpackets;

public class AutoAttackStart extends L2GameServerPacket
{
	// dh
	private int _targetId;

	public AutoAttackStart(int targetId)
	{
		this._targetId = targetId;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x25);
		this.writeD(this._targetId);
	}
}