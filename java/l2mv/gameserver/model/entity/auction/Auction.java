package l2mv.gameserver.model.entity.auction;

import l2mv.gameserver.model.items.ItemInstance;

public class Auction
{
	private int _auctionId;
	private int _sellerObjectId;
	private String _sellerName;
	private ItemInstance _item;
	private long _countToSell;
	private long _pricePerItem;
	private AuctionItemTypes _itemType;
	private boolean _privateStore;

	public Auction(int id, int sellerObjectId, String sellerName, ItemInstance item, long pricePerItem, long countToSell, AuctionItemTypes itemType, boolean privateStore)
	{
		_auctionId = id;
		_sellerObjectId = sellerObjectId;
		_sellerName = sellerName;
		_item = item;
		_pricePerItem = pricePerItem;
		_countToSell = countToSell;
		_itemType = itemType;
		_privateStore = privateStore;
	}

	public int getAuctionId()
	{
		return _auctionId;
	}

	public int getSellerObjectId()
	{
		return _sellerObjectId;
	}

	public String getSellerName()
	{
		return _sellerName;
	}

	public ItemInstance getItem()
	{
		return _item;
	}

	public void setCount(long count)
	{
		_countToSell = count;
	}

	public long getCountToSell()
	{
		return _countToSell;
	}

	public long getPricePerItem()
	{
		return _pricePerItem;
	}

	public AuctionItemTypes getItemType()
	{
		return _itemType;
	}

	public boolean isPrivateStore()
	{
		return _privateStore;
	}
}
