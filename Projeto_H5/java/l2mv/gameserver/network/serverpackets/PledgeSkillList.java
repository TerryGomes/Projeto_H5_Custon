package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.model.pledge.SubUnit;

/**
 * Reworked: VISTALL
 */
public class PledgeSkillList extends L2GameServerPacket
{
	private List<SkillInfo> _allSkills = Collections.emptyList();
	private List<UnitSkillInfo> _unitSkills = new ArrayList<UnitSkillInfo>();

	public PledgeSkillList(Clan clan)
	{
		Collection<Skill> skills = clan.getSkills();
		this._allSkills = new ArrayList<SkillInfo>(skills.size());

		for (Skill sk : skills)
		{
			this._allSkills.add(new SkillInfo(sk.getId(), sk.getLevel()));
		}

		for (SubUnit subUnit : clan.getAllSubUnits())
		{
			for (Skill sk : subUnit.getSkills())
			{
				this._unitSkills.add(new UnitSkillInfo(subUnit.getType(), sk.getId(), sk.getLevel()));
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x3a);
		this.writeD(this._allSkills.size());
		this.writeD(this._unitSkills.size());

		for (SkillInfo info : this._allSkills)
		{
			this.writeD(info._id);
			this.writeD(info._level);
		}

		for (UnitSkillInfo info : this._unitSkills)
		{
			this.writeD(info._type);
			this.writeD(info._id);
			this.writeD(info._level);
		}
	}

	static class SkillInfo
	{
		public int _id, _level;

		public SkillInfo(int id, int level)
		{
			this._id = id;
			this._level = level;
		}
	}

	static class UnitSkillInfo extends SkillInfo
	{
		private int _type;

		public UnitSkillInfo(int type, int id, int level)
		{
			super(id, level);
			this._type = type;
		}
	}
}