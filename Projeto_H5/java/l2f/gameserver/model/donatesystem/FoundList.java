package l2f.gameserver.model.donatesystem;

import java.util.List;

public class FoundList
{
	private final int _id;
	private final long _count;
	private final List<DonateItem> _items;

	public FoundList(int id, long count, List<DonateItem> items)
	{
		_id = id;
		_count = count;
		_items = items;
	}

	public int getId()
	{
		return _id;
	}

	public long getCount()
	{
		return _count;
	}

	public List<DonateItem> getList()
	{
		return _items;
	}
}
