package l2f.gameserver.model.items;

import java.util.Collection;

import org.apache.commons.lang3.ArrayUtils;

import l2f.commons.collections.CollectionUtils;
import l2f.commons.dao.JdbcEntityState;
import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.data.xml.holder.DressArmorHolder;
import l2f.gameserver.instancemanager.CursedWeaponsManager;
import l2f.gameserver.model.DressArmorData;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Zone;
import l2f.gameserver.model.items.ItemInstance.ItemLocation;
import l2f.gameserver.model.items.listeners.AccessoryListener;
import l2f.gameserver.model.items.listeners.ArmorSetListener;
import l2f.gameserver.model.items.listeners.BowListener;
import l2f.gameserver.model.items.listeners.ItemAugmentationListener;
import l2f.gameserver.model.items.listeners.ItemEnchantOptionsListener;
import l2f.gameserver.model.items.listeners.ItemSkillsListener;
import l2f.gameserver.network.serverpackets.ExBR_AgathionEnergyInfo;
import l2f.gameserver.network.serverpackets.InventoryUpdate;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import l2f.gameserver.templates.item.ItemTemplate;
import l2f.gameserver.utils.ItemFunctions;

public class PcInventory extends Inventory
{
	private final Player _owner;

	// locks
	private LockType _lockType = LockType.NONE;
	private int[] _lockItems = ArrayUtils.EMPTY_INT_ARRAY;

	public PcInventory(Player owner)
	{
		super(owner.getObjectId());
		_owner = owner;

		addListener(ItemSkillsListener.getInstance());
		addListener(ItemAugmentationListener.getInstance());
		addListener(ItemEnchantOptionsListener.getInstance());
		addListener(ArmorSetListener.getInstance());
		addListener(BowListener.getInstance());
		addListener(AccessoryListener.getInstance());
	}

	@Override
	public Player getActor()
	{
		return _owner;
	}

	@Override
	protected ItemLocation getBaseLocation()
	{
		return ItemLocation.INVENTORY;
	}

	@Override
	protected ItemLocation getEquipLocation()
	{
		return ItemLocation.PAPERDOLL;
	}

	public long getAdena()
	{
		ItemInstance _adena = getItemByItemId(57);
		if (_adena == null)
		{
			return 0;
		}
		return _adena.getCount();
	}

	/**
	 * Добавляет адену игроку.<BR>
	 * <BR>
	 * @param amount - сколько адены дать
	 * @return L2ItemInstance - новое количество адены
	 */
	public ItemInstance addAdena(long amount, String log)
	{
		return addItem(ItemTemplate.ITEM_ID_ADENA, amount, log);
	}

