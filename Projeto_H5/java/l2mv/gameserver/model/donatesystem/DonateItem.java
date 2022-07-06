package l2mv.gameserver.model.donatesystem;

public class DonateItem
{
	private final int _id;
	private final long _count;
	private final int _enchant;

	public DonateItem(int id, long count, int enchant)
	{
		_id = id;
		_count = count;
		_enchant = enchant;
	}

	public int getId()
	{
		return _id;
	}

	public long getCount()
	{
		return _count;
	}

	public int getEnchant()
	{
		return _enchant;
	}
}
