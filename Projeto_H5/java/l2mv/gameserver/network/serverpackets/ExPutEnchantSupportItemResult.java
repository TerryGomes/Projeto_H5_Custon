package l2mv.gameserver.network.serverpackets;

public class ExPutEnchantSupportItemResult extends L2GameServerPacket
{
	private int _result;

	public ExPutEnchantSupportItemResult(int result)
	{
		this._result = result;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x82);
		this.writeD(this._result);
	}
}