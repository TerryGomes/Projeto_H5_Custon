package l2mv.gameserver.model.items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import l2mv.commons.dao.JdbcEntityState;
import l2mv.gameserver.dao.ItemsDAO;
import l2mv.gameserver.model.items.ItemInstance.ItemLocation;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.templates.item.ItemTemplate.ItemClass;

public abstract class Warehouse extends ItemContainer
{
	private static final ItemsDAO _itemsDAO = ItemsDAO.getInstance();

	public static enum WarehouseType
	{
		NONE, PRIVATE, CLAN, CASTLE, FREIGHT
	}

	public static class ItemClassComparator implements Comparator<ItemInstance>, Serializable
	{
		private static final Comparator<ItemInstance> instance = new ItemClassComparator();
		private static final long serialVersionUID = 1936136265154026060L;

		public static Comparator<ItemInstance> getInstance()
		{
			return instance;
		}

		@Override
		public int compare(ItemInstance o1, ItemInstance o2)
		{
			if (o1 == null || o2 == null)
			{
				return 0;
			}
			int diff = Integer.compare(o1.getItemClass().ordinal(), o2.getItemClass().ordinal());
			if (diff == 0)
			{
				diff = Integer.compare(o1.getCrystalType().ordinal(), o2.getCrystalType().ordinal());
			}
			if (diff == 0)
			{
				diff = Integer.compare(o1.getItemId(), o2.getItemId());
			}
			if (diff == 0)
			{
				diff = Integer.compare(o1.getEnchantLevel(), o2.getEnchantLevel());
			}
			return diff;
		}
	}

	protected final int _ownerId;

	protected Warehouse(int ownerId)
	{
		_ownerId = ownerId;
	}

	public int getOwnerId()
	{
		return _ownerId;
	}

	public abstract ItemLocation getItemLocation();

	public ItemInstance[] getItems(ItemClass itemClass)
	{
		List<ItemInstance> result = new ArrayList<ItemInstance>();

		readLock();
		try
		{
			ItemInstance item;
			for (int i = 0; i < _items.size(); i++)
			{
				item = _items.get(i);
				if ((itemClass == null || itemClass == ItemClass.ALL) || item.getItemClass() == itemClass)
				{
					result.add(item);
				}
			}
		}
		finally
		{
			readUnlock();
		}

		return result.toArray(new ItemInstance[result.size()]);
	}

	public long getCountOfAdena()
	{
		return getCountOf(ItemTemplate.ITEM_ID_ADENA);
	}

	@Override
	protected void onAddItem(ItemInstance item)
	{
		item.setOwnerId(getOwnerId());
		item.setLocation(getItemLocation());
		item.setLocData(0);
		if (item.getJdbcState().isSavable())
		{
			item.save();
		}
		else
		{
			item.setJdbcState(JdbcEntityState.UPDATED);
			item.update();
		}
	}

	@Override
	protected void onModifyItem(ItemInstance item)
	{
		item.setJdbcState(JdbcEntityState.UPDATED);
		item.update();
	}

	@Override
	protected void onRemoveItem(ItemInstance item)
	{
		item.setLocData(-1);
	}

	@Override
	protected void onDestroyItem(ItemInstance item)
	{
		item.setCount(0L);
		item.delete();
	}

	public void restore()
	{
		final int ownerId = getOwnerId();

		writeLock();
		try
		{
			Collection<ItemInstance> items = _itemsDAO.getItemsByOwnerIdAndLoc(ownerId, getItemLocation());

			for (ItemInstance item : items)
			{
				_items.add(item);
			}
		}
		finally
		{
			writeUnlock();
		}
	}
}