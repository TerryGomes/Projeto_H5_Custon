package l2f.gameserver.network.serverpackets;

public class ExBR_BuyProduct extends L2GameServerPacket
{
	public static final int RESULT_OK = 1; // ok
	public static final int RESULT_NOT_ENOUGH_POINTS = -1;
	public static final int RESULT_WRONG_PRODUCT = -2; // also -5
	public static final int RESULT_INVENTORY_FULL = -4;
	public static final int RESULT_SALE_PERIOD_ENDED = -7; // also -8
	public static final int RESULT_WRONG_USER_STATE = -9; // also -11
	public static final int RESULT_WRONG_PRODUCT_ITEM = -10;

	private final int _result;

	public ExBR_BuyProduct(int result)
	{
		_result = result;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0xD8);

		writeD(_result);
	}
}