package l2mv.gameserver.model.base;

import l2mv.gameserver.utils.Util;

/**
 * This class defines all classes (ex : human fighter, darkFighter...) that a player can chose.<BR><BR>
 *
 * Data :<BR><BR>
 * <li>id : The Identifier of the class</li>
 * <li>isMage : True if the class is a mage class</li>
 * <li>race : The race of this class</li>
 * <li>parent : The parent ClassId for male or null if this class is the root</li>
 * <li>parent2 : The parent2 ClassId for female or null if parent2 like parent</li>
 * <li>level : The child level of this Class</li><BR><BR>
 */
public enum ClassId
{
	fighter(0, "Human Fighter", false, Race.human, null, null, 1, null),

	warrior(1, "Human Warrior", false, Race.human, fighter, null, 2, null), gladiator(2, "Gladiator", false, Race.human, warrior, null, 3, ClassType2.Warrior), warlord(3, "Warlord", false, Race.human, warrior, null, 3, ClassType2.Warrior), knight(4, "Human Knight", false, Race.human, fighter, null, 2, null), paladin(5, "Paladin", false, Race.human, knight, null, 3, ClassType2.Knight), darkAvenger(6, "DarkAvanger", false, Race.human, knight, null, 3, ClassType2.Knight),
	rogue(7, "Rogue", false, Race.human, fighter, null, 2, null), treasureHunter(8, "Treausure Hunter", false, Race.human, rogue, null, 3, ClassType2.Rogue), hawkeye(9, "Hawkeye", false, Race.human, rogue, null, 3, ClassType2.Rogue),

	mage(10, "Human Mage", true, Race.human, null, null, 1, null), wizard(11, "Human Wizzard", true, Race.human, mage, null, 2, null), sorceror(12, "Sorcerror", true, Race.human, wizard, null, 3, ClassType2.Wizard), necromancer(13, "Necromancer", true, Race.human, wizard, null, 3, ClassType2.Wizard), warlock(14, "Warlock", true, Race.human, wizard, null, 3, ClassType2.Summoner), cleric(15, "Cleric", true, Race.human, mage, null, 2, null),
	bishop(16, "Bishop", true, Race.human, cleric, null, 3, ClassType2.Healer), prophet(17, "Prophet", true, Race.human, cleric, null, 3, ClassType2.Enchanter),

	elvenFighter(18, "Elven Fighter", false, Race.elf, null, null, 1, null), elvenKnight(19, "Elven Knight", false, Race.elf, elvenFighter, null, 2, null), templeKnight(20, "Temple Knight", false, Race.elf, elvenKnight, null, 3, ClassType2.Knight), swordSinger(21, "Sword Singer", false, Race.elf, elvenKnight, null, 3, ClassType2.Enchanter), elvenScout(22, "Elven Scout", false, Race.elf, elvenFighter, null, 2, null),
	plainsWalker(23, "Plains Walker", false, Race.elf, elvenScout, null, 3, ClassType2.Rogue), silverRanger(24, "Silver Ranger", false, Race.elf, elvenScout, null, 3, ClassType2.Rogue),

	elvenMage(25, "Elven Mage", true, Race.elf, null, null, 1, null), elvenWizard(26, "Elven Wizard", true, Race.elf, elvenMage, null, 2, null), spellsinger(27, "Spellsinger", true, Race.elf, elvenWizard, null, 3, ClassType2.Wizard), elementalSummoner(28, "Elemental Summoner", true, Race.elf, elvenWizard, null, 3, ClassType2.Summoner), oracle(29, "Elven Oracle", true, Race.elf, elvenMage, null, 2, null), elder(30, "Elven Elder", true, Race.elf, oracle, null, 3, ClassType2.Healer),

	darkFighter(31, "Dark Fighter", false, Race.darkelf, null, null, 1, null), palusKnight(32, "Palus Knight", false, Race.darkelf, darkFighter, null, 2, null), shillienKnight(33, "Shillien Knight", false, Race.darkelf, palusKnight, null, 3, ClassType2.Knight), bladedancer(34, "Bladedancer", false, Race.darkelf, palusKnight, null, 3, ClassType2.Enchanter), assassin(35, "Assassin", false, Race.darkelf, darkFighter, null, 2, null),
	abyssWalker(36, "Abyss Walker", false, Race.darkelf, assassin, null, 3, ClassType2.Rogue), phantomRanger(37, "Phantom Ranger", false, Race.darkelf, assassin, null, 3, ClassType2.Rogue),

