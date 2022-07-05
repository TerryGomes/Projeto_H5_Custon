package l2f.gameserver.network.serverpackets;

/**
 * format: cS
 */
public class FriendAddRequest extends L2GameServerPacket
{
	private String _requestorName;

	public FriendAddRequest(String requestorName)
	{
		_requestorName = requestorName;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x83);
		writeS(_requestorName);
	}
}