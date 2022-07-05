package l2f.gameserver.model.base;

import l2f.gameserver.model.Player;
import l2f.gameserver.tables.SkillTable;

public final class EnchantSkillLearn
{
	// these two build the primary key
	private final int _id;
	private final int _level;

	// not needed, just for easier debug
	private final String _name;
	private final String _type;

	private final int _baseLvl;
	private final int _maxLvl;
	private final int _minSkillLevel;

	private int _costMul;

	public EnchantSkillLearn(int id, int lvl, String name, String type, int minSkillLvl, int baseLvl, int maxLvl)
	{
		_id = id;
		_level = lvl;
		_baseLvl = baseLvl;
		_maxLvl = maxLvl;
		_minSkillLevel = minSkillLvl;
		_name = name.intern();
		_type = type.intern();
		_costMul = _maxLvl == 15 ? 5 : 1;
	}

	/**
	 * @return Returns the id.
	 */
	public int getId()
	{
		return _id;
	}

	/**
	 * @return Returns the level.
	 */
	public int getLevel()
	{
		return _level;
	}

	/**
	 * @return Returns the minLevel.
	 */
	public int getBaseLevel()
	{
		return _baseLvl;
	}

	/**
	 * @return Returns the minSkillLevel.
	 */
	public int getMinSkillLevel()
	{
		return _minSkillLevel;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return _name;
	}

	public int getCostMult()
	{
		return _costMul;
	}

	/**
	 * @return Returns the spCost.
	 */
	public int[] getCost()
	{
		return SkillTable.getInstance().getInfo(_id, 1).isOffensive() ? _priceCombat[_level % 100] : _priceBuff[_level % 100];
	}

	/** Шанс заточки скилов 2й профы */
	private static final int[][] _chance =
	{
		{},
		// 76 77 78 79 80 81 82 83 84 85
		{
			82,
			92,
			97,
			97,
			97,
			97,
			97,
			97,
			97,
			97
		}, // 1
		{
			80,
			90,
			95,
			95,
			95,
			95,
			95,
			95,
			95,
			95
		}, // 2
		{
			78,
			88,
			93,
			93,
			93,
			93,
			93,
			93,
			93,
			93
		}, // 3
		{
			52,
			76,
			86,
			91,
			91,
			91,
			91,
			91,
			91,
			91
		}, // 4
		{
			50,
			74,
			84,
			89,
			89,
			89,
			89,
			89,
			89,
			89
		}, // 5
		{
			48,
			72,
			82,
			87,
			87,
			87,
			87,
			87,
			87,
			87
		}, // 6
		{
			01,
			46,
			70,
			80,
			85,
			85,
			85,
			85,
			85,
			85
		}, // 7
		{
			01,
			44,
			68,
			78,
			83,
			83,
			83,
			83,
			83,
			83
		}, // 8
		{
			01,
			42,
			66,
			76,
			81,
			81,
			81,
			81,
			81,
			81
		}, // 9
		{
			01,
			01,
			40,
			64,
			74,
			79,
			79,
			79,
			79,
			79
		}, // 10
		{
			01,
			01,
			38,
			62,
			72,
			77,
			77,
			77,
			77,
			77
		}, // 11
		{
			01,
			01,
			36,
			60,
			70,
			75,
			75,
			75,
			75,
			75
		}, // 12
		{
			01,
			01,
			01,
			34,
			58,
			68,
			73,
			73,
			73,
			73
		}, // 13
		{
			01,
			01,
			01,
			32,
			56,
			66,
			71,
			71,
			71,
			71
		}, // 14
		{
			01,
			01,
			01,
			30,
			54,
			64,
			69,
			69,
			69,
			69
		}, // 15
		{
			01,
			01,
			01,
			01,
			28,
			52,
			62,
			67,
			67,
			67
		}, // 16
		{
			01,
			01,
			01,
			01,
			26,
			50,
			60,
			65,
			65,
			65
		}, // 17
		{
			01,
			01,
			01,
			01,
			24,
			48,
			58,
			63,
			63,
			63
		}, // 18
		{
			01,
			01,
			01,
			01,
			01,
			22,
			46,
			56,
			61,
			61
		}, // 19
		{
			01,
			01,
			01,
			01,
			01,
			20,
			44,
			54,
			59,
			59
		}, // 20
		{
			01,
			01,
			01,
			01,
			01,
			18,
			42,
			52,
			57,
			57
		}, // 21
		{
			01,
			01,
			01,
			01,
			01,
			01,
			16,
			40,
			50,
			55
		}, // 22
		{
			01,
			01,
			01,
			01,
			01,
			01,
			14,
			38,
			48,
			53
		}, // 23
		{
			01,
			01,
			01,
			01,
			01,
			01,
			12,
			36,
			46,
			51
		}, // 24
		{
			01,
			01,
			01,
			01,
			01,
			01,
			01,
			10,
			34,
			44
		}, // 25
		{
			01,
			01,
			01,
			01,
			01,
			01,
			01,
			8,
			32,
			42
		}, // 26
		{
			01,
			01,
			01,
			01,
			01,
			01,
			01,
			06,
			30,
			40
		}, // 27
		{
			01,
			01,
			01,
			01,
			01,
			01,
			01,
			01,
			04,
			28
		}, // 28
		{
			01,
			01,
			01,
			01,
			01,
			01,
			01,
			01,
			02,
			26
		}, // 29
		{
			01,
			01,
			01,
			01,
			01,
			01,
			01,
			01,
			02,
			24
		}, // 30
	};

