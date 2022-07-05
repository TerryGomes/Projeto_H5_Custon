package l2f.gameserver.network.serverpackets;

/**
 * sample
 * <p>
 * 7d
 * c1 b2 e0 4a
 * 00 00 00 00
 * <p>
 *
 * format
 * cdd
 */
public class AskJoinAlliance extends L2GameServerPacket
{
	private String _requestorName;
	private String _requestorAllyName;
	private int _requestorId;

	public AskJoinAlliance(int requestorId, String requestorName, String requestorAllyName)
	{
		_requestorName = requestorName;
		_requestorAllyName = requestorAllyName;
		_requestorId = requestorId;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xbb);
		writeD(_requestorId);
		writeS(_requestorName);
		writeS("");
		writeS(_requestorAllyName);
	}
}