package l2f.gameserver.skills;

import java.util.NoSuchElementException;

public enum AbnormalEffect
{
	NULL("null", 0x0), BLEEDING("bleeding", 0x00000001), POISON("poison", 0x00000002), REDCIRCLE("redcircle", 0x00000004), ICE("ice", 0x00000008),

	AFFRAID("affraid", 0x00000010), CONFUSED("confused", 0x00000020), STUN("stun", 0x00000040), SLEEP("sleep", 0x00000080),

	MUTED("muted", 0x00000100), ROOT("root", 0x00000200), HOLD_1("hold1", 0x00000400), HOLD_2("hold2", 0x00000800), // the effect of Dance of Medusa

	UNKNOWN_13("unk13", 0x00001000), BIG_HEAD("bighead", 0x00002000), FLAME("flame", 0x00004000), UNKNOWN_16("unk16", 0x00008000), // corpse with abnormalom goes white

	GROW("grow", 0x00010000), FLOATING_ROOT("floatroot", 0x00020000), DANCE_STUNNED("dancestun", 0x00040000), // танцует со звездочками над головой
	FIREROOT_STUN("firerootstun", 0x00080000), // красная аура у ног со звездочками над головой

	STEALTH("shadow", 0x00100000), IMPRISIONING_1("imprison1", 0x00200000), // синяя аура на уровне пояса
	IMPRISIONING_2("imprison2", 0x00400000), // синяя аура на уровне пояса
	MAGIC_CIRCLE("magiccircle", 0x00800000), // большой синий круг вокруг чара

	ICE2("ice2", 0x01000000), // небольшая ледяная аура, скорее всего DOT
	EARTHQUAKE("earthquake", 0x02000000), // землетрясение
	UNKNOWN_27("unk27", 0x04000000), INVULNERABLE("invul1", 0x08000000), // Ultimate Defence

	VITALITY("vitality", 0x10000000), // Vitality херб, красное пламя
	REAL_TARGET("realtarget", 0x20000000), // дебафф Real Target (знак над головой)
	DEATH_MARK("deathmark", 0x40000000), // голубая морда над головой
	SOUL_SHOCK("soulshock", 0x80000000), // голубой череп над головой

	// special effects
	S_INVULNERABLE("invul2", 0x00000001, true), // целестиал
	S_AIR_STUN("redglow", 0x00000002, true), // непонятное красное облако
	S_AIR_ROOT("redglow2", 0x00000004, true), // непонятное красное облако
	S_BAGUETTE_SWORD("baguettesword", 0x00000008, true), // пусто

	S_YELLOW_AFFRO("yellowafro", 0x00000010, true), // Большая круглая желтая прическа с воткнутой в волосы расческой
	S_PINK_AFFRO("pinkafro", 0x00000020, true), // Большая круглая розовая прическа с воткнутой в волосы расческой
	S_BLACK_AFFRO("blackafro", 0x00000040, true), // Большая круглая черная прическа с воткнутой в волосы расческой
	S_UNKNOWN8("sunk8", 0x00000080, true), // пусто

	S_STIGMA("stigma", 0x00000100, true), // Stigma of Shillen
	S_UNKNOWN10("sunk10", 0x00000200, true), // какой то рут
	FROZEN_PILLAR("frozenpillar", 0x00000400, true), // Frozen Pillar (Freya)
	S_UNKNOWN12("sunk12", 0x00000800, true), // пусто

	S_DESTINO_SET("vesper_red", 0x00001000, true), // Фейковый сет Веспера
	S_VESPER_SET("vesper_noble", 0x00002000, true), // фейковый сет Веспера Белый
	S_SOA_RESP("soa_respawn", 0x00004000, true), // Мобы на респе СОА появляются с таким абнормалом
	S_ARCANE_SHIELD("arcane_invul", 0x00008000, true), // Щит Арканы

	S_UNKNOWN17("sunk17", 0x00010000, true), // пусто
	S_UNKNOWN18("sunk18", 0x00020000, true), // пусто
	S_UNKNOWN19("sunk19", 0x00040000, true), // пусто
	S_NAVIT("nevitSystem", 0x00080000, true), // пусто

	S_UNKNOWN21("sunk21", 0x00100000, true), // пусто
	S_UNKNOWN22("sunk22", 0x00200000, true), // пусто
	S_UNKNOWN23("sunk23", 0x00400000, true), // пусто
	S_UNKNOWN24("sunk24", 0x00800000, true), // пусто

	S_UNKNOWN25("sunk25", 0x01000000, true), // пусто
	S_UNKNOWN26("sunk26", 0x02000000, true), // пусто
	S_UNKNOWN27("sunk27", 0x04000000, true), // пусто
	S_UNKNOWN28("sunk28", 0x08000000, true), // пусто

	S_UNKNOWN29("sunk29", 0x10000000, true), // пусто
	S_UNKNOWN30("sunk30", 0x20000000, true), // пусто
	S_UNKNOWN31("sunk31", 0x40000000, true), // пусто
	S_UNKNOWN32("sunk32", 0x80000000, true), // пусто

	// event effects
	E_AFRO_1("afrobaguette1", 0x000001, false, true), E_AFRO_2("afrobaguette2", 0x000002, false, true), E_AFRO_3("afrobaguette3", 0x000004, false, true), E_EVASWRATH("evaswrath", 0x000008, false, true), E_HEADPHONE("headphone", 0x000010, false, true), E_VESPER_1("vesper1", 0x000020, false, true), E_VESPER_2("vesper2", 0x000040, false, true), E_VESPER_3("vesper3", 0x000080, false, true);

	private final int _mask;
	private final String _name;
	private final boolean _special;
	private final boolean _event;

	private AbnormalEffect(String name, int mask)
	{
		_name = name;
		_mask = mask;
		_special = false;
		_event = false;
	}

	private AbnormalEffect(String name, int mask, boolean special)
	{
		_name = name;
		_mask = mask;
		_special = special;
		_event = false;
	}

	private AbnormalEffect(String name, int mask, boolean special, boolean event)
	{
		_name = name;
		_mask = mask;
		_special = special;
		_event = event;
	}

	public final int getMask()
	{
		return _mask;
	}

	public final String getName()
	{
		return _name;
	}

	public final boolean isSpecial()
	{
		return _special;
	}

	public final boolean isEvent()
	{
		return _event;
	}

	public static AbnormalEffect getByName(String name)
	{
		for (AbnormalEffect eff : AbnormalEffect.values())
		{
			if (eff.getName().equals(name))
			{
				return eff;
			}
		}

		throw new NoSuchElementException("AbnormalEffect not found for name: '" + name + "'.\n Please check " + AbnormalEffect.class.getCanonicalName());
	}
}