	darkMage(38, "Dark Mage", true, Race.darkelf, null, null, 1, null), darkWizard(39, "Dark Wizard", true, Race.darkelf, darkMage, null, 2, null), spellhowler(40, "Spellhowler", true, Race.darkelf, darkWizard, null, 3, ClassType2.Wizard), phantomSummoner(41, "Phantom Summoner", true, Race.darkelf, darkWizard, null, 3, ClassType2.Summoner), shillienOracle(42, "Shillien Oracle", true, Race.darkelf, darkMage, null, 2, null),
	shillienElder(43, "Shillien Elder", true, Race.darkelf, shillienOracle, null, 3, ClassType2.Healer),

	orcFighter(44, "Orc Fighter", false, Race.orc, null, null, 1, null), orcRaider(45, "Orc Raider", false, Race.orc, orcFighter, null, 2, null), destroyer(46, "Destroyer", false, Race.orc, orcRaider, null, 3, ClassType2.Warrior), orcMonk(47, "Monk", false, Race.orc, orcFighter, null, 2, null), tyrant(48, "Tyrant", false, Race.orc, orcMonk, null, 3, ClassType2.Warrior),

	orcMage(49, "Orc Mage", true, Race.orc, null, null, 1, null), orcShaman(50, "Orc Shaman", true, Race.orc, orcMage, null, 2, null), overlord(51, "Overlord", true, Race.orc, orcShaman, null, 3, ClassType2.Enchanter), warcryer(52, "Warcryer", true, Race.orc, orcShaman, null, 3, ClassType2.Enchanter),

	dwarvenFighter(53, "Dwarven Fighter", false, Race.dwarf, null, null, 1, null), scavenger(54, "Scavenger", false, Race.dwarf, dwarvenFighter, null, 2, null), bountyHunter(55, "Bounty Hunter", false, Race.dwarf, scavenger, null, 3, ClassType2.Warrior), artisan(56, "Artisan", false, Race.dwarf, dwarvenFighter, null, 2, null), warsmith(57, "Warsmith", false, Race.dwarf, artisan, null, 3, ClassType2.Warrior),

	/*
	 * Dummy Entries (id's already in decimal format)
	 * btw FU NCSoft for the amount of work you put me
	 * through to do this!!
	 * <START>
	 */
	dummyEntry1(58, "dummyEntry1", false, null, null, null, 0, null), dummyEntry2(59, "dummyEntry2", false, null, null, null, 0, null), dummyEntry3(60, "dummyEntry3", false, null, null, null, 0, null), dummyEntry4(61, "dummyEntry4", false, null, null, null, 0, null), dummyEntry5(62, "dummyEntry5", false, null, null, null, 0, null), dummyEntry6(63, "dummyEntry6", false, null, null, null, 0, null), dummyEntry7(64, "dummyEntry7", false, null, null, null, 0, null),
	dummyEntry8(65, "dummyEntry8", false, null, null, null, 0, null), dummyEntry9(66, "dummyEntry9", false, null, null, null, 0, null), dummyEntry10(67, "dummyEntry10", false, null, null, null, 0, null), dummyEntry11(68, "dummyEntry11", false, null, null, null, 0, null), dummyEntry12(69, "dummyEntry12", false, null, null, null, 0, null), dummyEntry13(70, "dummyEntry13", false, null, null, null, 0, null), dummyEntry14(71, "dummyEntry14", false, null, null, null, 0, null),
	dummyEntry15(72, "dummyEntry15", false, null, null, null, 0, null), dummyEntry16(73, "dummyEntry16", false, null, null, null, 0, null), dummyEntry17(74, "dummyEntry17", false, null, null, null, 0, null), dummyEntry18(75, "dummyEntry18", false, null, null, null, 0, null), dummyEntry19(76, "dummyEntry19", false, null, null, null, 0, null), dummyEntry20(77, "dummyEntry20", false, null, null, null, 0, null), dummyEntry21(78, "dummyEntry21", false, null, null, null, 0, null),
	dummyEntry22(79, "dummyEntry22", false, null, null, null, 0, null), dummyEntry23(80, "dummyEntry23", false, null, null, null, 0, null), dummyEntry24(81, "dummyEntry24", false, null, null, null, 0, null), dummyEntry25(82, "dummyEntry25", false, null, null, null, 0, null), dummyEntry26(83, "dummyEntry26", false, null, null, null, 0, null), dummyEntry27(84, "dummyEntry27", false, null, null, null, 0, null), dummyEntry28(85, "dummyEntry28", false, null, null, null, 0, null),
	dummyEntry29(86, "dummyEntry29", false, null, null, null, 0, null), dummyEntry30(87, "dummyEntry30", false, null, null, null, 0, null),
	/*
	 * <END>
	 * Of Dummy entries
	 */

