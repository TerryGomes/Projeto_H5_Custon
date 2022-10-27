package l2mv.gameserver.model;

public class DressWeaponData
{
	private final int _id;
	private final String _name;
	private final String _type;
	private final int _priceId;
	private final long _priceCount;

	public DressWeaponData(int id, String name, String type, int priceId, long priceCount)
	{
		_id = id;
		_name = name;
		_type = type;
		_priceId = priceId;
		_priceCount = priceCount;
	}

	public int getId()
	{
		return _id;
	}

	public String getName()
	{
		return _name;
	}

	public String getType()
	{
		return _type;
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
