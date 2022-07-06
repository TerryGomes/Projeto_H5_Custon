package l2mv.gameserver.model.exchange;

import java.util.List;

public class Change
{
	final int id;
	final String name;
	final String icon;
	final int cost_id;
	final long cost_count;
	final boolean attribute_change;
	final boolean is_upgrade;
	final List<Variant> variants;

	public Change(int id, String name, String icon, int cost_id, long cost_count, boolean attribute_change, boolean is_upgrade, List<Variant> variants)
	{
		this.id = id;
		this.name = name;
		this.icon = icon;
		this.cost_id = cost_id;
		this.cost_count = cost_count;
		this.attribute_change = attribute_change;
		this.is_upgrade = is_upgrade;
		this.variants = variants;
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

	public int getCostId()
	{
		return cost_id;
	}

	public long getCostCount()
	{
		return cost_count;
	}

	public boolean attChange()
	{
		return attribute_change;
	}

	public boolean isUpgrade()
	{
		return is_upgrade;
	}

	public List<Variant> getList()
	{
		return variants;
	}

	public Variant getVariant(int id)
	{
		for (Variant var : variants)
		{
			if (var.getNumber() == id)
			{
				return var;
			}
		}
		return null;
	}
}
