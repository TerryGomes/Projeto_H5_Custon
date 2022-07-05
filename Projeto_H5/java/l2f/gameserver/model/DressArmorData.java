package l2f.gameserver.model;

public class DressArmorData
{
	private final int _id;
	private final String _name;
	private final int _chest;
	private final int _legs;
	private final int _gloves;
	private final int _feet;
	private final int _priceId;
	private final long _priceCount;

	public DressArmorData(int id, String name, int chest, int legs, int gloves, int feet, int priceId, long priceCount)
	{
		_id = id;
		_name = name;
		_chest = chest;
		_legs = legs;
		_gloves = gloves;
		_feet = feet;
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

	public int getChest()
	{
		return _chest;
	}

	public int getLegs()
	{
		return _legs;
	}

	public int getGloves()
	{
		return _gloves;
	}

	public int getFeet()
	{
		return _feet;
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
