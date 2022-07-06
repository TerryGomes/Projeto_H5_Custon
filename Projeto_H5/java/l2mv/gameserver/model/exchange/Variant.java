package l2mv.gameserver.model.exchange;

public class Variant
{
	final int number;
	final int id;
	final String name;
	final String icon;

	public Variant(int number, int id, String name, String icon)
	{
		this.number = number;
		this.id = id;
		this.name = name;
		this.icon = icon;
	}

	public int getNumber()
	{
		return number;
	}

	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String getIcon()
	{
		return icon;
	}
}
