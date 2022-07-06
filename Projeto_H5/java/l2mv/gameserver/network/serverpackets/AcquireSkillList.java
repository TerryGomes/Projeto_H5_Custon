package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.base.AcquireType;

/**
 * Reworked: VISTALL
 */
public class AcquireSkillList extends L2GameServerPacket
{
	private AcquireType _type;
	private final List<Skill> _skills;

	class Skill
	{
		public int id;
		public int nextLevel;
		public int maxLevel;
		public int cost;
		public int requirements;
		public int subUnit;

		Skill(int id, int nextLevel, int maxLevel, int cost, int requirements, int subUnit)
		{
			this.id = id;
			this.nextLevel = nextLevel;
			this.maxLevel = maxLevel;
			this.cost = cost;
			this.requirements = requirements;
			this.subUnit = subUnit;
		}
	}

	public AcquireSkillList(AcquireType type, int size)
	{
		_skills = new ArrayList<Skill>(size);
		_type = type;
	}

	public void addSkill(int id, int nextLevel, int maxLevel, int Cost, int requirements, int subUnit)
	{
		_skills.add(new Skill(id, nextLevel, maxLevel, Cost, requirements, subUnit));
	}

	public void addSkill(int id, int nextLevel, int maxLevel, int Cost, int requirements)
	{
		_skills.add(new Skill(id, nextLevel, maxLevel, Cost, requirements, 0));
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x90);
		writeD(_type.ordinal());
		writeD(_skills.size());

		for (Skill temp : _skills)
		{
			writeD(temp.id);
			writeD(temp.nextLevel);
			writeD(temp.maxLevel);
			writeD(temp.cost);
			writeD(temp.requirements);
			if (_type == AcquireType.SUB_UNIT)
			{
				writeD(temp.subUnit);
			}
		}
	}
}