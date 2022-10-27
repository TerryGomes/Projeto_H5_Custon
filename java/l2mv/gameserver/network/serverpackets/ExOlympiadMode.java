package l2mv.gameserver.network.serverpackets;

public class ExOlympiadMode extends L2GameServerPacket
{
	// chc
	private int _mode;

	public ExOlympiadMode(int mode)
	{
		this._mode = mode;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x7c);

		this.writeC(this._mode);
	}
}