package l2mv.gameserver.model.items;

import java.util.Collection;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.PetInstance;
import l2mv.gameserver.model.items.ItemInstance.ItemLocation;
import l2mv.gameserver.network.serverpackets.PetInventoryUpdate;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.utils.ItemFunctions;

public class PetInventory extends Inventory
{
	private final PetInstance _actor;

	public PetInventory(PetInstance actor)
	{
		super(actor.getPlayer().getObjectId());
		_actor = actor;
	}

	@Override
	public PetInstance getActor()
	{
		return _actor;
	}

	public Player getOwner()
	{
		return _actor.getPlayer();
	}

	@Override
	protected ItemLocation getBaseLocation()
	{
		return ItemLocation.PET_INVENTORY;
	}

	@Override
	protected ItemLocation getEquipLocation()
	{
		return ItemLocation.PET_PAPERDOLL;
	}

	@Override
	protected void onRefreshWeight()
	{
		getActor().sendPetInfo();
	}

	@Override
	protected void sendAddItem(ItemInstance item)
	{
		getOwner().sendPacket(new PetInventoryUpdate().addNewItem(item));
	}

	@Override
	protected void sendModifyItem(ItemInstance item)
	{
		getOwner().sendPacket(new PetInventoryUpdate().addModifiedItem(item));
	}

	@Override
	protected void sendRemoveItem(ItemInstance item)
	{
		getOwner().sendPacket(new PetInventoryUpdate().addRemovedItem(item));
	}

	@Override
	public void restore()
	{
		final int ownerId = getOwnerId();

		writeLock();
		try
		{
			Collection<ItemInstance> items = _itemsDAO.getItemsByOwnerIdAndLoc(ownerId, getBaseLocation());

			for (ItemInstance item : items)
			{
				_items.add(item);
				onRestoreItem(item);
			}

			items = _itemsDAO.getItemsByOwnerIdAndLoc(ownerId, getEquipLocation());

			for (ItemInstance item : items)
			{
				_items.add(item);
				onRestoreItem(item);
				if (ItemFunctions.checkIfCanEquip(getActor(), item) == null)
				{
					setPaperdollItem(item.getEquipSlot(), item);
				}
			}
		}
		finally
		{
			writeUnlock();
		}

		refreshWeight();
	}

	@Override
	public void store()
	{
		writeLock();
		try
		{
			_itemsDAO.update(_items);
		}
		finally
		{
			writeUnlock();
		}
	}

	public void validateItems()
	{
		for (ItemInstance item : _paperdoll)
		{
			if (item != null && (ItemFunctions.checkIfCanEquip(getActor(), item) != null || !item.getTemplate().testCondition(getActor(), item)))
			{
				unEquipItem(item);
			}
		}
	}

	public boolean destroyItem(ItemInstance item, long count, String log)
	{
		return destroyItem(item, count, "Pet " + _actor.getPlayer().toString(), log);
	}

	public boolean destroyItem(ItemInstance item, String log)
	{
		return destroyItem(item, "Pet " + _actor.getPlayer().toString(), log);
	}

	public boolean destroyItemByItemId(int itemId, long count, String log)
	{
		return destroyItemByItemId(itemId, count, "Pet " + _actor.getPlayer().toString(), log);
	}

	public boolean destroyItemByObjectId(int objectId, long count, String log)
	{
		return destroyItemByObjectId(objectId, count, "Pet " + _actor.getPlayer().toString(), log);
	}

	public void addItem(int itemId, long count, boolean notify)
	{
		if (count < 1)
		{
			return;
		}

		addItem(itemId, count, "");

		if (notify)
		{
			getActor().sendPacket(SystemMessage2.obtainItems(itemId, count, 0));
		}
	}

	public ItemInstance addItem(ItemInstance item, String log)
	{
		return addItem(item, "Pet " + _actor.getPlayer().toString(), log);
	}

	public ItemInstance addItem(int itemId, long count, String log)
	{
		return addItem(itemId, count, "Pet " + _actor.getPlayer().toString(), log);
	}

	public ItemInstance removeItem(ItemInstance item, long count, String log)
	{
		return removeItem(item, count, "Pet " + _actor.getPlayer().toString(), log);
	}

	public ItemInstance removeItem(ItemInstance item, String log)
	{
		return removeItem(item, "Pet " + _actor.getPlayer().toString(), log);
	}

	public ItemInstance removeItemByItemId(int itemId, long count, String log)
	{
		return removeItemByItemId(itemId, count, "Pet " + _actor.getPlayer().toString(), log);
	}

	public ItemInstance removeItemByObjectId(int objectId, long count, String log)
	{
		return removeItemByObjectId(objectId, count, "Pet " + _actor.getPlayer().toString(), log);
	}
}