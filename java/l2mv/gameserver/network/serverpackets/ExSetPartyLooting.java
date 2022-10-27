package l2mv.gameserver.network.serverpackets;

public class ExSetPartyLooting extends L2GameServerPacket
{
	private int _result;
	private int _mode;

	public ExSetPartyLooting(int result, int mode)
	{
		this._result = result;
		this._mode = mode;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xC0);
		this.writeD(this._result);
		this.writeD(this._mode);
	}
}
