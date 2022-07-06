package l2mv.gameserver.model.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.napile.primitive.Containers;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;

import l2mv.commons.dao.JdbcEntity;
import l2mv.commons.dao.JdbcEntityState;
import l2mv.gameserver.Config;
import l2mv.gameserver.ai.CtrlIntention;
import l2mv.gameserver.dao.ItemsDAO;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.geodata.GeoEngine;
import l2mv.gameserver.instancemanager.CursedWeaponsManager;
import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.Element;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.items.attachment.FlagItemAttachment;
import l2mv.gameserver.model.items.attachment.ItemAttachment;
import l2mv.gameserver.model.items.listeners.ItemEnchantOptionsListener;
import l2mv.gameserver.network.serverpackets.DropItem;
import l2mv.gameserver.network.serverpackets.L2GameServerPacket;
import l2mv.gameserver.network.serverpackets.SpawnItem;
import l2mv.gameserver.scripts.Events;
import l2mv.gameserver.stats.Env;
import l2mv.gameserver.stats.Stats;
import l2mv.gameserver.stats.funcs.Func;
import l2mv.gameserver.stats.funcs.FuncTemplate;
import l2mv.gameserver.tables.PetDataTable;
import l2mv.gameserver.taskmanager.ItemsAutoDestroy;
import l2mv.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.templates.item.ItemTemplate.Grade;
import l2mv.gameserver.templates.item.ItemTemplate.ItemClass;
import l2mv.gameserver.templates.item.ItemType;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.Location;

public final class ItemInstance extends GameObject implements JdbcEntity
{
	public static final int[] EMPTY_AUGMENTATIONS = new int[2];
	public static final int[] EMPTY_ENCHANT_OPTIONS = new int[3];

	private static final long serialVersionUID = 3162753878915133228L;

	private static final ItemsDAO _itemsDAO = ItemsDAO.getInstance();

	/** Enumeration of locations for item */
	public static enum ItemLocation
	{
		VOID, INVENTORY, PAPERDOLL, PET_INVENTORY, PET_PAPERDOLL, WAREHOUSE, CLANWH, FREIGHT, // restored used Dimension Manager
		@Deprecated
		LEASE, MAIL, AUCTION
	}

	public static final int CHARGED_NONE = 0;
	public static final int CHARGED_SOULSHOT = 1;
	public static final int CHARGED_SPIRITSHOT = 1;
	public static final int CHARGED_BLESSED_SPIRITSHOT = 2;

	public static final int FLAG_NO_DROP = 1 << 0;
	public static final int FLAG_NO_TRADE = 1 << 1;
	public static final int FLAG_NO_TRANSFER = 1 << 2;
	public static final int FLAG_NO_CRYSTALLIZE = 1 << 3;
	public static final int FLAG_NO_ENCHANT = 1 << 4;
	public static final int FLAG_NO_DESTROY = 1 << 5;
	public static final int FLAG_NO_UNEQUIP = 1 << 6;
	// public static final int FLAG_ALWAYS_DROP_ON_DIE = 1 << 7;
	public static final int FLAG_EQUIP_ON_PICKUP = 1 << 7;
	// public static final int FLAG_NO_RIDER_PICKUP = 1 << 9;
	// public static final int FLAG_PET_EQUIPPED = 1 << 10;

	/** ID of the owner */
	private int ownerId;
	/** ID of the item */
	private int itemId;
	private int visualItemId = 0;
	/** Quantity of the item */
	private long count;
	/** Level of enchantment of the item */
	private int enchantLevel = -1;
	/** Location of the item */
	private ItemLocation loc;
	/** Slot where item is stored */
	private int locData;
	/** Custom item types (used loto, race tickets) */
	private int customType1;
	private int customType2;
	/** Время жизни временных вещей */
	private int lifeTime;
	/** Спецфлаги для конкретного инстанса */
	private int customFlags;
	/** Атрибуты вещи */
	private ItemAttributes attrs = new ItemAttributes();
	/** Аугментация вещи */
	private int[] _enchantOptions = EMPTY_ENCHANT_OPTIONS;

	/** Object L2Item associated to the item */
	private ItemTemplate template;
	/** Флаг, что вещь одета, выставляется в инвентаре **/
	private boolean isEquipped;

	/** Item drop time for autodestroy task */
	private long timeToDeleteAfterDrop;

