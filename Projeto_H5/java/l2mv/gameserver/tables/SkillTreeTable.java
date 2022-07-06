package l2mv.gameserver.tables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.data.xml.holder.SkillAcquireHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.SkillLearn;
import l2mv.gameserver.model.base.AcquireType;
import l2mv.gameserver.model.base.EnchantSkillLearn;

public class SkillTreeTable
{
	public static final int NORMAL_ENCHANT_COST_MULTIPLIER = 1;
	public static final int SAFE_ENCHANT_COST_MULTIPLIER = 5;
	public static final int NORMAL_ENCHANT_BOOK = 6622;
	public static final int SAFE_ENCHANT_BOOK = 9627;
	public static final int CHANGE_ENCHANT_BOOK = 9626;
	public static final int UNTRAIN_ENCHANT_BOOK = 9625;

	private static final Logger _log = LoggerFactory.getLogger(SkillTreeTable.class);

	private static SkillTreeTable _instance;

	public static Map<Integer, List<EnchantSkillLearn>> _enchant = new ConcurrentHashMap<Integer, List<EnchantSkillLearn>>();

	public static SkillTreeTable getInstance()
	{
		if (_instance == null)
		{
			_instance = new SkillTreeTable();
		}
		return _instance;
	}

	private SkillTreeTable()
	{
		_log.info("SkillTreeTable: Loaded " + _enchant.size() + " enchanted skills.");
	}

	public static void checkSkill(Player player, Skill skill)
	{
		SkillLearn learn = SkillAcquireHolder.getInstance().getSkillLearn(player, skill.getId(), levelWithoutEnchant(skill), AcquireType.NORMAL);
		if (learn == null)
		{
			return;
		}

		if (learn.getMinLevel() > player.getLevel() + 5) // Official +9 (Synerge)
		{
			player.removeSkill(skill, true);

			// если у нас низкий лвл для скила, то заточка обнуляется 100%
			// и ищем от большего к меньшему подходящий лвл для скила
			for (int i = skill.getBaseLevel(); i != 0; i--)
			{
				SkillLearn learn2 = SkillAcquireHolder.getInstance().getSkillLearn(player, skill.getId(), i, AcquireType.NORMAL);
				if ((learn2 == null) || (learn2.getMinLevel() > player.getLevel() + 6)) // Official +10 (Synerge)
				{
					continue;
				}

				Skill newSkill = SkillTable.getInstance().getInfo(skill.getId(), i);
				if (newSkill != null)
				{
					player.addSkill(newSkill, true);
					break;
				}
			}
		}
	}

	private static int levelWithoutEnchant(Skill skill)
	{
		return skill.getDisplayLevel() > 100 ? skill.getBaseLevel() : skill.getLevel();
	}

	public static List<EnchantSkillLearn> getFirstEnchantsForSkill(int skillid)
	{
		List<EnchantSkillLearn> result = new ArrayList<EnchantSkillLearn>();

		List<EnchantSkillLearn> enchants = _enchant.get(skillid);
		if (enchants == null)
		{
			return result;
		}

		for (EnchantSkillLearn e : enchants)
		{
			if (e.getLevel() % 100 == 1)
			{
				result.add(e);
			}
		}

		return result;
	}

	public static int isEnchantable(Skill skill)
	{
		List<EnchantSkillLearn> enchants = _enchant.get(skill.getId());
		if (enchants == null)
		{
			return 0;
		}

		for (EnchantSkillLearn e : enchants)
		{
			if (e.getBaseLevel() <= skill.getLevel())
			{
				return 1;
			}
		}

		return 0;
	}

	public static List<EnchantSkillLearn> getEnchantsForChange(int skillid, int level)
	{
		List<EnchantSkillLearn> result = new ArrayList<EnchantSkillLearn>();

		List<EnchantSkillLearn> enchants = _enchant.get(skillid);
		if (enchants == null)
		{
			return result;
		}

		for (EnchantSkillLearn e : enchants)
		{
			if (e.getLevel() % 100 == level % 100)
			{
				result.add(e);
			}
		}

		return result;
	}

	public static EnchantSkillLearn getSkillEnchant(int skillid, int level)
	{
		List<EnchantSkillLearn> enchants = _enchant.get(skillid);
		if (enchants == null)
		{
			return null;
		}

		for (EnchantSkillLearn e : enchants)
		{
			if (e.getLevel() == level)
			{
				return e;
			}
		}
		return null;
	}

	/**
	 * Преобразует уровень скила из клиентского представления в серверное
	 * @param baseLevel базовый уровень скила - максимально возможный без заточки
	 * @param level - текущий уровень скила
	 * @param enchantlevels
	 * @return уровень скила
	 */
	public static int convertEnchantLevel(int baseLevel, int level, int enchantlevels)
	{
		if (level < 100)
		{
			return level;
		}
		return baseLevel + ((level - level % 100) / 100 - 1) * enchantlevels + level % 100;
	}

	public static void unload()
	{
		if (_instance != null)
		{
			_instance = null;
		}

		_enchant.clear();
	}
}