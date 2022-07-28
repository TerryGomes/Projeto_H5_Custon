package l2mv.gameserver.network.clientpackets;

public class RequestExChangeName extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		int unk1 = this.readD();
		String name = this.readS();
		int unk2 = this.readD();
	}

	@Override
	protected void runImpl()
	{
		// TODO not implemented
	}
}