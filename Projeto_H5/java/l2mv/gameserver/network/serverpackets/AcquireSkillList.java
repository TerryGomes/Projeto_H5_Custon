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
		this._skills = new ArrayList<Skill>(size);
		this._type = type;
	}

	public void addSkill(int id, int nextLevel, int maxLevel, int Cost, int requirements, int subUnit)
	{
		this._skills.add(new Skill(id, nextLevel, maxLevel, Cost, requirements, subUnit));
	}

	public void addSkill(int id, int nextLevel, int maxLevel, int Cost, int requirements)
	{
		this._skills.add(new Skill(id, nextLevel, maxLevel, Cost, requirements, 0));
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x90);
		this.writeD(this._type.ordinal());
		this.writeD(this._skills.size());

		for (Skill temp : this._skills)
		{
			this.writeD(temp.id);
			this.writeD(temp.nextLevel);
			this.writeD(temp.maxLevel);
			this.writeD(temp.cost);
			this.writeD(temp.requirements);
			if (this._type == AcquireType.SUB_UNIT)
			{
				this.writeD(temp.subUnit);
			}
		}
	}
}