	/** Шанс заточки скилов 3ей профы */
	private static final int[][] _chance15 =
	{
		{},
		// 76 77 78 79 80 81 82 83 84 85
		{
			18,
			28,
			38,
			48,
			58,
			82,
			92,
			97,
			97,
			97
		}, // 1
		{
			01,
			01,
			01,
			46,
			56,
			80,
			90,
			95,
			95,
			95
		}, // 2
		{
			01,
			01,
			01,
			01,
			54,
			78,
			88,
			93,
			93,
			93
		}, // 3
		{
			01,
			01,
			01,
			01,
			42,
			52,
			76,
			86,
			91,
			91
		}, // 4
		{
			01,
			01,
			01,
			01,
			01,
			50,
			74,
			84,
			89,
			89
		}, // 5
		{
			01,
			01,
			01,
			01,
			01,
			48,
			72,
			82,
			87,
			87
		}, // 6
		{
			01,
			01,
			01,
			01,
			01,
			01,
			46,
			70,
			80,
			85
		}, // 7
		{
			01,
			01,
			01,
			01,
			01,
			01,
			44,
			68,
			78,
			83
		}, // 8
		{
			01,
			01,
			01,
			01,
			01,
			01,
			42,
			66,
			76,
			81
		}, // 9
		{
			01,
			01,
			01,
			01,
			01,
			01,
			01,
			40,
			64,
			74
		}, // 10
		{
			01,
			01,
			01,
			01,
			01,
			01,
			01,
			38,
			62,
			72
		}, // 11
		{
			01,
			01,
			01,
			01,
			01,
			01,
			01,
			36,
			60,
			70
		}, // 12
		{
			01,
			01,
			01,
			01,
			01,
			01,
			01,
			01,
			34,
			58
		}, // 13
		{
			01,
			01,
			01,
			01,
			01,
			01,
			01,
			01,
			32,
			56
		}, // 14
		{
			01,
			01,
			01,
			01,
			01,
			01,
			01,
			01,
			30,
			54
		}, // 15
	};

