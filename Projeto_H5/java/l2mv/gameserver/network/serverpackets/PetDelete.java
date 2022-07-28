package l2mv.gameserver.network.serverpackets;

public class PetDelete extends L2GameServerPacket
{
	private int _petId;
	private int _petnum;

	public PetDelete(int petId, int petnum)
	{
		this._petId = petId;
		this._petnum = petnum;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xb7);
		this.writeD(this._petId);// dont really know what these two are since i never needed them
		this.writeD(this._petnum);
	}
}