	duelist(88, "Duelist", false, Race.human, gladiator, null, 4, ClassType2.Warrior), dreadnought(89, "Dreadnought", false, Race.human, warlord, null, 4, ClassType2.Warrior), phoenixKnight(90, "Phoenix Knight", false, Race.human, paladin, null, 4, ClassType2.Knight), hellKnight(91, "Hell Knight", false, Race.human, darkAvenger, null, 4, ClassType2.Knight), sagittarius(92, "Sagittarius", false, Race.human, hawkeye, null, 4, ClassType2.Rogue),
	adventurer(93, "Adventurer", false, Race.human, treasureHunter, null, 4, ClassType2.Rogue), archmage(94, "Archmage", true, Race.human, sorceror, null, 4, ClassType2.Wizard), soultaker(95, "Soultaker", true, Race.human, necromancer, null, 4, ClassType2.Wizard), arcanaLord(96, "Arcana Lord", true, Race.human, warlock, null, 4, ClassType2.Summoner), cardinal(97, "Cardinal", true, Race.human, bishop, null, 4, ClassType2.Healer),
	hierophant(98, "Hierophant", true, Race.human, prophet, null, 4, ClassType2.Enchanter),

	evaTemplar(99, "Eva Templar", false, Race.elf, templeKnight, null, 4, ClassType2.Knight), swordMuse(100, "Sword Muse", false, Race.elf, swordSinger, null, 4, ClassType2.Enchanter), windRider(101, "Wind Rider", false, Race.elf, plainsWalker, null, 4, ClassType2.Rogue), moonlightSentinel(102, "Moonlight Sentinel", false, Race.elf, silverRanger, null, 4, ClassType2.Rogue), mysticMuse(103, "Mystic Muse", true, Race.elf, spellsinger, null, 4, ClassType2.Wizard),
	elementalMaster(104, "Elemental Master", true, Race.elf, elementalSummoner, null, 4, ClassType2.Summoner), evaSaint(105, "Eva Saint", true, Race.elf, elder, null, 4, ClassType2.Healer),

	shillienTemplar(106, "Shillien Templar", false, Race.darkelf, shillienKnight, null, 4, ClassType2.Knight), spectralDancer(107, "Spectral Dancer", false, Race.darkelf, bladedancer, null, 4, ClassType2.Enchanter), ghostHunter(108, "Ghost Hunter", false, Race.darkelf, abyssWalker, null, 4, ClassType2.Rogue), ghostSentinel(109, "Ghost Sentinel", false, Race.darkelf, phantomRanger, null, 4, ClassType2.Rogue),
	stormScreamer(110, "Storm Screamer", true, Race.darkelf, spellhowler, null, 4, ClassType2.Wizard), spectralMaster(111, "Spectral Master", true, Race.darkelf, phantomSummoner, null, 4, ClassType2.Summoner), shillienSaint(112, "Shillien Saint", true, Race.darkelf, shillienElder, null, 4, ClassType2.Healer),

	titan(113, "Titan", false, Race.orc, destroyer, null, 4, ClassType2.Warrior), grandKhauatari(114, "Grand Khauatari", false, Race.orc, tyrant, null, 4, ClassType2.Warrior), dominator(115, "Dominator", true, Race.orc, overlord, null, 4, ClassType2.Enchanter), doomcryer(116, "Doomcryer", true, Race.orc, warcryer, null, 4, ClassType2.Enchanter),

	fortuneSeeker(117, "Fortune Seeker", false, Race.dwarf, bountyHunter, null, 4, ClassType2.Warrior), maestro(118, "Maestro", false, Race.dwarf, warsmith, null, 4, ClassType2.Warrior),

	dummyEntry31(119, "dummyEntry31", false, null, null, null, 0, null), dummyEntry32(120, "dummyEntry32", false, null, null, null, 0, null), dummyEntry33(121, "dummyEntry33", false, null, null, null, 0, null), dummyEntry34(122, "dummyEntry34", false, null, null, null, 0, null),

