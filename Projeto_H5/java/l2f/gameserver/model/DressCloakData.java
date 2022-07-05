package l2f.gameserver.model;

public class DressCloakData
{
	private final int _id;
	private final int _cloak;
	private final String _name;
	private final int _priceId;
	private final long _priceCount;

	public DressCloakData(int id, int cloak, String name, int priceId, long priceCount)
	{
		_id = id;
		_cloak = cloak;
		_name = name;
		_priceId = priceId;
		_priceCount = priceCount;
	}

	public int getId()
	{
		return _id;
	}

	public int getCloakId()
	{
		return _cloak;
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