	private IntSet _dropPlayers = Containers.EMPTY_INT_SET;
	private long _dropTimeOwner;

	private int _chargedSoulshot = CHARGED_NONE;
	private int _chargedSpiritshot = CHARGED_NONE;

	private boolean _chargedFishtshot = false;
	private int _augmentationId;
	private int _agathionEnergy;
	private int[] _augmentations = EMPTY_AUGMENTATIONS;

	private ItemAttachment _attachment;
	private JdbcEntityState _state = JdbcEntityState.CREATED;

	public ItemInstance(int objectId)
	{
		super(objectId);
	}

	/**
	 * Constructor<?> of the L2ItemInstance from the objectId and the itemId.
	 * @param objectId : int designating the ID of the object in the world
	 * @param itemId : int designating the ID of the item
	 */
	public ItemInstance(int objectId, int itemId)
	{
		super(objectId);
		setItemId(itemId);
		setLifeTime(getTemplate().isTemporal() ? (int) (System.currentTimeMillis() / 1000L) + getTemplate().getDurability() * 60 : getTemplate().getDurability());
		setAgathionEnergy(getTemplate().getAgathionEnergy());
		setLocData(-1);
		setEnchantLevel(0);
	}

	public int getOwnerId()
	{
		return ownerId;
	}

	public void setOwnerId(int ownerId)
	{
		this.ownerId = ownerId;
	}

	public int getItemId()
	{
		return itemId;
	}

	public void setItemId(int id)
	{
		itemId = id;
		template = ItemHolder.getInstance().getTemplate(id);
		setCustomFlags(getCustomFlags());
	}

	public long getCount()
	{
		return count;
	}

	public void setCount(long count)
	{
		if (count < 0)
		{
			count = 0;
		}

		if (!isStackable() && count > 1L)
		{
			this.count = 1L;
			return;
		}

		this.count = count;
	}

	public int getEnchantLevel()
	{
		return enchantLevel;
	}

	public void setEnchantLevel(int enchantLevel)
	{
		final int old = this.enchantLevel;

		this.enchantLevel = enchantLevel;

		if (old != this.enchantLevel && getTemplate().getEnchantOptions().size() > 0)
		{
			Player player = GameObjectsStorage.getPlayer(ownerId);

			if (isEquipped() && player != null)
			{
				ItemEnchantOptionsListener.getInstance().onUnequip(getEquipSlot(), this, player);
			}

			int[] enchantOptions = getTemplate().getEnchantOptions().get(this.enchantLevel);

			_enchantOptions = enchantOptions == null ? EMPTY_ENCHANT_OPTIONS : enchantOptions;

			if (isEquipped() && player != null)
			{
				ItemEnchantOptionsListener.getInstance().onEquip(getEquipSlot(), this, player);
			}
		}
	}

	public void setLocName(String loc)
	{
		this.loc = ItemLocation.valueOf(loc);
	}

	public String getLocName()
	{
		return loc.name();
	}

	public void setLocation(ItemLocation loc)
	{
		this.loc = loc;
	}

	public ItemLocation getLocation()
	{
		return loc;
	}

	public void setLocData(int locData)
	{
		this.locData = locData;
	}

	public int getLocData()
	{
		return locData;
	}

	public int getCustomType1()
	{
		return customType1;
	}

	public void setCustomType1(int newtype)
	{
		customType1 = newtype;
	}

	public int getCustomType2()
	{
		return customType2;
	}

	public void setCustomType2(int newtype)
	{
		customType2 = newtype;
	}

	public int getLifeTime()
	{
		return lifeTime;
	}

	public void setLifeTime(int lifeTime)
	{
		this.lifeTime = Math.max(0, lifeTime);
	}

	public int getCustomFlags()
	{
		return customFlags;
	}

	public void setCustomFlags(int flags)
	{
		customFlags = flags;
	}

	public ItemAttributes getAttributes()
	{
		return attrs;
	}

	public void setAttributes(ItemAttributes attrs)
	{
		this.attrs = attrs;
	}

	public int getShadowLifeTime()
	{
		if (!isShadowItem())
		{
			return 0;
		}
		return getLifeTime();
	}

	public int getTemporalLifeTime()
	{
		if (!isTemporalItem())
		{
			return 0;
		}
		return getLifeTime() - (int) (System.currentTimeMillis() / 1000L);
	}

