package l2f.gameserver.model;

public class DressShieldData
{
	private final int _id;
	private final int _shield;
	private final String _name;
	private final int _priceId;
	private final long _priceCount;

	public DressShieldData(int id, int shield, String name, int priceId, long priceCount)
	{
		_id = id;
		_shield = shield;
		_name = name;
		_priceId = priceId;
		_priceCount = priceCount;
	}

	public int getId()
	{
		return _id;
	}

	public int getShieldId()
	{
		return _shield;
	}

	public String getName()
	{
		return _name;
	}

	public int getPriceId()
	{
		return _priceId;
	}

	public long getPriceCount()
	{
		return _priceCount;
	}
}
