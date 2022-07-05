package l2f.gameserver.templates.item;

import java.util.ArrayList;
import java.util.List;

import org.napile.primitive.Containers;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

import l2f.commons.lang.ArrayUtils;
import l2f.commons.time.cron.SchedulingPattern;
import l2f.gameserver.handler.items.IItemHandler;
import l2f.gameserver.instancemanager.CursedWeaponsManager;
import l2f.gameserver.model.Playable;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.base.Element;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.stats.Env;
import l2f.gameserver.stats.StatTemplate;
import l2f.gameserver.stats.conditions.Condition;
import l2f.gameserver.stats.funcs.FuncTemplate;
import l2f.gameserver.templates.StatsSet;
import l2f.gameserver.templates.augmentation.AugmentationInfo;
import l2f.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import l2f.gameserver.templates.item.WeaponTemplate.WeaponType;

public abstract class ItemTemplate extends StatTemplate
{
	public static enum ReuseType
	{
		NORMAL(
					SystemMsg.THERE_ARE_S2_SECONDS_REMAINING_IN_S1S_REUSE_TIME,
					SystemMsg.THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_IN_S1S_REUSE_TIME,
					SystemMsg.THERE_ARE_S2_HOURS_S3_MINUTES_AND_S4_SECONDS_REMAINING_IN_S1S_REUSE_TIME)
		{

			@Override
			public long next(ItemInstance item)
			{
				return System.currentTimeMillis() + item.getTemplate().getReuseDelay();
			}
		},
		EVERY_DAY_AT_6_30(
					SystemMsg.THERE_ARE_S2_SECONDS_REMAINING_FOR_S1S_REUSE_TIME,
					SystemMsg.THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_FOR_S1S_REUSE_TIME,
					SystemMsg.THERE_ARE_S2_HOURS_S3_MINUTES_S4_SECONDS_REMAINING_FOR_S1S_REUSE_TIME)
		{
			private final SchedulingPattern _pattern = new SchedulingPattern("30 6 * * *");

			@Override
			public long next(ItemInstance item)
			{
				return _pattern.next(System.currentTimeMillis());
			}
		};

		private SystemMsg[] _messages;

		ReuseType(SystemMsg... msg)
		{
			_messages = msg;
		}

		public abstract long next(ItemInstance item);

		public SystemMsg[] getMessages()
		{
			return _messages;
		}
	}

	public static enum ItemClass
	{
		ALL, WEAPON, ARMOR, JEWELRY, ACCESSORY,
		/** Soul/Spiritshot, Potions, Scrolls */
		CONSUMABLE,
		/** Common craft matherials */
		MATHERIALS,
		/** Special (item specific) craft matherials */
		PIECES,
		/** Crafting recipies */
		RECIPIES,
		/** Skill learn books */
		SPELLBOOKS,
		/** Dyes, lifestones */
		MISC,

		EXTRACTABLE,
		/** All other */
		OTHER
	}

	public static final int ITEM_ID_PC_BANG_POINTS = -100;
	public static final int ITEM_ID_CLAN_REPUTATION_SCORE = -200;
	public static final int ITEM_ID_FAME = -300;
	public static final int ITEM_ID_ADENA = 57;
	private IntObjectMap<AugmentationInfo> _augmentationInfos = Containers.emptyIntObjectMap();
	/** Item ID для замковых корон */
	public static final int[] ITEM_ID_CASTLE_CIRCLET =
	{
		0, // no castle - no circlet.. :)
		6838, // Circlet of Gludio
		6835, // Circlet of Dion
		6839, // Circlet of Giran
		6837, // Circlet of Oren
		6840, // Circlet of Aden
		6834, // Circlet of Innadril
		6836, // Circlet of Goddard
		8182, // Circlet of Rune
		8183, // Circlet of Schuttgart
	};