	private ScheduledFuture<?> _timerTask;

	public void startTimer(Runnable r)
	{
		_timerTask = LazyPrecisionTaskManager.getInstance().scheduleAtFixedRate(r, 0, 60000L);
	}

	public void stopTimer()
	{
		if (_timerTask != null)
		{
			_timerTask.cancel(false);
			_timerTask = null;
		}
	}

	/**
	 * Returns if item is equipable
	 * @return boolean
	 */
	public boolean isEquipable()
	{
		if (getAttachment() != null && (getAttachment() instanceof FlagItemAttachment) && !((FlagItemAttachment) getAttachment()).canBeLost())
		{
			return false;
		}

		return template.isEquipable();
	}

	/**
	 * Returns if item is equipped
	 * @return boolean
	 */
	public boolean isEquipped()
	{
		return isEquipped;
	}

	public void setEquipped(boolean isEquipped)
	{
		this.isEquipped = isEquipped;
	}

	public int getBodyPart()
	{
		return template.getBodyPart();
	}

	/**
	 * Returns the slot where the item is stored
	 * @return int
	 */
	public int getEquipSlot()
	{
		return getLocData();
	}

	/**
	 * Returns the characteristics of the item
	 * @return L2Item
	 */
	public ItemTemplate getTemplate()
	{
		return template;
	}

	public void setTimeToDeleteAfterDrop(long time)
	{
		timeToDeleteAfterDrop = time;
	}

	public long getTimeToDeleteAfterDrop()
	{
		return timeToDeleteAfterDrop;
	}

	public long getDropTimeOwner()
	{
		return _dropTimeOwner;
	}

	/**
	 * Returns the type of item
	 * @return Enum
	 */
	public ItemType getItemType()
	{
		return template.getItemType();
	}

	public boolean isArmor()
	{
		return template.isArmor();
	}

	public boolean isAccessory()
	{
		return template.isAccessory();
	}

	public boolean isNoEnchant()
	{
		return template.isNoEnchant();
	}

	public boolean isShieldNoEnchant()
	{
		return template.isShieldNoEnchant();
	}

	public boolean isSigelNoEnchant()
	{
		return template.isSigelNoEnchant();
	}

	public boolean isWeapon()
	{
		return template.isWeapon();
	}

	public boolean isNotAugmented()
	{
		return template.isNotAugmented();
	}

	public boolean isArrow()
	{
		return template.isArrow();
	}

	public boolean isUnderwear()
	{
		return template.isUnderwear();
	}

	/**
	 * Returns the reference price of the item
	 * @return int
	 */
	public int getReferencePrice()
	{
		return template.getReferencePrice();
	}

	/**
	 * Returns if item is stackable
	 * @return boolean
	 */
	public boolean isStackable()
	{
		return template.isStackable();
	}

	@Override
	public void onAction(Player player, boolean shift)
	{
		if (Events.onAction(player, this, shift) || (player.isCursedWeaponEquipped() && CursedWeaponsManager.getInstance().isCursed(itemId)))
		{
			return;
		}

		player.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, this, null);
	}

	public boolean isAugmented()
	{
		return getAugmentationId() != 0;
	}

