package l2mv.gameserver.network.clientpackets;

public class RequestChangeBookMarkSlot extends L2GameClientPacket
{
	private int slot_old, slot_new;

	@Override
	protected void readImpl()
	{
		this.slot_old = this.readD();
		this.slot_new = this.readD();
	}

	@Override
	protected void runImpl()
	{
		// TODO not implemented
	}
}