	public static final int[] ITEM_ID_CASTLE_CLOAK =
	{
		0, // no castle - no cloak..
		37018, // Cloak of Gludio
		37016, // Cloak of Dion
		37017, // Cloak of Giran
		37021, // Cloak of Oren
		37015, // Cloak of Aden
		37020, // Cloak of Innadril
		37019, // Cloak of Goddard
		37022, // Cloak of Rune
		37023, // Cloak of Schuttgart
	};

	public static final int ITEM_ID_FORMAL_WEAR = 6408;

	// Uniforms
	public static final int ITEM_ID_SCHOOL_UNIFORM_A = 57000;
	public static final int ITEM_ID_SCHOOL_UNIFORM_B = 57001;
	public static final int ITEM_ID_CHRISTMAS_UNIFORM = 57002;
	public static final int ITEM_ID_KNIGHT_UNIFORM = 57003;
	public static final int ITEM_ID_QUIPAO_UNIFORM = 57004;
	public static final int ITEM_ID_NAVI_UNIFORM = 57005;
	public static final int ITEM_ID_PIRAT_UNIFORM = 57007;
	public static final int ITEM_ID_MUSKETEER_UNIFORM = 57008;
	public static final int ITEM_ID_MAGICIAN_UNIFORM = 57009;
	public static final int ITEM_ID_MAGICIAN_UPGRADED_UNIFORM = 57010;
	public static final int ITEM_ID_NINJA_UNIFORM = 57011;
	public static final int ITEM_ID_DARK_ASSASIN_UNIFORM = 57012;
	public static final int ITEM_ID_METAL_UNIFORM = 57013;

	public static final int TYPE1_WEAPON_RING_EARRING_NECKLACE = 0;
	public static final int TYPE1_SHIELD_ARMOR = 1;
	public static final int TYPE1_OTHER = 2;
	public static final int TYPE1_ITEM_QUESTITEM_ADENA = 4;

	public static final int TYPE2_WEAPON = 0;
	public static final int TYPE2_SHIELD_ARMOR = 1;
	public static final int TYPE2_ACCESSORY = 2;
	public static final int TYPE2_QUEST = 3;
	public static final int TYPE2_MONEY = 4;
	public static final int TYPE2_OTHER = 5;
	public static final int TYPE2_PET_WOLF = 6;
	public static final int TYPE2_PET_HATCHLING = 7;
	public static final int TYPE2_PET_STRIDER = 8;
	public static final int TYPE2_NODROP = 9;
	public static final int TYPE2_PET_GWOLF = 10;
	public static final int TYPE2_PENDANT = 11;
	public static final int TYPE2_PET_BABY = 12;

	public static final int SLOT_NONE = 0x00000;
	public static final int SLOT_UNDERWEAR = 0x00001;

	public static final int SLOT_R_EAR = 0x00002;
	public static final int SLOT_L_EAR = 0x00004;

	public static final int SLOT_NECK = 0x00008;

	public static final int SLOT_R_FINGER = 0x00010;
	public static final int SLOT_L_FINGER = 0x00020;

	public static final int SLOT_HEAD = 0x00040;
	public static final int SLOT_R_HAND = 0x00080;
	public static final int SLOT_L_HAND = 0x00100;
	public static final int SLOT_GLOVES = 0x00200;
	public static final int SLOT_CHEST = 0x00400;
	public static final int SLOT_LEGS = 0x00800;
	public static final int SLOT_FEET = 0x01000;
	public static final int SLOT_BACK = 0x02000;
	public static final int SLOT_LR_HAND = 0x04000;
	public static final int SLOT_FULL_ARMOR = 0x08000;
	public static final int SLOT_HAIR = 0x10000;
	public static final int SLOT_FORMAL_WEAR = 0x20000;
	public static final int SLOT_DHAIR = 0x40000;
	public static final int SLOT_HAIRALL = 0x80000;
	public static final int SLOT_R_BRACELET = 0x100000;
	public static final int SLOT_L_BRACELET = 0x200000;
	public static final int SLOT_DECO = 0x400000;
	public static final int SLOT_BELT = 0x10000000;
	public static final int SLOT_WOLF = -100;
	public static final int SLOT_HATCHLING = -101;
	public static final int SLOT_STRIDER = -102;
	public static final int SLOT_BABYPET = -103;
	public static final int SLOT_GWOLF = -104;
	public static final int SLOT_PENDANT = -105;

