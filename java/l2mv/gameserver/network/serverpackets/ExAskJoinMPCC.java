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
		this._requestorName = requestorName;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x1a);
		this.writeS(this._requestorName); // лидер CC
		this.writeD(0x00);
	}
}