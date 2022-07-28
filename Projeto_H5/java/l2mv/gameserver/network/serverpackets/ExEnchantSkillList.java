package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

public class ExEnchantSkillList extends L2GameServerPacket
{
	public enum EnchantSkillType
	{
		NORMAL, SAFE, UNTRAIN, CHANGE_ROUTE,
	}

	private final List<Skill> _skills;
	private final EnchantSkillType _type;

	class Skill
	{
		public int id;
		public int level;

		Skill(int id, int nextLevel)
		{
			this.id = id;
			this.level = nextLevel;
		}
	}

	public void addSkill(int id, int level)
	{
		this._skills.add(new Skill(id, level));
	}

	public ExEnchantSkillList(EnchantSkillType type)
	{
		this._type = type;
		this._skills = new ArrayList<Skill>();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x29);

		this.writeD(this._type.ordinal());
		this.writeD(this._skills.size());
		for (Skill sk : this._skills)
		{
			this.writeD(sk.id);
			this.writeD(sk.level);
		}
	}
}