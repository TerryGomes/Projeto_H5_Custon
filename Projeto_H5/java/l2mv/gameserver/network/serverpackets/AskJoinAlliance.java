package l2mv.gameserver.network.serverpackets;

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
		this._requestorName = requestorName;
		this._requestorAllyName = requestorAllyName;
		this._requestorId = requestorId;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xbb);
		this.writeD(this._requestorId);
		this.writeS(this._requestorName);
		this.writeS("");
		this.writeS(this._requestorAllyName);
	}
}