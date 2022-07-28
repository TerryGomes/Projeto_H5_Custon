package l2mv.gameserver.network.clientpackets;

public class RequestCreatePledge extends L2GameClientPacket
{
	// Format: cS
	private String _pledgename;

	@Override
	protected void readImpl()
	{
		this._pledgename = this.readS(64);
	}

	@Override
	protected void runImpl()
	{
		// TODO not implemented
	}
}