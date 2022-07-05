package l2f.gameserver.fandc.tournament.model;

public class ItemHolder
{
	private final int _itemId;
	private final long _count;

	public ItemHolder(int id, long count)
	{
		_itemId = id;
		_count = count;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public long getCount()
	{
		return _count;
	}
}
