package l2f.gameserver.network.serverpackets;

public class PetDelete extends L2GameServerPacket
{
	private int _petId;
	private int _petnum;

	public PetDelete(int petId, int petnum)
	{
		_petId = petId;
		_petnum = petnum;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xb7);
		writeD(_petId);// dont really know what these two are since i never needed them
		writeD(_petnum);
	}
}