//	public boolean isAugmented()
//	{
//		return _augmentationId != 0;
//	}
	public int getAugmentationId()
	{
		return _augmentationId;
	}

	public void setAugmentationId(int val)
	{
		_augmentationId = val;
	}

	/**
	 * Returns the type of charge with SoulShot of the item.
	 * @return int (CHARGED_NONE, CHARGED_SOULSHOT)
	 */
	public int getChargedSoulshot()
	{
		return _chargedSoulshot;
	}

	/**
	 * Returns the type of charge with SpiritShot of the item
	 * @return int (CHARGED_NONE, CHARGED_SPIRITSHOT, CHARGED_BLESSED_SPIRITSHOT)
	 */
	public int getChargedSpiritshot()
	{
		return _chargedSpiritshot;
	}

	public boolean getChargedFishshot()
	{
		return _chargedFishtshot;
	}

	/**
	 * Sets the type of charge with SoulShot of the item
	 * @param type : int (CHARGED_NONE, CHARGED_SOULSHOT)
	 */
	public void setChargedSoulshot(int type)
	{
		_chargedSoulshot = type;
	}

	/**
	 * Sets the type of charge with SpiritShot of the item
	 * @param type : int (CHARGED_NONE, CHARGED_SPIRITSHOT, CHARGED_BLESSED_SPIRITSHOT)
	 */
	public void setChargedSpiritshot(int type)
	{
		_chargedSpiritshot = type;
	}

	public void setChargedFishshot(boolean type)
	{
		_chargedFishtshot = type;
	}

	public class FuncAttack extends Func
	{
		private final Element element;

		public FuncAttack(Element element, int order, Object owner)
		{
			super(element.getAttack(), order, owner);
			this.element = element;
		}

		@Override
		public void calc(Env env)
		{
			env.value += getAttributeElementValue(element, true);
		}
	}

	public class FuncDefence extends Func
	{
		private final Element element;

		public FuncDefence(Element element, int order, Object owner)
		{
			super(element.getDefence(), order, owner);
			this.element = element;
		}

		@Override
		public void calc(Env env)
		{
			env.value += getAttributeElementValue(element, true);
		}
	}

	/**
	 * This function basically returns a set of functions from
	 * L2Item/L2Armor/L2Weapon, but may add additional
	 * functions, if this particular item instance is enhanched
	 * for a particular player.
	 * @return Func[]
	 */
	public Func[] getStatFuncs()
	{
		Func[] result = Func.EMPTY_FUNC_ARRAY;

		List<Func> funcs = new ArrayList<>();

		if (template.getAttachedFuncs().length > 0)
		{
			for (FuncTemplate t : template.getAttachedFuncs())
			{
				Func f = t.getFunc(this);
				if (f != null)
				{
					funcs.add(f);
				}
			}
		}

		for (Element e : Element.VALUES)
		{
			if (isWeapon())
			{
				funcs.add(new FuncAttack(e, 0x40, this));
			}
			if (isArmor())
			{
				funcs.add(new FuncDefence(e, 0x40, this));
			}
		}

		if (!funcs.isEmpty())
		{
			result = funcs.toArray(new Func[funcs.size()]);
		}

		return result;
	}

	/**
	 * Return true if item is hero-item
	 * @return boolean
	 */
	public boolean isHeroWeapon()
	{
		return template.isHeroWeapon();
	}

	/**
	 * Return true if item can be destroyed
	 * @param player
	 * @return
	 */
	public boolean canBeDestroyed(Player player)
	{
		if (((customFlags & FLAG_NO_DESTROY) == FLAG_NO_DESTROY) || isHeroWeapon() || (PetDataTable.isPetControlItem(this) && player.isMounted()))
		{
			return false;
		}

		if ((player.getPetControlItem() == this) || (player.getEnchantScroll() == this) || isCursed())
		{
			return false;
		}

		if ((getAttachment() != null && (getAttachment() instanceof FlagItemAttachment) && !((FlagItemAttachment) getAttachment()).canBeLost()) || !player.getPermissions().canLoseItem(this, false))
		{
			return false;
		}

		return template.isDestroyable();
	}

	/**
	 * Return true if item can be dropped
	 * @param player
	 * @param pk
	 * @return
	 */
	public boolean canBeDropped(Player player, boolean pk)
	{
		if (player.isGM())
		{
			return true;
		}

		if (((customFlags & FLAG_NO_DROP) == FLAG_NO_DROP) || isShadowItem() || isTemporalItem())
		{
			return false;
		}

		if (isAugmented() && (!pk || !Config.DROP_ITEMS_AUGMENTED) && !Config.ALT_ALLOW_DROP_AUGMENTED)
		{
			return false;
		}

		if (!ItemFunctions.checkIfCanDiscard(player, this) || !template.isDropable())
		{
			return false;
		}

		if ((getAttachment() != null && (getAttachment() instanceof FlagItemAttachment) && !((FlagItemAttachment) getAttachment()).canBeLost()) || !player.getPermissions().canLoseItem(this, false))
		{
			return false;
		}

		return true;
	}

	public boolean canBeTraded(Player player)
	{
		if (isEquipped())
		{
			return false;
		}

		if (player.isGM())
		{
			return true;
		}

		if (((customFlags & FLAG_NO_TRADE) == FLAG_NO_TRADE) || isShadowItem() || isTemporalItem())
		{
			return false;
		}

		if ((isAugmented() && !Config.ALT_ALLOW_DROP_AUGMENTED) || !ItemFunctions.checkIfCanDiscard(player, this))
		{
			return false;
		}

		if ((!template.isTradeable() && !Config.CAN_BE_TRADED_NO_TARADEABLE) || (!template.isSellable() && !Config.CAN_BE_TRADED_NO_SELLABLE))
		{
			return false;
		}

		if ((!template.isStoreable() && !Config.CAN_BE_TRADED_NO_STOREABLE) || (isShadowItem() && !Config.CAN_BE_TRADED_SHADOW_ITEM))
		{
			return false;
		}

		if (isHeroWeapon() && !Config.CAN_BE_TRADED_HERO_WEAPON)
		{
			return false;
		}

		if (getAttachment() != null && (getAttachment() instanceof FlagItemAttachment) && !((FlagItemAttachment) getAttachment()).canBeLost())
		{
			return false;
		}

		if (!player.getPermissions().canLoseItem(this, false))
		{
			return false;
		}

		return true;
	}

	/**
	 * Можно ли продать в магазин NPC
	 * @param player
	 * @return
	 */
	public boolean canBeSold(Player player)
	{
		if (((customFlags & FLAG_NO_DESTROY) == FLAG_NO_DESTROY) || (getItemId() == ItemTemplate.ITEM_ID_ADENA) || (template.getReferencePrice() == 0) || isShadowItem())
		{
			return false;
		}

		if (isTemporalItem() || (isAugmented() && !Config.ALT_ALLOW_DROP_AUGMENTED) || isEquipped())
		{
			return false;
		}

		if (!ItemFunctions.checkIfCanDiscard(player, this) || !template.isTradeable() || !template.isSellable() || !template.isStoreable())
		{
			return false;
		}

		if ((getAttachment() != null && (getAttachment() instanceof FlagItemAttachment) && !((FlagItemAttachment) getAttachment()).canBeLost()) || !player.getPermissions().canLoseItem(this, false))
		{
			return false;
		}

		return true;
	}

	/**
	 * Можно ли положить на клановый склад
	 * @param player
	 * @param privatewh
	 * @return
	 */
	public boolean canBeStored(Player player, boolean privatewh)
	{
		if (((customFlags & FLAG_NO_TRANSFER) == FLAG_NO_TRANSFER) || !getTemplate().isStoreable())
		{
			return false;
		}

		if (!privatewh && (isShadowItem() || isTemporalItem()))
		{
			return false;
		}

		if ((!privatewh && isAugmented() && !Config.ALT_ALLOW_DROP_AUGMENTED) || isEquipped())
		{
			return false;
		}

		if (!ItemFunctions.checkIfCanDiscard(player, this) || (!privatewh && isAugmented() && !Config.CAN_BE_CWH_IS_AUGMENTED))
		{
			return false;
		}

		if ((getAttachment() != null && (getAttachment() instanceof FlagItemAttachment) && !((FlagItemAttachment) getAttachment()).canBeLost()) || !player.getPermissions().canLoseItem(this, false))
		{
			return false;
		}

		return privatewh || template.isTradeable();
	}

	public boolean canBeCrystallized(Player player)
	{
		if (((customFlags & FLAG_NO_CRYSTALLIZE) == FLAG_NO_CRYSTALLIZE) || isShadowItem() || isTemporalItem() || !ItemFunctions.checkIfCanDiscard(player, this))
		{
			return false;
		}

		if ((getAttachment() != null && (getAttachment() instanceof FlagItemAttachment) && !((FlagItemAttachment) getAttachment()).canBeLost()) || !player.getPermissions().canLoseItem(this, false))
		{
			return false;
		}

		return template.isCrystallizable();
	}

	public boolean canBeEnchanted(boolean gradeCheck)
	{
		if ((customFlags & FLAG_NO_ENCHANT) == FLAG_NO_ENCHANT)
		{
			return false;
		}

		return template.canBeEnchanted(gradeCheck);
	}

	public boolean canBeAugmented(Player player, boolean isAccessoryLifeStone)
	{
		if (!canBeEnchanted(true) || isAugmented() || isCommonItem() || isTerritoryAccessory())
		{
			return false;
		}

		if ((getTemplate().getItemGrade().ordinal() < Grade.C.ordinal()) || !getTemplate().isAugmentable())
		{
			return false;
		}

		if (isAccessory())
		{
			return isAccessoryLifeStone;
		}

		if (isArmor())
		{
			return Config.ALT_ALLOW_AUGMENT_ALL;
		}

		if (isWeapon())
		{
			return !isAccessoryLifeStone;
		}

		return true;
	}

	public boolean canBeAugmented()
	{
		if (!canBeEnchanted(true) || isAugmented() || isCommonItem() || isTerritoryAccessory())
		{
			return false;
		}
		if ((getTemplate().getItemGrade().ordinal() < Grade.C.ordinal()) || !getTemplate().isAugmentable() || isAccessory())
		{
			return false;
		}
		if (isArmor())
		{
			return Config.ALT_ALLOW_AUGMENT_ALL;
		}
		return true;
	}

	public boolean canBeExchanged(Player player)
	{
		if (((customFlags & FLAG_NO_DESTROY) == FLAG_NO_DESTROY) || isShadowItem() || isHeroWeapon() || isTemporalItem())
		{
			return false;
		}

		if (!ItemFunctions.checkIfCanDiscard(player, this) || (getAttachment() != null && (getAttachment() instanceof FlagItemAttachment) && !((FlagItemAttachment) getAttachment()).canBeLost()))
		{
			return false;
		}

		if (!player.getPermissions().canLoseItem(this, false))
		{
			return false;
		}

		return template.isDestroyable();
	}

	public boolean isTerritoryAccessory()
	{
		return template.isTerritoryAccessory();
	}

	public boolean isShadowItem()
	{
		return template.isShadowItem();
	}

	public boolean isTemporalItem()
	{
		return template.isTemporal();
	}

	public boolean isCommonItem()
	{
		return template.isCommonItem();
	}

	public boolean isAltSeed()
	{
		return template.isAltSeed();
	}

	public boolean isAdena()
	{
		return template.isAdena();
	}

	public boolean isCursed()
	{
		return template.isCursed();
	}

	/**
	 * Бросает на землю лут с NPC
	 * @param lastAttacker
	 * @param fromNpc
	 */
	public void dropToTheGround(Player lastAttacker, NpcInstance fromNpc)
	{
		Creature dropper = fromNpc;
		if (dropper == null)
		{
			dropper = lastAttacker;
		}

		Location pos = Location.findAroundPosition(dropper, 100);

		// activate non owner penalty
		if (lastAttacker != null) // lastAttacker в данном случае top damager
		{
			_dropPlayers = new HashIntSet(1, 2);
			for (Player $member : lastAttacker.getPlayerGroup())
			{
				_dropPlayers.add($member.getObjectId());
			}

			_dropTimeOwner = System.currentTimeMillis() + (fromNpc != null && fromNpc.isRaid() ? Config.NONOWNER_ITEM_PICKUP_DELAY_RAIDS : Config.NONOWNER_ITEM_PICKUP_DELAY);
		}

		// Init the dropped L2ItemInstance and add it in the world as a visible object at the position where mob was last
		dropMe(dropper, pos);

		// Add drop to auto destroy item task
		if (isHerb())
		{
			ItemsAutoDestroy.getInstance().addHerb(this);
		}
		else if (Config.AUTODESTROY_ITEM_AFTER > 0 && !isCursed() && _attachment == null)
		{
			ItemsAutoDestroy.getInstance().addItem(this, Config.AUTODESTROY_ITEM_AFTER * 1000L);
		}
	}

	/**
	 * Бросает вещь на землю туда, где ее можно поднять
	 * @param dropper
	 * @param dropPos
	 */
	public void dropToTheGround(Creature dropper, Location dropPos)
	{
		if (GeoEngine.canMoveToCoord(dropper.getX(), dropper.getY(), dropper.getZ(), dropPos.x, dropPos.y, dropPos.z, dropper.getGeoIndex()))
		{
			dropMe(dropper, dropPos);
		}
		else
		{
			dropMe(dropper, dropper.getLoc());
		}
	}

	/**
	 * Бросает вещь на землю из инвентаря туда, где ее можно поднять
	 * @param dropper
	 * @param dropPos
	 */
	public void dropToTheGround(Playable dropper, Location dropPos)
	{
		setLocation(ItemLocation.VOID);
		if (getJdbcState().isPersisted())
		{
			setJdbcState(JdbcEntityState.UPDATED);
			update();
		}

		if (GeoEngine.canMoveToCoord(dropper.getX(), dropper.getY(), dropper.getZ(), dropPos.x, dropPos.y, dropPos.z, dropper.getGeoIndex()))
		{
			dropMe(dropper, dropPos);
		}
		else
		{
			dropMe(dropper, dropper.getLoc());
		}

		// Add drop to auto destroy item task from player items.
		if (Config.AUTODESTROY_PLAYER_ITEM_AFTER > 0 && _attachment == null)
		{
			ItemsAutoDestroy.getInstance().addItem(this, Config.AUTODESTROY_PLAYER_ITEM_AFTER * 1000L);
		}
	}

	/**
	 * Init a dropped L2ItemInstance and add it in the world as a visible object.<BR><BR>
	 *
	 * <B><U> Actions</U> :</B><BR><BR>
	 * <li>Set the x,y,z position of the L2ItemInstance dropped and update its _worldregion </li>
	 * <li>Add the L2ItemInstance dropped to _visibleObjects of its L2WorldRegion</li>
	 * <li>Add the L2ItemInstance dropped in the world as a <B>visible</B> object</li><BR><BR>
	 *
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T ADD the object to _allObjects of L2World </B></FONT><BR><BR>
	 *
	 * <B><U> Assert </U> :</B><BR><BR>
	 * <li> this instanceof L2ItemInstance</li>
	 * <li> _worldRegion == null <I>(L2Object is invisible at the beginning)</I></li><BR><BR>
	 *
	 * <B><U> Example of use </U> :</B><BR><BR>
	 * <li> Drop item</li>
	 * <li> Call Pet</li><BR>
	 *
	 * @param dropper Char that dropped item
	 * @param loc drop coordinates
	 */
	public void dropMe(Creature dropper, Location loc)
	{
		if (dropper != null)
		{
			setReflection(dropper.getReflection());
		}

		spawnMe0(loc, dropper);
		if (isHerb())
		{
			ItemsAutoDestroy.getInstance().addHerb(this);
		}
		else if ((Config.AUTODESTROY_ITEM_AFTER > 0) && (!isCursed()))
		{
			ItemsAutoDestroy.getInstance().addItem(this, 100000L);
		}
	}

	public final void pickupMe()
	{
		decayMe();
		setReflection(ReflectionManager.DEFAULT);
	}

	public ItemClass getItemClass()
	{
		return template.getItemClass();
	}

	/**
	 * Возвращает защиту от элемента.
	 * @param element
	 * @return значение защиты
	 */
	private int getDefence(Element element)
	{
		return isArmor() ? getAttributeElementValue(element, true) : 0;
	}

	/**
	 * Возвращает защиту от элемента: огонь.
	 * @return значение защиты
	 */
	public int getDefenceFire()
	{
		return getDefence(Element.FIRE);
	}

	/**
	 * Возвращает защиту от элемента: вода.
	 * @return значение защиты
	 */
	public int getDefenceWater()
	{
		return getDefence(Element.WATER);
	}

	/**
	 * Возвращает защиту от элемента: воздух.
	 * @return значение защиты
	 */
	public int getDefenceWind()
	{
		return getDefence(Element.WIND);
	}

	/**
	 * Возвращает защиту от элемента: земля.
	 * @return значение защиты
	 */
	public int getDefenceEarth()
	{
		return getDefence(Element.EARTH);
	}

	/**
	 * Возвращает защиту от элемента: свет.
	 * @return значение защиты
	 */
	public int getDefenceHoly()
	{
		return getDefence(Element.HOLY);
	}

	/**
	 * Возвращает защиту от элемента: тьма.
	 * @return значение защиты
	 */
	public int getDefenceUnholy()
	{
		return getDefence(Element.UNHOLY);
	}

	/**
	 * Возвращает значение элемента.
	 * @param element
	 * @param withBase
	 * @return
	 */
	public int getAttributeElementValue(Element element, boolean withBase)
	{
		return attrs.getValue(element) + (withBase ? template.getBaseAttributeValue(element) : 0);
	}

	/**
	 * Возвращает элемент атрибуции предмета.<br>
	 * @return
	 */
	public Element getAttributeElement()
	{
		return attrs.getElement();
	}

	public int getAttributeElementValue()
	{
		return attrs.getValue();
	}

	public Element getAttackElement()
	{
		Element element = isWeapon() ? getAttributeElement() : Element.NONE;
		if (element == Element.NONE)
		{
			for (Element e : Element.VALUES)
			{
				if (template.getBaseAttributeValue(e) > 0)
				{
					return e;
				}
			}
		}
		return element;
	}

	public int getAttackElementValue()
	{
		return isWeapon() ? getAttributeElementValue(getAttackElement(), true) : 0;
	}

	/**
	 * Устанавливает элемент атрибуции предмета.<br>
	 * Element (0 - Fire, 1 - Water, 2 - Wind, 3 - Earth, 4 - Holy, 5 - Dark, -1 - None)
	 * @param element элемент
	 * @param value
	 */
	public void setAttributeElement(Element element, int value)
	{
		attrs.setValue(element, value);
	}

	/**
	 * Проверяет, является ли данный инстанс предмета хербом
	 * @return true если предмет является хербом
	 */
	public boolean isHerb()
	{
		if (getTemplate().isHerb())
		{
			return true;
		}
		return false;
	}

	public Grade getCrystalType()
	{
		return template.getCrystalType();
	}

	@Override
	public String getName()
	{
		return getTemplate().getName();
	}

	@Override
	public void save()
	{
		_itemsDAO.save(this);
	}

	@Override
	public void update()
	{
		_itemsDAO.update(this);
	}

	@Override
	public void delete()
	{
		_itemsDAO.delete(this);
	}

	@Override
	public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper)
	{
		L2GameServerPacket packet = null;
		if (dropper != null)
		{
			packet = new DropItem(this, dropper.getObjectId());
		}
		else
		{
			packet = new SpawnItem(this);
		}

		return Collections.singletonList(packet);
	}

	/**
	 * Returns the item in String format
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(getTemplate().getItemId());
		sb.append(" ");
		if (getEnchantLevel() > 0)
		{
			sb.append("+");
			sb.append(getEnchantLevel());
			sb.append(" ");
		}
		sb.append(getTemplate().getName());
		if (!getTemplate().getAdditionalName().isEmpty())
		{
			sb.append(" ");
			sb.append("\\").append(getTemplate().getAdditionalName()).append("\\");
		}
		sb.append(" ");
		sb.append("(");
		sb.append(getCount());
		sb.append(")");
		sb.append("[");
		sb.append(getObjectId());
		sb.append("]");

		return sb.toString();

	}

	@Override
	public void setJdbcState(JdbcEntityState state)
	{
		_state = state;
	}

	@Override
	public JdbcEntityState getJdbcState()
	{
		return _state;
	}

	@Override
	public boolean isItem()
	{
		return true;
	}

	public ItemAttachment getAttachment()
	{
		return _attachment;
	}

	public void setAttachment(ItemAttachment attachment)
	{
		ItemAttachment old = _attachment;
		_attachment = attachment;
		if (_attachment != null)
		{
			_attachment.setItem(this);
		}
		if (old != null)
		{
			old.setItem(null);
		}
	}

	public int getAgathionEnergy()
	{
		return _agathionEnergy;
	}

	public void setAgathionEnergy(int agathionEnergy)
	{
		_agathionEnergy = agathionEnergy;
	}

	public int[] getEnchantOptions()
	{
		return _enchantOptions;
	}

	public IntSet getDropPlayers()
	{
		return _dropPlayers;
	}

	/**
	 * sending item stat like pAtk or mDef
	 * @param stat
	 * @return
	 */
	public double getStatFunc(Stats stat)
	{
		for (FuncTemplate func : template.getAttachedFuncs())
		{
			if (func._stat == stat)
			{
				return func._value;
			}
		}
		return 0.0;
	}

	/**
	 * @return
	 */
	public int getVisualItemId()
	{
		return visualItemId;
	}

	public void setVisualItemId(int visualItemId)
	{
		this.visualItemId = visualItemId;
	}

	public int getAugmentationMineralId()
	{
		return _augmentationId;
	}

	public void setAugmentation(int mineralId, int[] augmentations)
	{
		_augmentationId = mineralId;
		_augmentations = augmentations;
	}

	public int[] getAugmentations()
	{
		return _augmentations;
	}

	// Savable in db
	private boolean _savableInDatabase = true;

	public void setSavableInDatabase(boolean savableInDatabase)
	{
		_savableInDatabase = savableInDatabase;
	}

	public boolean isSavableInDatabase()
	{
		return _savableInDatabase;
	}
}