	public boolean reduceAdena(long adena, String log)
	{
		return destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, adena, log);
	}

	public int getPaperdollAugmentationId(int slot)
	{
		ItemInstance item = _paperdoll[slot];
		if ((item != null) && item.isAugmented())
		{
			return item.getAugmentationId();
		}
		return 0;
	}

	@Override
	public int getPaperdollItemId(int slot)
	{
		Player player = getActor();

		int itemId = super.getPaperdollItemId(slot);

		if ((slot == PAPERDOLL_RHAND) && (itemId == 0) && player.isClanAirShipDriver())
		{
			itemId = 13556; // Затычка на отображение штурвала - Airship Helm
		}

		return itemId;
	}

	@Override
	public int getPaperdollVisualItemId(int slot)
	{
		if (slot == PAPERDOLL_RHAND && getActor().isClanAirShipDriver())
		{
			return 13556; // Airship Helm
		}

		ItemInstance item = getPaperdollItem(slot);
		if (item != null)
		{
			// Synerge - Support for visual dressme
			switch (slot)
			{
			case PAPERDOLL_CHEST:
			case PAPERDOLL_LEGS:
			case PAPERDOLL_GLOVES:
			case PAPERDOLL_FEET:
			{
				if (mustShowDressMe())
				{
					int visualItemId = item.getVisualItemId();

					if (visualItemId == -1)
					{
						return 0;
					}
					if (visualItemId != 0)
					{
						return visualItemId;
					}
				}
				break;
			}
			default:
			{
				int visualItemId = item.getVisualItemId();

				if (visualItemId == -1)
				{
					return 0;
				}
				if (visualItemId != 0)
				{
					return visualItemId;
				}
				break;
			}

			}

			return item.getItemId();
		}
		else if (slot == PAPERDOLL_HAIR)
		{
			item = _paperdoll[PAPERDOLL_DHAIR];
			if (item != null)
			{
				return item.getItemId();
			}
		}

		return 0;
	}

	@Override
	protected void onRefreshWeight()
	{
		// notify char for overload checking
		getActor().refreshOverloaded();
	}

	/**
	 * Функция для валидации вещей в инвентаре. Снимает все вещи, которые нельзя носить. Применяется при входе в игру, смене саба, захвате замка, выходе из клана.
	 */
	public void validateItems()
	{
		for (ItemInstance item : _paperdoll)
		{
			if ((item != null) && ((ItemFunctions.checkIfCanEquip(getActor(), item) != null) || !item.getTemplate().testCondition(getActor(), item)))
			{
				unEquipItem(item);
				getActor().sendDisarmMessage(item);
			}
		}
	}

	/**
	 * FIXME [VISTALL] for skills is critical to always delete them and add, for no triggers
	 */
	public void validateItemsSkills()
	{
		for (ItemInstance item : _paperdoll)
		{
			if ((item == null) || (item.getTemplate().getType2() != ItemTemplate.TYPE2_WEAPON))
			{
				continue;
			}

			boolean needUnequipSkills = getActor().getWeaponsExpertisePenalty() > 0;

			if (item.getTemplate().getAttachedSkills().length > 0)
			{
				boolean has = getActor().getSkillLevel(item.getTemplate().getAttachedSkills()[0].getId()) > 0;
				if (needUnequipSkills && has)
				{
					ItemSkillsListener.getInstance().onUnequip(item.getEquipSlot(), item, getActor());
				}
				else if (!needUnequipSkills && !has)
				{
					ItemSkillsListener.getInstance().onEquip(item.getEquipSlot(), item, getActor());
				}
			}
			else if (item.getTemplate().getEnchant4Skill() != null)
			{
				boolean has = getActor().getSkillLevel(item.getTemplate().getEnchant4Skill().getId()) > 0;
				if (needUnequipSkills && has)
				{
					ItemSkillsListener.getInstance().onUnequip(item.getEquipSlot(), item, getActor());
				}
				else if (!needUnequipSkills && !has)
				{
					ItemSkillsListener.getInstance().onEquip(item.getEquipSlot(), item, getActor());
				}
			}
			else if (!item.getTemplate().getTriggerList().isEmpty())
			{
				if (needUnequipSkills)
				{
					ItemSkillsListener.getInstance().onUnequip(item.getEquipSlot(), item, getActor());
				}
				else
				{
					ItemSkillsListener.getInstance().onEquip(item.getEquipSlot(), item, getActor());
				}
			}
		}
	}

	/**
	 * FIXME Hack to update skills to equip when changing subclass
	 */
	public boolean isRefresh = false;

	public void refreshEquip()
	{
		isRefresh = true;
		for (ItemInstance item : getItems())
		{
			if (item.isEquipped())
			{
				int slot = item.getEquipSlot();
				_listeners.onUnequip(slot, item);
				_listeners.onEquip(slot, item);
			}
			else if (item.getItemType() == EtcItemType.RUNE)
			{
				_listeners.onUnequip(-1, item);
				_listeners.onEquip(-1, item);
			}
		}
		isRefresh = false;
	}

	/**
	 * Вызывается из RequestSaveInventoryOrder
	 */
	public void sort(int[][] order)
	{
		boolean needSort = false;
		for (int[] element : order)
		{
			ItemInstance item = getItemByObjectId(element[0]);
			if ((item == null) || (item.getLocation() != ItemLocation.INVENTORY) || (item.getLocData() == element[1]))
			{
				continue;
			}
			item.setLocData(element[1]);
			item.setJdbcState(JdbcEntityState.UPDATED); // lazy update
			needSort = true;
		}
		if (needSort)
		{
			CollectionUtils.eqSort(_items, ItemOrderComparator.getInstance());
		}
	}

	private static final int[][] arrows =
	{
		//
		{
			17
		}, // NG
		{
			1341,
			22067
		}, // D
		{
			1342,
			22068
		}, // C
		{
			1343,
			22069
		}, // B
		{
			1344,
			22070
		}, // A
		{
			1345,
			22071
		}, // S
	};

	public ItemInstance findArrowForBow(ItemTemplate bow)
	{
		int[] arrowsId = arrows[bow.getCrystalType().externalOrdinal];
		ItemInstance ret = null;
		for (int id : arrowsId)
		{
			if ((ret = getItemByItemId(id)) != null)
			{
				return ret;
			}
		}
		return null;
	}

	private static final int[][] bolts =
	{
		//
		{
			9632
		}, // NG
		{
			9633,
			22144
		}, // D
		{
			9634,
			22145
		}, // C
		{
			9635,
			22146
		}, // B
		{
			9636,
			22147
		}, // A
		{
			9637,
			22148
		}, // S
	};

	public ItemInstance findArrowForCrossbow(ItemTemplate xbow)
	{
		int[] boltsId = bolts[xbow.getCrystalType().externalOrdinal];
		ItemInstance ret = null;
		for (int id : boltsId)
		{
			if ((ret = getItemByItemId(id)) != null)
			{
				return ret;
			}
		}
		return null;
	}

	public ItemInstance findEquippedLure()
	{
		ItemInstance res = null;
		int last_lure = 0;
		Player owner = getActor();
		String LastLure = owner.getVar("LastLure");
		if ((LastLure != null) && !LastLure.isEmpty())
		{
			last_lure = Integer.valueOf(LastLure);
		}
		for (ItemInstance temp : getItems())
		{
			if (temp.getItemType() == EtcItemType.BAIT)
			{
				if ((temp.getLocation() == ItemLocation.PAPERDOLL) && (temp.getEquipSlot() == PAPERDOLL_LHAND))
				{
					return temp;
				}
				else if ((last_lure > 0) && (res == null) && (temp.getObjectId() == last_lure))
				{
					res = temp;
				}
			}
		}
		return res;
	}

	public void lockItems(LockType lock, int[] items)
	{
		if (_lockType != LockType.NONE)
		{
			return;
		}

		_lockType = lock;
		_lockItems = items;

		getActor().sendItemList(false);
	}

	public void unlock()
	{
		if (_lockType == LockType.NONE)
		{
			return;
		}

		_lockType = LockType.NONE;
		_lockItems = ArrayUtils.EMPTY_INT_ARRAY;

		getActor().sendItemList(false);
	}

	public boolean isLockedItem(ItemInstance item)
	{
		switch (_lockType)
		{
		case INCLUDE:
			return ArrayUtils.contains(_lockItems, item.getItemId());
		case EXCLUDE:
			return !ArrayUtils.contains(_lockItems, item.getItemId());
		default:
			return false;
		}
	}

	public LockType getLockType()
	{
		return _lockType;
	}

	public int[] getLockItems()
	{
		return _lockItems;
	}

	@Override
	protected void onRestoreItem(ItemInstance item)
	{
		super.onRestoreItem(item);

		if (item.getItemType() == EtcItemType.RUNE)
		{
			_listeners.onEquip(-1, item);
		}

		if (item.isTemporalItem())
		{
			item.startTimer(new LifeTimeTask(item));
		}

		if (item.isCursed())
		{
			CursedWeaponsManager.getInstance().checkPlayer(getActor(), item);
		}
	}

	@Override
	protected void onAddItem(ItemInstance item)
	{
		super.onAddItem(item);

		if (item.getItemType() == EtcItemType.RUNE)
		{
			_listeners.onEquip(-1, item);
		}

		if (item.isTemporalItem())
		{
			item.startTimer(new LifeTimeTask(item));
		}

		if (item.isCursed())
		{
			CursedWeaponsManager.getInstance().checkPlayer(getActor(), item);
		}
	}

	@Override
	protected void onRemoveItem(ItemInstance item)
	{
		super.onRemoveItem(item);

		getActor().removeItemFromShortCut(item.getObjectId());

		if (item.getItemType() == EtcItemType.RUNE)
		{
			_listeners.onUnequip(-1, item);
		}

		if (item.isTemporalItem())
		{
			item.stopTimer();
		}
	}

	@Override
	protected void onEquip(int slot, ItemInstance item)
	{
		super.onEquip(slot, item);

		// Synerge - When a item is equipped, call the listener on each zone the player is in
		for (Zone zone : getActor().getZones())
		{
			zone.onEquipChanged(getActor());
		}

		if (item.isShadowItem())
		{
			item.startTimer(new ShadowLifeTimeTask(item));
		}
	}

	@Override
	protected void onUnequip(int slot, ItemInstance item)
	{
		super.onUnequip(slot, item);

		if (item.isShadowItem())
		{
			item.stopTimer();
		}
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
			CollectionUtils.eqSort(_items, ItemOrderComparator.getInstance());

			items = _itemsDAO.getItemsByOwnerIdAndLoc(ownerId, getEquipLocation());

			for (ItemInstance item : items)
			{
				_items.add(item);
				onRestoreItem(item);
				if (item.getEquipSlot() >= PAPERDOLL_MAX)
				{
					// Invalid slot - item returned to inventory.
					item.setLocation(getBaseLocation());
					item.setLocData(0); // A bit ugly, but all the equipment is not loaded and can not find a free slot
					item.setEquipped(false);
					continue;
				}
				setPaperdollItem(item.getEquipSlot(), item);
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

	@Override
	protected void sendAddItem(ItemInstance item)
	{
		Player actor = getActor();

		actor.sendPacket(new InventoryUpdate().addNewItem(item));
		if (item.getTemplate().getAgathionEnergy() > 0)
		{
			actor.sendPacket(new ExBR_AgathionEnergyInfo(1, item));
		}
	}

	@Override
	protected void sendModifyItem(ItemInstance item)
	{
		Player actor = getActor();

		actor.sendPacket(new InventoryUpdate().addModifiedItem(item));
		if (item.getTemplate().getAgathionEnergy() > 0)
		{
			actor.sendPacket(new ExBR_AgathionEnergyInfo(1, item));
		}
	}

	@Override
	protected void sendRemoveItem(ItemInstance item)
	{
		getActor().sendPacket(new InventoryUpdate().addRemovedItem(item));
	}

	public boolean destroyItem(ItemInstance item, long count, String log)
	{
		return destroyItem(item, count, _owner.toString(), log);
	}

	public boolean destroyItem(ItemInstance item, String log)
	{
		return destroyItem(item, _owner.toString(), log);
	}

	public boolean destroyItemByItemId(int itemId, long count, String log)
	{
		return destroyItemByItemId(itemId, count, _owner.toString(), log);
	}

	public boolean destroyItemByObjectId(int objectId, long count, String log)
	{
		return destroyItemByObjectId(objectId, count, _owner.toString(), log);
	}

	public ItemInstance addItem(ItemInstance item, String log)
	{
		return addItem(item, _owner.toString(), log);
	}

	public ItemInstance addItem(int itemId, long count, String log)
	{
		return addItem(itemId, count, _owner.toString(), log);
	}

	public ItemInstance removeItem(ItemInstance item, long count, String log)
	{
		return removeItem(item, count, _owner.toString(), log);
	}

	public ItemInstance removeItem(ItemInstance item, String log)
	{
		return removeItem(item, _owner.toString(), log);
	}

	public ItemInstance removeItemByItemId(int itemId, long count, String log)
	{
		return removeItemByItemId(itemId, count, _owner.toString(), log);
	}

	public ItemInstance removeItemByObjectId(int objectId, long count, String log)
	{
		return removeItemByObjectId(objectId, count, _owner.toString(), log);
	}

	public void startTimers()
	{

	}

	public void stopAllTimers()
	{
		for (ItemInstance item : getItems())
		{
			if (item.isShadowItem() || item.isTemporalItem())
			{
				item.stopTimer();
			}
		}
	}

	@Override
	protected void onDestroyItem(ItemInstance item)
	{
		// Synerge - If one item of the set for the dress me system is destroyed, then we have to disolve the complete set to avoid problems
		if (item.getVisualItemId() > 0)
		{
			DressArmorData dress = DressArmorHolder.getInstance().getArmorByPartId(item.getVisualItemId());
			if (dress != null)
			{
				for (ItemInstance invItem : getItems())
				{
					if ((invItem.getObjectId() == item.getObjectId()) || (invItem.getVisualItemId() < 1))
					{
						continue;
					}

					if (invItem.getVisualItemId() == dress.getChest() || invItem.getVisualItemId() == dress.getLegs() || invItem.getVisualItemId() == dress.getGloves() || invItem.getVisualItemId() == dress.getFeet())
					{
						invItem.setVisualItemId(0);
						invItem.setJdbcState(JdbcEntityState.UPDATED);
						invItem.update();
					}
				}

				// Refund the price paid for this set so he can pay for it again
				ItemFunctions.addItem(_owner, dress.getPriceId(), dress.getPriceCount(), true, "DressMeRefund");

				// Send message
				_owner.sendPacket(new Say2(_owner.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "DressMe", "You have destroyed a part of a dressMe set, for that you will be refunded with the original price, so you can make it again"));
			}
		}

		super.onDestroyItem(item);
	}

	protected class ShadowLifeTimeTask extends RunnableImpl
	{
		private final ItemInstance item;

		ShadowLifeTimeTask(ItemInstance item)
		{
			this.item = item;
		}

		@Override
		public void runImpl() throws Exception
		{
			Player player = getActor();

			if (!item.isEquipped())
			{
				return;
			}

			int mana;
			synchronized (item)
			{
				item.setLifeTime(item.getLifeTime() - 1);
				mana = item.getShadowLifeTime();
				if (mana <= 0)
				{
					destroyItem(item, "Shadow Life Time End");
				}
			}

			SystemMessage sm = null;
			switch (mana)
			{
			case 10:
				sm = new SystemMessage(SystemMessage.S1S_REMAINING_MANA_IS_NOW_10);
				break;
			case 5:
				sm = new SystemMessage(SystemMessage.S1S_REMAINING_MANA_IS_NOW_5);
				break;
			case 1:
				sm = new SystemMessage(SystemMessage.S1S_REMAINING_MANA_IS_NOW_1_IT_WILL_DISAPPEAR_SOON);
				break;
			default:
				if (mana <= 0)
				{
					sm = new SystemMessage(SystemMessage.S1S_REMAINING_MANA_IS_NOW_0_AND_THE_ITEM_HAS_DISAPPEARED);
				}
				else
				{
					player.sendPacket(new InventoryUpdate().addModifiedItem(item));
				}
				break;
			}

			if (sm != null)
			{
				sm.addItemName(item.getItemId());
				player.sendPacket(sm);
			}
		}
	}

	protected class LifeTimeTask extends RunnableImpl
	{
		private final ItemInstance item;

		LifeTimeTask(ItemInstance item)
		{
			this.item = item;
		}

		@Override
		public void runImpl() throws Exception
		{
			Player player = getActor();

			int left;
			synchronized (item)
			{
				left = item.getTemporalLifeTime();
				if (left <= 0)
				{
					destroyItem(item, "Life Time End");
				}
			}

			if (left <= 0)
			{
				player.sendPacket(new SystemMessage(SystemMessage.THE_LIMITED_TIME_ITEM_HAS_BEEN_DELETED).addItemName(item.getItemId()));
			}
		}
	}

	// Synerge - Vars to check when visual ids for items of dressme must be used. Only when the set is complete
	private boolean _mustShowDressMe = false;

	public void setMustShowDressMe(boolean val)
	{
		_mustShowDressMe = val;
	}

	public boolean mustShowDressMe()
	{
		return _mustShowDressMe;
	}

	/**
	 * @return Returns true if all the armor items equipped in the player are from a dress me set. Doesnt check if they are all from the same set, that is done on another place
	 */
	public boolean hasAllDressMeItemsEquipped()
	{
		final ItemInstance chestItem = getPaperdollItem(Inventory.PAPERDOLL_CHEST);
		final ItemInstance legsItem = getPaperdollItem(Inventory.PAPERDOLL_LEGS);
		final ItemInstance glovesItem = getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
		final ItemInstance feetItem = getPaperdollItem(Inventory.PAPERDOLL_FEET);

		if (chestItem == null || legsItem == null || glovesItem == null || feetItem == null)
		{
			return false;
		}

		if (chestItem.getVisualItemId() != 0 && legsItem.getVisualItemId() != 0 && glovesItem.getVisualItemId() != 0 && feetItem.getVisualItemId() != 0)
		{
			return true;
		}

		/*
		 * DressArmorData dress = DressArmorHolder.getInstance().getArmorByPartId(glovesItem.getVisualItemId() != 0 ? glovesItem.getVisualItemId() : feetItem.getVisualItemId());
		 * if (dress == null)
		 * return false;
		 * if (chestItem.getVisualItemId() == 0)
		 * {
		 * chestItem.setVisualItemId(dress.getChest());
		 * chestItem.setJdbcState(JdbcEntityState.UPDATED);
		 * chestItem.update();
		 * }
		 * if (legsItem.getVisualItemId() == 0)
		 * {
		 * legsItem.setVisualItemId(dress.getLegs());
		 * legsItem.setJdbcState(JdbcEntityState.UPDATED);
		 * legsItem.update();
		 * }
		 * if (glovesItem.getVisualItemId() == 0)
		 * {
		 * glovesItem.setVisualItemId(dress.getGloves());
		 * glovesItem.setJdbcState(JdbcEntityState.UPDATED);
		 * glovesItem.update();
		 * }
		 * if (feetItem.getVisualItemId() == 0)
		 * {
		 * feetItem.setVisualItemId(dress.getFeet());
		 * feetItem.setJdbcState(JdbcEntityState.UPDATED);
		 * feetItem.update();
		 * }
		 */

		return false;
	}
}