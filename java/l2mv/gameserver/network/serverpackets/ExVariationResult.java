package l2mv.gameserver.network.serverpackets;

public class ExVariationResult extends L2GameServerPacket
{
	private int _stat12;
	private int _stat34;
	private int _unk3;

	public ExVariationResult(int unk1, int unk2, int unk3)
	{
		this._stat12 = unk1;
		this._stat34 = unk2;
		this._unk3 = unk3;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x56);
		this.writeD(this._stat12);
		this.writeD(this._stat34);
		this.writeD(this._unk3);
	}
}