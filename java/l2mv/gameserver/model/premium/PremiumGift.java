package l2mv.gameserver.model.premium;

public class PremiumGift
{
	private final int _id;
	private final long _count;
	private final boolean _removable;

	public PremiumGift(int id, long count, boolean removable)
	{
		_id = id;
		_count = count;
		_removable = removable;
	}

	public int getId()
	{
		return _id;
	}

	public long getCount()
	{
		return _count;
	}

	public boolean isRemovable()
	{
		return _removable;
	}
}
