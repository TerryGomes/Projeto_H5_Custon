package l2mv.gameserver.network.clientpackets;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.SkillAcquireHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.Zone.ZoneType;
import l2mv.gameserver.model.base.EnchantSkillLearn;
import l2mv.gameserver.network.serverpackets.ExEnchantSkillInfo;
import l2mv.gameserver.network.serverpackets.ExEnchantSkillResult;
import l2mv.gameserver.network.serverpackets.SkillList;
import l2mv.gameserver.network.serverpackets.SystemMessage;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.skills.TimeStamp;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.tables.SkillTreeTable;
import l2mv.gameserver.utils.Log;

/**
 * Format (ch) dd
 */
public final class RequestExEnchantSkillSafe extends L2GameClientPacket
{
	private int _skillId;
	private int _skillLvl;

	@Override
	protected void readImpl()
	{
		this._skillId = this.readD();
		this._skillLvl = this.readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
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

		EnchantSkillLearn sl = SkillTreeTable.getSkillEnchant(this._skillId, this._skillLvl);

		if (sl == null)
		{
			return;
		}

		int slevel = activeChar.getSkillLevel(this._skillId);
		if (slevel == -1)
		{
			return;
		}

		int enchantLevel = SkillTreeTable.convertEnchantLevel(sl.getBaseLevel(), this._skillLvl, sl.getMaxLevel());

		// already knows the skill with this level
		if (slevel >= enchantLevel)
		{
			return;
		}

		// Можем ли мы перейти с текущего уровня скилла на данную заточку
		if (slevel == sl.getBaseLevel() ? this._skillLvl % 100 != 1 : slevel != enchantLevel - 1)
		{
			activeChar.sendMessage("Incorrect enchant level.");
			return;
		}

		Skill skill = SkillTable.getInstance().getInfo(this._skillId, enchantLevel);
		if (skill == null)
		{
			return;
		}

		// claww fix sub
		if (!SkillAcquireHolder.getInstance().isSkillPossible(activeChar, skill))
		{
			activeChar.sendMessage("Skill cannot be enchanted from this current class, please switch to class it belong.");
			return;
		}

		int[] cost = sl.getCost();
		int requiredSp = cost[1] * SkillTreeTable.SAFE_ENCHANT_COST_MULTIPLIER * sl.getCostMult();
		int requiredAdena = cost[0] * SkillTreeTable.SAFE_ENCHANT_COST_MULTIPLIER * sl.getCostMult();

		int rate = sl.getRate(activeChar);

		if (activeChar.getSp() < requiredSp)
		{
			activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL);
			return;
		}

		if (activeChar.getAdena() < requiredAdena)
		{
			activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		if (Functions.getItemCount(activeChar, SkillTreeTable.SAFE_ENCHANT_BOOK) == 0)
		{
			activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL);
			return;
		}

		Functions.removeItem(activeChar, SkillTreeTable.SAFE_ENCHANT_BOOK, 1, "SkillEnchantSafe");

		if (Rnd.chance(rate))
		{
			activeChar.addSkill(skill, true);
			activeChar.addExpAndSp(0, -1 * requiredSp);
			Functions.removeItem(activeChar, 57, requiredAdena, "SkillEnchantSafe");
			activeChar.sendPacket(new SystemMessage2(SystemMsg.YOUR_SP_HAS_DECREASED_BY_S1).addInteger(requiredSp), new SystemMessage2(SystemMsg.SKILL_ENCHANT_WAS_SUCCESSFUL_S1_HAS_BEEN_ENCHANTED).addSkillName(this._skillId, this._skillLvl), new ExEnchantSkillResult(1));
			activeChar.sendPacket(new SkillList(activeChar));
			RequestExEnchantSkill.updateSkillShortcuts(activeChar, this._skillId, this._skillLvl);
			Log.add(activeChar.getName() + "|Successfully safe enchanted|" + this._skillId + "|to+" + this._skillLvl + "|" + rate, "enchant_skills");
		}
		else
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.Skill_enchant_failed_Current_level_of_enchant_skill_S1_will_remain_unchanged).addSkillName(this._skillId, this._skillLvl), new ExEnchantSkillResult(0));
			Log.add(activeChar.getName() + "|Failed to safe enchant|" + this._skillId + "|to+" + this._skillLvl + "|" + rate, "enchant_skills");
		}

		activeChar.sendPacket(new ExEnchantSkillInfo(this._skillId, activeChar.getSkillDisplayLevel(this._skillId)));

		// Synerge - In retail server there is a bug when you enchant a skill, its reuse gets reset if you try to use it from a macro.
		if (!Config.ALLOW_MACROS_ENCHANT_BUG)
		{
			TimeStamp oldSkillReuse = activeChar.getSkillReuses().stream().filter(ts -> ts.getId() == this._skillId).findFirst().orElse(null);
			if (oldSkillReuse != null)
			{
				activeChar.disableSkill(skill, oldSkillReuse.getReuseCurrent());
			}
		}
	}
}