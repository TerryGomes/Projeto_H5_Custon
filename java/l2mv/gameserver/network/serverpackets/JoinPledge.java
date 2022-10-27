package l2mv.gameserver.network.serverpackets;

public class JoinPledge extends L2GameServerPacket
{
	private int _pledgeId;

	public JoinPledge(int pledgeId)
	{
		this._pledgeId = pledgeId;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x2d);

		this.writeD(this._pledgeId);
	}
}