package l2f.gameserver.templates.item;

public enum Bodypart
{
	NONE(ItemTemplate.SLOT_NONE), CHEST(ItemTemplate.SLOT_CHEST), BELT(ItemTemplate.SLOT_BELT), RIGHT_BRACELET(ItemTemplate.SLOT_R_BRACELET), LEFT_BRACELET(ItemTemplate.SLOT_L_BRACELET),
	FULL_ARMOR(ItemTemplate.SLOT_FULL_ARMOR), HEAD(ItemTemplate.SLOT_HEAD), HAIR(ItemTemplate.SLOT_HAIR), FACE(ItemTemplate.SLOT_DHAIR), HAIR_ALL(ItemTemplate.SLOT_HAIRALL),
	UNDERWEAR(ItemTemplate.SLOT_UNDERWEAR), BACK(ItemTemplate.SLOT_BACK), NECKLACE(ItemTemplate.SLOT_NECK), LEGS(ItemTemplate.SLOT_LEGS), FEET(ItemTemplate.SLOT_FEET), GLOVES(ItemTemplate.SLOT_GLOVES),
	RIGHT_HAND(ItemTemplate.SLOT_R_HAND), LEFT_HAND(ItemTemplate.SLOT_L_HAND), LEFT_RIGHT_HAND(ItemTemplate.SLOT_LR_HAND), RIGHT_EAR(ItemTemplate.SLOT_R_EAR), LEFT_EAR(ItemTemplate.SLOT_L_EAR),
	RIGHT_FINGER(ItemTemplate.SLOT_R_FINGER), FORMAL_WEAR(ItemTemplate.SLOT_FORMAL_WEAR), TALISMAN(ItemTemplate.SLOT_DECO), LEFT_FINGER(ItemTemplate.SLOT_L_FINGER),
	// эти слоты не используются клиентом, ток для понимания серваком для каких петов
	WOLF(ItemTemplate.SLOT_WOLF, CHEST), GREAT_WOLF(ItemTemplate.SLOT_GWOLF, CHEST), HATCHLING(ItemTemplate.SLOT_HATCHLING, CHEST), STRIDER(ItemTemplate.SLOT_STRIDER, CHEST),
	BABY_PET(ItemTemplate.SLOT_BABYPET, CHEST), PENDANT(ItemTemplate.SLOT_PENDANT, NECKLACE);

	private int _mask;
	private Bodypart _real;

	Bodypart(int mask)
	{
		this(mask, null);
	}

	Bodypart(int mask, Bodypart real)
	{
		_mask = mask;
		_real = real;
	}

	public int mask()
	{
		return _mask;
	}

	public Bodypart getReal()
	{
		return _real;
	}
}
