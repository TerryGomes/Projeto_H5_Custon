package l2mv.gameserver.network.clientpackets;

public class RequestSEKCustom extends L2GameClientPacket
{
	private int SlotNum, Direction;

	/**
	 * format: dd
	 */
	@Override
	protected void readImpl()
	{
		this.SlotNum = this.readD();
		this.Direction = this.readD();
	}

	@Override
	protected void runImpl()
	{
		// TODO not implemented
	}
}