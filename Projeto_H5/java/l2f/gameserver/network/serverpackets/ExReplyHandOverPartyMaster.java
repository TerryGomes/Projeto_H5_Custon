package l2f.gameserver.network.serverpackets;

/**
 * @author VISTALL
 * @date 20:34/01.09.2011
 */
public class ExReplyHandOverPartyMaster extends L2GameServerPacket
{
	public static final L2GameServerPacket TRUE = new ExReplyHandOverPartyMaster(true);
	public static final L2GameServerPacket FALSE = new ExReplyHandOverPartyMaster(false);
	private final boolean _isLeader;

	public ExReplyHandOverPartyMaster(boolean leader)
	{
		_isLeader = leader;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0xC4);
		writeD(_isLeader);
	}
}
