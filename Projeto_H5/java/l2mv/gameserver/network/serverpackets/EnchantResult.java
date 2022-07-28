package l2mv.gameserver.network.serverpackets;

public class EnchantResult extends L2GameServerPacket
{
	private final int _resultId, _crystalId;
	private final long _count;

	public static final EnchantResult SUCESS = new EnchantResult(0, 0, 0); // вещь заточилась
	// public static final EnchantResult FAILED = new EnchantResult(1, 0, 0); // вещь разбилась, требует указания получившихся кристаллов, в статичном виде не используется
	public static final EnchantResult CANCEL = new EnchantResult(2, 0, 0); // заточка невозможна
	public static final EnchantResult BLESSED_FAILED = new EnchantResult(3, 0, 0); // заточка не удалась, уровень заточки сброшен на 0
	public static final EnchantResult FAILED_NO_CRYSTALS = new EnchantResult(4, 0, 0); // вещь разбилась, но кристаллов не получилось (видимо для эвента, сейчас использовать невозможно,
																						// там заглушка)
	public static final EnchantResult ANCIENT_FAILED = new EnchantResult(5, 0, 0); // заточка не удалась, уровень заточки не изменен (для Ancient Enchant Crystal из итем молла)

	public EnchantResult(int resultId, int crystalId, long count)
	{
		this._resultId = resultId;
		this._crystalId = crystalId;
		this._count = count;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x87);
		this.writeD(this._resultId);
		this.writeD(this._crystalId); // item id кристаллов
		this.writeQ(this._count); // количество кристаллов
	}
}