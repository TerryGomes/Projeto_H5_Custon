package l2mv.gameserver.network.serverpackets;

public class ExPutEnchantTargetItemResult extends L2GameServerPacket
{
	public static final L2GameServerPacket FAIL = new ExPutEnchantTargetItemResult(0);
	public static final L2GameServerPacket SUCCESS = new ExPutEnchantTargetItemResult(1);

	private int _result;

	public ExPutEnchantTargetItemResult(int result)
	{
		this._result = result;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x81);
		this.writeD(this._result);
	}
}