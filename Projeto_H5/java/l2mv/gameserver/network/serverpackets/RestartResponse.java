package l2mv.gameserver.network.serverpackets;

public class RestartResponse extends L2GameServerPacket
{
	public static final RestartResponse OK = new RestartResponse(1), FAIL = new RestartResponse(0);
	private String _message;
	private int _param;

	public RestartResponse(int param)
	{
		_message = "bye";
		_param = param;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x71);
		writeD(_param); // 01-ok
		writeS(_message);
	}
}