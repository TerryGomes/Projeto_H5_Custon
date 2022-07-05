package l2f.gameserver.templates.item.support;

import l2f.commons.collections.MultiValueSet;

public class FishTemplate
{
	private final FishGroup _group;
	private final FishGrade _grade;
	private final double _biteRate;
	private final double _guts;
	private final double _lengthRate;
	private final double _hpRegen;
	private final double _gutsCheckProbability;
	private final double _cheatingProb;
	private final int _itemId;
	private final int _hp;
	private final int _level;
	private final int _maxLength;
	private final int _startCombatTime;
	private final int _combatDuration;
	private final int _gutsCheckTime;

	public FishTemplate(MultiValueSet<String> map)
	{
		_group = map.getEnum("group", FishGroup.class);
		_grade = map.getEnum("grade", FishGrade.class);

		_biteRate = map.getDouble("bite_rate");
		_guts = map.getDouble("guts");
		_lengthRate = map.getDouble("length_rate");
		_hpRegen = map.getDouble("hp_regen");
		_gutsCheckProbability = map.getDouble("guts_check_probability");
		_cheatingProb = map.getDouble("cheating_prob");

		_itemId = map.getInteger("item_id");
		_level = map.getInteger("level");
		_hp = map.getInteger("hp");
		_maxLength = map.getInteger("max_length");
		_startCombatTime = map.getInteger("start_combat_time");
		_combatDuration = map.getInteger("combat_duration");
		_gutsCheckTime = map.getInteger("guts_check_time");
	}

	public FishGroup getGroup()
	{
		return _group;
	}

	public FishGrade getGrade()
	{
		return _grade;
	}

	public double getBiteRate()
	{
		return _biteRate;
	}

	public double getGuts()
	{
		return _guts;
	}

	public double getLengthRate()
	{
		return _lengthRate;
	}

	public double getHpRegen()
	{
		return _hpRegen;
	}

	public double getGutsCheckProbability()
	{
		return _gutsCheckProbability;
	}

	public double getCheatingProb()
	{
		return _cheatingProb;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public int getHp()
	{
		return _hp;
	}

	public int getLevel()
	{
		return _level;
	}

	public int getMaxLength()
	{
		return _maxLength;
	}

	public int getStartCombatTime()
	{
		return _startCombatTime;
	}

	public int getCombatDuration()
	{
		return _combatDuration;
	}

	public int getGutsCheckTime()
	{
		return _gutsCheckTime;
	}
}
