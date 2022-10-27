package l2mv.gameserver.network.clientpackets;

public class RequestPrivateStoreList extends L2GameClientPacket
{
	private int unk;

	/**
	 * format: d
	 */
	@Override
	protected void readImpl()
	{
		this.unk = this.readD();
	}

	@Override
	protected void runImpl()
	{
		// TODO not implemented
	}
}