	// Все слоты, используемые броней.
	public static final int SLOTS_ARMOR = SLOT_HEAD | SLOT_L_HAND | SLOT_GLOVES | SLOT_CHEST | SLOT_LEGS | SLOT_FEET | SLOT_BACK | SLOT_FULL_ARMOR;
	// Все слоты, используемые бижей.
	public static final int SLOTS_JEWELRY = SLOT_R_EAR | SLOT_L_EAR | SLOT_NECK | SLOT_R_FINGER | SLOT_L_FINGER;

	public static final int CRYSTAL_NONE = 0;
	public static final int CRYSTAL_D = 1458;
	public static final int CRYSTAL_C = 1459;
	public static final int CRYSTAL_B = 1460;
	public static final int CRYSTAL_A = 1461;
	public static final int CRYSTAL_S = 1462;

	public static enum Grade
	{
		NONE(CRYSTAL_NONE, 0), D(CRYSTAL_D, 1), C(CRYSTAL_C, 2), B(CRYSTAL_B, 3), A(CRYSTAL_A, 4), S(CRYSTAL_S, 5), S80(CRYSTAL_S, 5), S84(CRYSTAL_S, 5);

		/** ID соответствующего грейду кристалла */
		public final int cry;
		/** ID грейда, без учета уровня S */
		public final int externalOrdinal;

		private Grade(int crystal, int ext)
		{
			cry = crystal;
			externalOrdinal = ext;
		}
	}

	public static final int ATTRIBUTE_NONE = -2;
	public static final int ATTRIBUTE_FIRE = 0;
	public static final int ATTRIBUTE_WATER = 1;
	public static final int ATTRIBUTE_WIND = 2;
	public static final int ATTRIBUTE_EARTH = 3;
	public static final int ATTRIBUTE_HOLY = 4;
	public static final int ATTRIBUTE_DARK = 5;

	protected final int _itemId;
	private final ItemClass _class;
	protected final String _name;
	protected final String _addname;
	protected final String _icon;
	protected final String _icon32;
	protected int _type1; // needed for item list (inventory)
	protected int _type2; // different lists for armor, weapon, etc
	private final int _weight;

	private final boolean _masterwork;
	private final int masterworkConvert;

	protected final Grade _crystalType; // default to none-grade

	private final int _durability;
	protected int _bodyPart;
	private final int _referencePrice;
	private final int _crystalCount;

	private final boolean _temporal;
	private final boolean _stackable;

	private final boolean _crystallizable;
	private int _flags;

	private final ReuseType _reuseType;
	private final int _reuseDelay;
	private final int _reuseGroup;
	private final int _agathionEnergy;

	protected Skill[] _skills;
	private Skill _enchant4Skill = null; // skill that activates when item is enchanted +4 (for duals)
	public ItemType type;

	private int[] _baseAttributes = new int[6];
	private IntObjectMap<int[]> _enchantOptions = Containers.emptyIntObjectMap();
	private final List<CapsuledItem> _capsuledItems = new ArrayList<>();
	private Condition _condition;
	private IItemHandler _handler = IItemHandler.NULL;

