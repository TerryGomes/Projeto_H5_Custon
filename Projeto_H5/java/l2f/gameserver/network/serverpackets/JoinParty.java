package l2f.gameserver.network.serverpackets;

public class JoinParty extends L2GameServerPacket
{
	public static final L2GameServerPacket SUCCESS = new JoinParty(1);
	public static final L2GameServerPacket FAIL = new JoinParty(0);

	private int _response;

	public JoinParty(int response)
	{
		_response = response;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x3A);
		writeD(_response);
	}
}