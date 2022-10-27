package l2mv.gameserver.model.items;

public final class TradeItem extends ItemInfo
{
	private long _price;
	private long _referencePrice;
	private long _currentValue;
	private int _lastRechargeTime;
	private int _rechargeTime;
	private int _auctionId;

	public TradeItem()
	{
		super();
	}

	public TradeItem(ItemInstance item)
	{
		super(item);
		setReferencePrice(item.getReferencePrice());
	}

	public void setOwnersPrice(long price)
	{
		_price = price;
	}

	public long getOwnersPrice()
	{
		return _price;
	}

	public void setReferencePrice(long price)
	{
		_referencePrice = price;
	}

	public long getReferencePrice()
	{
		return _referencePrice;
	}

	public long getStorePrice()
	{
		return getReferencePrice() / 2;
	}

	public void setCurrentValue(long value)
	{
		_currentValue = value;
	}

	public long getCurrentValue()
	{
		return _currentValue;
	}

	/**
	 * Устанавливает время респауна предмета, используется в NPC магазинах с ограниченным количеством.
	 * @param rechargeTime : unixtime в минутах
	 */
	public void setRechargeTime(int rechargeTime)
	{
		_rechargeTime = rechargeTime;
	}

	/**
	 * Возвращает время респауна предмета, используется в NPC магазинах с ограниченным количеством.
	 * @return unixtime в минутах
	 */
	public int getRechargeTime()
	{
		return _rechargeTime;
	}

	/**
	 * Возвращает ограничен ли этот предмет в количестве, используется в NPC магазинах с ограниченным количеством.
	 * @return true, если ограничен
	 */
	public boolean isCountLimited()
	{
		return getCount() > 0;
	}

	/**
	 * Устанавливает время последнего респауна предмета, используется в NPC магазинах с ограниченным количеством.
	 * @param lastRechargeTime : unixtime в минутах
	 */
	public void setLastRechargeTime(int lastRechargeTime)
	{
		_lastRechargeTime = lastRechargeTime;
	}

	/**
	 * Возвращает время последнего респауна предмета, используется в NPC магазинах с ограниченным количеством.
	 * @return unixtime в минутах
	 */
	public int getLastRechargeTime()
	{
		return _lastRechargeTime;
	}

	public void setAuctionId(int id)
	{
		_auctionId = id;
	}

	public int getAuctionId()
	{
		return _auctionId;
	}
}