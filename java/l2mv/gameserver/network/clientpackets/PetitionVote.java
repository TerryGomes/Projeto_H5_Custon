package l2mv.gameserver.network.clientpackets;

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
		this._type = this.readD();
		this._unk1 = this.readD();
		this._petitionText = this.readS(4096);
	}
}