	/**
	 * Kamael
	 */
	maleSoldier(123, "Male Soldier", false, Race.kamael, null, null, 1, null), femaleSoldier(124, "Female Soldier", false, Race.kamael, null, null, 1, null), trooper(125, "Trooper", false, Race.kamael, maleSoldier, null, 2, null), warder(126, "Warder", false, Race.kamael, femaleSoldier, null, 2, null), berserker(127, "Berserker", false, Race.kamael, trooper, null, 3, ClassType2.Warrior), maleSoulbreaker(128, "Male Soulbreaker", false, Race.kamael, trooper, null, 3, ClassType2.Warrior),
	femaleSoulbreaker(129, "Female Soulbreaker", false, Race.kamael, warder, null, 3, ClassType2.Warrior), arbalester(130, "Arbalester", false, Race.kamael, warder, null, 3, ClassType2.Rogue), doombringer(131, "Doombringer", false, Race.kamael, berserker, null, 4, ClassType2.Warrior), maleSoulhound(132, "Male Soulhound", false, Race.kamael, maleSoulbreaker, null, 4, ClassType2.Warrior), femaleSoulhound(133, "Female Soulhound", false, Race.kamael, femaleSoulbreaker, null, 4, ClassType2.Warrior),
	trickster(134, "Trickster", false, Race.kamael, arbalester, null, 4, ClassType2.Rogue), inspector(135, "Inspector", false, Race.kamael, trooper, warder, 3, ClassType2.Enchanter), judicator(136, "Judicator", false, Race.kamael, inspector, null, 4, ClassType2.Enchanter);

	public static final ClassId[] VALUES = values();

	/** The Identifier of the Class<?> */
	private final int _id;

	private final String _name;

	/** True if the class is a mage class */
	private final boolean _isMage;

	/** The Race object of the class */
	private final Race _race;

	/** The parent ClassId for male or null if this class is a root */
	private final ClassId _parent;

	/** The parent2 ClassId for female or null if parent2 class is parent */
	private final ClassId _parent2;

	private final ClassType2 _type2;

	private final int _level;

	/**
	 * Constructor<?> of ClassId.<BR><BR>
	 */
	private ClassId(int id, String name, boolean isMage, Race race, ClassId parent, ClassId parent2, int level, ClassType2 classType2)
	{
		_id = id;
		_name = name;
		_isMage = isMage;
		_race = race;
		_parent = parent;
		_parent2 = parent2;
		_level = level;
		_type2 = classType2;
	}

	/**
	 * Return the Identifier of the Class.<BR><BR>
	 */
	public final int getId()
	{
		return _id;
	}

	/**
	 * Return the display name of the Class.<BR><BR>
	 */
	public final String getName()
	{
		return _name;
	}

	/**
	 * Return True if the class is a mage class.<BR><BR>
	 */
	public final boolean isMage()
	{
		return _isMage;
	}

	/**
	 * Return the Race object of the class.<BR><BR>
	 */
	public final Race getRace()
	{
		return _race;
	}

	/**
	 * Return True if this Class<?> is a child of the selected ClassId.<BR><BR>
	 *
	 * @param cid The parent ClassId to check
	 */
	public final boolean childOf(ClassId cid)
	{
		if (_parent == null)
		{
			return false;
		}

		if (_parent == cid || _parent2 == cid)
		{
			return true;
		}

		return _parent.childOf(cid);

	}

	/**
	 * Return True if this Class<?> is equal to the selected ClassId or a child of the selected ClassId.<BR><BR>
	 *
	 * @param cid The parent ClassId to check
	 */
	public final boolean equalsOrChildOf(ClassId cid)
	{
		return this == cid || childOf(cid);
	}

	/**
	 * Return the child level of this Class<?> (0=root, 1=child leve 1...).<BR><BR>
	 *
	 * @param cid The parent ClassId to check
	 */
	public final int level()
	{
		if (_parent == null)
		{
			return 0;
		}

		return 1 + _parent.level();
	}

	public final ClassId getParent(int sex)
	{
		return sex == 0 || _parent2 == null ? _parent : _parent2;
	}

	public final int getLevel()
	{
		return _level;
	}

	public ClassType2 getType2()
	{
		return _type2;
	}

	public String toPrettyString()
	{
		switch (this)
		{
		case evaTemplar:
		{
			return "Eva's Templar";
		}
		case evaSaint:
		{
			return "Eva's Saint";
		}
		default:
		{
			String prettyName = Util.spaceBeforeUpper(name());
			prettyName = prettyName.substring(0, 1).toUpperCase() + prettyName.substring(1);
			prettyName = prettyName.trim();
			return prettyName;
		}
		}
	}

	public static ClassId getById(int id)
	{
		for (ClassId classId : values())
		{
			if (classId._id == id)
			{
				return classId;
			}
		}
		return null;
	}

	@Override
	public String toString()
	{
		return _name;
	}
}