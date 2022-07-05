package l2f.gameserver.network.serverpackets;

public class ExPutEnchantSupportItemResult extends L2GameServerPacket
{
	private int _result;

	public ExPutEnchantSupportItemResult(int result)
	{
		_result = result;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0x82);
		writeD(_result);
	}
}