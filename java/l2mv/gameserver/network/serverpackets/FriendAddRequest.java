package l2mv.gameserver.network.serverpackets;

/**
 * format: cS
 */
public class FriendAddRequest extends L2GameServerPacket
{
	private String _requestorName;

	public FriendAddRequest(String requestorName)
	{
		this._requestorName = requestorName;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x83);
		this.writeS(this._requestorName);
	}
}