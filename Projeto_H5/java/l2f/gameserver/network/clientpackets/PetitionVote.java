package l2f.gameserver.network.clientpackets;

/**
 * format: ddS
 */
public class PetitionVote extends L2GameClientPacket
{
	private int _type, _unk1;
	private String _petitionText;

	@Override
	protected void runImpl()
	{
	}

	@Override
	protected void readImpl()
	{
		_type = readD();
		_unk1 = readD();
		_petitionText = readS(4096);
	}
}