	/**
	 * Constructor<?> of the L2Item that fill class variables.<BR>
	 * <BR>
	 * <U><I>Variables filled :</I></U><BR>
	 * <LI>type</LI> <LI>_itemId</LI> <LI>_name</LI> <LI>_type1 & _type2</LI> <LI>_weight</LI> <LI>_crystallizable</LI> <LI>_stackable</LI> <LI>_materialType & _crystalType & _crystlaCount</LI> <LI>_durability</LI> <LI>_bodypart</LI> <LI>_referencePrice</LI> <LI>_sellable</LI>
	 * @param set : StatsSet corresponding to a set of couples (key,value) for description of the item
	 */
	protected ItemTemplate(StatsSet set)
	{
		_itemId = set.getInteger("item_id");
		_class = set.getEnum("class", ItemClass.class, ItemClass.OTHER);
		_name = set.getString("name");
		_addname = set.getString("add_name", "");
		_icon = set.getString("icon", "");
		_icon32 = "<img src=icon." + _icon + " width=32 height=32>";
		_weight = set.getInteger("weight", 0);
		_crystallizable = set.getBool("crystallizable", false);
		_stackable = set.getBool("stackable", false);
		_crystalType = set.getEnum("crystal_type", Grade.class, Grade.NONE); // default to none-grade
		_durability = set.getInteger("durability", -1);
		_temporal = set.getBool("temporal", false);
		_bodyPart = set.getInteger("bodypart", 0);
		_referencePrice = set.getInteger("price", 0);
		_crystalCount = set.getInteger("crystal_count", 0);
		_reuseType = set.getEnum("reuse_type", ReuseType.class, ReuseType.NORMAL);
		_reuseDelay = set.getInteger("reuse_delay", 0);
		_reuseGroup = set.getInteger("delay_share_group", -_itemId);
		_agathionEnergy = set.getInteger("agathion_energy", 0);
		_masterwork = set.getBool("masterwork", false);
		masterworkConvert = set.getInteger("masterwork_convert", -1);

		for (ItemFlags f : ItemFlags.VALUES)
		{
			boolean flag = set.getBool(f.name().toLowerCase(), f.getDefaultValue());
			if (flag)
			{
				activeFlag(f);
			}
		}

		_funcTemplates = FuncTemplate.EMPTY_ARRAY;
		_skills = Skill.EMPTY_ARRAY;
	}

	/**
	 * Returns the itemType.
	 * @return Enum
	 */
	public ItemType getItemType()
	{
		return type;
	}

	public String getIcon()
	{
		return _icon;
	}

	/**
	 * Returns ready for display in the html string of the form <img src=icon.icon width=32 height=32>
	 * @return
	 */
	public String getIcon32()
	{
		return _icon32;
	}

	/**
	 * Returns the durability of the item
	 * @return int
	 */
	public final int getDurability()
	{
		return _durability;
	}

	public final boolean isTemporal()
	{
		return _temporal;
	}

	/**
	 * Returns the ID of the item
	 * @return int
	 */
	public final int getItemId()
	{
		return _itemId;
	}

	public abstract long getItemMask();

	/**
	 * Returns the type 2 of the item
	 * @return int
	 */
	public final int getType2()
	{
		return _type2;
	}

	public final int getBaseAttributeValue(Element element)
	{
		if (element == Element.NONE)
		{
			return 0;
		}
		return _baseAttributes[element.getId()];
	}

	public void setBaseAtributeElements(int[] val)
	{
		_baseAttributes = val;
	}

	public final int getType2ForPackets()
	{
		int type2 = _type2;
		switch (_type2)
		{
		case TYPE2_PET_WOLF:
		case TYPE2_PET_HATCHLING:
		case TYPE2_PET_STRIDER:
		case TYPE2_PET_GWOLF:
		case TYPE2_PET_BABY:
			if (_bodyPart == ItemTemplate.SLOT_CHEST)
			{
				type2 = TYPE2_SHIELD_ARMOR;
			}
			else
			{
				type2 = TYPE2_WEAPON;
			}
			break;
		case TYPE2_PENDANT:
			type2 = TYPE2_ACCESSORY;
			break;
		}
		return type2;
	}

	/**
	 * Returns the weight of the item
	 * @return int
	 */
	public final int getWeight()
	{
		return _weight;
	}

	/**
	 * Returns if the item is crystallizable
	 * @return boolean
	 */
	public final boolean isCrystallizable()
	{
		return _crystallizable && !isStackable() && (getCrystalType() != Grade.NONE) && (getCrystalCount() > 0);
	}

