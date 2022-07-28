package l2mv.gameserver.network.serverpackets;

public class AskJoinPledge extends L2GameServerPacket
{
	private int _requestorId;
	private String _pledgeName;

	public AskJoinPledge(int requestorId, String pledgeName)
	{
		this._requestorId = requestorId;
		this._pledgeName = pledgeName;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x2c);
		this.writeD(this._requestorId);
		this.writeS(this._pledgeName);
	}
}