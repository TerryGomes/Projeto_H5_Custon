package l2f.gameserver.model;

public class PremiumItem
{
	private int _itemId;
	private long _count;
	private String _sender;

	public PremiumItem(int itemid, long count, String sender)
	{
		_itemId = itemid;
		_count = count;
		_sender = sender;
	}

	public void updateCount(long newcount)
	{
		_count = newcount;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public long getCount()
	{
		return _count;
	}

	public String getSender()
	{
		return _sender;
	}
}