	/**
	 * Return the type of crystal if item is crystallizable
	 * @return int
	 */
	public final Grade getCrystalType()
	{
		return _crystalType;
	}

	/**
	 * Returns the grade of the item.<BR>
	 * <BR>
	 * <U><I>Concept :</I></U><BR>
	 * In fact, this fucntion returns the type of crystal of the item.
	 * @return int
	 */
	public final Grade getItemGrade()
	{
		return getCrystalType();
	}

	/**
	 * For grades S80 and S84 return S
	 * @return the grade of the item.
	 */
	public final Grade getItemGradeSPlus()
	{
		switch (getItemGrade())
		{
		case S80:
		case S84:
			return Grade.S;
		default:
			return getItemGrade();
		}
	}

	/**
	 * Returns the quantity of crystals for crystallization
	 * @return int
	 */
	public final int getCrystalCount()
	{
		return _crystalCount;
	}

	/**
	 * Returns the name of the item
	 * @return String
	 */
	public final String getName()
	{
		return _name;
	}

	/**
	 * Returns the additional name of the item
	 * @return String
	 */
	public final String getAdditionalName()
	{
		return _addname;
	}

	/**
	 * Return the part of the body used with the item.
	 * @return int
	 */
	public final int getBodyPart()
	{
		return _bodyPart;
	}

	/**
	 * Returns the type 1 of the item
	 * @return int
	 */
	public final int getType1()
	{
		return _type1;
	}

	/**
	 * Returns if the item is stackable
	 * @return boolean
	 */
	public final boolean isStackable()
	{
		return _stackable;
	}

	/**
	 * Returns the price of reference of the item
	 * @return int
	 */
	public final int getReferencePrice()
	{
		return _referencePrice;
	}

	/**
	 * Returns if item is for hatchling
	 * @return boolean
	 */
	public boolean isForHatchling()
	{
		return _type2 == TYPE2_PET_HATCHLING;
	}

	/**
	 * Returns if item is for strider
	 * @return boolean
	 */
	public boolean isForStrider()
	{
		return _type2 == TYPE2_PET_STRIDER;
	}

	/**
	 * Returns if item is for wolf
	 * @return boolean
	 */
	public boolean isForWolf()
	{
		return _type2 == TYPE2_PET_WOLF;
	}

	public boolean isForPetBaby()
	{
		return _type2 == TYPE2_PET_BABY;
	}

	/**
	 * Returns if item is for great wolf
	 * @return boolean
	 */
	public boolean isForGWolf()
	{
		return _type2 == TYPE2_PET_GWOLF;
	}

	/**
	 * Магическая броня для петов
	 * @return
	 */
	public boolean isPendant()
	{
		return _type2 == TYPE2_PENDANT;
	}

	public boolean isForPet()
	{
		return (_type2 == TYPE2_PENDANT) || (_type2 == TYPE2_PET_HATCHLING) || (_type2 == TYPE2_PET_WOLF) || (_type2 == TYPE2_PET_STRIDER) || (_type2 == TYPE2_PET_GWOLF) || (_type2 == TYPE2_PET_BABY);
	}

	/**
	 * Add the L2Skill skill to the list of skills generated by the item
	 * @param skill : L2Skill
	 */
	public void attachSkill(Skill skill)
	{
		_skills = ArrayUtils.add(_skills, skill);
	}

	public Skill[] getAttachedSkills()
	{
		return _skills;
	}

	public Skill getFirstSkill()
	{
		if (_skills.length > 0)
		{
			return _skills[0];
		}
		return null;
	}

	/**
	 * @return skill that player get when has equipped weapon +4 or more (for duals SA)
	 */
	public Skill getEnchant4Skill()
	{
		return _enchant4Skill;
	}

	/**
	 * Returns the name of the item
	 * @return String
	 */
	@Override
	public String toString()
	{
		return _itemId + " " + _name;
	}

