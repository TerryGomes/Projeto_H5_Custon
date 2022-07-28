package l2mv.gameserver.network.serverpackets;

public class ExVariationCancelResult extends L2GameServerPacket
{
	private int _closeWindow;
	private int _unk1;

	public ExVariationCancelResult(int result)
	{
		this._closeWindow = 1;
		this._unk1 = result;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x58);
		this.writeD(this._unk1);
		this.writeD(this._closeWindow);
	}
}