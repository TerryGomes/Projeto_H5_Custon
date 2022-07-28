package l2mv.gameserver.network.clientpackets;

/**
 * format: chS
 */
public class RequestPCCafeCouponUse extends L2GameClientPacket
{
	// format: (ch)S
	private String _unknown;

	@Override
	protected void readImpl()
	{
		this._unknown = this.readS();
	}

	@Override
	protected void runImpl()
	{
		// TODO not implemented
	}
}