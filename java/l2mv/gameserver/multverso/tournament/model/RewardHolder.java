package l2mv.gameserver.multverso.tournament.model;

public class RewardHolder
{
	final int _itemId;
	final long _count;

	public RewardHolder(int id, long count)
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
