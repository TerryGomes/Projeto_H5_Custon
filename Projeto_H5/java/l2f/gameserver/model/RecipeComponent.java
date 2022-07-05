package l2f.gameserver.model;

public class RecipeComponent
{
	private final int _itemId;
	private final int _quantity;

	public RecipeComponent(int itemId, int quantity)
	{
		_itemId = itemId;
		_quantity = quantity;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public int getQuantity()
	{
		return _quantity;
	}
}
