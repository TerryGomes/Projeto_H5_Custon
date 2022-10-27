package l2mv.gameserver.model.base;

import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.model.items.ItemAttributes;

public class MultiSellIngredient implements Cloneable
{
	private int _itemId;
	private long _itemCount;
	private int _itemEnchant;
	private ItemAttributes _itemAttributes;
	private boolean _mantainIngredient;

	public MultiSellIngredient(int itemId, long itemCount)
	{
		this(itemId, itemCount, 0);
	}

	public MultiSellIngredient(int itemId, long itemCount, int enchant)
	{
		_itemId = itemId;
		_itemCount = itemCount;
		_itemEnchant = enchant;
		_mantainIngredient = false;
		_itemAttributes = new ItemAttributes();
	}

	@Override
	public MultiSellIngredient clone()
	{
		MultiSellIngredient mi = new MultiSellIngredient(_itemId, _itemCount, _itemEnchant);
		mi.setMantainIngredient(_mantainIngredient);
		mi.setItemAttributes(_itemAttributes.clone());
		return mi;
	}

	/**
	 * @param itemId The itemId to set.
	 */
	public void setItemId(int itemId)
	{
		_itemId = itemId;
	}

	/**
	 * @return Returns the itemId.
	 */
	public int getItemId()
	{
		return _itemId;
	}

	/**
	 * @param itemCount The itemCount to set.
	 */
	public void setItemCount(long itemCount)
	{
		_itemCount = itemCount;
	}

	/**
	 * @return Returns the itemCount.
	 */
	public long getItemCount()
	{
		return _itemCount;
	}

	/**
	 * Returns if item is stackable
	 * @return boolean
	 */
	public boolean isStackable()
	{
		return _itemId <= 0 || ItemHolder.getInstance().getTemplate(_itemId).isStackable();
	}

	/**
	 * @param itemEnchant The itemEnchant to set.
	 */
	public void setItemEnchant(int itemEnchant)
	{
		_itemEnchant = itemEnchant;
	}

	/**
	 * @return Returns the itemEnchant.
	 */
	public int getItemEnchant()
	{
		return _itemEnchant;
	}

	public ItemAttributes getItemAttributes()
	{
		return _itemAttributes;
	}

	public void setItemAttributes(ItemAttributes attr)
	{
		_itemAttributes = attr;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (_itemCount ^ _itemCount >>> 32);
		for (Element e : Element.VALUES)
		{
			result = prime * result + _itemAttributes.getValue(e);
		}
		result = prime * result + _itemEnchant;
		result = prime * result + _itemId;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass()))
		{
			return false;
		}
		MultiSellIngredient other = (MultiSellIngredient) obj;
		if ((_itemId != other._itemId) || (_itemCount != other._itemCount) || (_itemEnchant != other._itemEnchant))
		{
			return false;
		}
		for (Element e : Element.VALUES)
		{
			if (_itemAttributes.getValue(e) != other._itemAttributes.getValue(e))
			{
				return false;
			}
		}
		return true;
	}

	public boolean getMantainIngredient()
	{
		return _mantainIngredient;
	}

	public void setMantainIngredient(boolean mantainIngredient)
	{
		_mantainIngredient = mantainIngredient;
	}
}