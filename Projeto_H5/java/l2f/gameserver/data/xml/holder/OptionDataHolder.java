package l2f.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

import l2f.commons.data.xml.AbstractHolder;
import l2f.gameserver.model.Options.AugmentationFilter;
import l2f.gameserver.model.Skill;
import l2f.gameserver.templates.OptionDataTemplate;

/**
 * @author VISTALL
 * @date 20:35/19.05.2011
 */
public final class OptionDataHolder extends AbstractHolder
{
	private static final OptionDataHolder _instance = new OptionDataHolder();

	private final IntObjectMap<OptionDataTemplate> _templates = new HashIntObjectMap<OptionDataTemplate>();

	public static OptionDataHolder getInstance()
	{
		return _instance;
	}

	public void addTemplate(OptionDataTemplate template)
	{
		_templates.put(template.getId(), template);
	}

	public OptionDataTemplate getTemplate(int id)
	{
		return _templates.get(id);
	}

	@Override
	public int size()
	{
		return _templates.size();
	}

	@Override
	public void clear()
	{
		_templates.clear();
	}

	/**
	 * @param filter
	 * @return Synerge - Devuelve todos las options de augmentations usando un filtro en especial
	 */
	public Collection<OptionDataTemplate> getUniqueOptions(AugmentationFilter filter)
	{
		if (filter == AugmentationFilter.NONE)
		{
			return _templates.values();
		}

		final Map<Integer, OptionDataTemplate> options = new HashMap<>();
		switch (filter)
		{
		case ACTIVE_SKILL:
		{
			for (OptionDataTemplate option : _templates.values())
			{
				// Solo activas
				if (!option.getTriggerList().isEmpty() || option.getSkills().isEmpty() || !option.getSkills().get(0).isActive())
				{
					continue;
				}

				// Chequeamos que el lvl de esta skill si ya fue agregado, sea mayor al anterior
				if (!options.containsKey(option.getSkills().get(0).getId()) || options.get(option.getSkills().get(0).getId()).getSkills().get(0).getLevel() < option.getSkills().get(0).getLevel())
				{
					options.put(option.getSkills().get(0).getId(), option);
				}
			}
			break;
		}
		case PASSIVE_SKILL:
		{
			for (OptionDataTemplate option : _templates.values())
			{
				// Solo pasivas
				if (!option.getTriggerList().isEmpty() || option.getSkills().isEmpty() || !option.getSkills().get(0).isPassive())
				{
					continue;
				}

				// Chequeamos que el lvl de esta skill si ya fue agregado, sea mayor al anterior
				if (!options.containsKey(option.getSkills().get(0).getId()) || options.get(option.getSkills().get(0).getId()).getSkills().get(0).getLevel() < option.getSkills().get(0).getLevel())
				{
					options.put(option.getSkills().get(0).getId(), option);
				}
			}
			break;
		}
		case CHANCE_SKILL:
		{
			for (OptionDataTemplate option : _templates.values())
			{
				// Solo de chance
				if (option.getTriggerList().isEmpty())
				{
					continue;
				}

				// Chequeamos que el lvl de esta skill si ya fue agregado, sea mayor al anterior
				if (!options.containsKey(option.getTriggerList().get(0).getSkillId())
							|| options.get(option.getTriggerList().get(0).getSkillId()).getTriggerList().get(0).getSkillLevel() < option.getTriggerList().get(0).getSkillLevel())
				{
					options.put(option.getTriggerList().get(0).getSkillId(), option);
				}
			}
			break;
		}
		case STATS:
		{
			for (OptionDataTemplate option : _templates.values())
			{
				// La lista de opciones de stats es hardcoded porque no tenemos forma de saber sino, son solo 5
				switch (option.getId())
				{
				case 16341: // +1 STR
				case 16342: // +1 CON
				case 16343: // +1 INT
				case 16344: // +1 MEN
					options.put(option.getId(), option);
					break;
				}
			}
			break;
		}
		}

		// Filtramos todos los augmentations del mismo nombre para solo dejar el de magicLvl mas alto, ya que hay muchos repetidos con el mismo nombre pero diferente id
		final Map<String, OptionDataTemplate> filteredAugs = new HashMap<>();
		for (OptionDataTemplate option : options.values())
		{
			if (filter == AugmentationFilter.STATS)
			{
				filteredAugs.put(String.valueOf(option.getId()), option);
				continue;
			}

			final Skill skill = (filter == AugmentationFilter.CHANCE_SKILL ? option.getTriggerList().get(0).getSkill() : option.getSkills().get(0));
			boolean mustAddSkill = true;
			for (OptionDataTemplate option2 : options.values())
			{
				if (option == option2)
				{
					continue;
				}

				switch (filter)
				{
				case CHANCE_SKILL:
				{
					if (skill.getName().equalsIgnoreCase(option2.getTriggerList().get(0).getSkill().getName()) && skill.getMagicLevel() < option2.getTriggerList().get(0).getSkill().getMagicLevel())
					{
						mustAddSkill = false;
					}
					break;
				}
				case ACTIVE_SKILL:
				{
					if (skill.getName().equalsIgnoreCase(option2.getSkills().get(0).getName()) && ((skill.getPower() > 0 && skill.getPower() < option2.getSkills().get(0).getPower())
								|| (skill.getPower() <= 0 && skill.getMagicLevel() < option2.getSkills().get(0).getMagicLevel())))
					{
						mustAddSkill = false;
					}
					break;
				}
				case PASSIVE_SKILL:
				{
					if (skill.getName().equalsIgnoreCase(option2.getSkills().get(0).getName()) && skill.getMagicLevel() < option2.getSkills().get(0).getMagicLevel())
					{
						mustAddSkill = false;
					}
					break;
				}
				}

				if (!mustAddSkill)
				{
					break;
				}
			}

			if (mustAddSkill)
			{
				filteredAugs.put(skill.getName(), option);
			}
		}

		// Ordenamos la lista de augmentations segun el nombre de su skill
		final List<OptionDataTemplate> augs = new ArrayList<>(filteredAugs.values());
		if (filter != AugmentationFilter.STATS)
		{
			Collections.sort(augs, new AugmentationComparator());
		}

		return augs;
	}

	// Comparator para ordenar la lista de augmentations segun el id de su skill
	private static class AugmentationComparator implements Comparator<OptionDataTemplate>
	{
		@Override
		public int compare(OptionDataTemplate left, OptionDataTemplate right)
		{
			final String leftName = (!left.getTriggerList().isEmpty() ? left.getTriggerList().get(0).getSkill().getName() : left.getSkills().get(0).getName());
			final String rightName = (!right.getTriggerList().isEmpty() ? right.getTriggerList().get(0).getSkill().getName() : right.getSkills().get(0).getName());

			return leftName.compareTo(rightName);
		}
	}
}
