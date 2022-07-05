package l2f.gameserver.network.serverpackets;

public class SendTradeDone extends L2GameServerPacket
{
	public static final L2GameServerPacket SUCCESS = new SendTradeDone(1);
	public static final L2GameServerPacket FAIL = new SendTradeDone(0);

	private int _response;

	private SendTradeDone(int num)
	{
		_response = num;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x1c);
		writeD(_response);
	}
}