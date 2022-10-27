package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.tables.SkillTreeTable;

/**
 * format   d (dddc)
 */
public class SkillList extends L2GameServerPacket
{
	private List<Skill> _skills;
	private boolean canEnchant;
	private Player activeChar;

	public SkillList(Player p)
	{
		this._skills = new ArrayList<Skill>(p.getAllSkills());
		this.canEnchant = p.getTransformation() == 0;
		this.activeChar = p;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x5f);
		this.writeD(this._skills.size());

		for (Skill temp : this._skills)
		{
			this.writeD(temp.isActive() || temp.isToggle() ? 0 : 1); // deprecated? клиентом игнорируется
			this.writeD(temp.getDisplayLevel());
			this.writeD(temp.getDisplayId());
			this.writeC(this.activeChar.isUnActiveSkill(temp.getId()) ? 0x01 : 0x00); // иконка скилла серая если не 0
			this.writeC(this.canEnchant ? SkillTreeTable.isEnchantable(temp) : 0); // для заточки: если 1 скилл можно точить
		}
	}
}