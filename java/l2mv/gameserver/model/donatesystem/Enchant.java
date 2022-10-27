package l2mv.gameserver.model.donatesystem;

public class Enchant
{
	private final int _id;
	private final long _count;
	private final int _value;

	public Enchant(int id, long count, int value)
	{
		_id = id;
		_count = count;
		_value = value;
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
		return _value;
	}
}
