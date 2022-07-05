package l2f.gameserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Config;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Zone.ZoneType;
import l2f.gameserver.model.base.EnchantSkillLearn;
import l2f.gameserver.network.serverpackets.ExEnchantSkillInfoDetail;
import l2f.gameserver.tables.SkillTreeTable;

public final class RequestExEnchantSkillInfoDetail extends L2GameClientPacket
{
	private static final Logger LOG = LoggerFactory.getLogger(RequestExEnchantSkillInfoDetail.class);

	private static final int TYPE_NORMAL_ENCHANT = 0;
	private static final int TYPE_SAFE_ENCHANT = 1;
	private static final int TYPE_UNTRAIN_ENCHANT = 2;
	private static final int TYPE_CHANGE_ENCHANT = 3;

	private int _type;
	private int _skillId;
	private int _skillLvl;

	@Override
	protected void readImpl()
	{
		_type = readD();
		_skillId = readD();
		_skillLvl = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();

		if ((activeChar == null) || (_skillId == 0))
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

		int bookId = 0;
		int sp = 0;
		int adenaCount = 0;
		double spMult = SkillTreeTable.NORMAL_ENCHANT_COST_MULTIPLIER;

		EnchantSkillLearn esd = null;

		switch (_type)
		{
		case TYPE_NORMAL_ENCHANT:
			if (_skillLvl % 100 == 1)
			{
				bookId = SkillTreeTable.NORMAL_ENCHANT_BOOK;
			}
			esd = SkillTreeTable.getSkillEnchant(_skillId, _skillLvl);
			break;
		case TYPE_SAFE_ENCHANT:
			bookId = SkillTreeTable.SAFE_ENCHANT_BOOK;
			esd = SkillTreeTable.getSkillEnchant(_skillId, _skillLvl);
			spMult = SkillTreeTable.SAFE_ENCHANT_COST_MULTIPLIER;
			break;
		case TYPE_UNTRAIN_ENCHANT:
			bookId = SkillTreeTable.UNTRAIN_ENCHANT_BOOK;
			esd = SkillTreeTable.getSkillEnchant(_skillId, _skillLvl + 1);
			break;
		case TYPE_CHANGE_ENCHANT:
			bookId = SkillTreeTable.CHANGE_ENCHANT_BOOK;
			try
			{
				esd = SkillTreeTable.getEnchantsForChange(_skillId, _skillLvl).get(0);
				spMult = 1f / SkillTreeTable.SAFE_ENCHANT_COST_MULTIPLIER;
			}
			catch (RuntimeException e)
			{
				LOG.error("Error while loading Change Skill Enchant Details for skill id:" + _skillId + " level:" + _skillLvl, e);
			}
			break;
		}

		if (esd == null)
		{
			return;
		}

		spMult *= esd.getCostMult();
		int[] cost = esd.getCost();

		sp = (int) (cost[1] * spMult);

		if (_type != TYPE_UNTRAIN_ENCHANT)
		{
			adenaCount = (int) (cost[0] * spMult);
		}

		// send skill enchantment detail
		activeChar.sendPacket(new ExEnchantSkillInfoDetail(_skillId, _skillLvl, sp, esd.getRate(activeChar), bookId, adenaCount));
	}
}