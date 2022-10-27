package l2mv.gameserver.network.clientpackets;

public class RequestExBR_EventRankerList extends L2GameClientPacket
{
	private int unk, unk2, unk3;

	/**
	 * format: ddd
	 */
	@Override
	protected void readImpl()
	{
		this.unk = this.readD();
		this.unk2 = this.readD();
		this.unk3 = this.readD();
	}

	@Override
	protected void runImpl()
	{
		// TODO not implemented
	}

}