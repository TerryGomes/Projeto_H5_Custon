package l2mv.gameserver.network.serverpackets;

public class RestartResponse extends L2GameServerPacket
{
	public static final RestartResponse OK = new RestartResponse(1), FAIL = new RestartResponse(0);
	private String _message;
	private int _param;

	public RestartResponse(int param)
	{
		this._message = "bye";
		this._param = param;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x71);
		this.writeD(this._param); // 01-ok
		this.writeS(this._message);
	}
}