package l2mv.gameserver.network.clientpackets;

public class RequestExChangeName extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		int unk1 = readD();
		String name = readS();
		int unk2 = readD();
	}

	@Override
	protected void runImpl()
	{
		// TODO not implemented
	}
}