	/**
	 * Определяет призрачный предмет или нет
	 * @return true, если предмет призрачный
	 */
	public boolean isShadowItem()
	{
		return (_durability > 0) && !isTemporal();
	}

	public boolean isCommonItem()
	{
		return _name.startsWith("Common Item - ");
	}

	public boolean isSealedItem()
	{
		return _name.startsWith("Sealed");
	}

	public boolean isAltSeed()
	{
		return _name.contains("Alternative");
	}

	public ItemClass getItemClass()
	{
		return _class;
	}

	/**
	 * Is the item if is money adena or stone Print
	 * @return
	 */
	public boolean isAdena()
	{
		return (_itemId == 57) || (_itemId == 6360) || (_itemId == 6361) || (_itemId == 6362);
	}

	public boolean isLifeStone()
	{
		return ((_itemId >= 8723) && (_itemId <= 8762)) || ((_itemId >= 9573) && (_itemId <= 9576)) || ((_itemId >= 10483) && (_itemId <= 10486)) || ((_itemId >= 12754) && (_itemId <= 12763))
					|| (_itemId == 12821) || (_itemId == 12822) || ((_itemId >= 12840) && (_itemId <= 12851)) || (_itemId == 14008) || ((_itemId >= 14166) && (_itemId <= 14169))
					|| ((_itemId >= 16160) && (_itemId <= 16167)) || (_itemId == 16177) || (_itemId == 16178);
	}

	public boolean isEnchantScroll()
	{
		return ((_itemId >= 6569) && (_itemId <= 6578)) || ((_itemId >= 17255) && (_itemId <= 17264)) || ((_itemId >= 22314) && (_itemId <= 22323)) || ((_itemId >= 949) && (_itemId <= 962))
					|| ((_itemId >= 729) && (_itemId <= 732));
	}

	public boolean isForgottenScroll()
	{
		return ((_itemId >= 10549) && (_itemId <= 10599)) || ((_itemId >= 12768) && (_itemId <= 12778)) || ((_itemId >= 14170) && (_itemId <= 14227)) || (_itemId == 17030)
					|| ((_itemId >= 17034) && (_itemId <= 17039));
	}

	public boolean isShieldNoEnchant()
	{
		return (_itemId == 11508) || (_itemId == 6377) || (_itemId == 11532) || (_itemId == 9441) || (_itemId == 16304) || (_itemId == 15621) || (_itemId == 16321) || (_itemId == 13471) || (_itemId == 15587)
					|| (_itemId == 15604);
	}

	public boolean isNoEnchant()
	{
		return (_itemId == 10514) || (_itemId == 10512) || (_itemId == 10513);
	}

	public boolean isSigelNoEnchant()
	{
		return ((_itemId >= 12811) && (_itemId <= 12813)) || (_itemId == 16305) || (_itemId == 15588) || (_itemId == 15605) || (_itemId == 16322) || (_itemId == 15622) || (_itemId >= 13078)
					|| (_itemId == 10119);
	}

	public boolean isCodexBook()
	{
		return _itemId >= 9625 && _itemId <= 9627 || _itemId == 6622;
	}

	public boolean isAttributeStone()
	{
		return _itemId >= 9546 && _itemId <= 9551;
	}

	public boolean isEquipment()
	{
		return _type1 != TYPE1_ITEM_QUESTITEM_ADENA;
	}

	public boolean isKeyMatherial()
	{
		return _class == ItemClass.PIECES;
	}

	public boolean isRecipe()
	{
		return _class == ItemClass.RECIPIES;
	}

	public boolean isExtractable()
	{
		return _class == ItemClass.EXTRACTABLE;
	}

	public boolean isTerritoryAccessory()
	{
		return ((_itemId >= 13740) && (_itemId <= 13748)) || ((_itemId >= 14592) && (_itemId <= 14600)) || ((_itemId >= 14664) && (_itemId <= 14672)) || ((_itemId >= 14801) && (_itemId <= 14809))
					|| ((_itemId >= 15282) && (_itemId <= 15299));
	}

