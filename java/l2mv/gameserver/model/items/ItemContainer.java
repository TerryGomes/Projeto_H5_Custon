package l2mv.gameserver.model.items;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import l2mv.commons.math.SafeMath;
import l2mv.gameserver.Config;
import l2mv.gameserver.dao.ItemsDAO;
import l2mv.gameserver.idfactory.IdFactory;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.utils.ItemActionLog;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.ItemStateLog;
import l2mv.gameserver.utils.Log;

public abstract class ItemContainer
{
	protected static final ItemsDAO _itemsDAO = ItemsDAO.getInstance();

	protected final List<ItemInstance> _items = new ArrayList<ItemInstance>();
	/** Блокировка для чтения/записи вещей из списка и внешних операций */
	protected final ReadWriteLock lock = new ReentrantReadWriteLock();
	protected final Lock readLock = lock.readLock();
	protected final Lock writeLock = lock.writeLock();

	protected ItemContainer()
	{

	}

	public int getSize()
	{
		return _items.size();
	}

	public ItemInstance[] getItems()
	{
		readLock();
		try
		{
			return _items.toArray(new ItemInstance[_items.size()]);
		}
		finally
		{
			readUnlock();
		}
	}

	public void clear()
	{
		writeLock();
		try
		{
			_items.clear();
		}
		finally
		{
			writeUnlock();
		}
	}

	public final void writeLock()
	{
		writeLock.lock();
	}

	public final void writeUnlock()
	{
		writeLock.unlock();
	}

	public final void readLock()
	{
		readLock.lock();
	}

	public final void readUnlock()
	{
		readLock.unlock();
	}

	/**
	 * Найти вещь по objectId
	 * @param objectId
	 * @return вещь, если найдена, либо null если не найдена
	 */
	public ItemInstance getItemByObjectId(int objectId)
	{
		readLock();
		try
		{
			ItemInstance item;
			for (int i = 0; i < _items.size(); i++)
			{
				item = _items.get(i);
				if (item.getObjectId() == objectId)
				{
					return item;
				}
			}
		}
		finally
		{
			readUnlock();
		}

		return null;
	}

	/**
	 * Найти первую вещь по itemId
	 * @param itemId
	 * @return вещь, если найдена, либо null если не найдена
	 */
	public ItemInstance getItemByItemId(int itemId)
	{
		readLock();
		try
		{
			ItemInstance item;
			for (int i = 0; i < _items.size(); i++)
			{
				item = _items.get(i);
				if (item.getItemId() == itemId)
				{
					return item;
				}
			}
		}
		finally
		{
			readUnlock();
		}

		return null;
	}

	/**
	 * Найти все вещи по itemId
	 * @param itemId
	 * @return Список найденых вещей
	 */
	public List<ItemInstance> getItemsByItemId(int itemId)
	{
		List<ItemInstance> result = new ArrayList<ItemInstance>();

		readLock();
		try
		{
			ItemInstance item;
			for (int i = 0; i < _items.size(); i++)
			{
				item = _items.get(i);
				if (item.getItemId() == itemId)
				{
					result.add(item);
				}
			}
		}
		finally
		{
			readUnlock();
		}

		return result;
	}

	public long getCountOf(int itemId)
	{
		long count = 0L;
		readLock();
		try
		{
			ItemInstance item;
			for (int i = 0; i < _items.size(); i++)
			{
				item = _items.get(i);
				if (item.getItemId() == itemId)
				{
					count = SafeMath.addAndLimit(count, item.getCount());
				}
			}
		}
		finally
		{
			readUnlock();
		}
		return count;
	}

	/**
	 * Создать вещь и добавить в список, либо увеличить количество вещи в инвентаре
	 *
	 * @param itemId - идентификатор itemId вещи
	 * @param count - количество для создания, либо увеличения
	 * @return созданная вещь
	 */
	public ItemInstance addItem(int itemId, long count, String owner, String log)
	{
		if (count < 1)
		{
			return null;
		}

		ItemInstance item;

		writeLock();
		try
		{
			item = getItemByItemId(itemId);

			if (item != null && item.isStackable())
			{
				synchronized (item)
				{
					item.setCount(SafeMath.addAndLimit(item.getCount(), count));

					// Synerge - Max adena count
					if (itemId == ItemTemplate.ITEM_ID_ADENA && Config.MAX_ADENA > -1 && item.getCount() > Config.MAX_ADENA)
					{
						item.setCount(Config.MAX_ADENA);
					}

					onModifyItem(item);
					if (owner != null && log != null)
					{
						Log.logItemActions(new ItemActionLog(ItemStateLog.ADD, log, owner, item, count));
					}
				}
			}
			else
			{
				item = ItemFunctions.createItem(itemId);
				item.setCount(count);

				// Synerge - Max adena count
				if (itemId == ItemTemplate.ITEM_ID_ADENA && Config.MAX_ADENA > -1 && item.getCount() > Config.MAX_ADENA)
				{
					item.setCount(Config.MAX_ADENA);
				}

				_items.add(item);
				onAddItem(item);

				if (owner != null && log != null)
				{
					Log.logItemActions(new ItemActionLog(ItemStateLog.ADD, log, owner, item, count));
				}
			}
		}
		finally
		{
			writeUnlock();
		}

		return item;
	}

