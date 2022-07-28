package l2mv.gameserver.network.clientpackets;

public class RequestTimeCheck extends L2GameClientPacket
{
	private int unk, unk2;

	/**
	 * format: dd
	 */
	@Override
	protected void readImpl()
	{
		this.unk = this.readD();
		this.unk2 = this.readD();
	}

	@Override
	protected void runImpl()
	{
		// TODO not implemented
	}
}