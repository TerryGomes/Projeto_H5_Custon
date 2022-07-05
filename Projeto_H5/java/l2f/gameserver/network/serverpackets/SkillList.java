package l2f.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.tables.SkillTreeTable;

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
		_skills = new ArrayList<Skill>(p.getAllSkills());
		canEnchant = p.getTransformation() == 0;
		activeChar = p;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x5f);
		writeD(_skills.size());

		for (Skill temp : _skills)
		{
			writeD(temp.isActive() || temp.isToggle() ? 0 : 1); // deprecated? клиентом игнорируется
			writeD(temp.getDisplayLevel());
			writeD(temp.getDisplayId());
			writeC(activeChar.isUnActiveSkill(temp.getId()) ? 0x01 : 0x00); // иконка скилла серая если не 0
			writeC(canEnchant ? SkillTreeTable.isEnchantable(temp) : 0); // для заточки: если 1 скилл можно точить
		}
	}
}