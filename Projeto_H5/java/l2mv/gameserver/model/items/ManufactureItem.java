package l2mv.gameserver.model.items;

public class ManufactureItem
{
	private final int _recipeId;
	private final long _cost;

	public ManufactureItem(int recipeId, long cost)
	{
		_recipeId = recipeId;
		_cost = cost;
	}

	public int getRecipeId()
	{
		return _recipeId;
	}

	public long getCost()
	{
		return _cost;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if ((o == null) || (o.getClass() != this.getClass()))
		{
			return false;
		}
		return ((ManufactureItem) o).getRecipeId() == getRecipeId();
	}
}
