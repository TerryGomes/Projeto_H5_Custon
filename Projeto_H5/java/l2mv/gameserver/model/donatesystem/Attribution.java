package l2mv.gameserver.model.donatesystem;

public class Attribution
{
	private final int _id;
	private final long _count;
	private final int _value;
	private final int _size;

	public Attribution(int id, long count, int value, int size)
	{
		_id = id;
		_count = count;
		_value = value;
		_size = size;
	}

	public int getId()
	{
		return _id;
	}

	public long getCount()
	{
		return _count;
	}

	public int getValue()
	{
		return _value;
	}

	public int getSize()
	{
		return _size;
	}
}
