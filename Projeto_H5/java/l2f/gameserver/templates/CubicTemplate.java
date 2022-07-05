package l2f.gameserver.templates;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gnu.trove.map.hash.TIntIntHashMap;
import l2f.gameserver.model.Skill;

public class CubicTemplate
{
	public static class SkillInfo
	{
		private final Skill _skill;
		private final int _chance;
		private final ActionType _actionType;
		private final boolean _canAttackDoor;
		private final TIntIntHashMap _chanceList;

		public SkillInfo(Skill skill, int chance, ActionType actionType, boolean canAttackDoor, TIntIntHashMap set)
		{
			_skill = skill;
			_chance = chance;
			_actionType = actionType;
			_canAttackDoor = canAttackDoor;
			_chanceList = set;
		}

		public int getChance()
		{
			return _chance;
		}

		public ActionType getActionType()
		{
			return _actionType;
		}

		public Skill getSkill()
		{
			return _skill;
		}

		public boolean isCanAttackDoor()
		{
			return _canAttackDoor;
		}

		public int getChance(int a)
		{
			return _chanceList.get(a);
		}
	}

	public static enum ActionType
	{
		ATTACK, DEBUFF, CANCEL, HEAL
	}

	private final int _id;
	private final int _level;
	private final int _delay;

	private List<Map.Entry<Integer, List<SkillInfo>>> _skills = new ArrayList<Map.Entry<Integer, List<SkillInfo>>>(3);

	public CubicTemplate(int id, int level, int delay)
	{
		_id = id;
		_level = level;
		_delay = delay;
	}

	public void putSkills(int chance, List<SkillInfo> skill)
	{
		_skills.add(new AbstractMap.SimpleImmutableEntry<Integer, List<SkillInfo>>(chance, skill));
	}

	public Iterable<Map.Entry<Integer, List<SkillInfo>>> getSkills()
	{
		return _skills;
	}

	public int getDelay()
	{
		return _delay;
	}

	public int getId()
	{
		return _id;
	}

	public int getLevel()
	{
		return _level;
	}
}
