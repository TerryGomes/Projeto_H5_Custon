package l2mv.gameserver.model.items;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dao.JdbcEntityState;
import l2mv.gameserver.dao.ItemsDAO;
import l2mv.gameserver.model.items.ItemInstance.ItemLocation;

public class AuctionStorage extends ItemContainer
{
	private static final Logger _log = LoggerFactory.getLogger(AuctionStorage.class);
	private static final ItemsDAO _itemsDAO = ItemsDAO.getInstance();
	private static AuctionStorage _instance;

	protected AuctionStorage()
	{
		restore();
	}

	public void deleteItemFromList(ItemInstance item)
	{
		_items.remove(item);
	}

	@Override
	public ItemInstance addItem(ItemInstance item, String owner, String log)
	{
		if ((item == null) || (item.getCount() < 1))
		{
			return null;
		}

		ItemInstance result = null;

		writeLock();
		try
		{
			_items.add(item);
			result = item;
			item.setJdbcState(JdbcEntityState.CREATED);
			onAddItem(result);
		}
		finally
		{
			writeUnlock();
		}

		return result;
	}

	public ItemInstance addFullItem(ItemInstance item)
	{
		if ((item == null) || (item.getCount() < 1))
		{
			return null;
		}

		ItemInstance result = null;

		writeLock();
		try
		{
			_items.add(item);
			result = item;
			item.setJdbcState(JdbcEntityState.UPDATED);
			onAddItem(result);
		}
		finally
		{
			writeUnlock();
		}

		return result;
	}

	@Override
	protected void onAddItem(ItemInstance item)
	{
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

	public ItemLocation getItemLocation()
	{
		return ItemLocation.AUCTION;
	}

	public void updateItem(int objectId, int newOwnerId)
	{
		writeLock();
		try
		{
			ItemInstance item;
			if ((item = getItemByObjectId(objectId)) == null)
			{
				_log.warn("item is null in auction storage, obj id:" + objectId);
				return;
			}

			synchronized (item)
			{
				item.setOwnerId(newOwnerId);
				item.setLocation(ItemLocation.INVENTORY);
				item.setJdbcState(JdbcEntityState.UPDATED);
				_itemsDAO.update(item);
				deleteItemFromList(item);
			}

		}
		finally
		{
			writeUnlock();
		}
	}

	public void restore()
	{
		writeLock();
		try
		{
			_items.clear();
			Collection<ItemInstance> items = _itemsDAO.getItemsByLocation(ItemLocation.AUCTION);

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

	public static AuctionStorage getInstance()
	{
		if (_instance == null)
		{
			_instance = new AuctionStorage();
		}
		return _instance;
	}
}