package l2mv.gameserver.network.serverpackets;

import java.util.Collection;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.tables.SkillTable;

public class GMViewSkillInfo extends L2GameServerPacket
{
	private String _charName;
	private Collection<Skill> _skills;
	private Player _targetChar;

	public GMViewSkillInfo(Player cha)
	{
		this._charName = cha.getName();
		this._skills = cha.getAllSkills();
		this._targetChar = cha;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x97);
		this.writeS(this._charName);
		this.writeD(this._skills.size());
		for (Skill skill : this._skills)
		{
			this.writeD(skill.isPassive() ? 1 : 0);
			this.writeD(skill.getDisplayLevel());
			this.writeD(skill.getId());
			this.writeC(this._targetChar.isUnActiveSkill(skill.getId()) ? 0x01 : 0x00);
			this.writeC(SkillTable.getInstance().getMaxLevel(skill.getId()) > 100 ? 1 : 0);
		}
	}
}