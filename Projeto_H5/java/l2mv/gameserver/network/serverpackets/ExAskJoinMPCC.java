package l2mv.gameserver.network.serverpackets;

/**
 * Asks the player to join a Command Channel
 */
public class ExAskJoinMPCC extends L2GameServerPacket
{
	private String _requestorName;

	/**
	 * @param String Name of CCLeader
	 */
	public ExAskJoinMPCC(String requestorName)
	{
		_requestorName = requestorName;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0x1a);
		writeS(_requestorName); // лидер CC
		writeD(0x00);
	}
}