	/* TODO: Вынести в ХМЛ, на оффе около 10 видов комбанаций цен. */
	/** Цена заточки скиллов 3й профессии */
	private static final int[][] _priceBuff =
	{
		{}, //
		{
			51975,
			352786
		}, // 1
		{
			51975,
			352786
		}, // 2
		{
			51975,
			352786
		}, // 3
		{
			78435,
			370279
		}, // 4
		{
			78435,
			370279
		}, // 5
		{
			78435,
			370279
		}, // 6
		{
			105210,
			388290
		}, // 7
		{
			105210,
			388290
		}, // 8
		{
			105210,
			388290
		}, // 9
		{
			132300,
			416514
		}, // 10
		{
			132300,
			416514
		}, // 11
		{
			132300,
			416514
		}, // 12
		{
			159705,
			435466
		}, // 13
		{
			159705,
			435466
		}, // 14
		{
			159705,
			435466
		}, // 15
		{
			187425,
			466445
		}, // 16
		{
			187425,
			466445
		}, // 17
		{
			187425,
			466445
		}, // 18
		{
			215460,
			487483
		}, // 19
		{
			215460,
			487483
		}, // 20
		{
			215460,
			487483
		}, // 21
		{
			243810,
			520215
		}, // 22
		{
			243810,
			520215
		}, // 23
		{
			243810,
			520215
		}, // 24
		{
			272475,
			542829
		}, // 25
		{
			272475,
			542829
		}, // 26
		{
			272475,
			542829
		}, // 27
		{
			304500,
			566426
		}, // 28, цифра неточная
		{
			304500,
			566426
		}, // 29, цифра неточная
		{
			304500,
			566426
		}, // 30, цифра неточная
	};

	/* TODO: Вынести в ХМЛ, на оффе около 10 видов комбанаций цен. */
	/** Цена заточки атакующих скиллов */
	private static final int[][] _priceCombat =
	{
		{}, //
		{
			93555,
			635014
		}, // 1
		{
			93555,
			635014
		}, // 2
		{
			93555,
			635014
		}, // 3
		{
			141183,
			666502
		}, // 4
		{
			141183,
			666502
		}, // 5
		{
			141183,
			666502
		}, // 6
		{
			189378,
			699010
		}, // 7
		{
			189378,
			699010
		}, // 8
		{
			189378,
			699010
		}, // 9
		{
			238140,
			749725
		}, // 10
		{
			238140,
			749725
		}, // 11
		{
			238140,
			749725
		}, // 12
		{
			287469,
			896981
		}, // 13
		{
			287469,
			896981
		}, // 14
		{
			287469,
			896981
		}, // 15
		{
			337365,
			959540
		}, // 16
		{
			337365,
			959540
		}, // 17
		{
			337365,
			959540
		}, // 18
		{
			387828,
			1002821
		}, // 19
		{
			387828,
			1002821
		}, // 20
		{
			387828,
			1002821
		}, // 21
		{
			438858,
			1070155
		}, // 22
		{
			438858,
			1070155
		}, // 23
		{
			438858,
			1070155
		}, // 24
		{
			496601,
			1142010
		}, // 25, цифра неточная
		{
			496601,
			1142010
		}, // 26, цифра неточная
		{
			496601,
			1142010
		}, // 27, цифра неточная
		{
			561939,
			1218690
		}, // 28, цифра неточная
		{
			561939,
			1218690
		}, // 29, цифра неточная
		{
			561939,
			1218690
		}, // 30, цифра неточная
	};

	/**
	 * Шанс успешной заточки
	 */
	public int getRate(Player ply)
	{
		int level = _level % 100;
		int chance = Math.min(_chance[level].length - 1, ply.getLevel() - 76);
		return _maxLvl == 15 ? _chance15[level][chance] : _chance[level][chance];
	}

	public int getMaxLevel()
	{
		return _maxLvl;
	}

	public String getType()
	{
		return _type;
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + _id;
		result = PRIME * result + _level;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass()) || !(obj instanceof EnchantSkillLearn))
		{
			return false;
		}
		EnchantSkillLearn other = (EnchantSkillLearn) obj;
		return getId() == other.getId() && getLevel() == other.getLevel();
	}
}