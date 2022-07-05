package l2f.gameserver.templates.item;

import l2f.gameserver.templates.StatsSet;
import l2f.gameserver.templates.item.WeaponTemplate.WeaponType;

public final class ArmorTemplate extends ItemTemplate
{
	public static final double EMPTY_RING = 5;
	public static final double EMPTY_EARRING = 9;
	public static final double EMPTY_NECKLACE = 13;
	public static final double EMPTY_HELMET = 12;
	public static final double EMPTY_BODY_FIGHTER = 31;
	public static final double EMPTY_LEGS_FIGHTER = 18;
	public static final double EMPTY_BODY_MYSTIC = 15;
	public static final double EMPTY_LEGS_MYSTIC = 8;
	public static final double EMPTY_GLOVES = 8;
	public static final double EMPTY_BOOTS = 7;

	public enum ArmorType implements ItemType
	{
		NONE(1, "None"), LIGHT(2, "Light"), HEAVY(3, "Heavy"), MAGIC(4, "Magic"), PET(5, "Pet"), SIGIL(6, "Sigil");

		public final static ArmorType[] VALUES = values();

		private final long _mask;
		private final String _name;

		ArmorType(int id, String name)
		{
			_mask = 1L << (id + WeaponType.VALUES.length);
			_name = name;
		}

		@Override
		public long mask()
		{
			return _mask;
		}

		@Override
		public String toString()
		{
			return _name;
		}
	}

	public ArmorTemplate(StatsSet set)
	{
		super(set);
		type = set.getEnum("type", ArmorType.class);

		if (_bodyPart == SLOT_NECK || (_bodyPart & SLOT_L_EAR) != 0 || (_bodyPart & SLOT_L_FINGER) != 0)
		{
			_type1 = TYPE1_WEAPON_RING_EARRING_NECKLACE;
			_type2 = TYPE2_ACCESSORY;
		}
		else if (_bodyPart == SLOT_HAIR || _bodyPart == SLOT_DHAIR || _bodyPart == SLOT_HAIRALL)
		{
			_type1 = TYPE1_OTHER;
			_type2 = ItemTemplate.TYPE2_OTHER;
		}
		else
		{
			_type1 = TYPE1_SHIELD_ARMOR;
			_type2 = TYPE2_SHIELD_ARMOR;
		}

		if (getItemType() == ArmorType.PET)
		{
			_type1 = TYPE1_SHIELD_ARMOR;
			switch (_bodyPart)
			{
			case SLOT_WOLF:
				_type2 = TYPE2_PET_WOLF;
				_bodyPart = SLOT_CHEST;
				break;
			case SLOT_GWOLF:
				_type2 = TYPE2_PET_GWOLF;
				_bodyPart = SLOT_CHEST;
				break;
			case SLOT_HATCHLING:
				_type2 = TYPE2_PET_HATCHLING;
				_bodyPart = SLOT_CHEST;
				break;
			case SLOT_PENDANT:
				_type2 = TYPE2_PENDANT;
				_bodyPart = SLOT_NECK;
				break;
			case SLOT_BABYPET:
				_type2 = TYPE2_PET_BABY;
				_bodyPart = SLOT_CHEST;
				break;
			default:
				_type2 = TYPE2_PET_STRIDER;
				_bodyPart = SLOT_CHEST;
				break;
			}
		}
	}

	/**
	 * Returns the type of the armor.
	 * @return L2ArmorType
	 */
	@Override
	public ArmorType getItemType()
	{
		return (ArmorType) super.type;
	}

	/**
	 * Returns the ID of the item after applying the mask.
	 * @return int : ID of the item
	 */
	@Override
	public final long getItemMask()
	{
		return getItemType().mask();
	}
}