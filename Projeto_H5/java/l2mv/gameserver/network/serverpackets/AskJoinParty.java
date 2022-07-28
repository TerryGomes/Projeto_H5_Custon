package l2mv.gameserver.network.serverpackets;

/**
 *
 * sample
 * <p>
 * 4b
 * c1 b2 e0 4a
 * 00 00 00 00
 * <p>
 *
 * format
 * cdd
 *
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class AskJoinParty extends L2GameServerPacket
{
	private String _requestorName;
	private int _itemDistribution;

	/**
	 * @param int objectId of the target
	 * @param int
	 */
	public AskJoinParty(String requestorName, int itemDistribution)
	{
		this._requestorName = requestorName;
		this._itemDistribution = itemDistribution;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x39);
		this.writeS(this._requestorName);
		this.writeD(this._itemDistribution);
	}
}