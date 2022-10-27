package l2mv.gameserver.network.serverpackets;

public class ExAskModifyPartyLooting extends L2GameServerPacket
{
	private String _requestor;
	private int _mode;

	public ExAskModifyPartyLooting(String name, int mode)
	{
		this._requestor = name;
		this._mode = mode;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xBF);
		this.writeS(this._requestor);
		this.writeD(this._mode);
	}
}