	public boolean isArrow()
	{
		return type == EtcItemType.ARROW;
	}

	public boolean isBelt()
	{
		return _bodyPart == SLOT_BELT;
	}

	public boolean isBracelet()
	{
		return (_bodyPart == SLOT_R_BRACELET) || (_bodyPart == SLOT_L_BRACELET);
	}

	public boolean isUnderwear()
	{
		return _bodyPart == SLOT_UNDERWEAR;
	}

	public boolean isCloak()
	{
		return _bodyPart == SLOT_BACK;
	}

	public boolean isTalisman()
	{
		return _bodyPart == SLOT_DECO;
	}

	public boolean isHerb()
	{
		return type == EtcItemType.HERB;
	}

	public boolean isAtt()
	{
		return isAttributeCrystal() || isAttributeJewel() || isAttributeEnergy();
	}

	public boolean isAttributeCrystal()
	{
		return (_itemId == 9552) || (_itemId == 9553) || (_itemId == 9554) || (_itemId == 9555) || (_itemId == 9556) || (_itemId == 9557);
	}

	public boolean isAttributeJewel()
	{
		return (_itemId == 9558) || (_itemId == 9559) || (_itemId == 9560) || (_itemId == 9561) || (_itemId == 9562) || (_itemId == 9563);
	}

	public boolean isAttributeEnergy()
	{
		return (_itemId == 9564) || (_itemId == 9565) || (_itemId == 9566) || (_itemId == 9567) || (_itemId == 9568) || (_itemId == 9569);
	}

	public boolean isHeroWeapon()
	{
		return ((_itemId >= 6611) && (_itemId <= 6621)) || ((_itemId >= 9388) && (_itemId <= 9390));
	}

	public boolean isEpolets()
	{
		return _itemId == 9912;
	}

	public boolean isCursed()
	{
		return CursedWeaponsManager.getInstance().isCursed(_itemId);
	}

	public boolean isMercenaryTicket()
	{
		return type == EtcItemType.MERCENARY_TICKET;
	}

	public boolean isTerritoryFlag()
	{
		return (_itemId == 13560) || (_itemId == 13561) || (_itemId == 13562) || (_itemId == 13563) || (_itemId == 13564) || (_itemId == 13565) || (_itemId == 13566) || (_itemId == 13567) || (_itemId == 13568);
	}

	public boolean isRod()
	{
		return getItemType() == WeaponType.ROD;
	}

	public boolean isWeapon()
	{
		return getType2() == ItemTemplate.TYPE2_WEAPON;
	}

	public boolean isNotAugmented()
	{
		return _itemId == 21712;
	}

	public boolean isArmor()
	{
		return getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR;
	}

	public boolean isAccessory()
	{
		return getType2() == ItemTemplate.TYPE2_ACCESSORY;
	}

	public boolean isQuest()
	{
		return getType2() == ItemTemplate.TYPE2_QUEST;
	}

	/**
	 * gradeCheck - использовать пока не перепишется система заточки
	 * @param gradeCheck
	 * @return
	 */
	public boolean canBeEnchanted(@Deprecated boolean gradeCheck)
	{
		if ((gradeCheck && (getCrystalType() == Grade.NONE)) || isCursed() || isQuest())
		{
			return false;
		}

		return isEnchantable();
	}

	/**
	 * Returns if item is equipable
	 * @return boolean
	 */
	public boolean isEquipable()
	{
		return (getItemType() == EtcItemType.BAIT) || (getItemType() == EtcItemType.ARROW) || (getItemType() == EtcItemType.BOLT) || !((getBodyPart() == 0) || (this instanceof EtcItemTemplate));
	}

	public void setEnchant4Skill(Skill enchant4Skill)
	{
		_enchant4Skill = enchant4Skill;
	}

