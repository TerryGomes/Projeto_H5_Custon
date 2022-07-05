package l2f.gameserver.model;

import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.templates.item.ItemTemplate;

public class ProductItemComponent
{
	private final int _itemId;
	private final int _count;

	private final int _weight;
	private final boolean _dropable;

	public ProductItemComponent(int item_id, int count)
	{
		_itemId = item_id;
		_count = count;

		ItemTemplate item = ItemHolder.getInstance().getTemplate(item_id);
		if (item != null)
		{
			_weight = item.getWeight();
			_dropable = item.isDropable();
		}
		else
		{
			// FIX ME what the mother facker???
			_weight = 0;
			_dropable = true;
		}
	}

	public int getItemId()
	{
		return _itemId;
	}

	public int getCount()
	{
		return _count;
	}

	public int getWeight()
	{
		return _weight;
	}

	public boolean isDropable()
	{
		return _dropable;
	}
}
