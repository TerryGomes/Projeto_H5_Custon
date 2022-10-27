package l2mv.gameserver.instancemanager.achievements_engine.conditions;

import l2mv.gameserver.instancemanager.achievements_engine.base.Condition;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;

public class SkillEnchant extends Condition
{
	public SkillEnchant(Object value)
	{
		super(value);
		setName("Skill Enchant");
	}

	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
		{
			return false;
		}

		int val = Integer.parseInt(getValue().toString());

		for (Skill s : player.getAllSkills())
		{
			String lvl = String.valueOf(s.getLevel());
			if (lvl.length() > 2)
			{
				int sklvl = Integer.parseInt(lvl.substring(1));
				if (sklvl >= val)
				{
					return true;
				}
			}
		}

		return false;
	}
}