package l2f.gameserver.network.clientpackets;

import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.SkillAcquireHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.Zone.ZoneType;
import l2f.gameserver.model.base.EnchantSkillLearn;
import l2f.gameserver.network.serverpackets.ExEnchantSkillInfo;
import l2f.gameserver.network.serverpackets.ExEnchantSkillResult;
import l2f.gameserver.network.serverpackets.SkillList;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.skills.TimeStamp;
import l2f.gameserver.tables.SkillTable;
import l2f.gameserver.tables.SkillTreeTable;
import l2f.gameserver.utils.Log;

public final class RequestExEnchantSkillUntrain extends L2GameClientPacket
{
	private int _skillId;
	private int _skillLvl;

	@Override
	protected void readImpl()
	{
		_skillId = readD();
		_skillLvl = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (activeChar.getTransformation() != 0)
		{
			activeChar.sendMessage("You must leave transformation mode first.");
			return;
		}

		// claww fix stuck sub
		if (!Config.ALT_ENABLE_MULTI_PROFA)
		{
			if (activeChar.getLevel() < 76 || activeChar.getClassId().getLevel() < 4)
			{
				activeChar.sendMessage("You must have 3rd class change quest completed.");
				return;
			}
		}

		if (activeChar.getLevel() < 76)
		{
			activeChar.sendMessage("You must be at leat level 76 in order to enchant the skills.");
			return;
		}

		// Synerge - If the config is enabled then enforce using the enchant skill system in peace zone
		if (!Config.ALLOW_SKILL_ENCHANTING_OUTSIDE_PEACE_ZONE && !activeChar.isInZone(ZoneType.peace_zone))
		{
			activeChar.sendMessage("You must be in a peace zone in order to enchant your skills");
			return;
		}

		int oldSkillLevel = activeChar.getSkillDisplayLevel(_skillId);
		if ((oldSkillLevel == -1) || _skillLvl != (oldSkillLevel - 1) || (_skillLvl / 100) != (oldSkillLevel / 100))
		{
			return;
		}

		EnchantSkillLearn sl = SkillTreeTable.getSkillEnchant(_skillId, oldSkillLevel);
		if (sl == null)
		{
			return;
		}

		Skill newSkill;

		if (_skillLvl % 100 == 0)
		{
			_skillLvl = sl.getBaseLevel();
			newSkill = SkillTable.getInstance().getInfo(_skillId, _skillLvl);
		}
		else
		{
			newSkill = SkillTable.getInstance().getInfo(_skillId, SkillTreeTable.convertEnchantLevel(sl.getBaseLevel(), _skillLvl, sl.getMaxLevel()));
		}

		if (newSkill == null)
		{
			return;
		}

		// claww fix sub
		if (!SkillAcquireHolder.getInstance().isSkillPossible(activeChar, newSkill))
		{
			activeChar.sendMessage("Skill cannot be enchanted from this current class, please switch to class it belong.");
			return;
		}

		if (Functions.getItemCount(activeChar, SkillTreeTable.UNTRAIN_ENCHANT_BOOK) == 0)
		{
			activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL);
			return;
		}

		Functions.removeItem(activeChar, SkillTreeTable.UNTRAIN_ENCHANT_BOOK, 1, "SkillEnchantUntrain");

		activeChar.addExpAndSp(0, sl.getCost()[1] * sl.getCostMult());
		activeChar.addSkill(newSkill, true);

		if (_skillLvl > 100)
		{
			SystemMessage sm = new SystemMessage(SystemMessage.Untrain_of_enchant_skill_was_successful_Current_level_of_enchant_skill_S1_has_been_decreased_by_1);
			sm.addSkillName(_skillId, _skillLvl);
			activeChar.sendPacket(sm);
		}
		else
		{
			SystemMessage sm = new SystemMessage(SystemMessage.Untrain_of_enchant_skill_was_successful_Current_level_of_enchant_skill_S1_became_0_and_enchant_skill_will_be_initialized);
			sm.addSkillName(_skillId, _skillLvl);
			activeChar.sendPacket(sm);
		}

		Log.add(activeChar.getName() + "|Successfully untranes|" + _skillId + "|to+" + _skillLvl + "|---", "enchant_skills");

		activeChar.sendPacket(new ExEnchantSkillInfo(_skillId, newSkill.getDisplayLevel()), ExEnchantSkillResult.SUCCESS, new SkillList(activeChar));
		RequestExEnchantSkill.updateSkillShortcuts(activeChar, _skillId, _skillLvl);

		// Synerge - In retail server there is a bug when you enchant a skill, its reuse gets reset if you try to use it from a macro.
		if (!Config.ALLOW_MACROS_ENCHANT_BUG)
		{
			TimeStamp oldSkillReuse = activeChar.getSkillReuses().stream().filter(ts -> ts.getId() == _skillId).findFirst().orElse(null);
			if (oldSkillReuse != null)
			{
				activeChar.disableSkill(newSkill, oldSkillReuse.getReuseCurrent());
			}
		}
	}
}