	/**
	 * Добавить вещь в список.<br>
	 * При добавлении нескольких вещей подряд, список должен быть заблокирован с writeLock() и разблокирован после добавления с writeUnlock()<br>
	 * <br>
	 * <b><font color="red">Должно выполнятся в блоке synchronized(item)</font></b>
	 *
	 * @return вещь, полученая в результате добавления, null если не найдена
	 */
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
			if (getItemByObjectId(item.getObjectId()) != null)
			{
				return null;
			}

			long countToAdd = item.getCount();
			if (item.isStackable())
			{
				int itemId = item.getItemId();
				result = getItemByItemId(itemId);
				if (result != null)
				{
					synchronized (result)
					{
						// увеличить количество в стопке
						result.setCount(SafeMath.addAndLimit(item.getCount(), result.getCount()));
						onModifyItem(result);
						onDestroyItem(item);

					}
				}
			}

			if (result == null)
			{
				_items.add(item);
				result = item;

				onAddItem(result);
			}

			if (owner != null && log != null)
			{
				Log.logItemActions(new ItemActionLog(ItemStateLog.ADD, log, owner, result, countToAdd));
			}
		}
		finally
		{
			writeUnlock();
		}

		return result;
	}

	/**
	 * Удаляет вещь из списка, либо уменьшает количество вещи по objectId
	 *
	 * @param objectId - идентификатор objectId вещи
	 * @param count - на какое количество уменьшить, если количество равно количество вещи, то вещь удаляется из списка
	 * @return вещь, полученая в результате удаления, null если не найдена
	 */
	public ItemInstance removeItemByObjectId(int objectId, long count, String owner, String log)
	{
		if (count < 1)
		{
			return null;
		}

		ItemInstance result;

		writeLock();
		try
		{
			ItemInstance item;
			if ((item = getItemByObjectId(objectId)) == null)
			{
				return null;
			}

			synchronized (item)
			{
				result = removeItem(item, count, owner, log);
			}
		}
		finally
		{
			writeUnlock();
		}

		return result;
	}

	/**
	 * Удаляет вещь из списка, либо уменьшает количество первой найденной вещи по itemId
	 *
	 * @param itemId - идентификатор itemId
	 * @param count - на какое количество уменьшить, если количество равно количество вещи, то вещь удаляется из списка
	 * @return вещь, полученая в результате удаления, null если не найдена
	 */
	public ItemInstance removeItemByItemId(int itemId, long count, String owner, String log)
	{
		if (count < 1)
		{
			return null;
		}

		ItemInstance result;

		writeLock();
		try
		{
			ItemInstance item;
			if ((item = getItemByItemId(itemId)) == null)
			{
				return null;
			}

			synchronized (item)
			{
				result = removeItem(item, count, owner, log);
			}
		}
		finally
		{
			writeUnlock();
		}

		return result;
	}

	/**
	 * Удаляет вещь из списка, либо уменьшает количество вещи.<br>
	 * При удалении нескольких вещей подряд, список должен быть заблокирован с writeLock() и разблокирован после добавления с writeUnlock()<br>
	 * <br>
	 * <b><font color="red">Должно выполнятся в блоке synchronized(item)</font></b>
	 *
	 * @param item - вещь для удаления
	 * @param count - на какое количество уменьшить, если количество равно количество вещи, то вещь удаляется из списка
	 * @return вещь, полученая в результате удаления
	 */
	public ItemInstance removeItem(ItemInstance item, long count, String owner, String log)
	{
		if ((item == null) || (count < 1) || (item.getCount() < count))
		{
			return null;
		}

		writeLock();
		try
		{
			if (!_items.contains(item))
			{
				return null;
			}

			if (item.getCount() > count)
			{
				if (owner != null && log != null)
				{
					Log.logItemActions(new ItemActionLog(ItemStateLog.REMOVE, log, owner, item, count));
				}

				item.setCount(item.getCount() - count);
				onModifyItem(item);

				ItemInstance newItem = new ItemInstance(IdFactory.getInstance().getNextId(), item.getItemId());
				newItem.setCount(count);

				return newItem;
			}
			else
			{
				return removeItem(item, owner, log);
			}
		}
		finally
		{
			writeUnlock();
		}
	}

	/**
	 * Удаляет вещь из списка.<br>
	 * При удалении нескольких вещей подряд, список должен быть заблокирован с writeLock() и разблокирован после добавления с writeUnlock()<br>
	 * <br>
	 * <b><font color="red">Должно выполнятся в блоке synchronized(item)</font></b>
	 *
	 * @param item - вещь для удаления
	 * @return вещь, полученая в результате удаления
	 */
	public ItemInstance removeItem(ItemInstance item, String owner, String log)
	{
		if (item == null)
		{
			return null;
		}

		writeLock();
		try
		{
			if (!_items.remove(item))
			{
				return null;
			}

			onRemoveItem(item);

			if (owner != null && log != null)
			{
				Log.logItemActions(new ItemActionLog(ItemStateLog.DESTROY, log, owner, item, item.getCount()));
			}

			return item;
		}
		finally
		{
			writeUnlock();
		}
	}

	/**
	 * Уничтожить вещь из списка, либо снизить количество по идентификатору objectId
	 *
	 * @param objectId
	 * @param count - количество для удаления
	 * @return true, если количество было снижено или вещь была уничтожена
	 */
	public boolean destroyItemByObjectId(int objectId, long count, String owner, String log)
	{
		writeLock();
		try
		{
			ItemInstance item;
			if ((item = getItemByObjectId(objectId)) == null)
			{
				return false;
			}

			synchronized (item)
			{
				return destroyItem(item, count, owner, log);
			}
		}
		finally
		{
			writeUnlock();
		}
	}

	/**
	 * Уничтожить вещь из списка, либо снизить количество по идентификатору itemId
	 *
	 * @param itemId
	 * @param count - количество для удаления
	 * @return true, если количество было снижено или вещь была уничтожена
	 */
	public boolean destroyItemByItemId(int itemId, long count, String owner, String log)
	{
		writeLock();
		try
		{
			ItemInstance item;
			if ((item = getItemByItemId(itemId)) == null)
			{
				return false;
			}

			synchronized (item)
			{
				return destroyItem(item, count, owner, log);
			}
		}
		finally
		{
			writeUnlock();
		}
	}

	/**
	 * Уничтожить вещь из списка, либо снизить количество<br>
	 * <br>
	 * <b><font color="red">Должно выполнятся в блоке synchronized(item)</font></b>
	 *
	 * @param count - количество для удаления
	 * @return true, если количество было снижено или вещь была уничтожена
	 */
	public boolean destroyItem(ItemInstance item, long count, String owner, String log)
	{
		if ((item == null) || (count < 1) || (item.getCount() < count))
		{
			return false;
		}

		writeLock();
		try
		{
			if (!_items.contains(item))
			{
				return false;
			}

			if (item.getCount() > count)
			{
				if (owner != null && log != null)
				{
					Log.logItemActions(new ItemActionLog(ItemStateLog.DESTROY, log, owner, item, count));
				}

				item.setCount(item.getCount() - count);
				onModifyItem(item);

				return true;
			}
			else
			{
				return destroyItem(item, owner, log);
			}
		}
		finally
		{
			writeUnlock();
		}
	}

	/**
	 * Удаляет вещь из списка.<br>
	 * <br>
	 * <b><font color="red">Должно выполнятся в блоке synchronized(item)</font></b>
	 *
	 * @param item - вещь для удаления
	 * @return вещь, полученая в результате удаления
	 */
	public boolean destroyItem(ItemInstance item, String owner, String log)
	{
		if (item == null)
		{
			return false;
		}

		writeLock();
		try
		{
			if (!_items.remove(item))
			{
				return false;
			}

			if (owner != null && log != null)
			{
				Log.logItemActions(new ItemActionLog(ItemStateLog.DESTROY, log, owner, item, item.getCount()));
			}
			onRemoveItem(item);
			onDestroyItem(item);

			return true;
		}
		finally
		{
			writeUnlock();
		}
	}

	protected abstract void onAddItem(ItemInstance item);

	protected abstract void onModifyItem(ItemInstance item);

	protected abstract void onRemoveItem(ItemInstance item);

	protected abstract void onDestroyItem(ItemInstance item);
}
