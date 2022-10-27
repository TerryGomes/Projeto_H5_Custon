package l2mv.gameserver.network.serverpackets;

/**
 * @author SYS
 */
public class ExAttributeEnchantResult extends L2GameServerPacket
{
	private int _result;

	public ExAttributeEnchantResult(int unknown)
	{
		this._result = unknown;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x61);
		this.writeD(this._result);
	}
}