	public boolean testCondition(Playable player, ItemInstance instance)
	{
		if (_condition == null)
		{
			return true;
		}

		Env env = new Env();
		env.character = player;
		env.item = instance;

		boolean res = _condition.test(env);
		if (!res && (_condition.getSystemMsg() != null))
		{
			if (_condition.getSystemMsg().size() > 0)
			{
				player.sendPacket(new SystemMessage2(_condition.getSystemMsg()).addItemName(getItemId()));
			}
			else
			{
				player.sendPacket(_condition.getSystemMsg());
			}
		}

		return res;
	}

	public void setCondition(Condition condition)
	{
		_condition = condition;
	}

	public boolean isEnchantable()
	{
		return hasFlag(ItemFlags.ENCHANTABLE);
	}

	public boolean isTradeable()
	{
		return hasFlag(ItemFlags.TRADEABLE);
	}

	public boolean isDestroyable()
	{
		return hasFlag(ItemFlags.DESTROYABLE);
	}

	public boolean isDropable()
	{
		return hasFlag(ItemFlags.DROPABLE);
	}

	public final boolean isSellable()
	{
		return hasFlag(ItemFlags.SELLABLE);
	}

	public final boolean isAugmentable()
	{
		return hasFlag(ItemFlags.AUGMENTABLE);
	}

	public final boolean isAttributable()
	{
		return hasFlag(ItemFlags.ATTRIBUTABLE);
	}

	public final boolean isStoreable()
	{
		return hasFlag(ItemFlags.STOREABLE);
	}

	public final boolean isFreightable()
	{
		return hasFlag(ItemFlags.FREIGHTABLE);
	}

	public boolean hasFlag(ItemFlags f)
	{
		return (_flags & f.mask()) == f.mask();
	}

	private void activeFlag(ItemFlags f)
	{
		_flags |= f.mask();
	}

	public IItemHandler getHandler()
	{
		return _handler;
	}

	public void setHandler(IItemHandler handler)
	{
		_handler = handler;
	}

	public int getReuseDelay()
	{
		return _reuseDelay;
	}

	public int getReuseGroup()
	{
		return _reuseGroup;
	}

	public int getDisplayReuseGroup()
	{
		return _reuseGroup < 0 ? -1 : _reuseGroup;
	}

	public int getAgathionEnergy()
	{
		return _agathionEnergy;
	}

	public void addEnchantOptions(int level, int[] options)
	{
		if (_enchantOptions.isEmpty())
		{
			_enchantOptions = new HashIntObjectMap<int[]>();
		}

		_enchantOptions.put(level, options);
	}

	public IntObjectMap<int[]> getEnchantOptions()
	{
		return _enchantOptions;
	}

	public ReuseType getReuseType()
	{
		return _reuseType;
	}

	public boolean isShield()
	{
		return _bodyPart == 256;
	}

	public List<CapsuledItem> getCapsuledItems()
	{
		return _capsuledItems;
	}

	public void addCapsuledItem(CapsuledItem ci)
	{
		_capsuledItems.add(ci);
	}

	public boolean isMasterwork()
	{
		return _masterwork;
	}

	public int getMasterworkConvert()
	{
		return masterworkConvert;
	}

	public static class CapsuledItem
	{
		private final int item_id;
		private final int min_count;
		private final int max_count;
		private final double chance;

		public CapsuledItem(int item_id, int min_count, int max_count, double chance)
		{
			this.item_id = item_id;
			this.min_count = min_count;
			this.max_count = max_count;
			this.chance = chance;
		}

		public int getItemId()
		{
			return item_id;
		}

		public int getMinCount()
		{
			return min_count;
		}

		public int getMaxCount()
		{
			return max_count;
		}

		public double getChance()
		{
			return chance;
		}
	}

	public void addAugmentationInfo(AugmentationInfo augmentationInfo)
	{
		if (_augmentationInfos.isEmpty())
		{
			_augmentationInfos = new HashIntObjectMap<AugmentationInfo>();
		}
		_augmentationInfos.put(augmentationInfo.getMineralId(), augmentationInfo);
	}

	public IntObjectMap<AugmentationInfo> getAugmentationInfos()
	{
		return _augmentationInfos;
	}
}